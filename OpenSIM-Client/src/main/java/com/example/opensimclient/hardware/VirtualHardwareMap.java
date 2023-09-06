package com.example.opensimclient.hardware;

import androidx.annotation.NonNull;

import com.example.opensimclient.SocketClient;
import com.example.opensimclient.hardware.actuators.VirtualDcMotor;
import com.example.opensimclient.hardware.actuators.VirtualDcMotorSimple;
import com.example.opensimclient.hardware.actuators.VirtualServo;
import com.example.opensimclient.utils.Pair;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiFunction;

/** Virtual version of {@link HardwareMap} that redirects all function calls
 * Note that only {@link HardwareMap#get(Class, String)} is really supported, and calling other things
 * or trying to access fields could result in errors or unexpected behaviors.
 * <br>
 * However, {@code HardwareMap.getAll(LynxModule.class)} is supported, but will return an empty list.
 *
 * @version 1.0 (untested)
 * @author TheConverseEngineer
 */
public class VirtualHardwareMap extends HardwareMap {

    private final Map<Class<?>, BiFunction<String, SocketClient, VirtualHardwareDevice>> interfaceToImplementation;
    private final Map<Pair<Class<? extends VirtualHardwareDevice>, String>, VirtualHardwareDevice> createdDevices;
    private final SocketClient receiver;

    public VirtualHardwareMap(SocketClient receiver) {
        super(null, null); // Completely avoid using the superclass
        createdDevices = new HashMap<>();
        this.receiver = receiver;

        interfaceToImplementation = new HashMap<>();
        interfaceToImplementation.put(DcMotorSimple.class, VirtualDcMotorSimple::new);
        interfaceToImplementation.put(DcMotor.class, VirtualDcMotor::new);
        interfaceToImplementation.put(Servo.class, VirtualServo::new);
    }

    public <T> T get(Class<? extends T> classOrInterface, SerialNumber serialNumber) {
        throw new UnsupportedOperationException("Accessing devices via serial number is not supported");
    }

    // This will create a new object if it is the first time it is called.
    // Otherwise, it will just return the already-created object
    // If T isn't a supported device type, it will throw an error
    @SuppressWarnings("unchecked")
    public<T> T	get(Class<? extends T> classOrInterface, String deviceName) {
        if (!interfaceToImplementation.containsKey(classOrInterface)) {
            throw new UnsupportedOperationException("Virtual hardware does not currently contain an implementation for " + classOrInterface.getName());
        } else {
            Pair<Class<? extends VirtualHardwareDevice>, String> id =  new Pair<>((Class<? extends VirtualHardwareDevice>)classOrInterface, deviceName);
            if (createdDevices.containsKey(id)) return (T) createdDevices.get(id);
            else {
                VirtualHardwareDevice device = interfaceToImplementation.get(classOrInterface).apply(deviceName, receiver);
                createdDevices.put(id, device);
                return (T) device;
            }
        }
    }

    // Since there is no actual hardware map, this doesn't work
    public HardwareDevice get(String deviceName) {
        throw new UnsupportedOperationException("Virtual hardware map does not support type inferring");
    }

    // I'm not sure if this is supposed to do that get doesn't already do...
    public <T> T tryGet(Class<? extends T> classOrInterface, String deviceName){
        return this.get(classOrInterface, deviceName);
    }

    // I have no idea what this is supposed to do
    public void	logDevices() { }

    // Because there is not actual hardware map, these don't really do anything
    public void	put(@NonNull SerialNumber serialNumber, @NonNull String deviceName, HardwareDevice device) { }
    public void put(String deviceName, HardwareDevice device) { }
    public void put(String deviceName, List<HardwareDevice> deviceInstances) { }
    public boolean remove(SerialNumber serialNumber, String deviceName, HardwareDevice device) { return false; }
    public boolean remove(String deviceName, HardwareDevice device) { return false; }
    public int size() { throw new UnsupportedOperationException("Virtual hardware map does not support size()"); }
    @NonNull
    public Iterator<HardwareDevice> iterator() {
        throw new UnsupportedOperationException("Virtual hardware map cannot be iterated!");
    }
    public <T> List<T> getAll(Class<? extends T> classOrInterface) {
        if (classOrInterface == LynxModule.class) return new ArrayList<T>();
        throw new UnsupportedOperationException("Virtual hardware map only supports getAll for LynxModules");
    }
    public SortedSet<String> getAllNames(Class<? extends HardwareDevice> classOrInterface) {
        throw new UnsupportedOperationException("Virtual hardware map cannot access all known devices together");
    }
    @NonNull
    public Set<String> getNamesOf(HardwareDevice device) {
        throw new UnsupportedOperationException("Virtual hardware map cannot access all known devices together");
    }
    @NonNull
    public Iterable<HardwareDevice>	unsafeIterable() {
        throw new UnsupportedOperationException("Virtual hardware map cannot be iterated!");
    }
}

