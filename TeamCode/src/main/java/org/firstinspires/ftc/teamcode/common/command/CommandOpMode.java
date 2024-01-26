package org.firstinspires.ftc.teamcode.common.command;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.common.command.gamepad.GamepadEx;

import java.util.List;

public abstract class CommandOpMode extends LinearOpMode {

    /** Reference to the command scheduler */
    protected final CommandScheduler scheduler = CommandScheduler.getInstance();

    /** Wrapper for gamepad 1 */
    protected final GamepadEx driver = new GamepadEx(() -> gamepad1);

    /** Wrapper for gamepad 2*/
    protected final GamepadEx codriver = new GamepadEx(() -> gamepad2);

    private List<LynxModule> modules;

    private double lastLoopTime = 0;
    private boolean inSimulation = false;

    @Override
    public void runOpMode() throws InterruptedException {
        scheduler.reset();

        modules = hardwareMap.getAll(LynxModule.class);
        modules.forEach(module -> module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        initialize();

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            modules.forEach(LynxModule::clearBulkCache);

            driver.update();
            codriver.update();

            scheduler.run();

            double loopTime = System.nanoTime()/(1e6) - lastLoopTime;
            lastLoopTime += loopTime;
            telemetry.addData("lt", loopTime);

            telemetry.update();
        }

    }

    /** Call this method at the start of init to use a simulator instead of the actual robot. */
    protected void enableSimMode() {

    }

    /** Initialize commands and subsystems here!
     * DO NOT FORGET to register subsystems schedule start-up
     * Gamepad bindings can also be done here. */
    public abstract void initialize();
}
