package org.firstinspires.ftc.teamcode.subsystems;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.common.command.Subsystem;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.common.utils.ProfiledSystemController;

@Config
public class SlideSubsystem implements Subsystem {

    public final ProfiledSystemController slideController, armController;
    public static ProfiledSystemController.ProfileConstants slideConstants
            = new ProfiledSystemController.ProfileConstants(70, 90, 0.03, 1);
    public static ProfiledSystemController.ProfileConstants armConstants
            = new ProfiledSystemController.ProfileConstants(4.5, 3, .22, 3);
    public static double SLIDE_TICKS_PER_INCH = 1/0.01838383217;
    public static double ARM_TICKS_PER_RAD = 428.365529;
    public static double SLIDE_MIN_POS = -0.2, SLIDE_MAX_POS = 22.5;
    public static double ARM_MIN_POS = -0.1, ARM_MAX_POS = 2.08;

    public static double armkF = 0.15, armkPV = 0;

    private final DcMotorEx slideMotor1, slideMotor2, armMotor;

    public double armPosition = 0, slidePosition = 0;
    private double lastArmTarget = 0, lastSlideTarget = 0;

    private boolean armkFEnabled = true;

    public SlideSubsystem(HardwareMap hardwareMap) {
        slideController = new ProfiledSystemController(0, slideConstants);
        slideMotor1 = hardwareMap.get(DcMotorEx.class, "slideMotor1");
        slideMotor2 = hardwareMap.get(DcMotorEx.class, "slideMotor2");
        slideMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        armController = new ProfiledSystemController(0, armConstants);
        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");

        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        armMotor.setZeroPowerBehavior(BRAKE);
        slideMotor1.setZeroPowerBehavior(BRAKE);
        slideMotor2.setZeroPowerBehavior(BRAKE);

    }

    @Override
    public void periodic() {
        armPeriodic();
        slidePeriodic();
    }

    private void armPeriodic() {
        armPosition = armMotor.getCurrentPosition() / ARM_TICKS_PER_RAD;
        if (armPosition < ARM_MIN_POS) {
            armMotor.setPower(0.25);
        } else if (armPosition > ARM_MAX_POS) {
            armMotor.setPower(-0.25);
        } else {
            double power = armController.getMotorPower(armPosition) + (armkFEnabled?(armkF*Math.cos(armPosition)):0) +
                    armkPV*(armController.lastTargetVelocity - (armMotor.getVelocity()/ARM_TICKS_PER_RAD));
            armMotor.setPower(power);
            DashboardManager.getInstance().put("arm vel", armMotor.getVelocity()/ARM_TICKS_PER_RAD);
            DashboardManager.getInstance().put("target vel", armController.lastTargetVelocity);
        }
    }

    public void enableArmKf() {
        armkFEnabled = true;
    }

    public void disableArmKf() {
        armkFEnabled = false;
    }

    private void slidePeriodic() {
        slidePosition = slideMotor1.getCurrentPosition() / SLIDE_TICKS_PER_INCH - armPosition;
        if (slidePosition < SLIDE_MIN_POS) {
            slideMotor1.setPower(.25);
            slideMotor2.setPower(.25);
        } else if (slidePosition > SLIDE_MAX_POS) {
            slideMotor1.setPower(-.25);
            slideMotor2.setPower(-.25);
        } else {
            double power = slideController.getMotorPower(slidePosition); // TODO: Feedforward term
            slideMotor1.setPower(power);
            slideMotor2.setPower(power);

            DashboardManager.getInstance().put("sp", power);
        }
    }

    /** Sets a new target for the slide (in inches)*/
    public void setSlideTarget(double newPos) {
        this.lastSlideTarget = newPos;
        this.slideController.setNewTarget(newPos);
    }

    /** Sets a new target for the arm (<u>in degrees</u>) */
    public void setArmTarget(double newAngle) {
        this.lastArmTarget = newAngle;
        this.armController.setNewTarget(Math.toRadians(newAngle));
    }

    /** In degrees */
    public double getLastArmTarget() {
        return lastArmTarget;
    }

    public double getLastSlideTarget() {
        return lastSlideTarget;
    }
}