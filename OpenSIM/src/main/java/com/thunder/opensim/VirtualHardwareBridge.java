package com.thunder.opensim;


import java.util.HashMap;

/** This class emulates the behavior of the LynxModule on an actual robot */
public class VirtualHardwareBridge {

    private static final VirtualHardwareBridge instance = new VirtualHardwareBridge();

    private final HashMap<String, Double> motorPowers = new HashMap<>();
    private final HashMap<String, Integer> motorPositions = new HashMap<>();

    public static VirtualHardwareBridge getInstance() {
        return instance;
    }

    private VirtualHardwareBridge() { }

    /** Resets all hardware values */
    public void reset() {
        motorPositions.clear();
        motorPowers.clear();
    }

    /** Sets the input power of a motor
     *
     * @param power     the power of the motor (between -1 and 1)
     */
    public void setMotorPower(String name, double power) {
        synchronized (motorPowers) {
            motorPowers.put(name, MathUtils.clamp(power, -1, 1));
        }
    }

    /** Gets the power of a motor (or returns zero if this motor has not been used yet) */
    public double getMotorPower(String name) {
        synchronized (motorPowers) {
            Double value = motorPowers.get(name);
            if (value == null)
                return 0;
            else
                return value;
        }
    }

    /** Sets the position of a motor */
    public void setMotorPos(String name, int pos) {
        synchronized (motorPositions) {
            motorPositions.put(name, pos);
        }
    }

    /** Gets the last recorded position of a motor (or returns zero if this encoder has not yet been registered) */
    public int getMotorPos(String name) {
        synchronized (motorPositions) {
            Integer value = motorPositions.get(name);
            if (value == null)
                return 0;
            else
                return value;
        }
    }
}
