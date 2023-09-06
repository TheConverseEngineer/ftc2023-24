package com.example.opensimclient;

public interface OpModeManager {

    /** Initializes the OpMode */
    void init();

    /** Starts the OpMode */
    void start();

    /** Ends the OpMode
     * <br>
     * Note that this method might be blocking and will wait for the opmode to end. If you have an infinite loop
     * somewhere, this might not happen, in which case, you will have to manually terminate the test.
     */
    void end();
}
