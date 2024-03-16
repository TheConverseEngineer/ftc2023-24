package org.firstinspires.ftc.teamcode.vision;


import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Config
public class TeamElementDetectionPipeline implements VisionProcessor {

    public static int[] rightCoords = new int[]{565, 40, 635, 110};
    public static int[] leftCoords = new int[]{90, 25, 150, 90};
    public static int[] centerCoords = new int[]{335, 28, 380, 75};

    private final Mat   leftLAB = new Mat(),
                        rightLAB = new Mat(),
                        centerLAB = new Mat();

    public enum Alliance {RED, BLUE}
    public enum Detection {LEFT, CENTER, RIGHT}

    private final Alliance currentAlliance;
    private final RollingWindow detections = new RollingWindow();
    private final AtomicInteger framesAnalyzed = new AtomicInteger(0);

    private final AtomicBoolean processorEnabled = new AtomicBoolean(true);

    public TeamElementDetectionPipeline(Alliance currentAlliance) {
        this.currentAlliance = currentAlliance;
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) { }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        if (!processorEnabled.get()) return null;

        // Keep track of how many frames I have seen
        framesAnalyzed.getAndIncrement();

        // Isolate the three potential regions
        Mat left = frame.submat(leftCoords[1], leftCoords[3], leftCoords[0], leftCoords[2]);
        Mat center = frame.submat(centerCoords[1], centerCoords[3], centerCoords[0], centerCoords[2]);
        Mat right = frame.submat(rightCoords[1], rightCoords[3], rightCoords[0], rightCoords[2]);

        // Now convert all three to LAB color space
        Imgproc.cvtColor(left, leftLAB, Imgproc.COLOR_RGB2Lab);
        Imgproc.cvtColor(right, rightLAB, Imgproc.COLOR_RGB2Lab);
        Imgproc.cvtColor(center, centerLAB, Imgproc.COLOR_RGB2Lab);

        // Release the isolated mats (in case the GC gets lazy or something)
        left.release(); right.release(); center.release();

        // Calculate averages for each region
        Scalar leftAverage = Core.mean(leftLAB);
        Scalar rightAverage = Core.mean(rightLAB);
        Scalar centerAverage = Core.mean(centerLAB);

        // Release the LAB mats (this one is actually important, because the GC collects these implicitly for some reason)
        leftLAB.release(); rightLAB.release(); centerLAB.release();

        if (this.currentAlliance == Alliance.BLUE) {
            // Minimum value of channel index 2
            if (leftAverage.val[2] < rightAverage.val[2] && leftAverage.val[2] < centerAverage.val[2]) detections.addReading(0);
            else if (leftAverage.val[2] > rightAverage.val[2] && rightAverage.val[2] < centerAverage.val[2]) detections.addReading(2);
            else detections.addReading(1);
        } else {
            if (leftAverage.val[1] > rightAverage.val[1] && leftAverage.val[1] > centerAverage.val[1]) detections.addReading(0);
            else if (leftAverage.val[1] < rightAverage.val[1] && rightAverage.val[1] > centerAverage.val[1]) detections.addReading(2);
            else detections.addReading(1);
        }


        return null;
    }

    public Detection getDetection() {
        return detections.getMax();
    }

    public int getFramesAnalyzed() {
        return framesAnalyzed.get();
    }

    public void enable() {
        processorEnabled.set(true);
    }

    public void disable() {
        processorEnabled.set(false);
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        // TODO: delete all code in this method when I am done debugging
        Paint paint = new Paint();
        paint.setAlpha(50);

        Paint text = new Paint();
        text.setTextSize(15*scaleBmpPxToCanvasPx);

        canvas.drawRect(leftCoords[0]*scaleBmpPxToCanvasPx, leftCoords[1]*scaleBmpPxToCanvasPx, leftCoords[2]*scaleBmpPxToCanvasPx, leftCoords[3]*scaleBmpPxToCanvasPx, paint);
        canvas.drawRect(centerCoords[0]*scaleBmpPxToCanvasPx, centerCoords[1]*scaleBmpPxToCanvasPx, centerCoords[2]*scaleBmpPxToCanvasPx, centerCoords[3]*scaleBmpPxToCanvasPx, paint);
        canvas.drawRect(rightCoords[0]*scaleBmpPxToCanvasPx, rightCoords[1]*scaleBmpPxToCanvasPx, rightCoords[2]*scaleBmpPxToCanvasPx, rightCoords[3]*scaleBmpPxToCanvasPx, paint);
    }

    @NonNull
    @Override
    public String toString() {
        return detections.getMax().toString();
    }
}
