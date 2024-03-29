package org.firstinspires.ftc.teamcode.common.command.gamepad;

public abstract class StandardTrigger extends Input {

    /** Abstract method which runs every loop
     * @param value   Current value of this input
     */
    public abstract void run(float value);

    /** Internal method used by GamepadEx class
     * @return if this button is currently pressed
     */
    protected abstract float detect();

    @Override
    protected void updateInput() {
        run(detect());
    }
}
