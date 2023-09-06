package org.firstinspires.ftc.teamcode.general.thundercore.actions;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.general.thundercore.input.ThunderGamepad;
import org.firstinspires.ftc.teamcode.general.thundercore.utils.Utils;

import java.util.List;

public abstract class ThunderOpMode extends LinearOpMode {

    /** Wrapper class for {@code gamepad1} */
    protected final ThunderGamepad driver = new ThunderGamepad(() -> gamepad1);

    /** Wrapper class for {@code gamepad2} */
    protected final ThunderGamepad codriver = new ThunderGamepad(() -> gamepad2);

    /** Reference to the Action Scheduler */
    protected final ActionScheduler scheduler = ActionScheduler.getInstance();

    /** Code inside this method will be called once on initialization */
    public abstract void initialize();

    /** Writes something to the android output log (essentially the equivalent of {@code System.out.println}). */
    public void println(Object thingToPrint) {
        Log.d("[OP-MODE]", thingToPrint.toString());
    }

    /** Code inside this method will be called repeatedly once the driver hits start
     * This method runs after gamepads are queried, but before subsystems/actions are. */
    public void whileActive() {}

    /** Code inside this method will be called once when the driver hits start */
    public void whenStarted() {}

    /** Code inside this method will be called once when the driver hits stop */
    public void whenStopped() {}

    /** Calling this method will result in all future telemetry being redirected to FTC Dashboard
     *
     * @param keepDSTelemetry   if true, a copy of telemetry data will still be sent to the Driver Station
     * */
    public void enableDashboardTelemetry(boolean keepDSTelemetry) {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    /** This method will immediately stop Op-Mode execution.
     *
     * Make sure that you don't have things that are in the middle of
     * moving when you call this, as it will immediately cut all motor powers.
     * */
    public void stopOpMode() {
        externalStopRequested = true;
    }

    /** Toggles if this opMode should output it's loop time in telemetry
     *
     * @param trackLoopTimes    if true, loop times will be exported to telemetry (defaults to true)
     * */
    public void toggleLoopTimeTracking(boolean trackLoopTimes) {
        this.trackLoopTimes = trackLoopTimes;
    }

    /** This method will forcibly clear and refresh the cache of both LynxModules.
     *
     * Note that this functionality is handled by the ThunderOpMode class. Only call this if you know
     * what you are doing.
     */
    public void flushLynxModuleCache() {
        for (LynxModule module : lynxModules) module.clearBulkCache();
    }

    private List<LynxModule> lynxModules;

    private boolean trackLoopTimes = true;
    private boolean externalStopRequested = false;

    @Override
    public final void runOpMode() throws InterruptedException {
        scheduler.reset();

        initialize();

        // Replace this with photon stuff once it is stable
        lynxModules = hardwareMap.getAll(LynxModule.class);
        for (LynxModule module : lynxModules) module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);

        waitForStart();

        double lastLoopTime = Utils.getMsTime();
        whenStarted();

        while (isStarted() && !isStopRequested() && !externalStopRequested) {
            flushLynxModuleCache();

            driver.update();
            codriver.update();

            whileActive();

            scheduler.update();

            double loopTime = Utils.getMsTime() - lastLoopTime;
            if (trackLoopTimes) telemetry.addData("LT", loopTime);
            lastLoopTime += loopTime;

            telemetry.update();
        }

        whenStopped();
    }
}
