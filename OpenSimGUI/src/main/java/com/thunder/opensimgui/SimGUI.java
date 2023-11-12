package com.thunder.opensimgui;

import static imgui.app.Application.launch;

public class SimGUI {

    public static void launchSimGUI(SimulationLogicHandler handler){
        launch(new SimulationApplication(handler));
    }
}
