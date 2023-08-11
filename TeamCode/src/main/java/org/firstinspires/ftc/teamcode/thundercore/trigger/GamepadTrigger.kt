package org.firstinspires.ftc.teamcode.thundercore.trigger

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

    fun onPress(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.ON_PRESS, threshold))
        return this
    }

    fun onRelease(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.ON_RELEASE, threshold))
        return this
    }

    fun whilePressed(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.WHILE_PRESSED, threshold))
        return this
    }

    fun whileReleased(threshold: Double, cmd: Runnable): GamepadTrigger {
        registerWithGamepad()
        runnables.add(Triple(cmd, UsageType.WHILE_PRESSED, threshold))
        return this
    }
}