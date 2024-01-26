package org.firstinspires.ftc.teamcode.common.command.prefabs;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.command.Command;

/** A simple pre-built command that waits for some duration of time.
 * <br>
 * Note that the resolution of this command is based on the average loop time (for example, if each loop
 * takes 30ms, then this command has a resolution of 30ms as well).
 * */
public class WaitCommand implements Command {
    private final ElapsedTime timer;
    private final long waitTime;

    /** Constructor for WaitCommand
     * @param waitTime  time to wait, in seconds
     */
    public WaitCommand(double waitTime) {
        timer = new ElapsedTime();
        this.waitTime = (long) (waitTime * 1000);
    }


    @Override
    public void init() {
        timer.reset();
    }

    @Override
    public void loop() {

    }

    @Override
    public void end() {

    }

    @Override
    public boolean isComplete() {
        return timer.milliseconds() > this.waitTime;
    }
}
