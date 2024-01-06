package com.thunder.opensim;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.thunder.opensimgui.SimulationApplication;

public class SimulationRunner {
    public static void runSimulation(Class<? extends LinearOpMode> opMode, SimParameters parameters) {
        SimulatorHandler sim = new SimulatorHandler(opMode, parameters);

        SimulationApplication app = new SimulationApplication(sim);
    }
}
