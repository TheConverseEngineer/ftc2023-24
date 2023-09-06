package org.firstinspires.ftc.teamcode.general.blacksmithcore
import android.util.Log
import org.firstinspires.ftc.teamcode.general.thundercore.utils.Utils.safeAssert
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.abs
import kotlin.math.min


/* This code should all be done, but I have not tested it yet */

data class MecanumConfiguration (
    val maxWheelSpeed: Double,  // Max speed of a wheel in in/sec
    val drivetrainMu: Double,
    val aMax: Double,           // Max acceleration
    val aMin: Double,           // Min acceleration
    val Kt: Double,
    val Kc: Double,
    val Kl: Double
) {
    fun verify() {
        safeAssert(maxWheelSpeed > 0, "maxWheelSpeed must be positive!")
        safeAssert(drivetrainMu > 0, "Drivetrain Mu must be positive!")
        safeAssert(aMax > 0, "The maximum acceleration must be positive!")
        safeAssert(aMin < 0, "maxWheelSpeed must be negative!")
        safeAssert(Kt >= 0, "Kt must be non-negative!")
        safeAssert(Kc >= 0, "Kc must be non-negative!")
        safeAssert(Kl >= 0, "Kl must be non-negative!")
    }
}

class Trajectory(
    val path: Path,
    private val mecConfig: MecanumConfiguration,
    private val resolution: Int,
    precision: Double,
    vi: Double = 0.0,
    vf: Double = 0.0
) {
    private val profilePoints = Array<ProfilePoint>(resolution + 1) { i -> JustTimeProfilePoint(i.toDouble()) }
    private val pathLength: Double = path.getLength()
    private val sampleDist = pathLength/resolution

    // First figure out the maximum speed that the robot can drive at.
    init {
        mecConfig.verify()

        profilePoints[resolution] = ProfilePointImpl(path, 1.0, 0.0, mecConfig, precision)
        for (i in (resolution-1) downTo 0) {
            val u = path.getParameterAtDisplacement(i*sampleDist)
            profilePoints[i] = ProfilePointImpl(
                path,
                u,
                (profilePoints[i+1].getRobotHeading()-path.getHeadingAtParameter(u))/sampleDist,
                mecConfig,
                precision
            )
            profilePoints[i].setNewMaxVel(path.getMaxVelAtParameter(u)) // If the robot can go faster than we want it to
        }
    }

    // And now apply acceleration/deceleration limits
    init {
        // Pass #1 -> forward pass
        profilePoints[0].setNewMaxVel(vi)
        var currentVel: Double = profilePoints[0].getCurrentMaxVel()

        for (i in 1..resolution) {
            profilePoints[i].setNewMaxVel(kotlin.math.sqrt(currentVel*currentVel + 2*mecConfig.aMax*sampleDist))
            currentVel = profilePoints[i].getCurrentMaxVel()
        }

        // Pass #2 -> reverse pass
        profilePoints[resolution].setNewMaxVel(vf)
        currentVel = profilePoints[resolution].getCurrentMaxVel()
        for (i in (resolution-1) downTo 0) {
            profilePoints[i].setNewMaxVel(kotlin.math.sqrt(currentVel*currentVel + -2*mecConfig.aMin*sampleDist))
            currentVel = profilePoints[i].getCurrentMaxVel()
        }
    }

    // Lastly, determine the length (in seconds) of the trajectory
    init {
        for (i in 1..resolution) {
            profilePoints[i].setTime(profilePoints[i-1].getTime() +
                    (2*sampleDist/(profilePoints[i].getCurrentMaxVel() + profilePoints[i-1].getCurrentMaxVel())))
        }
    }

    fun getDuration() = profilePoints[resolution].getTime()

    fun getGlobalRobotTargetAtTime(t: Double) : TrajectoryState {
        // First binary search to find the correct profile point
        val (disp, v, omega) = getDisplacementAndVelocityAtTime(t) // binary search #1
        val u = path.getParameterAtDisplacement(disp) // binary search #2

        val point = path.atParameter(u)

        val tangentMag = sqrt(square(differentialShift(point.x)) + square(differentialShift(point.y)))
        val normalMag = sqrt(square(differentialShift(differentialShift(point.x)/tangentMag)) +
                square(differentialShift(differentialShift(point.y)/tangentMag)))
        val curvature: Double = normalMag.x/tangentMag.x

        val tangentUnitVector = Vector2d(point.x.dx, point.y.dx)/tangentMag.x
        val normalUnitVector  = Vector2d((differentialShift(point.x)/tangentMag).dx, (differentialShift(point.y)/tangentMag).dx)/normalMag.x

        return TrajectoryState(
            point.getVector(),
            tangentUnitVector*(mecConfig.Kt*v) + normalUnitVector*(mecConfig.Kc*v*v*curvature),
            path.getHeadingAtParameter(u),
            omega*v
        )

    }

    private fun getDisplacementAndVelocityAtTime(t: Double) : Triple<Double, Double, Double> { // displacement, velocity, omega
        var index = profilePoints.binarySearch(JustTimeProfilePoint(t))
        if (index < 0) index = -index - 1

        return if (index == 0) Triple(0.0, profilePoints[0].getCurrentMaxVel(), 0.0)
        else if (index >= resolution) Triple(pathLength, profilePoints[resolution].getCurrentMaxVel(), 0.0)
        else Triple((index - 1)*sampleDist +
            0.5*(profilePoints[index].getCurrentMaxVel() + profilePoints[index-1].getCurrentMaxVel()) * (t-profilePoints[index-1].getTime()),
            profilePoints[index-1].getCurrentMaxVel() +
                    (profilePoints[index].getCurrentMaxVel() - profilePoints[index-1].getCurrentMaxVel())*(t - profilePoints[index-1].getTime())/
                    (profilePoints[index].getTime() - profilePoints[index-1].getTime()),
            profilePoints[index-1].getOmega()
        )
    }

}

