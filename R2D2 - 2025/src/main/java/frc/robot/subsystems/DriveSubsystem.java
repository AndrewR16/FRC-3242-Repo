package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {
    // Drive motors
    private final WPI_TalonSRX m_leftDrive = new WPI_TalonSRX(DriveConstants.kLeftDriveMotorCANId);
    private final WPI_TalonSRX m_rightDrive = new WPI_TalonSRX(DriveConstants.kRightDriveMotorCANId);

    // Robot drive
    private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftDrive, m_rightDrive);

    public DriveSubsystem() {
        // Set the right side motors to be inverted
        m_rightDrive.setInverted(true);

        // Add differential drive to SmartDashboard
        SmartDashboard.putData("Differential Drive", m_robotDrive);
    }

    /**
     * Drives the robot using arcade controls.
     *
     * @param fwd the commanded forward movement
     * @param rot the commanded rotation
     */
    public void arcadeDrive(double fwd, double rot) {
        m_robotDrive.arcadeDrive(fwd, rot);
    }
}
