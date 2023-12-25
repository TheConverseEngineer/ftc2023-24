package com.thunder.opensim;

import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.thunder.opensim.hardware.VirtualDcMotorEx;
import com.thunder.opensim.hardware.VirtualServo;
import com.thunder.opensim.physics.FlywheelModel;
import com.thunder.opensim.physics.StateSpaceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimParameters {

    private final StateSpaceModel[] models;
    private final String[] motorNames, servoNames;

    /** All credit goes to FTC team 9929 for this! */
    private final HardwareMap hardwareMap = new HardwareMap(null, new OpModeManagerNotifier() {
        @Override public OpMode registerListener(OpModeManagerNotifier.Notifications listener) { return null; }
        @Override public void unregisterListener(OpModeManagerNotifier.Notifications listener) { }
    }) {

        @Nullable
        @Override
        public <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
            synchronized (lock) {
                deviceName = deviceName.trim();
                List<HardwareDevice> list = allDevicesMap.get(deviceName);
                @Nullable T result = null;

                if (list != null) {
                    for (HardwareDevice device : list) {
                        if (classOrInterface.isInstance(device)) {
                            result = classOrInterface.cast(device);
                            break;
                        }
                    }
                }

                return result;
            }
        }
    };

    private SimParameters(StateSpaceModel[] models, String[] motorNames, String[] servoNames) {
        this.models = models;
        this.motorNames = motorNames;
        this.servoNames = servoNames;

        // Add stuff to the hardware map
        for (String name : motorNames) {
            VirtualDcMotorEx motor = new VirtualDcMotorEx(name);
            hardwareMap.put(name, motor);
            hardwareMap.dcMotor.put(name, motor);
        }

        for (String name : servoNames) {
            VirtualServo servo = new VirtualServo(name);
            hardwareMap.put(name, servo);
            hardwareMap.servo.put(name, servo);
        }
    }

    public HardwareMap getHardwareMap() {
        return this.hardwareMap;
    }

    @SuppressWarnings("unused")
    public static class SimParametersBuilder {

        private final Map<StateSpaceModel, ArrayList<String>> models = new HashMap<>();
        private final Set<String> allHardwareDeviceNames = new HashSet<>();

        private final ArrayList<String> motorNames = new ArrayList<>();
        private final ArrayList<String> servoNames = new ArrayList<>();

        private StateSpaceModel lastCreatedModel = null;


        /** Simpler version of {@link SimParametersBuilder#addFlywheelSubsystem(double, double, double, double, double, double, double)}
         *  that uses the defaults of a MR/Matrix motor (the same used in the GoBilda Yellow Jacket series).
         *  It also assumes that the flywheel used has constant density, which may be slightly inaccurate
         *  <br>
         *  These constants should also work for Rev's HD Hex and AndyMark's NeveRest series motors.
         *  <br>
         *  Make sure to use the correct units on all parameters!
         *
         * @param gearRatio         The gear ratio off of a bare motor, with higher values signifying more torque
         *                          (for example, a 5.2:1 ratio motor geared down 2:1 would have a gear ratio of
         *                          5.2*2=10.4)
         * @param wheelDiameter     The diameter of the flywheel in millimeters.
         * @param wheelMass         The mass of the flywheel in grams.
         */
        public SimParametersBuilder addFlywheelSubsystem(double gearRatio, double wheelDiameter, double wheelMass) {
            return addFlywheelSubsystem(
                    gearRatio,
                    (wheelMass/1000)*wheelDiameter*wheelDiameter/8,
                    HardwareConstants.MATRIX_FREE_RPM,
                    HardwareConstants.MATRIX_STALL_TORQUE_KGCM,
                    HardwareConstants.MATRIX_MAX_VOLTAGE,
                    HardwareConstants.MATRIX_STALL_CURRENT,
                    HardwareConstants.MATRIX_FREE_CURRENT
            );
        }


        /** Adds a new flywheel subsystem and allows the user to specify all system parameters
         * <br>
         * Consider using {@link SimParametersBuilder#addFlywheelSubsystem(double, double, double)}, which uses the
         * pre-inputted constants for a GoBilda Yellow Jacket/REV HD Hex/AndyMark NeveRest/MR Matrix motor.
         *
         * @param gearRatio         The gear ratio off of a bare motor, with higher values signifying more torque
         *                          (for example, a 5.2:1 ratio motor geared down 2:1 would have a gear ratio of
         *                          5.2*2=10.4)
         * @param momentOfInertia   the moment of inertia of the flywheel in kgm^2
         * @param freeSpeed         the free speed of the bare motor (not geared) in rpm.
         * @param stallTorque       the stall torque of the bare motor (not geared) in kg*cm
         * @param maxVoltage        the maximum voltage draw of the motor (for FTC, this should always be 12)
         * @param stallCurrent      the current draw at stall of this motor in amps.
         * @param freeCurrent       the current draw of this motor when spinning freely in amps.
         *
         */
        public SimParametersBuilder addFlywheelSubsystem(
                double gearRatio,
                double momentOfInertia,
                double freeSpeed,
                double stallTorque,
                double maxVoltage,
                double stallCurrent,
                double freeCurrent
        ) {
            double resistance = maxVoltage/stallCurrent;

            // Conversions
            freeSpeed   *= Math.PI/30;  // rpm to rad/s
            stallTorque *= 0.0980665;   // kg*cm to Nm

            lastCreatedModel =  new FlywheelModel(
                    freeSpeed / (maxVoltage - freeCurrent*resistance),
                    stallTorque / stallCurrent,
                    resistance,
                    gearRatio,
                    momentOfInertia
            );

            models.put(
                    lastCreatedModel,
                    new ArrayList<>()
            );
            return this;
        }

        /** Assigns a motor to the last-created subsystem
         *
         * @param name      the hardware map name of this motor
         */
        @SuppressWarnings("ConstantConditions")
        public SimParametersBuilder addMotor(String name) {
            if (lastCreatedModel == null) throw new IllegalArgumentException("Tried to create a motor without first creating a subsystem");
            if (allHardwareDeviceNames.contains(name)) throw new IllegalArgumentException("Tried to create a hardware device called " + name + " twice.");
            else {
                allHardwareDeviceNames.add(name);
                models.get(lastCreatedModel).add(name);
                motorNames.add(name);
            }
            return this;
        }

        /** Adds a new servo. Note that servos are not connected to a subsystem
         *
         * @param name      the hardware map name of this servo
         */
        public SimParametersBuilder addServo(String name) {
            if (allHardwareDeviceNames.contains(name)) throw new IllegalArgumentException("Tried to create a hardware device called " + name + " twice.");
            else {
                allHardwareDeviceNames.add(name);
                servoNames.add(name);
            }
            return this;
        }

        /** Returns the created {@link SimParameters} object */
        public SimParameters build() {
            int i = 0;
            StateSpaceModel[] modelArray = new StateSpaceModel[models.size()];
            for (Map.Entry<StateSpaceModel, ArrayList<String>> model : models.entrySet()) {
                model.getKey().assignMotors(model.getValue().toArray(new String[0]));
                modelArray[i++] = model.getKey();
            }

            return new SimParameters(modelArray, motorNames.toArray(new String[0]), servoNames.toArray(new String[0]));
        }
    }

}
