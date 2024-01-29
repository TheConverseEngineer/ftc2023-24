package org.firstinspires.ftc.teamcode.common.command;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

/** An interface representing an abstracted mechanism of the robot */
public interface Subsystem {

    /** Runs on every loop iteration before commands are executed, but after read-requests are issued */
    default void earlyPeriodic() {

    }

    /** Runs on every loop iteration after commands are executed */
    void periodic();
}
