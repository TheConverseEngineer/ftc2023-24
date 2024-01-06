package com.thunder.opensim.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class VirtualDcMotorEx extends VirtualDcMotor implements DcMotorEx {

    boolean isEnabled = true;
    double currentAlert = 9.3;

    public VirtualDcMotorEx(String deviceName) {
        super(deviceName);
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        super.resetDeviceConfigurationForOpMode();
        this.isEnabled = true;
        this.currentAlert = 9.3;
    }

    @Override public void setMotorEnable() { isEnabled = true; }
    @Override public boolean isMotorEnabled() { return isEnabled; }

    @Override
    public void setMotorDisable() {
        isEnabled = false;
        setPower(0);
    }

    @Override public void setVelocity(double angularRate) { throwUnsupported("DcMotorEx.setVelocity"); }
    @Override public void setVelocity(double angularRate, AngleUnit unit) { throwUnsupported("DcMotorEx.setVelocity"); }

    @Override
    public double getVelocity() {
        return hardwareBridge.getMotorVelocity(getDeviceName());
    }

    @Override
    public double getVelocity(AngleUnit unit) {
        return hardwareBridge.getMotorVelocity(getDeviceName(), unit);
    }

    @Override public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) { throwUnsupported("DcMotorEx.setPIDCoefficients"); }
    @Override public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) { throwUnsupported("DcMotorEx.setPIDFCoefficients"); }
    @Override public void setVelocityPIDFCoefficients(double p, double i, double d, double f) { throwUnsupported("DcMotorEx.setVelocityPIDFCoefficients"); }
    @Override public void setPositionPIDFCoefficients(double p) { throwUnsupported("DcMotorEx.setPositionPIDFCoefficients"); }

    @Override public PIDCoefficients getPIDCoefficients(RunMode mode) { throwUnsupported("DcMotorEx.getPIDCoefficients"); return null; }
    @Override public PIDFCoefficients getPIDFCoefficients(RunMode mode) { throwUnsupported("DcMotorEx.setPIDFCoefficients"); return null; }

    @Override public void setTargetPositionTolerance(int tolerance) { throwUnsupported("DcMotorEx.setTargetPositionTolerance"); }
    @Override public int getTargetPositionTolerance() { throwUnsupported("DcMotorEx.getTargetPositionTolerance"); return 0; }

    @Override
    public double getCurrent(CurrentUnit unit) {
        if (unit == CurrentUnit.AMPS) return hardwareBridge.getCurrentDraw(getDeviceName());
        else return hardwareBridge.getCurrentDraw(getDeviceName()) * 1000;
    }

    @Override
    public double getCurrentAlert(CurrentUnit unit) {
        return this.currentAlert;
    }

    @Override
    public void setCurrentAlert(double current, CurrentUnit unit) {
        if (unit == CurrentUnit.AMPS) this.currentAlert = current;
        else this.currentAlert = current/1000;
    }

    @Override
    public boolean isOverCurrent() {
        return getCurrent(CurrentUnit.AMPS) > this.currentAlert;
    }
}
