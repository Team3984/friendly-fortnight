/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID;// <-- Needed for xbox style controllers
//import edu.wpi.first.wpilibj.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.*;// <-- gets us access to WPI_TalonSRX which works with wpilibj.drive.Mecanum
import edu.wpi.first.wpilibj.SpeedControllerGroup;//for the cargo system
import edu.wpi.first.wpilibj.TimedRobot;// <-- New for 2019, takes over for the depricated Iteritive robot
import edu.wpi.first.wpilibj.XboxController;// <-- For using a gamepad controller
import edu.wpi.first.wpilibj.drive.MecanumDrive;// <-- Needed for the drive base.
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;// <-- For writing data back to the drivers station.


/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.  It's been modifed to call the WPI_TalonSRX controllers, which use PWM. 
 */
public class Robot extends TimedRobot {
  // These will need to be updated to the CAN Ids of the WPI_TalonSRX's
  private static int kFrontLeftChannel = 3;
  private static int kRearLeftChannel = 2;
  private static int kFrontRightChannel = 1;
  private static int kRearRightChannel = 0;
  private static int kLiftChannel = 4;
  private static int kHatchChannel = 5;
  private static int kLeftCargoChannel = 6;
  private static int kRightCargoChannel = 7;

  // What ever USB port we have the controller plugged into.
  private static int kGamePadChannel = 0;

  //mapping out the camera
  private static int kUsbCameraChannel = 0;

  // The rate that the motor controller will speed up to full;
  private static final double kRampUpRate = 1.5; 

  // Setting the talons neutralmode to brake
  private static final NeutralMode K_MODE = NeutralMode.Brake; 

  //Lets map out the buttons
  //private static final int kXboxButtonA = 1;
  //private static final int kXboxButtonB = 2;
  //private static final int kXboxButtonX = 3;
  //private static final int kXboxButtonY = 4;

  //private static final int kXboxButtonLB = 5; // <-- Left Button
  //private static final int kXboxButtonRB = 6; // <-- Right Button
  private static final int kXboxButtonLT = 2; // <-- Left Trigger
  private static final int kXboxButtonRT = 3; // <-- Right Trigger

  //private static final double kRampUpRate = 0.0; // The rate that the motor controller will speed up to full;
  

  private UsbCamera m_camera;
  
  private SpeedControllerGroup m_cargoSystem;

  private MecanumDrive m_robotDrive; //hhhhhh

  private XboxController m_controllerDriver; //fcjekjdfej

  private DeadBand m_stick;
  
  private DeadBand m_leftTrigger;  //fwerhfhih
  
  private DeadBand m_rightTrigger;  //ejvejifji

  private DeadBand m_leftBumper;

  private SpeedController m_liftMotor;
  
