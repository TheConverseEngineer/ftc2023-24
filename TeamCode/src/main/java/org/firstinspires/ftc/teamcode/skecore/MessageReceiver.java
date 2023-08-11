package org.firstinspires.ftc.teamcode.skecore;

/** This interface is used by the simulation runner to link virtual hardware.
 */
public interface MessageReceiver {

    /** Send a message to the Unity simulator without waiting for a reply */
    void receiveMessage(byte[] msg);
}
