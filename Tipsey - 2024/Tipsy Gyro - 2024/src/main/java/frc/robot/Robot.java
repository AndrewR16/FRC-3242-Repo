// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; // SmartDashboard
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX; // Motors
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.wpilibj.XboxController; // Control
import edu.wpi.first.wpilibj.Timer; // Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive; // Differential Drive

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
  private final Timer timer = new Timer();

  // Drivetrain motors
  private final WPI_TalonSRX m_leftFront = new WPI_TalonSRX(0);
  private final WPI_TalonSRX m_rightFront = new WPI_TalonSRX(5);
  private final WPI_TalonSRX m_leftBack = new WPI_TalonSRX(4);
  private final WPI_TalonSRX m_rightBack = new WPI_TalonSRX(3);

  // Drivetrain
  private final DifferentialDrive m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

  // Xbox controller
  private final XboxController driverInput = new XboxController(0);

  // Manual control variable
  private boolean manualControlEnabled;

  // Crane motors
  // private final WPI_TalonSRX m_lift = new WPI_TalonSRX(2);
  // private final WPI_TalonSRX m_tilt = new WPI_TalonSRX(1);
  // private final WPI_VictorSPX m_extend = new WPI_VictorSPX(7);

  // Numatics
  private final DoubleSolenoid m_grabber = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  // Grabber values
  private final DoubleSolenoid.Value grabberOpen = Value.kReverse;
  private final DoubleSolenoid.Value grabberClose = Value.kForward;

  // Crane motors
  private final WPI_TalonSRX m_tilt = new WPI_TalonSRX(1);

  // levels of tilt
  double tiltAngle = 0.0;

  // *Encoder
  // private final Encoder extendEncoder = new Encoder(null, null);
  // private final Encoder tiltEncoder = new Encoder(0, 1);
  Faults extendFault = new Faults();

  // Analog potentiometer
  AnalogPotentiometer potentiometer = new AnalogPotentiometer(0, 145, 30);

  // Gyroscopes
  private final WPI_PigeonIMU gyro = new WPI_PigeonIMU(m_rightBack);

  // Autonomous routines sendable chooser variables
  private static final String defaultAutonomous = "DefaultAuto";
  private static final String turnToHeadingAuto = "FaceHeadingAuto";
  private String m_selectedAutonomous;
  private final SendableChooser<String> m_autonomousRoutines = new SendableChooser<String>();

  private static final double desiredHeading = 0;

  // PID controller error
  double error;

  // Forward drive p calculator
  private double calculatePDrive(double error) {
    double kP = (1 / Math.PI) * (Math.atan(0.3 * error));

    return kP;
  }

  // Turn p calculator
  private double calculatePTurn(double error) {
    double kP = 0.006 * error;
    // Maximum Output
    if (kP < -0.8) {
      kP = -0.8;
    }
    if (kP > 0.8) {
      kP = 0.8;
    }

    // Minimum Output
    if (kP > 0 && kP < 0.3) {
      kP = 0.3;
    }
    if (kP < 0 && kP > -0.3) {
      kP = -0.3;
    }

    // Stop when heading is acheived
    if (Math.abs(error) < 1) {
      kP = 0;
    }

    return kP;
  }

  boolean initialCheck = true;
  double stoppingTime = 0;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Back motors follow front
    m_leftBack.follow(m_leftFront);
    m_rightBack.follow(m_rightFront);

    // Invert right side motors
    m_leftFront.setInverted(true);
    m_leftBack.setInverted(true);

    // Add differential drive to SmartDashboard
    SmartDashboard.putData("Differential Drive", m_drive);

    // Autonomous Routines
    m_autonomousRoutines.setDefaultOption("Default Autonomous", defaultAutonomous);
    m_autonomousRoutines.addOption("90DegRotation", turnToHeadingAuto);
    SmartDashboard.putData("Autonomous Routines", m_autonomousRoutines);

    // Heading Selection

  }

  @Override
  public void robotPeriodic() {
    // Add gyro to SmartDashboard
    SmartDashboard.putNumber("Gyro", gyro.getAngle());
  }

  @Override
  public void autonomousInit() {
    // Reset command values
    resetCommandValues();

    // Reset timer
    timer.reset();
    timer.start();

    // Get autonomous selection
    m_selectedAutonomous = m_autonomousRoutines.getSelected();
    System.out.println("Selected Autonomous: " + m_selectedAutonomous);
  }

  @Override
  public void autonomousPeriodic() {
    // Reset id
    resetCommandId();

    SmartDashboard.putNumber("Desired Heading", desiredHeading);

    // Autonomous routines
    switch (m_selectedAutonomous) {
      // *Turns to 90 degrees, using the gyro to stabilize turn amount
      case turnToHeadingAuto:
        error = 90 - gyro.getAngle();
        // SmartDashboard.putNumber("Error", error);

        if (runTillComplete()) {
          // Turn robot
          double turnRate = calculatePTurn(error);
          m_leftFront.set(turnRate);
          m_rightFront.set(-turnRate);

          // Stop after turn is complete
          if (turnRate == 0) {
            if (initialCheck) {
              stoppingTime = timer.get() + 1.5;
              initialCheck = false;
            } else if (stoppingTime < timer.get() && turnRate == 0) {
              initialCheck = true;
              commandCompleted();
            }
          }

          // SmartDashboard.putNumber("Turn Rate", turnRate);
          // SmartDashboard.putNumber("Motor Speed", m_leftFront.get());
        }
        break;

      // *Drives forward continuously, using the gyro to stabilize the heading
      case defaultAutonomous:
      default:
        error = -gyro.getAngle();

        // Drive forward
        if (runFor(2)) {
          m_drive.tankDrive(.63 + calculatePDrive(error), .63 - calculatePDrive(error));
        }
        break;
    }
  }

  @Override
  public void teleopInit() {
    // Reset data collection values
    // DataCollection.resetDataValues();

    // Reset timer
    timer.reset();
    timer.start();

    // Initialize manual control
    manualControlEnabled = true;
    SmartDashboard.putBoolean("Manual Control", manualControlEnabled);
  }

  @Override
  public void teleopPeriodic() {
    // Manual control
    if (manualControlEnabled) {
      // Drive
      m_drive.arcadeDrive(-driverInput.getLeftY(), -driverInput.getLeftX());

      // Reset gyro
      if (driverInput.getXButtonPressed()) {
        gyro.reset();
      }

      // Grabber
      if (driverInput.getRightBumperPressed()) {
        if (m_grabber.get() == grabberOpen) {
          m_grabber.set(grabberClose);
        } else {
          m_grabber.set(grabberOpen);
        }
      }
      if (driverInput.getPOV() == 0) {
        tiltAngle = tiltAngle + 0.05;
      }
      if (driverInput.getPOV() == 180) {
        tiltAngle = tiltAngle - 0.05;
      }

      // Tilt
      if (driverInput.getYButtonPressed()) {
        m_tilt.set(0.6);
      }
      if (driverInput.getAButtonPressed()) {
        m_tilt.set(-0.3);
      }
      if (driverInput.getAButtonReleased() || driverInput.getYButtonReleased()) {
        m_tilt.set(0.0);
      }
    }

    // Toggle manual control
    if (driverInput.getBButtonPressed()) {
      manualControlEnabled = !manualControlEnabled;
      SmartDashboard.putBoolean("Manual Control", manualControlEnabled);
    }

    // Autonomous control
    if (manualControlEnabled == false) {
      // *Turns to 0 degrees
      error = -gyro.getAngle();

      // Turn robot
      double turnRate = calculatePTurn(error);
      m_leftFront.set(turnRate);
      m_rightFront.set(-turnRate);

      // Stop after turn is complete
      if (turnRate == 0) {
        if (initialCheck) {
          stoppingTime = timer.get() + 1.5;
          initialCheck = false;
        } else if (stoppingTime < timer.get() && turnRate == 0) {
          initialCheck = true;
          manualControlEnabled = true;
          SmartDashboard.putBoolean("Manual Control", manualControlEnabled);
        }
      }
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
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
