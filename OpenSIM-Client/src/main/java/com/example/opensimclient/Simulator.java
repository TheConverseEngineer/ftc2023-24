package com.example.opensimclient;

import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

// This should probably be a Singleton, but I intentionally made it a static class
//      to simplify the code required to begin a simulation

/** Static class that serves as the entry point to the OpenSIM FTC Simulator
 * <br>
 * @see Simulator#runSimulation
 * */
@SuppressWarnings({"unused", "unchecked"})
public class Simulator {

    private static boolean simActive = false;

    /** Run this method inside a local test to enable the simulator.
     * <br>
     * In order to use this method, you must pass the class you wish to run. For example, if you would like to
     * run SampleOpMode, then pass {@code SampleOpMode.class}.
     *
     * @param opModeTemplate    Pass the opmode you wish to run here. Passing null will cause the simulator to not run.
     * @param duration          How long (in seconds) the simulation should run for
     *
     * */
    public static void runSimulation(@Nullable Class<? extends OpMode> opModeTemplate, double duration) {
        if (opModeTemplate == null) return;
        simActive = true;
        System.out.println("Running simulator on " + opModeTemplate.getName());

        SocketClient receiver = new SocketClient("127.0.0.1", 49153);
        OpModeManager manager;
        if (LinearOpMode.class.isAssignableFrom(opModeTemplate))
            manager = new LinearOpModeManager((Class<? extends LinearOpMode>)opModeTemplate, receiver);
        else manager = null; // TODO

        System.out.println("Initializing");
        manager.init();

        sleep(5000);

        System.out.println("Starting");
        manager.start();

        sleep((long)(duration*1000));

        System.out.println("Stopping");
        manager.end();

        System.out.println("Done!");
        receiver.stopConnection();
    }

    /** Returns true if the simulator is currently running, and false otherwise. */
    public static boolean isSimActive() { return simActive; }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
