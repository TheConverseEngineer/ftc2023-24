package com.thunder.opensim.hardware;

import com.thunder.opensim.MathUtils;
import com.thunder.opensimgui.input.SimulationInput;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/** This class emulates the behavior of the LynxModule on an actual robot
 *
 * It does NOT implement the actual LynxModule class, and should never be called directly by the end user
 */
public class VirtualHardwareBridge {

    private static final VirtualHardwareBridge instance = new VirtualHardwareBridge();

    private final HashMap<String, Double> motorPowers = new HashMap<>();
    private final HashMap<String, Integer> motorPositions = new HashMap<>();
    private final HashMap<String, Double> motorCurrents = new HashMap<>();

    /** A mapping of motor name -> (angular velocity in ticks/sec, angular velocity in rad/s) */
    private final HashMap<String, Map.Entry<Double, Double>> motorSpeed = new HashMap<>();

    private final HashMap<String, Double> servoPositions = new HashMap<>();

    public static VirtualHardwareBridge getInstance() {
        return instance;
    }

    private VirtualHardwareBridge() { }

    /** Resets all hardware values */
    public void reset() {
        motorPositions.clear();
        motorPowers.clear();
        motorSpeed.clear();
        motorCurrents.clear();
    }

    public SimulationInput generateGUIInput() {
        SimulationInput.SimulationInputBuilder builder = new SimulationInput.SimulationInputBuilder();

        // Motors
        String[] _motorNames; double[] _motorPowers; int[] _motorPositions;
        synchronized (motorPowers) {
            _motorNames = motorPowers.keySet().toArray(new String[0]);
            _motorPowers = new double[_motorNames.length];
            for (int i = 0; i < _motorNames.length; i++)
                _motorPowers[i] = MathUtils.ifNull(motorPowers.get(_motorNames[i]), 0.0);
        }

        synchronized (motorPositions) {
            _motorPositions = new int[_motorNames.length];
            for (int i = 0; i < _motorNames.length; i++)
                _motorPositions[i] = MathUtils.ifNull(motorPositions.get(_motorNames[i]), 0);
        }

        for (int i = 0; i < _motorNames.length; i++)
            builder.addMotor(_motorNames[i], _motorPowers[i], _motorPositions[i]);

        // Servos
        synchronized (servoPositions) {
            for (Map.Entry<String, Double> i : servoPositions.entrySet())
                builder.addServo(i.getKey(), i.getValue());
        }

        return builder.build();
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

    /** Sets the angular velocity of a motor */
    public void setMotorVelocity(String name, double tickVel, double radVel) {
        synchronized (motorSpeed) {
            motorSpeed.put(name, new AbstractMap.SimpleEntry<>(tickVel, radVel));
        }
    }

    /** Returns the last recorded angular velocity of a motor in the desired units
     *  (or zero if the velocity has not been recorded)
     */
    public double getMotorVelocity(String name, AngleUnit unit) {
        synchronized (motorSpeed) {
            Map.Entry<Double, Double> result = motorSpeed.get(name);
            if (result == null) return 0.0;
            switch (unit) {
                case RADIANS: return result.getValue();
                case DEGREES: return result.getValue() * 180 / Math.PI;
                default: return 0.0;
            }
        }
    }

    /** Returns the last recorded angular velocity of a motor in ticks/sec
     *  (or zero if the velocity has not been recorded)
     */
    public double getMotorVelocity(String name) {
        synchronized (motorSpeed) {
            Map.Entry<Double, Double> result = motorSpeed.get(name);
            if (result == null) return 0.0;
            else return result.getKey();
        }
    }

    /** Sets the current draw of motor (in amps) */
    public void setCurrentDraw(String name, double currentDraw) {
        synchronized (motorCurrents) {
            motorCurrents.put(name, currentDraw);
        }
    }

    /** Gets the last recorded current draw of a motor (or zero if the current draw has not yet been recorded) */
    public double getCurrentDraw(String name) {
        synchronized (motorCurrents) {
            Double result = motorCurrents.get(name);
            if (result == null) return 0.0;
            else return result;
        }
    }

    /** Sets the position of a servo */
    public void setServoPositions(String name, double pos) {
        synchronized (servoPositions) {
            servoPositions.put(name, MathUtils.clamp(pos, 0, 1));
        }
    }

    /** Gets the position of a servo (or zero if this servo has not been used) */
    public double getServoPosition(String name) {
        synchronized (servoPositions) {
            Double value = servoPositions.get(name);
            if (value == null) return 0.0;
            else return value;
        }
    }
}
