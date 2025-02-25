// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RobotContainer {
    // The robot's subsystems
    private final DriveSubsystem m_robotDrive = new DriveSubsystem();
    private final ElevatorSubsystem m_robotElevator = new ElevatorSubsystem();
    private final ShooterSubsystem m_robotShooter = new ShooterSubsystem();
    
    // The driver's controller
    CommandXboxController m_driverController = new CommandXboxController(OIConstants.kDriverControllerPort);

    // Triggers
    private final Trigger m_gantryForward = new Trigger(m_robotElevator::getGantryFrontSwitch);
    private final Trigger m_gantryBack = new Trigger(m_robotElevator::getGantryBackSwitch);

    public RobotContainer() {
        configureBindings();

        // Configure default commands
        m_robotDrive.setDefaultCommand(
            // The left stick controls translation of the robot.
            // Turning is controlled by the X axis of the right stick.
            new RunCommand(
                () -> m_robotDrive.drive(
                    -MathUtil.applyDeadband(m_driverController.getLeftY(), OIConstants.kDriveDeadband),
                    -MathUtil.applyDeadband(m_driverController.getLeftX(), OIConstants.kDriveDeadband),
                    -MathUtil.applyDeadband(m_driverController.getRightX(), OIConstants.kDriveDeadband),
                    true),
                m_robotDrive));

    }

    private void configureBindings() {
        // Lift up and down (Right and Left Bumpers)
        m_driverController.rightBumper().whileTrue(m_robotElevator.elevatorUpCommand());
        m_driverController.leftBumper().whileTrue(m_robotElevator.elevatorDownCommand());

        // Gantry forward and backward (Right and left on D-pad)
        m_driverController.povRight().and(m_gantryForward.negate()).whileTrue(m_robotElevator.gantryForwardCommand());
        m_driverController.povLeft().and(m_gantryBack.negate()).whileTrue(m_robotElevator.gantryBackwardCommand());

        //Shooter out and in (Right and Left Triggers)
        m_driverController.rightTrigger().whileTrue(m_robotShooter.shooterOutCommand());
        m_driverController.leftTrigger().whileTrue(m_robotShooter.shooterInCommand());

        // Shooter open and close (Up and Down on D-pad)
        m_driverController.povUp().whileTrue(m_robotShooter.jawOpenCommand());
        m_driverController.povDown().whileTrue(m_robotShooter.jawCloseCommand());

        // Stop gantry movement
        m_gantryForward.or(m_gantryBack).onTrue(m_robotElevator.runOnce(Commands::none));
    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
