package com.thunder.opensimgui;

import com.thunder.opensimgui.input.SimulationInput;

import imgui.ImGui;
import imgui.flag.ImGuiCond;

public abstract class PanelBase {

    private final Point size, pos;
    private final String name;

    public PanelBase(Point size, Point pos, String name) {
        this.size = size;
        this.pos = pos;
        this.name = name;
    }

    public abstract void populateInternal(SimulationInput input, SimulationLogicHandler handler);

    public final void show(SimulationInput input, SimulationLogicHandler handler) {
        ImGui.setNextWindowSize(size.x, size.y, ImGuiCond.Once);
        ImGui.setNextWindowPos(pos.x, pos.y, ImGuiCond.Once);
        if (ImGui.begin(name)) {
            populateInternal(input, handler);
        }
        ImGui.end();
    }
}