package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.trajectory.DriveSubsystem;
import org.firstinspires.ftc.teamcode.common.utils.MathUtils;
import org.firstinspires.ftc.teamcode.subsystems.GripperSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.WristSubsystem;

@TeleOp
public class StandardTeleOp extends CommandOpMode {

    SlideSubsystem slideSubsystem;
    GripperSubsystem gripper;
    DriveSubsystem drive;
    WristSubsystem wrist;


    public enum SlideState{LOWERED, RAISING, LOWERING, RAISED}

    private SlideState currentSlideState = SlideState.LOWERED;
    private int currentStackTarget = 2;

    @Override
    public void initialize() {

        slideSubsystem = new SlideSubsystem(hardwareMap);
        gripper = new GripperSubsystem(hardwareMap);
        drive = new DriveSubsystem(hardwareMap);
        wrist = new WristSubsystem(hardwareMap);

        scheduler.registerSubsystem(slideSubsystem, gripper, drive, wrist);

        codriver.add("raise", codriver.new TriangleToggleButton() {
            @Override
            public void onPress(boolean value) {
                currentSlideState = SlideState.RAISING;
            }
        });

        codriver.add("lower", codriver.new CircleToggleButton() {
            @Override
            public void onPress(boolean value) {
                currentSlideState = SlideState.LOWERING;
            }
        });

        codriver.add("backdrop up", codriver.new RightToggleTrigger() {
            @Override
            public void onPress(boolean value) {
                currentStackTarget += 2;
                currentStackTarget = (int)MathUtils.clamp(currentStackTarget, 2, 20);
            }
        });

        codriver.add("backdrop down", codriver.new LeftToggleTrigger() {
            @Override
            public void onPress(boolean value) {
                currentStackTarget -= 2;
                currentStackTarget = (int)MathUtils.clamp(currentStackTarget, 2, 20);
            }
        });

        driver.add("close claw", driver.new LeftBumperToggleButton() {
            @Override
            public void onPress(boolean value) {
                if (currentSlideState == SlideState.RAISED) gripper.toggleDeposit();
                else gripper.toggleIntake();
            }
        });

        codriver.add("rb", codriver.new RightBumperToggleButton() {
            @Override
            public void onPress(boolean value) {
                if (gamepad2.a) {
                    wrist.LOWER_WRIST_INTAKE -= 0.01;
                    if (wrist.LOWER_WRIST_INTAKE < 0) wrist.LOWER_WRIST_INTAKE = 0;
                } else if (gamepad2.x) {
                    wrist.LOWER_WRIST_OUTTAKE -= 0.01;
                    if (wrist.LOWER_WRIST_OUTTAKE < 0) wrist.LOWER_WRIST_OUTTAKE = 0;
                } else {
                    gripper.openLeftClaw();
                }
            }
        });

        codriver.add("lb", codriver.new LeftBumperToggleButton() {
            @Override
            public void onPress(boolean value) {
                if (gamepad2.a) {
                    wrist.LOWER_WRIST_INTAKE += 0.01;
                    if (wrist.LOWER_WRIST_INTAKE > 1) wrist.LOWER_WRIST_INTAKE = 1;
                } else if (gamepad2.x) {
                    wrist.LOWER_WRIST_OUTTAKE += 0.01;
                    if (wrist.LOWER_WRIST_OUTTAKE > 1) wrist.LOWER_WRIST_OUTTAKE = 1;
                } else {
                    gripper.openRightClaw();
                }
            }
        });

    }

    @Override
    public void run() {

        drive.driveWithGamepad(gamepad1);

        // Heading resets
        if (gamepad2.dpad_left) {
            drive.getOdometry().setMHeading(Math.PI/2);
        }

        if (gamepad2.dpad_right) {
            drive.getOdometry().setMHeading(-Math.PI/2);
        }

        if (gamepad2.dpad_up) {
            drive.getOdometry().setMHeading(0);
        }

        if (gamepad2.dpad_down) {
            drive.getOdometry().setMHeading(Math.PI);
        }

        // FSM which controls the slides and arms
        switch (currentSlideState) {
            case LOWERED:
                slideSubsystem.disableArmKf();
                break;
            case RAISED:
                slideSubsystem.disableArmKf();
                if (Math.abs(slideSubsystem.getLastSlideTarget() - currentStackTarget) > 0.01) {
                    slideSubsystem.setSlideTarget(currentStackTarget);
                }
                break;
            case LOWERING:
                slideSubsystem.enableArmKf();
                if (Math.abs(slideSubsystem.getLastSlideTarget()) > 0.01)
                    // Move slide first
                    slideSubsystem.setSlideTarget(0);
                else if (Math.abs(slideSubsystem.slidePosition) < 1 && Math.abs(slideSubsystem.getLastArmTarget()) > 0.01) {
                    // Now move the arm
                    slideSubsystem.setArmTarget(0);
                } else if (Math.abs(slideSubsystem.slidePosition) < 1 && Math.abs(slideSubsystem.armPosition) < 0.1) {
                    // The actuator has been lowered
                    currentSlideState = SlideState.LOWERED;
                }
                break;
            case RAISING:
                slideSubsystem.enableArmKf();
                if (Math.abs(slideSubsystem.getLastSlideTarget()) > 0.01)
                    // Move slide first
                    slideSubsystem.setSlideTarget(0);
                else if (Math.abs(slideSubsystem.slidePosition) < 1 && Math.abs(slideSubsystem.getLastArmTarget() - 116) > 0.01) {
                    // Now move the arm
                    slideSubsystem.setArmTarget(116);
                } else if (Math.abs(slideSubsystem.slidePosition) < 1 && Math.abs(slideSubsystem.armPosition - Math.toRadians(116)) < 0.1) {
                    // The actuator has been raised
                    currentSlideState = SlideState.RAISED;
                }

            default:
                break;
        }

        if (currentSlideState == SlideState.LOWERED && gamepad1.right_bumper) {
            wrist.intakePosition();
        } else if(currentSlideState == SlideState.LOWERING) {
            wrist.transferPosition();
        } else if (currentSlideState == SlideState.RAISED) {
            wrist.outtakePosition();
        } else {
            wrist.idlePosition();
        }


        telemetry.addData("state", currentSlideState.name());
        telemetry.addData("lower", wrist.LOWER_WRIST_INTAKE);
        telemetry.addData("height", currentStackTarget);
    }
}
