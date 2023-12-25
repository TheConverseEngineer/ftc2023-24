package com.thunder.opensim;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.thunder.opensimgui.SimulationLogicHandler;
import com.thunder.opensimgui.input.SimulationInput;

//TODO: add LinearOpModeSupport
public class Simulator implements SimulationLogicHandler {

    private enum OpModeState { STOPPED, INITIATED, RUNNING }

    private final Class<? extends OpMode> opMode;
    private final SimParameters parameters;

    private OpMode activeOpMode;
    private OpModeState currentState = OpModeState.STOPPED;

    public Simulator(Class<? extends OpMode> opMode, SimParameters parameters) {
        this.opMode = opMode;
        this.parameters = parameters;
    }


    @Override
    public SimulationInput update() {
        return null;
    }

    @Override
    public void init() {
        assert currentState == OpModeState.STOPPED: "Tried to initiate an OpMode that was already active";
        try {
            activeOpMode = opMode.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Exception occurred while trying to instantiate OpMode");
        }
        activeOpMode.hardwareMap = parameters.getHardwareMap();
        currentState = OpModeState.INITIATED;
        activeOpMode.init();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }
}
