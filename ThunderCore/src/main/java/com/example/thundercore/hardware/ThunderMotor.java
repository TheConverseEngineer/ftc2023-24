package com.example.thundercore.hardware;

/** Wrapper class for DcMotorEx that adds support for caching, among other things */
public interface ThunderMotor {

    /** Directly sets the power of this motor
     *
     * @param power     the power of this motor (between -1 and 1)
     */
    void setPower(double power);

    /** Sets the "voltage" received by this motor
     *
     * <p> 12 volts is generally considered to be a safe maximum, but excessive battery usage
     * may cause the battery to drop below that amount.
     *
     * <p> Note that motors are technically controlled through PWM, not voltage. However, treating this
     * as a setVoltage command is close enough for virtually all purposes.
     */
    void setVoltage(double voltage);

    /** Returns the current position of this motor in ticks */
    int getCurrentPosition();

    /** Returns the current velocity of this motor in ticks/sec */
    double getRawVelocity();

    /** Returns the filtered velocity of this motor in ticks/sec */
    double getCorrectedVelocity();

    /** Internal method that is called whenever new sensor data is available.
     * You should never need to call this method directly.
     *
     * @param dT        time (in seconds) since the last update, or negative if this is the first update
     * @param voltage   the latest-recorded battery voltage
     */
    void updateInternal(double dT, double voltage);
}
