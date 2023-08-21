package org.firstinspires.ftc.teamcode.blacksmithcore
import kotlin.math.sqrt

/** Empty class used as a generic for the Variable class
 *  Represents the arbitrary spline parameter u (0 <= u <= 1)
 */
class SplineParameter

/** Immutable representation of a differentiable variable in Kotlin
 *  In addition to the value itself, the numerical first and second derivatives of the variable are also stored.
 *
 *  @version 1.0
 *  @author TheConverseEngineer
 */
data class Variable<T>(val x: Double, val dx: Double, val ddx: Double) {
    override fun toString() = "{$x $dx $ddx}"

    // Basic operators with other variables
    operator fun plus(other: Variable<T>) = Variable<T>(x+other.x, dx+other.dx, ddx+other.ddx)
    operator fun minus(other: Variable<T>) = Variable<T>(x-other.x, dx-other.dx, ddx-other.ddx)
    operator fun times(other: Variable<T>) = Variable<T>(x*other.x, dx*other.x + x*other.dx, ddx*other.x + 2*dx*other.dx + x*other.ddx)
    operator fun div(other: Variable<T>) = Variable<T>(x/other.x, (dx*other.x - x*other.dx)/(other.x*other.x),
        (other.x*(ddx*other.x - 2*other.dx*dx - other.ddx*x) + 2*x*other.dx*other.dx)/(other.x*other.x*other.x))

    // And now for operators with constants
    operator fun plus(other: Double) = Variable<T>(x + other, dx, ddx)
    operator fun minus(other: Double) = Variable<T>(x - other, dx, ddx)
    operator fun times(other: Double) = Variable<T>(other*x, other*dx, other*ddx)
    operator fun div(other: Double) = Variable<T>(x/other, dx/other, ddx/other)
}

/** Returns the square root of the inputted variable */
fun <T> sqrt(v: Variable<T>) : Variable<T> {
    val sqrtValue: Double = sqrt(v.x)
    return Variable(sqrtValue, v.dx/(2*sqrtValue), (2*v.x*v.ddx - v.dx*v.dx) / (4*sqrtValue*sqrtValue*sqrtValue))
}

/** Returns the square root of the inputted variable */
fun <T> square(v: Variable<T>) = v*v;

/** Simulates differentiating a variable. Note that this just shifts the dx and ddx values. The returned ddx value is always 0. */
fun <T> differentialShift(v: Variable<T>) = Variable<T>(v.dx, v.ddx, 0.0)
