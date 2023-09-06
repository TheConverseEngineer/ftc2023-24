package org.firstinspires.ftc.teamcode.general.thundercore.actions.templates;

import org.firstinspires.ftc.teamcode.ActionResources;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.Action;

/** A simple action that will run the inputted code once upon initialization before terminating.
 *
 * @author TheConverseEngineer
 * @version 1.0
 */
public final class InstantAction extends Action {
    private final Runnable runnable; // This action will do this once
    private final ActionResources[] resources; // Any resources this action might require

    /** A simple action that will run the inputted code once upon initialization before terminating.
     *
     * @param resources     An array of {@link ActionResources} that this Action might require
     * @param runnable      The code that this action should run once
     */
    public InstantAction(ActionResources[] resources, Runnable runnable) {
        this.runnable = runnable;
        this.resources = resources;
    }

    /** A simple action that will run the inputted code once upon initialization before terminating.
     *
     * @param runnable      The code that this action should run once
     */
    public InstantAction(Runnable runnable) {
        this(new ActionResources[0], runnable);
    }

    @Override
    public void init() {
        runnable.run();
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public ActionResources[] getRequiredResources() {
        return resources;
    }


}
