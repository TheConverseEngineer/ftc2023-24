package org.firstinspires.ftc.teamcode.common.trajectory;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;

import org.jetbrains.annotations.NotNull;

public class TrajectoryBuilderEx {
    private final TrajectoryBuilder trajectoryBuilder;

    public TrajectoryBuilderEx(@NonNull Knot knot, @NonNull TrajectoryVelocityConstraint baseVelConstraint, @NonNull TrajectoryAccelerationConstraint baseAccelConstraint) {
        this.trajectoryBuilder = new TrajectoryBuilder(knot.getPose(), knot.splineHeading, baseVelConstraint, baseAccelConstraint);
    }

    @NotNull
    public Trajectory build() {
        return trajectoryBuilder.build();
    }

    @NotNull
    public TrajectoryBuilderEx splineToConstantHeading(@NonNull Vector2d endPosition, double endTangent) {
        trajectoryBuilder.splineToConstantHeading(endPosition, endTangent);
        return this;
    }

    @NotNull
    public TrajectoryBuilderEx splineToLinearHeading(@NonNull Pose2d endPose, double endTangent) {
        trajectoryBuilder.splineToLinearHeading(endPose, endTangent);
        return this;
    }

    @NotNull
    public TrajectoryBuilderEx splineToSplineHeading(@NonNull Pose2d endPose, double endTangent) {
        trajectoryBuilder.splineToSplineHeading(endPose, endTangent);
        return this;
    }

    @NotNull
    public TrajectoryBuilderEx splineToConstantHeading(Knot knot) {
        trajectoryBuilder.splineToConstantHeading(knot.getPoint(), knot.splineHeading);
        return this;
    }

    @NotNull
    public TrajectoryBuilderEx splineToLinearHeading(Knot knot) {
        trajectoryBuilder.splineToLinearHeading(knot.getPose(), knot.splineHeading);
        return this;
    }

    @NotNull
    public TrajectoryBuilderEx splineToSplineHeading(Knot knot) {
        trajectoryBuilder.splineToSplineHeading(knot.getPose(), knot.splineHeading);
        return this;
    }


}
