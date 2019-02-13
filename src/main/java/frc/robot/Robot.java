/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID;// <-- Needed for xbox style controllers
import edu.wpi.first.wpilibj.SpeedControllerGroup;//for the cargo system
import edu.wpi.first.wpilibj.TimedRobot;// <-- New for 2019, takes over for the depricated Iteritive robot
import edu.wpi.first.wpilibj.XboxController;// <-- For using a gamepad controller
import edu.wpi.first.wpilibj.drive.MecanumDrive;// <-- Needed for the drive base.
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;// <-- For writing data back to the drivers station.


/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.  It's been modifed to call the WPI_TalonSRX controllers, which use . 
 */
public class Robot extends TimedRobot {
  // These will need to be updated to the CAN Ids of the WPI_TalonSRX's
  private static int kFrontLeftChannel = 3;
  private static int kRearLeftChannel = 2;
  private static int kFrontRightChannel = 1;
  private static int kRearRightChannel = 0;
  private static int kLiftChannel = 7;   //4 (DOES NOT EXIST)
  private static int kHatchChannel = 4;  //5
  private static int kLeftCargoChannel = 6;
  private static int kRightCargoChannel = 4; //7 

  // What ever USB port we have the controller plugged into.
  private static int kGamePadChannel = 0;

  //mapping out the camera
  private static int kUsbCameraChannel = 0;

  // The rate that the motor controller will speed up to full;
  //private static final double kRampUpRate = 1.5; 

  // Setting the talons neutralmode to brake
  //private static final NeutralMode K_MODE = NeutralMode.Brake; 

  //Lets map out the buttons
  private static final int kXboxButtonA = 1;
  //private static final int kXboxButtonB = 2;
  private static final int kXboxButtonX = 3;
  //private static final int kXboxButtonY = 4;

  //private static final int kXboxButtonLB = 5; // <-- Left Button
  //private static final int kXboxButtonRB = 6; // <-- Right Button
  private static final int kXboxButtonLT = 2; // <-- Left Trigger
  private static final int kXboxButtonRT = 3; // <-- Right Trigger

  private static final int kLeftStickX = 0; //left joystick X axis input
  private static final int kLeftStickY = 1;  // left joystick Y axis input
  private static final int kRightStickX = 4;  //right joystick X axis input
  private static final int kRightStickY = 5;  // right joystick Y axis input

  //private static final double kRampUpRate = 0.0; // The rate that the motor controller will speed up to full;
  
  //hello 
  private UsbCamera m_camera;
  
  private SpeedControllerGroup m_cargoSystem;

  private MecanumDrive m_robotDrive; 

  private XboxController m_controllerDriver; 

  private DeadBand m_stick;
  
  private DeadBand m_leftTrigger; 
  
  private DeadBand m_rightTrigger;  

  private WPI_TalonSRX m_liftMotor;
  
  private WPI_TalonSRX m_hatchMotor;

  private Encoder m_liftEncoder;

  private SpeedBoolean sp;


  
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

    
    WPI_TalonSRX frontLeftWPI_TalonSRX = new WPI_TalonSRX(kFrontLeftChannel);
    WPI_TalonSRX frontRightWPI_TalonSRX = new WPI_TalonSRX(kFrontRightChannel);
    WPI_TalonSRX rearLeftWPI_TalonSRX = new WPI_TalonSRX(kRearLeftChannel);
    WPI_TalonSRX rearRightWPI_TalonSRX = new WPI_TalonSRX(kRearRightChannel);
    WPI_TalonSRX liftWPI_TalonSRX = new WPI_TalonSRX(kLiftChannel);
    WPI_TalonSRX hatchWPI_TalonSRX = new WPI_TalonSRX(kHatchChannel);
    WPI_TalonSRX rightCargoWPI_TalonSRX = new WPI_TalonSRX(kRightCargoChannel);
    WPI_TalonSRX leftCargoWPI_TalonSRX = new WPI_TalonSRX(kLeftCargoChannel);
    
    // Invert the motors.
    // You may need to change or remove this to match your robot.
    frontLeftWPI_TalonSRX.setInverted(true);
    rearRightWPI_TalonSRX.setInverted(true);

    /**
     * Added to test out setting talon config some settings internal
     * to the WPI_TalonSRXs
     */
    //frontRightWPI_TalonSRX.configOpenloopRamp(kRampUpRate);
    //frontRightWPI_TalonSRX.setNeutralMode(K_MODE);

