// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
 
// LED

// Motors
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

// Drive and control
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.XboxController;

// Gyro
// import com.ctre.phoenix.sensors.WPI_PigeonIMU;

// Limelight
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

// Limit switches
import edu.wpi.first.wpilibj.DigitalInput;

// SmartDashboard
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
// Potentiometer
import edu.wpi.first.wpilibj.AnalogPotentiometer;
public class Robot extends TimedRobot {
  // Wheel motors
  private final CANSparkMax m_frontLeftDrive = new CANSparkMax(Constants.frontLeftPort, MotorType.kBrushed);
  private final CANSparkMax m_frontRightDrive = new CANSparkMax(Constants.frontRightPort, MotorType.kBrushed);
  private final CANSparkMax m_backLeftDrive = new CANSparkMax(Constants.backLeftPort, MotorType.kBrushed);
  private final CANSparkMax m_backRightDrive = new CANSparkMax(Constants.backRightPort, MotorType.kBrushed);

  // Shooter motors
  private final CANSparkMax m_topLeftShooter = new CANSparkMax(Constants.topLeftShooterPort, MotorType.kBrushed);
  private final CANSparkMax m_topRightShooter = new CANSparkMax(Constants.topRightShooterPort, MotorType.kBrushed);
  private final CANSparkMax m_bottomLeftShooter = new CANSparkMax(Constants.bottomLeftShooterPort, MotorType.kBrushed);
  private final CANSparkMax m_bottomRightShooter = new CANSparkMax(Constants.bottomRightShooterPort,
      MotorType.kBrushed);

  // Other motors
  private final CANSparkMax m_intake = new CANSparkMax(Constants.intakePort, MotorType.kBrushed);
  private final CANSparkMax m_flipBack = new CANSparkMax(Constants.flipBackPort, MotorType.kBrushed);
  private final CANSparkMax m_shooterRotator = new CANSparkMax(Constants.shooterLinearA, MotorType.kBrushed);

  @SuppressWarnings("unused")
  private final AnalogPotentiometer m_rotatorPotentiometer = new AnalogPotentiometer(0);

  // Infrared sensors

  // Limit switches
  private final DigitalInput switch_intakeUp = new DigitalInput(Constants.intakeBackSwitchPort);
  private final DigitalInput switch_intakeDown = new DigitalInput(Constants.intakeFrontSwitchPort);

  // private final DigitalInput switch_ampDown = new
  // DigitalInput(Constants.ampDownSwitchPort);

  // Mecanum drive
  private final MecanumDrive mDrive = new MecanumDrive(m_frontLeftDrive, m_backLeftDrive, m_frontRightDrive,
      m_backRightDrive);
  private final XboxController controller = new XboxController(0);

  // TODO: Gyro (Adjust port)
  // private final WPI_PigeonIMU gyro = new WPI_PigeonIMU(0);

  // private double correctedGyroAngle() {
  // return -gyro.getAngle();
  // }

  // Flipback Timer
  private final Timer flipbackTimer = new Timer();

  // Command system
  CommandSystem autonomousCommands = new CommandSystem();
  CommandSystem teleopCommands = new CommandSystem();
  CommandSystem aprilTagCommands = new CommandSystem();

  // Team color chooser
  private static final String blueTeam = "Blue";
  private static final String redTeam = "Red";
  private final SendableChooser<String> teamColorSelector = new SendableChooser<String>();

  // Intake booleans
  private boolean intakeFlipping = false;
  private boolean isIntakeUp = false;

  // Emergency stop and reverse
  private boolean emergencyStopEnabled = false;
  private boolean reverseMotorsEnabled = false;
  private boolean reverseDriveDirection = false;

  @Override
  public void robotInit() {
    // Set up drive motors
    m_frontRightDrive.setInverted(true);
    m_backRightDrive.setInverted(true);
    m_backRightDrive.setInverted(true);
    m_backLeftDrive.setInverted(false);

    // Set up shooter motors
    // m_topRightShooter.setInverted(true);
    // m_bottomRightShooter.setInverted(true);
  

    // Mechanum drive in smart dashboard
    SmartDashboard.putData("MecanumDrive", mDrive);

    // Team color chooser
    teamColorSelector.setDefaultOption("Blue Team", blueTeam);
    teamColorSelector.addOption("Red Team", redTeam);
    SmartDashboard.putData("Team Color", teamColorSelector);
  }

  @Override
  public void robotPeriodic() {
    // Limit Switches
    SmartDashboard.putBoolean("FlipBack Up", switch_intakeUp.get() == false);
    SmartDashboard.putBoolean("FlipBack Down", switch_intakeDown.get() == false);

    // Gyro in smart dashboard
    // SmartDashboard.putNumber("Gyro Heading", correctedGyroAngle());
    // SmartDashboard.putNumber("Read Heading", correctedGyroAngle());
    // SmartDashboard.putNumber("Adjusted Heading",
    // Proportional.adjustHeadingValue(gyro.getAngle()));
  }

  @Override
  public void autonomousInit() {
    // Command system
    autonomousCommands.resetCommandValues();
    aprilTagCommands.resetCommandValues();

    // Team color selector
    LimelightNode.setupNodeIds(teamColorSelector.getSelected());
  }