  private SpeedController m_hatchMotor;


  
  /**
   * This function if called when the robot boots up.
   * It creates the objects that are called by the other robot functions.
   */
  @Override
  public void robotInit() {

    UsbCamera m_camera = CameraServer.getInstance().startAutomaticCapture(kUsbCameraChannel);
    m_camera.setResolution(240, 180);
    //try increasing camera resolution
    //m_camera.setResolution(352, 240);

    //m_camera.setVideoMode(VideoMode.PixelFormat.kYUYV,320,180,30);

    
    WPI_TalonSRX frontLeftTalonSRX = new WPI_TalonSRX(kFrontLeftChannel);
    WPI_TalonSRX frontRightTalonSRX = new WPI_TalonSRX(kFrontRightChannel);
    WPI_TalonSRX rearLeftTalonSRX = new WPI_TalonSRX(kRearLeftChannel);
    WPI_TalonSRX rearRightTalonSRX = new WPI_TalonSRX(kRearRightChannel);
    WPI_TalonSRX liftTalonSRX = new WPI_TalonSRX(kLiftChannel);
    WPI_TalonSRX hatchTalonSRX = new WPI_TalonSRX(kHatchChannel);
    WPI_TalonSRX rightCargoTalonSRX = new WPI_TalonSRX(kRightCargoChannel);
    WPI_TalonSRX leftCargoTalonSRX = new WPI_TalonSRX(kLeftCargoChannel);
    
    // Invert the motors.
    // You may need to change or remove this to match your robot.
    frontLeftTalonSRX.setInverted(true);
    rearRightTalonSRX.setInverted(true);

    /**
     * Added to test out setting talon config some settings internal
     * to the TalonSRXs
     */
    frontRightTalonSRX.configOpenloopRamp(kRampUpRate);
    frontRightTalonSRX.setNeutralMode(K_MODE);

    frontLeftTalonSRX.configOpenloopRamp(kRampUpRate);
    frontLeftTalonSRX.setNeutralMode(K_MODE);
    
    rearRightTalonSRX.configOpenloopRamp(kRampUpRate);
    rearRightTalonSRX.setNeutralMode(K_MODE);

    rearLeftTalonSRX.configOpenloopRamp(kRampUpRate);
    rearLeftTalonSRX.setNeutralMode(K_MODE);

    
    m_cargoSystem = new SpeedControllerGroup(leftCargoTalonSRX, rightCargoTalonSRX);

    m_robotDrive = new MecanumDrive(frontLeftTalonSRX, rearLeftTalonSRX, frontRightTalonSRX, rearRightTalonSRX);

    //Construct the lift motor

    m_liftMotor = liftTalonSRX;
    
    //Construct the hatch motor
    
    m_hatchMotor = hatchTalonSRX;

    // m_controllerDriver = new Joystick(kJoystickChannel);
    
    m_controllerDriver = new XboxController(kGamePadChannel);
    
    m_leftTrigger = new DeadBand();
    
    m_rightTrigger = new DeadBand();

    m_stick = new DeadBand();
  
  } // *********************** End of roboInit **********************************
  
  /**
   * When in teleop this function is called periodicly
   */
  @Override
  public void teleopPeriodic() {
    // Need to come up with a way to tone down the joysticks

    // If I did this right, this should allow for direction of travel to be set by using the left joystick
    // while the rotation of the robot is set by the right stick on the controller.
    m_robotDrive.driveCartesian(m_stick.SmoothAxis(m_controllerDriver.getRawAxis(1)), 
                                m_stick.SmoothAxis(m_controllerDriver.getRawAxis(0)), 
                                m_stick.SmoothAxis(m_controllerDriver.getRawAxis(4)));
    m_liftMotor.set(m_leftTrigger.SmoothAxis(m_controllerDriver.getRawAxis(kXboxButtonLT)) - m_rightTrigger.SmoothAxis(m_controllerDriver.getRawAxis(kXboxButtonRT)));
    
    //If this is set up right, it should allow the actuator to extend or retract by using left and right bumpers
    
    boolean hatchinSpeed = m_controllerDriver.getBumper(GenericHID.Hand.kRight);
    
    if (hatchinSpeed = true){

      m_hatchMotor.set(1);

    }

    boolean hatchoutSpeed = m_controllerDriver.getBumper(GenericHID.Hand.kRight);

    if (hatchoutSpeed = true){

      m_hatchMotor.set(-1);
      
    }

    //m_liftMotor.set(cargoraw);


    
  } // ************************** End of teleopPeriodic *************************
    
  /**
   * testPeriodic function is called periodicly when the DS is 
   * in test mode.
   */
  @Override
  public void testPeriodic(){
  // Ok, so how do we read that a button has been pressed?  Also can we output it to a dashboard?
  //  public static final String ButtonStatus = "Button Pressed:";

    //This block of code should be moved down to
    if (m_controllerDriver.getRawButtonPressed(1)){
      System.out.println("Button A Pressed");
      SmartDashboard.putString("Button A = ", "I was pushed");
    }
    else if (m_controllerDriver.getRawButtonReleased(1)){
      System.out.println("Button A Released");
      SmartDashboard.putString("Button A = " , "I was released");
    }

  } // ************************ End of testPeriodic **************************

}
