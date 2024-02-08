// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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
  @SuppressWarnings("unused")
  private final Timer timer = new Timer();

  // drivetrain motors
  private final WPI_TalonSRX m_leftFront = new WPI_TalonSRX(0);
  private final WPI_TalonSRX m_rightFront = new WPI_TalonSRX(5);
  private final WPI_TalonSRX m_leftBack = new WPI_TalonSRX(4);
  private final WPI_TalonSRX m_rightBack = new WPI_TalonSRX(3);

  // crane motors
  // private final WPI_TalonSRX m_lift = new WPI_TalonSRX(2);
  // private final WPI_TalonSRX m_tilt = new WPI_TalonSRX(1);
  // private final WPI_VictorSPX m_extend = new WPI_VictorSPX(7);

  // drivetrain
  private final DifferentialDrive m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

  // xbox controller
  private final XboxController driverInput = new XboxController(0);

  // numatics
  private final DoubleSolenoid m_grabber = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  // grabber values
  private final DoubleSolenoid.Value grabberOpen = Value.kReverse;
  private final DoubleSolenoid.Value grabberClose = Value.kForward;

  // *Encoder
  // private final Encoder extendEncoder = new Encoder(null, null);
  // private final Encoder tiltEncoder = new Encoder(0, 1);
  Faults extendFault = new Faults();

  // crane motors
  private final WPI_TalonSRX m_tilt = new WPI_TalonSRX(1);

  // Analog Potentiometer
  AnalogPotentiometer potentiometer = new AnalogPotentiometer(0, 145, 30);

  // levels of Tilt
  double tiltAngle = 0.0;

  // Speed Variables
  // private static double defaultSpeed = -0.4;
  // private static double turnSpeed = -0.4;
  // private static double stopSpeed = 0.0;

  // Gyroscopes
  WPI_PigeonIMU gyro = new WPI_PigeonIMU(m_rightBack);

  // gain for a simple P loop
  double proportionalConstant = 0.3;

  private double calculateP(double error) {
    double kP = (1 / Math.PI) * (Math.atan(0.3 * error));
    SmartDashboard.putNumber("Proportional Constant", kP);

    return kP;
  }

  PIDController pid;

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
  }

  @Override
  public void robotPeriodic() {
    // Add gyro to SmartDashboard
    SmartDashboard.putNumber("Gyro", gyro.getAngle());
    SmartDashboard.putNumber("Gyro Rate", gyro.getRate());
  }

  @Override
  public void autonomousInit() {
    // Reset command values
    resetCommandValues();

    // Reset gyro
    gyro.reset();

    pid = new PIDController(0.5, 0.5, 0.5);
  }

  @Override
  public void autonomousPeriodic() {
    // Reset id
    resetCommandId();

    // Error
    double error = -gyro.getAngle();

    // Drives forward continuously at half speed, using the gyro to stabilize the
    // heading
    if (runFor(2)) {
      m_drive.tankDrive(.63 + calculateP(error), .63 - calculateP(error));
    }

    // if (runFor(3)) {
    // m_drive.tankDrive(0.6, 0.6);
    // }
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    // Reset gyro
    if (driverInput.getXButtonPressed()) {
      gyro.reset();
    }

    // drive
    m_drive.arcadeDrive(-driverInput.getLeftY(), -driverInput.getLeftX());

    // grabber
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
    // Reset id
    resetCommandId();

    // Error
    double error = -gyro.getRate();

    // Drives forward continuously at half speed, using the gyro to stabilize the
    // heading
    if (runFor(3)) {
      m_drive.tankDrive(.5 + proportionalConstant * error, .5 - proportionalConstant * error);
    }
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
