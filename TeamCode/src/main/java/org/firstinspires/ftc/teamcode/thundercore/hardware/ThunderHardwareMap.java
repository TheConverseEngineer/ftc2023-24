package org.firstinspires.ftc.teamcode.thundercore.hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public interface ThunderHardwareMap {

    /** Returns an motor with the given name
     *
     * @param name      the name of this motor
     * @param reversed  if true, this motor will be reversed.
     */
    DcMotorEx getMotor(String name, boolean reversed);

    /** Returns the (not reversed) motor with this given name */
    DcMotorEx getMotor(String name);

    /** Returns the servo with this name */
    Servo getServo(String name);

    /** Returns the continuous rotation servo with this name */
    CRServo getCRServo(String name);

    /** Updates the hardware cache and reads all sensors
     * This method is called automatically and only matters when using physical hardware.
     */
    default void updateCache() {

    }
}
