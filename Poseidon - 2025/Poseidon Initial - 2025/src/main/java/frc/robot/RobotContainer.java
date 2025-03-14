// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.ElevatorSetpoints;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import java.util.List;

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
        
        // Get USB camera feed
        CameraServer.startAutomaticCapture();
        
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
        // Lift up and down (Y and A Buttons)
        m_driverController.y().whileTrue(m_robotElevator.elevatorUpCommand());
        m_driverController.a().whileTrue(m_robotElevator.elevatorDownCommand());
        
        // Gantry forward and backward (D-pad Up and Down)
        m_driverController.povUp().and(m_gantryForward.negate()) // Gantry must not be contacting the front limit switch
            .whileTrue(m_robotElevator.moveGantryCommand(ElevatorSetpoints.kGantryForward));
        m_driverController.povDown().and(m_gantryBack.negate()) // Gantry must not be contacting the back limit switch
            .whileTrue(m_robotElevator.moveGantryCommand(ElevatorSetpoints.kGantryBackward));
        
        // Shooter out and in (Right and Left Triggers)
        m_driverController.rightTrigger().whileTrue(m_robotShooter.shooterOutCommand());
        m_driverController.leftTrigger().whileTrue(m_robotShooter.shooterInCommand());
        
        // Shooter open and close (Right and Left Bumpers)
        m_driverController.rightBumper().whileTrue(m_robotShooter.jawOpenCommand());
        m_driverController.leftBumper().whileTrue(m_robotShooter.jawCloseCommand());
        
        // Stop gantry movement (Gantry Limit Switches)
        m_gantryForward.or(m_gantryBack).onTrue(m_robotElevator.runOnce(Commands::none));

        // Reset encoder positions (Gantry Limit Switches)
        m_gantryBack.onTrue(m_robotElevator.resetGantryEncoder(ElevatorSetpoints.kGantryBackward));
        m_gantryForward.onTrue(m_robotElevator.resetGantryEncoder(ElevatorSetpoints.kGantryForward));
    }

    public Command getAutonomousCommand() {
        return Commands.sequence(
            Commands.runOnce(() -> m_robotDrive.drive(0.3, 0, 0, true), m_robotDrive),
            Commands.waitSeconds(2.0),
            Commands.runOnce(() -> m_robotDrive.drive(0, 0, 0, true), m_robotDrive)
        );
        
    //     // Create config for trajectory
    //     TrajectoryConfig config = new TrajectoryConfig(
    //         AutoConstants.kMaxSpeedMetersPerSecond,
    //         AutoConstants.kMaxAccelerationMetersPerSecondSquared)
    //         // Add kinematics to ensure max speed is actually obeyed
    //         .setKinematics(DriveConstants.kDriveKinematics);

    //         // An example trajectory to follow. All units in meters.
    //     Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
    //         // Start at the origin facing the +X direction
    //         new Pose2d(0, 0, new Rotation2d(0)),
    //         // Pass through these two interior waypoints, making an 's' curve path
    //         List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
    //         // End 3 meters straight ahead of where we started, facing forward
    //         new Pose2d(3, 0, new Rotation2d(0)),
    //         config);

    // var thetaController = new ProfiledPIDController(
    //     AutoConstants.kPThetaController, 0, 0, AutoConstants.kThetaControllerConstraints);
    // thetaController.enableContinuousInput(-Math.PI, Math.PI);

    // SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
    //     exampleTrajectory,
    //     m_robotDrive::getPose, // Functional interface to feed supplier
    //     DriveConstants.kDriveKinematics,

    //     // Position controllers
    //     new PIDController(AutoConstants.kPXController, 0, 0),
    //     new PIDController(AutoConstants.kPYController, 0, 0),
    //     thetaController,
    //     m_robotDrive::setModuleStates,
    //     m_robotDrive);

    // // Reset odometry to the starting pose of the trajectory.
    // m_robotDrive.resetOdometry(exampleTrajectory.getInitialPose());

    // // Run path following command, then stop at the end.
    // return swerveControllerCommand.andThen(() -> m_robotDrive.drive(0, 0, 0, false));
    }
}
