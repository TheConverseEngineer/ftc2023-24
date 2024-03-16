package org.firstinspires.ftc.teamcode.common.command;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.common.command.gamepad.GamepadEx;
import org.firstinspires.ftc.teamcode.common.simulation.VirtualHardwareMapFactory;
import org.firstinspires.ftc.teamcode.common.utils.DashboardManager;

import java.util.List;

public abstract class CommandOpMode extends LinearOpMode {

    /** Reference to the command scheduler */
    protected final CommandScheduler scheduler = CommandScheduler.getInstance();

    /** Wrapper for gamepad 1 */
    protected final GamepadEx driver = new GamepadEx(() -> gamepad1);

    /** Wrapper for gamepad 2*/
    protected final GamepadEx codriver = new GamepadEx(() -> gamepad2);

    private double lastLoopTime = 0;
    private boolean inSimulation = false;
    private boolean dashboardEnabled = false;

    @Override
    public final void runOpMode() throws InterruptedException {
        scheduler.reset();

        initialize();

        List<LynxModule> modules = hardwareMap.getAll(LynxModule.class);
        modules.forEach(module -> module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        while (opModeInInit() && !isStopRequested()) {
            modules.forEach(LynxModule::clearBulkCache);
            initLoop();
        }

        begin();

        lastLoopTime = System.nanoTime()/(1e6);

        while (opModeIsActive() && !isStopRequested()) {
            modules.forEach(LynxModule::clearBulkCache);

            driver.update();
            codriver.update();

            run();

            scheduler.run();

            double loopTime = System.nanoTime()/(1e6) - lastLoopTime;
            lastLoopTime += loopTime;
            telemetry.addData("lt", loopTime);

            if (inSimulation) {
                VirtualHardwareMapFactory.getInstance().updateSimEngines(loopTime/1000);
                if (dashboardEnabled) {
                    DashboardManager.getInstance().drawRobot(VirtualHardwareMapFactory.getInstance().getDrivetrainPosition());
                    telemetry.addData("x", VirtualHardwareMapFactory.getInstance().getDrivetrainPosition().get(0, 0));
                    telemetry.addData("y", VirtualHardwareMapFactory.getInstance().getDrivetrainPosition().get(1, 0));
                    telemetry.addData("theta", VirtualHardwareMapFactory.getInstance().getDrivetrainPosition().get(2, 0));

                }
            }

            if (dashboardEnabled) {
                DashboardManager.getInstance().update();
                telemetry.addData("update", "true");
            }

            //telemetry.update();
        }

    }

    protected void initLoop() { }

    /** Call this method at the start of init to use a simulator instead of the actual robot. */
    protected final void enableSimMode(Pose2d startPose) {
        VirtualHardwareMapFactory.getInstance().generateVirtualization();
        VirtualHardwareMapFactory.getInstance().setPose(startPose);
        hardwareMap = VirtualHardwareMapFactory.getInstance().getVirtualMap();

        inSimulation = true;
    }

    /** Call this method at the start of init to use the ftc web dashboard */
    protected final void enableDashboard() {
        dashboardEnabled = true;
    }

    /** Initialize commands and subsystems here!
     * DO NOT FORGET to register subsystems schedule start-up
     * Gamepad bindings can also be done here. */
    public abstract void initialize();

    /** Put code that should run on every loop iteration here. */
    public void run() { }

    /** Put code that should be run once on start here. */
    public void begin() { }
}
