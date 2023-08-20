import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.skecore.MessageReceiver;
import org.firstinspires.ftc.teamcode.skecore.VirtualHardwareMap;
import org.firstinspires.ftc.teamcode.skecore.testOpMode.DcMotorSimpleTest;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Arrays;

public class GenerateSimulator {

    /**
     *  This method starts the Android-Studio side of the simulator
     *
     *  Make sure the run this function in order for the Unity Simulation to work!
     */
    //@Test
    public void generateSimulator() {
        SocketClient link = new SocketClient();
        link.startConnection("127.0.0.1", 6666);
        float power = 0.25f;
        int intBits = Float.floatToIntBits(power);

        link.sendMessage(new byte[]{0, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, (byte) (intBits >> 24), (byte) (intBits >> 16), (byte) (intBits >> 8), (byte) (intBits)});
        link.stopConnection();
    }

    //@Test
    public void testSimulator() {
        Class<? extends LinearOpMode> opModeTemplate = DcMotorSimpleTest.class;

        SocketClient receiver = new SocketClient();
        receiver.startConnection("127.0.0.1", 49153);
        OpModeManager manager = new OpModeManager(opModeTemplate, receiver);

        System.out.println("Initializing");
        manager.init();

        sleep(3000);

        System.out.println("Starting");
        manager.start();

        sleep(10000);

        System.out.println("Stopping");
        manager.end();

        System.out.println("Done!");
        receiver.stopConnection();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class OpModeManager {
        private final LinearOpMode opMode;

        private final Field isStartedField, stopRequestedField;
        final Object opModeRunningNotifier;

        private Thread opModeThread = null;

        State currentState = State.UNINITIALIZED;

        private enum State { UNINITIALIZED, INITIALIZED, RUNNING, TERMINATED }


        public OpModeManager(
                Class<? extends LinearOpMode> template,
                MessageReceiver receiver
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
                throw new RuntimeException("[FTC-SIM] Could not find private class members 'isStarted' and 'stopRequested'." +
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
                        "[FTC-SIM] For some reason, the opMode's fields were not changed to public. " +
                                "This is an internal issue with the simulator and should never occur."
                );
            }

            opMode.hardwareMap = new VirtualHardwareMap(receiver);
            // TODO: gamepad support
        }

        public void notifyOpMode() {
            synchronized (opModeRunningNotifier) {
                opModeRunningNotifier.notifyAll();
            }
        }

        public void init() {
            assert currentState == State.UNINITIALIZED: "[FTC-SIM] OpMode was already initialized";
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

        public void start() {
            assert currentState != State.UNINITIALIZED: "[FTC-SIM] OpMode cannot be started without first being initialized";
            assert currentState == State.INITIALIZED: "[FTC-SIM] OpMode was already started";
            currentState = State.RUNNING;
            try {
                isStartedField.setBoolean(opMode, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "[FTC-SIM] For some reason, the opMode's fields were not changed to public. " +
                        "This is an internal issue with the simulator and should never occur."
                );
            }
            notifyOpMode();
        }

        /** Note that this method is blocking and will wait for the opmode to end. If you have an infinite loop
         * somewhere, this might not happen, in which case, you might have to manually terminate the test.
         */
        public void end() {
            assert currentState != State.TERMINATED: "[FTC-SIM] OpMode was already ended";
            assert currentState == State.RUNNING: "[FTC-SIM] OpMode must be started before it can be ended";
            currentState = State.TERMINATED;
            try {
                stopRequestedField.setBoolean(opMode, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "[FTC-SIM] For some reason, the opMode's fields were not changed to public. " +
                                "This is an internal issue with the simulator and should never occur."
                );
            }
            opModeThread.interrupt();
            try {
                opModeThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            notifyOpMode();
        }


        private static LinearOpMode tryToCreateOpMode(Class<? extends LinearOpMode> opModeTemplate) {
            LinearOpMode opMode = null;
            try {
                opMode = opModeTemplate.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("[FTC-SIM] OpMode Constructor missing!: " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("[FTC-SIM] OpMode Constructor not public!: " + e.getMessage());
            } catch (InstantiationException e) {
                throw new RuntimeException("[FTC-SIM] OpMode cannot be instantiated!: " + e.getMessage());
            } catch (InvocationTargetException e) {
                throw new RuntimeException("[FTC-SIM] Error constructing opmode!: " + e.getMessage());
            }
            return opMode;
        }
    }

    static class MockReceiver implements MessageReceiver {

        @Override
        public void receiveMessage(byte[] msg) {
            System.out.println("Recv: " + Arrays.toString(msg));
        }

        @Override
        public byte[] receiveMessageWithReply(byte[] msg) {
            return new byte[0];
        }

    }

    /* Everything past this point can (and probably should be) be ignored! */
    private static final String SOCKET_IP = "127.0.0.1";
    private static final int TO_UNITY_PORT = 6666;
    private static final int TO_AS_PORT = 6667;

    /** Handles all of the simulation logic */
    private static final class SocketClient implements MessageReceiver {
        private Socket client;
        private BufferedOutputStream output;
        private DataInputStream input;

        public void startConnection(String ip, int port) {
            try {
                client = new Socket(ip, port);
                output = new BufferedOutputStream(client.getOutputStream());
                input = new DataInputStream(client.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("[FTC-SIM] Error: " + e.getMessage());
            }
        }

        @Override
        public void receiveMessage(byte[] msg) {
            this.sendMessage(msg);
        }

        @Override
        public byte[] receiveMessageWithReply(byte[] msg) {
            this.sendMessage(msg);
            byte[] response = new byte[4];
            try {
                input.readFully(response);
            } catch (IOException e) {
                System.out.println("[FTC-SIM] Error reading input: " + e.getMessage());
            }
            return response;
        }

        private void sendMessage(byte[] msg) {
            System.out.println("Sending " + Arrays.toString(msg));
            try {
                output.write(msg);
                output.flush();
            } catch (IOException e) {
                throw new RuntimeException("[FTC-SIM] Error when sending message!: " + e.getMessage());
            }
        }

        public void stopConnection() {
            try {
                input.close();
                output.close();
                client.close();
            } catch (IOException e) {
                throw new RuntimeException("[FTC-SIM] Error closing socket!");
            }
        }

    }





}
