package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class LED {
     private static Spark ledController;
  public LED(Spark spark){
    ledController = spark;

  }

  
public void setColor(double colorValue){
    ledController.set(colorValue);
}
public void rainbow(){
  ledController.set(-0.99);
}
public void rainbowParty(){
  ledController.set(-0.97);
}
public void rainbowOcean(){
  ledController.set(-0.95);
}
public void rainbowLava(){
  ledController.set(-0.93);
}
public void rainbowForest(){
  ledController.set(-0.91);
}
public void rainbowGlitter(){
  ledController.set(-0.89);
}
public void confetti(){
  ledController.set(-0.87);
}
public void americanRed(){
  ledController.set(-0.85);
}
public void americanBlue(){
  ledController.set(-0.83);
}
public void americanWhite(){
  ledController.set(-0.81);
}
public void sinelonRainbow(){
  ledController.set(-0.79);
}
public void sinelonParty(){
  ledController.set(-0.77);
}
public void sinelonOcean(){
  ledController.set(-0.75);
}
public void sinelonLava(){
  ledController.set(-0.73);
}
public void sinelonForest(){
  ledController.set(-0.71);
}
public void hotPink(){
  ledController.set(0.57);
}
public void crimsonRed(){
  ledController.set(0.59);
}
public void red(){
  ledController.set(0.61);
}
public void redOrange(){
  ledController.set(0.21);
}
public void orange(){
  ledController.set(0.65);
}
public void gold(){
  ledController.set(0.67);
}
public void yellow(){
  ledController.set(0.69);
}
public void lawnGreen(){
  ledController.set(0.71);
}
public void lime(){
  ledController.set(0.73);
}
public void darkGreen(){
  ledController.set(0.75);
}
public void green(){
  ledController.set(0.77);
}
public void blueGreen(){
  ledController.set(0.79);
}
public void aqua(){
  ledController.set(0.81);
}
public void skyBLue(){
  ledController.set(0.83);
}
public void darkBlue(){
  ledController.set(0.85);
}
public void blue(){
  ledController.set(0.87);
}
public void blueViolet(){
  ledController.set(0.89);
}
public void violet(){
  ledController.set(0.91);
}
public void white(){
  ledController.set(0.93);
}
public void gray(){
  ledController.set(0.95);
}
public void darkGray(){
  ledController.set(0.97);
}
public void black(){
  ledController.set(0.99);
}
public void twinkleForest(){
  ledController.set(-0.47);
}
public void twinkleOcean(){
  ledController.set(-0.51);
}
public void twinkleRainbow(){
  ledController.set(-0.55);
}
}
