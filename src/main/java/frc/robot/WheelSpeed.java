package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

/**
 * WheelSpeed
 */
public class WheelSpeed {

    private static int kGamePadChannel = 0;

    private static final int kLeftStickX = 0; //left joystick X axis input
    private static final int kLeftStickY = 1;  // left joystick Y axis input
    private static final int kRightStickX = 4;  //right joystick X axis input

    private DeadBand m_stick = new DeadBand();

    private static XboxController m_controllerDriver = new XboxController(kGamePadChannel);


    private static double speedPercent = 1;

    public double ySpeed() {

        double ySpeed = m_stick.SmoothAxis((m_controllerDriver.getRawAxis(kLeftStickY)) * (speedPercent));
        return ySpeed;
    }

    public double xSpeed() {
        double xSpeed = m_stick.SmoothAxis((-m_controllerDriver.getRawAxis(kLeftStickX)) * (speedPercent));
        return xSpeed;
    }

    public double zRotation() {
        
        double zRotat = m_stick.SmoothAxis((-m_controllerDriver.getRawAxis(kRightStickX)) * (speedPercent));
        
        if (zRotat > .5) {
            return 1;
        }else if (zRotat < -.5) {
            return -1;
        }else {
            return zRotat;
        }
    }
    
}