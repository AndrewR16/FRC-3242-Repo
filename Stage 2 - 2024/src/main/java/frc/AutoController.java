package frc;
import javax.management.loading.MLet;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;


public class AutoController {
    //variable declares
    WPI_TalonSRX leftFront;
    WPI_TalonSRX rightFront;
    WPI_TalonSRX leftBack;
    WPI_TalonSRX rightBack;
    WPI_TalonSRX lift1;
    WPI_TalonSRX lift2;
    DoubleSolenoid grabber;
    WPI_TalonSRX leftIntake;
    WPI_TalonSRX rightIntake;
    private final Timer timer = new Timer();


 

    public AutoController(WPI_TalonSRX m_lf, WPI_TalonSRX m_lb, WPI_TalonSRX m_rf, WPI_TalonSRX m_rb, WPI_TalonSRX lone, WPI_TalonSRX ltwo, DoubleSolenoid n,WPI_TalonSRX rI,WPI_TalonSRX lI, double time){
            leftFront = m_lf;
            rightFront = m_rf;
            leftBack = m_lb;
            rightBack = m_rb;
            lift1 = lone;
            lift2 = ltwo;
            grabber = n;
            rightIntake= rI;
            leftIntake = lI;
          //timer.reset();
            //timer.set(time);
    } 
    public void forward(double speed){
        leftFront.set(speed);
        rightFront.set(speed);
        leftBack.set(speed);
        rightBack.set(speed);
    } 
    public void backward(double speed){
        leftFront.set(-speed);
        rightFront.set(-speed);
        leftBack.set(-speed);
        rightBack.set(-speed);
    }
    public void right(double speed){
        leftFront.set(-speed);
        rightFront.set(speed);
        leftBack.set(-speed);
        rightBack.set(speed);
    }
    public void left(double speed){
        leftFront.set(speed);
        rightFront.set(-speed);
        leftBack.set(speed);
        rightBack.set(-speed);
    }
    public void left90(){
        leftFront.set(.582);
        rightFront.set(-.582);
        leftBack.set(.582);
        rightBack.set(-.582);
    }
    public void right90(){
        leftFront.set(-.582);
        rightFront.set(.582);
        leftBack.set(-.582);
        rightBack.set(.582);

    }
    public void stop(){
        leftFront.set(0);
        rightFront.set(0);
        leftBack.set(0);
        rightBack.set(0);
    
    }
    public void robotbreak(){
        leftFront.stopMotor();
        rightFront.stopMotor();
        leftBack.stopMotor();
        rightBack.stopMotor();
    }
    public void lift(){
lift1.set(.4);
lift2.set(.4);
    }
    public void liftStop(){
lift1.set(.15);
lift2.set(.15);
    }
    public void liftDrop(){
        lift1.set(0);
lift2.set(0);
    }
    public void grabberOpen(){
        grabber.set(Value .kForward);
    }
    public void grabberClosed(){
        grabber.set(Value .kReverse);
    }
public void intakeIn(){
    leftIntake.set(.2);
rightIntake.set(.2);
}
public void intakeOut(){
    leftIntake.set(-.2);
rightIntake.set(-.2);

}
public void intakeStop(){
    leftIntake.set(0);
rightIntake.set(0);  
}
public void Reset(){
    timer.reset();
    
intakeStop();
liftDrop(); 
grabberClosed();
}
public void backLeftArc(){
        leftFront.set(-.7);
        rightFront.set(-.3);
        leftBack.set(-.7);
        rightBack.set(-.3);
}
}
