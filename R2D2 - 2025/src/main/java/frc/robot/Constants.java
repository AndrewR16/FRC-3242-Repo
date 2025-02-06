package frc.robot;

public class Constants {
    public static class OperatorConstants {
        public static final int kDriverControllerPort = 0;
      }
    
    public static class DriveConstants {
        // Max motor speeds
        public static final double kMaxDriveSpeed = 0.8;
        public static final double kMaxTurnSpeed = 0.8;
        
        // Motor CAN ids
        public static final int kLeftDriveMotorCANId = 4;
        public static final int kRightDriveMotorCANId = 3;
    }
}
