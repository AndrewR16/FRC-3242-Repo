// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import edu.wpi.first.wpilibj.TimedRobot;

// Motors
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

// Drive and control
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.XboxController;

// Gyro
import com.ctre.phoenix.sensors.WPI_PigeonIMU;

// Limelight
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

// Limit switches
import edu.wpi.first.wpilibj.DigitalInput;

// SmartDashboard
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends TimedRobot {
  // Wheel motors
  private final CANSparkMax m_frontLeft = new CANSparkMax(Constants.frontLeftPort, MotorType.kBrushed);
  private final CANSparkMax m_frontRight = new CANSparkMax(Constants.frontRightPort, MotorType.kBrushed);
  private final CANSparkMax m_backLeft = new CANSparkMax(Constants.backLeftPort, MotorType.kBrushed);
  private final CANSparkMax m_backRight = new CANSparkMax(Constants.backRightPort, MotorType.kBrushed);

  // Other motors
  private final CANSparkMax m_shooter = new CANSparkMax(Constants.shooterPort, MotorType.kBrushed);
  private final CANSparkMax m_ampShooter = new CANSparkMax(Constants.ampShootingPort, MotorType.kBrushed);
  private final CANSparkMax m_intake = new CANSparkMax(Constants.intakePort, MotorType.kBrushed);
  private final CANSparkMax m_flipBack = new CANSparkMax(Constants.flipBackPort, MotorType.kBrushed);

  // Infrared sensors
  private final DigitalInput intakeInf = new DigitalInput(Constants.intakeInfPort);
  private final DigitalInput ampInf = new DigitalInput(Constants.ampInfPort);

  // Limit switches
  private final DigitalInput switch_intakeUp = new DigitalInput(Constants.intakeBackSwitchPort);
  private final DigitalInput switch_intakeDown = new DigitalInput(Constants.intakeFrontSwitchPort);
  @SuppressWarnings("unused")
  private final DigitalInput switch_ampDown = new DigitalInput(Constants.ampDownSwitchPort);

  // Mecanum drive
  private final MecanumDrive mDrive = new MecanumDrive(m_frontLeft, m_backLeft, m_frontRight, m_backRight);
  private final XboxController controller = new XboxController(0);

  // Autocontroller
  AutoController auto = new AutoController(
      m_frontRight, m_frontLeft, m_backRight, m_backLeft, m_shooter, m_intake,
      m_flipBack, m_ampShooter, intakeInf, switch_intakeDown, switch_intakeUp, ampInf
  );

  // TODO: Gyro (Adjust port)
  private final WPI_PigeonIMU gyro = new WPI_PigeonIMU(0);

  private double correctedGyroAngle() {
    return -gyro.getAngle();
  }

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
  private boolean isIntakeUp = true;

  // Emergency stop and reverse
  private boolean emergencyStopEnabled = false;
  private boolean reverseMotorsEnabled = false;

  @Override
  public void robotInit() {
    // Set up motors
    m_frontRight.setInverted(true);
    m_backRight.setInverted(true);

    // Mechanum drive in smart dashboard
    SmartDashboard.putData("MecanumDrive", mDrive);

    // Team color chooser
    teamColorSelector.setDefaultOption("Blue Team", blueTeam);
    teamColorSelector.addOption("Red Team", redTeam);
    SmartDashboard.putData("Team Color", teamColorSelector);
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
      double targetingError = LimelightNode.getNodeHeading((int) targetId) - correctedGyroAngle();
      double adjustedHeadingError = Proportional.adjustHeadingValue(targetingError);
      double driveSpeed = Proportional.calculatePDrive(adjustedHeadingError, 0.006, 2);
      mDrive.driveCartesian(0.0, 0.0, driveSpeed);

      // SmartDashboard.putString("Current Task", "Aligning Heading");
      // SmartDashboard.putNumber("Adjusted Heading", adjustedHeadingError);
      // TODO: (Test) Target an april tag and align x position
      driveSpeed = Proportional.calculatePDrive(xOffset, 0.04, 3);
      mDrive.driveCartesian(0.0, driveSpeed, 0.0);

      aprilTagCommands.checkIfCompleted(driveSpeed, 0.0);
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
  }

  @Override
  public void teleopPeriodic() {
    // *Mechanum drive
    mDrive.driveCartesian(-Controller.leftStickY(), Controller.rightStickX(), Controller.leftStickX());

    // *Shooting (speaker)
    if (Controller.rightTrigger()) {
      m_shooter.set(Constants.shooterSpeed);
    } else {
      m_shooter.set(0);
    }

    // *Shooting (amp)
    if (Controller.leftTrigger()) {
      // TODO: Set up shooting into amp
    } else {
      m_shooter.set(0);
    }

    // *Shooter rotation
    if (Controller.dPad_Up()) {
      // TODO: Rotate shooter up
    } else if (Controller.dPad_Down()) {
      // TODO: Rotate shooter down
    } else {
      // speed zero
    }

    // *Intake (in and out)
    if (Controller.dPad_Left()) {
      // TODO: Intake in
    } else if (Controller.dPad_Right()) {
      // TODO: Intake out
    } else {
      // speed zero
    }

    // *Intake (rotation/flipback)
    // TODO: Test limit switch direction
    if (switch_intakeUp.get()) {
      isIntakeUp = true;
      intakeFlipping = false;
    }
    if (switch_intakeDown.get()) {
      isIntakeUp = false;
      intakeFlipping = false;
    }

    if (controller.getLeftBumperPressed()) {
      intakeFlipping = !intakeFlipping;
    }

    if (intakeFlipping) {
      if (isIntakeUp) {
        // TODO: move intake down
      } else {
        // TODO: move intake up
      }
    } else {
      // speed zero
    }

    // *Emergency stop
    if (controller.getXButtonPressed()) {
      emergencyStopEnabled = !emergencyStopEnabled;
    }
    if (emergencyStopEnabled) {
      // Drive motors
      m_frontLeft.set(0);
      m_frontRight.set(0);
      m_backLeft.set(0);
      m_backRight.set(0);

      // Other motors
      m_shooter.set(0);
      m_ampShooter.set(0);
      m_intake.set(0);
      m_flipBack.set(0);
    }

    // *Reverse motors
    if (controller.getYButton()) {
      reverseMotorsEnabled = !reverseMotorsEnabled;

      if (reverseMotorsEnabled) {
        m_shooter.setInverted(true);
        m_ampShooter.setInverted(true);
        m_intake.setInverted(true);
        m_flipBack.setInverted(true);
      } else {
        m_shooter.setInverted(false);
        m_ampShooter.setInverted(false);
        m_intake.setInverted(false);
        m_flipBack.setInverted(false);

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
  }

  @Override
  public void testPeriodic() {
    mDrive.driveCartesian(Constants.driveSpeed, 0.0, 0.0);
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}