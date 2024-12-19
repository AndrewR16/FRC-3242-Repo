/*
--------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
//import packages
package frc.robot;
//motor import
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//basic framework for robot, includes major classes and functionality
import edu.wpi.first.wpilibj.TimedRobot;
//import for use of an xboxcontroller(duh)
import edu.wpi.first.wpilibj.XboxController;
//basic drivetrain import, includes tank drive and omniwheel drive
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//digitalinput import, controls 1/0 sensors such as a limit switch or infrared
import edu.wpi.first.wpilibj.DigitalInput;
//motorcontrollergroup import, allows for motors to be grouped together, particularly useful for declaring tank/differential drives
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//other motorcontroller type, used on jagbot for shooting
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
//import to control motor speeds of VictorSPX
import com.ctre.phoenix.motorcontrol.ControlMode;
//below 4 are used for limelight, building a network table, and adding and reading entries from said table
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
//adds timer for auto
import edu.wpi.first.wpilibj.Timer;
public class Robot extends TimedRobot {;
  //limelight calculation variables
  double targetOffsetAngle_Vertical;
  double limelightMountAngleDegrees;
  double limelightLensHeightInches;
  double goalHeightInches;
  double angleToGoalDegrees;
  double angleToGoalRadians;

  double distanceFromLimelightToGoalInches;

  //declares variables used for limelight
  NetworkTable table;
  NetworkTableEntry tx;
  NetworkTableEntry ty;
  NetworkTableEntry ta;
  NetworkTableEntry tv;
  double v;
  double x;
  double y;
  double area;
  
  //declares motors
  WPI_TalonSRX m_Left = new WPI_TalonSRX(10); 
  WPI_TalonSRX m_Left2 = new WPI_TalonSRX(9);
  MotorControllerGroup m_left = new MotorControllerGroup(m_Left,m_Left2); //left side
  WPI_TalonSRX m_Right = new WPI_TalonSRX(12);
  WPI_TalonSRX m_Right2 = new WPI_TalonSRX(11);
  MotorControllerGroup m_right = new MotorControllerGroup(m_Right,m_Right2);   //right side
  WPI_TalonSRX shooterready = new WPI_TalonSRX(8); //shooter motor 
  VictorSPX shooterpos1 = new VictorSPX(2);  //motor after intake
  VictorSPX shooterpos2 = new VictorSPX(3);  //second motor after intake
  VictorSPX shooterpos3 = new VictorSPX(4);  //third motor after intake
  VictorSPX intake = new VictorSPX(1);  //intake

  //infrared switches
  DigitalInput firstPos = new DigitalInput(1);
  DigitalInput secondPos = new DigitalInput(3);
  DigitalInput thirdPos = new DigitalInput(5); 
  Timer timer = new Timer();
  double motorSpeed = Var.mSpeed;  //sets local motor speed to the one in Var.jar
  int shooter = Var.Shooter;//sets shooter variable for use with the switch statement 
  private final DifferentialDrive m_drive = new DifferentialDrive(m_left,m_right); //declares differential drive, our main drive train class
  private final XboxController m_driverController = new XboxController(1); //declares an instance of the xbox controller class
  @Override()
  public void disabledInit() {
      //disables all shooting motors upon robot being disabled, effectively resetting the robot after each run
      intake.set(ControlMode.PercentOutput, 0);
      shooterpos1.set(ControlMode.PercentOutput, 0);
      shooterpos2.set(ControlMode.PercentOutput, 0);
      shooterpos3.set(ControlMode.PercentOutput,0);
      shooterready.set(ControlMode.PercentOutput,0);
      m_Left.set(0);
      m_Left2.set(0);
      m_Right.set(0);
      m_Right2.set(0);
      //NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
  }
  @Override
  public void robotInit() {
    //NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
    //inverts motors on the right side of the robot so all motors spin in the correct direction
   m_right.setInverted(true);
   shooterready.setInverted(true);
   intake.setInverted(true);
   shooterpos1.setInverted(true);
   shooterpos2.setInverted(true);
   shooterpos3.setInverted(true);
    System.out.println("Robot Initialized - latest code");
  }
  @Override
  public void testPeriodic(){
    shooterpos3.set(ControlMode.PercentOutput,1);
    shooterpos2.set(ControlMode.PercentOutput,1);
    shooterpos1.set(ControlMode.PercentOutput,1);
  }
  @Override()
  public void robotPeriodic(){
    //declares the shooter variable based off certain conditionals, used with the switch statement in autonomous periodic
    if(m_driverController.getBackButtonPressed()){ 
    shooter = 5;
    }
    if(m_driverController.getStartButtonPressed()){
    shooter = 6;
    } 
    if(thirdPos.get() == false){ 
    shooter = 1;  
    }
    if (thirdPos.get() == false && secondPos.get() == false){  
    shooter = 2;
    }
    if(thirdPos.get() == false && secondPos.get() == false && firstPos.get() == false){
    shooter = 3;
    }
    if(m_driverController.getRightTriggerAxis()>0.5){
    shooter = 4; 
    }
    if(m_driverController.getRightBumperPressed()){
    shooter = 7;
    }
    table = NetworkTableInstance.getDefault().getTable("limelight");
    tx = table.getEntry("tx");
    ty = table.getEntry("ty");
    ta = table.getEntry("ta");
    tv = table.getEntry("tv");
    
    v= tv.getDouble(0.0);
    x = tx.getDouble(0.0);
    y = ty.getDouble(0.0);
    area = ta.getDouble(0.0);

      //limelight calculation variables
  targetOffsetAngle_Vertical = ty.getDouble(0.0);
  limelightMountAngleDegrees = 25.0;
  limelightLensHeightInches = 23.0;
  goalHeightInches = 23.0;
  angleToGoalDegrees = limelightMountAngleDegrees + targetOffsetAngle_Vertical;
  angleToGoalRadians = angleToGoalDegrees * (3.14159 / 180.0);

  distanceFromLimelightToGoalInches = (goalHeightInches - limelightLensHeightInches)/Math.tan(angleToGoalRadians);
  }
  @Override()
  public void autonomousInit(){
    //resets timer and starts it again
    timer.reset();
    timer.start();  
  }
  @Override()
  public void autonomousPeriodic() {
    if(v == 1){
      if(x > -27 && x < -4.5){
      m_right.set(-0.25);
      m_left.set(0.25);
      }
    else if(x < 27 && x > 4.5){
      m_right.set(0.25);
      m_left.set(-0.25);
      }
    else{
        if(area > 0.6){
        m_left.set(0.35);
        m_right.set(0.35);
        }
        else if(area < 0.5){
        m_left.set(-0.35);
        m_right.set(-0.35);
        }
        else{
        m_left.set(0);
        m_right.set(0);
        }
    }
  }


System.out.println(x);
System.out.println(v);
System.out.println(y);
System.out.println(area);
  }
  @Override()
  public void teleopInit(){
  }

  @Override()
  public void teleopPeriodic(){ 
    //limelight override test
    if(m_driverController.getLeftTriggerAxis() > 0){
      if(v == 1){
        if(x > -27 && x < -4.5){
        m_right.set(-0.4);
        m_left.set(0.4);
        }
      else if(x < 27 && x > 4.5){
        m_right.set(0.25);
        m_left.set(-0.25);
        }
      else{
          if(area > 0.6){
          m_left.set(0.35);
          m_right.set(0.35);
          }
          else if(area < 0.5){
          m_left.set(-0.35);
          m_right.set(-0.35);
          }
    }  
   }
  }
  else{
     //takes in controller input and sends to Talons to drive
   m_drive.arcadeDrive(m_driverController.getLeftY(), m_driverController.getLeftX());
  }
  switch (shooter){
    case 5:
    intake.set(ControlMode.PercentOutput, -1);
    shooterpos1.set(ControlMode.PercentOutput, -1);
    shooterpos2.set(ControlMode.PercentOutput, -1);
    shooterpos3.set(ControlMode.PercentOutput,-1);
    shooterready.set(ControlMode.PercentOutput,0);
    System.out.println("reversing motors");
    break;
    case 6:
    intake.set(ControlMode.PercentOutput, 0);
    shooterpos1.set(ControlMode.PercentOutput, 0);
    shooterpos2.set(ControlMode.PercentOutput, 0);
    shooterpos3.set(ControlMode.PercentOutput,0);
    shooterready.set(ControlMode.PercentOutput,0);
    System.out.println("emergency stop");
    break;
    case 4:
    intake.set(ControlMode.PercentOutput, 1);
    shooterpos1.set(ControlMode.PercentOutput, 1);
    shooterpos2.set(ControlMode.PercentOutput, 1);
    shooterpos3.set(ControlMode.PercentOutput,1);
    shooterready.set(ControlMode.PercentOutput,10); 
    System.out.println("shooters firing");
    break;
    case 3:
    intake.set(ControlMode.PercentOutput, 0);
    shooterpos1.set(ControlMode.PercentOutput, 0);
    shooterpos2.set(ControlMode.PercentOutput, 0);
    shooterpos3.set(ControlMode.PercentOutput, 0);
    System.out.println("third position set");
    break;
    case 2:
    intake.set(ControlMode.PercentOutput, 1);
    shooterpos1.set(ControlMode.PercentOutput, 1);
    shooterpos2.set(ControlMode.PercentOutput, 0);
    shooterpos3.set(ControlMode.PercentOutput, 0);
    System.out.println("second position set");
    break;
    case 1:
    intake.set(ControlMode.PercentOutput, 1);
    shooterpos1.set(ControlMode.PercentOutput, 1);
    shooterpos2.set(ControlMode.PercentOutput, 1);
    shooterpos3.set(ControlMode.PercentOutput,0);
    System.out.println("first position set");
    break;
    case 7:
    intake.set(ControlMode.PercentOutput, 1);
    shooterpos1.set(ControlMode.PercentOutput,1);
    shooterpos2.set(ControlMode.PercentOutput, 1);
    shooterpos3.set(ControlMode.PercentOutput, 1);
    shooterready.set(ControlMode.PercentOutput,0);
    System.out.println("returning to default motors");
    default:
    intake.set(ControlMode.PercentOutput, 1);
    shooterpos1.set(ControlMode.PercentOutput, 1);
    shooterpos2.set(ControlMode.PercentOutput, 1);
    shooterpos3.set(ControlMode.PercentOutput,1);
    System.out.println("default motors running");
    break;
  }

  

  }
}


