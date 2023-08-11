package org.firstinspires.ftc.teamcode.blacksmithcore.controllers

import kotlin.math.cos
import kotlin.math.withSign

interface StaticController {
    /** Calculates the static feedforward term
     *
     * @param currentPosition   the current actuator position
     * @param targetVelocity    the power that this actuator should drive at (ignoring this controller)
     */
    fun calculate(currentPosition: Double, targetVelocity: Double) : Double
}

/** Applies a constant force regardless of direction
 *
 * @param kStatic   the applied force
 */
class ConstantStaticFeedforward (var kStatic: Double): StaticController {
    override fun calculate(currentPosition: Double, targetVelocity: Double) = kStatic
}

/** Applies a constant force in the direction of travel
 *
 * @param kStatic   the applied force
 */
class LinearStaticFeedforward (var kStatic: Double) : StaticController{
    override fun calculate(currentPosition: Double, targetVelocity: Double) = kStatic.withSign(targetVelocity)
}

/** Applies a constant force scaled based on the position of the arm
 * NOTE: For this to work, the position must be in radians with pi/2 radians being vertical
 *
 * @param kStatic   the applied force
 */
class ArmStaticFeedforward (var kStatic: Double) : StaticController {
    override fun calculate(currentPosition: Double, targetVelocity: Double) = kStatic * cos(currentPosition)
}