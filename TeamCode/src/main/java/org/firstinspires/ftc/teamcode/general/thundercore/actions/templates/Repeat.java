package org.firstinspires.ftc.teamcode.general.thundercore.actions.templates;

import org.firstinspires.ftc.teamcode.ActionResources;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.Action;

/** Repeats a list of actions a set number of times
 * <p>
 * Inputted actions will run as a sequence for the specified number of iterations.
 * Note that a new actions is NOT initialized for every repetition. Instead, the same
 * instance is used.
 * <p>
 * Because of this, make sure that any necessary "resets" are handled in the Action's init phase
 * Otherwise, the action might immediately end again when it is repeated.
 * <p>
 * Since the inputted actions are internally held in a {@link Sequence}, using {@link Sequence#exitSequence()}
 * will essentially function the same as {@code continue} would in a loop
 *
 * @author TheConverseEngineer
 * @version 1.0
 */
public final class Repeat extends Action {
    private final int numIterations;        // Stores how many times the inputted command should be repeated
    private int completedIterations = 0;    // Stores how many iterations have been completed

    private final Action actionToRepeat;    // The action that should be repeated

    private final ActionResources[] actionResources;    // The resources that this action might require

    /** Repeats a list of actions a set number of times and specify which action resources should be used
     * <p>
     * Inputted actions will run as a sequence for the specified number of iterations.
     * Note that a new actions is NOT initialized for every repetition. Instead, the same
     * instance is used.
     * <p>
     * Because of this, make sure that any necessary "resets" are handled in the Action's init phase
     * Otherwise, the action might immediately end again when it is repeated.
     * <p>
     * Since the inputted actions are internally held in a {@link Sequence}, using {@link Sequence#exitSequence()}
     * will essentially function the same as {@code continue} would in a loop
     *
     * @param numIterations         how many times these actions should be repeated
     * @param actionResources       an array of the required ActionResources
     * @param actionsToRepeat       the actions that should be repeated (note that they will be executed in order)
     */
    public Repeat(int numIterations, ActionResources[] actionResources, Action... actionsToRepeat) {
        this.actionResources = actionResources;
        this.numIterations = numIterations;
        this.actionToRepeat = new Sequence(actionsToRepeat);
    }

    /** Repeats a list of actions a set number of times
     * <p>
     * Inputted actions will run as a sequence for the specified number of iterations.
     * Note that a new actions is NOT initialized for every repetition. Instead, the same
     * instance is used.
     * <p>
     * Because of this, make sure that any necessary "resets" are handled in the Action's init phase
     * Otherwise, the action might immediately end again when it is repeated.
     * <p>
     * Since the inputted actions are internally held in a {@link Sequence}, using {@link Sequence#exitSequence()}
     * will essentially function the same as {@code continue} would in a loop
     *
     * @param numIterations     how many times these actions should be repeated
     * @param actionsToRepeat   the actions that should be repeated (note that they will be executed in order)
     */
    public Repeat(int numIterations, Action... actionsToRepeat) {
        this(numIterations, new ActionResources[0], actionsToRepeat);
    }

    @Override
    public void init() {
        actionToRepeat.init();
    }

    @Override
    public void loop() {
        if (isComplete()) return;
        if (actionToRepeat.isComplete()) {
            actionToRepeat.end(false);
            if (++completedIterations < numIterations) actionToRepeat.init();
        } else actionToRepeat.loop();
    }

    @Override
    public void end(boolean wasCancelled) {
        if (isComplete()) return;            // Already finished this!
        actionToRepeat.end(true); // Otherwise cancel the current iteration
    }

    @Override
    public boolean isComplete() {
        return completedIterations >= numIterations;
    }

    @Override
    public ActionResources[] getRequiredResources() {
        return this.actionResources;
    }
}
