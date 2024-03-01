// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static frc.robot.Command.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  // Timer
  private final Timer m_timer = new Timer();

  // Motors
  private final WPI_TalonSRX m_frontLeft = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_backLeft = new WPI_TalonSRX(0);
  private final WPI_TalonSRX m_frontRight = new WPI_TalonSRX(3);
  private final WPI_TalonSRX m_backRight = new WPI_TalonSRX(1);

  // Controller and drive
  private final XboxController driverInput = new XboxController(0);
  private final MecanumDrive m_robotDrive = new MecanumDrive(m_frontLeft, m_backLeft, m_frontRight, m_backRight);

  // Manual control handeler
  private boolean manualControlEnabled;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Reverse motors
    m_frontRight.setInverted(true);
    m_backRight.setInverted(true);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
    // Limelight
    NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry xOffsetEntry = limelight.getEntry("tx");
    NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");
    NetworkTableEntry tagIDEntry = limelight.getEntry("tid");

    double xOffset = xOffsetEntry.getDouble(0.0);
    double targetArea = targetAreaEntry.getDouble(0.0);
    double tagID = tagIDEntry.getDouble(0.0);

    // Smart Dashboard
    SmartDashboard.putNumber("X Offest", xOffset);
    SmartDashboard.putNumber("Target Area", targetArea);
    SmartDashboard.putNumber("April Tag ID", tagID);
    SmartDashboard.putString("Dectected Station", Limelight.getStation(tagID));

    // Target an april tag and align x position
    if (runTillComplete()) {
      double driveSpeed = Limelight.calculatePDrive(xOffset);
      m_robotDrive.driveCartesian(0.0, driveSpeed, 0.0);

      checkIfCompleted(driveSpeed, 0.0);
    }
    
    // TODO: Target an april tag and align y position
  }

  @Override
  public void teleopInit() {
    // Reset timer
    m_timer.reset();
    m_timer.start();

    // Initialize manual control
    manualControlEnabled = true;
    SmartDashboard.putBoolean("Manual Control", manualControlEnabled);
  }

  @Override
  public void teleopPeriodic() {
    // Toggle manual control
    if (driverInput.getBButtonPressed()) {
      manualControlEnabled = !manualControlEnabled;
      SmartDashboard.putBoolean("Manual Control", manualControlEnabled);
    }

    // Teleop
    if (manualControlEnabled) {
      // Drive
      m_robotDrive.driveCartesian(driverInput.getLeftY(), driverInput.getLeftX(), driverInput.getRightX());
    } else {
      // TODO: Add autonomous routines to teleop
    }
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
    m_frontLeft.set(0.4);
    m_frontRight.set(0.4);
    m_backLeft.set(0.4);
    m_backRight.set(0.4);
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
