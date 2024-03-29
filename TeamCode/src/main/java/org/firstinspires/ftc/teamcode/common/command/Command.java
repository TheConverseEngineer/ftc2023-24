package org.firstinspires.ftc.teamcode.common.command;

/** An interface which represents an action
 * Commands may involve one or more subsystems.
 * */
public interface Command {

    /** Runs once when this command is scheduled */
    void init();

    /** Runs repeatedly while this command is active */
    void loop();

    /** Runs once when this command ends */
    void end();

    /** If true, the scheduler will end this command */
    boolean isComplete();

    /* Return a modified version of this command, which automatically times out after a set duration
     * @param timeout   the time (in seconds) after which this command should time out.
    default Command withTimeout(double timeout) {
        return new ParallelRaceCommand(this, new WaitCommand(timeout));
    } */
}
