package org.firstinspires.ftc.teamcode.thundercoreV2.actions;

/** The Subsystem class represents an subsystem on the robot (drivetrain, arm, etc.)
 *  <br>
 *  A subsystem has two notable methods:
 *  <it> {@link Subsystem#earlyPeriodic} - Code in this method runs before actions
 *  <it> {@link Subsystem#latePeriodic}  - Code in this method runs after actions
 *  As a generality, it is probably best to put sensor-related code in {@code earlyPeriodic()} and
 *  motor-related code in {@code latePeriodic}
 *
 * @author TheConverseEngineer
 * @version 1.0
 */
public abstract class Subsystem {
    /** Code inside this method will be ran every loop iteration before actions */
    public void earlyPeriodic() {}

    /** Code inside this method will be ran every loop iteration after actions */
    public void latePeriodic() {}
}
