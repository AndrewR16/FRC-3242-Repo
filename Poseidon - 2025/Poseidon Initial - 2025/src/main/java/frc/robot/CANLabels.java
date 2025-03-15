package frc.robot;

import java.util.HashMap;

public class CANLabels {
    public static HashMap<Integer, String> getLabels() {
        HashMap<Integer, String> sparkMap = new HashMap<Integer, String>();
        
        sparkMap.put(1, "Rear Left Turning");
        sparkMap.put(2, "Front Left Turning");
        sparkMap.put(3, "Front Right Turning");
        sparkMap.put(4, "Rear Right Turning");
        sparkMap.put(5, "Rear Left Driving");
        sparkMap.put(6, "Front Left Driving");
        sparkMap.put(7, "Front Right Driving");
        sparkMap.put(8, "Rear Right Driving");
        sparkMap.put(9, "Lift");
        sparkMap.put(10, "Gantry");
        sparkMap.put(11, "Jaw");
        sparkMap.put(12, "Shooter");
        
        return sparkMap;
    }
}
