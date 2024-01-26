package org.firstinspires.ftc.teamcode.common.command.gamepad;

public abstract class ToggleButton extends Input {

    private boolean lastValue = false;
    private boolean toggledValue = false;

    /** Abstract method which runs when the button is pressed
     * @param value   The toggled state of this button
     */
    public abstract void onPress(boolean value);

    /** Method which runs whenever the button is released.
     *
     * @param value   The toggle state of the button (same as after it was pressed).
     * */
    public void onRelease(boolean value) { }

    /** Internal method used by GamepadEx class
     * @return if this button is currently pressed
     */
    protected abstract boolean detect();

    @Override
    protected void updateInput() {
        boolean currentState = detect();
        if (currentState != lastValue) {
            lastValue = currentState;

            if (currentState) {
                toggledValue = !toggledValue;
                onPress(toggledValue);
            } else onRelease(toggledValue);
        }
    }
}