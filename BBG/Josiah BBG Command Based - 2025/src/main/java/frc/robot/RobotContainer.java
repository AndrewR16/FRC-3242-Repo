// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.LimelightCommand;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.List;
import java.util.function.BooleanSupplier;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.PneumaticsSubsystem;

@SuppressWarnings("unused")
public class RobotContainer {
  //Robot controller
  CommandXboxController m_driveController = new CommandXboxController(OIConstants.kDriverControllerPort);
    
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  private final PneumaticsSubsystem m_pneumaticsSubsystem = new PneumaticsSubsystem();
  private final CameraSubsystem m_cameraSubsystem = new CameraSubsystem();
  private final LimelightSubsystem m_limelightSubsystem = new LimelightSubsystem();

  private Trigger bButton = m_driveController.b();
  private Trigger rightTrigger = m_driveController.rightTrigger();
  private Trigger aButton = m_driveController.a();
  private Trigger yButton = m_driveController.y();

  public RobotContainer() {
    configureBindings();

    m_driveSubsystem.setDefaultCommand(
      new RunCommand(() -> m_driveSubsystem.drive(
          MathUtil.applyDeadband(m_driveController.getLeftY(), OIConstants.kDriveDeadZone), 
          MathUtil.applyDeadband(m_driveController.getLeftX(), OIConstants.kDriveDeadZone)), m_driveSubsystem));
  
    m_intakeSubsystem.setDefaultCommand(
        new RunCommand(() -> m_intakeSubsystem.intakeEnable(), m_intakeSubsystem));

    m_limelightSubsystem.setDefaultCommand(
      new LimelightCommand(m_limelightSubsystem));
  }

  private void configureBindings() {
    bButton.onTrue(m_intakeSubsystem.stopIntake());

    rightTrigger.whileTrue(m_intakeSubsystem.shoot());

    aButton.whileTrue(m_intakeSubsystem.eject());

    yButton.onTrue(m_pneumaticsSubsystem.toggle());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
