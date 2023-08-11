package org.firstinspires.ftc.teamcode.thundercore.trigger

/**
 *
 */
abstract class GamepadButton (private val gamepad: ThunderGamepad) {
    private var lastValue = false
    private var used = false

    abstract fun get(): Boolean

    private enum class UsageType {
        ON_PRESS, ON_RELEASE, WHILE_PRESSED, WHILE_RELEASED
    }

    private val runnables = mutableListOf<Pair<Runnable, UsageType>>()

    fun update() {
        val value = get()
        for ((function, type) in runnables) {
            if (when(type) {
                UsageType.ON_PRESS -> value && !lastValue
                UsageType.ON_RELEASE -> !value && lastValue
                UsageType.WHILE_PRESSED -> value
                UsageType.WHILE_RELEASED -> !value
            }) function.run()
        }
        lastValue = value
    }

    private fun registerWithGamepad() {
        if (!used) gamepad.registerListener(this)
        used = true
    }

    fun onPress(cmd: Runnable) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.ON_PRESS))
        return this
    }

    fun onRelease(cmd: Runnable) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.ON_RELEASE))
        return this
    }

    fun whilePressed(cmd: Runnable) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.WHILE_PRESSED))
        return this
    }

    fun whileReleased(cmd: Runnable) : GamepadButton {
        registerWithGamepad()
        runnables.add(Pair(cmd, UsageType.WHILE_RELEASED))
        return this
    }
}