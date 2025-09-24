package frc.robot.subsystems;

import org.opencv.core.Mat;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import frc.robot.Constants.CameraConstants;

public class CameraSubsystem {
    Thread m_visionThread;

    public CameraSubsystem () {
        m_visionThread = new Thread(
            () -> {
                UsbCamera camera = CameraServer.startAutomaticCapture();
                
                camera.setResolution(CameraConstants.cameraResolutionWidth, CameraConstants.cameraResolutionHeight);

                CvSink cvSink = CameraServer.getVideo();

                CvSource outputStream = CameraServer.putVideo("Rectangle", 
                CameraConstants.cameraResolutionWidth, 
                CameraConstants.cameraResolutionHeight);

                Mat mat = new Mat();

                while (!Thread.interrupted()) {
                    if (cvSink.grabFrame(mat) == 0) {
                        outputStream.notifyError(cvSink.getError());
                        continue;
                    }
                }
            });
 
        m_visionThread.setDaemon(true);
        m_visionThread.start();
    }
}
