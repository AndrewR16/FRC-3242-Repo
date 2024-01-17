// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  //Motors
  private final WPI_TalonSRX m_leftfrontdrive = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_leftbackdrive = new WPI_TalonSRX(0);
  // private final MotorController m_left = new MotorControllerGroup(m_leftfrontdrive, m_leftbackdrive);

  private final WPI_TalonSRX m_rightfrontdrive = new WPI_TalonSRX(3);
  private final WPI_TalonSRX m_rightbackdrive = new WPI_TalonSRX(1);
  
  // private final MotorController m_right = new MotorControllerGroup(m_rightfrontdrive, m_rightbackdrive);
  private final DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);

  WPI_PigeonIMU gyro = new WPI_PigeonIMU(m_leftfrontdrive);
  

  private XboxController m_stick = new XboxController(0);
  Timer timer = new Timer() ;
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    m_right.setInverted(true);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    timer.reset();
    timer.start();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
      if(timer.get() < 1){

      }else if(timer.get() < 2){

      }else if(timer.get() < 3){

      } 
        break;
      case kDefaultAuto:
      break;
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    
  ///m_drive.arcadeDrive(-m_stick.getLeftY(), -m_stick.getLeftX());
  m_drive.curvatureDrive(-m_stick.getLeftY(), -m_stick.getLeftX(), m_stick.getAButton());
  }
  @Override
  public void testInit(){
  
  }
  @Override
  public void testPeriodic(){
    double[] ybr = new double[3];
    gyro.getYawPitchRoll(ybr);
    System.out.println(ybr[0]);

  }
}
