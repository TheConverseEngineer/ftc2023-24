package org.firstinspires.ftc.teamcode.blacksmithcore

/** Represents a Quintic Polynomial, where parameters a-f represent the coefficients of this polynomial
 *
 * Consider using the fitPolynomial method instead of declaring the coefficients directly
 */
class QuinticPolynomial(
        private val a: Double,
        private val b: Double,
        private val c: Double,
        private val d: Double,
        private val e: Double,
        private val f: Double
    ) {

    /** Returns the value of this polynomial at a given instance
     *
     * @param t     the spline parameter (should be between 0 and 1)
     * @param dt    the velocity (as a normalized value)
     * @param ddt   the acceleration (as a normalized value)
     * @return      the value of the polynomial (as a dual number)
     */
    operator fun invoke(t: Double, dt: Double, ddt: Double) = DualNum(
            ((((a*t + b)*t + c)*t + d)*t + e)*t + f,
            ((((5*a*t+4*b)*t + 3*c)*t + 2*d)*t + e) * dt,
            (((20*a*t + 12*b)*t + 6*c)*t + 2*d) * dt + ((((5*a*t+4*b)*t + 3*c)*t + 2*d)*t + e)*ddt
    )

    companion object {
        /** Creates a quintic hermite polynomial
         *
         * @param xi    the initial value
         * @param xf    the final value
         * @param vi    the initial first derivative
         * @param vf    the final first derivative
         * @param ai    the initial second derivative
         * @param af    the final second derivative
         */
        @JvmStatic
        fun fitPolynomial(xi: Double, xf: Double, vi: Double, vf: Double, ai: Double, af: Double) : QuinticPolynomial {
            return QuinticPolynomial(
                    af/2 - ai/2 - 3*vf - 3*vi + 6*xf - 6*xi,
                    -af + 3*ai/2 + 7*vf + 8*vi - 15*xf + 15*xi,
                    af/2 - 3*ai/2 - 4*vf - 6*vi + 10*xf - 10*xi,
                    ai/2,
                    vi,
                    xi
            )
        }
    }
}

/** Represents a Quintic Spline path (parameterized by displacement)
 *
 * @param xPolynomial   the polynomial that represents the x component
 * @param yPolynomial   the polynomial that represents the y component
 * @param res           the resolution at which re-parametrization should be handled
 */
class QuinticSpline(val xPolynomial: QuinticPolynomial, val yPolynomial: QuinticPolynomial, private val res: Int = 100) {
    val splineDistance: Double
    private val steps = DoubleArray(res+1) {0.0}

    init { // Approximate the length of the curve with riemann sums
        steps[0] = 0.0
        for (i in 1..res) {
            steps[i] = steps[i-1] + (1.0/res) * Math.sqrt(with( getRaw((i-0.5)/res, 1.0, 0.0) ){ x.dx*x.dx + y.dx*y.dx })
        }
        splineDistance = steps[res]
    }

    private fun getRaw(u: Double, du: Double, ddu: Double) = Vector2dDual(xPolynomial(u, du, ddu), yPolynomial(u, du, ddu))

    /** Returns the state of the robot at a given position
     *
     * @param dist  the current displacement of the robot (as a Dual Number)
     * @return      the current desired state of the robot (as a Vector2d)
     */
    operator fun invoke(dist: DualNum):  Vector2dDual {
        if (dist.x >= splineDistance) return getRaw(1.0, 0.0, 0.0)
        else if (dist.x <= 0.0) return getRaw(0.0, 0.0, 0.0)

        // Binary search for the first greater step
        var lo = -1
        var hi = res
        while (lo < hi) {
            val mid = lo + (hi-lo+1)/2;
            if (steps[mid] < dist.x) lo = mid
            else hi = mid - 1
        }

        ((steps[lo+1]-steps[lo])*res).let {
            return getRaw(lo.toDouble()/res + (dist.x-steps[lo])/it, dist.dx/it, dist.ddx/it)
        }
    }
}

class Path (
    private val splines: Array<QuinticSpline>,
    private val headings: Array<Double>
) {

    /* PRELIMINARY CHECKS */
    init {
        if (headings.size - 1 != splines.size) throw RuntimeException("Tried to create a trajectory with ${headings.size} headings but ${splines.size} splines")
    }

    /* CONSTRUCT PIECEWISE */
    private val endpoints = DoubleArray(splines.size+1) {0.0}
    init { // Determine endpoint lengths for all splines
        for (i in 1..(splines.size)) endpoints[i] = endpoints[i-1] + splines[i-1].splineDistance
    }

    /** Returns the robot state at a given displacement
     *
     *  @param disp     the displacement
     *  @return         the target robot state at displacement disp
     */
    operator fun invoke(disp: DualNum) : Pose2dDual {
        for (i in 1 until endpoints.size) {
            if (endpoints[i] >= disp.x)
                return Pose2dDual.createFromVector(
                    splines[i-1](disp - endpoints[i-1]),
                    ((disp-endpoints[i-1]) * DualNum((headings[i]-headings[i-1])/(endpoints[i]-endpoints[i-1]), 0.0, 0.0)) + headings[i-1]
                )
        }
        return Pose2dDual.createFromVector(
            splines.last()(disp-endpoints[endpoints.size-2]),
            DualNum.constant(headings.last())
        )
    }

    fun length() = endpoints.last();
}
