package org.firstinspires.ftc.teamcode.trajectory

import org.firstinspires.ftc.teamcode.DrivetrainConstants
import kotlin.math.cos
import kotlin.math.sin

class TrajectoryBuilder(firstPoint: TrajectoryPoint) {
    private val points = mutableListOf(firstPoint)

    /** Adds a waypoint to the current trajectory
     *
     * @param x                     the x-position that the robot should go to.
     * @param y                     the y-position that the robot should go to.
     * @param heading               the direction that the robot should face at this position (in degrees)
     * @param splineHeading         the direction that the robot should travel in at this point (in degrees)
     * @param velocity              the maximum velocity (in/sec) of the robot while it travels to this point
     * @param spline_ddx            the second derivative of x-spline at this waypoint (only edit this if you know what you are doing)
     * @param spline_ddy            the second derivative of y-spline at this waypoint (only edit this if you know what you are doing)
     */
    @JvmOverloads
    fun goTo(x: Double, y: Double, heading: Double, splineHeading: Double, velocity: Double, spline_ddx: Double=0.0, spline_ddy: Double=0.0) : TrajectoryBuilder {
        points.add(TrajectoryPoint.createFromInput(x, y, heading, splineHeading, velocity, spline_ddx, spline_ddy))
        return this
    }

    /** Generates the final trajectory. In most cases, you should not need to use any parameters here.
     *
     * @param vi            the velocity that the robot should start with
     * @param vf            the velocity that the robot should end with
     * @param resolution    the number of sections that the path should be divided into for profiling purposes
     * @param precision     how precise the motion profile should be (in inches/sec)
     */
    @JvmOverloads
    fun build(vi: Double = 0.0, vf: Double = 0.0, resolution: Int = 1000, precision: Double = 0.1) : Trajectory {
        safeAssert(points.size > 1, "TrajectoryBuilder needs at least 2 points! Make sure the goTo method is used before calling build!")
        val paths = Array<Path>(points.size-1) {i ->
            val r0: Double = if (i == 0) Vector2d.dist(points[i].point, points[i+1].point)
                    else (Vector2d.dist(points[i-1].point, points[i].point) + Vector2d.dist(points[i].point, points[i+1].point))/2
            val r1: Double = if (i == points.size-2) Vector2d.dist(points[i].point, points[i+1].point)
                    else (Vector2d.dist(points[i].point, points[i+1].point) + Vector2d.dist(points[i+1].point, points[i+2].point))/2
            PathSegment(
                Quintic.generateFromControlPoints(points[i].point.x, points[i+1].point.x, r0*cos(points[i].splineHeading), r1*cos(points[i+1].splineHeading), 0.0, 0.0),
                Quintic.generateFromControlPoints(points[i].point.y, points[i+1].point.y, r0*sin(points[i].splineHeading), r1*sin(points[i+1].splineHeading), 0.0, 0.0),
                points[i+1].velocity,
                points[i].heading,
                points[i+1].heading,
                resolution
            )
        }

        return Trajectory(CompoundPath(paths), MecanumConfiguration(
            DrivetrainConstants.maxWheelRPM*DrivetrainConstants.wheelDiameter*Math.PI*DrivetrainConstants.maxSafeAutoSpeed/60,
            (DrivetrainConstants.trackHeight + DrivetrainConstants.trackWidth)/2,
            DrivetrainConstants.maxAcceleration,
            DrivetrainConstants.minAcceleration,
            DrivetrainConstants.Kt,
            DrivetrainConstants.Kc,
            DrivetrainConstants.Kl
        ), resolution, precision, vi, vf)
    }

    companion object {
        /** Builds a trajectory using builder pattern. This is the recommended way to create a trajectory.
         *
         * @param startX                    the x position that the robot should start at
         * @param startY                    the y position that the robot should start at
         * @param startHeading              the robot's initial heading (in degrees)
         * @param startSplineHeading        the direction that the robot should initially move in (in degrees)
         * @param startSpline_ddx            the second derivative of x-spline (only edit this if you know what you are doing)
         * @param startSpline_ddy            the second derivative of y-spline (only edit this if you know what you are doing)
         */
        @JvmStatic
        @JvmOverloads
        fun buildTrajectory(
            startX: Double, startY: Double,
            startHeading: Double, startSplineHeading: Double,
            startSpline_ddx: Double = 0.0, startSpline_ddy: Double = 0.0

        ) = TrajectoryBuilder(TrajectoryPoint.createFromInput(
            startX, startY,
            startHeading, startSplineHeading, 0.0,
            startSpline_ddx, startSpline_ddy
        ))
    }
}

data class TrajectoryPoint(
    val point: Vector2d,
    val heading: Double,
    val splineHeading: Double,
    val velocity: Double,
    val ddx: Double,
    val ddy: Double
) {
    companion object {
        fun createFromInput(x: Double, y: Double, heading: Double, splineHeading: Double, velocity: Double, ddx: Double, ddy: Double) =
            TrajectoryPoint(Vector2d(x, y), Math.toRadians(heading), Math.toRadians(splineHeading), velocity, ddx, ddy)
    }
}