package frc.robot;

public class Command {
    // Command variables
    private static int currentCommandNumber = -1;
    private static double endTime = 0.0;

    /**
     * Resets the values for command variables
     */
    public static void resetValues() {
        currentCommandNumber = -1;
        endTime = 0.0;
    }

    /**
     * Allows commands to be run for a specified time rather than within a range of
     * timer.get() values.
     * 
     * @param seconds Time for the command to run.
     * @param cmdId   Unique identifier for the command.
     * 
     * @return Whether the current command should continue to run.
     */
    public static boolean runFor(double seconds, int cmdId) {
        double currentTime = Robot.timer.get();

        // Handle old and current commands
        if (cmdId < currentCommandNumber) {
            return false;
        }
        if (cmdId == currentCommandNumber) {
            return (endTime > currentTime);
        }

        // When a new command is recieved
        if (cmdId > currentCommandNumber) {
            currentCommandNumber = cmdId;
            endTime = currentTime + seconds;

            return true;
        }

        System.err.println("Command ID not recognized");
        return false;
    }
}
