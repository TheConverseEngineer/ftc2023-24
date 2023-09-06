package org.firstinspires.ftc.teamcode.general.skecore.hardware;

import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.firstinspires.ftc.teamcode.general.skecore.MessageReceiver;

import java.nio.charset.StandardCharsets;

public abstract class VirtualHardwareDevice implements HardwareDevice {

    private final String deviceName;
    protected final MessageReceiver messageReceiver;
    private final byte[] deviceByteName;

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

    // Some convenience methods
    protected static int decodeToInt(byte[] bytes) {
        return (((int)bytes[0])<<24) + (((int)bytes[1])<<16) + (((int)bytes[2])<<8) + ((int)bytes[3]);
    }

    protected static byte[] encode(int x) {
        return new byte[]{(byte) (x>>24), (byte) (x>>16), (byte) (x>>8), (byte) x};
    }

    protected static byte[] encode(float x) {
        int intBits = Float.floatToIntBits(x);
        return new byte[]{(byte) (intBits), (byte) (intBits>>8), (byte) (intBits>>16), (byte) (intBits>>24)};
    }

    protected static byte[] encode(double x) { return encode((float) x); }

    protected static byte[] encode(String x) {
        return x.getBytes(StandardCharsets.UTF_8);
    }

    protected final byte[] getByteName() {
        return this.deviceByteName;
    }

    protected final void throwDeprecatedError(String funcName) {
        throw new UnsupportedOperationException("[FTC-SIM] Function " + funcName + " is not supported, as it is deprecated.");
    }

    // And now for usable methods
    public VirtualHardwareDevice(String deviceName, MessageReceiver messageReceiver) {
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
