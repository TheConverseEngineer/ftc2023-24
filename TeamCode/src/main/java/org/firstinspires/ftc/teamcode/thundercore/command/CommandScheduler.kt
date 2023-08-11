package org.firstinspires.ftc.teamcode.thundercore.command

import java.util.function.BiConsumer
import java.util.function.Consumer

/** Singleton class that manages command scheduling */
object CommandScheduler {

    private val queuedCommands = ArrayDeque<Command>()
    private val commandsToCancel = HashSet<Command>()
    private val runningCommands = mutableListOf<Command>()
    private val resourceManager = mutableMapOf<Subsystem, Command?>()

    private val initActions = ArrayList<Consumer<Command>>()
    private val endActions = ArrayList<BiConsumer<Command, Boolean>>()


    /** Resets the command scheduler */
    fun reset() {
        queuedCommands.clear()
        commandsToCancel.clear()
        for (i in runningCommands.size-1 downTo 0) runningCommands.removeAt(i).end(true)
        resourceManager.clear()

        initActions.clear()
        endActions.clear()
    }

    /** Adds an init action to the command scheduler
     *
     * All init actions are called whenever a command is initialized, and the initialized command is the passed parameter. */
    fun addInitAction(action: Consumer<Command>) {
        initActions.add(action)
    }

    /** Adds an end action to the command scheduler
     *
     * All end actions are called whenever a command is ended
     * The two parameters are the command that was ended and a boolean that is true if that command was cancelled */
    fun addEndAction(action: BiConsumer<Command, Boolean>) {
        endActions.add(action)
    }

    /** Registers a subsystem with the command scheduler */
    fun registerSubsystem(vararg subsystems: Subsystem) {
        for (subsystem in subsystems) {
            if (!resourceManager.containsKey(subsystem))
                resourceManager[subsystem] = null
        }
    }

    /** Adds commands to be scheduled */
    fun scheduleCommand(vararg commands: Command) {
        for (command in commands) queuedCommands.add(command)
    }


    /** Updates the command scheduler.
     * This method is automatically called every loop iteration
     *
     * You should probably not need to ever call this method.
     */
    fun updateCommandScheduler() {
        // First: add any new commands to the execution stack
        while (!queuedCommands.isEmpty()) initCommand(queuedCommands.removeFirst())

        // Next: Run any currently active commands and remove completed ones
        for (i in runningCommands.size-1 downTo 0) {
            if (runningCommands[i] in commandsToCancel) runningCommands.removeAt(i)
            else if (runningCommands[i].isComplete) endCommand(
                runningCommands.removeAt(i), false)
            else runningCommands[i].loop()
        }

        // Now update all subsystems
        for (subsystem in resourceManager.keys) {
            subsystem.periodic()
        }
    }

    /** Initialized a command (or ignores it if a non-cancellable command is in progress) */
    private fun initCommand(command: Command) {
        if(command.requirements.any{ resourceManager[it]?.isCancellable == false }) return // This command cannot be scheduled

        // specify requirements
        for (requirement in command.requirements) {
            resourceManager[requirement]?.let {
                endCommand(it, true)
                commandsToCancel.add(it)
            }
            resourceManager[requirement] = command
        }

        for (func in initActions) func.accept(command)
        command.init()
        runningCommands.add(command)
    }

    /** Ends a command and removes any allocated resources */
    private fun endCommand(command: Command, cancelled: Boolean) {
        for (subsystem in command.requirements) resourceManager[subsystem] = null
        for (func in endActions) func.accept(command, cancelled)
        command.end(cancelled)
    }

}