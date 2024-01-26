package frc.robot;

public class Command {
    // Command variables
    private static int commandId;
    private static int currentCommandNumber;
    private static double endTime;

    /**
     * Implement at the start of periodic functions. Resets the values of commandId.
     */
    protected static void resetCommandId() {
        commandId = -1;
    }
    
    /**
     * Implement at the start of init functions. Resets the values for all command variables
     */
    protected static void resetValues() {
        currentCommandNumber = -1;
        commandId = -1;
        endTime = 0.0;
    }

    /**
     * Allows commands to be run for a specified time rather than within a range of
     * timer.get() values.
     * 
     * @param seconds Time for the command to run.
     * 
     * @return Whether the current command should continue to run.
     */
    protected static boolean runFor(double seconds) {
        double currentTime = Robot.timer.get();
        commandId++;

        // Handle old and current commands
        if (commandId < currentCommandNumber) {
            return false;
        }
        if (commandId == currentCommandNumber) {
            return (endTime > currentTime);
        }

        // When a new command is recieved
        if (commandId > currentCommandNumber) {
            currentCommandNumber++;
            endTime = currentTime + seconds;

            return true;
        }

        System.err.println("Command ID not recognized");
        return false;
    }
}
