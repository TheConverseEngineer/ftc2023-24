package org.firstinspires.ftc.teamcode.thundercoreV2.utils;

/** Utility class full of various static methods
 *
 * @author TheConverseEngineer
 * @version 1.0
 */
public class Utils {

    /** This class only has static methods, and, as such, should not be instantiated */
    private Utils() {}

    /** The keyword {@code assert} just crashes the RC app, and, as such, isn't very useful.
     * Use this method instead, as it produces an error message for the Driver Station
     *
     * @param condition         The condition. If this is false, an error will be raised
     * @param errorMessage      The error message that should be raised if the given condition is false.
     *                          Note that error messages will be preceded by the string "SafeAssert failed: "
     */
    public static void safeAssert(boolean condition, String errorMessage) {
        if (!condition) throw new RuntimeException("SafeAssert failed: " + errorMessage);
    }

    /** Returns the number of milliseconds since epoch, backed by {@code System.nanoTime()}
     * Note that the epoch is completely arbitrary, and, as such, this method should only be
     * use to get relative durations.
     *
     * @return      The number of milliseconds since some arbitrary moment in time (known as the epoch)
     */
    public static double getMsTime() {
        return System.nanoTime() / 1e6;
    }

}
