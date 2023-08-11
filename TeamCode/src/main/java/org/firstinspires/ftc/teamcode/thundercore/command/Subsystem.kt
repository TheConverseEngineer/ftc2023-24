package org.firstinspires.ftc.teamcode.thundercore.command

interface Subsystem {

    /** Code inside this method will run on every loop iteration
     *
     * Please ensure that periodic code is NOT blocking.
     */
    fun periodic()
}