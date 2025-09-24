package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LimelightSubsystem;

public class LimelightCommand extends Command {
    private final LimelightSubsystem m_subsystem;

    public LimelightCommand(LimelightSubsystem subsystem) {
        m_subsystem = subsystem;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        SmartDashboard.putNumber("Target Horizontal", m_subsystem.getX());
        SmartDashboard.putNumber("Target Vertical", m_subsystem.getY());
        SmartDashboard.putNumber("Target Area", m_subsystem.getA());
        SmartDashboard.putBoolean("Object", m_subsystem.getV());
        SmartDashboard.putNumber("Id", m_subsystem.getID());


    }

    @Override
    public boolean isFinished() {
        if (m_subsystem.getX() == 0.0 && m_subsystem.getY() == 0.0) {
            return true;
        } else {
            return false;
        }
    }
}
