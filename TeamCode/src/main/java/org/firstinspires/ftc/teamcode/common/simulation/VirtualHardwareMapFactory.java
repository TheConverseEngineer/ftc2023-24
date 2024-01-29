package org.firstinspires.ftc.teamcode.common.simulation;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

/** Utility class used to create a virtual mock-up of the lynx device bridge
 * <br>
 * To add functionality, edit the {@link VirtualHardwareMapFactory#generateVirtualization} method.
 * */
public class VirtualHardwareMapFactory {

    private static final VirtualHardwareMapFactory instance = new VirtualHardwareMapFactory();

    private HardwareMap virtualMap = null;
    private DrivetrainSimEngine drivetrain = null;


    public static VirtualHardwareMapFactory getInstance() {
        return instance;
    }

    public HardwareMap getVirtualMap() {
        return virtualMap;
    }

    public Matrix getDrivetrainPosition() {
        return drivetrain.getCurrentPose();
    }

    private VirtualHardwareMapFactory() {

    }

    enum MOTOR {
        GB_435(435, 384.5), GB_312(312, 537.7);

        public final int rpm;
        public final double cpr;
        MOTOR(int rpm, double cpr) {
            this.rpm = rpm;
            this.cpr = cpr;
        }
    }

    public void updateSimEngines(double deltaTime) {
        drivetrain.update(deltaTime);
    }

    public void setPose(Pose2d pose) {
        drivetrain.setCurrentPose(pose);
    }

    public void generateVirtualization() {
        virtualMap = new HardwareMap(null, new OpModeManagerNotifier() {
            @Override
            public OpMode registerListener(Notifications listener) {
                return null;
            }

            @Override
            public void unregisterListener(Notifications listener) {

            }
        });

        addMotor(virtualMap, "armMotor", MOTOR.GB_435);
        addMotor(virtualMap, "slideMotor1", MOTOR.GB_435);
        addMotor(virtualMap, "slideMotor2", MOTOR.GB_435);

        VoltageSensor voltageSensor = new VirtualVoltageSensor();
        virtualMap.put("voltageSensor", voltageSensor);
        virtualMap.voltageSensor.put("voltageSensor", voltageSensor);

        generateDrivetrainMock();

    }

    private static void addMotor(HardwareMap map, String name, MOTOR motorStats) {
        final VirtualDcMotorEx motor = new VirtualDcMotorEx(name, 0, motorStats.rpm, motorStats.cpr);

        map.put(name, motor);
        map.dcMotor.put(name, motor);
    }

    private void generateDrivetrainMock() {
        VirtualDummyMotorEx leftFront = new VirtualDummyMotorEx(),
                            rightFront = new VirtualDummyMotorEx(),
                            leftRear = new VirtualDummyMotorEx(),
                            rightRear = new VirtualDummyMotorEx();

        virtualMap.put("leftFrontDrive", leftFront);
        virtualMap.put("rightFrontDrive", rightFront);
        virtualMap.put("leftRearDrive", leftRear);
        virtualMap.put("rightRearDrive", rightRear);

        virtualMap.dcMotor.put("leftFrontDrive", leftFront);
        virtualMap.dcMotor.put("rightFrontDrive", rightFront);
        virtualMap.dcMotor.put("leftRearDrive", leftRear);
        virtualMap.dcMotor.put("rightRearDrive", rightRear);

        drivetrain = new DrivetrainSimEngine(leftFront, rightFront, leftRear, rightRear);
    }
}