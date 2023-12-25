package com.thunder.opensimgui;

import com.thunder.opensimgui.input.SimulationInput;

public interface SimulationLogicHandler {

    /** When called, this method should return the most recent hardware stats. This method will be called automatically */
    SimulationInput update();

    /** Called when the user hits init */
    void init();

    /** Called when the user hits stop */
    void stop();

    /** Called when the user hits start */
    void start();
}
