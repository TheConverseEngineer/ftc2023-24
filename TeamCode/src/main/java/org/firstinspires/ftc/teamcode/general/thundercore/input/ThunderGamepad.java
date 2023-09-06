package org.firstinspires.ftc.teamcode.general.thundercore.input;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.general.thundercore.actions.ThunderOpMode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ThunderGamepad {

    private final Supplier<Gamepad> gamepad;

    private final List<GamepadButton> buttonListeners = new ArrayList<>();
    private final List<GamepadTrigger> triggerListeners = new ArrayList<>();

    public ThunderGamepad(Supplier<Gamepad> gamepad) {
        this.gamepad = gamepad;
    }

    /** Add a listener to the list of listeners that this gamepad should track */
    public void registerListener(GamepadButton listener) {
        if (!buttonListeners.contains(listener)) buttonListeners.add(listener);
    }

    /** Add a listener to the list of listeners that this gamepad should track */
    public void registerListener(GamepadTrigger listener) {
        if (!triggerListeners.contains(listener)) triggerListeners.add(listener);
    }

    /** Updates this gamepad. This method is called internally by {@link ThunderOpMode}. */
    public void update() {
        for (GamepadButton button : buttonListeners) button.update();
        for (GamepadTrigger trigger : triggerListeners) trigger.update();
    }

    public final GamepadButton circle = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().circle; }};
    public final GamepadButton triangle = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().triangle; }};
    public final GamepadButton square = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().square; }};
    public final GamepadButton cross = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().cross; }};

    public final GamepadButton x = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().x; }};
    public final GamepadButton y = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().y; }};
    public final GamepadButton a = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().a; }};
    public final GamepadButton b = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().b; }};

    public final GamepadButton rightBumper = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().right_bumper; }};

    public final GamepadButton start = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().start; }};
    public final GamepadButton guide = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().guide; }};

    public final GamepadButton dpadDown = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().dpad_down; }};
    public final GamepadButton dpadUp = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().dpad_up; }};
    public final GamepadButton dpadLeft = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().dpad_left; }};
    public final GamepadButton dpadRight = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().dpad_right; }};

    public final GamepadTrigger rightTrigger = new GamepadTrigger(this) {@Override public float get() { return gamepad.get().right_trigger; }};
    public final GamepadTrigger leftTrigger = new GamepadTrigger(this) {@Override public float get() { return gamepad.get().left_trigger; }};

    public final GamepadTrigger rightStickX = new GamepadTrigger(this) {@Override public float get() { return gamepad.get().right_stick_x; }};
    public final GamepadTrigger rightStickY = new GamepadTrigger(this) {@Override public float get() { return gamepad.get().right_stick_y; }};
    public final GamepadTrigger leftStickX = new GamepadTrigger(this) {@Override public float get() { return gamepad.get().left_stick_x; }};
    public final GamepadTrigger leftStickY = new GamepadTrigger(this) {@Override public float get() { return gamepad.get().left_stick_y; }};
}
