package frc.robot;

import edu.wpi.first.wpilibj.Timer;

public class Command {
    // Command variables
    private static int commandId;
    private static int currentCommandNumber;
    private static double endTime;
    private static boolean commandInProgress;

    // Timer
    private final static Timer timer = new Timer();

    /**
     * Implement at the start of periodic functions. Resets the values of commandId.
     */
    protected static void resetCommandId() {
        commandId = -1;
    }

    /**
     * Implement at the start of init functions. Resets the values for all command
     * variables
     */
    protected static void resetCommandValues() {
        // Reset command variables
        currentCommandNumber = -1;
        commandId = -1;
        endTime = 0.0;
        commandInProgress = false;

        // Resets timer and starts it again
        timer.reset();
        timer.start();
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
        double currentTime = timer.get();
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

        // Handle command faliure
        System.err.println("Command ID not recognized");
        return false;
    }

    protected static boolean runTillComplete() {
        commandId++;
        // Handle old and current commands
        if (commandId < currentCommandNumber) {
            return false;
        }
        if (commandId == currentCommandNumber) {
            return commandInProgress;
        }

        // When a new command is recieved
        if (commandId > currentCommandNumber) {
            currentCommandNumber++;
            commandInProgress = true;

            return true;
        }

        // Handle command faliure
        System.err.println("Command ID not recognized");
        return false;
    }

    protected static void commandCompleted() {
        commandInProgress = false;
    }

    protected static boolean runOnce() {
        commandId++;
        // Handle old and current commands
        if (commandId < currentCommandNumber) {
            return false;
        }
        if (commandId == currentCommandNumber) {
            currentCommandNumber++;
            return true;
        }

        // Handle command faliure
        System.err.println("Command ID not recognized");
        return false;
    }
}
