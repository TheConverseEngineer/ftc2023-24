package com.example.thundercore.math.matrix;

/** Replacement for the c-style usage of variables as generics.
 * Based off of wpilib's Matrix implementation
 */
public interface Nat<T extends Num> {
    /** Returns the literal number represented by this interface */
    int getNum();
}
