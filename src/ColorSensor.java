import lejos.hardware.Button;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;


public class ColorSensor {

	private float[] ca;
	private SampleProvider cp;
	private EV3ColorSensor cs;
	
	private static int NB_COLORS;
	public static String[] color_names = {"BACKGROUND", "GOAL_LINE", "CROSS", "EAST", "WEST", "NORTH", "SOUTH"};
	private Color[] colors;
	
	private float[][] default_colors = {
										{49.02F, 55.88F, 39.22F},	// BACKGROUND
										{169.61F, 176.47F, 115.69F},	// GOAL_LINE
										{12.75F, 15.69F, 9.80F},	// CROSS
										{114.12F, 130F, 39, 21.57F},	// EAST
										{73.52F, 15.68F, 8.82F},	// WEST
										{8.82F, 17.64F, 27.45F},	// NORTH
										{25.49F, 50.98F, 13.73F},	// SOUTH
									};
	
	public ColorSensor(Port p) {
		NB_COLORS = color_names.length;
		cs = new EV3ColorSensor(p);
		cp = cs.getRGBMode();
		ca = new float[cp.sampleSize()];
		colors = new Color[NB_COLORS];
	}
	
	public void set_defaults() {
		for (int i = 0; i < NB_COLORS; i++) 
			colors[i] = new Color(color_names[i], default_colors[i]);
	}
	
	public void calibrate() {
		for (int i = 0; i < NB_COLORS; i++) {
			String c_name = color_names[i];
			System.out.println(c_name);
			Button.ENTER.waitForPress();
			float[] rgb = this.read();
			// System.out.println("Size of rgb: " + rgb.length);
			colors[i] = new Color(c_name, rgb);
			System.out.println("RGB: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
		}
	}

	/* TODO color unknown if distance is too high */
	public int recognize_color(Color c) {
		int idx = 0;
		float dist = c.dist(colors[0]);
		for (int i = 1; i < NB_COLORS; i++) {
			if (c.dist(colors[i]) < dist) {
				dist = c.dist(colors[i]);
				idx = i;
			}
		}
		System.out.println("Color: " + color_names[idx]);
		return idx;
	}
	
	public int color_on_spot() {
		Color c = new Color("", read());
		return recognize_color(c);
	}
	
	public float[] read() {
		cp.fetchSample(ca, 0);
		for (int i = 0; i < cp.sampleSize(); i++) 
			ca[i] *= 1000;
		return ca;
	}
	
}