    //frontLeftWPI_TalonSRX.configOpenloopRamp(kRampUpRate);
    //frontLeftWPI_TalonSRX.setNeutralMode(K_MODE);
    
    //rearRightWPI_TalonSRX.configOpenloopRamp(kRampUpRate);
    //rearRightWPI_TalonSRX.setNeutralMode(K_MODE);

    //rearLeftWPI_TalonSRX.configOpenloopRamp(kRampUpRate);
    //rearLeftWPI_TalonSRX.setNeutralMode(K_MODE);

    
    m_liftEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
    m_liftEncoder.setMaxPeriod(1);
    m_liftEncoder.setMinRate(10);
    m_liftEncoder.setDistancePerPulse(5);
    m_liftEncoder.setReverseDirection(true);
    m_liftEncoder.setSamplesToAverage(7);
    
    m_cargoSystem = new SpeedControllerGroup(leftCargoWPI_TalonSRX, rightCargoWPI_TalonSRX);

    m_robotDrive = new MecanumDrive(frontLeftWPI_TalonSRX, rearLeftWPI_TalonSRX, frontRightWPI_TalonSRX, rearRightWPI_TalonSRX);

    //Construct the lift motor

    m_liftMotor = liftWPI_TalonSRX;
    
    //Construct the hatch motor
    
    m_hatchMotor = hatchWPI_TalonSRX;

    // m_controllerDriver = new Joystick(kJoystickChannel);
    
    m_controllerDriver = new XboxController(kGamePadChannel);
    
    m_leftTrigger = new DeadBand();
    
    m_rightTrigger = new DeadBand();

    m_stick = new DeadBand();

    sp = new SpeedBoolean();
  
  } // *********************** End of roboInit **********************************
  
  /**
   * When in teleop this function is called periodicly
   */
  @Override
  public void teleopPeriodic() {
    // Need to come up with a way to tone down the joysticks

    // If I did this right, this should allow for direction of travel to be set by using the left joystick
    // while the rotation of the robot is set by the right stick on the controller.
    m_robotDrive.driveCartesian(m_stick.SmoothAxis(m_controllerDriver.getRawAxis(kLeftStickY)), 
                                m_stick.SmoothAxis(-m_controllerDriver.getRawAxis(kLeftStickX)), 
                                m_stick.SmoothAxis(-m_controllerDriver.getRawAxis(kRightStickX)));
    double liftCmd = m_leftTrigger.SmoothAxis(m_controllerDriver.getRawAxis(kXboxButtonLT)) - m_rightTrigger.SmoothAxis(m_controllerDriver.getRawAxis(kXboxButtonRT));
    double aproxStop = 0.22834645211696625;  //assigning 1/2 way trigger press

    m_liftMotor.set(liftCmd);         //Normal run

    boolean liftStop = m_controllerDriver.getBButton();   //B button stops lift
    
    if (liftStop == true){
      m_liftMotor.set(m_leftTrigger.SmoothAxis(m_controllerDriver.getRawAxis(kXboxButtonLT)) - m_rightTrigger.SmoothAxis(aproxStop));     //OPTION 1
      //m_liftMotor.stopMotor();      //OPTION 2
    }

    if (liftStop == true && (liftCmd >= 0 || liftCmd <= 0)){  //Make sure 'B' overides 'triggers'
    m_liftMotor.set(m_leftTrigger.SmoothAxis(m_controllerDriver.getRawAxis(kXboxButtonLT)) - m_rightTrigger.SmoothAxis(aproxStop));
    }


    System.out.println(m_controllerDriver.getRawAxis(kXboxButtonRT));    //If all else fails, we'll manually assign the value of the trigger axis which holds the lift at a certain elevation
    
    
   ///////////HATCH CODE////////////////////////////////////////////////////////////////////////////////////////////////////
    
   //If this is set up right, it should allow the actuator to extend or retract by using left and right bumpers
    boolean hatchoutSpeed = m_controllerDriver.getBumper(GenericHID.Hand.kLeft);
    boolean hatchinSpeed = m_controllerDriver.getBumper(GenericHID.Hand.kRight);

    m_hatchMotor.set(sp.speedrate(hatchoutSpeed, hatchinSpeed));


    boolean cargoOutSpeed = m_controllerDriver.getAButton();
    boolean cargoInSpeed = m_controllerDriver.getXButton();

    m_cargoSystem.set(sp.speedrate(cargoOutSpeed, cargoInSpeed));


    //////////////////////////CARGO CODE ///////////////////////////////////////////////////////////////////////////////////
    double distance = m_liftEncoder.getDistance();
    //System.out.println(distance);

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
