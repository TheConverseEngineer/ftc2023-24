package org.firstinspires.ftc.teamcode.tests;


import static org.firstinspires.ftc.teamcode.blacksmithcore.utils.clamp;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.checkerframework.checker.units.qual.C;
import org.firstinspires.ftc.teamcode.thundercore.command.Command;
import org.firstinspires.ftc.teamcode.thundercore.command.Subsystem;
import org.firstinspires.ftc.teamcode.thundercore.command.ThunderOpMode;
import org.firstinspires.ftc.teamcode.thundercore.controllers.PIDController;

@TeleOp
@Config
public class LogCatUpdate extends ThunderOpMode {

    public static double targetPosition;
    public static double ERROR = 0.1;
    public static double speed = 0;
    private double pos = 0;

    public static PIDController.PIDCoefficients coefficients = new PIDController.PIDCoefficients(0, 0, 0);

    private final PIDController controller = new PIDController(coefficients);


    @Override
    public void initializeHardware() {

    }

    @Override
    public void initializeCommands() {
        scheduler.scheduleCommand(new Command() {
            private long epoch;

            @Override public void init() { epoch = System.nanoTime(); }
            @Override public void loop() {
                double output = clamp(controller.calculate(pos, targetPosition), -1, 1);
                double dTime = (System.nanoTime() - epoch)/1000000.0;
                epoch += dTime;
                pos += output * dTime * speed * (1+Math.random()*ERROR - (ERROR/2));

                logger.put("pos", pos);
                logger.put("out", output);
                logger.put("der", controller.getDerivative());
            }
            @Override public void end(boolean cancelled) { }
            @Override public boolean isComplete() { return false; }
        });
    }


}
