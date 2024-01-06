package org.firstinspires.ftc.teamcode.trajectory.controllers

import kotlin.math.abs
import kotlin.math.withSign

interface FeedforwardController {
    fun getFeedforward(targetPos: Double, currentPos: Double, targetVel: Double, targetAccel: Double) : Double
}

/** Simple feedforward term that adds a constant power to the output (useful for counteracting gravity)*/
class ConstantFeedforward(private val kF: Double) : FeedforwardController {
    override fun getFeedforward(
        targetPos: Double, currentPos: Double, targetVel: Double, targetAccel: Double
    ) = kF
}

class ProfileFeedforward(private val coefficients: ProfileCoefficients) : FeedforwardController {
    override fun getFeedforward(
        targetPos: Double, currentPos: Double, targetVel: Double, targetAccel: Double
    ) = coefficients.kV * targetVel + coefficients.kA * targetAccel +
            if (abs(targetVel) < 0.001) 0.0 else coefficients.kS * ((1.0).withSign(targetVel))
}

class PIDFController(
    coefficients: PIDCoefficients,
    private val feedforwards: Array<FeedforwardController>
) : PIDController(coefficients) {

    override fun update(currentPos: Double): Double {
        var output = super.update(currentPos)
        for (feedforward in feedforwards)
            output += feedforward.getFeedforward(getTargetPos(), currentPos, 0.0, 0.0)
        return output
    }
}