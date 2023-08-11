package org.firstinspires.ftc.teamcode.thundercore.command;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.thundercore.hardware.PhysicalHardwareMap;
import org.firstinspires.ftc.teamcode.thundercore.hardware.ThunderHardwareMap;
import org.firstinspires.ftc.teamcode.thundercore.hardware.VirtualHardwareMap;
import org.firstinspires.ftc.teamcode.thundercore.trigger.ThunderGamepad;
import org.firstinspires.ftc.teamcode.thundercore.utils.Logger;

abstract public class ThunderOpMode extends LinearOpMode {

    /** The command scheduler instance*/
    protected final CommandScheduler scheduler = CommandScheduler.INSTANCE;

    /** The logger instance */
    protected final Logger logger = Logger.INSTANCE;

    /** The driver's gamepad */
    protected final ThunderGamepad driver = new ThunderGamepad(() -> gamepad1);

    /** The codriver's gamepad */
    protected final ThunderGamepad codriver = new ThunderGamepad(() -> gamepad2);

    /** The hardware map */
    protected ThunderHardwareMap thunderMap;

    @Override
    public final void runOpMode() {
        scheduler.reset();
        logger.setTelemetry(telemetry);

        // Add some debugging messages
        scheduler.addInitAction((Command cmd) -> logger.logMessage("Scheduled " + cmd.getClass().getSimpleName()));
        scheduler.addEndAction((Command cmd, Boolean cancelled) ->
                logger.logMessage((cancelled?"Cancelled ":"Ended " + cmd.getClass().getSimpleName())));

        // Set up the hardware map
        thunderMap = new PhysicalHardwareMap(hardwareMap);

        initializeHardware();
        initializeCommands();

        waitForStart();

        logger.logMessage("Began Op Mode");

        long lastLoopEpoch = System.nanoTime();
        while (!isStopRequested()) {
            thunderMap.updateCache();

            driver.update();
            codriver.update();

            scheduler.updateCommandScheduler();

            long deltaLoopTime = System.nanoTime() - lastLoopEpoch;
            logger.put("Loop Time", deltaLoopTime/(1000000.0));
            lastLoopEpoch += deltaLoopTime;

            logger.update();
        }

        logger.logMessage("Op Mode ended");
    }

    /** Create {@link Subsystem subsystems} here.
     * If you are going to use virtual hardware, declare that on the first line by calling {@link ThunderOpMode#enableVirtualHardware() enableVirtualHardware}.
     * You can access the scheduler by using the {@link ThunderOpMode#scheduler} instance variable.
     * <br>
     * Optional: You can also change {@link org.firstinspires.ftc.teamcode.thundercore.utils.Logger logger} configurations here.
     */
    public abstract void initializeHardware();

    /** Create/schedule {@link Command commands} that you want to run immediately when the opmode begins here.
     * You can also use this method to set up gamepad control using the
     * {@link ThunderOpMode#driver driver} and {@link ThunderOpMode#codriver codriver} instances. You can access
     * the scheduler by using the {@link ThunderOpMode#scheduler} instance variable
     */
    public abstract void initializeCommands();

    /** If called, this opmode will use virtual hardware (requires FTCDashboard and a phone) */
    protected final void enableVirtualHardware() {
        thunderMap = new VirtualHardwareMap();
    }
}
