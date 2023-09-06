package com.example.opensimclient;

/** Static class that keeps track of all the different message codes used by the simulator */
public class MessageCodes {
    private MessageCodes() {}

    public static final byte[] SET_MOTOR_POWER = new byte[]{0};
    public static final byte[] GET_MOTOR_POSITION = new byte[]{1};

    public static final byte[] SET_SERVO_POSITION = new byte[]{0};
}
