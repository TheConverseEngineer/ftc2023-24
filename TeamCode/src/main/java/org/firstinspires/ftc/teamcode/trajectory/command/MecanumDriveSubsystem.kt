package org.firstinspires.ftc.teamcode.trajectory.command

import com.arcrobotics.ftclib.command.Subsystem
import org.firstinspires.ftc.teamcode.DrivetrainConstants
import org.firstinspires.ftc.teamcode.extensions.CachedMotor
import org.firstinspires.ftc.teamcode.trajectory.Vector2d
import kotlin.math.*

/** Represents a mecanum drivetrain subsystem
 *
 * Note that this class is actually fairly simple -- most of the logic is held in the
 * Trajectory class or in the FollowTrajectory command
 *
 * Make sure to set the feedforward tuning variables on all motors! */
class MecanumDriveSubsystem(
    private val leftFrontDrive: CachedMotor,
    private val rightFrontDrive: CachedMotor,
    private val leftRearDrive: CachedMotor,
    private val rightRearDrive: CachedMotor
) : Subsystem {

    init {
        register()
    }

    fun setDrivetrainVelocity(velocity: Vector2d, headingVelocity: Double) {
        val y = velocity.y
        val x = velocity.x*DrivetrainConstants.Kl
        val omega = DrivetrainConstants.drivetrainMu * headingVelocity

        leftFrontDrive.setFromFeedforward(y + x - omega)
        rightFrontDrive.setFromFeedforward(y + x + omega)
        leftRearDrive.setFromFeedforward(y - x - omega)
        rightRearDrive.setFromFeedforward(y - x + omega)
    }

    fun driveWithGamepad(x: Double, y: Double, rw: Double) {
        val theta = atan2(y, x) - PI/4
        var rho = (x*x + y*y)/(max(abs(cos(theta)), abs(sin(theta))))
        var w = rw
        if (rho+abs(rw) > 1) {
            w = rw/(rho + abs(rw))
            rho /= rw/(rho + abs(rw))
        }
        leftFrontDrive.set(rho*cos(theta)+w)
        rightFrontDrive.set(rho*sin(theta)-w)
        rightRearDrive.set(rho*cos(theta)-w)
        rightRearDrive.set(rho*sin(theta)+w)
    }

    fun stop() {
        leftFrontDrive.set(0.0)
        rightFrontDrive.set(0.0)
        leftRearDrive.set(0.0)
        rightRearDrive.set(0.0)
    }
}