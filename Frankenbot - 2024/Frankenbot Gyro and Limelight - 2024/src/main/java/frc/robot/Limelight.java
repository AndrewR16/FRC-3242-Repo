package frc.robot;

public class Limelight {
    // Alliance color checker
    protected static final boolean isTeamBlue = true;

    // Limelight variables
    protected static final double speakerArea = 0.0;
    protected static final double sourceArea = 0.0;
    protected static final double ampArea = 0.0;

    /**
     * Drive speed calculator for limelight
     * @param error The x axis offset from the april tag
     * @return Speed to set the motors
     */
    protected static double calculatePDrive(double error) {
        double kP = 0.006 * error;
        
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
        if (Math.abs(error) < 1) {
            kP = 0;
        }

        return kP;
    }

    // Central april tag ID for the speaker
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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

    // Gets the heading for the source
    @SuppressWarnings("unused")
    protected static int getSourceHeading() {
        if (isTeamBlue) {
            return 220;
        } else {
            return 140;
        }
    }

    // Gets the april tag ID for the amp
    @SuppressWarnings("unused")
    protected static int getAmpID() {
        if (isTeamBlue) {
            return 6;
        } else {
            return 5;
        }
    }
}
