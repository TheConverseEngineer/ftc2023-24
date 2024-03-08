package org.firstinspires.ftc.teamcode.common.gvf;

/** Represents some form of number outside of a double.
 * <br>
 * Unit implements standard operations (+, -, *, /)
 * */
public abstract class Unit {
    public abstract Unit plus(Unit other);
    public abstract Unit minus(Unit other);
    public abstract Unit times(Unit other);
    public abstract Unit div(Unit other);


    public abstract Unit plus(double other);
    public abstract Unit minus(double other);
    public abstract Unit times(double other);
    public abstract Unit div(double other);
}
