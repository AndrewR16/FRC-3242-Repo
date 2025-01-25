package frc.robot.subsystems;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {
    // Left side drive motors
    private final WPI_TalonSRX m_leftLeader = new WPI_TalonSRX(DriveConstants.kLeftLeadMotorPort);
    private final WPI_TalonSRX m_leftFollower = new WPI_TalonSRX(DriveConstants.kLeftFollowMotorPort);

    // Right side drive motors
    private final WPI_TalonSRX m_rightLeader = new WPI_TalonSRX(DriveConstants.kRightLeadMotorPort);
    private final WPI_TalonSRX m_rightFollower = new WPI_TalonSRX(DriveConstants.kRightFollowMotorPort);

    // Robot drive
    private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftLeader, m_rightLeader);

    // Sets up the drive subsystem
    public DriveSubsystem() {
        // Set back motors to follow front motors
        m_leftFollower.follow(m_leftLeader);
        m_rightFollower.follow(m_rightLeader);

        // Invert right side motors to ensure correct direction
        m_rightLeader.setInverted(true);
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
