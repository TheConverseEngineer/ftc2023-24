package org.firstinspires.ftc.teamcode.opmodes;

import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.hardware.PwmControl;

import org.firstinspires.ftc.teamcode.trajectory.command.GamepadDriveCommand;

/** Temporary OpMode designed to showcase how this works */
public class SampleOpMode extends HardwareOpMode {

    @Override
    public void onInit() {
        // Opens/closes the gripper when the right bumper is pressed
        driver.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .toggleWhenPressed(gripper::closeClaw, gripper::openClaw);



        // Drivetrain control (this should never be interrupted)
        schedule(false, new GamepadDriveCommand(drivetrain,
                driver::getRightY, driver::getRightX, driver::getLeftX));
    }


}
