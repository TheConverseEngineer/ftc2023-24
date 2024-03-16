package org.firstinspires.ftc.teamcode.vision;

import java.util.LinkedList;

public class RollingWindow {

    private final int[] detectionCounters = new int[]{0, 0, 0};
    public static final Object mutexLock = new Object();
    public static final LinkedList<Integer> updates = new LinkedList<>();

    public RollingWindow() {

    }

    public void addReading(int x) {
        synchronized (mutexLock) {
            updates.addLast(x);
            detectionCounters[x]++;
            while (updates.size() > 10) {
                detectionCounters[updates.removeFirst()]--;
            }
        }
    }

    public TeamElementDetectionPipeline.Detection getMax() {
        synchronized (mutexLock) {
            if (detectionCounters[0] > detectionCounters[1] && detectionCounters[0] > detectionCounters[2])
                return TeamElementDetectionPipeline.Detection.LEFT;
            else if (detectionCounters[1] > detectionCounters[0] && detectionCounters[1] > detectionCounters[2])
                return TeamElementDetectionPipeline.Detection.CENTER;
            else
                return TeamElementDetectionPipeline.Detection.RIGHT;
        }
    }
}
