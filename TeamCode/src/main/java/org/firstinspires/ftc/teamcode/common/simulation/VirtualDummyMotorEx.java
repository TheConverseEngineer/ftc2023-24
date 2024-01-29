package org.firstinspires.ftc.teamcode.common.simulation;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.common.utils.MathUtils;

/** An alternative to {@link VirtualDcMotorEx} that relies on a simulation engine to receive data. */
public class VirtualDummyMotorEx implements DcMotorEx {

    private Direction direction = Direction.FORWARD;
    private double currentPower = 0;
    private double currentVelocity = 0;
    private double currentPosition = 0;


    @Override
    public int getCurrentPosition() {
        return (int) Math.round(this.currentPosition);
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void setPower(double power) {
        this.currentPower = MathUtils.clamp(power, -1, 1);
    }

    @Override
    public double getPower() {
        return this.currentPower;
    }


    @Override
    public void setMode(RunMode mode) {
        if (mode == RunMode.STOP_AND_RESET_ENCODER) currentPosition = 0;
        else if (mode != RunMode.RUN_WITHOUT_ENCODER) unsupported();
    }

    @Override
    public RunMode getMode() {
        return RunMode.RUN_WITHOUT_ENCODER;
    }

    public void internalSetPosition(double newPosition) {
        this.currentPosition = newPosition;
    }

    public double internalGetExactPosition() {
        return this.currentPosition;
    }

    public void internalSetVelocity(double currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    @Override
    public double getVelocity() {
        return currentVelocity;
    }

    private void unsupported() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Operation not supported by VirtualDummyMotorEx");
    }

    @Override public void setMotorEnable() { unsupported(); }
    @Override public void setMotorDisable() { unsupported(); }
    @Override public boolean isMotorEnabled() { unsupported(); return false; }

    @Override public void setVelocity(double angularRate) { unsupported(); }
    @Override public void setVelocity(double angularRate, AngleUnit unit) { unsupported(); }
    @Override public double getVelocity(AngleUnit unit) { unsupported(); return 0; }

    @Override public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) { unsupported(); }
    @Override public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) { unsupported(); }
    @Override public void setVelocityPIDFCoefficients(double p, double i, double d, double f) { unsupported(); }
    @Override public void setPositionPIDFCoefficients(double p) { unsupported(); }
    @Override public PIDCoefficients getPIDCoefficients(RunMode mode) { unsupported(); return null; }
    @Override public PIDFCoefficients getPIDFCoefficients(RunMode mode) { unsupported(); return null; }

    @Override public void setTargetPositionTolerance(int tolerance) { unsupported(); }
    @Override public int getTargetPositionTolerance() { unsupported(); return 0; }

    @Override public double getCurrent(CurrentUnit unit) { unsupported(); return 0; }
    @Override public double getCurrentAlert(CurrentUnit unit) { unsupported(); return 0; }
    @Override public void setCurrentAlert(double current, CurrentUnit unit) { unsupported(); }
    @Override public boolean isOverCurrent() { unsupported(); return false; }

    @Override public MotorConfigurationType getMotorType() { unsupported(); return null; }
    @Override public void setMotorType(MotorConfigurationType motorType) { unsupported(); }
    @Override public DcMotorController getController() { unsupported(); return null; }
    @Override public int getPortNumber() { unsupported(); return 0; }

    @Override public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) { }
    @Override public ZeroPowerBehavior getZeroPowerBehavior() { unsupported(); return null; }

    @Override public void setPowerFloat() { unsupported(); }
    @Override public boolean getPowerFloat() { unsupported(); return false; }

    @Override public void setTargetPosition(int position) { unsupported(); }
    @Override public int getTargetPosition() { unsupported(); return 0; }
    @Override public boolean isBusy() { unsupported(); return false; }

    @Override public Manufacturer getManufacturer() { unsupported(); return null; }
    @Override public String getDeviceName() { unsupported(); return null; }
    @Override public String getConnectionInfo() { unsupported(); return null; }
    @Override public int getVersion() { unsupported(); return 0; }
    @Override public void resetDeviceConfigurationForOpMode() { unsupported(); }
    @Override public void close() { }
}