package com.opensim.config;

import com.opensim.hardware.devices.VirtualDcMotorEx;
import com.opensim.physics.FreeMotorSystem;
import com.opensim.physics.SystemBase;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** This class handles the configuration used for the simulator
 *
 * Use the provided builder in order to generate a configuration.
 *
 * Credit goes to FTC team 9929 for inspiring this implementation!
 */
public class SimConfig {

    private final HardwareMap hardwareMap;

    private final SystemBase[] systems;

    private SimConfig(HwMapEntry[] hwMapEntries, Map<SystemBase, String[]> systems) {
        hardwareMap = new HardwareMap(null, new OpModeManagerNotifier() {
            @Override public OpMode registerListener(Notifications listener) { return null; }
            @Override public void unregisterListener(Notifications listener) { }
        }) {
            // Override the tryGet method to never attempt to initialize a device
            @Override
            public <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
                synchronized (lock) {
                    deviceName = deviceName.trim();
                    List<HardwareDevice> list = allDevicesMap.get(deviceName);
                    T result = null;
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

        // Populate the hardware map
        for (HwMapEntry entry : hwMapEntries) {
            entry.addDeviceToHwMap(hardwareMap);
        }

        // Assign motors to systems where needed
        this.systems = systems.keySet().toArray(new SystemBase[0]);
        for (SystemBase system : this.systems) {
            for (String motorName : systems.get(system)) {
                system.assignMotor(hardwareMap.get(DcMotorSimple.class, motorName));
            }
        }
    }

    public HardwareMap getHardwareMap() {
        return this.hardwareMap;
    }

    public SystemBase[] getAllSystems() {
        return systems;
    }


    private static abstract class HwMapEntry {
        public abstract void addDeviceToHwMap(HardwareMap hwMap);
    }


    /** Use this method to access the builder the builder */
    public static SimConfigBuilder builder() {
        return new SimConfigBuilder();
    }

    public static class SimConfigBuilder {
        private final HashSet<String> hardwareDeviceNames = new HashSet<>();

        private final ArrayList<HwMapEntry> hwMapEntries = new ArrayList<>();
        private final Map<SystemBase, String[]> systems = new HashMap<>();

        private SystemBase currentSystem = null;

        private SimConfigBuilder() {

        }

        /** Builds the Simulation configuration */
        public SimConfig buildConfig() {
            return new SimConfig(hwMapEntries.toArray(new HwMapEntry[0]), systems);
        }

        /** Adds a free-motor system to this simulator
         * <br>
         * By default, this method assumes that a matrix motor/gobilda motor is used. These values generally still apply for
         * HD Hex and NeveRest motors (although they may be inaccurate for core hex motors). If additional accuracy, is
         * desired, see {@link SimConfigBuilder#addFreeMotorSystem(double, double, double, double, double)}
         * <br>
         * Note that a higher gear ratio represents less speed/more torque
         *
         * @param gearRatio     the system ratio off of a bare motor.
         *                      For example, if you have a 5.2:1 motor geared 2:1, then this value should be 5.2*2=10.4
         */
        public SystemBuilder addFreeMotorSystem(double gearRatio) {
            return addFreeMotorSystem(5900/gearRatio, .25, 9.2, 1.47*gearRatio, 12);
        }

        /** Overload of {@link SimConfigBuilder#addFreeMotorSystem(double)} that allows one to specify all system parameters */
        public SystemBuilder addFreeMotorSystem(double freeSpeed, double freeCurrent, double stallCurrent, double stallTorque, double maxVoltage) {
            if (currentSystem != null) systems.put(currentSystem, new String[0]);
            currentSystem = new FreeMotorSystem(freeSpeed, freeCurrent, stallCurrent, stallTorque, maxVoltage);
            return new SystemBuilder();
        }

        public class SystemBuilder {

            private final ArrayList<String> attachedActuators = new ArrayList<>();

            private SystemBuilder() {

            }

            /** Use this method to add a motor to this system */
            public SystemBuilder addMotor(String deviceName) {
                if (hardwareDeviceNames.contains(deviceName)) throw new IllegalArgumentException("FtcSim: Tried to crate two hardware devices with the same name: " + deviceName);

                hardwareDeviceNames.add(deviceName);
                hwMapEntries.add(new HwMapEntry() {
                    @Override
                    public void addDeviceToHwMap(HardwareMap hwMap) {
                        VirtualDcMotorEx dcMotorEx = new VirtualDcMotorEx(deviceName);

                        hwMap.put(deviceName, dcMotorEx);
                        hwMap.dcMotor.put(deviceName, dcMotorEx);
                    }
                });

                attachedActuators.add(deviceName);
                return this;
            }

            /** Call this method once you are done adding motors/encoders to this system */
            public SimConfigBuilder buildSystem() {
                systems.put(currentSystem, attachedActuators.toArray(new String[0]));
                currentSystem = null;
                return SimConfigBuilder.this;
            }
        }
    }
}
