package org.firstinspires.ftc.teamcode.common.command.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.function.Supplier;

/** A complex wrapper for {@link Gamepad} which adds support for automatic button debouncing and toggling
 * <br>
 * Below is a simple example which runs code when the triangle button is pressed.
 * <pre>
 * {@code gamepadEx.add("Move Slides", gamepadEx.new TriangleToggleButton() {
 *      @Override
 *      public void onPress(boolean value) {
 *           // Put code that should run on press here
 *      }
 *  });}
 * </pre>
 */
public class GamepadEx {
    // I am pretty sure that the SDK does not modify gamepad
    // objects during runtime, but I am using a supplier just in case.
    private final Supplier<Gamepad> gamepad;

    private final HashMap<String, Input> inputs;

    /** Constructor for class GamepadEx
     * @param gamepad      The gamepad that this class should use
     */
    public GamepadEx(Supplier<Gamepad> gamepad) {
        this.gamepad = gamepad;
        this.inputs = new HashMap<>();
    }

    /** Clears all event listeners */
    public void clear() {
        this.inputs.clear();
    }

    /** Adds an event listener
     * @param id        The id of this listener (can be anything)
     * @param input     What this listener should do (use one of the abstract child classes provided)
     */
    public void add(String id, Input input) {
        this.inputs.put(id, input);
    }

    /** Remove an event listener
     * @param id       The id of the listener that should be removed
     */
    public void remove(String id) {
        this.inputs.remove(id);
    }

    /** Update all listeners (should ideally be called every loop iteration */
    public void update(){
        for (Input i: this.inputs.values()) {
            i.updateInput();
        }
    }

    //****************************************************************************************************************************
    //------------------------THE FOLLOWING CODE CONSISTS OF ABSTRACT SUB-CLASSES FOR EVERY GAME-PAD INPUT------------------------
    //****************************************************************************************************************************
    // Buttons A, B, X, and Y
    public abstract class TriangleStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().triangle;}}
    public abstract class TriangleToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().triangle;}}
    public abstract class CircleStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().circle;}}
    public abstract class CircleToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().circle;}}
    public abstract class SquareStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().square;}}
    public abstract class SquareToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().square;}}
    public abstract class CrossStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().cross;}}
    public abstract class CrossToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().cross;}}

    // Back, Guide, and Start buttons
    public abstract class BackStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().back;}}
    public abstract class BackToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().back;}}
    public abstract class GuideStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().guide;}}
    public abstract class GuideToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().guide;}}
    public abstract class StartStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().start;}}
    public abstract class StartToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().start;}}

    // D-Pad buttons
    public abstract class DPadUpStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().dpad_up;}}
    public abstract class DPadUpToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().dpad_up;}}
    public abstract class DPadRightStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().dpad_right;}}
    public abstract class DPadRightToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().dpad_right;}}
    public abstract class DPadLeftStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().dpad_left;}}
    public abstract class DPadLeftToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().dpad_left;}}
    public abstract class DPadDownStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().dpad_down;}}
    public abstract class DPadDownToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().dpad_down;}}

    // The left and right bumpers
    public abstract class LeftBumperStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().left_bumper;}}
    public abstract class LeftBumperToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().left_bumper;}}
    public abstract class RightBumperStandardButton extends StandardButton { @Override protected boolean detect() {return gamepad.get().right_bumper;}}
    public abstract class RightBumperToggleButton extends ToggleButton { @Override protected boolean detect() {return gamepad.get().right_bumper;}}

    // The left and right triggers
    public abstract class RightStandardTrigger extends StandardTrigger { @Override protected float detect() {return gamepad.get().right_trigger;}}
    public abstract class RightToggleTrigger extends ToggleTrigger { @Override protected float detect() {return gamepad.get().right_trigger;}}
    public abstract class LeftStandardTrigger extends StandardTrigger { @Override protected float detect() {return gamepad.get().left_trigger;}}
    public abstract class LeftToggleTrigger extends ToggleTrigger { @Override protected float detect() {return gamepad.get().left_trigger;}}
}
