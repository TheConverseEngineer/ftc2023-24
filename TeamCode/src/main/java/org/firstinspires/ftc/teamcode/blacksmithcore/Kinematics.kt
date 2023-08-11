@file:JvmName("kinematics")
package org.firstinspires.ftc.teamcode.blacksmithcore

fun mecanumInverseKinematics(v: Pose2d, trackWidth: Double, wheelBase: Double, wheelRadius: Double) = WheelSpeeds(
        (v.x - v.y - v.theta*(trackWidth + wheelBase))/wheelRadius,
        (v.x + v.y - v.theta*(trackWidth + wheelBase))/wheelRadius,
        (v.x - v.y + v.theta*(trackWidth + wheelBase))/wheelRadius,
        (v.x + v.y + v.theta*(trackWidth + wheelBase))/wheelRadius
)

fun mecanumForwardKinematics(ws: WheelSpeeds, trackWidth: Double, wheelBase: Double, wheelRadius: Double) = (1/(wheelBase+trackWidth)).let { Pose2d(
        (ws.leftFront + ws.leftRear + ws.rightRear + ws.rightFront)*wheelRadius/4,
        (-ws.leftFront + ws.leftRear - ws.rightRear + ws.rightFront)*wheelRadius/4,
        (-ws.leftFront*it - ws.leftRear*it + ws.rightRear*it + ws.rightFront*it)*wheelRadius/4
)}