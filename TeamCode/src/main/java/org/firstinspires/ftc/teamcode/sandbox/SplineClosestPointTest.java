package org.firstinspires.ftc.teamcode.sandbox;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.example.thundercore.gvf.CubicSpline;
import com.example.thundercore.math.dualnum.DualNumber;
import com.example.thundercore.math.dualnum.generics.Parameter;
import com.example.thundercore.math.geometry.Vector2d;
import com.example.thundercore.math.geometry.generics.Global;
import com.example.thundercore.math.geometry.generics.Position;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.example.thundercore.gvf.Knot;

@TeleOp
@Config
public class SplineClosestPointTest extends LinearOpMode {

    public static Knot i = new Knot(), f = new Knot();

    public static double x = 0, y = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        CubicSpline spline = new CubicSpline(i, f);

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            Vector2d<Parameter, Position, Global> pose = new Vector2d<>(new DualNumber<>(x, 0, 0), new DualNumber<>(y, 0, 0));

            long a = System.nanoTime();
            double u = spline.getClosestPoint(pose);
            long b = System.nanoTime();

            long c = System.nanoTime();
            Vector2d<Parameter, Position, Global> proj = spline.get(u);
            long d = System.nanoTime();

            long e = System.nanoTime();
            long f = System.nanoTime();

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("param", u);
            packet.put("time", (b-a - (f-e))/1e6);
            packet.put("time2", (d-c - (f-e))/1e6);

            drawTrajectory(packet, spline);
            packet.fieldOverlay()
                    .setStroke("red")
                    .strokeCircle(x, y, 5)
                    .strokeCircle(proj.getX().get(), proj.getY().get(), 5);

            FtcDashboard.getInstance().sendTelemetryPacket(packet);

        }
    }

    private void drawTrajectory(TelemetryPacket packet, CubicSpline trajectory) {
        double[] x = new double[31];
        double[] y = new double[31];

        for (int i = 0; i <= 30; i++) {
            Vector2d<Parameter, Position, Global> p = trajectory.get(i/30.0);
            x[i] = p.getX().get();
            y[i] = p.getY().get();
        }
        packet.fieldOverlay()
                .setStroke("green")
                .strokePolyline(x, y);
    }

}
