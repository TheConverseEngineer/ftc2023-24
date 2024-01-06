package com.thunder.opensimgui;

import com.thunder.opensimgui.input.SimulationInput;

import imgui.ImGui;

public class OpModeControlPanel extends PanelBase {

    public enum OpModeState { STOPPED, INITIATED, RUNNING }

    private OpModeState currentState = OpModeState.STOPPED;

    private long lastStartTimeMs = 0;

    public OpModeControlPanel(Point pos) {
        super(new Point(200, 200), pos, "OpMode Control Panel");
    }

    @SuppressWarnings("DefaultLocale")
    @Override
    public void populateInternal(SimulationInput input, SimulationLogicHandler handler) {
        ImGui.text("Current state: " + currentState.name());
        if (currentState == OpModeState.STOPPED) {
            if (ImGui.button("Initialize")) {
                currentState = OpModeState.INITIATED;
                handler.init();
            }

        } else if (currentState == OpModeState.INITIATED) {
            if (ImGui.button("Start")) {
                currentState = OpModeState.RUNNING;
                lastStartTimeMs = System.currentTimeMillis();
                handler.start();
            }

            ImGui.sameLine();
            if (ImGui.button("Stop")) {
                currentState = OpModeState.STOPPED;
                handler.stop();
            }

        } else {
            if (ImGui.button("Stop")) {
                currentState = OpModeState.STOPPED;
                handler.stop();
            }
            ImGui.text(String.format("Runtime: %,.3f", (System.currentTimeMillis() - lastStartTimeMs)/1000d));
        }
    }
}
