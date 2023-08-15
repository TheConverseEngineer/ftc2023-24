package org.firstinspires.ftc.teamcode.skecore.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.teamcode.skecore.MessageCodes;
import org.firstinspires.ftc.teamcode.skecore.MessageReceiver;

public class VirtualDcMotor extends VirtualDcMotorSimple implements DcMotor {

    private int offset = 0;
    private ZeroPowerBehavior zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
    //private MotorConfigurationType motorType = MotorConfigurationType.getUnspecifiedMotorType();

    public VirtualDcMotor(String deviceName, MessageReceiver messageReceiver) {
        super(deviceName, messageReceiver);
    }

    public void resetDeviceConfigurationForOpMode() {
        offset = 0;
        zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
        //motorType = MotorConfigurationType.getUnspecifiedMotorType();
    }

    @Override
    public int getCurrentPosition() {
        int rawPos = decodeToInt(sendMessageWithReply(MessageCodes.GET_MOTOR_POSITION, getByteName()));
        return rawPos - offset;
    }

    @Override public DcMotorController getController() { return null; }
    @Override public int getPortNumber() { return 0; }

    @Override public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        this.zeroPowerBehavior = zeroPowerBehavior;
        System.out.println("[FTC-SIM] Please note that while you tried to set the zeroPowerBehavior of a DcMotor, this will" +
                "not make any impact on the simulation.");
    }
    @Override public ZeroPowerBehavior getZeroPowerBehavior() { return zeroPowerBehavior; }

    @Override  public void setPowerFloat() { throwDeprecatedError("setPowerFloat"); }
    @Override public boolean getPowerFloat() { throwDeprecatedError("getPowerFalse"); return false; }
    @Override public void setTargetPosition(int position) { throwNotInModeError("setTargetPosition", RunMode.RUN_TO_POSITION); }
    @Override public int getTargetPosition() { throwNotInModeError("getTargetPosition", RunMode.RUN_TO_POSITION); return 0;}
    @Override public boolean isBusy() { throwNotInModeError("isBusy", RunMode.RUN_TO_POSITION); return false; }

    @Override
    public void setMode(RunMode mode) {
        if (mode == RunMode.STOP_AND_RESET_ENCODER) {
            setPower(0);
            offset += getCurrentPosition();
        } else if (mode != RunMode.RUN_WITHOUT_ENCODER) {
            throw new UnsupportedOperationException(
                    "[FTC-SIM] Virtual motor currently only supports RUN_WITHOUT_ENCODER and STOP_AND_RESET_ENCODER run modes"
            );
        }
    }
    @Override public RunMode getMode() { return RunMode.RUN_WITHOUT_ENCODER; }
    @Override public void setMotorType(MotorConfigurationType motorType) { }
    @Override public MotorConfigurationType getMotorType() { return null; }


    private void throwNotInModeError(String funcName, RunMode mode)  {
        throw new RuntimeException(
                "[FTC-SIM] Attempted to run " + funcName + " without being in " + mode.name()
                        + "mode (note that FTCSim only supports RUN_WITHOUT_ENCODER and STOP_AND_RESET_ENCODER run modes)"
        );
    }
}
