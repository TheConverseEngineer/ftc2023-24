package com.thunder.opensimgui.input;

public class MotorInputEntry {

    public final String name;
    public final double power;
    public final int currentEncoderValue;

    public MotorInputEntry(String name, double power, int currentEncoderValue) {
        this.name = name;
        this.power = power;
        this.currentEncoderValue = currentEncoderValue;
    }
}
