@file:JvmName("utils")
package org.firstinspires.ftc.teamcode.blacksmithcore

import kotlin.math.*

/** Simple data class representing a dual-number in kotlin
 *  Supports auto-differentiation (first and second derivatives)
 *
 *  @param x    the value
 *  @param dx   the first derivative
 *  @param ddx  the second derivative
 */
data class DualNum(val x: Double, val dx: Double, val ddx: Double) {

    operator fun plus(other: Double) = DualNum(x+other, dx, ddx)
    operator fun minus(other: Double) = DualNum(x-other, dx, ddx)


    operator fun plus(other: DualNum) = DualNum(x+other.x, dx+other.dx, ddx+other.ddx)
    operator fun minus(other: DualNum) = DualNum(x-other.x, dx-other.dx, ddx-other.ddx)
    operator fun times(other: DualNum) = DualNum(x*other.x, x*other.dx + dx*other.x, x*other.ddx + 2*dx*other.dx + ddx*other.x)
    operator fun div(other: DualNum) = DualNum(x/other.x,
            (dx*other.x-x*other.dx)/(other.x*other.x),
            (other.x*other.x*ddx-other.x*(2*dx*other.dx+x*other.ddx)+2*x*other.dx*other.dx)/(other.x*other.x*other.x)
    )

    fun pow(exp: Double) = DualNum(x.pow(exp), exp*x.pow(exp-1)*dx, exp*x.pow(exp-2)*(x*ddx+(exp-1)*dx*dx))

    override fun toString() = "[$x $dx $ddx]"

    companion object {
        fun constant(x: Double) = DualNum(x, 0.0, 0.0)
        fun variable(x: Double) = DualNum(x, 1.0, 0.0)
    }
}

/** Simple dataclass representing a 2d cartesian vector made up of dual numbers in kotlin
 *
 * @param x     the x component
 * @param y     the y component
 */
data class Vector2dDual(val x: DualNum, val y: DualNum) {
    fun getPosVector() = Vector2d(x.x, y.x)
    fun getVelVector() = Vector2d(x.dx, y.dx)

    override fun toString() = "($x, $y)"
}

/** Simple dataclass representing a 2d cartesian vector made up of dual numbers in kotlin
 *
 * @param x     the x component
 * @param y     the y component
 */
data class Vector2d(val x: Double, val y: Double) {
    override fun toString() = "($x, $y)"
}

/** Simple dataclass representing a robot pose in kotlin
 *
 * @param x     the x position of the robot
 * @param y     the y position of the robot
 * @param theta the heading of the robot (in radians)
 */
data class Pose2d(val x: Double, val y: Double, val theta: Double) {
    override fun equals(other: Any?) = (other is Pose2d) && (x epsEquals other.x) && (y epsEquals other.y) && (theta epsEquals other.theta)
    override fun toString() = "($x, $y, $theta)"
    override fun hashCode() = 31*31 * x.hashCode() + 31*y.hashCode() + theta.hashCode();
}

data class Pose2dDual(val x: DualNum, val y: DualNum, val theta: DualNum) {
    fun toPose() = Pose2d(x.x, y.x, theta.x)

    override fun toString() = "($x, $y, $theta)"
    companion object {
        @JvmStatic
        fun createFromVector(vector: Vector2dDual, theta: DualNum): Pose2dDual {
            return Pose2dDual(vector.x, vector.y, theta)
        }
    }
}

/** Mutable version of Pose2d
 * This version assumes that theta is in radians
 * @see Pose2d */
data class MutablePose2D(var x : Double, var y: Double, var theta: Double) {

    fun integrateGlobalTransform(dx: Pose2d) {
        val xr: Double
        val yr: Double
        if (dx.theta epsEquals 0.0) {
            xr = sin(dx.theta)*dx.x/dx.theta - (1- cos(dx.theta))*dx.y/dx.theta
            yr = (1 - cos(dx.theta))*dx.x/dx.theta + sin(dx.theta)*dx.y/dx.theta
        } else {
            xr = dx.x
            yr = dx.y
        }

        x += cos(theta)*xr - sin(theta)*yr
        y += sin(theta)*xr -+ cos(theta)*yr
        theta += dx.theta
    }

    override fun toString() = "($x, $y, $theta)"
    override fun hashCode() = 31 * x.hashCode() + y.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is MutablePose2D -> (x epsEquals other.x) && (y epsEquals other.y) && (theta epsEquals other.theta)
            is Pose2d -> (x epsEquals other.x) && (y epsEquals other.y) && (theta epsEquals other.theta)
            else -> false
        }
    }

    fun toPose() = Pose2d(x, y, theta)
}

data class WheelSpeeds(val leftFront: Double, val leftRear: Double, val rightRear: Double, val rightFront: Double) {
    override fun toString() = "WS: [$leftFront $leftRear $rightRear $rightFront]"
}

/* Some math methods */

infix fun Double.epsEquals(other: Double) = abs(this-other)<0.0000001
fun linspace(start: Double, stop: Double, num: Int) = (0..num).map {start + it*(stop-start)/num}
fun clamp(x: Double, mn: Double, mx: Double) = max(mn,min(x, mx))
