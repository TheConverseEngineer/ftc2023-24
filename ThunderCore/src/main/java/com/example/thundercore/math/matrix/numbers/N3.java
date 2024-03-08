package com.example.thundercore.math.matrix.numbers;

import com.example.thundercore.math.matrix.Nat;
import com.example.thundercore.math.matrix.Num;

public class N3 extends Num implements Nat<N3> {
    private N3() { }

    @Override
    public int getNum() {
        return 3;
    }

    public static final N3 instance = new N3();
}

