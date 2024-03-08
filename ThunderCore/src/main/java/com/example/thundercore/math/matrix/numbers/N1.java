package com.example.thundercore.math.matrix.numbers;

import com.example.thundercore.math.matrix.Nat;
import com.example.thundercore.math.matrix.Num;

public class N1 extends Num implements Nat<N1> {
    private N1() { }

    @Override
    public int getNum() {
        return 1;
    }

    public static final N1 instance = new N1();
}
