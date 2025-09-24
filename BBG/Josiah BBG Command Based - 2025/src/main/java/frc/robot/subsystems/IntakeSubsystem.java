package frc.robot.subsystems;

import frc.robot.Constants.ShooterConstants;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.robot.Robot;
import frc.robot.RobotContainer;

@SuppressWarnings("unused")
public final class IntakeSubsystem extends SubsystemBase {
    public static final VictorSPX intakeOne = new VictorSPX(ShooterConstants.kIntakeOneID);
    public static final VictorSPX intakeTwo = new VictorSPX(ShooterConstants.kIntakeTwoID);
    public static final VictorSPX intakeThree = new VictorSPX(ShooterConstants.kIntakeThreeID);
    public static final VictorSPX intakeFour = new VictorSPX(ShooterConstants.kIntakeFourID);

    public static final DigitalInput firstPos = new DigitalInput(ShooterConstants.kFirstPos);
    public static final DigitalInput secondPos = new DigitalInput(ShooterConstants.kSecondPos);
    public static final DigitalInput thirdPos = new DigitalInput(ShooterConstants.kThirdPos);

    public static final WPI_TalonSRX shooter = new WPI_TalonSRX(ShooterConstants.kShooterID);

    public boolean stopEnabled = true;

    public IntakeSubsystem () {
    }

    public void intakeSet (int val1, int val2, int val3, int val4) {
        intakeOne.set(ControlMode.PercentOutput, val1);
        intakeTwo.set(ControlMode.PercentOutput, val2);
        intakeThree.set(ControlMode.PercentOutput, val3);
        intakeFour.set(ControlMode.PercentOutput, val4);
    }
    
    public void intakeEnable(){
        if (!stopEnabled) {
            if (firstPos.get() == false && secondPos.get() == false && thirdPos.get() == false) {
                intakeSet(0, 0, 0, 0);
            } else if (firstPos.get() == false && secondPos.get() == false && thirdPos.get() == true) {
                intakeSet(-1, -1, 0, 0);
            } else if (firstPos.get() == false && secondPos.get() == true && thirdPos.get() == true) {
                intakeSet(-1, -1, -1, 0);
            }else if (firstPos.get() == true && secondPos.get() == true && thirdPos.get() == true) {
                intakeSet(-1, -1, -1, -1);
            }
        }
    }

    public Command stopIntake(){
        return this.runOnce(() -> stopEnabled = !stopEnabled);
    }

    public Command shoot() {
        return this.startEnd(
            () -> {
                shooter.set(ControlMode.PercentOutput, -1);
                intakeFour.set(ControlMode.PercentOutput, -1);
            }, 
            () -> {
                shooter.set(ControlMode.PercentOutput, 0);
                intakeFour.set(ControlMode.PercentOutput, 0);
            });
        }

    public Command eject(){
        return this.runOnce(() -> intakeSet(1, 1, 1, 1));
    }
}