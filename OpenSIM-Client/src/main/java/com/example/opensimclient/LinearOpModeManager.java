package com.example.opensimclient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.example.opensimclient.hardware.VirtualHardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/** Creates a LinearOpMode and runs it without the FTC SDK's backend */
public class LinearOpModeManager implements OpModeManager {
    private final LinearOpMode opMode;

    private final Field isStartedField, stopRequestedField;
    final Object opModeRunningNotifier;

    private Thread opModeThread = null;

    State currentState = State.UNINITIALIZED;

    private enum State { UNINITIALIZED, INITIALIZED, RUNNING, TERMINATED }


    public LinearOpModeManager(
            Class<? extends LinearOpMode> template,
            SocketClient receiver
    ) {
        opMode = tryToCreateOpMode(template);

        // Now time for some questionable programming decisions, featuring the Reflections library!
        // Step 1: go up the sub-class hierarchy until we are at OpMode (first class to not extend LinearOpMode)
        Class<?> rawOpModeClass = opMode.getClass();
        Class<?> rawLinearOpModeClass = opMode.getClass();
        while (LinearOpMode.class.isAssignableFrom(rawOpModeClass)) {
            rawLinearOpModeClass = rawOpModeClass;
            rawOpModeClass = rawOpModeClass.getSuperclass();
        }

        // Now extract the fields
        Field runningNotifierField;
        try {
            isStartedField = rawOpModeClass.getSuperclass().getDeclaredField("isStarted");
            stopRequestedField = rawOpModeClass.getSuperclass().getDeclaredField("stopRequested");
            runningNotifierField = rawLinearOpModeClass.getDeclaredField("runningNotifier");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not find private class members 'isStarted' and 'stopRequested'." +
                    "This probably means that FIRST has edited the OpModeInternals class");
        }

        // Lastly, change these fields to public
        isStartedField.setAccessible(true);
        stopRequestedField.setAccessible(true);
        runningNotifierField.setAccessible(true);

        // For the running notifier field, all we care about is the actual object
        try {
            opModeRunningNotifier = runningNotifierField.get(opMode);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "For some reason, the opMode's fields were not changed to public. " +
                            "This is an internal issue with the simulator and should never occur."
            );
        }

        opMode.hardwareMap = new VirtualHardwareMap(receiver);
        // TODO: gamepad and telemetry support
    }

    /** Informs any currently-blocking parts of the LinearOpMode class to re-check their current status */
    public void notifyOpMode() {
        synchronized (opModeRunningNotifier) {
            opModeRunningNotifier.notifyAll();
        }
    }

    @Override
    public void init() {
        assert currentState == State.UNINITIALIZED: "OpMode was already initialized";
        currentState = State.INITIALIZED;
        opModeThread = new Thread(()-> {
            try {
                opMode.runOpMode();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        opModeThread.start();
        notifyOpMode();
    }

    @Override
    public void start() {
        assert currentState != State.UNINITIALIZED: "OpMode cannot be started without first being initialized";
        assert currentState == State.INITIALIZED: "OpMode was already started";
        currentState = State.RUNNING;
        try {
            isStartedField.setBoolean(opMode, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "For some reason, the opMode's fields were not changed to public. " +
                            "This is an internal issue with the simulator and should never occur."
            );
        }
        notifyOpMode();
    }

    @Override
    public void end() {
        assert currentState != State.TERMINATED: "OpMode was already ended";
        assert currentState == State.RUNNING: "OpMode must be started before it can be ended";
        currentState = State.TERMINATED;
        try {
            stopRequestedField.setBoolean(opMode, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "For some reason, the opMode's fields were not changed to public. " +
                            "This is an internal issue with the simulator and should never occur."
            );
        }
        opModeThread.interrupt();
        try {
            opModeThread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("OpMode was stuck in stop. Please check for infinite/uninterruptible loops");
        }
        notifyOpMode();
    }


    private static LinearOpMode tryToCreateOpMode(Class<? extends LinearOpMode> opModeTemplate) {
        LinearOpMode opMode;
        try {
            opMode = opModeTemplate.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("OpMode Constructor missing!: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("OpMode Constructor not public!: " + e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException("OpMode cannot be instantiated!: " + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error constructing opmode!: " + e.getMessage());
        }
        return opMode;
    }
}
