# Command System

The `Command` class allows the `TimedRobot` class to be used with Autonomous without specifying specific time intervals for code blocks to run. The `Command` class is implemented as follows:

```java
/* Robot.java file */

// Imports static Command methods
import static frc.robot.Command.*;

/* --- Inside Robot class --- */
@Override
public void autonomousInit() {
  resetCommandValues(); // Command reset
}

@Override
public void autonomousPeriodic() {
  resetCommandId(); // Id reset

  // Drive forward
  if (runFor(0.5)) {
    m_frontLeft.set(0.5);
    m_frontRight.set(0.5);
  } 
}
```

- All of the methods from `Command` are imported into the `Robot` class.
- In `autonomousInit()`, `resetCommandValues()` is called.
- In `autonomousPeriodic()`, `resetCommandId()` is called.

This setup allows the `runFor()` method to be used in `autonomousPeriodic()`. This method takes in one parameter, *seconds*, that dictates the length of time the command should be run for. When `runFor()` is called as part of an *if* statement, all commands within the *if* statement are run for the specified time.