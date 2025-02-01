package frc.robot;

import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

public final class Configs {
    public static final SparkMaxConfig drivingConfig = new SparkMaxConfig();
    public static final SparkMaxConfig invertedDrivingConfig = new SparkMaxConfig();

    static {
        drivingConfig
            .idleMode(IdleMode.kBrake)
            .inverted(false);

        invertedDrivingConfig
            .idleMode(IdleMode.kBrake)
            .inverted(true);
    }
}
