// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;

import java.security.spec.MGF1ParameterSpec;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.CounterBase;
//numatic import
import edu.wpi.first.wpilibj.DoubleSolenoid;
//.
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import frc.AutoController;
//import frc.LED;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;

//import com.revrobotics.CANSparkMax;
import frc.LED;
/*
 * Pigeon gyro import
 */
import com.ctre.phoenix.sensors.PigeonIMU;
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
q   * initialization code.
   */

//drivetrain
private final WPI_TalonSRX m_LeftFront = new WPI_TalonSRX(3);
private final WPI_TalonSRX m_RightFront = new WPI_TalonSRX(7);
private final WPI_TalonSRX m_LeftBack = new WPI_TalonSRX(4);
private final WPI_TalonSRX m_RightBack = new WPI_TalonSRX(8);
private final MecanumDrive mDrive = new MecanumDrive(m_LeftFront, m_LeftBack, m_RightFront, m_RightBack);


//displayVision = CameraServer.getInstance();
//UsbCamera Cam = displayVision.startAutomaticCapture()
//lift
private final WPI_TalonSRX Lift1 = new WPI_TalonSRX(1);
private final WPI_TalonSRX Lift2 = new WPI_TalonSRX(2);
//Numatics
//Check if it is a single solenoid or double
//grabber is whatever you want to call it
//needs import ^^^
private DoubleSolenoid grabber = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

//IntakeTalons
private final WPI_TalonSRX leftIntake = new WPI_TalonSRX(6);
private final WPI_TalonSRX rightIntake = new WPI_TalonSRX(5);
 
private final Timer timer = new Timer();
private final AutoController Auto = new AutoController(m_LeftFront,m_LeftBack,m_RightFront,m_RightBack,Lift1,Lift2,grabber,leftIntake,rightIntake,timer.get());
//controller
   //smart dashboard
   private static final String kRed1 = "Red One";
   private static final String kRed2 = "Red Two";
   private static final String kBlue1 = "Blue One";
   private static final String kBlue2 = "Blue Two";
   private String autoSelected;
   private final SendableChooser<String>chooser = new SendableChooser<>();
   LED led = new LED(new Spark(0));

  public Encoder liftEncoder = new Encoder(8, 9);
  public Encoder frEncoder = new Encoder(2, 3, false, CounterBase.EncodingType.k2X);
 // frEncoder.setDistancePerPulse(0.5);
  private final XboxController controller = new XboxController(0);
  @Override
  public void robotInit() {
    
    rightIntake.setInverted(true);
    Lift1.setInverted(true);
    Lift2.setInverted(true);
    m_RightBack.setInverted(true);
    m_RightFront.setInverted(true);
    CameraServer.startAutomaticCapture();
    chooser.setDefaultOption("Red 1", kRed1);
    chooser.addOption("Red 2", kRed2);
    chooser.addOption("Blue 1", kBlue1);
    chooser.addOption("Blue 2", kBlue2);
    
    SmartDashboard.putData("Auto Choices", chooser);
    
  }
  

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
    autoSelected = chooser.getSelected();
    //put right side motor inversions into robot initialization, if this creates further issues, invert motors in auto init later
      
  }
  @Override
  public void autonomousPeriodic() { 
    switch (autoSelected){
      case kRed1:
      System.out.println("Red One");
     if (timer.get()< 0.75){
      Auto.lift();
      } else if (timer.get()<1.19){
        Auto.liftStop();
        Auto.forward(.5);
      }else if (timer.get()<1.49){
        Auto.stop();
      } 
      else if (timer.get()< 1.93){
        Auto.liftDrop();
      }else if (timer.get()<2){
        Auto.intakeOut();
      } else if (timer.get()<2.3){
        Auto.intakeStop();
      } else if (timer.get()<4){
        Auto.backward(.5);
      } else if (timer.get()<4.5){
       Auto.stop();
      }else if (timer.get()<5){
        Auto.left90();
      } else if (timer.get()<6){
        Auto.backLeftArc();
      } else if (timer.get()<6.5){
        Auto.left90();
      }

      break;
      case kRed2:
        /*PigeonIMU _pigeon = new PigeonIMU(0);
        double [] ypr = new double[3];
        _pigeon.GetYawPitchRoll(ypr);
        System.out.println("Yaw: " + ypr[0]);
        */
      System.out.println("Red Two");
      break;
      case kBlue1:
      System.out.println("Blue One");
      break;
      case kBlue2:
      System.out.println("Blue Two");
      break;
    }
    
    if (timer.get() % 2 <= 1){
      led.gold();
    } else {
      led.darkGreen();
    }
      
    }
  
     


  @Override
  public void teleopInit() {
    timer.reset();
    timer.start();

  }

  @Override
  public void teleopPeriodic() {

    
    mDrive.driveCartesian(-controller.getLeftY()/2, controller.getRightX(), -controller.getLeftX()/2);
  

  if(timer.get() < 2){
    led.darkGreen();
  }
  else if(timer.get()<4){
    led.gold();
  }
  else{
    timer.reset();
  }
  
    if(controller.getLeftBumperPressed()){
      System.out.println("Lift Down");
      //Lift goes down because of gravity
      Lift1.set(0.0);
      Lift2.set(0.0);
    }
    if(controller.getLeftBumperReleased()){
      //stops lift
      Lift1.set(0.1);
      Lift2.set(0.1);
    }
    //Encoder
    if(controller.getRightBumper()){
      //Raises lift
   
     if(Math.abs(liftEncoder.get()) < 550){
      
        Lift1.set(0.4);
        Lift2.set(0.4);
      }
      else{
        Lift1.set(0.1);
        Lift2.set(0.1);
      }

   
    }
    if(controller.getRightBumperReleased()){
      //stops lift
      Lift1.set(0.1);
      Lift2.set(0.1); 
    }
    if(controller.getXButtonPressed()){
      //stops lift
      Lift1.set(0.1);
      Lift2.set(0.1); 
    }
    if(controller.getLeftTriggerAxis() > 0.1){
      //open grabber
      grabber.set(Value.kForward);
    }
    if(controller.getYButtonPressed()){
      //reverse intake wheels
      leftIntake.set(-0.2f);
      rightIntake.set(-0.2f);
    }
    if(controller.getRightTriggerAxis() > 0.1){
      //close grabber
      grabber.set(Value.kReverse);
    }
    if(controller.getAButton()){
      //intake wheels
      leftIntake.set(0.2f);
      rightIntake.set(0.2f);
    }
    if(controller.getBButton()){
      //stop intake wheels
      leftIntake.set(0.0f);
      rightIntake.set(0.0f);
    }
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    
  }

  @Override
  public void testPeriodic() {
    timer.reset();
    timer.start();
    
  }

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}
