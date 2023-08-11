package org.firstinspires.ftc.teamcode.thundercoreV2.actions.templates;

import org.firstinspires.ftc.teamcode.ActionResources;
import org.firstinspires.ftc.teamcode.thundercoreV2.actions.Action;
import org.firstinspires.ftc.teamcode.thundercoreV2.utils.Utils;

/** Wraps a list of actions into a sequence of actions (which is intended to replicate what a function would do).
 * <p>
 * A sequence will run a list of actions, one after the other, until all actions are complete.
 * Note that interrupting a sequence will prematurely end whichever action was currently active, and
 * will ignore future actions.
 * The ActionResources used by this action must be defined separately (individual ActionResources are ignored).
 * Be careful if actions inside a sequence schedule other commands.
 * <p>
 * Similarly to the {@code return} keyword found in a function, use the code {@code SequentialAction.exitSequence()}
 * to get a command that, when run, will stop the sequence it is currently in (assuming one is exists)
 *
 * @author TheConverseEngineer
 * @version 1.5
 */
public final class Sequence extends Action {
    private final Action[] actions; // List of all actions (in the order they should be run)
    private final ActionResources[] requiredResources; // List of all the resources this action needs to run
    private int currentAction = 0;  // The currently active action
    private boolean terminated = false;

    /** Wraps a list of action into a sequence of actions that does not require any other {@link ActionResources}
     *
     * @param actions   The list of actions to run
     *
     * @see Sequence#Sequence(ActionResources[], Action...)
     */
    public Sequence(Action... actions) {
        this(new ActionResources[0], actions);
    }

    /** Wraps a list of actions into a sequence of actions and allows you to specify which {@link ActionResources}
     * this sequence requires
     *
     * @param resources     An array consisting of the resources required by this action
     * @param actions       The list of actions to run
     *
     * @see Sequence#Sequence(Action...)
     */
    public Sequence(ActionResources[] resources, Action... actions) {
        this.actions = actions;
        Utils.safeAssert(this.actions.length > 0, "Tried to create an empty sequential group!");
        this.requiredResources = resources;
    }

    @Override
    public void init() {
        currentAction = 0;
        terminated = false;
        this.actions[0].init();
        if (this.actions[0].getActionKeyword() == ACTION_KEYWORD.EXIT_SEQUENCE) terminated = true;
    }

    @Override
    public void loop() {
        if (isComplete()) return; // Avoids an IndexOutOfBounds exception
        if (this.actions[currentAction].isComplete()) {
            this.actions[currentAction].end(false);
            if (++currentAction < this.actions.length) {
                this.actions[currentAction].init();
                if (this.actions[currentAction].getActionKeyword() == ACTION_KEYWORD.EXIT_SEQUENCE) terminated = true;
            }
        } else {
            this.actions[currentAction].loop();
            if (this.actions[currentAction].getActionKeyword() == ACTION_KEYWORD.EXIT_SEQUENCE) terminated = true;
        }
    }

    @Override
    public void end(boolean wasCancelled) {
        if (isComplete()) return; // All commands were already ended
        this.actions[currentAction].end(true); // Otherwise, interrupt the current action
    }

    @Override
    public boolean isComplete() {
        // True if all actions have been completed
        return terminated || (currentAction >= this.actions.length);
    }

    @Override
    public ActionResources[] getRequiredResources() {
        return this.requiredResources;
    }

    /** Use this command to stop a sequence or loop iteration prematurely.
     * Note that when used in a loop, it has the same effect as the {@code continue} keyword.
     *
     * @return  A command that, when called, will stop the active sequence
     */
    public static Action exitSequence() {
        return new ExitSequenceAction();
    }

    /** Dummy action that will terminate a sequence when called */
    public static final class ExitSequenceAction extends Action {
        @Override
        public ACTION_KEYWORD getActionKeyword() {
            return ACTION_KEYWORD.EXIT_SEQUENCE;
        }

        @Override
        public boolean isComplete() {
            return true;
        }
    }
}
