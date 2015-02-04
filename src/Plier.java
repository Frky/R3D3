import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;

public class Plier {
	
	private static int ANGLE_RANGE = 1257;
	private static int CLOSED = 0;
	private static int OPENED = 1;
	private int state = 0;
	private SensorMode ts;
	
	private EV3MediumRegulatedMotor plierMotor;
	private EV3TouchSensor touchSensor;
	
	public Plier(char plierPort, int touchPort) {
		Port realPlierPort, realTouchPort;
		switch (plierPort) {
		case 'A':
			realPlierPort = MotorPort.A;
		case 'B':
			realPlierPort = MotorPort.B;
		case 'C':
			realPlierPort = MotorPort.C;
		default:
			realPlierPort = MotorPort.A;
		}
		switch (touchPort) {
		case 1:
			realTouchPort = SensorPort.S1;
		case 2:
			realTouchPort = SensorPort.S2;
		case 3:
			realTouchPort = SensorPort.S3;
		default:
			realTouchPort = SensorPort.S1;
		}
		this.plierMotor = new EV3MediumRegulatedMotor(realPlierPort);
		this.touchSensor = new EV3TouchSensor(realTouchPort);
		ts = this.touchSensor.getTouchMode();
		
	}
	
	public Plier(Port port, int state) {
		this.plierMotor = new EV3MediumRegulatedMotor(port);
		this.state = state;
	}
	
	public void test_rotate(int ang) {
		this.plierMotor.rotate(ang);
		return;
	}
	
	public boolean isEmpty() {
		float mes[] = new float[ts.sampleSize()];
		touchSensor.fetchSample(mes, 0);
		return !(mes[0] > 0);
	}
	
	public void close() {
		if (this.state <= CLOSED)
			return;
		this.plierMotor.rotate(-ANGLE_RANGE);
		this.state -= 1;
	}
	
	public void open() {
		if (this.state >= OPENED)
			return;
		this.plierMotor.rotate(ANGLE_RANGE);
		this.state += 1;
	}
	
}
