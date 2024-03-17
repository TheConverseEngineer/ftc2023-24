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
        return new Knot(29, 18, 180, 0); // TODO: Replace these coordinates with the intermediate position
    }

    @Override
    protected TeamElementDetectionPipeline.Alliance getAlliance() {
        return TeamElementDetectionPipeline.Alliance.RED; //change this
    }

    @Override
    protected double initialHeading() {
        return -90; // in degrees
    }

    Command right, center, left; // These are initialized below

    @Override
    protected void onInit() {
        right = generateAutoPath(
                90, 0, new Vector2d(7,26), new Vector2d(41,18) // TODO: replace these coordinates with the appropriate values
        ); //first is where the robot should be for spike should be, where the robot should be for second is where the backdrop should be
        left = generateAutoPath(
                90, 0, new Vector2d(43,27), new Vector2d(43,27) // TODO: replace these coordinates with the appropriate values
        );
        center = generateAutoPath(
                90, 0, new Vector2d(28,26), new Vector2d(42,23) // TODO: replace these coordinates with the appropriate values
        );
        //TODO: also make the center and left paths
    }

    @Override
    protected void onStart() {
        TeamElementDetectionPipeline.Detection detection = getVisionDetection();
        if (detection == TeamElementDetectionPipeline.Detection.RIGHT) {
            scheduler.scheduleCommand(right);
        }         else if (detection == TeamElementDetectionPipeline.Detection.LEFT) {
            scheduler.scheduleCommand(left);
        }         else if (detection == TeamElementDetectionPipeline.Detection.CENTER) {
            scheduler.scheduleCommand(center);
        }


        disableWebcam();

        // TODO: repeat this for center and left DONE
    }
}
