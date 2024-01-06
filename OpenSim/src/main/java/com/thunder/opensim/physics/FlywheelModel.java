package com.thunder.opensim.physics;

public class FlywheelModel extends StateSpaceModel{

    /** Creates a state-space representation of a flywheel
     * <br>
     * Make sure to use radians and Nm!
     *
     *
     * @param Kv            equal to freeSpeed/(maxVoltage - freeCurrent*resistance)
     * @param Kt            equal to stallTorque/stallCurrent
     * @param resistance    equal to maxVoltage/stallCurrent
     * @param gearRatio     the total gearRatio off of a bare motor (numbers greater than
     *                      one represent more torque)
     * @param moment        the moment of inertia of the flywheel.
     *
     * */
    public FlywheelModel(double Kv, double Kt, double resistance, double gearRatio, double moment) {
        super(
            new QuadMatrix(
                0, 1, 0, -gearRatio*gearRatio*Kt/(Kv*resistance*moment)
            ), new DualMatrix(
                0, gearRatio*Kt/(resistance*moment)
            )
        );
    }

    @Override
    protected double getAccelFeedforward() {
        return 0; // Flywheels do not have gravity
    }

    @Override
    protected double getInput() {
        // TODO: Make 12 a inputted parameter (max voltage)
        return 12*getRawInputPower();
    }
}
