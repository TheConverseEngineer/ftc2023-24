package org.firstinspires.ftc.teamcode.blacksmithcore
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt

/** Small Immutable Vector class consisting of two Variables  */
data class DifferentiatedVector2d<T>(val x: Variable<T>, val y: Variable<T>) {
    fun firstDerMagnitude() = sqrt(x.dx * x.dx + y.dx * y.dx)

    fun getVector() = Vector2d(x.x, y.x)
}

/** Standard Vector2d implementation */
data class Vector2d(val x: Double, val y: Double) {
    infix fun dot(other: Vector2d) = x*other.x + y*other.x
    infix fun cross(other: Vector2d) = x*other.y - y*other.x

    operator fun plus(other: Vector2d) = Vector2d(x+other.x, y+other.y)
    operator fun minus(other: Vector2d) = Vector2d(x-other.x, y-other.y)
    operator fun times(other: Double) = Vector2d(x*other, y*other)

    fun angle() = atan2(y, x)
    fun magnitude() = hypot(x, y)
}

/** Finds the greatest positive double such that given function evaluates to false,
 *  subject to the inputted precision
 *
 *  Implemented through unbounded binary search. Complexity is log(precision)
 */
fun unboundedBinarySearch(evalFunc: (Double)->Boolean, precision: Double): Double {
    var step = precision
    var currentCheck: Double = 0.0

    while (!evalFunc(currentCheck)) {
        currentCheck += step;
        step *= 2
    }

    // Now binary step in reverse!
    while (step > precision) {
        while (currentCheck - step > 0 && !evalFunc(currentCheck - step)) currentCheck -= step
        step /= 2
    }

    return currentCheck
}