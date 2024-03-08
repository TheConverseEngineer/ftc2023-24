package com.example.thundercore.math.matrix.numbers;

import com.example.thundercore.math.matrix.Nat;
import com.example.thundercore.math.matrix.Num;

public class N2 extends Num implements Nat<N2> {
    private N2() { }

    @Override
    public int getNum() {
        return 2;
    }

    public static final N2 instance = new N2();
}
