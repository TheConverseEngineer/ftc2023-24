package org.firstinspires.ftc.teamcode.blacksmithcore

import kotlin.math.*


class BetterTrajectory (
        private val path: Path,
        res: Int = 100
) {
    val samples = DoubleArray(res) {0.0}
    init {
        for (i in 0 until res) {
            samples[i] = 0.0
        }
    }

}


/** Large kotlin class representing a motion-profiled robot trajectory
 *
 * @param splines   an array of spline paths to follow
 * @param vmx       the maximum robot velocity
 * @param a         the maximum robot acceleration
 * @param vi        the starting robot velocity
 * @param vf        the ending robot velocity
 */
class Trajectory (
        private val splines: Array<QuinticSpline>,
        private val vmx: Double,
        private val a: Double,
        private var vi: Double = 0.0,
        private var vf: Double = 0.0,
    ) {

    private val endpoints = DoubleArray(splines.size+1) {0.0}
    init { // Determine endpoint lengths for all splines
        for (i in 1..(splines.size)) endpoints[i] = endpoints[i-1] + splines[i-1].splineDistance
    }

    private val timestamps: Array<Triple<Double, Double, Double>> // (time, speed, dist)
    init { // Create the motion profile
        vi = vi.coerceIn(0.0, vmx)
        vf = vf.coerceIn(0.0, vmx)
        val distToAccel = (vmx*vmx - vi*vi)/(2*a)
        val distToDeccel = (vmx*vmx - vf*vf)/(2*a)
        if (distToAccel + distToDeccel < getDistance()) { // Normal trajectory
            timestamps = arrayOf(
                    Triple(0.0, vi, 0.0),
                    Triple((vmx-vi)/a, vmx, distToAccel),
                    Triple((vmx-vi)/a + (getDistance() - distToAccel - distToDeccel)/vmx, vmx, getDistance()-distToDeccel),
                    Triple((vmx-vi)/a + (getDistance() - distToAccel - distToDeccel)/vmx + (vmx-vf)/a, vf, getDistance())
            )
        } else if (((max(vf, vi).let{it*it})- (min(vf, vf).let{it+it}))/(2*a) < getDistance()) { // partially degenerate
            sqrt( (vf*vf + vi*vi + 2*a+getDistance())/2 ).let{timestamps = arrayOf(
                    Triple(0.0, vi, 0.0),
                    Triple((it-vi)/a, it, vi*(it-vi)/a + 0.5*(it-vi)*(it-vi)/a),
                    Triple((it-vi)/a + (it-vf)/a, vf, getDistance())
            )}
        } else { // fully degenerate
            timestamps = arrayOf(
                    Triple(0.0, vi, 0.0),
                    Triple(getDistance()/(vi + 0.5*abs(vf-vi)), vf, getDistance())
            )
        }
    }

    /** Returns the length of this trajectory in inches */
    fun getDistance() = endpoints.last()

    /** Returns the length of this trajectory in seconds */
    fun getLength() = timestamps.last().first

    /** Returns the robot state at a given time
     *
     *  @param t    the time
     *  @return     the target robot state at time t
     */
    operator fun invoke(t: Double) : Vector2dDual{
        for (i in 1 until timestamps.size) {
            if (timestamps[i].first >= t) {
                val dv: Double = (timestamps[i].second - timestamps[i-1].second) / (timestamps[i].first - timestamps[i-1].first)
                return getAtDisplacement(DualNum(
                        timestamps[i-1].third + (timestamps[i-1].second+0.5*dv*(t-timestamps[i-1].first))*(t-timestamps[i-1].first),
                        timestamps[i-1].second + dv*(t-timestamps[i-1].first),
                        dv,
                ))
            }
        }
        return getAtDisplacement(DualNum.constant(getDistance()))
    }

    /** Returns the robot state at a given displacement
     *
     *  @param disp     the displacement
     *  @return         the tar get robot state at displacement disp
     */
    fun getAtDisplacement(disp: DualNum) : Vector2dDual {
        for (i in 1 until endpoints.size) {
            if (endpoints[i] >= disp.x) return splines[i-1](disp - endpoints[i-1])
        }
        return splines.last()(disp-endpoints[endpoints.size-2])
    }

    /** Builder for the Trajectory class
     *
     * @param currentPose       The pose of the robot at the start of the trajectory
     * @param currentHeading    The direction the robot should move in (in degrees)
     */
    class TrajectoryBuilder internal constructor (private var currentPose: Pose2d, private var currentHeading: Double) {
        private val knots = mutableListOf<Pair<Pose2d, Double>>()
        init { knots.add(Pair(currentPose, Math.toRadians(currentHeading))) }

        /** Adds a new waypoint to the trajectory
         *
         * @param x      The x position this robot should be at
         * @param y      The y position this robot should be at
         * @param heading   The direction the robot should be facing in at this waypoint (in degrees)
         * @param splineHeading  The direction this robot should move in at this waypoint (in degrees)
         */
        fun splineTo(x: Double, y: Double, heading: Double, splineHeading: Double) : TrajectoryBuilder {
            if (Pose2d(x, y, Math.toRadians(heading)) != currentPose) { // Cannot spline to the same position
                knots.add(Pair(Pose2d(x, y, Math.toRadians(heading)), Math.toRadians(splineHeading)))
                currentPose = Pose2d(x, y, Math.toRadians(heading))
            }
            return this
        }

        /** Adds a new waypoint to the trajectory
         *
         * @param waypoint  the waypoint to add (pass as a waypoint)
         */
        fun splineTo(waypoint: Waypoint) = this.splineTo(waypoint.x, waypoint.y, waypoint.heading, waypoint.splineHeading)

        /** Create the trajectory
         *
         * @param vmx       the maximum robot velocity
         * @param a       the maximum robot acceleration
         */
        @JvmOverloads
        fun build(vmx: Double, a: Double, vi: Double = 0.0, vf: Double = 0.0, res: Int = 100): Trajectory {
            if (knots.size <= 1) throw Exception("Every trajectory must have at least two points")
            val splines = mutableListOf<QuinticSpline>()
            for (i in 1 until knots.size) {
                (sqrt(knots[i-1].first.x * knots[i-1].first.x + knots[i].first.x * knots[i].first.x)).let{splines.add(QuinticSpline(
                        QuinticPolynomial.fitPolynomial(knots[i-1].first.x, knots[i].first.x, it*cos(knots[i-1].second), it*cos(knots[i].second), 0.0, 0.0),
                        QuinticPolynomial.fitPolynomial(knots[i-1].first.y, knots[i].first.y, it*sin(knots[i-1].second), it*sin(knots[i].second), 0.0, 0.0),
                res))}
            }
            return Trajectory(splines.toTypedArray(), vmx, a, vi, vf)
        }
    }

    companion object {
        /** Returns a TrajectoryBuilder instance
         *
         * @param x      The x position this robot should be at
         * @param y      The y position this robot should be at
         * @param heading   The direction the robot should be facing in at (in degrees)
         * @param splineHeading  The direction this robot should move in at (in degrees)
         */
        @JvmStatic
        fun builder(x: Double, y: Double, heading: Double, splineHeading: Double)
                = TrajectoryBuilder(Pose2d(x, y, Math.toRadians(heading)), splineHeading)

        /** Returns TrajectoryBuilder Instance
         *
         * @param waypoint  the first waypoint in the trajectory
         */
        @JvmStatic
        fun builder(waypoint: Waypoint) = builder(waypoint.x, waypoint.y, waypoint.heading, waypoint.splineHeading)
    }
}