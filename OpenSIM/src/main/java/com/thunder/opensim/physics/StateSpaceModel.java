package com.thunder.opensim.physics;

/** A slightly-modified state space model with additional support for a feedforward term
 *
 * the state consists of [x, dx].
 *
 * */
public abstract class StateSpaceModel {

    private final DualMatrix currentState = new DualMatrix(0,0);

    private final QuadMatrix A;
    private final DualMatrix B;
    private final DualMatrix feedForwardMatrix = new DualMatrix(0, 1);

    private double lastRecordedAccel = 0;

    protected String[] motors = new String[0];

    public StateSpaceModel(QuadMatrix A, DualMatrix B) {
        this.A = A;
        this.B = B;
    }

    public void assignMotors(String[] names) {
        this.motors = names;
    }

    protected abstract double getAccelFeedforward();
    protected abstract double getInput();

    /** Updates the current state
     *
     * @param dTime the time since the last update (in milliseconds!)
     * */
    public void update(double dTime) {
        // Convert dTime to seconds
        dTime /= 1000;

        DualMatrix dX = DualMatrix.sum(A.times(currentState), B.times(getInput()), feedForwardMatrix.times(getAccelFeedforward()));

        // Assume constant acceleration (good enough if this method is called frequently)
        currentState.set(currentState.a + dX.a*dTime + 0.5*dX.b*dTime*dTime, dX.a);
        lastRecordedAccel = dX.b;
    }

    protected double getPosition() {
        return currentState.a;
    }

    protected double getVelocity() {
        return currentState.b;
    }

    protected double getAcceleration() {
        return lastRecordedAccel;
    }

    /** Immutable matrix data-container that represents the 2x2 matrix [[a b] [c d]] */
    public static class QuadMatrix {
        public final double a, b, c, d;

        public QuadMatrix(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
        /** Multiplies this matrix by a 2x1 matrix and returns a new object with the result. */
        public DualMatrix times(DualMatrix other) {
            return new DualMatrix(a*other.a + b*other.b, c*other.a + d*other.b);
        }
    }

    /** Mutable matrix data-container that represents the 2x1 matrix [[a] [b]] */
    public static class DualMatrix {
        public double a, b;

        public DualMatrix(double a, double b) {
            this.a = a;
            this.b = b;
        }

        /** Sets the values of this matrix */
        public void set(double a, double b) {
            this.a = a;
            this.b = b;
        }

        /** Multiplies this matrix by a scalar and returns a new object with the result */
        public DualMatrix times(double other) {
            return new DualMatrix(a*other, b*other);
        }

        /** Returns the sum of three 2x1 matrices */
        public static DualMatrix sum(DualMatrix a, DualMatrix b, DualMatrix c) {
            return new DualMatrix(a.a + b.a + c.a, a.b + b.b + c.b);
        }
    }
}
