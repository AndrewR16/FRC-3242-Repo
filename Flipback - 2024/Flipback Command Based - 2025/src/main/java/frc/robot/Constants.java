package frc.robot;

public final class Constants {
    public static class OperatorConstants {
        public static final int kDriverControllerPort = 0;
        public static final double kDriveDeadband = 0.05;
    }

    public static class DriveConstants {
        // Drive motor ports
        public static final int kFrontLeftDrivingCanId = 5;
        public static final int kBackLeftDrivingCanId = 3;
        public static final int kFrontRightDrivingCanId = 9;
        public static final int kBackRightDrivingCanId = 2;

        // Default driving speed
        public static final double kDefaultDriveSpeed = 0.4;
    }

    public final class ShooterConstants {
        // Shooter motor ports
        public static final int kTopLeftShooterCanId = 8;
        public static final int kBottomLeftShooterCanId = 11;
        public static final int kTopRightShooterCanId = 10;
        public static final int kBottomRightShooterCanId = 4;

        // Linear actuator port
        public static final int kLinearActuatorPort = 1;

        // Shooter motor speed
        public static final int kShooterSpeed = 1;
    }

    public final class IntakeConstants {
        // Intake and flip intake motor ports
        public static final int kIntakeMotorCanId = 7;
        public static final int kIntakeFlipMotorCanId = 6;

        // Intake infrared sensor port
        public static final int kIntakeInfraredSensorPort = 1;

        // Intake flip limit switch ports
        public static final int kIntakeUpLimitSwitchPort = 4;
        public static final int kIntakeDownLimitSwitchPort = 3;

        // Intake flip motor speed
        public static final double kIntakeFlipSpeed = 0.6;
    }
}
