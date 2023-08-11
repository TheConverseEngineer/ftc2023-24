package org.firstinspires.ftc.teamcode.thundercore.utils

import android.util.Log
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.blacksmithcore.*
import kotlin.math.*

object Logger {
    private val dashboard = FtcDashboard.getInstance()
    var telemetry: Telemetry? = null
    var configuration = LoggerConfiguration.DASHBOARD_AND_TELEMETRY

    private val valuesToLog = HashMap<String, Any?>()
    private var packet: TelemetryPacket = TelemetryPacket()


    fun put(name: String, value: Any?) {
        if (!configuration.dashboardEnabled && !configuration.telemetryEnabled) return // No point in tracking this
        valuesToLog[name] = value
    }

    fun logMessage(message: String) {
        if (!configuration.androidLoggerEnabled) return // Log disabled
        Log.d("ThunderCore", message)
    }

    fun logWarning(message: String) {
        if (!configuration.androidLoggerEnabled) return // Log disabled
        Log.w("ThunderCore", message)
    }

    fun drawRobot(pose: Pose2d, trackWidth: Double, wheelBase: Double) {
        packet.fieldOverlay()
            .setStroke("green")
            .strokePolygon(doubleArrayOf(
                    pose.x - wheelBase*cos(pose.theta) - trackWidth*sin(pose.theta),
                    pose.x + wheelBase*cos(pose.theta) - trackWidth*sin(pose.theta),
                    pose.x + wheelBase*cos(pose.theta) + trackWidth*sin(pose.theta),
                    pose.x - wheelBase*cos(pose.theta) + trackWidth*sin(pose.theta)
            ), doubleArrayOf(
                    pose.y - wheelBase*sin(pose.theta) + trackWidth*cos(pose.theta),
                    pose.y + wheelBase*sin(pose.theta) + trackWidth*cos(pose.theta),
                    pose.y + wheelBase*sin(pose.theta) - trackWidth*cos(pose.theta),
                    pose.y - wheelBase*sin(pose.theta) - trackWidth*cos(pose.theta),
            ))
            .strokeLine(pose.x, pose.y, pose.x+wheelBase*cos(pose.theta), pose.y+wheelBase*sin(pose.theta))
    }

    fun drawRobot(pose: MutablePose2D, trackWidth: Double, wheelBase: Double) {
        drawRobot(pose.toPose(), trackWidth, wheelBase)
    }

    fun drawTrajectory(trajectory: Trajectory, res: Int = 30) {
        val (xData, yData) = linspace(0.0, trajectory.getLength(), res).map {
            trajectory(it).let { point -> Pair(point.x.x, point.y.x) }
        }.unzip()
        packet.fieldOverlay()
            .setStroke("blue")
            .strokePolyline(xData.toDoubleArray(), yData.toDoubleArray())
    }

    /** Draws a vector on the field */
    private fun drawVector(vector: Vector2d, start: Vector2d, color: String) {
        val theta = atan2(vector.y, vector.x)
        val rho = hypot(vector.y, vector.x)

        packet.fieldOverlay()
            .setStroke(color)
            .strokeLine(start.x, start.y, start.x + vector.x, start.y + vector.y)
            .strokePolyline(
                doubleArrayOf(
                    start.x + 0.9*rho*cos(theta + PI/12),
                    start.x + vector.x,
                    start.x + 0.9*rho*cos(theta - PI/12)
                ), doubleArrayOf(
                    start.y + 0.9*rho*sin(theta + PI/12),
                    start.y + vector.y,
                    start.y + 0.9*rho*sin(theta - PI/12)
                )
            )
    }

    /** Draws an red velocity vector and an orange acceleration vector on the robot. */
    fun drawForceVectors(vector: Vector2dDual) {
        drawVector(Vector2d(vector.x.x, vector.y.x), Vector2d(vector.x.dx, vector.y.dx), "red");
        drawVector(Vector2d(vector.x.x, vector.y.x), Vector2d(vector.x.ddx, vector.y.ddx), "orange");
    }

    fun update() {
        if (!configuration.telemetryEnabled && !configuration.dashboardEnabled) return
        for ((key, value) in valuesToLog) {
            if (configuration.telemetryEnabled) telemetry?.addData(key, value)
            if (configuration.dashboardEnabled) packet.put(key, value)
        }

        if (configuration.telemetryEnabled) telemetry?.update()
        if (configuration.dashboardEnabled) dashboard.sendTelemetryPacket(packet)
        packet = TelemetryPacket()
        valuesToLog.clear()
    }
}