package org.firstinspires.ftc.teamcode.thundercoreV2.utils;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import org.firstinspires.ftc.teamcode.blacksmithcore.Path;
import org.firstinspires.ftc.teamcode.blacksmithcore.Vector2d;

public class Logger {

    public static Logger instance = new Logger();
    public static Logger getInstance() { return instance; }

    private TelemetryPacket currentPacket = new TelemetryPacket();
    private final FtcDashboard dashboard = FtcDashboard.getInstance();

    private Logger() { }

    public void update() {
        dashboard.sendTelemetryPacket(currentPacket);
        currentPacket = new TelemetryPacket();
    }

    public void put(String name, Object val) {
        currentPacket.put(name, val);
    }

    public void drawVector(Vector2d vector, Vector2d position, String color) {
        double arrowAngle = Math.PI + vector.angle();
        double arrowLen = vector.magnitude()/10;
        Vector2d p2 = position.plus(vector);
        currentPacket.fieldOverlay()
                .setStroke(color)
                .setStrokeWidth(1)
                .strokeLine(position.getX(), position.getY(), p2.getX(), p2.getY())
                .strokeLine(p2.getX(), p2.getY(), p2.getX() + arrowLen*Math.cos(arrowAngle+0.35), p2.getY() + arrowLen*Math.sin(arrowAngle+0.35))
                .strokeLine(p2.getX(), p2.getY(), p2.getX() + arrowLen*Math.cos(arrowAngle-0.35), p2.getY() + arrowLen*Math.sin(arrowAngle-0.35));

    }

    public void drawRobot(Vector2d position, double heading, String color, Double radius) {
        currentPacket.fieldOverlay()
                .setStroke(color)
                .setStrokeWidth(1)
                .strokeLine(position.getX(), position.getY(), position.getX() + radius*Math.cos(heading), position.getY() + radius*Math.sin(heading))
                .strokeCircle(position.getX(), position.getY(), radius);
    }

    public void drawPath(Path path, String color) {
        int res = Math.max((int)path.getLength() / 5, 10);
        double[] xPoints = new double[res+1];
        double[] yPoints = new double[res+1];
        for (int i = 0; i <= res; i++) {
            Vector2d point = path.atParameter(i/(double)res).getVector();
            xPoints[i] = point.getX();
            yPoints[i] = point.getY();
        }
        currentPacket.fieldOverlay()
                .setStroke(color)
                .setStrokeWidth(1)
                .strokePolyline(xPoints, yPoints);
    }
}
