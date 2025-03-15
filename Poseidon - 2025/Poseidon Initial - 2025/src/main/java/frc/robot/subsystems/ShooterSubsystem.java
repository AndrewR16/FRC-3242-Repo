package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase{
    // Motors
    private final SparkMax m_jawMotor = new SparkMax(ShooterConstants.kJawCanId, MotorType.kBrushed);
    private final SparkMax m_shooterMotor = new SparkMax(ShooterConstants.kShooterCanId, MotorType.kBrushed);

    // Encoders
    private final RelativeEncoder m_jawEncoder = m_jawMotor.getEncoder();
    private final RelativeEncoder m_shooterEncoder = m_shooterMotor.getEncoder();

    // Feed forward control
    private final SimpleMotorFeedforward m_jawFeedforward = new SimpleMotorFeedforward(
        ShooterConstants.kJawKs, 
        ShooterConstants.kJawKv, 
        ShooterConstants.kJawKa); 
    
    // Closed loop control
    private final SparkClosedLoopController m_jawController = m_jawMotor.getClosedLoopController();

    // Jaw motion profile
    private final Timer m_jawProfileTimer = new Timer();
    private final TrapezoidProfile m_jawProfile = new TrapezoidProfile(new TrapezoidProfile.Constraints(1, 1));

    private TrapezoidProfile.State m_jawInitialSetpoint = new TrapezoidProfile.State();
    private TrapezoidProfile.State m_jawPreviousSetpoint = new TrapezoidProfile.State();

    public ShooterSubsystem() {
        m_jawMotor.configure(Configs.Shooter.jawConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_shooterMotor.configure(Configs.Shooter.shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public Command swingJawCommand(double setpoint) {
        TrapezoidProfile.State goal = new TrapezoidProfile.State(setpoint, 0);

        return this.startRun(
            () -> {
                m_jawProfileTimer.reset();
                m_jawProfileTimer.start();
                m_jawInitialSetpoint = new TrapezoidProfile.State(m_jawEncoder.getPosition(), 0);
                m_jawPreviousSetpoint = m_jawInitialSetpoint;
            },
            () -> {
            var nextSetpoint = m_jawProfile.calculate(m_jawProfileTimer.get(), m_jawInitialSetpoint, goal);

            // Use regular feed forward if the claw is opening, otherwise just use Ks to adjust for gravity
            double feedforward = (m_jawInitialSetpoint.position > goal.position) 
                ? (-ShooterConstants.kJawKs / 3.0)
                : m_jawFeedforward.calculateWithVelocities(m_jawPreviousSetpoint.velocity, nextSetpoint.velocity);
            
            m_jawController.setReference(
                nextSetpoint.position, 
                ControlType.kPosition, 
                ClosedLoopSlot.kSlot0,
                feedforward);

            m_jawPreviousSetpoint = nextSetpoint;
            SmartDashboard.putNumber("Jaw Profile Position", nextSetpoint.position);
            SmartDashboard.putNumber("Jaw Profile Velocity", nextSetpoint.velocity);
        }).finallyDo(() -> m_jawMotor.set(0.0));
    }
    
    public Command jawOpenCommand() {
        return this.startEnd(
            () -> m_jawMotor.set(ShooterConstants.kDefaultOpenJawSpeed),
            () -> m_jawMotor.set(0.0));
    }

    public Command jawCloseCommand() {
        return this.startEnd(
            () -> m_jawMotor.set(ShooterConstants.kDefaultCloseJawSpeed),
            () -> m_jawMotor.set(0.0));
    }

    public Command shooterInCommand() {
        return this.startEnd(
            () -> m_shooterMotor.set(-ShooterConstants.kDefaultShooterInSpeed),
            () -> m_shooterMotor.set(0.0));
    }

    public Command shooterOutCommand() {
        return this.startEnd(
            () -> m_shooterMotor.set(ShooterConstants.kDefaultShooterOutSpeed),
            () -> m_shooterMotor.set(0.0));
    }

    public Boolean isJawAtMax() {
        return m_jawEncoder.getPosition() > 1.8;
    }

    public Boolean isJawAtMin() {
        return m_jawEncoder.getPosition() < 0.05;
    }
}
