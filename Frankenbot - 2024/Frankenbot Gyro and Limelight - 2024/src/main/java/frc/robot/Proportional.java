package frc.robot;

public class Proportional {
    /**
     * Proportional motor speed calculator
     * 
     * @param error    The differece between the current value and the desired value
     * @param Kp       The proportional constant
     * @param deadzone The allowed margin of error
     * @return Speed to set the motors
     */
    protected static double calculatePDrive(double error, double Kp, double deadzone) {
        double output = Kp * error;

        // Maximum Output
        if (output < -0.8) {
            output = -0.8;
        }
        if (output > 0.8) {
            output = 0.8;
        }

        // Minimum Output
        if (output > 0 && output < 0.3) {
            output = 0.3;
        }
        if (output < 0 && output > -0.3) {
            output = -0.3;
        }

        // Stop when heading is acheived
        if (Math.abs(error) < deadzone) {
            output = 0;
        }

        return output;
    }

    /**
     * Adjusts the value of the given heading error to be from -180 to 180, with
     * negative values signifying that the robot is turned too far to the left.
     * 
     * @param heading The true heading of the robot
     * @return The adjusted value of the heading error from -180 to 180;
     */
    public static double adjustHeadingValue(double heading) {
        heading %= 360;

        // Read values in this range as from 0 to 180 (Turn counterclockwise)
        if (heading > -360 && heading < -180) {
            heading += 360;
        }

        // Read values in this range as from -180 to 0 (Turn clockwise)
        if (heading > 180 && heading < 360) {
            heading -= 360;
        }

        return heading;
    }
}