abstract class ProfilePoint : Comparable<ProfilePoint> {
    abstract fun getCurrentMaxVel() : Double

    abstract fun setNewMaxVel(maxVel: Double)

    abstract fun getRobotHeading() : Double

    abstract fun setTime(t: Double)

    abstract fun getTime() : Double

    abstract fun getOmega(): Double

    override operator fun compareTo(other: ProfilePoint) : Int {
        return getTime().compareTo(other.getTime())
    }
}

/**
 * @param precision         the precision of this profile (in in/sec)
 *
 * IMPLEMENTATION complete! (see EE paper)
 */
class ProfilePointImpl (
    path: Path,
    u: Double,
    private val omega: Double,
    private val mecConfig: MecanumConfiguration,
    precision: Double = 0.1
) : ProfilePoint() {

    private val tangentMag: Variable<SplineParameter> // Magnitude of the path's tangent vector
    private val normalMag: Variable<SplineParameter> // Magnitude of the path's normal vector
    private val curvature: Double                   // Inverse of the radius of the circle tangent to the path at this point
    private var velocity: Double

    private val robotHeading = path.getHeadingAtParameter(u)

    private var time: Double = 0.0

    init {


        val point = path.atParameter(u)
        tangentMag = sqrt(square(differentialShift(point.x)) + square(differentialShift(point.y)))
        normalMag = sqrt(square(differentialShift(differentialShift(point.x)/tangentMag)) +
                                square(differentialShift(differentialShift(point.y)/tangentMag)))
        curvature = normalMag.x/tangentMag.x

        val tangentUnitVector = Vector2d(point.x.dx, point.y.dx)/tangentMag.x
        val normalUnitVector  = Vector2d((differentialShift(point.x)/tangentMag).dx, (differentialShift(point.y)/tangentMag).dx)/normalMag.x
        val directionVector   = Vector2d(cos(robotHeading), sin(robotHeading))

        velocity = unboundedBinarySearch(
            {v: Double ->
                val globalPower = tangentUnitVector*(mecConfig.Kt*v) + normalUnitVector*(mecConfig.Kc*v*v*curvature)
                val localPower  = Vector2d(globalPower dot directionVector, globalPower cross directionVector)
                (abs(localPower.y) + abs(localPower.x)*mecConfig.Kl + abs(mecConfig.drivetrainMu*omega*v)) > mecConfig.maxWheelSpeed
            }, precision)
        val globalPower = tangentUnitVector*(mecConfig.Kt*velocity) + normalUnitVector*(mecConfig.Kc*velocity*velocity*curvature)
        val localPower  = Vector2d(globalPower dot directionVector, globalPower cross directionVector)
        val mp = (abs(localPower.y) + abs(localPower.x)*mecConfig.Kl + abs(mecConfig.drivetrainMu*omega*velocity))
        Log.d("BSC", "Velocity: $velocity, $mp, ${localPower.magnitude()}")
    }

    override fun getCurrentMaxVel() = velocity

    override fun setNewMaxVel(maxVel: Double) { velocity = min(velocity, maxVel) }

    override fun getRobotHeading() = robotHeading

    override fun setTime(t: Double) { time = t}

    override fun getTime() = time

    override fun getOmega() = omega
}

class JustTimeProfilePoint(private var time: Double) : ProfilePoint() {
    override fun getCurrentMaxVel() = 0.0

    override fun setNewMaxVel(maxVel: Double) { }

    override fun getRobotHeading() = 0.0

    override fun setTime(t: Double) { time = t}

    override fun getTime() = time

    override fun getOmega() = 0.0
}