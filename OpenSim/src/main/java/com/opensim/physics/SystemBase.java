package com.opensim.physics;

import com.opensim.hardware.devices.VirtualDcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public abstract class SystemBase {

    /** Updates the system, along with any sensors currently reading this system.
     *
     * @param deltaTime     the time since the last update
     * */
    public abstract void update(double deltaTime);

    public abstract void assignMotor(DcMotorSimple motor);

    /** Returns the theoretical current draw of this system in amperes*/
    public abstract double getCurrentDraw();

    /** Returns the theoretical current draw per motor of this system */
    public abstract double getCurrentDrawPerMotor();
}
