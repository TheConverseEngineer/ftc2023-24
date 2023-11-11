package com.opensim;

import com.opensim.panels.MotorStatsPanel;

import imgui.ImGuiViewport;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.ImGui;

public class SimulationApplication extends Application {

    //MotorStatsPanel statsPanel = new MotorStatsPanel();

    @Override
    protected void configure(Configuration config) {
        config.setTitle("OpenSIM FTC Simulator v" + Constants.version);
    }

    @Override
    protected void initWindow(Configuration config) {
        super.initWindow(config);

        ImGuiViewport viewport = new ImGuiViewport(handle);
    }

    @Override
    public void process() {
        ImGui.text("Hello, World!");
        ImGui.button("press me!");

        //statsPanel.show();
    }
}
