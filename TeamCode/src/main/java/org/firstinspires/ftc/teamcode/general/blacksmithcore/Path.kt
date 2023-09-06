package org.firstinspires.ftc.teamcode.general.blacksmithcore

import org.firstinspires.ftc.teamcode.general.thundercore.utils.Utils.clamp

interface Path {
    fun atParameter(u: Double): DifferentiatedVector2d<SplineParameter>

    fun getLength(): Double

    /** Converts from displacement to parameter via binary search.
     *  Time complexity should be log(precision)
     */
    fun getParameterAtDisplacement(d: Double) : Double

    fun getMaxVelAtParameter(u: Double) : Double

    fun getHeadingAtParameter(u: Double) : Double
}

class CompoundPath(private val paths: Array<Path>) : Path {
    private var lengths = DoubleArray(paths.size+1)

    init {
        for (i in 1..paths.size) lengths[i] = lengths[i-1] + paths[i-1].getLength()
    }

    override fun atParameter(u: Double): DifferentiatedVector2d<SplineParameter> {
        val pathToUse = clamp((u*paths.size).toInt(), 0, paths.size-1)
        return paths[pathToUse].atParameter(u*paths.size - pathToUse)
    }

    override fun getMaxVelAtParameter(u: Double): Double {
        val pathToUse = clamp((u*paths.size).toInt(), 0, paths.size-1)
        return paths[pathToUse].getMaxVelAtParameter(u*paths.size - pathToUse)
    }

    override fun getHeadingAtParameter(u: Double): Double {
        val pathToUse = clamp((u*paths.size).toInt(), 0, paths.size-1)
        return paths[pathToUse].getHeadingAtParameter(u*paths.size - pathToUse)
    }

    override fun getLength() = lengths[paths.size]

    override fun getParameterAtDisplacement(d: Double): Double {
        var index = lengths.binarySearch(d)
        if (index < 0) index = -index - 1

        return if (index == 0) 0.0
        else if (index > paths.size) 1.0
        else paths[index-1].getParameterAtDisplacement(d - lengths[index-1])/paths.size + ((index-1).toDouble()/paths.size)
    }

}

class PathSegment(
    private val xPolynomial: Quintic,
    private val yPolynomial: Quintic,
    private val maxVel: Double,
    private val startHeading: Double,
    private val endHeading: Double,
    private val precision: Int = 1000
) : Path {
    private val distanceLookup = DoubleArray(precision + 1)

    init{
        for (i in 1..precision) {
            distanceLookup[i] = distanceLookup[i-1] + atParameter(i.toDouble()/precision).firstDerMagnitude()/precision
        }
    }

    override fun atParameter(u: Double) = DifferentiatedVector2d<SplineParameter>(xPolynomial.evalAt(u), yPolynomial.evalAt(u))

    override fun getLength() = distanceLookup[precision]

    override fun getParameterAtDisplacement(d: Double): Double {
        var index = distanceLookup.binarySearch(d)
        if (index < 0) index = -index - 1

        return if (index == 0) 0.0
        else if (index >= precision) 1.0
        else (index-1).toDouble()/precision + ((d-distanceLookup[index-1])/(distanceLookup[index]-distanceLookup[index-1]))/precision
    }

    override fun getMaxVelAtParameter(u: Double) = maxVel

    override fun getHeadingAtParameter(u: Double) = startHeading + (endHeading-startHeading)*u
}

data class Quintic(val a: Double, val b: Double, val c: Double, val d: Double, val e: Double, val f: Double) {
    fun evalAt(s: Double) = Variable<SplineParameter>(
        ((((a*s + b)*s + c)*s + d)*s + e)*s + f,
        (((5*a*s + 4*b)*s + 3*c)*s + 2*d)*s + e,
        ((20*a*s + 12*b)*s + 6*c)*s + 2*d
    )

    companion object {
        /** Returns a quintic polynomial constructed from the inputted control points */
        fun generateFromControlPoints(xi: Double, xf: Double, vi: Double, vf: Double, ai: Double, af: Double): Quintic {
            return Quintic(
                (af/2) - (ai/2) - 3*vf - 3*vi + 6*xf -6*xi,
                -af + (3*ai/2) + 7*vf + 8*vi - 15*xf + 15*xi,
                (af/2) - (3*ai/2) - 4*vf - 6*vi + 10*xf - 10*xi,
                ai/2,
                vi,
                xi
            )
        }
    }
}


