package frc.robot;

/**
 * In the Limelight applicaiton, these values should be put in the ID Filters field
 * according to our team color:
 * <ul>
 * <li>Blue: 1, 2, 6, 7, 14, 15, 16</li>
 * <li>Red: 4, 5, 9, 10, 11, 12, 13</li>
 * </ul>
 */
public class Limelight {
    // Alliance color checker
    protected static final boolean isTeamBlue = true;

    // Limelight variables
    protected static final double speakerArea = 0.0;
    protected static final double sourceArea = 0.0;
    protected static final double ampArea = 0.0;

    /**
     * Drive speed calculator for limelight
     * 
     * @param error The x axis offset from the april tag
     * @return Speed to set the motors
     */
    protected static double calculatePDrive(double error) {
        double kP = 0.04 * error;

        // Maximum Output
        if (kP < -0.8) {
            kP = -0.8;
        }
        if (kP > 0.8) {
            kP = 0.8;
        }

        // Minimum Output
        if (kP > 0 && kP < 0.3) {
            kP = 0.3;
        }
        if (kP < 0 && kP > -0.3) {
            kP = -0.3;
        }

        // Stop when heading is acheived
        if (Math.abs(error) < 3) {
            kP = 0;
        }

        return kP;
    }

    /** Gets the central april tag ID for the speaker */
    protected static int getSpeakerID() {
        if (isTeamBlue) {
            return 7;
        } else {
            return 4;
        }
    }

    /**
     * Gets left or right april tag id for the source
     * 
     * @param direction takes in a value of "left" or "right"
     * @return Returns -1 if direction is typed inccorectly
     */
    protected static int getSourceID(String direction) {
        if (isTeamBlue) {
            if (direction.equals("left")) {
                return 2;
            } else if (direction.equals("right")) {
                return 1;
            }
        } else {
            if (direction.equals("left")) {
                return 10;
            } else if (direction.equals("right")) {
                return 9;
            }
        }

        return -1;
    }

    /** Gets the heading for the source */
    protected static int getSourceHeading() {
        if (isTeamBlue) {
            return 220;
        } else {
            return 140;
        }
    }

    /** Gets the april tag ID for the amp */
    protected static int getAmpID() {
        if (isTeamBlue) {
            return 6;
        } else {
            return 5;
        }
    }

    /** Gets the name of the station seen by the robot */
    protected static String getStation(double AprilTagID) {
        if ((int) AprilTagID == getSpeakerID()) {
            return "Speaker";
        }
        if ((int) AprilTagID == getAmpID()) {
            return "Amplifier";
        }
        if ((int) AprilTagID == getSourceID("left")) {
            return "Source (Left)";
        }
        if ((int) AprilTagID == getSourceID("right")) {
            return "Source (Right)";
        }

        return "No station detected";
    }
}
