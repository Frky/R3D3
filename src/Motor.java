import java.io.IOException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.PilotProps;


public class Motor {
	
	private static float WHEEL_DIAMETER = (float) 5.6;
	private static float TRACK_WIDTH = (float) 17.0;
	
	private RegulatedMotor rightMotor;
	private RegulatedMotor leftMotor;
	public DifferentialPilot motor;
	
	public Motor(String lwheel, String rwheel) {
		PilotProps pp = new PilotProps();
    	try {
			pp.loadPersistentValues();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);  
    	rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		motor = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, leftMotor, rightMotor, false);
    	motor.setAcceleration(500);
		motor.setTravelSpeed(15);
		motor.setRotateSpeed(45);
	}
	
	public void calibrate() {
		
	}
	
	public void setRotateSpeed(int speed) {
		motor.setRotateSpeed(speed);
	}
	
	public void setForwardSpeed(int speed) {
		motor.setTravelSpeed(speed);
	}
	
	public void rotate(int ang, boolean async) {
		motor.rotate(ang * 0.715, async);
	}	
	
	public void rotate(int ang) {
		motor.rotate(ang * 0.715);
	}
	
	public void rotate() {
		motor.rotateLeft();
	}
	
	public boolean is_rotating() {
		return motor.isMoving();
	}
	
	public void rotate_right() {
		motor.rotateRight();
	}
	
	public void avanti() {
		motor.forward();
	}
	
	public void avanti(double dist) {
		motor.travel(dist);
	}
	
	public void indietro(double dist) {
		motor.travel(-dist);
	}
	
	public void indietro() {
		motor.backward();
	}
	
	public void stop() {
		motor.stop();
	}
}
