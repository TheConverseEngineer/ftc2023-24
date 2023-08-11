package org.firstinspires.ftc.teamcode.thundercoreV2;

import org.firstinspires.ftc.teamcode.thundercoreV2.actions.Action;
import org.firstinspires.ftc.teamcode.thundercoreV2.actions.templates.InstantAction;
import org.firstinspires.ftc.teamcode.thundercoreV2.actions.templates.Repeat;
import org.firstinspires.ftc.teamcode.thundercoreV2.actions.templates.Sequence;
import org.firstinspires.ftc.teamcode.thundercoreV2.actions.templates.WaitAction;

public class ActionCompTest {

    public void foo() { }

    public static void main(String[] args) {
        ActionCompTest bar = new ActionCompTest();

        Action a = new Sequence(
                new InstantAction(bar::foo),
                new InstantAction(bar::foo),
                new Repeat(5,
                    new InstantAction(bar::foo),
                    new WaitAction(200),
                    new InstantAction(bar::foo)
                )
        );

        /*
        What I want to be able to do

        Declare psuedo-code
        Sequence extendSlideToFull:
            While slides are not at position:
                Extend slides
                    |-> Stop if (a) reach position, (b) current draw spike + velocity decrease
                If slides not at position:
                    |-> retract slides
                Else: exit sequence

         Action-code version
         Action extendSlideAndGrab = new Sequence(
                new WhileLoop(
                    new ExtendSlideCommand().stopIf(slideSystem.stalled()),
                    new IfElse(() -> slideSystem.position == EXTENDED,
                        WhileLoop.break(), // if true
                        new RetractSlideCommand() // if false
                    )
                )
         );

         */
    }
}
