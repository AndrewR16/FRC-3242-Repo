package frc.robot;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;

public class AutoController {
    //* Drive Motors
    private CANSparkMax frontRight;
    private CANSparkMax frontLeft;
    private CANSparkMax backRight;
    private CANSparkMax backLeft;

    //* Non-Drive Motors
    private CANSparkMax shooter;
    private CANSparkMax intake;
    private CANSparkMax flipBack;
    private CANSparkMax ampScoring;

    //* Sensors
    private DigitalInput intakeInf;
    private DigitalInput intakeDownSwitch;
     private DigitalInput intakeUpSwitch;
    private DigitalInput ampInf;
    private DigitalInput ampDownSwitch;
    public AutoController(CANSparkMax fr, CANSparkMax fl, CANSparkMax br, CANSparkMax bl, CANSparkMax sh, CANSparkMax in, CANSparkMax fb, CANSparkMax as, DigitalInput ii, DigitalInput ids, DigitalInput ius, DigitalInput ai ){
       //* Drive
        frontRight = fr;
        frontLeft = fl;
        backLeft = bl;
        backRight = br;
        //* Non-Drive
        shooter = sh;
        intake = in;
        flipBack = fb;
        ampScoring = as;
        //* Sensors
        intakeInf = ii;
        intakeDownSwitch = ids;
        intakeUpSwitch = ius;
        ampInf = ai;
    }   
    //yippee

    //* Driving Auto
    public void moveForward(double speed){
        frontLeft.set(speed);
        frontRight.set(speed);
        backLeft.set(speed);
        backRight.set(speed);
        //"all speed no motor"-AUTO-nomous
    }
    public void moveBackward(double speed){
        frontLeft.set(-speed);
        frontRight.set(-speed);
        backLeft.set(-speed);
        backRight.set(-speed);
    }
    public void pause(){
        frontLeft.set(0);
        frontRight.set(0);
        backLeft.set(0);
        backRight.set(0);
    }
    public void strafeRight(double speed){
         frontLeft.set(-speed);
        frontRight.set(speed);
        backLeft.set(speed);
        backRight.set(-speed);
    }
     public void strafeLeft(double speed){
         frontLeft.set(speed);
        frontRight.set(-speed);
        backLeft.set(-speed);
        backRight.set(speed);
    }
     public void turnRight(double speed){
         frontLeft.set(speed);
        frontRight.set(-speed);
        backLeft.set(speed);
        backRight.set(-speed);
    }
     public void turnLeft(double speed){
         frontLeft.set(-speed);
        frontRight.set(speed);
        backLeft.set(speed);
        backRight.set(-speed);
    }
    //* Scoring Auto
     public void setSpeakerScoring(double speed){
       shooter.set(Constants.shooterSpeed);
    }
     public void setAmpScoring(double speed){
       
    }

    //*Intake Auto
     public void setFlipBack(double speed){
 
        flipBack.set(speed);    
     }
     public void setIntake(double Speed){
            intake.set(Speed);
     }
    
    
    //*Amp Auto
    public void ampAuto(double speed){

    }
    
}
