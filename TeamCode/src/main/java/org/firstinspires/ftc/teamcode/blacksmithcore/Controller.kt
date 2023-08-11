package org.firstinspires.ftc.teamcode.blacksmithcore

import kotlin.math.abs
import kotlin.math.cos

/** A representation of a PID class
 *
 *  @param kP               the proportional gain
 *  @param kI               the integral gain
 *  @param kD               the derivative gain
 *  @param intLimit         the maximum value the integral can take
 *  @param stabilityLimit   the maximum value of the derivative before the integral activates
 *  @param lowPassGain      the gain on the lowPassFilter (between 0 and 1, lower is more noise, higher is more lag)
 *  In order to use this with FTCDashboard, declare tuning values as static and update them in a loop
 */
open class PIDController (
    var kP: Double,
    var kI: Double,
    var kD: Double,
    var intLimit: Double,
    var stabilityLimit: Double,
    var lowPassGain: Double,
    private var targetPos: Double
 ) {
    private var calledYet = false
    private var lastEpoch: Long = 0

    private var previousError = 0.0
    private var integralSum = 0.0
    private var derivative = 0.0

    private var lastDerivativeEstimate = 0.0

    /** Runs the PID Calculation
     *
     * @param currentPos    the current position of the system
     * @return              the motor velocity
     */
    open fun calculate(currentPos: Double): Double {
        val dt = getDeltaTime()
        (targetPos - currentPos).let { error ->
            // Put kd first so that ki can use the integral
            return (
                    error*kP + calculateDerivative(error, dt)*kD + integrate(error, dt)*kI
            ).also {previousError = error}
        }
    }

    /** Sets a new target for the PID controller
     *  @param newTarget    the new target for the PID controller
     */
    open fun setNewTarget(newTarget: Double) {
        targetPos = newTarget
        integralSum = 0.0
    }

    fun setTuningValues(kP: Double, kI: Double, kD: Double, intLimit: Double, stabilityLimit: Double, lowPassGain: Double) {
        this.kP = kP
        this.kI = kI
        this.kD = kD
        this.intLimit = intLimit
        this.stabilityLimit = stabilityLimit
        this.lowPassGain = lowPassGain
    }

    /** Calculates the derivative (using a low-pass filter) */
    private fun calculateDerivative(error: Double, dt: Double): Double {
        derivative = (lowPassGain*lastDerivativeEstimate + (1-lowPassGain)*(error-previousError)/dt).also { lastDerivativeEstimate = it }
        return derivative
    }

    /** Calculates the integral (with a few added safety measures) */
    private fun integrate(error: Double, dt: Double): Double {
        if (crossOverDetected(error, previousError)) integralSum = 0.0
        else if(abs(derivative) > stabilityLimit) return integralSum
        integralSum = (integralSum+((error+previousError) / 2)*dt).coerceIn(-intLimit, intLimit)
        return integralSum
    }

    /** Returns the time in seconds since the last loop iteration */
    private fun getDeltaTime(): Double {
        if (!calledYet) {
            calledYet = true
            lastEpoch = System.nanoTime()
            return 0.0
        }
        return ((System.nanoTime() - lastEpoch).also { lastEpoch += it })/1000000000.0
    }

    private fun crossOverDetected(error: Double, prev: Double) = (error >= 0 && prev < 0) || (error <= 0 && prev > 0)
}

/** A simple PIDF controller that combines the stock PID controller with a static term
 *
 * @param kStatic   the constant term
 * */
open class PIDFController(
    kP: Double,
    kI: Double,
    kD: Double,
    var kStatic: Double,
    intLimit: Double,
    stabilityLimit: Double,
    lowPassGain: Double,
    targetPos: Double
) : PIDController(kP, kI, kD, intLimit, stabilityLimit, lowPassGain, targetPos) {

    @Override
    override fun calculate(currentPos: Double): Double {
        return super.calculate(currentPos) + kStatic
    }
}

/** A PIDF controller that uses angles (intended for arms)
 * assumes that targetPos is in radians with vertical being pi/2
 *
 * @param kStatic   the constant term
 */
open class PIDFAngleController(
    kP: Double,
    kI: Double,
    kD: Double,
    var kStatic: Double,
    intLimit: Double,
    stabilityLimit: Double,
    lowPassGain: Double,
    targetPos: Double
) : PIDController(kP, kI, kD, intLimit, stabilityLimit, lowPassGain, targetPos) {

    @Override
    override fun calculate(currentPos: Double): Double {
        return super.calculate(currentPos) + kStatic*cos(currentPos)
    }
}



