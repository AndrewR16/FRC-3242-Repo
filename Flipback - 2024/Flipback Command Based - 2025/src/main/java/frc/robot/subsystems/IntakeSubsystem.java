package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
    // Intake motors
    private final SparkMax m_intakeFeedMotor = new SparkMax(IntakeConstants.kIntakeFeedMotorCanId, MotorType.kBrushed);
    private final SparkMax m_intakeFlipMotor = new SparkMax(IntakeConstants.kIntakeFlipMotorCanId, MotorType.kBrushed);

    // Infared sensor
    private final DigitalInput m_intakeInfared = new DigitalInput(IntakeConstants.kIntakeInfraredSensorPort);

    // Limit switches
    private final DigitalInput m_intakeUpSwitch = new DigitalInput(IntakeConstants.kIntakeUpLimitSwitchPort);
    private final DigitalInput m_intakeDownSwitch = new DigitalInput(IntakeConstants.kIntakeDownLimitSwitchPort);

    // Intake configuration
    public IntakeSubsystem() {
        // Configure intake motors
        m_intakeFeedMotor.configure(Configs.standardConfig, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
        m_intakeFlipMotor.configure(Configs.standardConfig, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
    }

    /** Flips intake into the up position */
    public Command intakeUpCommand() {
        return this.startEnd(
            () -> m_intakeFlipMotor.set(IntakeConstants.kIntakeFlipSpeed), 
            () -> m_intakeFlipMotor.set(0.0));
    }

    /** Flips intake into the down position */
    public Command intakeDownCommand() {
        return this.startEnd(
            () -> m_intakeFlipMotor.set(-IntakeConstants.kIntakeFlipSpeed), 
            () -> m_intakeFlipMotor.set(0.0));
    }

    /** Runs the intake inwards */
    public Command intakeInCommand() {
        return this.startEnd(
            () -> m_intakeFeedMotor.set(-IntakeConstants.kIntakeFeedSpeed), 
            () -> m_intakeFeedMotor.set(0.0));
    }

    /** Runs the intake outwards */
    public Command intakeOutCommand() {
        return this.startEnd(
            () -> m_intakeFeedMotor.set(IntakeConstants.kIntakeFeedSpeed), 
            () -> m_intakeFeedMotor.set(0.0));
    }

    /**
     * Gets the infared sensor value from the intake
     * 
     * @return whether a game piece is seen by the sensor
     */
    public boolean getSensorValue() {
        return !m_intakeInfared.get();
    }

    /**
     * Gets the status of the limit switch that checks whether the intake is flipped
     * up
     * 
     * @return whether the intake is flipped up
     */
    public boolean getIntakeUp() {
        return !m_intakeUpSwitch.get();
    }

    /**
     * Gets the status of the limit switch that checks whether the intake is flipped
     * down
     * 
     * @return whether the intake is flipped down
     */
    public boolean getIntakeDown() {
        return !m_intakeDownSwitch.get();
    }
}
