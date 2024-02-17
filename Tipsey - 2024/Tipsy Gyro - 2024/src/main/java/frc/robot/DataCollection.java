package frc.robot;

// PrintWriter
import java.io.IOException;
import java.io.PrintWriter;

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
    private static String[] coordinateArray = new String[40];
    
    private final static double collectionRate = 0.25; // Seconds
    private static double lastCollectionTime = 0.0;
    private static double lastHeading = 0.0;

    private static int coordinateIndex;

    // Reset values
    protected static void resetDataValues() {
        // Reset and start timer
        timer.reset();
        timer.start();

        // Reset gyro and index
        gyro.reset();
        coordinateIndex = 0;
    }

    // Collects data every "collectionRate" seconds
    protected static void collectData() {
        // Turning data collection
        double currentTime = timer.get();
        if (currentTime > (lastCollectionTime + collectionRate)) {
            double deltaHeading = gyro.getAngle() - lastHeading;

            // Output an ordered pair of the current time and heading
            coordinateArray[coordinateIndex] = String.format("%f,%f", currentTime, deltaHeading);

            // Set lastHeading and lastColleciton time
            lastCollectionTime = currentTime;
            lastHeading = gyro.getAngle();
        }
    }

    // Output data to a csv file
    protected static void outputData() {
        try (PrintWriter writer = new PrintWriter("output.csv", "UTF-8")) {
            writer.println("X,Y");  // header row

            for (String coordinate : coordinateArray) {
                writer.println(coordinate);
            }

            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the CSV file.");
            e.printStackTrace();
        }
    }
}
