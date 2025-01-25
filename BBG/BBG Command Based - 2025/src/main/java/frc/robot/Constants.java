// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static class DriveConstants {
    public static final int kLeftLeadMotorPort = 10;
    public static final int kLeftFollowMotorPort = 9;
    public static final int kRightLeadMotorPort = 12;
    public static final int kRightFollowMotorPort = 11;
  }

  public static class ShooterConstants {
    public static final int kIntakeMotorPort = 1;
    
    public static final int kFirstFeederMotorPort = 2;
    public static final int kSecondFeederMotorPort = 3;
    public static final int kThirdFeederMotorPort = 4;
    
    public static final int kLauncherMotorPort = 8;
  }

  public static class SensorConstants {
    public static final int kFirstPositionPort = 1;
    public static final int kSecondPositionPort = 3;
    public static final int kThirdPositionPort = 5;
  }
}
