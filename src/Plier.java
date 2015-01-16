import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public class Plier {
	
	private static int ANGLE_RANGE = 800;
	private static int CLOSED = 0;
	private static int OPENED = 1;
	private int state = CLOSED;
	
	private EV3MediumRegulatedMotor plierMotor;
	
	public Plier(char port) {
		Port realPort;
		switch (port) {
		case 'A':
			realPort = MotorPort.A;
		case 'B':
			realPort = MotorPort.B;
		case 'C':
			realPort = MotorPort.C;
		default:
			realPort = MotorPort.A;
		}
		this.plierMotor = new EV3MediumRegulatedMotor(realPort);
	}
	
	public Plier(Port port, int state) {
		this.plierMotor = new EV3MediumRegulatedMotor(port);
		this.state = state;
	}
	
	public void test_rotate(int ang) {
		this.plierMotor.rotate(ang);
		return;
	}
	
	public void close() {
		if (this.state == CLOSED)
			return;
		this.plierMotor.rotate(-ANGLE_RANGE);
	}
	
	public void open() {
		if (this.state == OPENED)
			return;
		this.plierMotor.rotate(ANGLE_RANGE);
	}
	
}
