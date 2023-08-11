package org.firstinspires.ftc.teamcode.blacksmithcore.controllers

import kotlin.math.abs

interface FeedbackController {
    /** Runs the PID Calculation
     *
     * @param currentPos    the current position of the system
     * @return              the motor velocity
     */
    fun calculate(currentPosition: Double) : Double

    /** Sets a new target for the PID controller
     *  @param newTarget    the new target for the PID controller
     */
    fun setNewTarget(newTarget: Double)
}

/** An empty class representing the absence of a feedback controller */
class NoFeedback : FeedbackController {
    override fun calculate(currentPosition: Double) = 0.0
    override fun setNewTarget(newTarget: Double) { }
}

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
class PIDController(
    var kP: Double,
    var kI: Double,
    var kD: Double,
    var intLimit: Double,
    var stabilityLimit: Double,
    var lowPassGain: Double,
    private var targetPos: Double
): FeedbackController {
    private var calledYet = false
    private var lastEpoch: Long = 0

    private var previousError = 0.0
    private var integralSum = 0.0
    private var derivative = 0.0

    private var lastDerivativeEstimate = 0.0

    override fun calculate(currentPosition: Double): Double {
        val dt = getDeltaTime()
        (targetPos - currentPosition).let { error ->
            // Put kd first so that ki can use the integral
            return (
                    error*kP + calculateDerivative(error, dt)*kD + integrate(error, dt)*kI
                    ).also {previousError = error}
        }
    }

    override fun setNewTarget(newTarget: Double) {
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