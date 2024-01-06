package org.firstinspires.ftc.teamcode.trajectory.command

import com.arcrobotics.ftclib.command.CommandBase
import com.arcrobotics.ftclib.command.OdometrySubsystem
import com.arcrobotics.ftclib.util.MathUtils.clamp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.DrivetrainConstants
import org.firstinspires.ftc.teamcode.trajectory.Trajectory
import org.firstinspires.ftc.teamcode.trajectory.Vector2d

class FollowTrajectoryCommand @JvmOverloads constructor(
    val drivetrain: MecanumDriveSubsystem,
    val odometry: OdometrySubsystem,
    val trajectory: Trajectory,
    val breakOnCompletion: Boolean = true
) : CommandBase() {

    private val timer: ElapsedTime = ElapsedTime()

    init {
        addRequirements(drivetrain)
    }

    override fun initialize() {
        timer.reset()
    }

    override fun execute() {
        val currentPose = odometry.pose
        val currentHeading = currentPose.heading
        val currentPos = Vector2d(currentPose.x, currentPose.y)
        val trajectoryState = trajectory.getGlobalRobotTargetAtTime(timer.time())

        val correctionMagnitude = DrivetrainConstants.Kp*(Vector2d.dist(trajectoryState.position, currentPos))

        val localVel = trajectoryState.convertToLocalVel(
            (currentPos-trajectoryState.position).setMagnitude(clamp(correctionMagnitude, -DrivetrainConstants.maxCorrectionSpeed, DrivetrainConstants.maxCorrectionSpeed)),
            currentHeading
        )

        drivetrain.setDrivetrainVelocity(localVel, trajectoryState.headingVelocity +
            clamp(DrivetrainConstants.headingKp*(trajectoryState.targetHeading-currentHeading), -DrivetrainConstants.maxCorrectionHeadingSpeed, DrivetrainConstants.maxCorrectionHeadingSpeed))
    }

    override fun isFinished() = timer.time() >= trajectory.getDuration()

    override fun end(interrupted: Boolean) {
        if (breakOnCompletion) drivetrain.stop()
    }
}