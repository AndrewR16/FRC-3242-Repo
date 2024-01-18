// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.hal.AllianceStationID;
// TODO: Update color sensor firmware
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.Timer;  
//import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
//import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.motorcontrol.PWMTalonFX;

public class Robot extends TimedRobot {
  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
  
  private final WPI_TalonSRX m_leftFrontDrive = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_leftBackDrive = new WPI_TalonSRX(0);
  //MotorControllerGroup m_left = new MotorControllerGroup(m_leftFrontDrive, m_leftBackDrive);
  private final WPI_TalonSRX m_rightFrontDrive = new WPI_TalonSRX(3);
  private final WPI_TalonSRX m_rightBackDrive = new WPI_TalonSRX(1);
  //MotorControllerGroup m_right = new MotorControllerGroup(m_rightFrontDrive, m_rightBackDrive);
  private final MecanumDrive m_robotDrive = new MecanumDrive(m_leftFrontDrive, m_leftBackDrive, m_rightFrontDrive, m_rightBackDrive);
  private final XboxController m_stick = new XboxController(0);
 Timer timer = new Timer();
 //Option choices = new Option("choice 1","choice 1b", "choice 2", "choice 2b");
// Alliance color = DriverStation.getAlliance();
private final ColorMatch m_colorMatcher = new ColorMatch();
private final Color kBlueTarget = new Color(0.25, 0.45, 0.28);
private final Color kGreenTarget = new Color(0.22, 0.50, 0.25);
private final Color kRedTarget = new Color(0.30, 0.45,0.24);
private final Color kYellowTarget = new Color(0.32,0.49,0.18);
private final Color kPurpleTarget = new Color(0.24, 0.44,0.30);
@Override
public void testInit(){
  
}
@Override
public void robotInit() {
m_colorMatcher.addColorMatch(kBlueTarget);
m_colorMatcher.addColorMatch(kGreenTarget);
m_colorMatcher.addColorMatch(kRedTarget);
m_colorMatcher.addColorMatch(kYellowTarget);
m_colorMatcher.addColorMatch(kPurpleTarget);
//System.out.println(choices.getValue());


}
@Override
public void robotPeriodic(){
  Color detectedColor = m_colorSensor.getColor();

  String colorString;
  ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);
  if(match.color == kBlueTarget){
    colorString = "Blue";
  }else if (match.color == kRedTarget){
    colorString = "Red";
  }else if (match.color == kGreenTarget){
    colorString = "Green";
  }else if (match.color == kYellowTarget){
    colorString = "Yellow";
}else if(match.color == kPurpleTarget){
colorString = "Purple";
}
else{
  colorString = "Unknown";
}
SmartDashboard.putNumber("Red", detectedColor.red);
SmartDashboard.putNumber("Green", detectedColor.green);
SmartDashboard.putNumber("Blue", detectedColor.blue);
SmartDashboard.putNumber("Confidence", match.confidence);
SmartDashboard.putString("Detected Color", colorString);


}
@Override
public void autonomousInit() {
  timer.reset();
  timer.start();
  m_rightFrontDrive.setInverted(true);
  m_rightBackDrive.setInverted(true);
 
}
@Override
public void autonomousPeriodic() {
  Color detectedColor = m_colorSensor.getColor();

  ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);
while(match.color != kYellowTarget){
leftTurn();
}

 
}



@Override
public void teleopInit() {
  m_rightFrontDrive.setInverted(true);
  m_rightBackDrive.setInverted(true);
}

@Override
public void teleopPeriodic() {
 // m_robotDrive.arcadeDrive(m_stick.getLeftX(), m_stick.getLeftY());




 


  m_robotDrive.driveCartesian((-m_stick.getLeftY()/2), (m_stick.getRightX()/2), (m_stick.getLeftX()/2)); 

  
}
  public void Back(){
    m_leftFrontDrive.set(-.3);
    m_rightFrontDrive.set(-.3);
   m_leftBackDrive.set(-.3);
   m_rightBackDrive.set(-.3); 
  }

  public void rightTurn(){
    m_leftFrontDrive.set(.48);
    m_rightFrontDrive.set(-.48);
   m_leftBackDrive.set(.48);
   m_rightBackDrive.set(-.48);
  } 


public void leftTurn(){
  m_leftFrontDrive.set(-.48);
  m_rightFrontDrive.set(.48);
 m_leftBackDrive.set(-.48);
 m_rightBackDrive.set(.48);
}
public void Stop(){
  m_leftFrontDrive.set(0);
   m_rightFrontDrive.set(0);
  m_leftBackDrive.set(0);
  m_rightBackDrive.set(0);
}
public void straight(){
  m_leftFrontDrive.set(.3);
   m_rightFrontDrive.set(.3);
  m_leftBackDrive.set(.3);
  m_rightBackDrive.set(.3); 
}
public void square(){
  if (timer.get()<1){
    straight();
}
else if (timer.get()<2){
Stop();
}
else if (timer.get()<3){
  leftTurn();
}
else if (timer.get()<4){
  Stop();
}
else if (timer.get()<5){
  straight();
}
else if (timer.get()<6){
Stop();
}
else if (timer.get()<7){
leftTurn();
}
else if (timer.get()<8){
Stop();
}
else if (timer.get()<9){
  straight();
} 
else if (timer.get()<10){
Stop();
}
else if (timer.get()<11){
leftTurn();
}
else if (timer.get()<12){
Stop();
}
else if (timer.get()<13){
  straight();
}
else if (timer.get()<14){
Stop();
}
else if (timer.get()<15){
leftTurn();
}
else if (timer.get()<16){
Stop();
}

}
public void LeftyDrive(double num){
  m_leftBackDrive.set(num);
  m_rightFrontDrive.set(num);
  m_rightBackDrive.set(-num);
  m_leftFrontDrive.set(-num);
}
public void RightyDrive(double num){
  LeftyDrive(-num);

}

}


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
 