package org.firstinspires.ftc.teamcode.thundercore.trigger;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ThunderGamepad {
    private final Supplier<Gamepad> gamepad;
    private final ArrayList<GamepadButton> activeButtons = new ArrayList<>();
    private final ArrayList<GamepadTrigger> activeTriggers = new ArrayList<>();

    public ThunderGamepad(Supplier<Gamepad> gamepad) {
        this.gamepad = gamepad;
    }

    public void registerListener(GamepadButton button) {
        activeButtons.add(button);
    }

    public void registerListener(GamepadTrigger trigger) {
        activeTriggers.add(trigger);
    }

    public void update() {
        for (GamepadButton button : activeButtons) button.update();
        for (GamepadTrigger trigger : activeTriggers) trigger.update();
    }

    public final GamepadButton circle = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().circle; }};
    public final GamepadButton triangle = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().triangle; }};
    public final GamepadButton square = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().square; }};
    public final GamepadButton cross = new GamepadButton(this) {@Override public boolean get(){ return gamepad.get().cross; }};

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
