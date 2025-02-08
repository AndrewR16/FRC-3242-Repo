package frc.robot.subsystems;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
    // Shooter motors
    private final SparkMax m_topLeftShooter = new SparkMax(ShooterConstants.kTopLeftShooterCanId, MotorType.kBrushed);
    private final SparkMax m_bottomLeftShooter = new SparkMax(ShooterConstants.kBottomLeftShooterCanId, MotorType.kBrushed);
    private final SparkMax m_topRightShooter = new SparkMax(ShooterConstants.kTopRightShooterCanId, MotorType.kBrushed);
    private final SparkMax m_bottomRightShooter = new SparkMax(ShooterConstants.kBottomRightShooterCanId, MotorType.kBrushed);

    // Linear actuator
    private final SparkMax m_linearActuator = new SparkMax(ShooterConstants.kLinearActuatorPort, MotorType.kBrushed);
    
    // Shooter rotaor potentiometer
    private final AnalogPotentiometer m_rotatorPotentiometer = new AnalogPotentiometer(ShooterConstants.kPotentiometerPort);

    // Shooter configurations
    public ShooterSubsystem() {
        // Configure shooter motors
        m_topLeftShooter.configure(Configs.shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_bottomLeftShooter.configure(Configs.shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_topRightShooter.configure(Configs.shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_bottomRightShooter.configure(Configs.shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    /**
     * Set all the shooter motors to a given speed
     * @param speed speed of the shooter motors
     */
    private void setShooterSpeed(double speed) {
        m_topLeftShooter.set(speed);
        m_bottomLeftShooter.set(speed);
        m_topRightShooter.set(speed);
        m_bottomRightShooter.set(speed);
    }

    /** Fires all shooter motors forward */
    public Command shootCommand() {
        return this.startEnd(
            () -> setShooterSpeed(ShooterConstants.kShooterSpeed), 
            () -> setShooterSpeed(0.0));
    }

    /** Fires all shooter motors in reverse at a low speed*/
    public Command shootReverseCommand() {
        return this.startEnd(
            () -> setShooterSpeed(ShooterConstants.kShooterReverseSpeed), 
            () -> setShooterSpeed(0.0));
    }

    /** Rotates the shooter up */
    public Command shooterUpCommand() {
        return this.startEnd(
            () -> m_linearActuator.set(ShooterConstants.kShooterRotationSpeed), 
            () -> m_linearActuator.set(0.0));
    }

    /** Rotates the shooter down */
    public Command shooterDownCommand() {
        return this.startEnd(
            () -> m_linearActuator.set(-ShooterConstants.kShooterRotationSpeed), 
            () -> m_linearActuator.set(0.0));
    }

    // Get potentiometer value
    public double getPotentiometerValue() {
        return m_rotatorPotentiometer.get();
    }
}
