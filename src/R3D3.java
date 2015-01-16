import lejos.hardware.Button;

public class R3D3 {

	public static void main(String[] args) {
	
		try {
			Robot robot = new Robot();
			
			while (true) {
				Button.ENTER.waitForPress();
				// System.out.println("Color on spot: " + ColorSensor.color_names[cs.color_on_spot()]);
				robot.find_line();
				robot.align_line();
			}
			
		} catch (Throwable e) {
			System.out.println(e.toString());
			Button.ENTER.waitForPress();
		}
	}	
}
