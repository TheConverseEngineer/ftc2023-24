package org.firstinspires.ftc.teamcode.blacksmithcore.controllers

import com.qualcomm.robotcore.hardware.DcMotor

class ControlledMotor (
    val motors: Array<DcMotor>,
    val encoderScalar: Double,
    val encoderOffset: Double,
) {

}