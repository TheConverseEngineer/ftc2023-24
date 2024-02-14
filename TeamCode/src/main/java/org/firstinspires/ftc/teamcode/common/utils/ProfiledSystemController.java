package org.firstinspires.ftc.teamcode.common.utils;

import com.qualcomm.robotcore.util.ElapsedTime;

public class ProfiledSystemController {

    private int directionMultiplier;

    private State initialState;
    private State goalState;

    private double endAccelTime;
    private double endFullSpeedTime;
    private double endDeccelTime;

    private final ElapsedTime timer;
    private double resetTime;

    private final ProfileConstants constants;

    public double lastTargetVelocity = 0;

    /** Creates a new ProfiledSystemController object
     * <br>
     * This class is similar to a PID controller, but smoothly interpolates between targets
     * in order to make motions more fluid.
     * */
    public ProfiledSystemController(double startPosition, ProfileConstants constants) {
        endAccelTime = -1;
        endFullSpeedTime = -1;
        endDeccelTime = -1;
        timer = new ElapsedTime();
        timer.reset();
        resetTime = 0;
        this.constants = constants;
        initialState = new State(startPosition, 0);
        goalState = new State(startPosition, 0);
    }

    /** Sets the current target of the controller. */
    public void setNewTarget(double newPos) {
        State current = this.calculate(timer.seconds() - resetTime);
        resetTime = timer.seconds();
        this.setNewTarget(newPos, current.position, current.velocity);
    }

    /** Call this method every loop iteration in order to get the current motor power.
     * <br>
     * This method uses an internal clock. Note that constant feedforward terms (like
     * anti-gravity terms) must be handled externally.
     * */
    public double getMotorPower(double currentPosition) {
        State target = this.calculate(timer.seconds() - resetTime);
        this.lastTargetVelocity = target.velocity;
        return  constants.kV * target.velocity +
                constants.kP * (target.position - currentPosition);
    }

    /** Small utility class representing a state */
    public static class State {
        public double position;
        public double velocity;

        public State(double position, double velocity) {
            this.position = position;
            this.velocity = velocity;
        }

    }

    /** Inverts a state if needed (reflexive normalization function) */
    private State direct(State in) {
        return new State(
                in.position * directionMultiplier,
                in.velocity * directionMultiplier
        );
    }

    /** Creates a new trapezoidal profile to move to the new target position */
    private void setNewTarget(double targetPosition, double currentPosition, double currentVelocity) {
        directionMultiplier = (currentPosition > targetPosition) ? -1 : 1;
        initialState = direct(new State(currentPosition, currentVelocity));
        goalState = direct(new State(targetPosition, 0.0));

        initialState.velocity = Math.min(constants.MAX_VEL, initialState.velocity);


        // Deal with a non-zero initial velocity
        double cutoffBegin = initialState.velocity / constants.MAX_ACCEL;
        double cutoffDistBegin = cutoffBegin * cutoffBegin * constants.MAX_ACCEL / 2.0;

        // Now we can calculate the parameters as if it was a full trapezoid instead of a truncated one
        double fullTrapezoidDist = cutoffDistBegin + (goalState.position - initialState.position);
        double accelerationTime = constants.MAX_VEL / constants.MAX_ACCEL;

        double fullSpeedDist = fullTrapezoidDist - accelerationTime * accelerationTime * constants.MAX_ACCEL;

        // Handle a degenerate profile
        if (fullSpeedDist < 0) {
            accelerationTime = Math.sqrt(fullTrapezoidDist / constants.MAX_ACCEL);
            fullSpeedDist = 0;
        }

        endAccelTime = accelerationTime - cutoffBegin;
        endFullSpeedTime = endAccelTime + fullSpeedDist / constants.MAX_ACCEL;
        endDeccelTime = endFullSpeedTime + accelerationTime;
    }

    /** Calculates the profile state at some time t */
    private State calculate(double t) {
        State result = new State(initialState.position, initialState.velocity);

        if (t < endAccelTime) {
            result.velocity += t * constants.MAX_ACCEL;
            result.position += (initialState.velocity + t * constants.MAX_ACCEL / 2.0) * t;
        } else if (t < endFullSpeedTime) {
            result.velocity = constants.MAX_VEL;
            result.position += (initialState.velocity + endAccelTime * constants.MAX_ACCEL
                    / 2.0) * endAccelTime + constants.MAX_VEL * (t - endAccelTime);
        } else if (t <= endDeccelTime) {
            result.velocity = goalState.velocity + (endDeccelTime - t) * constants.MAX_ACCEL;
            double timeLeft = endDeccelTime - t;
            result.position = goalState.position - (goalState.velocity + timeLeft
                    * constants.MAX_ACCEL / 2.0) * timeLeft;
        } else {
            result = goalState;
        }

        return direct(result);
    }

    public double getTargetPosition() {
        return calculate(timer.time() - resetTime).position;
    }

    public double getTargetVelocity() {
        return calculate(timer.time() - resetTime).velocity;
    }

    public static class ProfileConstants {
        public double MAX_ACCEL, MAX_VEL, kV, kP;

        /** Creates a new ProfiledConstants Object.
         * <br>
         * These constants are used by the {@link ProfiledSystemController} class. Note
         * that and constant feedforward term (primarily anti-gravity terms) must be handled
         * separately.
         *
         * @param MAX_VEL       the maximum desired velocity of the system (in units/sec)
         * @param MAX_ACCEL     the maximum desired acceleration of the system (in units/sec^2)
         * @param kV            the velocity feedforward gain
         * @param kP            the proportional feedback gain
         * */
        public ProfileConstants(double MAX_VEL, double MAX_ACCEL, double kV, double kP) {
            this.MAX_ACCEL = MAX_ACCEL;
            this.MAX_VEL = MAX_VEL;
            this.kV = kV;
            this.kP = kP;
        }
    }
}
