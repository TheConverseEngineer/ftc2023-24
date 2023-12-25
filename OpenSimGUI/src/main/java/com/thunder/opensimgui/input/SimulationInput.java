package com.thunder.opensimgui.input;

import java.util.ArrayList;

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


    public static class SimulationInputBuilder {
        private final ArrayList<MotorInputEntry> motors = new ArrayList<>();
        private final ArrayList<ServoInputEntry> servos = new ArrayList<>();

        public SimulationInputBuilder addMotor(String name, double power, int encoderValue) {
            motors.add(new MotorInputEntry(name, power, encoderValue));
            return this;
        }

        public SimulationInputBuilder addServo(String name, double pos) {
            servos.add(new ServoInputEntry(name, pos));
            return this;
        }


        public SimulationInput build() {
            return new SimulationInput(
                    motors.toArray(new MotorInputEntry[0]),
                    servos.toArray(new ServoInputEntry[0])
            );
        }

    }
}
