package frc.robot;

import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.ModuleConstants;

public final class Configs {
    public static final class MAXSwerveModule {
        public static final SparkMaxConfig drivingConfig = new SparkMaxConfig();
        public static final SparkMaxConfig turningConfig = new SparkMaxConfig();

        static {
            // Use module constants to calculate conversion factors and feed forward gain.
            double drivingFactor = ModuleConstants.kWheelDiameterMeters * Math.PI
                    / ModuleConstants.kDrivingMotorReduction;
            double turningFactor = 2 * Math.PI;
            double drivingVelocityFeedForward = 1 / ModuleConstants.kDriveWheelFreeSpeedRps;

            drivingConfig
                    .idleMode(IdleMode.kCoast)
                    .smartCurrentLimit(50);
            drivingConfig.encoder
                    .positionConversionFactor(drivingFactor) // meters
                    .velocityConversionFactor(drivingFactor / 60.0); // meters per second
            drivingConfig.closedLoop
                    .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                    // These are example gains you may need to them for your own robot!
                    .pid(0.04, 0, 0)
                    .velocityFF(drivingVelocityFeedForward)
                    .outputRange(-1, 1);

            turningConfig
                    .idleMode(IdleMode.kCoast)
                    .smartCurrentLimit(20);
            turningConfig.absoluteEncoder
                    // Invert the turning encoder, since the output shaft rotates in the opposite
                    // direction of the steering motor in the MAXSwerve Module.
                    .inverted(true)
                    .positionConversionFactor(turningFactor) // radians
                    .velocityConversionFactor(turningFactor / 60.0); // radians per second
            turningConfig.closedLoop
                    .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
                    // These are example gains you may need to them for your own robot!
                    .pid(1, 0, 0)
                    .outputRange(-1, 1)
                    // Enable PID wrap around for the turning motor. This will allow the PID
                    // controller to go through 0 to get to the setpoint i.e. going from 350 degrees
                    // to 10 degrees will go through 0 rather than the other direction which is a
                    // longer route.
                    .positionWrappingEnabled(true)
                    .positionWrappingInputRange(0, turningFactor);

                }
            }
            
    public static final class Elevator {
        public static final SparkMaxConfig liftConfig = new SparkMaxConfig();
        public static final SparkMaxConfig gantryConfig = new SparkMaxConfig();
        
        static {
            // TODO: Adjust conversion factors
            double liftFactor = 1 / ElevatorConstants.kLiftMotorReduction;
            double gantryFactor = 1 / ElevatorConstants.kGantryMotorReduction;

            // TODO: Adjust pid values
            liftConfig
                .inverted(true)
                .idleMode(IdleMode.kBrake);
            liftConfig.encoder
                .countsPerRevolution(12)
                .positionConversionFactor(liftFactor)
                .velocityConversionFactor(liftFactor / 60.0);
            liftConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(0.1, 0, 0)
                .outputRange(-1, 1);
            liftConfig.signals
                .primaryEncoderPositionAlwaysOn(true)
                .primaryEncoderVelocityAlwaysOn(true);

            gantryConfig
                .idleMode(IdleMode.kBrake);
            gantryConfig.encoder
                .inverted(true)
                .countsPerRevolution(12)
                .positionConversionFactor(gantryFactor)
                .velocityConversionFactor(gantryFactor / 60.0);
            gantryConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(1.8548, 0, 0)
                .outputRange(-1, 1);
            gantryConfig.signals
                .primaryEncoderPositionAlwaysOn(true)
                .primaryEncoderVelocityAlwaysOn(true);
        }                
    }

    public static final class Shooter{
        public static final SparkMaxConfig jawConfig = new SparkMaxConfig();
        public static final SparkMaxConfig shooterConfig = new SparkMaxConfig();

        static {
            // TODO: Adjust conversion factors
            double jawFactor = 2 * Math.PI;
            double shooterFactor = 1;
            
            // TODO: Adjust pid values
            // TODO: Configure encoder type
            jawConfig
                .inverted(true)
                .idleMode(IdleMode.kBrake);
            jawConfig.encoder
                .inverted(true)
                .countsPerRevolution(8192)
                .positionConversionFactor(jawFactor) // radians
                .velocityConversionFactor(jawFactor / 60.0); // radians per second
            jawConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(1, 0, 0)
                .outputRange(-0.5, 0.5);
            jawConfig.signals
                .primaryEncoderPositionAlwaysOn(true)
                .primaryEncoderVelocityAlwaysOn(true);

            shooterConfig
                .idleMode(IdleMode.kCoast);
            shooterConfig.encoder
                .positionConversionFactor(shooterFactor)
                .velocityConversionFactor(shooterFactor / 60.0);
            shooterConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(0.0, 0, 0)
                .outputRange(-1, 1);
            shooterConfig.signals
                .primaryEncoderPositionAlwaysOn(true)
                .primaryEncoderVelocityAlwaysOn(true);

        }
    }
}
