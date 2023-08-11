package org.firstinspires.ftc.teamcode.thundercoreV2.actions;

import org.firstinspires.ftc.teamcode.ActionResources;

/** The Action class represents an action that the robot can perform
 *  <br>
 *  An action has 3 phases: <br>
 *      <li> Initialization: Code in the {@link Action#init} method is run once when the action begins
 *      <li> Loop:           Code in the {@link Action#loop} method runs over and over again as long as
 *                           {@link Action#isComplete} returns false
 *      <li> End:            Once {@link Action#isComplete} returns false, code in the {@link Action#end} method is run once.
 *
 * @author TheConverseEngineer
 * @version 1.0
 */
@SuppressWarnings("unused")
public abstract class Action {

    /** This enumerator is used internally by some of the action templates. It is not recommended to
     * use this enum unless you really know what you are doing.
     */
    public enum ACTION_KEYWORD {
        NONE, EXIT_SEQUENCE
    }

    /** This method returns the {@link ACTION_KEYWORD} associated with this action. This method is
     * used internally by some of the action templates, and it is not recommended to use this enum
     * unless you really know what you are doing
     */
    public ACTION_KEYWORD getActionKeyword() {
        return ACTION_KEYWORD.NONE;
    }

    /** Code in this method is run once when this action begins
     * <br>
     *  In order to run code on initialization, override this method
     */
    public void init() { }

    /** Code in this method is run repeatedly while this action is active
     * <br>
     *  In order to run code on loop, override this method
     */
    public void loop() { }

    /** Code in this method is run once when this action ends
     * <br>
     *  In order to run code on completion, override this method
     *
     * @param wasCancelled      if false, this action was ended because {@link Action#isComplete} returned true. If
     *                          true, then this action was interrupted prematurely (either because the OpMode was ended
     *                          or because another action required a resource used by this action
     */
    public void end(boolean wasCancelled) { }

    /** This method should return true when this action is complete.
     * <br>
     *  Returning false lets the {@link ActionScheduler} know that the {@link Action#end} method should be run.
     *  Note that this method must be overridden.
     */
    public abstract boolean isComplete();

    /** This method specifies which resources this action needs in order to run.
     * <br>
     *  Note that only one action may use a resource at a time, and that scheduling an action that uses
     *  a resource will cause other actions using that resource to be terminated.
     */
    public ActionResources[] getRequiredResources() {
        return new ActionResources[0];
    }

    /** Convenience method that automatically schedules this action with the Action scheduler.
     * <br>
     *  This method is essentially equivalent to {@code ActionScheduler.getInstance().scheduleAction(thisAction); }
     */
    public final void schedule() {
        ActionScheduler.getInstance().scheduleAction(this);
    }
}
