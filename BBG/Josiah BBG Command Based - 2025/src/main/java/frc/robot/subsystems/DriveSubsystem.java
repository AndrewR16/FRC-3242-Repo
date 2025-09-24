package frc.robot.subsystems;

import frc.robot.Constants.DriveConstants;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

@SuppressWarnings("unused")
public class DriveSubsystem extends SubsystemBase {
    //Create Motors
    private final WPI_TalonSRX m_frontLeft = new WPI_TalonSRX(DriveConstants.kFrontLeftID);
    private final WPI_TalonSRX m_frontRight = new WPI_TalonSRX(DriveConstants.kFrontRightID);
    private final WPI_TalonSRX m_backLeft = new WPI_TalonSRX(DriveConstants.kBackLeftID);
    private final WPI_TalonSRX m_backRight = new WPI_TalonSRX(DriveConstants.kBackRightID);

    //Create drive system
    private final DifferentialDrive arcadeDrive = new DifferentialDrive(m_frontLeft, m_frontRight);

    //Initiatize Subsystem
    public DriveSubsystem () {
        m_backLeft.follow(m_frontLeft);
        m_backRight.follow(m_frontRight);
    
        m_frontRight.setInverted(true);
        m_backRight.setInverted(true);
    }

    //Drive
    public void drive (double xSpeed, double ySpeed) {
        arcadeDrive.arcadeDrive(xSpeed, ySpeed);
    }
}
