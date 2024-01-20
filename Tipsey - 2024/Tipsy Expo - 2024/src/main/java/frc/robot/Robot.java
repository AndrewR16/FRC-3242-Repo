// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; // SmartDashboard
import edu.wpi.first.wpilibj.TimedRobot; // Structure
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX; // Motors
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.XboxController; // Control
import edu.wpi.first.wpilibj.Timer; // Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive; // Differential Drive
import edu.wpi.first.wpilibj.drive.MecanumDrive; // Mecanum Drive
import edu.wpi.first.wpilibj.Encoder;

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

  // drivetrain motors
  private final WPI_TalonSRX m_leftFront = new WPI_TalonSRX(0);
  private final WPI_TalonSRX m_rightFront = new WPI_TalonSRX(5);
  private final WPI_TalonSRX m_leftBack = new WPI_TalonSRX(4);
  private final WPI_TalonSRX m_rightBack = new WPI_TalonSRX(3);

  // crane motors
  private final WPI_TalonSRX m_lift = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_extend = new WPI_TalonSRX(1);
  private final WPI_VictorSPX m_tilt = new WPI_VictorSPX(7);

  // drivetrain
  private final DifferentialDrive m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

  // xbox controller
  private final XboxController driverInput = new XboxController(0);

  // numatics
  private final DoubleSolenoid grabber = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  // Encoder
  // *private final Encoder extendEncoder = new Encoder(null, null);
  private final Encoder tiltEncoder = new Encoder(0, 1);
  Faults extendFault = new Faults();

  // Analog Potentiometer
  AnalogPotentiometer potentiometer = new AnalogPotentiometer(0, 145, 30);

  // levels of Tilt
  double tiltAngle = 0.0;

  // Speed Variables
  private static double defaultSpeed = -0.5;
  private static double backwards = -1.0;
  private static double stopSpeed = 0.0;

  // *Smart Dashboard
  private static int counter = 0;
  private static final String kDefaultAuto = "Test Left";
  private static final String kCustomAuto = "Test Right";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

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
    m_rightFront.setInverted(true);
    m_chooser.setDefaultOption("Test Left", kDefaultAuto);
    m_chooser.addOption("Test Right", kCustomAuto);
    SmartDashboard.putData("Auto Choices", m_chooser);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    // resets timer and starts it again
    timer.reset();
    timer.start();
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      // Testing the Right Side
      case kCustomAuto:
        if (timer.get() < 2) {
          // Slow down the right side
          m_leftFront.set(defaultSpeed);
          m_rightFront.set(defaultSpeed  * 0.7);
        }else if (timer.get() == 2) {
          m_leftFront.set(stopSpeed);
          m_rightFront.set(stopSpeed);
        }
        break;

      // Testing the Left Side
      case kDefaultAuto:
      default:
        if (timer.get() < 2) {
          // Slow down the left side
          m_leftFront.set(defaultSpeed * 0.7);
          m_rightFront.set(defaultSpeed);
        } else if (timer.get() == 2) {
          m_leftFront.set(stopSpeed);
          m_rightFront.set(stopSpeed);
        }
        break;
    }
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    m_drive.arcadeDrive(driverInput.getLeftY(), driverInput.getLeftX());
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
