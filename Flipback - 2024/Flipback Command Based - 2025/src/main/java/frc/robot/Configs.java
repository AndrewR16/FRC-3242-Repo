package frc.robot;

import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

public final class Configs {
    public static final SparkMaxConfig standardConfig = new SparkMaxConfig();
    public static final SparkMaxConfig invertedConfig = new SparkMaxConfig();

    public static final SparkMaxConfig shooterConfig = new SparkMaxConfig();

    static {
        standardConfig
            .idleMode(IdleMode.kBrake)
            .inverted(false);

        invertedConfig
            .idleMode(IdleMode.kBrake)
            .inverted(true);

        shooterConfig
            .idleMode(IdleMode.kCoast)
            .inverted(false);
    }
}
