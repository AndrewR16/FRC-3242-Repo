package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase {
    public LimelightSubsystem() {
    }

    public double getX() {
    return -NetworkTableInstance
            .getDefault()
            .getTable("limelight")
            .getEntry("tx")
            .getDouble(0.0);
    }

    public double getY() {
        return -NetworkTableInstance
            .getDefault()
            .getTable("limelight")
            .getEntry("ty")
            .getDouble(0.0);
    }

    public double getA() {
        return NetworkTableInstance
            .getDefault()
            .getTable("limelight")
            .getEntry("ta")
            .getDouble(0.0);
    }

    public boolean getV() {
        return !NetworkTableInstance
            .getDefault()
            .getTable("limelight")
            .getEntry("tv")
            .getBoolean(false);
    }

    public double getID() {
        return NetworkTableInstance
            .getDefault()
            .getTable("limelight")
            .getEntry("tid")
            .getDouble(0.0);
    }
}