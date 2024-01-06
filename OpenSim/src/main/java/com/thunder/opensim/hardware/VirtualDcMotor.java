package com.thunder.opensim.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

public class VirtualDcMotor extends VirtualDcMotorSimple implements DcMotor {

    private ZeroPowerBehavior zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
    private int encoderOffset = 0;

    public VirtualDcMotor(String deviceName) {
        super(deviceName);
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        super.resetDeviceConfigurationForOpMode();
        this.encoderOffset = 0;
        this.zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
    }

    @Override public MotorConfigurationType getMotorType() { throwUnsupported("getMotorType"); return null; }
    @Override public void setMotorType(MotorConfigurationType motorType) { throwUnsupported("setMotorType"); }

    @Override public DcMotorController getController() { throwUnsupported("DcMotor.getController"); return null; }
    @Override public int getPortNumber() { throwUnsupported("DcMotor.getPortNumber"); return 0; }

    @Override public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) { this.zeroPowerBehavior = zeroPowerBehavior; }
    @Override public ZeroPowerBehavior getZeroPowerBehavior() { return this.zeroPowerBehavior; }

    @Override public void setPowerFloat() { throwUnsupported("DcMotor.setPowerFloat"); }
    @Override public boolean getPowerFloat() { throwUnsupported("DcMotor.setPowerFloat"); return false; }

    @Override public void setTargetPosition(int position) { throwUnsupported("DcMotor.setTargetPosition"); }
    @Override public int getTargetPosition() { throwUnsupported("DcMotor.getTargetPosition"); return 0; }
    @Override public boolean isBusy() { throwUnsupported("DcMotor.isBusy"); return false; }

    @Override
    public int getCurrentPosition() {
        return hardwareBridge.getMotorPos(getDeviceName()) + encoderOffset;
    }

    @Override
    public void setMode(RunMode mode) {
        if (mode == RunMode.STOP_AND_RESET_ENCODER) this.encoderOffset = -hardwareBridge.getMotorPos(getDeviceName());
        else if (mode != RunMode.RUN_WITHOUT_ENCODER) {
            throwUnsupportedParam("DcMotor.setMode", "RunMode."+mode.name());
        }
    }

    @Override
    public RunMode getMode() {
        return RunMode.RUN_WITHOUT_ENCODER; // This is the only supported mode at the moment
    }
}
