package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Volts;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Configs;
import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase{
    private final SparkMax m_jawMotor = new SparkMax(ShooterConstants.kJawCanId, MotorType.kBrushed);
    private final SparkMax m_shooterMotor = new SparkMax(ShooterConstants.kShooterCanId, MotorType.kBrushed);

    private final RelativeEncoder m_jawEncoder = m_jawMotor.getEncoder();
    private final RelativeEncoder m_shooterEncoder = m_shooterMotor.getEncoder();

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
    
    public ShooterSubsystem() {
        m_jawMotor.configure(Configs.Shooter.jawConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_jawMotor.configure(Configs.Shooter.jawConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
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

    // System identification methods
    public void runVolts(double volts) {
        m_jawMotor.setVoltage(volts); // Set up for jaw open and close
    }    
    
    public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutine.quasistatic(direction);
    }

    public Command sysIdDynamic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutine.dynamic(direction);
      }
    
}
