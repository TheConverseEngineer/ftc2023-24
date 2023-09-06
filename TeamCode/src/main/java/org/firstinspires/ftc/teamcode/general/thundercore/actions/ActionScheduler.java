package org.firstinspires.ftc.teamcode.general.thundercore.actions;

import org.firstinspires.ftc.teamcode.ActionResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Singleton class that handles the scheduling of actions.
 * WRITE MORE
 *
 * @author TheConverseEngineer
 * @version 1.0
 */
public final class ActionScheduler {
    private static final ActionScheduler instance = new ActionScheduler();

    private final List<Action> runningActions = new ArrayList<>();    // All actions in this list are currently running
    private final Queue<Action> queuedActions = new LinkedList<>();   // These actions are queued and should be initialized
    private final List<Subsystem> subsystems  = new ArrayList<>();    // List of all registered subsystems

    /* This list tracks which of the resources specified in {@link org.firstinspires.ftc.teamcode.ActionResources}
     * are being used, and which action are using them. Note that the resource ordinal corresponds to the index in
     * this list */
    private final List<Action> consumedResources = new ArrayList<>(ActionResources.values().length);


    /** Returns the ActionScheduler instance
     * <br>
     * Because this is a singleton class, you cannot instantiate it using the traditional "new ActionScheduler(...)" syntax.
     * Instead, you must use this method to access this class's methods
     */
    public static ActionScheduler getInstance() {
        return instance;
    }

    /** Constructor for ActionScheduler
     * <br>
     * This is a singleton class, and, as such, this constructor cannot be accessed. Use {@link ActionScheduler#getInstance()}
     * instead.
     */
    private ActionScheduler() {
        this.reset();
    }

    /** Resets the ActionScheduler. It is recommended to run this method before and after every OpMode.
     * <br>
     * Note that ThunderOpModeV2 (in-progress) automatically calls this method. Do NOT use this method if
     * you are using ThunderOpModeV2
     */
    public void reset() {
        this.consumedResources.clear();
        this.queuedActions.clear();
        this.runningActions.clear();
        this.subsystems.clear();
    }

    /** Adds an {@link Action} to the queue of actions to run.
     * <br>
     * Inputted action(s) will be initiated and ran on the next loop iteration
     */
    public void scheduleAction(Action... action) {
        queuedActions.addAll(Arrays.asList(action));
    }

    /** Registers a {@link Subsystem}
     * <br>
     *  Registered subsystems will have their {@link Subsystem#earlyPeriodic} and {@link Subsystem#latePeriodic} methods
     *  run every loop iteration at the appropriate times
     */
    public void registerSubsystem(Subsystem... subsystems) {
        this.subsystems.addAll(Arrays.asList(subsystems));
    }

    /** Updates the ActionScheduler. This method must be run every loop iteration.
     * <br>
     * Note that ThunderOpModeV2 (in-progress) automatically calls this method. Do NOT use this method if
     * you are using ThunderOpModeV2
     */
    public void update() {
        while (!queuedActions.isEmpty()) {
            Action actionToInitialize = queuedActions.poll();

            // Prevent the same command from being scheduled twice
            if (actionToInitialize != null && !runningActions.contains(actionToInitialize)) {
                initializeAction(actionToInitialize);
            }
        }

        for (int actionIndex = runningActions.size()-1; actionIndex >= 0; actionIndex--) {
            if (runningActions.get(actionIndex).isComplete()) {
                for (ActionResources resource : runningActions.get(actionIndex).getRequiredResources())
                    consumedResources.set(resource.ordinal(), null);
                runningActions.remove(actionIndex).end(false);
            } else runningActions.get(actionIndex).loop();
        }
    }

    /** Initializes an {@link Action} while taking care of any resource conflicts */
    private void initializeAction(Action actionToInitialize) {
        for (ActionResources resource : actionToInitialize.getRequiredResources()) {
            if (consumedResources.get(resource.ordinal()) != null) {
                consumedResources.get(resource.ordinal()).end(true);
                runningActions.remove(consumedResources.get(resource.ordinal()));
            }

            consumedResources.set(resource.ordinal(), actionToInitialize);
        }

        actionToInitialize.init();
        runningActions.add(actionToInitialize);
    }
}
