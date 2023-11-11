package com.opensim.panels;

import com.opensim.utils.Point;

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

    public abstract void populateInternal();

    public final void show() {
        ImGui.setNextWindowSize(size.x, size.y, ImGuiCond.Once);
        ImGui.setNextWindowPos(pos.x, pos.y, ImGuiCond.Once);
        if (ImGui.begin(name)) {
            populateInternal();
        }
        ImGui.end();
    }
}
