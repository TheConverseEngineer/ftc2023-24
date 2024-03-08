package com.example.thundercore.hardware;

import java.util.ArrayList;

public interface ThunderMap {

    /** Returns a new thunder motor instance.
     *
     * <p> Note that the returned motor will have its encoder reset before being returned.
     *
     * @param name          The name of this motor, as it appears in the hardware map
     * @param reversed      If true, this motor will be reversed
     * @param shouldBrake   If true, this motor will brake when receiving zero power.
     *                      If false, then this motor will float.
     */
    ThunderMotor getMotor(String name, boolean reversed, boolean shouldBrake);

    /** Returns a new thunder motor instance with the following defaults
     *
     * <p> Note that the returned motor will have its encoder reset before being returned.
     *
     * <ul>
     *     <li>brake on zero power -> true </li>
     * </ul>
     *
     * @param name          The name of this motor, as it appears in the hardware map
     * @param reversed      If true, this motor will be reversed
     */
    default ThunderMotor getMotor(String name, boolean reversed) {
        return getMotor(name, reversed, true);
    }

    /** Returns a new thunder motor instance with the following defaults
     *
     * <p> Note that the returned motor will have its encoder reset before being returned.
     *
     * <ul>
     *     <li>brake on zero power -> true </li>
     *     <li>reversed            -> false </li>
     * </ul>
     *
     * @param name          The name of this motor, as it appears in the hardware map
     */
    default ThunderMotor getMotor(String name) {
        return getMotor(name, false, true);
    }
}
