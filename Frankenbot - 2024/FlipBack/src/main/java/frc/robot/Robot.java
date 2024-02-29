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
  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  private final WPI_TalonSRX m_rightBack = new WPI_TalonSRX(1);
  private final WPI_TalonSRX m_leftFront = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_leftBack = new WPI_TalonSRX(0);
  private final WPI_TalonSRX m_rightFront = new WPI_TalonSRX(3);
  private final Timer m_timer = new Timer();

  double distance;
  double speed;

  // private final DifferentialDrive arcadeDrive = new
  // DifferentialDrive(m_leftFront, m_rightFront);

  private final XboxController controller = new XboxController(0);
  private boolean stopEnabled;

  @Override
  public void robotInit() {
    // Forces the motors to move "forward"
    m_leftBack.setInverted(true);
    m_rightFront.setInverted(true);

    m_timer.reset();
    stopEnabled = false;

    MecanumDrive m_robotDrive = new MecanumDrive(m_rightBack::set, m_leftFront::set, m_leftBack::set,
        m_rightFront::set);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    speed = .25 / 2;
    distance = .64;
  }

  @Override
  public void autonomousPeriodic() {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    table.getEntry("ledMode").setNumber(0);

    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    double id = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tid").getDouble(0);
    SmartDashboard.putNumber("LimeLightID", id);
    System.out.println("LimeLightID " + id);

    // read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);
    System.out.println(x);
    System.out.println("Limelight X = " + x);
    System.out.println("Limelight Y = " + y);
    SmartDashboard.putNumber("LimeLightArea", area);
    System.out.println("Limelight Area " + area);
    SmartDashboard.putNumber("X", x);

    float Kp = -0.1f;
    double min_command = 0.05f;

    double heading_error = (double) -x;
    double steering_adjust = 0.0f;
    if (Math.abs(heading_error) > 1.0) {
      if (heading_error < 0) {
        steering_adjust = Kp * heading_error + min_command;
      } else {
        steering_adjust = Kp * heading_error - min_command;
      }
    }

    if (x <= -5) {
      m_leftBack.set(-speed * 2);
      m_leftFront.set(-speed * 2);
      m_rightBack.set(speed * 2);
      m_rightFront.set(speed * 2);
    }
    if (x >= 5) {
      m_leftBack.set(speed * 2);
      m_leftFront.set(speed * 2);
      m_rightBack.set(-speed * 2);
      m_rightFront.set(-speed * 2);
    }
    if (x < 5 && x > -5 && x != 0) {
      if (area > 0.54 && area < 0.64) {
        m_leftFront.set(0);
        m_rightBack.set(0);
        m_rightFront.set(0);
        m_leftBack.set(0);
      }
      if (area <= distance - .1) {
        m_leftFront.set(speed);
        m_rightBack.set(-speed);
        m_rightFront.set(speed);
        m_leftBack.set(-speed);
      }
      if (area >= distance) {
        m_leftFront.set(-speed);
        m_rightBack.set(speed);
        m_rightFront.set(-speed);
        m_leftBack.set(speed);
      }
      SmartDashboard.putNumber("Steering Adjust", steering_adjust);
    }
  }

  @Override
  public void teleopInit() {
    // Makes it so that the motors follow each other on both sides
    m_leftFront.follow(m_rightBack);
    m_rightFront.follow(m_leftBack);

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
    m_leftFront.set(.2);
    m_rightBack.set(-.2);
    m_rightFront.set(.2);
    m_leftBack.set(-.2);
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
