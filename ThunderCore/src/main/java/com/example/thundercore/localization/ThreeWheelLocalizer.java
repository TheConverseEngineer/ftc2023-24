package com.example.thundercore.localization;

import com.example.thundercore.filter.LeastSquaresFilter;
import com.example.thundercore.math.dualnum.DualNumber;
import com.example.thundercore.math.dualnum.generics.Time;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class ThreeWheelLocalizer {
    public static double velocityFilterK = 0.3;

    private static class DeadWheel {
        private DcMotorEx encoder;
        private final boolean inverted;
        private static final double IN_PER_TICK = 0.00105691;
        private LeastSquaresFilter filter;
        private DualNumber<Time> lastValue = new DualNumber<>(0,0,0);
        private double lastQueryTime;

        /** Creates a new dead wheel object. Assumes that this dead wheel has already been initialized */
        public DeadWheel(DcMotorEx encoder, boolean inverted) {
            this.encoder = encoder;
            this.inverted = inverted;
            this.filter = new LeastSquaresFilter(velocityFilterK, 3);
            this.lastQueryTime = 0;
        }

        public DualNumber<Time> get() {
            double filteredVelocity = this.filter.filter(encoder.getVelocity()*IN_PER_TICK);
            double deltaTime = System.nanoTime()/1e9 - lastQueryTime;

            if (deltaTime > 0.3) { // Too large a distance to reliably predict acceleration
                lastQueryTime += deltaTime;
                lastValue = new DualNumber<>(encoder.getCurrentPosition()*IN_PER_TICK, filteredVelocity, 0.0);
                return lastValue;
            } else {
                // Predict acceleration
                // TODO: Fix this
                return new DualNumber<>(0,0,0);
            }
        }

    }
}
