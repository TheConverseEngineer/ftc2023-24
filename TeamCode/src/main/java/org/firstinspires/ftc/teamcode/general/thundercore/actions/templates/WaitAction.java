package org.firstinspires.ftc.teamcode.general.thundercore.actions.templates;

import org.firstinspires.ftc.teamcode.general.thundercore.actions.Action;
import org.firstinspires.ftc.teamcode.general.thundercore.utils.Utils;

/** A simple action that waits for a specified number of milliseconds.
 * Note that the actual wait time might not be exactly the duration specified, as precision is
 * based off of the loop time of the system
 *
 * @author TheConverseEngineer
 * @version 1.0
 */
public final class WaitAction extends Action {
    private final long waitDuration;      // The number of milliseconds that this action should wait for
    private double startTime;             // The number of milliseconds (relative to epoch) at which this action was started

    /** A simple action that waits for a specified number of milliseconds.
     * Note that the actual wait time might not be exactly the duration specified, as precision is
     * based off of the loop time of the system
     *
     * @param waitDuration      the number of milliseconds that this Action should wait for
     */
    public WaitAction(long waitDuration) {
        this.waitDuration = waitDuration;
    }

    @Override
    public void init() {
        startTime = Utils.getMsTime();
    }

    @Override
    public boolean isComplete() {
        return (Utils.getMsTime() - startTime) >= waitDuration;
    }
}
