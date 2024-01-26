package org.firstinspires.ftc.teamcode.common.command.gamepad;

public abstract class ToggleTrigger extends Input {

    private boolean lastState = false;
    private boolean toggleState = false;
    private static final float PRESS_THRESHOLD = 0.85f;

    /** Abstract method which runs when pressed
     * @param value   Current toggled value of this input
     */
    public abstract void onPress(boolean value);

    /** Abstract method which runs when pressed
     * @param value   Current toggled value of this input
     */
    public void onRelease(boolean value) { }


    /** Internal method used by GamepadEx class
     * @return if this button is currently pressed
     */
    protected abstract float detect();

    @Override
    protected void updateInput() {
        boolean currentState = detect() > PRESS_THRESHOLD;
        if (currentState != lastState) {
            lastState = currentState;

            if (currentState) {
                toggleState = !toggleState;
                onPress(toggleState);
            }
        }
    }
}
