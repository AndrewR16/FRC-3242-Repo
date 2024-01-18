// Copyright (c) FIRST and other WPILib contributors.
// TODO: Update motor controller groups to use the .follow() method instead

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import com.revrobotics.CANSparkMax;
import frc.robot.LED;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

public class Robot extends TimedRobot {

  // drivetrain motors
  private final WPI_TalonSRX m_leftFront = new WPI_TalonSRX(0);
  private final WPI_TalonSRX m_rightFront = new WPI_TalonSRX(5);
  private final WPI_TalonSRX m_leftBack = new WPI_TalonSRX(4);
  private final WPI_TalonSRX m_rightBack = new WPI_TalonSRX(3);
  // crane motors
  private final WPI_TalonSRX lift = new WPI_TalonSRX(2);
  private final WPI_TalonSRX extend = new WPI_TalonSRX(1);
  private final WPI_VictorSPX tilt = new WPI_VictorSPX(7);

  // drivetrain
  private final DifferentialDrive m_drive = new DifferentialDrive(m_leftFront, m_rightFront);
  // xbox controller
  private final XboxController driverInput = new XboxController(0);
  // numatics
  private final DoubleSolenoid grabber = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  // Encoder
 //  private final Encoder extendEncoder = new Encoder(null, null);
  private final Encoder tiltEncoder = new Encoder(0, 1);
  Faults extendFault = new Faults();
  // Pigeon gyro
 // WPI_PigeonIMU gyro = new WPI_PigeonIMU(rightBack);
  // auto chooser
  private static final String kBlue1 = "Blue 1";
  private static final String kBlue2 = "Blue 2";
  private static final String kRed1 = "Red 1";
  private static final String kRed2 = "Red 2";
  private String m_autoSelected;
  private final SendableChooser<String> chooser = new SendableChooser<>();
  // Analog Potentiometer
  AnalogPotentiometer pot = new AnalogPotentiometer(0, 145, 30);
  // levels of Tilt
  double tiltAngle = 0.0;
  //Led
  Spark ledSpark = new Spark(0);
  LED led = new LED(ledSpark);
  @Override
  public void robotInit() {

    chooser.setDefaultOption("Red 1", kRed1);
    chooser.addOption("Red 2", kRed2);
    chooser.addOption("Blue 1", kBlue1);
    chooser.addOption("Blue 2", kBlue2);
    SmartDashboard.putData("Auto Choices", chooser);
    m_leftFront.setInverted(true);
    m_leftBack.setInverted(true);
    tilt.setInverted(true);
    tiltEncoder.setDistancePerPulse(4.0 / 256);
    
  }

  
  @Override
  public void robotPeriodic() {

    // System.out.println("Encoder" + extendEncoder.get());
    // System.out.println("Tilt " + tiltEncoder.getDistance());
    // SmartDashboard.putNumber("extednencoder",extendEncoder.get());
    SmartDashboard.putNumber("tiltencoder", tiltEncoder.getDistance());

//    SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
//    SmartDashboard.putNumber("Turn rate", gyro.getRate());
   // SmartDashboard.putNumber("Pitch+", gyro.getPitch());
    SmartDashboard.putNumber("Pot", pot.get());
    SmartDashboard.putNumber("tilt angle", tiltAngle);
    SmartDashboard.putNumber("Sensor Vel:", extend.getSelectedSensorVelocity());
    SmartDashboard.putNumber("Sensor Pos:", extend.getSelectedSensorPosition());
    SmartDashboard.putNumber("Out %", extend.getMotorOutputPercent());
    SmartDashboard.putBoolean("Out of Phase:", extendFault.SensorOutOfPhase);

  }

  @Override
  public void autonomousInit() {

    m_autoSelected = chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kRed1:

        break;
      case kRed2:

        break;
      case kBlue1:

        break;
      case kBlue2:

        break;
    }
  }

  @Override
  public void teleopInit() {
    extend.configFactoryDefault();
    extend.setInverted(false);
    extend.setSensorPhase(false);

    m_leftBack.follow(m_leftFront);
    m_rightBack.follow(m_rightFront);
  }

  @Override
  public void teleopPeriodic() {
    led.lawnGreen();
    // drivetrain
    m_drive.arcadeDrive(-driverInput.getLeftY(), -driverInput.getLeftX());
    // extensions
    if (driverInput.getAButtonPressed()) {
      extend.set(4.25f);
    }
    if (driverInput.getAButtonReleased() || driverInput.getBButtonReleased()) {
      extend.set(0.0);
    }
    if (driverInput.getBButtonPressed()) {
      extend.set(-0.3);
    }
    // grabber
    if (driverInput.getLeftBumperPressed()) {
      grabber.set(Value.kForward);
    }
    if (driverInput.getRightBumperPressed()) {
      grabber.set(Value.kReverse);
    }
    if (driverInput.getPOV() == 0) {
      tiltAngle = tiltAngle +0.05;
    }
    if (driverInput.getPOV() == 180) {
      tiltAngle = tiltAngle - 0.05;
    }
//tilt
    tilt.set(tiltAngle/2);
    
  //lift makes sure it doesn't go past
    if(pot.get() < 120 || 
    driverInput.getRightY() > 0){
    lift.set(driverInput.getRightY());
    }
    else{
      lift.set(0.0);
    }
    if (driverInput.getYButtonPressed() == true) {
      tiltAngle = 0.8;

    }
    if (driverInput.getRightTriggerAxis() > 0.1) {
      tilt.set(0.0);
      extend.getFaults(extendFault);
    
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
    m_leftFront.set(0.3);
    m_leftBack.set(0.3);
    m_rightFront.set(0.3);
    m_rightBack.set(0.3);
     extend.set(0.3);
    // lift.set(0.3);
    
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }

}
