// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final class DriveConstants {
    // Driving Parameters - Note that these are not the maximum capable speeds of
    // the robot, rather the allowed maximum speeds
    public static final double kMaxSpeedMetersPerSecond = 1.5;
    public static final double kMaxAngularSpeed = 1.2 * Math.PI; // radians per second

    // Chassis configuration
    // Distance between centers of right and left wheels on robot
    public static final double kTrackWidth = Units.inchesToMeters(29.5);
    // Distance between front and back wheels on robot
    public static final double kWheelBase = Units.inchesToMeters(29.5);

    public static final SwerveDriveKinematics kDriveKinematics = new SwerveDriveKinematics(
        new Translation2d(kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(kWheelBase / 2, -kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, -kTrackWidth / 2));

    // Angular offsets of the modules relative to the chassis in radians
    public static final double kFrontLeftChassisAngularOffset = -Math.PI / 2;
    public static final double kFrontRightChassisAngularOffset = 0;
    public static final double kBackLeftChassisAngularOffset = Math.PI;
    public static final double kBackRightChassisAngularOffset = Math.PI / 2;

    // SPARK MAX CAN IDs
    public static final int kFrontLeftDrivingCanId = 6;
    public static final int kRearLeftDrivingCanId = 5;
    public static final int kFrontRightDrivingCanId = 7;
    public static final int kRearRightDrivingCanId = 8;

    public static final int kFrontLeftTurningCanId = 2;
    public static final int kRearLeftTurningCanId = 1;
    public static final int kFrontRightTurningCanId = 3;
    public static final int kRearRightTurningCanId = 4;

    public static final boolean kGyroReversed = false;
  }

  public static final class ElevatorConstants {
    // Can Ids
    public static final int kLiftCanId = 9;
    public static final int kGantryCanId = 10;

    // Default motor speeds
    public static final double kDefaultLiftSpeed = 0.3;
    public static final double kDefaultGantrySpeed = 0.1;

    // Limit switch ports
    public static final int kGantryFrontSwitchPort = 0;
    public static final int kGantryBackSwitchPort = 9;
    public static final int kLiftSwitchPort = 8;

    // Motor reductions
    public static final double kLiftMotorReduction = 36.0;
    public static final double kGantryMotorReduction = 20.0;

    // Feedforward values
    // Gantry
    public static final double kGantryKs = 0.37489;
    public static final double kGantryKv = 0.80445;
    public static final double kGantryKa = 0.056019;
  }
  
  public static final class ElevatorSetpoints {
    // Lowest point of the elevator
    public static final double kLowest = 0.0;
    
    // TODO: Adjust setpoint values
    // Reef setpoints (inches from bottom setpoint)
    public static final double kLevel1 = 10.0;
    public static final double kLevel2 = 20.0;
    public static final double kLevel3 = 30.0;
    public static final double kLevel4 = 40.0;

    // Gantry setpoints
    public static final double kGantryForward = 14.8;
    public static final double kGantryBackward = 0.0;
  }
  
  public static final class ShooterConstants{
    // Can ids
    public static final int kJawCanId = 11;
    public static final int kShooterCanId = 12;

    // Default motor speeds
    public static final double kDefaultOpenJawSpeed = 0.3;
    public static final double kDefaultCloseJawSpeed = -0.15;
    public static final double kDefaultShooterInSpeed = 0.4;
    public static final double kDefaultShooterOutSpeed = 0.4;

    // Feedforward values
    public static final double kJawKs = 2.9;
    public static final double kJawKv = 1.25;
    public static final double kJawKa = 0.05;

  }

  public static final class ShooterSetpoints {
    public static final double kClosed = 0;

    // TODO: Adjust setpoint values
    // Jaw angle to hold game piece
    public static final double kCoral = 0.25;
    public static final double kAlgae = 1.3;
  }

  public static final class ModuleConstants {
    // The MAXSwerve module can be configured with one of three pinion gears: 12T,
    // 13T, or 14T. This changes the drive speed of the module (a pinion gear with
    // more teeth will result in a robot that drives faster).
    public static final int kDrivingMotorPinionTeeth = 12;

    // Calculations required for driving motor conversion factors and feed forward
    public static final double kDrivingMotorFreeSpeedRps = NeoMotorConstants.kFreeSpeedRpm / 60;
    public static final double kWheelDiameterMeters = 0.0762;
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // 45 teeth on the wheel's bevel gear, 22 teeth on the first-stage spur gear, 15
    // teeth on the bevel pinion
    public static final double kDrivingMotorReduction = (45.0 * 22) / (kDrivingMotorPinionTeeth * 15);
    public static final double kDriveWheelFreeSpeedRps = (kDrivingMotorFreeSpeedRps * kWheelCircumferenceMeters)
        / kDrivingMotorReduction;
  }

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
    public static final double kDriveDeadband = 0.05;
  }

  public static final class SensorConstants {
    public static final int kGyroDeviceId = 13;
  }

  public static final class AutoConstants {
    public static final double kMaxSpeedMetersPerSecond = 3;
    public static final double kMaxAccelerationMetersPerSecondSquared = 3;
    public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI;
    public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;

    public static final double kPXController = 1;
    public static final double kPYController = 1;
    public static final double kPThetaController = 1;

    // Constraint for the motion profiled robot angle controller
    public static final TrapezoidProfile.Constraints kThetaControllerConstraints = new TrapezoidProfile.Constraints(
        kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
  }

  public static final class NeoMotorConstants {
    public static final double kFreeSpeedRpm = 5676;
  }
}
