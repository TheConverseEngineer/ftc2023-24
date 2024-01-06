package org.firstinspires.ftc.teamcode.opmodes;

import com.arcrobotics.ftclib.command.OdometrySubsystem;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.DrivetrainConstants;
import org.firstinspires.ftc.teamcode.extensions.CachedMotor;
import org.firstinspires.ftc.teamcode.extensions.ThunderOpMode;
import org.firstinspires.ftc.teamcode.subsystems.ActuatorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GripperSubsystem;
import org.firstinspires.ftc.teamcode.trajectory.command.MecanumDriveSubsystem;

/** Base opmode that defines all subsystems and hardware. Useful as a template to reduce
 * the amount of copy-pasted code in further opModes. */
public abstract class HardwareOpMode extends ThunderOpMode {

    // All motors, servos, and encoders
    protected CachedMotor leftFrontDrive, rightFrontDrive, leftRearDrive, rightRearDrive;
    protected CachedMotor armMotor, slideMotor1, slideMotor2;
    protected Motor.Encoder leftOdoPod, rightOdoPod, frontOdoPod;
    protected ServoImplEx clawServo;

    // All subsystems
    protected OdometrySubsystem odometry;
    protected MecanumDriveSubsystem drivetrain;
    protected GripperSubsystem gripper;
    protected ActuatorSubsystem actuator;

    @Override
    public final void createSubsystems() {
        createMotors();
        createServos();
        resetEncoders();

        odometry = new OdometrySubsystem(new HolonomicOdometry(
                leftOdoPod::getDistance, rightOdoPod::getDistance, frontOdoPod::getDistance,
                DrivetrainConstants.odoTrackWidth, DrivetrainConstants.odoForwardOffset
        ));

        drivetrain = new MecanumDriveSubsystem(leftFrontDrive, rightFrontDrive, leftRearDrive, rightRearDrive);

        gripper = new GripperSubsystem(clawServo);

        actuator = new ActuatorSubsystem(armMotor, slideMotor1, slideMotor2);
    }

    private void createMotors() {
        leftFrontDrive = new CachedMotor(hardwareMap, "leftFrontDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        rightFrontDrive = new CachedMotor(hardwareMap, "rightFrontDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        leftRearDrive = new CachedMotor(hardwareMap, "leftRearDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);
        rightRearDrive = new CachedMotor(hardwareMap, "rightRearDrive", Motor.GoBILDA.RPM_312).setFFParams(DrivetrainConstants.Kt, DrivetrainConstants.Kstatic);

        leftFrontDrive.setInverted(true);
        leftRearDrive.setInverted(true);

        leftOdoPod = leftFrontDrive.encoder.setDistancePerPulse(DrivetrainConstants.distancePerTick);
        rightOdoPod = rightFrontDrive.encoder.setDistancePerPulse(DrivetrainConstants.distancePerTick);
        frontOdoPod = leftRearDrive.encoder.setDistancePerPulse(DrivetrainConstants.distancePerTick);

        armMotor = new CachedMotor(hardwareMap, "armMotor", Motor.GoBILDA.RPM_435);
        slideMotor1 = new CachedMotor(hardwareMap, "slideMotorA", Motor.GoBILDA.RPM_435);
        slideMotor2 = new CachedMotor(hardwareMap, "slideMotorB", Motor.GoBILDA.RPM_435);

        slideMotor2.setInverted(true);
    }

    private void resetEncoders() {
        leftFrontDrive.stopAndResetEncoder();
        rightFrontDrive.stopAndResetEncoder();
        leftRearDrive.stopAndResetEncoder();

        armMotor.stopAndResetEncoder();
        slideMotor1.stopAndResetEncoder();
    }

    private void createServos() {
        clawServo = hardwareMap.get(ServoImplEx.class, "clawServo");
        clawServo.setPwmRange(new PwmControl.PwmRange(500, 2500));
    }
}
