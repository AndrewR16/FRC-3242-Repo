// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot; // Structure
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX; // Motors
import edu.wpi.first.wpilibj.XboxController; // Control
import edu.wpi.first.wpilibj.Timer; // Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive; // Drive
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
// *import frc.robot.Speed;

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
  // Motors
  private final WPI_TalonFX m_frontLeftDrive = new WPI_TalonFX(6);
  private final WPI_TalonFX m_frontRightDrive = new WPI_TalonFX(2);
  private final WPI_TalonFX m_backLeftDrive = new WPI_TalonFX(5);
  private final WPI_TalonFX m_backRightDrive = new WPI_TalonFX(1);

  // Motor groups for arcade drive
  private final MotorControllerGroup m_leftDrive = new MotorControllerGroup(m_backLeftDrive, m_frontLeftDrive);
  private final MotorControllerGroup m_rightDrive = new MotorControllerGroup(m_backRightDrive, m_frontRightDrive);

  // Drive Control
  private final DifferentialDrive m_arcadeDrive = new DifferentialDrive(m_leftDrive, m_rightDrive);
  private final MecanumDrive m_mecanumDrive = new MecanumDrive(m_frontLeftDrive, m_backLeftDrive, m_frontRightDrive,
      m_backRightDrive);
  private final XboxController controller = new XboxController(0);
  private final Timer m_timer = new Timer();

  // Global Variables
  private boolean arcadeDriveActivated = true;
  private boolean invertedToggle = false;

  /**
   * The first call of the function will properly align the direction of the
   * motors.
   * Subsequent calls will toggle the direction.
   */
  private void toggleMotorDirections() {
    m_backLeftDrive.setInverted(!invertedToggle);
    m_backRightDrive.setInverted(invertedToggle);
    m_frontLeftDrive.setInverted(invertedToggle);
    m_frontRightDrive.setInverted(invertedToggle);

    invertedToggle = !invertedToggle;
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    toggleMotorDirections();
  }

  

  @Override
  public void robotPeriodic() {
  }

  // Robot runs without input
  @Override
  public void autonomousInit() {
    toggleMotorDirections();
    m_timer.reset();
    m_timer.start();
  }

  @Override
  public void autonomousPeriodic() {
    if (m_timer.get() <= 3){
      m_leftDrive.set(Speed.velocity);
      m_rightDrive.set(Speed.velocity);
    }

    if (m_timer.get() >= 4 && m_timer.get() <= 8) {
      // Turns right
      m_leftDrive.set(Speed.velocity);
      m_rightDrive.set(Speed.velocity * -1);
    }
  }

  // Robot runs with input
  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    // Steering
    if (arcadeDriveActivated) {
      m_arcadeDrive.arcadeDrive(controller.getLeftY() / 2, controller.getLeftX() / 2);
    } else {
      m_mecanumDrive.driveCartesian(controller.getLeftY() / 2, controller.getLeftX() / -2, controller.getRightX() / -2);
    }

    // Change Motor Directions (Y)
    if (controller.getYButtonPressed()) {
      toggleMotorDirections();
    }
    // Swap between arcade and mecanum drive (X)
    if (controller.getXButtonPressed()) {
      arcadeDriveActivated = !arcadeDriveActivated;
    }
  }

  // Code executes when the robot is disabled
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
    m_leftDrive.set(-Speed.velocity);
    m_rightDrive.set(-Speed.velocity);
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}