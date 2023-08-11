package org.firstinspires.ftc.teamcode.thundercore.utils

enum class LoggerConfiguration (
    val dashboardEnabled: Boolean,
    val telemetryEnabled: Boolean,
    val androidLoggerEnabled: Boolean
) {
    DASHBOARD_AND_TELEMETRY(true, true, true),
    DASHBOARD(true, false, true),
    TELEMETRY(false, true, true),
    OFF(false, false, false)
}