  @Override
  public void autonomousPeriodic() {
    // Command system
    autonomousCommands.resetCommandId();

    // Limelight
    NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry xOffsetEntry = limelight.getEntry("tx");
    NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");
    NetworkTableEntry aprilTagIDEntry = limelight.getEntry("tid");

    double xOffset = xOffsetEntry.getDouble(0.0);
    double targetArea = targetAreaEntry.getDouble(0.0);
    double targetId = aprilTagIDEntry.getDouble(0.0);

    // Limelight smart dashboard
    SmartDashboard.putNumber("X Offest", xOffset);
    SmartDashboard.putNumber("Target Area", targetArea);
    SmartDashboard.putNumber("April Tag ID", targetId);
    SmartDashboard.putString("Dectected Station", LimelightNode.getNodeName((int) targetId));

    // Autonomous Routine
    if (autonomousCommands.runTillComplete()) {
      // TODO: Shoot into speaker
    } else if (autonomousCommands.runTillComplete()) {
      // TODO: Drive to amplifier

      // Switch over to targeting system once april tag is detected
      if (targetId == LimelightNode.amplifierId) {
        autonomousCommands.commandCompleted();
      }
    } else if (autonomousCommands.runTillComplete()) {
      targetAprilTag(xOffset, targetArea, targetId);
    }
  }

  /**
   * Gets the robot into the ready position to perfrom the action for the node
   * seen.
   */
  private void targetAprilTag(double xOffset, double targetArea, double targetId) {
    aprilTagCommands.resetCommandId();

    if (aprilTagCommands.runTillComplete()) {
      // Align with april tag using gyro
      // double targetingError = LimelightNode.getNodeHeading((int) targetId) -
      // correctedGyroAngle();
      // double adjustedHeadingError =
      // Proportional.adjustHeadingValue(targetingError);
      // double driveSpeed = Proportional.calculatePDrive(adjustedHeadingError, 0.006,
      // 2);
      // mDrive.driveCartesian(0.0, 0.0, driveSpeed);

      // SmartDashboard.putString("Current Task", "Aligning Heading");
      // SmartDashboard.putNumber("Adjusted Heading", adjustedHeadingError);
      // TODO: (Test) Target an april tag and align x position
      // driveSpeed = Proportional.calculatePDrive(xOffset, 0.04, 3);
      // mDrive.driveCartesian(0.0, driveSpeed, 0.0);

      // aprilTagCommands.checkIfCompleted(driveSpeed, 0.0);
    } else if (aprilTagCommands.runTillComplete()) {
      // TODO (Test): Target an april tag and align y position
      SmartDashboard.putString("Current Task", "Aligning Y");
      double error = LimelightNode.getNodeArea((int) targetId) - targetArea;
      double driveSpeed = Proportional.calculatePDrive(error, 0.8, 0.05);
      mDrive.driveCartesian(driveSpeed, 0.0, 0.0);

      aprilTagCommands.checkIfCompleted(driveSpeed, 0.0);
    } else {
      autonomousCommands.commandCompleted();
    }
  }

  @Override
  public void teleopInit() {
    // Team color selector
    LimelightNode.setupNodeIds(teamColorSelector.getSelected());

    // Set up drive motors
    m_frontRightDrive.setInverted(true);
    m_backRightDrive.setInverted(true);
    m_backRightDrive.setInverted(true);
    m_backLeftDrive.setInverted(false);

    // Set up shooter motors
    m_topLeftShooter.setInverted(false);
    m_topRightShooter.setInverted(false);
    m_bottomLeftShooter.setInverted(false);
    m_bottomRightShooter.setInverted(false);

  }

