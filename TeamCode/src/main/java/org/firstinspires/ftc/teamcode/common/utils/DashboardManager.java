package org.firstinspires.ftc.teamcode.common.utils;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.common.simulation.Matrix;

import java.util.HashMap;
import java.util.Map;

public class DashboardManager {

    public static DashboardManager instance = new DashboardManager();

    public static DashboardManager getInstance() {
        return instance;
    }

    private TelemetryPacket packet = new TelemetryPacket();

    private final HashMap<String, Object> data = new HashMap<>();

    private DashboardManager() {

    }

    /** Adds a line of data to the next telemetry packet */
    public void put(String caption, Object data) {
        this.data.put(caption, data);
    }

    /** Overload of {@link DashboardManager#drawRobot(double, double, double)} */
    public void drawRobot(Matrix position) {
        drawRobot(position.get(0, 0), position.get(1, 0), position.get(2, 0));
    }

    /** Draws a robot at the specified coordinates*/
    public void drawRobot(double x, double y, double theta) {
        packet.fieldOverlay()
                .setStroke("blue")
                .strokeCircle(x, y, 7)
                .strokeLine(x, y, x+7*Math.cos(theta), y+7*Math.sin(theta));
    }

    public void drawRobot(Pose2d pose) {
        drawRobot(pose.getX(), pose.getY(), pose.getHeading());
    }

    public void drawTrajectory(Trajectory trajectory) {
        double[] x = new double[31];
        double[] y = new double[31];

        for (int i = 0; i <= 30; i++) {
            Pose2d pose = trajectory.get(trajectory.duration()*i/30.0);
            x[i] = pose.getX();
            y[i] = pose.getY();
        }
        packet.fieldOverlay()
                .setStroke("green")
                .strokePolyline(x, y);

    }

    /** Flushes all stored data and updates the dashboard telemetry. */
    public void update() {
        for (Map.Entry<String, Object> item : data.entrySet()) {
            packet.put(item.getKey(), item.getValue());
        }

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
        packet = new TelemetryPacket();
    }
}
