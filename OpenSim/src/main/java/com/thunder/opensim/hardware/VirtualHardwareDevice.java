package com.thunder.opensim.hardware;

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

    protected void throwUnsupported(String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("OpenSim does not support method " + name + " at this time!");
    }

    protected void throwUnsupportedParam(String funcName, String paramName) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("OpenSim does not support using the method " + funcName + " with the parameter " + " at this time!");
    }
}
