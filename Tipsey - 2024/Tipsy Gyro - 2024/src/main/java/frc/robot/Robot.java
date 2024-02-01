// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; // SmartDashboard
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX; // Motors
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.wpilibj.XboxController; // Control
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer; // Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive; // Differential Drive
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.Encoder;
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

  // drivetrain motors
  private final WPI_TalonSRX m_leftFront = new WPI_TalonSRX(0);
  private final WPI_TalonSRX m_rightFront = new WPI_TalonSRX(5);
  private final WPI_TalonSRX m_leftBack = new WPI_TalonSRX(4);
  private final WPI_TalonSRX m_rightBack = new WPI_TalonSRX(3);

  // crane motors
  private final WPI_TalonSRX m_lift = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_tilt = new WPI_TalonSRX(1);
  private final WPI_VictorSPX m_extend = new WPI_VictorSPX(7);

  // drivetrain
  private final DifferentialDrive m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

  // xbox controller
  private final XboxController driverInput = new XboxController(0);

  // numatics
  private final DoubleSolenoid m_grabber = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  // grabber values
  private final DoubleSolenoid.Value grabberOpen = Value.kReverse;
  private final DoubleSolenoid.Value grabberClose = Value.kForward;

  // Encoder
  // *private final Encoder extendEncoder = new Encoder(null, null);
  private final Encoder tiltEncoder = new Encoder(0, 1);
  Faults extendFault = new Faults();

  // Analog Potentiometer
  AnalogPotentiometer potentiometer = new AnalogPotentiometer(0, 145, 30);

  // levels of Tilt
  double tiltAngle = 0.0;

  // Speed Variables
  private static double defaultSpeed = -0.4;
  private static double turnSpeed = -0.4;
  private static double stopSpeed = 0.0;

  // Gyroscopes
  WPI_PigeonIMU gyro = new WPI_PigeonIMU(m_rightBack);

  // Gain for a simple P loop
  double kP = 1;

   //gain for a simple P loop
  double kPangle = 0.05;

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
    m_rightBack.setInverted(true);

    // Add gyro to SmartDashboard
    SmartDashboard.putData("Gyro", gyro);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    // Reset command values
    resetCommandValues();

    // Reset gyro
    gyro.reset();
  }

  @Override
  public void autonomousPeriodic() {
    // Reset id
    resetCommandId();

    if (runFor(1)) {
      SmartDashboard.putNumber("Initial Amount", gyro.getAngle());
    } else if (runFor(10)) {
      while (gyro.getAngle() < 90) {
        m_rightFront.set(-defaultSpeed);
        m_leftFront.set(defaultSpeed);
      }
    }
    //Find the heading error; setpoint is 90
    double error = 90 - gyro.getAngle();

    //Turns the robot to face the desired direction
    m_drive.tankDrive(kPangle * error, -kPangle*error);
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
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
