// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
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

  // Gyro
  private final WPI_PigeonIMU gyro = new WPI_PigeonIMU(m_backRight);

  private double correctedGyroAngle() {
    return -gyro.getAngle();
  }

  // Manual control handeler
  private boolean manualControlEnabled;

  // Correct controller input
  protected static double correctInput(double driverInput) {
    if ((driverInput > 0 && driverInput < 0.15) || (driverInput > -0.15 && driverInput < 0)) {
      return 0;
    }

    return driverInput;
  }

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

    // Meccanum drive in smart dashboard
    SmartDashboard.putData("Mecanum Drive", m_robotDrive);
  }

  @Override
  public void robotPeriodic() {
    // TODO: Correct gyro
    SmartDashboard.putNumber("Gyro Heading", correctedGyroAngle());
    SmartDashboard.putNumber("Read Heading", correctedGyroAngle());
    // Put adjusted heading

  }

  @Override
  public void autonomousInit() {
    resetCommandValues();
  }

  @Override
  public void autonomousPeriodic() {
    resetCommandId();

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
    SmartDashboard.putString("Dectected Station", LimelightNode.getNodeName((int) tagID));

    if (runTillComplete()) {
      // TODO: Align with april tag using gyro
      SmartDashboard.putString("Current Task", "Aligning Heading");
      double adjustedHeadingError = Proportional.adjustHeadingValue(gyro.getAngle());
      SmartDashboard.putNumber("Adjusted Heading", adjustedHeadingError);
      double driveSpeed = Proportional.calculatePDrive(adjustedHeadingError, 0.006, 2);
      m_robotDrive.driveCartesian(0.0, 0.0, driveSpeed);

      // Target an april tag and align x position
      // driveSpeed = Proportional.calculatePDrive(xOffset, 0.04, 3);
      // m_robotDrive.driveCartesian(0.0, driveSpeed, 0.0);

      // checkIfCompleted(driveSpeed, 0.0);
    } else if (runTillComplete()) {
      // TODO (Test): Target an april tag and align y position
      SmartDashboard.putString("Current Task", "Aligning Y");
      double error = 0.64 - targetArea;
      double driveSpeed = Proportional.calculatePDrive(error, 0.8, 0.05);
      m_robotDrive.driveCartesian(driveSpeed, 0.0, 0.0);

      checkIfCompleted(driveSpeed, 0.0);
    }

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
      m_robotDrive.driveCartesian(correctInput(-driverInput.getLeftY()), correctInput(driverInput.getRightX()),
          correctInput(driverInput.getLeftX()));

      if (driverInput.getXButtonPressed()) {
        gyro.reset();
      }
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
    resetCommandValues();
  }

  @Override
  public void testPeriodic() {
    resetCommandId();

    if (runFor(3)) {
      m_robotDrive.driveCartesian(0.2, 0, 0); // Forward
    } else if (runFor(3)) {
      m_robotDrive.driveCartesian(-0.2, 0, 0); // Backward
    } else if (runFor(3)) {
      m_robotDrive.driveCartesian(0, -0.2, 0); // Strafe left
    } else if (runFor(3)) {
      m_robotDrive.driveCartesian(0, 0.2, 0); // Strafe right
    } else {
      m_robotDrive.driveCartesian(0, 0, 0);
    }
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
