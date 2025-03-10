package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Volts;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Configs;
import frc.robot.Constants.ElevatorConstants;

public class ElevatorSubsystem extends SubsystemBase{
    private final SparkMax m_liftMotor = new SparkMax(ElevatorConstants.kLiftCanId, MotorType.kBrushed);
    private final SparkMax m_gantryMotor = new SparkMax(ElevatorConstants.kGantryCanId, MotorType.kBrushed);

    private final RelativeEncoder m_liftEncoder = m_liftMotor.getEncoder();
    private final RelativeEncoder m_gantryEncoder = m_gantryMotor.getEncoder();

    // Limit switches
    private final DigitalInput m_gantryFrontSwitch = new DigitalInput(ElevatorConstants.kGantryFrontSwitchPort);
    private final DigitalInput m_gantryBackSwitch = new DigitalInput(ElevatorConstants.kGantryBackSwitchPort);

    // Creates a system identification routine
    private final SysIdRoutine m_sysIdRoutine = new SysIdRoutine(
        new SysIdRoutine.Config(
          null, null, null,
          (state) -> Logger.recordOutput("SysIdTestState", state.toString())
        ),
        new SysIdRoutine.Mechanism(
          (voltage) -> this.runVolts(voltage.in(Volts)),
          null, // No log consumer, since data is recorded by URCL
          this
        )
      );

    public ElevatorSubsystem() {
        m_liftMotor.configure(Configs.Elevator.liftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_gantryMotor.configure(Configs.Elevator.gantryConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    // Manual command methods
    public Command elevatorUpCommand() {
        return this.startEnd(
            () -> m_liftMotor.set(ElevatorConstants.kDefaultLiftSpeed), 
            () -> m_liftMotor.set(0.0));
    }

    public Command elevatorDownCommand() {
        return this.startEnd(
            () -> m_liftMotor.set(-ElevatorConstants.kDefaultLiftSpeed), 
            () -> m_liftMotor.set(0.0));
    }

    public Command gantryForwardCommand() {
        return this.startEnd(
            () -> m_gantryMotor.set(ElevatorConstants.kDefaultGantrySpeed), 
            () -> m_gantryMotor.set(0.0));
    }

    public Command gantryBackwardCommand() {
        return this.startEnd(
            () -> m_gantryMotor.set(-ElevatorConstants.kDefaultGantrySpeed), 
            () -> m_gantryMotor.set(0.0));
    }

    public Boolean getGantryFrontSwitch() {
        return !m_gantryFrontSwitch.get();
    }

    public Boolean getGantryBackSwitch() {
        return !m_gantryBackSwitch.get();
    }

    // System identification methods
    public void runVolts(double volts) {
        m_liftMotor.setVoltage(volts); // Set up for lift
    }    
    
    public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutine.quasistatic(direction);
    }

    public Command sysIdDynamic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutine.dynamic(direction);
      }
}
