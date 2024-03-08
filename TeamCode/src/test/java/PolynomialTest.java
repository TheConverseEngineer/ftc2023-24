import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import org.firstinspires.ftc.teamcode.common.gvf.Polynomial;
import org.junit.Test;

import java.util.Random;

public class PolynomialTest {
    @Test
    public void verifyPolynomialEval() {
        for (int t = 0; t < 40; t++) {
            double[] coeffs = genRandomDoubleArray(5);
            double x = Math.random()*100;

            double a = 0;
            for (int i = 0; i < 5; i++) {
                a += coeffs[i] * Math.pow(x, 4-i);
            }

            assertTrue(Math.abs(a - new Polynomial(coeffs).eval(x)) < 0.00001);
        }
    }

    @Test
    public void verifyPolynomialSquare() {
        for (int t = 0; t < 40; t++) {
            Polynomial p = genRandomPolynomial(6);
            Polynomial p2 = p.getSquared();
            for (int i = 0; i < 30; i++) {
                double x = Math.random();
                assertEquals(p.eval(x)*p.eval(x), p2.eval(x), 0.00001);
            }
        }
    }

    @Test
    public void verifyPolynomialSum() {
        for (int t = 0; t < 40; t++) {
            Polynomial p1 = genRandomPolynomial(6);
            Polynomial p2 = genRandomPolynomial(6);
            Polynomial s = p1.plus(p2);
            for (int i = 0; i < 30; i++) {
                double x = Math.random();
                assertEquals(p1.eval(x) + p2.eval(x), s.eval(x), 0.00001);
            }
        }
    }

    @Test
    public void verifyPolynomialDerivative() {
        for (int t = 0; t < 40; t++) {
            Polynomial p = genRandomPolynomial(6);
            Polynomial d = p.getDerivative();
            for (int i = 0; i < 30; i++) {
                double x = Math.random();
                assertEquals(d.eval(x), (p.eval(x+.000000001)-p.eval(x-.000000001))/.000000002, 0.001);
            }
        }
    }

    private static double[] genRandomDoubleArray(int size) {
        Random random = new Random();
        double[] r = new double[size];
        for (int i = 0; i < size; i++) r[i] = random.nextDouble()*100;
        return r;
    }

    private static Polynomial genRandomPolynomial(int size) {
        return new Polynomial(genRandomDoubleArray(size));
    }
}
