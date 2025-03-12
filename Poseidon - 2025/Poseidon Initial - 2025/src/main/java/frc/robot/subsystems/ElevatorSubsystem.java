package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Volts;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.ExponentialProfile;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Configs;
import frc.robot.Constants.ElevatorConstants;

public class ElevatorSubsystem extends SubsystemBase{
    // Motors
    private final SparkMax m_liftMotor = new SparkMax(ElevatorConstants.kLiftCanId, MotorType.kBrushed);
    private final SparkMax m_gantryMotor = new SparkMax(ElevatorConstants.kGantryCanId, MotorType.kBrushed);

    // Encoders
    private final RelativeEncoder m_liftEncoder = m_liftMotor.getEncoder();
    private final RelativeEncoder m_gantryEncoder = m_gantryMotor.getEncoder();

    // Limit switches
    private final DigitalInput m_gantryFrontSwitch = new DigitalInput(ElevatorConstants.kGantryFrontSwitchPort);
    private final DigitalInput m_gantryBackSwitch = new DigitalInput(ElevatorConstants.kGantryBackSwitchPort);

    // Feed forward control
    private final ElevatorFeedforward m_elevatorFeedforward = new ElevatorFeedforward(0, 0, 0);
    private final SimpleMotorFeedforward m_gantryFeedForward = new SimpleMotorFeedforward(ElevatorConstants.kGantryKs, ElevatorConstants.kGantryKv, ElevatorConstants.kGantryKa);

    // Closed loop control
    private final SparkClosedLoopController m_liftController = m_liftMotor.getClosedLoopController();
    private final SparkClosedLoopController m_gantryController = m_gantryMotor.getClosedLoopController();
    
    // Gantry motion profile
    private final Timer m_gantryProfileTimer = new Timer();
    private final ExponentialProfile m_gantryProfile = new ExponentialProfile(ExponentialProfile.Constraints.fromCharacteristics(1.4, ElevatorConstants.kGantryKv, ElevatorConstants.kGantryKa));
    private ExponentialProfile.State m_initialSetpoint = new ExponentialProfile.State();
    private ExponentialProfile.State m_previousSetpoint = new ExponentialProfile.State();
    
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

    // Position controlled methods
    public Command moveGantryCommand(double setpoint) {
        ExponentialProfile.State goal = new ExponentialProfile.State(setpoint, 0);

        return this.startRun(
            () -> {
                m_gantryProfileTimer.reset();
                m_gantryProfileTimer.start();
                m_previousSetpoint = m_initialSetpoint;
                m_initialSetpoint = new ExponentialProfile.State(m_gantryEncoder.getPosition(), 0);
            },
            () -> {
            var nextSetpoint = m_gantryProfile.calculate(m_gantryProfileTimer.get(), m_initialSetpoint, goal);
            
            m_gantryController.setReference(
                nextSetpoint.position, 
                ControlType.kPosition, 
                ClosedLoopSlot.kSlot0,
                m_gantryFeedForward.calculateWithVelocities(m_previousSetpoint.velocity, nextSetpoint.velocity));

            m_previousSetpoint = nextSetpoint;
            SmartDashboard.putNumber("Profile Position", nextSetpoint.position);
            SmartDashboard.putNumber("Profile Velocity", nextSetpoint.velocity);
        }).finallyDo(() -> m_gantryMotor.set(0.0));
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

    // Reset encoder position
    public Command resetGantryEncoder(double position) {
        return this.runOnce(() -> m_gantryEncoder.setPosition(position));
    }
    
    public Boolean getGantryFrontSwitch() {
        return !m_gantryFrontSwitch.get();
    }

    public Boolean getGantryBackSwitch() {
        return !m_gantryBackSwitch.get();
    }

    // System identification methods
    public void runVolts(double volts) {
        m_gantryMotor.setVoltage(volts); // Set up for gantry
    }    
    
    public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutine.quasistatic(direction);
    }

    public Command sysIdDynamic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutine.dynamic(direction);
      }
}
