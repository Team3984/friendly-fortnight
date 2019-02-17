package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.XboxController;

/**
 * StopLift
 */
public class StopLift {

    private static int kGamePadChannel = 0;
    private static int kLiftChannel = 7;
    private static final int kXboxButtonLT = 2; // <-- Left Trigger
    private static final int kXboxButtonRT = 3; // <-- Right Trigger

    private DeadBand m_leftTrigger = new DeadBand();
    private DeadBand m_rightTrigger = new DeadBand();
    private WPI_TalonSRX m_liftMotor;

    private static XboxController m_controllerDriver = new XboxController(kGamePadChannel);

    WPI_TalonSRX liftWPI_TalonSRX = new WPI_TalonSRX(kLiftChannel);




    public void stoplift(double liftCmd, double aproxStop, boolean liftStop) {
        m_liftMotor = liftWPI_TalonSRX;

        m_liftMotor.set(liftCmd); 

        if (liftStop == true || (liftStop == true && (liftCmd >= 0 || liftCmd <= 0))){
            rawTriggerGuide(aproxStop);
            //m_liftMotor.stopMotor();      //OPTION 2
          }
        
    }

    public void rawTriggerGuide(double rawRTInput) {
        m_liftMotor = liftWPI_TalonSRX;

        double rage = m_leftTrigger.SmoothAxis(m_controllerDriver.getRawAxis(kXboxButtonLT)) - m_rightTrigger.SmoothAxis(rawRTInput);

        m_liftMotor.set(rage);
        
    }
}