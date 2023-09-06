package org.firstinspires.ftc.teamcode.sample;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.general.thundercore.actions.Subsystem;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.ThunderOpMode;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.templates.InstantAction;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.templates.Repeat;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.templates.Sequence;
import org.firstinspires.ftc.teamcode.general.thundercore.actions.templates.WaitAction;

@SuppressWarnings({"FieldCanBeLocal", "unused"}) // Ignore this, it isn't needed for anything to work



@TeleOp(name = "Example tele-op/auto", group = "Examples")
@Disabled
public class SampleOpMode extends ThunderOpMode {

    private DcMotor armMotor;
    private Subsystem claw, elevator;
    private SampleArmSubsystem arm;


    @Override
    public void initialize() {
        // Initialize the subsystems
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        arm = new SampleArmSubsystem(armMotor);

        scheduler.registerSubsystem(arm, claw, elevator);

        // Now for some actions
        driver.x
            .onPress(new InstantAction(()   -> println("Pressed x")))
            .onRelease(new InstantAction(() -> println("Released x")));


        // And a slightly more complicated example
        codriver.rightBumper
            .onPress(new Sequence(
                  new InstantAction(() -> println("Pressed the right bumper!")),

                  new Repeat(4,
                          new InstantAction(() -> println("This will repeat 4 times!")),
                          new InstantAction(() -> arm.setTargetPosition(50))
                  ),

                  new InstantAction(() -> println("All done!"))
            ));

        // If this was an auto, we would do this instead
        new Sequence(
            new InstantAction(() -> println("Starting Auto!")),
            new InstantAction(() -> println("Waiting 5 seconds!")),
            new WaitAction(5000),
            new InstantAction(() -> {
                // You can have multiple lines here too!
                println("Finishing auto!");
                stopOpMode();
            })
        ).schedule(); // Calling .schedule() will make this command run automatically when the op-mode starts
    }
}
