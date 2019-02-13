package frc.robot;
/**
 * A class to try to tone down the dead pan in the joysticks to make
 * the robot less jumpy.
 * Takes in the axis from a joystick, checks if it's under the deadBandrate
 * set the axis to 0.0 and return it.
 */
/**
 * SpeedBoolean
 */
public class SpeedBoolean {

    public static final double rate = 1;

    public double speedrate(boolean out, boolean in){
        //x is in and a is out
        if (out == true) {
            return -rate;
            
          }
          if (in == true) {     //while the button is pressed the code will continue looping
            return rate;
      
          }
          if (out == false && in == false){  //When both buttons are not pressed
            return 0;
      
          }
          if (out == true && in == true) {
              return 0;
          }else {
              return 0;
          }

    }
}