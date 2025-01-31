// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Checked at 3:54

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

// LED

// Motors
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

// Drive and control
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.cameraserver.CameraServer;

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
// import edu.wpi.first.wpilibj.AnalogPotentiometer;

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

  private final AnalogPotentiometer m_rotatorPotentiometer = new AnalogPotentiometer(0);

  // Infrared sensors
  private final DigitalInput intakeInf = new DigitalInput(Constants.intakeInfPort);
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
  private final Timer autoFlipbackTimer = new Timer();

  // Intake Timer
  private final Timer autoIntakeStopTimer = new Timer();

  /**
   * <p>
   * This timer is used to allow for the toggling of the intake wheels with the
   * left and right d pad buttons. D pad buttons do not have their own WPILib
   * getDPadButton() pressed method, so the timer is used to mimic this feature.
   * </p>
   * 
   * <p>
   * In the implementation of this timer, a 0.4 second delay is used between
   * the initial press of the button is detected, and when the program checks
   * for the button press again.
   * </p>
   */
  private final Timer intakeControllerTimer = new Timer();

  // Command system
  CommandSystem autonomousCommands = new CommandSystem();
  CommandSystem teleopCommands = new CommandSystem();
  CommandSystem aprilTagCommands = new CommandSystem();
  CommandSystem testingCommands = new CommandSystem();

  // Team color chooser
  private static final String blueTeam = "Blue";
  private static final String redTeam = "Red";
  private final SendableChooser<String> teamColorSelector = new SendableChooser<String>();

  // Intake booleans
  private boolean intakeFlipping = false;
  private boolean isIntakeUp = false;
  private boolean isIntakeDown = false;

  private boolean enableIntakeOut = false;
  private boolean enableIntakeIn = false;

  private boolean initialNoteView = true;

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

    CameraServer.startAutomaticCapture();
  }

  @Override
  public void robotPeriodic() {
    // Limit Switches
    SmartDashboard.putBoolean("FlipBack Up", switch_intakeUp.get() == false);
    SmartDashboard.putBoolean("FlipBack Down", switch_intakeDown.get() == false);

    SmartDashboard.putBoolean("Intake Inf", intakeInf.get());
    SmartDashboard.putData("Rotation Potentiometer", m_rotatorPotentiometer);
    SmartDashboard.putNumber("timer", autoIntakeStopTimer.get());
    SmartDashboard.putBoolean("Start Button", controller.getStartButton());
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
    // NetworkTable limelight =
    // NetworkTableInstance.getDefault().getTable("limelight");
    // NetworkTableEntry xOffsetEntry = limelight.getEntry("tx");
    // NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");
    // NetworkTableEntry aprilTagIDEntry = limelight.getEntry("tid");

    // double xOffset = xOffsetEntry.getDouble(0.0);
    // double targetArea = targetAreaEntry.getDouble(0.0);
    // double targetId = aprilTagIDEntry.getDouble(0.0);

    // // Limelight smart dashboard
    // SmartDashboard.putNumber("X Offest", xOffset);
    // SmartDashboard.putNumber("Target Area", targetArea);
    // SmartDashboard.putNumber("April Tag ID", targetId);
    // SmartDashboard.putString("Dectected Station", LimelightNode.getNodeName((int)
    // targetId));

    // Autonomous Routine
    if (autonomousCommands.runFor(3)) {
      // *Drive backwards to leave starting area
      mDrive.driveCartesian(0.3, 0.0, 0.0);
    }

    // if (autonomousCommands.runTillComplete()) {
    // // TODO: Shoot into speaker
    // } else if (autonomousCommands.runTillComplete()) {
    // // TODO: Drive to amplifier

    // // Switch over to targeting system once april tag is detected
    // if (targetId == LimelightNode.amplifierId) {
    // autonomousCommands.commandCompleted();
    // }
    // } else if (autonomousCommands.runTillComplete()) {
    // targetAprilTag(xOffset, targetArea, targetId);
    // }
  }

  /**
   * Gets the robot into the ready position to perfrom the action for the node
   * seen.
   */
  private void targetAprilTag(double xOffset, double targetArea, double targetId) {
    aprilTagCommands.resetCommandId();
    /*
     * if (aprilTagCommands.runTillComplete()) {
     * // Align with april tag using gyro
     * // double targetingError = LimelightNode.getNodeHeading((int) targetId) -
     * // correctedGyroAngle();
     * // double adjustedHeadingError =
     * // Proportional.adjustHeadingValue(targetingError);
     * // double driveSpeed = Proportional.calculatePDrive(adjustedHeadingError,
     * 0.006,
     * // 2);
     * // mDrive.driveCartesian(0.0, 0.0, driveSpeed);
     * 
     * // SmartDashboard.putString("Current Task", "Aligning Heading");
     * // SmartDashboard.putNumber("Adjusted Heading", adjustedHeadingError);
     * // TODO: (Test) Target an april tag and align x position
     * // driveSpeed = Proportional.calculatePDrive(xOffset, 0.04, 3);
     * // mDrive.driveCartesian(0.0, driveSpeed, 0.0);
     * 
     * // aprilTagCommands.checkIfCompleted(driveSpeed, 0.0);
     * } else if (aprilTagCommands.runTillComplete()) {
     * // TODO (Test): Target an april tag and align y position
     * SmartDashboard.putString("Current Task", "Aligning Y");
     * double error = LimelightNode.getNodeArea((int) targetId) - targetArea;
     * double driveSpeed = Proportional.calculatePDrive(error, 0.8, 0.05);
     * mDrive.driveCartesian(driveSpeed, 0.0, 0.0);
     * 
     * aprilTagCommands.checkIfCompleted(driveSpeed, 0.0);
     * } else {
     * autonomousCommands.commandCompleted();
     * }
     */
  }

  @Override
  public void teleopInit() {
    // Team color selector
    // LimelightNode.setupNodeIds(teamColorSelector.getSelected());

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

    // Intake controller timer
    intakeControllerTimer.reset();
    intakeControllerTimer.start();

    // Auto intake stop
    autoIntakeStopTimer.stop();
    autoIntakeStopTimer.reset();
  }

  @Override
  public void teleopPeriodic() {

    // SmartDashboard.putBoolean("Intake Inf", intakeInf.get());
    // SmartDashboard.putData("Infared", intakeInf);

    // *Mechanum drive
    mDrive.driveCartesian(-Controller.leftStickY(), Controller.rightStickX(), Controller.leftStickX());

    // *Shooting (speaker) (Right Trigger)
    SmartDashboard.putNumber("Shooter Speed", m_topLeftShooter.get());
    if (Controller.rightTrigger()) {
      // Shoot to speaker
      runShooter(Constants.speakerShootingSpeed);
      m_intake.set(Constants.converyorSpeed);

    }
    // *Shooting (amp) (Left Trigger)
    if (Controller.leftTrigger()) {
      // Shoot to amp
      runShooter(Constants.ampShootingSpeed);
      m_intake.set(Constants.converyorSpeed);
    }
    // *Sets shooting motors back to zero when not in use
    if (!Controller.rightTrigger() && !Controller.leftTrigger()) {
      runShooter(0);
      if (!Controller.dPad_Left() || !Controller.dPad_Right()) {
        m_intake.set(0);
      }
    }

    // *Shooter rotation (Up and Down on D Pad)
    SmartDashboard.putNumber("Shooter Rotation Speed", m_shooterRotator.get());
    if (Controller.dPad_Up()) {
      // Rotate shooter up (Up on D Pad)
      m_shooterRotator.set(Constants.shooterRotatorSpeed);
    } else if (Controller.dPad_Down()) {
      // Rotate shooter down (Down on D Pad)
      m_shooterRotator.set(-Constants.shooterRotatorSpeed);
    } else if (controller.getStartButton() == false) {
      m_shooterRotator.set(0);
    }

    // *Intake (in and out) (Left and Right on D Pad)
    SmartDashboard.putNumber("Intake Speed", m_intake.get());
    SmartDashboard.putBoolean("Intake In", enableIntakeIn);
    SmartDashboard.putBoolean("Intake Out", enableIntakeOut);

    // Intake controls
    if (Controller.dPad_Left() && intakeControllerTimer.get() > 0.4) { // Hover over timer variable to see documentation
      enableIntakeOut = !enableIntakeOut;
      enableIntakeIn = false;

      intakeControllerTimer.reset();
      intakeControllerTimer.start();
    }
    if (Controller.dPad_Right() && intakeControllerTimer.get() > 0.4) {
      enableIntakeIn = !enableIntakeIn;
      enableIntakeOut = false;

      intakeControllerTimer.reset();
      intakeControllerTimer.start();
    }

    // Run intake
    if (enableIntakeOut) {
      // Intake out (Left on D Pad)
      m_intake.set(Constants.intakeSpeed);
    } else if (enableIntakeIn) {
      // Intake in (Right on D Pad)
      m_intake.set(-Constants.intakeSpeed);
    } else if (Controller.leftTrigger() == false && Controller.rightTrigger() == false) {
      m_intake.set(0);
    }

    // Auto intake stop and flipback
    if (intakeInf.get() == false && initialNoteView == true && isIntakeDown == true) { // When intake sees a note
      // Start auto intake
      autoIntakeStopTimer.reset();
      autoIntakeStopTimer.start();

      initialNoteView = false;
    }
    if (autoIntakeStopTimer.get() > Constants.intakeDelay) { // Runs once
      // Stop intake
      enableIntakeIn = false;

      // Start flip intake up
      isIntakeDown = true;
      intakeFlipping = true;

      flipbackTimer.reset();
      flipbackTimer.start();

      // Reset auto intake
      autoIntakeStopTimer.stop();
      autoIntakeStopTimer.reset();
    }

    // Reset inital view after note leaves
    if (intakeInf.get() == true) {
      initialNoteView = true;
    }

    // *Intake (rotation/flipback) (Left and Right Bumpers)
    if (switch_intakeUp.get() == false && isIntakeUp == false) {
      isIntakeUp = true;
      isIntakeDown = false;
      intakeFlipping = false;

    }
    if (switch_intakeDown.get() == false && isIntakeDown == false) {
      isIntakeUp = false;
      isIntakeDown = true;
      intakeFlipping = false;
    }

    // Input for fliback
    SmartDashboard.putBoolean("Flipback Enabled", intakeFlipping);
    if (controller.getLeftBumperPressed()) {
      // Start/stop flipback down
      intakeFlipping = !intakeFlipping;
      isIntakeUp = true;
      isIntakeDown = false;

      flipbackTimer.reset();
      flipbackTimer.start();
    }
    if (controller.getRightBumperPressed()) {
      // Start/stop flipback up
      intakeFlipping = !intakeFlipping;
      isIntakeUp = false;
      isIntakeDown = true;

      flipbackTimer.reset();
      flipbackTimer.start();
    }

    // Flip intake
    if (intakeFlipping) {
      if (isIntakeUp) {
        // *Intake Down (Left Bumper)
        m_flipBack.set(-Proportional.calculateFlipbackSpeed(flipbackTimer.get(), 0.8));
      } else if (isIntakeDown) {
        // *Intake Up (Right Bumper)
        m_flipBack.set(Proportional.calculateFlipbackSpeed(flipbackTimer.get(), 0.8));
      }
    } else {
      m_flipBack.set(0);
    }

    // *Emergency stop
    // if (controller.getXButtonPressed()) {
    // emergencyStopEnabled = !emergencyStopEnabled;
    // }
    // if (emergencyStopEnabled) {
    // // Other motors
    // m_topLeftShooter.set(0);
    // m_intake.set(0);
    // }

    // Reverse shooter
    // SmartDashboard.putBoolean("Reverse Enabled", reverseMotorsEnabled);
    // if (controller.getYButtonPressed()) {
    // reverseMotorsEnabled = !reverseMotorsEnabled;

    // if (reverseMotorsEnabled) {
    // m_intake.setInverted(true);
    // } else {
    // m_intake.setInverted(false);
    // }
    // }
    // *Reverse intake (Y)
    if (controller.getYButton()) {
      runShooter(-0.2);
      m_intake.set(-1);
    }

    // *Reverse Drive Motors (B)
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

    // 55 degrees (shooting angle): .2125
    // 60 degrees (starting angle): .241
    // *Set to shooting angle
    if (controller.getStartButton()) {
      if (m_rotatorPotentiometer.get() > 0.2125 + 0.01) {
        m_shooterRotator.set(-Constants.shooterRotatorSpeed);
      } else if (m_rotatorPotentiometer.get() < 0.2125 - 0.01) {
        m_shooterRotator.set(Constants.shooterRotatorSpeed);
      } else {
        m_shooterRotator.set(0);
      }
    }

    if (controller.getBackButton()) {
      if (m_rotatorPotentiometer.get() > 0.241 + 0.01) {
        m_shooterRotator.set(-Constants.shooterRotatorSpeed);
      } else if (m_rotatorPotentiometer.get() < 0.241 - 0.01) {
        m_shooterRotator.set(Constants.shooterRotatorSpeed);
      } else {
        m_shooterRotator.set(0);
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