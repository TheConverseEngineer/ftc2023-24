package org.firstinspires.ftc.teamcode.common.utils;

import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class CachedIMU extends CachingSensor implements IMU{

    private final IMU imu;
    private final double offset;

    public CachedIMU(IMU imu, long cacheDurationMs, double initialHeading) {
        super(cacheDurationMs, initialHeading);
        this.imu = imu;
        this.offset = initialHeading - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    @Override
    protected double querySensor() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) + offset;
    }

    @Override
    public YawPitchRollAngles getRobotYawPitchRollAngles() {
        return new YawPitchRollAngles(AngleUnit.RADIANS, getValue(), 0, 0, System.nanoTime());
    }

    @Override
    public boolean initialize(Parameters parameters) {
        return false;
    }

    @Override
    public AngularVelocity getRobotAngularVelocity(AngleUnit angleUnit) {
        return imu.getRobotAngularVelocity(angleUnit);
    }

    @Override
    public void resetYaw() {
        imu.resetYaw();
    }

    @Override
    public Orientation getRobotOrientation(AxesReference reference, AxesOrder order, AngleUnit angleUnit) {
        return imu.getRobotOrientation(reference, order, angleUnit);
    }

    @Override
    public Quaternion getRobotOrientationAsQuaternion() {
        return imu.getRobotOrientationAsQuaternion();
    }

    @Override
    public Manufacturer getManufacturer() {
        return imu.getManufacturer();
    }

    @Override
    public String getDeviceName() {
        return imu.getDeviceName();
    }

    @Override
    public String getConnectionInfo() {
        return imu.getConnectionInfo();
    }

    @Override
    public int getVersion() {
        return imu.getVersion();
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        imu.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void close() {
        imu.close();
    }
}
