package com.example.thundercore.math.geometry;

import com.example.thundercore.math.dualnum.DualNumber;
import com.example.thundercore.math.dualnum.generics.Parameter;
import com.example.thundercore.math.geometry.generics.Global;
import com.example.thundercore.math.geometry.generics.Local;
import com.example.thundercore.math.geometry.generics.Unit;
import com.example.thundercore.math.geometry.generics.VContext;
import com.example.thundercore.math.geometry.generics.VType;

/** Simple immutable dataclass representing a DualNumber-based x and y component
 *
 * @param <P>   The parameterization variable used by the internally-stored dual numbers
 * @param <T>   The type of this vector (unit, position, velocity, etc.)
 * @param <C>   The context of this vector (local or global)
 *
 * @see com.example.thundercore.math.dualnum.DualNumber
 */
@SuppressWarnings("unused")
public class Vector2d<P extends Parameter, T extends VType, C extends VContext> {
    private final DualNumber<P> x, y;

    /** Creates a new Vector2d with x and y components */
    public Vector2d(DualNumber<P> x, DualNumber<P> y){
        this.x = x;
        this.y = y;
    }

    /** Returns the x component of this vector */
    public DualNumber<P> getX() {
        return x;
    }

    /** Returns the y component of this vector */
    public DualNumber<P> getY() {
        return y;
    }

    /** Returns the sum of two vectors */
    public Vector2d<P, T, C> add (Vector2d<P, ? extends T,C> other) {
        return new Vector2d<>(this.x .plus (other.x), this.y .plus (other.y));
    }

    /** Returns the difference of two vectors */
    public Vector2d<P, T, C> subtract (Vector2d<P, ? extends T,C> other) {
        return new Vector2d<>(this.x .minus (other.x), this.y .minus (other.y));
    }

    /** Returns the product of this vector and a scalar */
    public Vector2d<P, T, C> multiply (DualNumber<P> other) {
        return new Vector2d<>(this.x .times (other), this.y .times (other));
    }

    /** Returns the quotient of this vector and a scalar */
    public Vector2d<P, T, C> divide (DualNumber<P> other) {
        return new Vector2d<>(this.x .div (other), this.y .div (other));
    }

    /** Returns the magnitude squared of this vector
     *
     * <p> Note that the derivatives of the resultant dual numbers are <u>not</u> the
     * magnitudes of the derivatives, but rather the rate of change of the magnitude
     */
    public DualNumber<P> getMagnitudeSquared() {
        return this.x.square() .plus (this.y.square());
    }

    /** Returns the magnitude of this vector
     *
     * <p> Note that the derivatives of the resultant dual numbers are <u>not</u> the
     * magnitudes of the derivatives, but rather the rate of change of the magnitude
     */
    public DualNumber<P> getMagnitude() {
        return getMagnitudeSquared().sqrt();
    }

    /** Returns the dot product of two vectors */
    public DualNumber<P> dot (Vector2d<P, ? extends VType, ? extends VContext> other) {
        return (this.x .times (other.x)) .plus (this.y .times (other.y));
    }


    /** Returns the "cross product" of two vectors.
     *
     * <p> For the purpose of this class, the cross product is defined as the orthogonal projection
     */
    public DualNumber<P> cross (Vector2d<P, ? extends VType, ? extends VContext> other) {
        return (this.x .times (other.y)) .minus (this.y .times (other.x));
    }

    /** Changes the generic parameter type of this vector without changing its value
     *
     * <p> Do not use this method unless you know what you are doing
     */
    public <_T extends VType, _C extends VContext> Vector2d<P, _T, _C> reparameterize() {
        return new Vector2d<>(x, y);
    }

    /** Constructs a new vector representing the derivative of this one. Note that
     * acceleration data on the new vector will be lost due to {@link DualNumber} only storing two derivatives
     *
     * @see DualNumber#differentiate
     */
    public Vector2d<P, T, C> differentiate() {
        return new Vector2d<>(x.differentiate(), y.differentiate());
    }

    /** Calculates the distance between this and another vector. To get the distance between the derivatives,
     * subtract and use the magnitude command instead
     */
    public double distTo(Vector2d<? extends Parameter, T, C> other) {
        return Math.hypot(x.get() - other.x.get(), y.get() - other.y.get());
    }

    /** Returns a unit vector that points in the given direction
     *
     * <p> Note that both derivatives will also be unit vectors angled in the desired direction
     *
     * @param angle     the desired angle (in radians)
     * */
    public static <_P extends Parameter> Vector2d<_P, Unit, Global> fromAngle(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Vector2d<>(
                new DualNumber<>(c, c, c),
                new DualNumber<>(s, s, s)
        );
    }

    /** Given a global vector and the current heading of the robot, returns a vector in the local
     * frame of the robot
     *
     * @param global    the global vector
     * @param heading   the robot's heading (in radians)
     * */
    public static <_P extends Parameter, _T extends VType> Vector2d<_P, _T, Local> toLocalContext(
            Vector2d<_P, _T, Global> global, double heading
    ) {
        Vector2d<_P, Unit, Global> angleVector = fromAngle(heading);
        return new Vector2d<>(
            global.dot(angleVector),
            global.cross(angleVector)
        );
    }

    /** Given a local vector and the current heading of the robot, returns a vector in the global
     * field frame
     *
     * @param local    the local vector
     * @param heading   the robot's heading (in radians)
     * */
    public static <_P extends Parameter, _T extends VType> Vector2d<_P, _T, Global> toGlobalContext(
            Vector2d<_P, _T, Local> local, double heading
    ) {
        Vector2d<_P, Unit, Global> angleVector = fromAngle(-heading);
        return new Vector2d<>(
                local.dot(angleVector),
                local.cross(angleVector)
        );
    }

    /** Converts a vector to a unit vector
     *
     * <p> Note that the derivatives of each component will represent the rate of change of the unit vector
     * and may not be unit vectors themselves
     */
    public static <_P extends Parameter, _T extends VType, _C extends VContext> Vector2d<_P, Unit, _C> toUnitVector(Vector2d<_P, _T, _C> v) {
        return v.divide(v.getMagnitude()).reparameterize();
    }
}
