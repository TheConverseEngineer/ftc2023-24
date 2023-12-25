package com.thunder.opensimgui;

import com.thunder.opensimgui.input.SimulationInput;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.app.Application;
import imgui.app.Configuration;

public class SimulationApplication extends Application {

    private static final String VERSION = "1.0";

    private final PanelBase[] panels = new PanelBase[]{
            new MotorStatsPanel(new Point(200, 200)),
            new FieldMapPanel(new Point(400, 400)),
            new OpModeControlPanel(new Point(600, 600))
    };
    private final SimulationLogicHandler handler;


    public SimulationApplication(SimulationLogicHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("OpenSIM FTC Simulator v" + VERSION);
    }

    @Override
    protected void initWindow(Configuration config) {
        super.initWindow(config);

        ImGuiViewport viewport = new ImGuiViewport(handle);
    }

    @Override
    public void process() {
        SimulationInput input = handler.update();

        for (PanelBase panel : panels) panel.show(input);
    }
}