package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class Controller {
    private static final XboxController controller = new XboxController(0);
    
    // *Controller corrections
    /** Correct controller input */
    protected static double correctInput(double driverInput) {
        if ((driverInput > 0 && driverInput < 0.15) || (driverInput > -0.15 && driverInput < 0)) {
            return 0;
        }

        return driverInput;
    }

    /** Up and down on left analog stick */
    protected static double leftStickY() {
        return correctInput(controller.getLeftY());
    }
    
    /** Left and right on left analog stick */
    protected static double leftStickX() {
        return correctInput(controller.getLeftX());
    }

    /** Left and right on right analog stick */
    protected static double rightStickX() {
        return correctInput(controller.getRightX());
    }

    // *Triggers
    protected static boolean leftTrigger() {
        return (controller.getLeftTriggerAxis() > 0.75);
    }

    protected static boolean rightTrigger() {
        return (controller.getRightTriggerAxis() > 0.75);
    }


    // *D Pad
    protected static boolean dPad_Up() {
        // TODO: Setup d pad
        return false;
    }
    protected static boolean dPad_Down() {
        // TODO: Setup d pad
        return false;
    }
    protected static boolean dPad_Left() {
        // TODO: Setup d pad
        return false;
    }
    protected static boolean dPad_Right() {
        // TODO: Setup d pad
        return false;
    }
}
