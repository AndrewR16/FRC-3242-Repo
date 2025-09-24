package frc.robot;

import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class Constants {
    public static final class DriveConstants {
        //WPI_TalonSRX IDs
        public static final int kFrontRightID = 12;
        public static final int kFrontLeftID = 10;
        public static final int kBackRightID = 11;
        public static final int kBackLeftID = 9;
    }

    public static final class ShooterConstants {
        //VictorSPX IDs
        public static final int kIntakeOneID = 1;
        public static final int kIntakeTwoID = 2;
        public static final int kIntakeThreeID = 3;
        public static final int kIntakeFourID = 4;

        //WPI_TalonSRX IDs
        public static final int kShooterID = 8;

        //DigitalInput IDs
        public static final int kFirstPos = 1;
        public static final int kSecondPos = 3;
        public static final int kThirdPos = 5;
    }

    public static final class PneumaticsConstants {
        public static final PneumaticsModuleType kBoardType = PneumaticsModuleType.CTREPCM;
        public static final int kForwardChannel = 0;
        public static final int kReverseChannel  = 1;
    }

    public static final class OIConstants {
        public static final int kDriverControllerPort = 0;
        public static final double kDriveDeadZone = 0.05;
    }

    public static final class CameraConstants {
        public static final int cameraResolutionWidth = 640;
        public static final int cameraResolutionHeight = 480;

    }
}
