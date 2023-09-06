package com.example.opensimclient.hardware;

import com.example.opensimclient.SocketClient;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.nio.charset.StandardCharsets;

/** Abstract class implementation of base interface {@link HardwareDevice} which
 * adds support for virtual hardware */
public abstract class VirtualHardwareDevice implements HardwareDevice {

    private final String deviceName;
    protected final SocketClient messageReceiver;
    private final byte[] deviceByteName;

    /** Sends a message to the Unity server without waiting for a reply */
    protected void sendMessage(byte[]... msg) {
        // doing this the long way because I don't want to have to deal with byte to Byte conversions.
        int combinedSize = 0; for (byte[] arr : msg) combinedSize += arr.length;

        byte[] fullMsg = new byte[combinedSize];
        int offset = 0;
        for (byte[] arr : msg) {
            System.arraycopy(arr, 0, fullMsg, offset, arr.length);
            offset += arr.length;
        }

        messageReceiver.receiveMessage(fullMsg);
    }

    /** Sends a message to the Unity server and returns the reply
     *
     * Don't use this method unless you are actually waiting for a reply, as it will
     * stall until a reply is received.
     */
    protected byte[] sendMessageWithReply(byte[]... msg) {
        // doing this the long way because I don't want to have to deal with byte to Byte conversions.
        int combinedSize = 0; for (byte[] arr : msg) combinedSize += arr.length;

        byte[] fullMsg = new byte[combinedSize];
        int offset = 0;
        for (byte[] arr : msg) {
            System.arraycopy(arr, 0, fullMsg, offset, arr.length);
            offset += arr.length;
        }

        return messageReceiver.receiveMessageWithReply(fullMsg);
    }

    /* Some convenience methods */

    /** Converts an array of four bytes into a signed integer */
    protected static int decodeToInt(byte[] bytes) {
        return (((int)bytes[0])<<24) + (((int)bytes[1])<<16) + (((int)bytes[2])<<8) + ((int)bytes[3]);
    }

    /** Converts a signed integer into an array of four bytes */
    protected static byte[] encode(int x) {
        return new byte[]{(byte) (x>>24), (byte) (x>>16), (byte) (x>>8), (byte) x};
    }

    /** Converts a float into an array of four bytes (using IEEE 754 convention) */
    protected static byte[] encode(float x) {
        int intBits = Float.floatToIntBits(x);
        return new byte[]{(byte) (intBits), (byte) (intBits>>8), (byte) (intBits>>16), (byte) (intBits>>24)};
    }

    /** Convenience method that casts a double to a float before calling {@link VirtualHardwareDevice#encode(float)}*/
    protected static byte[] encode(double x) { return encode((float) x); }

    /** Converts a string into an array of bytes (using the utf-8 charset) */
    protected static byte[] encode(String x) {
        return x.getBytes(StandardCharsets.UTF_8);
    }

    /** Returns the name of this device, encoded in bytes */
    protected final byte[] getByteName() {
        return this.deviceByteName;
    }

    /** Throws an {@link UnsupportedOperationException} explaining that the inputted function name is not
     * supported because it is deprecated.
     */
    protected final void throwDeprecatedError(String funcName) {
        throw new UnsupportedOperationException("Function " + funcName + " is not supported, as it is deprecated.");
    }

    /** Constructor for the VirtualHardwareDevice class
     *
     * @param deviceName        the name of the device (should match what ever is used in Unity)
     * @param messageReceiver   the SocketClient that is currently handling hardware interactions.
     */
    public VirtualHardwareDevice(String deviceName, SocketClient messageReceiver) {
        this.deviceName = deviceName;
        this.messageReceiver = messageReceiver;

        // Create byte string here so we can make sure the name isn't too long
        this.deviceByteName = new byte[20];
        byte[] nameInBytes = this.deviceName.getBytes(StandardCharsets.UTF_8);
        assert nameInBytes.length <= 20: "Virtual device names must not be more than 20 characters (following UTF-8 encoding)!";
        System.arraycopy(nameInBytes, 0, this.deviceByteName, 0, nameInBytes.length);
    }

    @Override
    public Manufacturer getManufacturer() { return Manufacturer.Other; }

    @Override
    public String getDeviceName() { return deviceName; }

    @Override
    public String getConnectionInfo() { return "Connected Virtually"; }

    @Override
    public int getVersion() { return 0; }

    @Override
    public void close() { }
}
