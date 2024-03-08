package com.example.thundercore.localization;

import androidx.annotation.Nullable;

import com.example.thundercore.math.dualnum.DualNumber;
import com.example.thundercore.math.dualnum.generics.Time;
import com.example.thundercore.math.geometry.Pose2d;
import com.example.thundercore.math.geometry.Vector2d;
import com.example.thundercore.math.geometry.generics.Global;
import com.example.thundercore.math.geometry.generics.Position;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/** Thread-safe lookup class that maintains a small buffer of old data, and attempts to interpolate historical queries */
public class PersistentPoseLookup {
    private final AtomicReferenceArray<Pose2d<Time, Global>> poses;
    private final AtomicLongArray timestamps;
    private int currentStackIndex;

    public PersistentPoseLookup(int n, Pose2d<Time, Global> start) {
        this.poses = new AtomicReferenceArray<>(n);
        this.timestamps = new AtomicLongArray(n);
        this.currentStackIndex = 1%n;
        this.poses.set(0, start);
    }

    public Pose2d<Time, Global> getClosest(long timestamp) {
        Pose2d<Time, Global> closest = poses.get(0);
        long closestTimestamp = Long.MAX_VALUE;
        for (int i = 0; i < poses.length(); i++) {
            if (poses.get(i) != null) {
                if (Math.abs(timestamp - timestamps.get(i)) < closestTimestamp) {
                    do {
                        closest = poses.get(i);
                        closestTimestamp = Math.abs(timestamps.get(i) - timestamp);
                    } while (poses.get(i) != closest); // In case this value gets changed mid-operation
                }
            }
        }
        return closest;
    }

    /** Returns the most recent pose */
    public Pose2d<Time, Global> getMostRecent() {
        return poses.get((currentStackIndex+poses.length()-1)%poses.length());
    }

    public void addNewEstimate(Pose2d<Time, Global> pose, long timestamp) {
        poses.set(currentStackIndex, pose);
        timestamps.set(currentStackIndex, timestamp);

        currentStackIndex = (currentStackIndex+1)%poses.length();
    }

    public void resetWithNewRoot(Pose2d<Time, Global> pose, long timestamp) {
        for (int i = 0; i < poses.length(); i++) {
            
        }
    }
}
