package org.firstinspires.ftc.teamcode.autoV2;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.common.command.Command;
import org.firstinspires.ftc.teamcode.common.trajectory.Knot;
import org.firstinspires.ftc.teamcode.vision.TeamElementDetectionPipeline;

@Autonomous // This line ensures that this class appears in the list of options
public class RedBackdropAuto extends AutoBaseClass{
    @Override
    protected Knot intermediateSpikePoint() {
        return new Knot(0, 0, 0, 0); // TODO: Replace these coordinates with the intermediate position
    }

    @Override
    protected TeamElementDetectionPipeline.Alliance getAlliance() {
        return TeamElementDetectionPipeline.Alliance.RED;
    }

    @Override
    protected double initialHeading() {
        return -90; // in degrees
    }

    Command right, center, left; // These are initialized below

    @Override
    protected void onInit() {
        right = generateAutoPath(
                90, 0, new Vector2d(0,0), new Vector2d(0,0); // TODO: replace these coordinates with the appropriate values
        );

        //TODO: also make the center and left paths
    }

    @Override
    protected void onStart() {
        TeamElementDetectionPipeline.Detection detection = getVisionDetection();
        if (detection == TeamElementDetectionPipeline.Detection.RIGHT) {
            scheduler.scheduleCommand(right);
        } // TODO: repeat this for center and left
    }
}
