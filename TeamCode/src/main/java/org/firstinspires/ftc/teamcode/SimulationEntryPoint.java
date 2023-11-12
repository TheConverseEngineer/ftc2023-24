package org.firstinspires.ftc.teamcode;

import com.thunder.opensimgui.SimGUI;
import com.thunder.opensimgui.SimulationLogicHandler;
import com.thunder.opensimgui.input.MotorInputEntry;
import com.thunder.opensimgui.input.ServoInputEntry;
import com.thunder.opensimgui.input.SimulationInput;

public class SimulationEntryPoint implements SimulationLogicHandler {

    private static final SimulationInput input = new SimulationInput(
            new MotorInputEntry[]{
                new MotorInputEntry("LeftFrontDrive", 0.25, 10),
                new MotorInputEntry("RightFrontDrive", -.75, 100),
                new MotorInputEntry("RightFrontDrive", 0, 5430)
            },
            new ServoInputEntry[]{
                new ServoInputEntry("RightClawServo", 0.25),
                new ServoInputEntry("LeftClawServo", 0.75),
                new ServoInputEntry("PlaneServo", 1)
            }
    );

    private static final SimulationLogicHandler instance = new SimulationEntryPoint();


    /*
    */
    public static void main(String[] args) {
        SimGUI.launchSimGUI(instance);
    }
    /*
     */

    @Override
    public SimulationInput update() {
        return input;
    }

}
