package com.thunder.opensim;

import com.qualcomm.robotcore.hardware.HardwareDevice;

public class VirtualHardwareDevice implements HardwareDevice {

    private final String deviceName;

    public VirtualHardwareDevice(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override public Manufacturer getManufacturer() { return Manufacturer.Other; }
    @Override public String getDeviceName() { return this.deviceName; }
    @Override public String getConnectionInfo() { return "Connected via OpenSIM";}
    @Override public int getVersion() { return 1; }

    @Override public void resetDeviceConfigurationForOpMode() { }
    @Override public void close() { }
}
