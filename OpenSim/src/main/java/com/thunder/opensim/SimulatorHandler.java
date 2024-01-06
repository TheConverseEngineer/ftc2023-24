package com.thunder.opensim;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.thunder.opensim.hardware.VirtualHardwareBridge;
import com.thunder.opensimgui.SimulationApplication;
import com.thunder.opensimgui.SimulationLogicHandler;
import com.thunder.opensimgui.input.SimulationInput;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.reflect.Field;

//TODO: add LinearOpModeSupport
public class SimulatorHandler implements SimulationLogicHandler {

    private enum OpModeState { STOPPED, INITIATED, RUNNING }

    private final Class<? extends LinearOpMode> opMode;
    private final SimParameters parameters;

    private OpModeState currentState = OpModeState.STOPPED;
    private Thread opModeThread;
    private Field isStarted, isStopped, notifier;
    private LinearOpMode activeOpMode;

    public SimulatorHandler(Class<? extends LinearOpMode> opMode, SimParameters parameters) {
        this.opMode = opMode;
        this.parameters = parameters;

        Class<? super LinearOpMode> internals = OpMode.class.getSuperclass();
        try {
            isStarted = internals.getDeclaredField("isStarted");
            isStarted.setAccessible(true);

            isStopped = internals.getDeclaredField("stopRequested");
            isStopped.setAccessible(true);

            notifier = LinearOpMode.class.getDeclaredField("runningNotifier");
            notifier.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Error accessing the isStarted/isStopped fields in OpModeInternal");
        }
    }

    private void notifyOpMode() {

        try {
            Object notification = notifier.get(activeOpMode);
            assert notification != null: "Unable to locate notifier object in LinearOpMode";
            synchronized (notification) {
                notification.notifyAll();
            }
        } catch (IllegalAccessException | NullPointerException e) {
            throw new RuntimeException("Exception occurred while trying to notify LinearOpMode of state changes");
        }
    }

    @Override
    public SimulationInput update(double dTime) {
        parameters.updateModels(dTime);
        return VirtualHardwareBridge.getInstance().generateGUIInput();
    }

    @Override
    public void init() {
        System.out.println("Initializing");
        assert currentState == OpModeState.STOPPED: "Tried to initiate an OpMode that was already active";
        try {
            activeOpMode = opMode.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Exception occurred while trying to instantiate OpMode");
        }
        VirtualHardwareBridge.getInstance().reset();
        activeOpMode.hardwareMap = parameters.getHardwareMap();
        currentState = OpModeState.INITIATED;
        opModeThread = new Thread(() -> {
            try {
                activeOpMode.runOpMode();
            } catch (InterruptedException ignore) { } // Update loop handles detection of thread closing
        });
        opModeThread.start();
    }


    @Override
    public void start() {
        System.out.println("Starting");
        assert currentState == OpModeState.INITIATED: "Tried to start opmode without initializing";
        currentState = OpModeState.RUNNING;
        try {
            isStarted.set(activeOpMode, true);
            notifyOpMode();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Exception occurred while trying to modify start field of OpModeInternal");
        }
    }

    @Override
    public void stop() {
        System.out.println("Stopping");
        if (currentState == OpModeState.INITIATED) {
            opModeThread = null;
            currentState = OpModeState.STOPPED;
        } else if (currentState == OpModeState.RUNNING) {
            try {
                isStopped.set(activeOpMode, true);
                notifyOpMode();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Exception occurred while trying to modify start field of OpModeInternal");
            }
            currentState = OpModeState.STOPPED;
        } else throw new AssertionError("Tried to stop an opmode that was already stopped");
    }
}
