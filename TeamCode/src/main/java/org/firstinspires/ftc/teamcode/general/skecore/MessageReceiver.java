package org.firstinspires.ftc.teamcode.general.skecore;

/** This interface is used by the simulation runner to link virtual hardware.
 */
public interface MessageReceiver {

    /** Send a message to the Unity simulator without waiting for a reply */
    void receiveMessage(byte[] msg);

    /** Sends a message to the Unity simulator and then waits for a reply */
    byte[] receiveMessageWithReply(byte[] msg);
}
