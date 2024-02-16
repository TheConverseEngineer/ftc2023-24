package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.command.CommandOpMode;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;

@Config
@TeleOp
public class ActuatorTunerOpMode extends CommandOpMode {
    private SlideSubsystem actuator;

    public static double armTarget = 0, slideTarget = 0;

    private double lastArmTarget = armTarget;
    private double lastSlideTarget = slideTarget;

    @Override
    public void initialize() {
        enableDashboard();

        actuator = new SlideSubsystem(hardwareMap);

        scheduler.registerSubsystem(actuator);
    }

    @Override
    public void run() {
        if (Math.abs(lastArmTarget - armTarget) > 0.0001) {
            lastArmTarget = armTarget;
            actuator.setArmTarget(armTarget);
        }

        if (Math.abs(lastSlideTarget - slideTarget) > 0.0001) {
            lastSlideTarget = slideTarget;
            actuator.setSlideTarget(slideTarget);
        }

        DashboardManager.getInstance().put("arm pos", actuator.armPosition);
        DashboardManager.getInstance().put("slide pos", actuator.slidePosition);
        DashboardManager.getInstance().put("arm tar", actuator.armController.getTargetPosition());
        DashboardManager.getInstance().put("slide tar", actuator.slideController.getTargetPosition());

    }
}
