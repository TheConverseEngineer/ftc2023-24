package com.opensim.hardware.devices;

import com.qualcomm.robotcore.hardware.HardwareDevice;

public abstract class VirtualHardwareDevice implements HardwareDevice {

    private final String deviceName;

    public VirtualHardwareDevice(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public Manufacturer getManufacturer() { return Manufacturer.Other; }

    @Override
    public String getDeviceName() { return deviceName; }

    @Override
    public String getConnectionInfo() { return "Connected Virtually"; }

    @Override
    public int getVersion() { return 0; }

    @Override
    public void close() { }
}
