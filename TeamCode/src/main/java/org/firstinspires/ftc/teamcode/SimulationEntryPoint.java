package org.firstinspires.ftc.teamcode;

import com.opensim.Simulation;
import com.opensim.config.SimConfig;

public class SimulationEntryPoint {

    private static final SimConfig config = SimConfig.builder()
            .addFreeMotorSystem(5.2)
                .addMotor("motor1")
                .buildSystem()
            .buildConfig();


    /*
    */
    public static void main(String[] args) {
        Simulation.getInstance().beginSimulation(config);
    }
    /*
     */
}
