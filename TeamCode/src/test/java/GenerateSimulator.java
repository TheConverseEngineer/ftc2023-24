import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.skecore.MessageReceiver;
import org.firstinspires.ftc.teamcode.skecore.VirtualHardwareMap;
import org.firstinspires.ftc.teamcode.skecore.testOpMode.DcMotorSimpleTest;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Arrays;


public class GenerateSimulator {

    static final String PATH_FROM_SRC_TO_TEAMCODE ="src\\main\\java\\org\\firstinspires\\ftc\\teamcode";
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

    @Test
    public void testSimulator() {
        Class<? extends LinearOpMode> opModeTemplate = DcMotorSimpleTest.class;
        final LinearOpMode opMode = tryToCreateOpMode(opModeTemplate);

        if (opMode == null) {
            System.out.println("Failed!");
            return;
        }
        MockReceiver receiver = new MockReceiver();
        opMode.hardwareMap = new VirtualHardwareMap(receiver);

        Thread opModeThread = new Thread(()-> {
            try {
                opMode.runOpMode();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("Initializing opMode");
        opModeThread.start();

        sleep(2000);

        System.out.println("Starting opMode");
        opMode.start();

        sleep(10000);

        System.out.println("Ending opmode");
        opModeThread.interrupt();
        try {
            opModeThread.join();
        } catch (InterruptedException e) {
            System.out.println("Experienced an interruption: " + e);
            Thread.currentThread().interrupt();
        }
        System.out.println("complete");

    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static LinearOpMode tryToCreateOpMode(Class<? extends LinearOpMode> opModeTemplate) {
        LinearOpMode opMode = null;
        try {
            opMode = opModeTemplate.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            System.out.println("[FTC-SIM] OpMode Constructor missing!: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println("[FTC-SIM] OpMode Constructor not public!: " + e.getMessage());
        } catch (InstantiationException e) {
            System.out.println("[FTC-SIM] OpMode cannot be instantiated!: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.out.println("[FTC-SIM] Error constructing opmode!: " + e.getMessage());
        }
        return opMode;
    }

    class MockReceiver implements MessageReceiver {

        @Override
        public void receiveMessage(byte[] msg) {
            System.out.println("Recv: " + Arrays.toString(msg));
        }
    }

    class SimulationManager {
        Class<LinearOpMode> opModeReference;
        LinearOpMode opMode;

        public SimulationManager(Class<LinearOpMode> opMode) {
            this.opModeReference = opMode;
        }

        public void startOpMode() {
            try {
                Constructor<LinearOpMode> constructor = opModeReference.getConstructor();
                opMode = constructor.newInstance();
            } catch (NoSuchMethodException e) {
                System.out.println("[FTC-SIM] OpMode Constructor missing!: " + e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println("[FTC-SIM] OpMode Constructor not public!: " + e.getMessage());
            } catch (InstantiationException e) {
                System.out.println("[FTC-SIM] OpMode cannot be instantiated!: " + e.getMessage());
            } catch (InvocationTargetException e) {
                System.out.println("[FTC-SIM] Error constructing opmode!: " + e.getMessage());
            }


        }
    }

    /* Everything past this point can (and probably should be) be ignored! */
    private static final String SOCKET_IP = "127.0.0.1";
    private static final int TO_UNITY_PORT = 6666;
    private static final int TO_AS_PORT = 6667;

    /** Handles all of the simulation logic */
    private static final class SocketClient {
        private Socket client;
        private BufferedOutputStream output;
        private BufferedReader input;

        public void startConnection(String ip, int port) {
            try {
                client = new Socket(ip, port);
                output = new BufferedOutputStream(client.getOutputStream());
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        public void sendMessage(byte[] msg) {
            try {
                output.write(msg);
                output.flush();
            } catch (IOException e) {
                System.out.println("[FTC-SIM] Error when sending message!: " + e.getMessage());
            }
        }

        public void stopConnection() {
            try {
                input.close();
                output.close();
                client.close();
            } catch (IOException e) {
                System.out.println("[FTC-SIM] Error closing socket!");
            }
        }
    }





}
