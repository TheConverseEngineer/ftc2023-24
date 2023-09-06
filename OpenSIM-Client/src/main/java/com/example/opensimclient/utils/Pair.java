package com.example.opensimclient.utils;

import androidx.annotation.NonNull;

import java.util.Objects;

/** Simple utility class representing a pair of objects.
 * Based off of the c++stdlib class by the same name.
 *
 * Equality and Hashing is done by directly comparing both elements
 */
public class Pair<F, S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @NonNull
    @Override
    public String toString() {
        return "{" + first + ", " + second + '}';
    }
}
