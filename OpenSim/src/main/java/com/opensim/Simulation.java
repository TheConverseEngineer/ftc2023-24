package com.opensim;


import static imgui.app.Application.launch;

import com.opensim.config.SimConfig;
import com.opensim.hardware.map.HardwareMapBuilder;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/** Singleton class that is responsible for managing the simulation */
public class Simulation {

    private static final Simulation instance = new Simulation();

    public static Simulation getInstance() {
        return instance;
    }

    private Simulation() { }

    public void beginSimulation(SimConfig configuration) {

        launch(new SimulationApplication());
    }

    public static HardwareMapBuilder getHardwareBuilder() {
        return new HardwareMapBuilder();
    }
}