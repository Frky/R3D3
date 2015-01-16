
public class Color {

	private float R;
	private float G;
	private float B;
	public String name;
	
	public Color(String name, float r, float g, float b) {
		this.name = name;
		R = r;
		G = g;
		B = b;
	}
	
	public Color(String name, float[] rgb) {
		this.name = name;
		R = rgb[0];
		G = rgb[1];
		B = rgb[2];
	}
	
	public float dist(Color c) {
		return (float) Math.sqrt(
					(c.R - R)*(c.R - R) + 
					(c.G - G)*(c.G - G) + 
					(c.B - B)*(c.B - B));
	}
}
