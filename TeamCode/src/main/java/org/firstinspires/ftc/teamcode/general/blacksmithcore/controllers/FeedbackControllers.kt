package org.firstinspires.ftc.teamcode.general.blacksmithcore.controllers

import kotlin.math.abs

abstract class Controller {

    abstract fun setTargetPos(target: Double)
    abstract fun getTargetPos() : Double

    abstract fun update(currentPos: Double) : Double
}

class ControllerTarget() {
}

open class PIDController(private val coefficients: PIDCoefficients) : Controller() {

    private var targetPos: Double = 0.0;
    private var started = false;
    private var lastError: Double = 0.0;
    private var lastLoopTime: Long = 0;

    // Handled by the calcIntegral and calcDerivative methods
    private var cumError: Double = 0.0;
    private var lastRecordedDerivative = 0.0;

    override fun getTargetPos() = targetPos

    /** Does not update last error! */
    private fun calculateDerivative(currentError: Double, deltaTime: Double) : Double {
        lastRecordedDerivative = (coefficients.lowPassFilter*lastRecordedDerivative +
                (1-coefficients.lowPassFilter)*(currentError-lastError))/deltaTime
        return lastRecordedDerivative
    }

    private fun calculateIntegral(currentError: Double, deltaTime: Double) : Double {
        if (shouldResetIntegral(currentError)) cumError = 0.0
        else { // Trapezoidal Approximation
            cumError += deltaTime * (currentError + lastError) / 2
        }
        return (cumError * coefficients.kI).coerceIn(-coefficients.intLimit, coefficients.intLimit)
    }

    override fun update(currentPos: Double) : Double {
        if (!started) { // Just proportional calculation
            started = true;
            lastError = targetPos - currentPos
            lastLoopTime = System.nanoTime();
            return lastError * coefficients.kP

        } else { // Full PID Calculation
            val error = targetPos - currentPos
            val deltaTime = System.nanoTime() - lastLoopTime

            var result = error*coefficients.kP + calculateDerivative(error, deltaTime/1000000.0)
            calculateIntegral(error, deltaTime/1000000.0).also { integral ->
                if (abs(lastRecordedDerivative) <= coefficients.stabilityLimit) result += integral
            }

            lastLoopTime += deltaTime
            lastError = error

            return result
        }
    }

    override fun setTargetPos(target: Double) {
        this.targetPos = target
    }

    private fun shouldResetIntegral(currentError: Double) =
        (currentError <= 0 && lastError >= 0) || (currentError >= 0 && lastError <= 0)
}