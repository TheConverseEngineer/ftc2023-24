package com.opensim.hardware.devices;

import com.opensim.hardware.VirtualEncoder;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

public class VirtualDcMotor extends VirtualDcMotorSimple implements DcMotor {

    private MotorConfigurationType motorType = MotorConfigurationType.getUnspecifiedMotorType();
    private ZeroPowerBehavior zeroPowerBehavior = ZeroPowerBehavior.BRAKE;

    protected VirtualEncoder encoder = null;

    /** Assigns a virtual encoder to this motor */
    public void linkVirtualEncoder(VirtualEncoder encoder) {
        this.encoder = encoder;
    }

    public VirtualDcMotor(String name) {
        super(name);
    }

    @Override public MotorConfigurationType getMotorType() { return motorType; }
    @Override public void setMotorType(MotorConfigurationType motorType) { this.motorType = motorType; }

    @Override public DcMotorController getController() { return null; }
    @Override public int getPortNumber() { return 0; }

    @Override public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        synchronized (hardwareLock) {
            this.zeroPowerBehavior = zeroPowerBehavior;
        }
    }

    @Override public ZeroPowerBehavior getZeroPowerBehavior() {
        synchronized (hardwareLock) {
            return this.zeroPowerBehavior;
        }
    }

    @Override public void setPowerFloat() { throw new UnsupportedOperationException("DcMotor.setPowerFloat is deprecated and not supported by OpenSim."); }
    @Override public boolean getPowerFloat() { throw new UnsupportedOperationException("DcMotor.getPowerFloat is deprecated and not supported by OpenSim."); }

    @Override public void setTargetPosition(int position) { throwUnsupportedRunModeException("DcMotor.setTargetPosition"); }
    @Override public int getTargetPosition() { throwUnsupportedRunModeException("DcMotor.getTargetPosition"); return 0; }
    @Override public boolean isBusy() { throwUnsupportedRunModeException("DcMotor.isBusy"); return false; }

    @Override
    public int getCurrentPosition() {
        synchronized (hardwareLock) {
            if (this.encoder == null) return 0;
            else return this.encoder.getEncoderCounts();
        }
    }

    @Override
    public void setMode(RunMode mode) {
        synchronized (hardwareLock) {
            if (mode == RunMode.STOP_AND_RESET_ENCODER) {
                if (this.encoder != null)
                    this.encoder.resetEncoder();
            } else if (mode != RunMode.RUN_WITHOUT_ENCODER) {
                throw new UnsupportedOperationException("At this time, OpenSim only supports the RUN_WITHOUT_ENCODER and STOP_AND_RESET_ENCODER run modes.");
            }
        }
    }

    @Override
    public RunMode getMode() {
        return RunMode.RUN_WITHOUT_ENCODER;
    }

    protected void throwUnsupportedRunModeException(String funcName) {
        throw new UnsupportedOperationException(funcName + " is not supported by OpenSim at this time. Currently, the only supported " +
                "DcMotor run modes are RUN_WITHOUT_ENCODER and STOP_AND_RESET_ENCODER.");
    }
}
