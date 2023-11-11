package com.opensim.hardware.devices;

import com.opensim.physics.SystemBase;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/** TODO:
 *       - Make setMotorEnable(), etc. do something?
 *       - Implement the built-in PID / velocity controller
 *       - Anyone know the default current alert value?
 */
public class VirtualDcMotorEx extends VirtualDcMotor implements DcMotorEx {

    private double currentAlert = 9.2; // in amps
    private boolean enabled = false;
    private SystemBase attachedSystem = null;

    public void attachSystem(SystemBase system) {
        this.attachedSystem = system;
    }

    public VirtualDcMotorEx(String name) {
        super(name);
    }

    @Override public void setMotorEnable() { enabled = true; }
    @Override public void setMotorDisable() { enabled = false; }
    @Override public boolean isMotorEnabled() { return enabled; }

    @Override public void setVelocity(double angularRate) { throw new UnsupportedOperationException("OpenSim does not support DcMotorEx.setVelocity at this time."); }
    @Override public void setVelocity(double angularRate, AngleUnit unit) { throw new UnsupportedOperationException("OpenSim does not support DcMotorEx.setVelocity at this time."); }

    @Override
    public double getVelocity() {
        synchronized (hardwareLock) {
            if (this.encoder == null) return 0.0;
            else return this.encoder.getEncoderVelocity();
        }
    }

    @Override
    public double getVelocity(AngleUnit unit) {
        synchronized (hardwareLock) {
            if (this.encoder == null) return 0.0;
            else {
                double rps = this.encoder.getEncoderVelocity() / this.encoder.getEncoderCPR();
                switch (unit) {
                    case RADIANS: return rps * 2 * Math.PI;
                    case DEGREES: return rps * 360;
                    default: return 0.0;
                }
            }
        }
    }

    @Override public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) { throwUnsupportedRunModeException("DcMotorEx.setPIDCoefficients"); }
    @Override public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) throws UnsupportedOperationException { throwUnsupportedRunModeException("DcMotorEx.setPIDFCoefficients"); }
    @Override public void setVelocityPIDFCoefficients(double p, double i, double d, double f) { throwUnsupportedRunModeException("DcMotorEx.setVelocityPIDFCoefficients"); }
    @Override public void setPositionPIDFCoefficients(double p) { throwUnsupportedRunModeException("DcMotorEx.setPositionPIDFCoefficients"); }

    @Override
    public PIDCoefficients getPIDCoefficients(RunMode mode) {
        throwUnsupportedRunModeException("DcMotorEx.getPIDCoefficients");
        return null;
    }

    @Override
    public PIDFCoefficients getPIDFCoefficients(RunMode mode) {
        throwUnsupportedRunModeException("DcMotorEx.getPIDFCoefficients");
        return null;
    }

    @Override
    public void setTargetPositionTolerance(int tolerance) {
        throwUnsupportedRunModeException("DcMotorEx.setTargetPositionTolerance");
    }

    @Override
    public int getTargetPositionTolerance() {
        throwUnsupportedRunModeException("DcMotorEx.getTargetPositionTolerance");
        return 0;
    }

    @Override
    public double getCurrent(CurrentUnit unit) {
        synchronized (hardwareLock) {
            if (this.attachedSystem == null) return 2.0; // generic answer
            else {
                switch(unit){
                    case AMPS: return this.attachedSystem.getCurrentDrawPerMotor();
                    case MILLIAMPS: return 1000 * this.attachedSystem.getCurrentDrawPerMotor();
                    default: return 0.0;
                }
            }
        }
    }

    @Override
    public double getCurrentAlert(CurrentUnit unit) {
        switch (unit) {
            case AMPS: return this.currentAlert;
            case MILLIAMPS: return this.currentAlert * 1000;
            default: return 0.0;
        }
    }

    @Override
    public void setCurrentAlert(double current, CurrentUnit unit) {
        this.currentAlert = current / (unit==CurrentUnit.MILLIAMPS?1000:1);
    }

    @Override
    public boolean isOverCurrent() {
        return getCurrent(CurrentUnit.AMPS) > this.currentAlert;
    }
}
