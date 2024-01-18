// Copyright (c) FIRST and other WPILib contributors.

package frc.robot;

//imports
import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
//import com.revrobotics.CANSparkMax;
import frc.LED;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

public class Robot extends TimedRobot {

  // drivetrain motors
  private final WPI_TalonSRX leftFront = new WPI_TalonSRX(0);
  private final WPI_TalonSRX rightFront = new WPI_TalonSRX(5);
  private final WPI_TalonSRX leftBack = new WPI_TalonSRX(4);
  private final WPI_TalonSRX rightBack = new WPI_TalonSRX(3);
  // crane motors
  private final WPI_TalonSRX lift = new WPI_TalonSRX(2);
  private final WPI_TalonSRX extend = new WPI_TalonSRX(1);
  private final WPI_VictorSPX tilt = new WPI_VictorSPX(7);

  // drivetrain
  MotorControllerGroup m_left = new MotorControllerGroup(leftFront, leftBack);
  MotorControllerGroup m_right = new MotorControllerGroup(rightFront, rightBack);
  private final DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
  // xbox controller
  private final XboxController driverInput = new XboxController(0);
  // pneumatics
  private final DoubleSolenoid grabber = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  // Encoder
  private final Encoder tiltEncoder = new Encoder(0, 1);
  Faults extendFault = new Faults();
  // Pigeon gyro
  WPI_PigeonIMU gyro = new WPI_PigeonIMU(rightBack);
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
  //dropoff position
  String dropPos = "N/A"; 
  //Led
  Spark ledSpark = new Spark(0);
  LED led = new LED(ledSpark);
  //limelight variables
  NetworkTable table;
  NetworkTableEntry tx;
  NetworkTableEntry ty;
  NetworkTableEntry ta;
  NetworkTableEntry tv;
  double v;
  double x;
  double y;
  double area;

  @Override
  public void robotInit() {

    chooser.setDefaultOption("Red 1", kRed1);
    chooser.addOption("Red 2", kRed2);
    chooser.addOption("Blue 1", kBlue1);
    chooser.addOption("Blue 2", kBlue2);
    SmartDashboard.putData("Auto Choices", chooser);
    leftFront.setInverted(true);
    leftBack.setInverted(true);
    tilt.setInverted(true);
    tiltEncoder.setDistancePerPulse(4.0 / 256);
    
  }

  
  @Override
  public void robotPeriodic() {
    //updates limelight values periodically
    table = NetworkTableInstance.getDefault().getTable("limelight");
    tx = table.getEntry("tx");
    ty = table.getEntry("ty");
    ta = table.getEntry("ta");
    tv = table.getEntry("tv");
    
    v= tv.getDouble(0.0);
    x = tx.getDouble(0.0);
    y = ty.getDouble(0.0);
    area = ta.getDouble(0.0);

    SmartDashboard.putNumber("tiltencoder", tiltEncoder.getDistance());

    SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
    SmartDashboard.putNumber("Turn rate", gyro.getRate());
    SmartDashboard.putNumber("Pitch+", gyro.getPitch());
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

  }

  @Override
  public void teleopPeriodic() {
    led.lawnGreen();
    // grabber
    if (driverInput.getLeftBumperPressed()) {
      grabber.set(Value.kForward);
    }
    if (driverInput.getRightBumperPressed()) {
      grabber.set(Value.kReverse);
    }
    //extend position
    if (driverInput.getPOV() == 90){
      extend.set(0.1);
    }
    else if (driverInput.getPOV() == 270){
      extend.set(-0.1);
    }
    else{
      extend.set(0);
    //changes tilt position, motor moves continuously to counteract gravity.
    }
    if (driverInput.getYButtonPressed() == true) {
      //sets tilt angle to be parallel with the ground
      tiltAngle = 0.8;
    }
    else if (driverInput.getPOV() == 0) {
      tiltAngle = tiltAngle + 0.05;
    }
    else if (driverInput.getPOV() == 180) {
      tiltAngle = tiltAngle - 0.05;
    }
    //sets tilt angle
    tilt.set(tiltAngle/2);
    //check if "or" operator needs to be changed to "and" in pit
    if(pot.get() < 120 || driverInput.getRightY() > 0){
    //moves lift when potentiometer is in playable position
      lift.set(driverInput.getRightY());
    }
    else{
      lift.set(0.0);
    }
    //uses button pushes to determine limelight drop off height
    if(driverInput.getBButtonPressed()){
      dropPos = "Low";
    }
    else if(driverInput.getAButtonPressed()){
      dropPos = "Mid";
    }
    else if(driverInput.getXButtonPressed()){
      dropPos = "High";
    }
    if (driverInput.getLeftTriggerAxis() > 0.1){
    switch(dropPos){
      case "Low":
      //use lift, tilt, and extend encoders to determine low drop position
      break;
      case "Mid":
      //use lift, tilt, and extend encoders to determine mid drop position
      break;
      case "High":
      //use lift, tilt, and extend encoders to determine high drop position
      break;
    }

    }
    if (driverInput.getRightTriggerAxis() > 0.1) {
      //limelight targeting override
      if(v == 1){
        if(x > -27 && x < -3.5){
        m_right.set(-0.4);
        m_left.set(0.4);
        }
      else if(x < 27 && x > 3.5){
        m_right.set(0.4);
        m_left.set(-0.4);
        }
      else{
          if(area > 0.25){
          m_left.set(0.35);
          m_right.set(0.35);
          }
          else if(area < 0.15){
          m_left.set(-0.35);
          m_right.set(-0.35);
          }
          else{
          m_left.set(0);
          m_right.set(0);
          }
        }
      }
    }
    else{
      //lets controller control drivetrain when ll override not pressed
      m_drive.arcadeDrive(-driverInput.getLeftY(), -driverInput.getLeftX());
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
