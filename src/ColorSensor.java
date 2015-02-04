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
	
	public static final int BACKGROUND = 0;
	public static final int GOAL_LINE = 1;
	public static final int CROSS = 2;
	public static final int EAST = 3;
	public static final int WEST = 4;
	public static final int NORTH = 5;
	public static final int SOUTH = 6;
	public static final int UNDEF = -1;
	
	private float[][] default_colors = {
										{49.01F, 54.90F, 37.25F},	// BACKGROUND
										{144.11F, 148.03F, 96.07F},	// GOAL_LINE
										{10.78F, 11.76F, 7.84F},	// CROSS
										{128.43F, 111.68F, 19.60F},	// EAST
										{70.58F, 14.70F, 7.84F},	// WEST
										{8.82F, 16.66F, 23.53F},	// NORTH
										{26.47F, 50.00F, 13.72F},	// SOUTH
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
		if (dist > 20)
			return BACKGROUND;
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
