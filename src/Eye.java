import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Eye {
	
	private float[] sample; 
	private SampleProvider distance;
	private SensorModes sensor;
	
	public Eye(Port port) {
		sensor = new EV3UltrasonicSensor(port);
		distance = sensor.getMode("Distance");
		sample = new float[distance.sampleSize()];	
	}
	
	public float watch() {
		distance.fetchSample(sample, 0);
		return sample[0];
	}
}
