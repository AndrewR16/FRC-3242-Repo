package frc.robot.subsystems;

import frc.robot.Constants.PneumaticsConstants;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

@SuppressWarnings("unused")
public class PneumaticsSubsystem extends SubsystemBase {
    private static final Compressor compressor = new Compressor(PneumaticsConstants.kBoardType);
    private static final DoubleSolenoid solenoid = new DoubleSolenoid(PneumaticsConstants.kBoardType, PneumaticsConstants.kForwardChannel, PneumaticsConstants.kReverseChannel);

    private static boolean frontPos = false;

    public PneumaticsSubsystem() {
        compressor.enableDigital();
    }

    public Command toggle(){
        return this.runOnce(() -> {
            frontPos = !frontPos;
            if (frontPos) {
                solenoid.set(Value.kForward);
            } else {
                solenoid.set(Value.kReverse);
            }
        });
    }
}
