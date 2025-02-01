// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.DriveSubsystem;

public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_robotDrive = new DriveSubsystem();

    // The driver's controller
  XboxController m_driverController = new XboxController(OperatorConstants.kDriverControllerPort);

  
  public RobotContainer() {
    // Configure the button bindings
    configureBindings();

    // Set default commands
    m_robotDrive.setDefaultCommand(
      new RunCommand(
        () -> m_robotDrive.drive(
          -MathUtil.applyDeadband(m_driverController.getLeftY(), OperatorConstants.kDriveDeadband), 
          MathUtil.applyDeadband(m_driverController.getLeftX(), OperatorConstants.kDriveDeadband), 
          MathUtil.applyDeadband(m_driverController.getRightX(), OperatorConstants.kDriveDeadband)),
          m_robotDrive));
  }

  private void configureBindings() {
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
