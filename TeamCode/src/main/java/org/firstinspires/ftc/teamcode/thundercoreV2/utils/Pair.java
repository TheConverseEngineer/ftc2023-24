package org.firstinspires.ftc.teamcode.thundercoreV2.utils;

import java.util.Objects;

/** Simple implementation of an immutable pair of items, inspired by c++'s std::pair
 *
 * @version 1.0
 * @author TheConverseEngineer
 * */
public class Pair<F, S> {
    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "{" + first + ", " + second + "}";
    }
}
