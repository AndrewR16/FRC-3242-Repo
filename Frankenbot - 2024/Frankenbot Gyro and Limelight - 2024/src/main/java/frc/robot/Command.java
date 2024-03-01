package frc.robot;

import edu.wpi.first.wpilibj.Timer;

public class Command {
    // Command variables
    private static int commandId;
    private static int currentCommandNumber;
    private static double endTime;
    private static boolean commandInProgress;

    // Confirm completed variables
    private static boolean initialCheck;
    private static double stoppingTime;

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

        initialCheck = true;

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

    /**
     * Used to prevent a routine from finishing early incase the resting value is overshot. 
     * @param checkedVariable The variable to check whether its equal to the resting value.
     * @param restingValue The value the checked variable is equal to once the routine is finished.
     * @return Returns true if the checked variable remains in its resting state after 1.5 seconds and false if otherwise
     */
    protected static void checkIfCompleted(double checkedVariable, double restingValue) {
        if (checkedVariable == restingValue) {
            if (initialCheck) {
                stoppingTime = timer.get() + 1.5;
                initialCheck = false;
            } else if (stoppingTime < timer.get()) {
                initialCheck = true;
                commandCompleted();
                // return true;
            }
        }

        // return false;
    }
}
