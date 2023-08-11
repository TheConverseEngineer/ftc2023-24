package org.firstinspires.ftc.teamcode.skecore.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class VirtualDcMotorEx implements DcMotorEx {

    private double currentPower = 0;
    private static final double powerTolerance = 0.10;
    private static final double stallPower = 0.05;
    private double currentPowerOffset = 1 + powerTolerance*(Math.random()*2 - 1);
    private Direction currentDirection = Direction.FORWARD;
    private ZeroPowerBehavior currentZeroPowerBehavior = ZeroPowerBehavior.BRAKE;
    private MotorConfigurationType motorConfigurationType = MotorConfigurationType.getUnspecifiedMotorType();

    private final double ticksPerRotation;
    private final double rpm;

    private double lastCachedPosition = 0.0;
    private long lastEpoch;

    public VirtualDcMotorEx(double ticksPerRotation, double rpm) {
        this.ticksPerRotation = ticksPerRotation;
        this.rpm = rpm;
        lastEpoch = System.nanoTime();
    }

    public VirtualDcMotorEx(DcMotorTypes type) {
        this.ticksPerRotation = type.ticksPerRotation;
        this.rpm = type.rpm;
        lastEpoch = System.nanoTime();
    }

    private void updateCachedPosition() {
        long newEpoch = System.nanoTime();
        lastCachedPosition += (newEpoch-lastEpoch)*getVelocity()/1000000000;
        lastEpoch = newEpoch;
    }

    @Override
    public int getCurrentPosition() {
        updateCachedPosition();
        return (int)Math.round(lastCachedPosition);
    }

    @Override public void setMotorEnable() { }
    @Override public void setMotorDisable() { currentPower = 0; }
    @Override public boolean isMotorEnabled() { return currentPower != 0; }

    @Override public void setVelocity(double angularRate) { throwUnsupportedRunToPointError("setVelocity"); }
    @Override public void setVelocity(double angularRate, AngleUnit unit) { throwUnsupportedRunToPointError("setVelocity"); }

    @Override
    public double getVelocity() {
        return currentPower*currentPowerOffset*rpm*ticksPerRotation/60;
    }

    @Override
    public double getVelocity(AngleUnit unit) {
        if (unit == AngleUnit.DEGREES) return currentPower*currentPowerOffset*rpm*6;
        else return currentPower*currentPowerOffset*rpm*Math.PI/30;
    }

    // DcMotorEx methods
    @Override public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) { throwUnsupportedRunToPointError("setPIDCoefficients"); }
    @Override public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) { throwUnsupportedRunToPointError("setPIDFCoefficients"); }
    @Override public void setVelocityPIDFCoefficients(double p, double i, double d, double f) { throwUnsupportedRunToPointError("setVelocityPIDFCoefficients"); }
    @Override public void setPositionPIDFCoefficients(double p) { throwUnsupportedRunToPointError("setPIDFCoefficients"); }
    @Override public PIDCoefficients getPIDCoefficients(RunMode mode) { throwUnsupportedRunToPointError("getPIDCoefficients"); return null; }
    @Override public PIDFCoefficients getPIDFCoefficients(RunMode mode) { throwUnsupportedRunToPointError("getPIDFCoefficients"); return null; }
    @Override public void setTargetPositionTolerance(int tolerance) { throwUnsupportedRunToPointError("setTargetPositionTolerance"); }
    @Override public int getTargetPositionTolerance() { throwUnsupportedRunToPointError("getTargetPositionTolerance"); return 0; }
    @Override public double getCurrent(CurrentUnit unit) { return Math.abs(currentPower)*6 * ((unit == CurrentUnit.MILLIAMPS)?1000:1); }
    @Override public double getCurrentAlert(CurrentUnit unit) { return 12; }
    @Override public void setCurrentAlert(double current, CurrentUnit unit) { }
    @Override public boolean isOverCurrent() { return false; }

    // DcMotor methods
    @Override public MotorConfigurationType getMotorType() { return motorConfigurationType; }
    @Override public void setMotorType(MotorConfigurationType motorType) { motorConfigurationType = motorType; }
    @Override public DcMotorController getController() { return null; }
    @Override public int getPortNumber() { return 0; }
    @Override public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) { currentZeroPowerBehavior = zeroPowerBehavior; }
    @Override public ZeroPowerBehavior getZeroPowerBehavior() { return currentZeroPowerBehavior; }
    @Override public void setPowerFloat() { throwDeprecatedError("setPowerFloat"); }
    @Override public boolean getPowerFloat() { throwDeprecatedError("getPowerFloat"); return false; }
    @Override public void setTargetPosition(int position) { throwUnsupportedRunToPointError("setTargetPosition"); }
    @Override public int getTargetPosition() { throwUnsupportedRunToPointError("getTargetPosition"); return 0; }
    @Override public boolean isBusy() { throwUnsupportedRunToPointError("isBusy"); return false;  }
    @Override public void setMode(RunMode mode) {
        if (mode == RunMode.STOP_AND_RESET_ENCODER) {
            currentPower = 0;
            lastCachedPosition = 0;
            lastEpoch = System.nanoTime();
            return;
        }
        if (mode != RunMode.RUN_WITHOUT_ENCODER)
            throw new UnsupportedOperationException("Mode " + mode.name() + " is not supported by virtual motors");
    }
    @Override public RunMode getMode() { return RunMode.RUN_WITHOUT_ENCODER; }

    // DcMotorSimple methods
    @Override public void setDirection(Direction direction) { currentDirection = direction; }
    @Override public Direction getDirection() { return currentDirection; }
    @Override public void setPower(double power) {
        if (Math.abs(power) < stallPower) power = 0;
        else if (Math.abs(power) > 1) power = (power>0)?1:-1;
        updateCachedPosition();
        if (Math.abs(power - currentPower) > 0.1) currentPowerOffset = 1 + powerTolerance*(Math.random()*2 - 1);
        currentPower = power;

    }
    @Override public double getPower() { return currentPower; }

    // Hardware device methods
    @Override public Manufacturer getManufacturer() { return Manufacturer.Other; }
    @Override public String getDeviceName() { return "Virtual DC Motor"; }
    @Override public String getConnectionInfo() { return "Connected Virtually"; }
    @Override public int getVersion() { return 0; }
    @Override public void resetDeviceConfigurationForOpMode() { }
    @Override public void close() { }


    private void throwUnsupportedRunToPointError(String funcName) {
        throw new UnsupportedOperationException("Tried to call " + funcName + " on a virtual motor -- virtual motor does not support run-to-position mode");
    }

    private void throwDeprecatedError(String funcName) {
        throw new UnsupportedOperationException("Function " + funcName + " is deprecated and not available on virtual motors");
    }
}
