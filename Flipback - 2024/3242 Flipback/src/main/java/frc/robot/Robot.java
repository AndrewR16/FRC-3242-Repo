// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
//The basics

import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.revrobotics.CANSparkLowLevel.MotorType;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagDetection;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static frc.robot.Command.*;

public class Robot extends TimedRobot {

  // Wheel Motors
  private final CANSparkMax m_frontLeft = new CANSparkMax(Constants.frontLeftPort, MotorType.kBrushed);
  private final CANSparkMax m_frontRight = new CANSparkMax(Constants.frontRightPort, MotorType.kBrushed);
  private final CANSparkMax m_backLeft = new CANSparkMax(Constants.backLeftPort, MotorType.kBrushed);
  private final CANSparkMax m_backRight = new CANSparkMax(Constants.backRightPort, MotorType.kBrushed);

  // Other Motors
  // private final CANSparkMax m_shooter = new CANSparkMax(Constants.shooterPort,
  // MotorType.kBrushed);
  // private final CANSparkMax m_ampShooter = new
  // CANSparkMax(Constants.ampShootingPort, MotorType.kBrushed);
  private final CANSparkMax m_intake = new CANSparkMax(Constants.intakePort, MotorType.kBrushed);
  private final CANSparkMax m_flipBack = new CANSparkMax(Constants.flipBackPort, MotorType.kBrushed);
  /*
   * // Infrared Sensors
   * private final DigitalInput intakeInf = new
   * DigitalInput(Constants.intakeInfPort);
   * private final DigitalInput ampInf = new DigitalInput(Constants.ampInfPort);
   * 
   * // Limit Switches
   * private final DigitalInput frontSwitch = new
   * DigitalInput(Constants.intakeFrontSwitchPort);
   * private final DigitalInput backSwitch = new
   * DigitalInput(Constants.intakeBackSwitchPort);
   * private final DigitalInput ampDownSwitch = new
   * DigitalInput(Constants.ampDownSwitchPort);
   */

  // Mecanum Drive stuff

  private final MecanumDrive mDrive = new MecanumDrive(m_frontLeft, m_backLeft, m_frontRight, m_backRight);
  private final XboxController controller = new XboxController(0);

  // Autocontroller
  // AutoController auto = new AutoController(m_frontRight, m_frontLeft,
  // m_backRight, m_backLeft, m_shooter, m_intake, m_flipBack, m_ampShooter,
  // intakeInf, frontSwitch, backSwitch, ampInf);

  // Gyro
  private final WPI_PigeonIMU gyro = new WPI_PigeonIMU(Constants.gyroPort);

  private double correctedGyroAngle() {
    return -gyro.getAngle();
  }

  // boolean
  private boolean stopEnabled = false;
  private boolean reverseEnabled = false;

  private boolean targetAprilTag = false;

  Timer timer = new Timer();
  Timer ampTimer = new Timer();

  @Override
  public void robotInit() {
    m_frontRight.setInverted(true);
    m_backRight.setInverted(true);

    SmartDashboard.putData("MecanumDrive", mDrive);
  }

  @Override
  public void robotPeriodic() {
    // Gyro in smart dashboard
    SmartDashboard.putNumber("Gyro Heading", correctedGyroAngle());
    SmartDashboard.putNumber("Read Heading", correctedGyroAngle());
    SmartDashboard.putNumber("Adjusted Heading", Proportional.adjustHeadingValue(gyro.getAngle()));
  }

  @Override
  public void autonomousInit() {
    // Command system
    resetCommandValues();

    // April tag targeting
    targetAprilTag = false;
  }

  @Override
  public void autonomousPeriodic() {
    // Command system
    resetCommandId();

    // Limelight
    NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry xOffsetEntry = limelight.getEntry("tx");
    NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");
    NetworkTableEntry tagIDEntry = limelight.getEntry("tid");

    double xOffset = xOffsetEntry.getDouble(0.0);
    double targetArea = targetAreaEntry.getDouble(0.0);
    double tagID = tagIDEntry.getDouble(0.0);

    // Limelight smart dashboard
    SmartDashboard.putNumber("X Offest", xOffset);
    SmartDashboard.putNumber("Target Area", targetArea);
    SmartDashboard.putNumber("April Tag ID", tagID);
    SmartDashboard.putString("Dectected Station", LimelightNode.getNodeName((int) tagID));

    // Target april tag
    if (targetAprilTag) {
      if (runOnce()) {
        resetCommandValues();
      } else if (runTillComplete()) {
        // TODO: Align with april tag using gyro
        SmartDashboard.putString("Current Task", "Aligning Heading");
        double adjustedHeadingError = Proportional.adjustHeadingValue(LimelightNode.getNodeHeading((int) tagID) - correctedGyroAngle());
        SmartDashboard.putNumber("Adjusted Heading", adjustedHeadingError);
        double driveSpeed = Proportional.calculatePDrive(adjustedHeadingError, 0.006, 2);
        mDrive.driveCartesian(0.0, 0.0, driveSpeed);
  
        // TODO: Target an april tag and align x position
        driveSpeed = Proportional.calculatePDrive(xOffset, 0.04, 3);
        mDrive.driveCartesian(0.0, driveSpeed, 0.0);
  
        checkIfCompleted(driveSpeed, 0.0);
      } else if (runTillComplete()) {
        // TODO (Test): Target an april tag and align y position
        SmartDashboard.putString("Current Task", "Aligning Y");
        double error = LimelightNode.getNodeArea((int) tagID) - targetArea;
        double driveSpeed = Proportional.calculatePDrive(error, 0.8, 0.05);
        mDrive.driveCartesian(driveSpeed, 0.0, 0.0);
  
        checkIfCompleted(driveSpeed, 0.0);
      }
    }
  }