  @Override
  public void teleopPeriodic() {
    // *Mechanum drive
    mDrive.driveCartesian(-Controller.leftStickY(), Controller.rightStickX(), Controller.leftStickX());

    // *Shooting (speaker) (Right Trigger)
    SmartDashboard.putNumber("Shooter Speed", m_topLeftShooter.get());
    if (Controller.rightTrigger()) {
      // Shoot to speaker
      runShooter(Constants.speakerShootingSpeed);
      m_intake.set(-Constants.converyorSpeed);
      
    }
    // *Shooting (amp) (Left Trigger)
    if (Controller.leftTrigger()) {
      // Shoot to amp
      runShooter(Constants.ampShootingSpeed);
       m_intake.set(-Constants.converyorSpeed);
    } 
    // *Sets shooting motors back to zero when not in use
    if(!Controller.rightTrigger() && !Controller.leftTrigger()){
      runShooter(0);
      if(!Controller.dPad_Left() || !Controller.dPad_Right()){
        m_intake.set(0);
      }
    }
    

    // *Shooter rotation (Up and Down on D Pad)
    SmartDashboard.putNumber("Shooter Rotation Speed", m_shooterRotator.get());
    // SmartDashboard.putNumber("Rotation Potentiometer", m_shooterRotator.get());
    if (Controller.dPad_Up()) {
      // Rotate shooter up (Up on D Pad)
      m_shooterRotator.set(Constants.shooterRotatorSpeed);
    } else if (Controller.dPad_Down()) {
      // Rotate shooter down (Down on D Pad)
      m_shooterRotator.set(-Constants.shooterRotatorSpeed);
    } else {
      m_shooterRotator.set(0);
    }

    // *Intake (in and out) (Left and Right on D Pad)
    SmartDashboard.putNumber("Intake Speed", m_intake.get());
    if (Controller.dPad_Left()) {
      // Intake in (Right on D Pad)
      m_intake.set(Constants.intakeSpeed);
    } else if (Controller.dPad_Right()) {
      // Intake out (Left on D Pad)
      m_intake.set(-Constants.intakeSpeed);
    } 

    // *Intake (rotation/flipback) (Left and Right Bumpers)
    // TODO: Test limit switch direction
     if (switch_intakeUp.get() == false) {
     isIntakeUp = true;
     intakeFlipping = false;
     }
     if (switch_intakeDown.get() == false) {
     isIntakeUp = false;
     intakeFlipping = false;
     }

    // Input for fliback
    SmartDashboard.putBoolean("Flipback Enabled", intakeFlipping);
    if (controller.getLeftBumperPressed()) {
      // Start/stop flipback
      intakeFlipping = !intakeFlipping;
      isIntakeUp = false;

      flipbackTimer.reset();
      flipbackTimer.start();
    }
    if (controller.getRightBumperPressed()) {
      // Start/stop flipback
      intakeFlipping = !intakeFlipping;
      isIntakeUp = true;

      flipbackTimer.reset();
      flipbackTimer.start();
    }

    // Flip intake
    if (intakeFlipping) {
      if (isIntakeUp) {
        // *Intake Up (Right Bumper)
        m_flipBack.set(Proportional.calculateFlipbackSpeed(flipbackTimer.get(), 0.6));
      } else {
        // *Intake Down (Left Bumper)
        m_flipBack.set(-Proportional.calculateFlipbackSpeed(flipbackTimer.get(), 0.45));
      }
    } else {
      m_flipBack.set(0);
    }

    // *Emergency stop
    if (controller.getXButtonPressed()) {
      emergencyStopEnabled = !emergencyStopEnabled;
    }
    if (emergencyStopEnabled) {
      // Other motors
      m_topLeftShooter.set(0);
      m_intake.set(0);
      m_flipBack.set(0);
    }

    // *Reverse shooter
    SmartDashboard.putBoolean("Reverse Enabled", reverseMotorsEnabled);
    if (controller.getYButtonPressed()) {
      reverseMotorsEnabled = !reverseMotorsEnabled;

      if (reverseMotorsEnabled) {
        m_topLeftShooter.setInverted(true);
        m_topRightShooter.setInverted(true);
        m_bottomLeftShooter.setInverted(true);
        m_bottomRightShooter.setInverted(true);
      } else {
        m_topLeftShooter.setInverted(false);
        m_topRightShooter.setInverted(false);
        m_bottomLeftShooter.setInverted(false);
        m_bottomRightShooter.setInverted(false);
      }
    }

    // *Reverse Drive Motors
    if (controller.getBButtonPressed()) {
      reverseDriveDirection = !reverseDriveDirection;

      if (reverseDriveDirection) {
        m_frontRightDrive.setInverted(false);
        m_backRightDrive.setInverted(false);
        m_frontLeftDrive.setInverted(true);
        m_backLeftDrive.setInverted(true);
      } else {
        m_frontRightDrive.setInverted(true);
        m_backRightDrive.setInverted(true);
        m_frontLeftDrive.setInverted(false);
        m_backLeftDrive.setInverted(false);
      }
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
  Timer timer = new Timer();
  timer.reset();
  timer.start();
  }

  @Override
  public void testPeriodic() {


 }

    // SmartDashboard.putData("FlipBack Up", switch_intakeUp);
    // SmartDashboard.putData("FlipBack Down", switch_intakeDown);
  

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }

  // *Motor Commands
  private void runShooter(double speed) {
    m_topLeftShooter.set(speed);
    m_topRightShooter.set(speed);
    m_bottomLeftShooter.set(speed);
    m_bottomRightShooter.set(speed);
  }

  // private void shootToSpeaker() {
  // m_shooter.set(Constants.speakerShootingSpeed);
  // }

  // private void shootToAmplifier() {
  // m_shooter.set(Constants.ampShootingSpeed);
  // }

  // private void rotateShooterUp() {
  // m_shooterRotator.set(Constants.shooterRotatorSpeed);
  // }

  // private void rotateShooterDown() {
  // m_shooterRotator.set(-Constants.shooterRotatorSpeed);
  // }

  // private void intakeIn() {
  // m_intake.set(Constants.intakeSpeed);
  // }

  // private void intakeOut() {
  // m_intake.set(-Constants.intakeSpeed);
  // }

  // private void intakeUp() {
  // m_flipBack.set(Proportional.calculateFlipbackSpeed(kDefaultPeriod,
  // kDefaultPeriod));
  // }

  // private void intakeDown() {
  // m_flipBack.set(-Constants.flipBackSpeed);
  // }
}