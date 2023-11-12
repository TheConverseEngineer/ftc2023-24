package com.thunder.opensimgui;

import com.thunder.opensimgui.input.MotorInputEntry;
import com.thunder.opensimgui.input.ServoInputEntry;
import com.thunder.opensimgui.input.SimulationInput;

import imgui.ImGui;
import imgui.flag.ImGuiSliderFlags;

public class MotorStatsPanel extends PanelBase {
    public MotorStatsPanel(Point pos) {
        super(new Point(400, 800), pos, "Motors and Servos");
    }

    @Override
    @SuppressWarnings("DefaultLocale")
    public void populateInternal(SimulationInput input) {

        if (ImGui.collapsingHeader("Motors")) {
            int motorCount = 0;
            for (MotorInputEntry motor : input.motors) {
                ImGui.text("Motor " + (motorCount++) + ": " + motor.name);
                ImGui.sliderFloat("Position: " + motor.currentEncoderValue, new float[]{(float)motor.power}, -1, 1, String.format("%4.3f", motor.power), ImGuiSliderFlags.NoInput);
            }
        }

        if (ImGui.collapsingHeader("Servos")) {
            int servoCount = 0;
            for (ServoInputEntry servo : input.servos) {
                ImGui.text("Servo " + (servoCount++) + ": " + servo.name);
                ImGui.sliderFloat(" ", new float[]{(float)servo.pos}, 0, 1, String.format("%4.3f", servo.pos), ImGuiSliderFlags.NoInput);
            }
        }
    }
}