  @Override
  public void teleopInit() {

  }

  @Override
  public void teleopPeriodic() {
    double vertical = controller.getLeftY();
    double strafing = controller.getRightX();
    double rotation = controller.getLeftX();

    // Mechanum Drive
    // *Dead Zones
    if (Math.abs(vertical) <= 0.2) {
      vertical = 0;
    }
    if (Math.abs(strafing) <= 0.2) {
      strafing = 0;
    }
    if (Math.abs(rotation) <= 0.2) {
      rotation = 0;
    }
    mDrive.driveCartesian(-vertical / 2, strafing / 2, rotation / 2);
    /*
     * if (controller.getLeftTriggerAxis() > .75) {
     * if (ampInf.get() == false) {
     * m_shooter.set(Constants.converyorSpeed);
     * } else {
     * m_shooter.set(0);
     * if (ampTimer.get() == 0) {
     * ampTimer.reset();
     * ampTimer.start();
     * } else if (ampTimer.get() < Constants.ampTimerLength && ampTimer.get() != 0)
     * {
     * m_ampShooter.set(Constants.ampShootingSpeed);
     * } else if (ampTimer.get() >= Constants.ampTimerLength) {
     * if (ampDownSwitch.get() == false) {
     * m_ampShooter.set(Constants.ampShootingSpeed);
     * }
     * }
     * }
     * }
     * 
     * //*Shooting for Speaker
     * if (controller.getRightTriggerAxis() > 0.75) {
     * m_shooter.set(Constants.shooterSpeed);
     * } else {
     * m_shooter.set(0);
     * }
     * //Amp Scoring
     * if(controller.getLeftTriggerAxis() > 0.75){
     * if(ampInf.get() == true){
     * m_ampShooter.set(Constants.ampShootingSpeed);
     * }
     * else{
     * m_shooter.set(Constants.converyorSpeed);
     * }
     * }
     */

    // *intake override
    // if (controller.getPOV() < 95 && controller.getPOV() > 85) {
    // if(backSwitch.get() == false){
    // m_flipBack.set(Constants.flipBackSpeed);
    // }
    // } else if (controller.getPOV() < 275 && controller.getPOV() > 265) {

    // if(frontSwitch.get() == false){
    // m_flipBack.set(-Constants.flipBackSpeed);
    // }
    // }
    // *rollers */
    // if(controller.getAButtonPressed()){
    // m_intake.set(Constants.intakeSpeed);
    // }
    /*
     * //*intake auto
     * if (intakeInf.get() == true) {
     * m_intake.set(0);
     * // rotate m_flipback however many degrees it should.
     * if(backSwitch.get() == false){
     * m_flipBack.set(Constants.flipBackSpeed);
     * }
     * }
     * else {
     * m_intake.set(Constants.intakeSpeed);
     * // rotate m_flipback however many degrees it should.
     * if(frontSwitch.get() == false){
     * m_flipBack.set(-Constants.flipBackSpeed);
     * }
     * }
     */
    // !emergency stop
    if (controller.getXButtonPressed()) {
      stopEnabled = !stopEnabled;
    }
    // if (stopEnabled == true){
    // m_shooter.set(0);
    // m_intake.set(0);
    // m_flipBack.set(0);
    // m_ampShooter.set(0);
    // add all non-wheel motors
    // }

    // emergancy reverse
    if (controller.getYButton()) {
      reverseEnabled = !reverseEnabled;

    }
    // if(reverseEnabled == true){
    // m_shooter.set(-Constants.shooterSpeed);
    // m_intake.set(-Constants.intakeSpeed);
    // m_flipBack.set(-1);
    // m_ampShooter.set(-1);
    // }

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
    m_frontLeft.set(Constants.driveSpeed);
    m_backLeft.set(Constants.driveSpeed);
    m_frontRight.set(Constants.driveSpeed);
    m_backRight.set(Constants.driveSpeed);
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
// lift goes down because of gravity

// !
// * */
