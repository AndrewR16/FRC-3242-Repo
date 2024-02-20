package frc.robot;

import java.util.ArrayList;

// Gyro and timer
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.wpilibj.Timer;

public class DataCollection {
    // Timer
    private final static Timer timer = new Timer();

    // Gyro
    private final static WPI_TalonSRX m_rightBack = new WPI_TalonSRX(3);
    private final static WPI_PigeonIMU gyro = new WPI_PigeonIMU(m_rightBack);

    // Turning data collection varialbes
    private static ArrayList<String> coordinateArray = new ArrayList<String>();

    private final static double collectionRate = 0.1; // Seconds
    private static double lastCollectionTime;
    private static double lastHeading;

    // Reset values
    protected static void resetDataValues() {
        // Reset and start timer
        timer.reset();
        timer.start();

        // Reset values
        gyro.reset();
        coordinateArray.clear();
        lastCollectionTime = 0.0;
        lastHeading = 0.0;

        System.out.println("");
        System.out.println("X,Y");
    }

    // Collects data every "collectionRate" seconds
    protected static void collectData() {
        double currentTime = timer.get();
        if (currentTime > (lastCollectionTime + collectionRate)) {
            double deltaHeading = gyro.getAngle() - lastHeading;

            // Output an ordered pair of the current time and heading
            // coordinateArray.add(String.format("%f,%f", currentTime, deltaHeading));
            System.out.printf("%f,%f\n", currentTime, deltaHeading);

            // Set lastHeading and lastColleciton time
            lastCollectionTime = currentTime;
            lastHeading = gyro.getAngle();
        }
    }

    // Output data to a csv file
    protected static void outputData() {
        System.out.println("X,Y"); // header row

        for (String coordinate : coordinateArray) {
            System.out.println(coordinate);
        }
    }
}
