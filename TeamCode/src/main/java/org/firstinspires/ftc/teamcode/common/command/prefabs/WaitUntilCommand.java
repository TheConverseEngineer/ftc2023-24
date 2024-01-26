package org.firstinspires.ftc.teamcode.common.command.prefabs;

import org.firstinspires.ftc.teamcode.common.command.Command;

import java.util.function.BooleanSupplier;

/** Alternative to {@link WaitCommand} which waits until some condition returns true. */
public class WaitUntilCommand implements Command {

    private final BooleanSupplier condition;

    public WaitUntilCommand(BooleanSupplier condition) {
        this.condition = condition;
    }

    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }

    @Override
    public void end() {

    }

    @Override
    public boolean isComplete() {
        return condition.getAsBoolean();
    }
}