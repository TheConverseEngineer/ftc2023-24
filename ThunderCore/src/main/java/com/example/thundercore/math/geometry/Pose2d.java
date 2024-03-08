package com.example.thundercore.math.geometry;

import com.example.thundercore.math.dualnum.DualNumber;
import com.example.thundercore.math.dualnum.generics.Parameter;
import com.example.thundercore.math.geometry.generics.Global;
import com.example.thundercore.math.geometry.generics.Position;
import com.example.thundercore.math.geometry.generics.VContext;

/** Simple immutable dataclass representing the position of a robot.
 *
 * <p> Note that the velocity and acceleration are also stored through the use of a dual number
 *
 * @param <P>   The parameterization variable used by the internally-stored dual numbers
 * @param <C>   The context of this pose (local or global)
 *
 * @see com.example.thundercore.math.dualnum.DualNumber
 * @see Vector2d
 */
@SuppressWarnings("unused")
public class Pose2d<P extends Parameter, C extends VContext> {

    private final Vector2d<P, Position, C> position;
    private final DualNumber<P> heading;

    /** Constructs a Pose2d given a position and a heading
     *
     * @param position      The position of the robot
     * @param heading       The heading of the robot
     */
    public Pose2d(Vector2d<P, Position, C> position, DualNumber<P> heading) {
        this.position = position;
        this.heading = heading;
    }

    /** Constructs a Pose2d given a position and a heading
     *
     * @param x             The x-coordinate of the robot
     * @param y             The y-coordinate of the robot
     * @param heading       The heading of the robot
     */
    public Pose2d(DualNumber<P> x, DualNumber<P> y, DualNumber<P> heading) {
        this(new Vector2d<>(x, y), heading);
    }

    public Vector2d<P, Position, C> getPosition() {
        return this.position;
    }

    public DualNumber<P> getHeading() {
        return heading;
    }
}
