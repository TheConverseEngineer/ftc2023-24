package org.firstinspires.ftc.teamcode.thundertest;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.example.thundercore.filter.LeastSquaresFilter;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Random;

@Config
@TeleOp
public class NoiseFilterTest extends OpMode {

    double epoch;
    Random random = new Random();
    LeastSquaresFilter filter = new LeastSquaresFilter(k, n);

    public static double speed = 1, stdev = 0.2, mag = 4, k = 1;
    public static int n = 3;

    private double lastUpdate = Double.MIN_VALUE;

    @Override
    public void init() {
        epoch = System.nanoTime()/1e9;
    }

    @Override
    public void loop() {
        if ((System.nanoTime()/1e6 - lastUpdate) < 8) return;
        lastUpdate = System.nanoTime()/1e6;

        double trueVal = Math.cos((System.nanoTime()/1e9 - epoch)*speed)*mag;

        double noisy = trueVal + random.nextGaussian()*stdev;

        double filtered = filter.filter(noisy);

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("raw", noisy);
        packet.put("filtered", filtered);
        packet.put("real", trueVal);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);


    }
}
