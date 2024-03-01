package frc.robot;

public class Controller {
    protected static double correctInput(double driverInput) {
        if ((driverInput > 0 && driverInput < 0.15) || (driverInput > -0.15 && driverInput < 0)) {
            return 0;
        }

        return driverInput;
    }
}
