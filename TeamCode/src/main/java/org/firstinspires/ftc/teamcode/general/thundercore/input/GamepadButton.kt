package org.firstinspires.ftc.teamcode.general.thundercore.input

import org.firstinspires.ftc.teamcode.general.thundercore.actions.Action

/** Represents a gamepad button. */
abstract class GamepadButton (private val gamepad: ThunderGamepad) {
    private var lastValue = false
    private var used = false

    abstract fun get(): Boolean

    private enum class UsageType {
        ON_PRESS, ON_RELEASE, WHILE_PRESSED, WHILE_RELEASED
    }

    private val runnables = mutableListOf<Pair<Action, UsageType>>()

    fun update() {
        val value = get()
        for ((function, type) in runnables) {
            if (when(type) {
                    UsageType.ON_PRESS -> value && !lastValue
                    UsageType.ON_RELEASE -> !value && lastValue
                    UsageType.WHILE_PRESSED -> value
                    UsageType.WHILE_RELEASED -> !value
                }) function.schedule()
        }
        lastValue = value
    }

    private fun registerWithGamepad() {
        if (!used) gamepad.registerListener(this)
        used = true
    }

    /** Specify an action that will be called once whenever this button is pressed */
    fun onPress(cmd: Action) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.ON_PRESS))
        return this
    }

    /** Specify an action that will be called once whenever this button is released */
    fun onRelease(cmd: Action) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.ON_RELEASE))
        return this
    }

    /** Specify an action that will be called while this button is pressed */
    fun whilePressed(cmd: Action) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.WHILE_PRESSED))
        return this
    }

    /** Specify an action that will be called while this button is not pressed */
    fun whileReleased(cmd: Action) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.WHILE_RELEASED))
        return this
    }
}