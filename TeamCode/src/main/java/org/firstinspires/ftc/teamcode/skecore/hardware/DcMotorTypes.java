package org.firstinspires.ftc.teamcode.skecore.hardware;

public enum DcMotorTypes {
    GOBILDA_435(435, 384.5),
    GOBILDA_312(312, 537.7),
    GOBILDA_1150(1150, 145.1),
    GOBILDA_84(84,1992.6);

    public final int rpm;
    public final double ticksPerRotation;

    DcMotorTypes(int rpm, double ticksPerRotation) {
        this.rpm = rpm;
        this.ticksPerRotation = ticksPerRotation;
    }
}
