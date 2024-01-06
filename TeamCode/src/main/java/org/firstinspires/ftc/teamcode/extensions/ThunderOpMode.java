package org.firstinspires.ftc.teamcode.extensions;


import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.hardware.lynx.LynxModule;

import java.util.ArrayList;
import java.util.List;

/** Extension of FTCLib's {@link CommandOpMode} that adds {@link GamepadEx} and bulk read support, among other things */
public abstract class ThunderOpMode extends CommandOpMode {

    // For some reason, the SDK does not define gamepads at creation-time, so the gamepad reference will be set to
    // null for the time being and will be changed later.
    /** Wrapper for gamepad1 */
    protected final GamepadEx driver = new GamepadEx(null);
    /** Wrapper for gamepad2 */
    protected final GamepadEx codriver = new GamepadEx(null);

    /** Stores the lynx modules*/
    List<LynxModule> lynxModules = new ArrayList<>();

    /** This method is called on OpMode initialization */
    @Override
    public final void initialize() {
        // Enable bulk reads
        lynxModules = hardwareMap.getAll(LynxModule.class);
        lynxModules.forEach(module -> module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        // Link gamepads
        driver.gamepad = gamepad1;
        codriver.gamepad = gamepad2;

        // Create subsystems and run other init actions
        createSubsystems();
        onInit();
    }

    /** This method is called at the start of each loop */
    @Override
    public void run() {
        lynxModules.forEach(LynxModule::clearBulkCache);
        super.run();
    }

    /** This method is called when the opmode is initialized.
     * Subsystems should be created here */
    public abstract void createSubsystems();

    /** This method is called after {@link ThunderOpMode#createSubsystems()} and can be used
     * to declare other init actions */
    public void onInit() { }

    /** Calls {@link com.arcrobotics.ftclib.command.CommandScheduler#schedule(boolean, Command...)}*/
    public void schedule(boolean canInterrupt, Command... commands) {
        CommandScheduler.getInstance().schedule(canInterrupt, commands);
    }
}
