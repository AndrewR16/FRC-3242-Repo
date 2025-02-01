package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {
    // Drive motors
    private final SparkMax m_frontLeftDrive = new SparkMax(DriveConstants.kFrontLeftDrivingCanId, MotorType.kBrushed);
    private final SparkMax m_backLeftDrive = new SparkMax(DriveConstants.kBackLeftDrivingCanId, MotorType.kBrushed);
    private final SparkMax m_frontRightDrive = new SparkMax(DriveConstants.kFrontRightDrivingCanId, MotorType.kBrushed);
    private final SparkMax m_backRightDrive = new SparkMax(DriveConstants.kBackRightDrivingCanId, MotorType.kBrushed);

    // Mechanum drive
    private final MecanumDrive m_robotDrive = new MecanumDrive(m_frontLeftDrive, m_backLeftDrive, m_frontRightDrive, m_backRightDrive);

    public DriveSubsystem() {
        // Configure all drive motors
        m_frontLeftDrive.configure(Configs.drivingConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_backLeftDrive.configure(Configs.drivingConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_frontRightDrive.configure(Configs.invertedDrivingConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_backRightDrive.configure(Configs.invertedDrivingConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // Add mecanum drive to SmartDashboard
        SmartDashboard.putData("Robot Drive", m_robotDrive);
    }

    /**
     * Drives the robot with cartesian controls
     * 
     * @param xSpeed
     * @param ySpeed
     * @param zRotation
     */
    public void drive(double xSpeed, double ySpeed, double rot) {
        m_robotDrive.driveCartesian(xSpeed, ySpeed, rot);
    }
}
