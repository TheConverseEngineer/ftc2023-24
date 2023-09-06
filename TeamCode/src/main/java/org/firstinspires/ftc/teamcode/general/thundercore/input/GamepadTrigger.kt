package org.firstinspires.ftc.teamcode.general.thundercore.input

/** Represents a gamepad trigger. */
abstract class GamepadTrigger (private val gamepad: ThunderGamepad) {
    private var lastValue = 0f
    private var used = false

    abstract fun get(): Float

    private enum class UsageType {
        ON_PRESS, ON_RELEASE, WHILE_PRESSED, WHILE_RELEASED
    }

    private val runnables = mutableListOf<Triple<Runnable, UsageType, Double>>()

    fun update() {
        val value = get()
        for ((function, type, threshold) in runnables) {
            if (when(type) {
                    UsageType.ON_PRESS -> (value>=threshold) && (lastValue<threshold)
                    UsageType.ON_RELEASE -> (value<threshold) && (lastValue>=threshold)
                    UsageType.WHILE_PRESSED -> (value>=threshold)
                    UsageType.WHILE_RELEASED -> (value<threshold)
                }) function.run()
        }
        lastValue = value
    }

    private fun registerWithGamepad() {
        if (!used) gamepad.registerListener(this)
        used = true
    }

    /** Specify an action that will be called once whenever this trigger is pressed past a threshold
     *
     * @param threshold     the threshold, or the point where the trigger is considered pressed. It should be
     *                      between 0 (fully released) and 1 (fully pressed).
     * */
    fun onPress(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.ON_PRESS, threshold))
        return this
    }

    /** Specify an action that will be called once whenever this trigger is released past a threshold
     *
     * @param threshold     the threshold, or the point where the trigger is considered pressed. It should be
     *                      between 0 (fully released) and 1 (fully pressed).
     * */
    fun onRelease(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.ON_RELEASE, threshold))
        return this
    }

    /** Specify an action that will be called while this trigger is pressed past a threshold
     *
     * @param threshold     the threshold, or the point where the trigger is considered pressed. It should be
     *                      between 0 (fully released) and 1 (fully pressed).
     * */
    fun whilePressed(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.WHILE_PRESSED, threshold))
        return this
    }

    /** Specify an action that will be called while this trigger is released past a threshold
     *
     * @param threshold     the threshold, or the point where the trigger is considered pressed. It should be
     *                      between 0 (fully released) and 1 (fully pressed).
     * */
    fun whileReleased(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.WHILE_PRESSED, threshold))
        return this
    }
}