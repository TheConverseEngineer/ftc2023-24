package org.firstinspires.ftc.teamcode.extensions;

import static com.arcrobotics.ftclib.util.MathUtils.clamp;

import static java.lang.Math.signum;

import androidx.annotation.NonNull;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/** Extension of FTCLib's {@link MotorEx} that supports caching to optimize hardware write speed
 * and a built-in feedforward controller */
public class CachedMotor extends MotorEx {
    private double cachedPowerCommand = 0;
    private double kStatic = 0, kV = 0;

    public CachedMotor(@NonNull HardwareMap hMap, String id) {
        super(hMap, id);
    }

    public CachedMotor(@NonNull HardwareMap hMap, String id, @NonNull GoBILDA gobildaType) {
        super(hMap, id, gobildaType);
    }

    public CachedMotor(@NonNull HardwareMap hMap, String id, double cpr, double rpm) {
        super(hMap, id, cpr, rpm);
    }

    public CachedMotor setFFParams(double kV, double kStatic) {
        this.kV = kV;
        this.kStatic = kStatic;
        return this;
    }

    public void setFromFeedforward(double vel) {
        set(clamp(kStatic*sgNum(vel) + kV*vel, -1, 1));
    }

    private double sgNum(double x) {
        return (x>0.01?1:(x<-0.01?-1:0));
    }

    @Override
    public void set(double power) {
        if (Math.abs(power - cachedPowerCommand) <= 0.01) return; // Change in motor power is negligible

        cachedPowerCommand = power;
        super.set(power);
    }

}
