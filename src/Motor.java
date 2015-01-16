import java.io.IOException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.PilotProps;


public class Motor {
	
	private static float WHEEL_DIAMETER = (float) 4.0;
	private static float TRACK_WIDTH = (float) 17.0;
	
	private RegulatedMotor rightMotor;
	private RegulatedMotor leftMotor;
	private DifferentialPilot motor;
	
	public Motor(String lwheel, String rwheel) {
		PilotProps pp = new PilotProps();
    	try {
			pp.loadPersistentValues();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);  // PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, lwheel));
    	rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);//PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, rwheel));
		motor = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, leftMotor, rightMotor, false);
    	motor.setAcceleration(4000);
		motor.setTravelSpeed(15);
		motor.setRotateSpeed(45);
	}
	
	public void setRotateSpeed(int speed) {
		motor.setRotateSpeed(speed);
	}
	
	public void setForwardSpeed(int speed) {
		motor.setTravelSpeed(speed);
	}
	
	public void rotate(int ang) {
		motor.rotate(ang);
	}	
	
	public void rotate() {
		motor.rotateLeft();
	}
	
	public void avanti() {
		motor.forward();
	}
	
	public void avanti(double dist) {
		motor.travel(dist);;
	}
	
	public void indietro() {
		motor.backward();
	}
	
	public void stop() {
		motor.stop();
	}
}
