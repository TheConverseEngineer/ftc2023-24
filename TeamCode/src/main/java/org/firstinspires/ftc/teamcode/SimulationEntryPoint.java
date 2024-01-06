package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.thunder.opensim.SimParameters;
import com.thunder.opensim.SimulationRunner;
import com.thunder.opensim.SimulatorHandler;

/** Used to enter the simulation engine. Currently a work-in-progress */
public class SimulationEntryPoint {


    /*public static void main(String[] args) {
        SimParameters params = new SimParameters.SimParametersBuilder()
                .addFlywheelSubsystem(1, 96, 40)
                .addMotor("leftfrontdrive")
                .addMotor("rightfrontdrive")
                .build();

        SimulationRunner.runSimulation(TestOpMode.class, params);
    }*/

    public static class TestOpMode extends LinearOpMode {
        private DcMotorEx leftFront, rightFront;

        @Override
        public void runOpMode() throws InterruptedException {
            leftFront = hardwareMap.get(DcMotorEx.class, "leftfrontdrive");
            rightFront = hardwareMap.get(DcMotorEx.class, "rightfrontdrive");
            System.out.println("Initialization complete");

            waitForStart();

            leftFront.setPower(1);
            rightFront.setPower(1);

            System.out.println("OpMode looping");
            while (opModeIsActive() && !isStopRequested()) { }

            System.out.println("OpMode Stopped");
        }
    }


}
