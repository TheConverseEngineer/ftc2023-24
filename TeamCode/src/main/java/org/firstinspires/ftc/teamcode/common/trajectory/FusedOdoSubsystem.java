package org.firstinspires.ftc.teamcode.common.trajectory;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.common.utils.CachedIMU;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FusedOdoSubsystem extends ThreeTrackingWheelLocalizer {
    public static final double par0YTicks = -4338.924672616547; // y position of the first parallel encoder (in tick units)
    public static final double par1YTicks = 4992.32995344449; // y position of the second parallel encoder (in tick units)
    public static final double perpXTicks = 5388.150835932822; // x position of the perpendicular encoder (in tick units)

    DcMotorEx left, right, front;

    private final CachedIMU imu;

    private final Object mutex = new Object();
    public FusedOdoSubsystem(IMU imu, DcMotorEx left, DcMotorEx right, DcMotorEx front, Pose2d initial) {
        super(Arrays.asList(
                new Pose2d(0, par1YTicks*DriveSubsystem.ODO_IN_PER_TICK[1], 0),
                new Pose2d(0, par0YTicks*DriveSubsystem.ODO_IN_PER_TICK[1], 0),
                new Pose2d(perpXTicks*DriveSubsystem.ODO_IN_PER_TICK[1], 0, Math.PI/2)
        ));

        this.left = left;
        this.right = right;
        this.front = front;

        this.setPoseEstimate(initial);
        this.imu = new CachedIMU(imu, 500, initial.getHeading());
        this.imu.setCacheUpdateCallback(this::setHeading);

    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                left.getCurrentPosition()*DriveSubsystem.ODO_IN_PER_TICK[0],
                right.getCurrentPosition()*DriveSubsystem.ODO_IN_PER_TICK[1],
                front.getCurrentPosition()*DriveSubsystem.ODO_IN_PER_TICK[2]
        );
    }

    @NonNull
    @Override
    public Pose2d getPoseEstimate() {
        synchronized (mutex) {
            return super.getPoseEstimate();
        }
    }

    @Override
    public void update() {
        synchronized (mutex) {
            imu.getValue();
            super.update();
        }
    }

    public void setHeading(double heading) {
        synchronized (mutex) {
            Pose2d current = super.getPoseEstimate();
            super.setPoseEstimate(new Pose2d(current.getX(), current.getY(), heading));
        }
    }

    public void setMHeading(double heading) {
        synchronized (mutex) {
            Pose2d current = super.getPoseEstimate();
            imu.manualReset(heading);
            super.setPoseEstimate(new Pose2d(current.getX(), current.getY(), heading));
        }
    }

    public void applyOffset(Vector2d offset) {
        synchronized (mutex) {
            Pose2d current = super.getPoseEstimate();
            super.setPoseEstimate(new Pose2d(current.getX() + offset.getX(), current.getY() + offset.getY(), current.getHeading()));
        }
    }
}
