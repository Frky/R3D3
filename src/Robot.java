
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;


public class Robot {
	
	private static boolean CALIBRATE = false;
	private Motor motor;
	private Plier plier;
	private Eye eye;
	private ColorSensor color;
	
	public Robot() {
		motor = new Motor("B", "C");
		plier = new Plier('A');
		color = new ColorSensor(SensorPort.S4);
		if (CALIBRATE) 
			color.calibrate();
		else 
			color.set_defaults();
	}
	
	public void find_line() {
		if (color.color_on_spot() == 0) {
			motor.avanti();
			while (color.color_on_spot() == 0) {
				Delay.msDelay(10);
			}
			motor.stop();
		}
	}
	
	public void align_line() {
		motor.setForwardSpeed(5);
		motor.setRotateSpeed(20);
		int current_color;
		motor.indietro();
		while (color.color_on_spot() == 0) 
			Delay.msDelay(5);
		current_color = color.color_on_spot();
		motor.avanti(7.);
		motor.rotate();
		while (color.color_on_spot() != current_color) {
			Delay.msDelay(10);
		}
		Delay.msDelay(10);
		motor.stop();
		motor.setForwardSpeed(15);
		motor.setRotateSpeed(45);
	}
}
