package com.thunder.opensimgui.input;

/** This class serves as the go between between this module (java + gui) and the OpenSim module (android + ftc integration) */
public class SimulationInput {

    public final MotorInputEntry[] motors;
    public final ServoInputEntry[] servos;

    public SimulationInput(
            MotorInputEntry[] motorInputs,
            ServoInputEntry[] servoInputs
    ) {
        this.motors = motorInputs;
        this.servos = servoInputs;
    }
}
