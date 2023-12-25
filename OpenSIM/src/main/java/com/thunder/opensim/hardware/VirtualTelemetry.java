package com.thunder.opensim.hardware;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class VirtualTelemetry implements Telemetry {

    private boolean shouldAutoClear = true;
    private int transmissionInterval = 3; // TODO: figure out the default value for this


    // Auto-Clear and Transmission Intervals
    @Override public boolean isAutoClear() { return shouldAutoClear; }
    @Override public void setAutoClear(boolean autoClear) { shouldAutoClear = autoClear; }
    @Override public int getMsTransmissionInterval() { return transmissionInterval; }
    @Override public void setMsTransmissionInterval(int msTransmissionInterval) { this.transmissionInterval = msTransmissionInterval; }
}
