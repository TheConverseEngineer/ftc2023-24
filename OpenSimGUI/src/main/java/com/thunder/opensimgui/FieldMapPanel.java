package com.thunder.opensimgui;


import com.thunder.opensimgui.input.SimulationInput;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.ImPlotLimits;
import imgui.flag.ImGuiCond;

public class FieldMapPanel extends PanelBase {


    public FieldMapPanel(Point pos) {
        super(new Point(550, 550), pos, "Field Map");

        ImPlot.createContext();
    }

    @Override
    public void populateInternal(SimulationInput input) {
        ImPlot.setNextPlotLimitsX(-75, 75, ImGuiCond.None);
        ImPlot.setNextPlotLimitsY(-75, 75, ImGuiCond.None);
        if (ImPlot.beginPlot("Field", "Driver Station Side", "Backdrop Side", new ImVec2(525, 525))) {
            drawRobot(0, 0, 12, 20, Math.PI*4/3);
            ImPlot.endPlot();
        }
    }

    /** Note that heading should be in radians */
    private void drawRobot(double x, double y, double width, double length, double heading) {
        double r = Math.hypot(width/2, length/2);
        double angle = Math.atan2(length, width);
        heading += Math.PI/2;
        ImPlot.plotLine("", // Robot body
            new Double[]{x + r*Math.cos(angle + heading), x + r*Math.cos(heading + Math.PI - angle), x + r*Math.cos(heading + Math.PI + angle), x + r*Math.cos(heading - angle), x + r*Math.cos(angle + heading)},
            new Double[]{y + r*Math.sin(angle + heading), y + r*Math.sin(heading + Math.PI - angle), y + r*Math.sin(heading + Math.PI + angle), y + r*Math.sin(heading - angle), y + r*Math.sin(angle + heading)}
        );
        ImPlot.plotLine("", // Line to signify forward
            new Double[]{x, x + length*Math.cos(heading - Math.PI/2)/2},
            new Double[]{y, y + length*Math.sin(heading - Math.PI/2)/2}
        );
    }
}
