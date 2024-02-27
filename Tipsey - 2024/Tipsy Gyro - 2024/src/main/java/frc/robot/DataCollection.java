package frc.robot;

// Gyro and timer
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.wpilibj.Timer;

public class DataCollection {
    // Timer
    private final static Timer timer = new Timer();

    // Turning data collection varialbes
    private final static double collectionRate = 0.1; // Seconds
    private static double lastCollectionTime;

    // Reset values
    protected static void resetDataValues() {
        // Reset and start timer
        timer.reset();
        timer.start();

        // Reset values
        lastCollectionTime = 0.0;

        System.out.println("");
        System.out.println("X,Y");
    }

    // Collects data every "collectionRate" seconds
    protected static void collectData(WPI_PigeonIMU gyro) {
        double currentTime = timer.get();
        if (currentTime > (lastCollectionTime + collectionRate)) {
            // Output an ordered pair of the current time and heading
            System.out.printf("%f,%f\n", currentTime, gyro.getAngle());

            // Set lastHeading and lastColleciton time
            lastCollectionTime = currentTime;
        }
    }
}
