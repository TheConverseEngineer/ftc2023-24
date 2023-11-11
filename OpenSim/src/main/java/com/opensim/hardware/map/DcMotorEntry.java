package com.opensim.hardware.map;

import com.opensim.hardware.devices.VirtualDcMotor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DcMotorEntry extends MapEntry{

    private final double cpr, rpm;
    private final String name;

    public DcMotorEntry(double cpr, double rpm, String name) {
        this.cpr = cpr;
        this.rpm = rpm;
        this.name = name;
    }

    @Override
    void addToMap(HardwareMap map) {
        DcMotor motor = new VirtualDcMotor(name);

        map.put(name, motor);
        map.dcMotor.put(name, motor);
    }
}
