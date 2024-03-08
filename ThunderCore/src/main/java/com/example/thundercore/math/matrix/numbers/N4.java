package com.example.thundercore.math.matrix.numbers;

import com.example.thundercore.math.matrix.Nat;
import com.example.thundercore.math.matrix.Num;

public class N4 extends Num implements Nat<N4> {
    private N4() { }

    @Override
    public int getNum() {
        return 4;
    }

    public static final N4 instance = new N4();
}

