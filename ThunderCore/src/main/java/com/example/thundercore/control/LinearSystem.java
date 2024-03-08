package com.example.thundercore.control;

import com.example.thundercore.math.matrix.Matrix;
import com.example.thundercore.math.matrix.Num;

/** Models a linear system using state-space notation.
 *
 * <p> Models the following equations:
 * <ul>
 *     <li>x' = Ax + Bu </li>
 *     <li>y  = Cx + Du</li>
 *     <br>
 *     <li> x -> current state </li>
 *     <li> u -> system input </li>
 *     <li> y -> system output </li>
 * </ul>
 *
 * @param <States>      number of state
 * @param <Inputs>      number of inputs
 * @param <Outputs>     number of outputs
 */
public class LinearSystem<States extends Num, Inputs extends Num, Outputs extends Num> {

    private final Matrix<States, States> A;
    private final Matrix<States, Inputs> B;
    private final Matrix<Outputs, States> C;
    private final Matrix<Outputs, Inputs> D;

    public LinearSystem(Matrix<States, States> a, Matrix<States, Inputs> b, Matrix<Outputs, States> c, Matrix<Outputs, Inputs> d) {
        a.assertFinite();
        b.assertFinite();
        c.assertFinite();
        d.assertFinite();

        A = a;
        B = b;
        C = c;
        D = d;
    }
}
