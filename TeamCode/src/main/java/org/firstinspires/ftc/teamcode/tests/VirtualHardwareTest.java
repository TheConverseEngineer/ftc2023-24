package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.skecore.hardware.DcMotorTypes;
import org.firstinspires.ftc.teamcode.skecore.hardware.VirtualDcMotorEx;

@TeleOp
public class VirtualHardwareTest extends LinearOpMode {
    DcMotorEx motor;

    @Override
    public void runOpMode() throws InterruptedException {
        motor = new VirtualDcMotorEx(DcMotorTypes.GOBILDA_435);

        telemetry = FtcDashboard.getInstance().getTelemetry();

        waitForStart();

        ElapsedTime dt = new ElapsedTime();
        dt.reset();

        while (!isStopRequested()) {
            if (dt.seconds() > 8) motor.setPower(0);
            else if (dt.seconds() > 5) motor.setPower(1);

            telemetry.addData("pos", motor.getCurrentPosition());
            telemetry.update();
        }
    }
}
