package org.firstinspires.ftc.teamcode.common.trajectory;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FusedOdoSubsystem extends ThreeTrackingWheelLocalizer {
    public static final double par0YTicks = -4338.924672616547; // y position of the first parallel encoder (in tick units)
    public static final double par1YTicks = 4992.32995344449; // y position of the second parallel encoder (in tick units)
    public static final double perpXTicks = 5388.150835932822; // x position of the perpendicular encoder (in tick units)

    DcMotorEx left, right, front;

    private final Object mutex = new Object();
    public FusedOdoSubsystem(DcMotorEx left, DcMotorEx right, DcMotorEx front) {
        super(Arrays.asList(
                new Pose2d(0, par0YTicks*DriveSubsystem.ODO_IN_PER_TICK[1], 0),
                new Pose2d(0, par1YTicks*DriveSubsystem.ODO_IN_PER_TICK[1], 0),
                new Pose2d(perpXTicks*DriveSubsystem.ODO_IN_PER_TICK[1], Math.PI/2)
        ));

        this.left = left;
        this.right = right;
        this.front = front;
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
            super.update();
        }
    }

    public void applyOffset(Vector2d offset) {
        synchronized (mutex) {
            Pose2d current = super.getPoseEstimate();
            super.setPoseEstimate(new Pose2d(current.getX() + offset.getX(), current.getY() + offset.getY(), current.getHeading()));
        }
    }
}
