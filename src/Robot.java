
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

/**
 * Class Robot: contains and manage all sensors and motors.
 * Also contains low-level algorithms such as "take a puck", 
 * "find a line", etc.
 */
public class Robot {
	
	/* Motor that allows to move and rotate */
	public Motor motor;
	/* Robot plier */
	public Plier plier;
	/* Distance sensor */
	private Eye eye;
	/* Color sensor */
	private ColorSensor color;
	
	/* Definition of several rotation speeds to 
	 * use according to specific cases 
	 */
	private static int slowRotateSpeed = 10;
	private static int fastRotateSpeed = 30;
	
	/* Definition of several travel speeds to 
	 * use according to specific cases 
	 */
	private static int slowFWSpeed = 10;
	private static int mediumFWSpeed = 20;
	private static int fastFWSpeed = 30;
	
	/* Global variables used to caracterize a puck
	 * (they highly influence the quality of puck detection)
	 * A puck is an object of size between 10cm and 30cm
	 * (NB. The size of an object is computed according to
	 * its distance ant perceived width - cf scan function)
	 */
	private static float PUCK_MAX_SIZE = 0.30F;
	private static float PUCK_MIN_SIZE = 0.10F;
	
	private static float SEUIL = 0.20F;
	
	public Robot() {
		motor = new Motor("B", "C");
		plier = new Plier('A', 1);
		color = new ColorSensor(SensorPort.S4);
		eye = new Eye(SensorPort.S3);
		/* Do we calibrate colors ? */
		System.out.println("Calibrate colors ? (Y <-|-> N)");
		if (Button.waitForAnyPress() == Button.ID_LEFT)
			color.calibrate();
		else 
			/* If no, use default colors */
			color.set_defaults();
		System.out.println("Let's go.");
	}
	
	/**
	 * This function aims to go forward until we find a line.
	 * TODO: currently, does not avoid collision. If there is no
	 * line between current robot position and a wall, will fail.
	 */
	private void find_line() {
		motor.setForwardSpeed(fastFWSpeed);
		/* If we see the background color */
		if (color.color_on_spot() == ColorSensor.BACKGROUND) {
			/* Then move forward */
			motor.avanti();
			/* As long as we still see background color */
			while (color.color_on_spot() == ColorSensor.BACKGROUND) {
				Delay.msDelay(10);
			}
			/* And then we stop, it means that we are currently seeing another color
			 * (not Background color) 
			 */
			motor.stop();
		}
	}
	
	/**
	 * Align the robot on current line. Requires find_line first.
	 * Because the robot may have stopped a few centimeters after 
	 * the line, we first go backwards to retrieve the line, and then 
	 * we align on it. 
	 */
	private void align_line() {
		System.out.println("Alignment ...");
		motor.setForwardSpeed(slowFWSpeed);
		motor.setRotateSpeed(slowRotateSpeed);
		int current_color;
		/* Let's go backwards slowly */
		motor.indietro();
		/* As long as we see the background */
		while (color.color_on_spot() == ColorSensor.BACKGROUND || color.color_on_spot() == ColorSensor.UNDEF) 
			Delay.msDelay(5);
		/* Now that we are back on the line, let's check its color */
		current_color = color.color_on_spot();
		System.out.println("Color on spot: " + ColorSensor.color_names[current_color]);
		
		/* Now that the color sensor is on the line, we go forward to have the rotation center
		 * of the robot on the line
		 */
		
		/* If this is the goal line, because its width is higher, we need to go a little 
		 * more forward 
		 */
		if (current_color == ColorSensor.GOAL_LINE)
			motor.avanti(10);
		else
			motor.avanti(7.);
		
		/* Then we rotate until we find back the line */
		motor.rotate();
		while (color.color_on_spot() != current_color) {
			Delay.msDelay(50);
		}
		motor.stop();
		/* And if it is the goal line, we rotate a little more to be aligned */
		if (current_color == ColorSensor.GOAL_LINE)
			motor.rotate(5);
	}
	
	/**
	 * This function is used to go take a puck that we detected before.
	 * Robot must be aligned with the puck.
	 * 
	 * @param dist	distance between the robot and the puck to take
	 */
	public void take_puck(float dist) {
		System.out.println("Take Puck at dist " + dist + "...");
		/* Open the plier */
		plier.open();
		/* Go forward */
		motor.setForwardSpeed(fastFWSpeed);
		motor.avanti();
		/* While the puck is not detected by the captor in the plier */
		while (plier.isEmpty()) {
			/* We check whether we still see the puck or not */
			if (eye.watch() < 0.40) {
				/* If we see it at less than 0.4 meters, we break */
				System.out.println("Less than 0.4");
				break;
			}
			Delay.msDelay(10);
		}
		/* Lets stop */
		motor.stop();
		/* If we break because of the distance */
		if (plier.isEmpty())
			/* We go forward by 0.4 meters (max of the distance that 
			 * still separes robot from puck 
			 */
			motor.avanti(40);
		/* And finally close the plier */
		plier.close();
	}

	/**
	 * Here, the robot goes forward until he crosses the goal line.
	 * Of course, the robot must be (almost) aligned in the right direction.
	 * 
	 */
	private void go_to_inline() {
		motor.setForwardSpeed(fastFWSpeed);
		/* While we do not see the goal line, we go forward */
		if (color.color_on_spot() != ColorSensor.GOAL_LINE) {
			motor.avanti();
			while (color.color_on_spot() != ColorSensor.GOAL_LINE) {
				Delay.msDelay(10);
			}
			motor.stop();
		}
	}
	
	/**
	 * Here, the robot goes forward until he crosses the cross line.
	 * 
	 */
	private void go_to_cross() {
		motor.setForwardSpeed(fastFWSpeed);
		/* While we do not see the cross line, we go forward */
		if (color.color_on_spot() != ColorSensor.CROSS) {
			motor.avanti();
			while (color.color_on_spot() != ColorSensor.CROSS) {
				Delay.msDelay(10);
			}
			motor.stop();
		}
	}
	
	/**
	 * This functions is in charge of scoring, i.e. finding the 
	 * goal line, and then put the puck
	 * 
	 */
	public void score() {
		/* Go until goal line */
		go_to_inline();
		/* Put the puck */
		plier.open();
		/* Go backward */
		motor.indietro(10);
		/* Close the plier */
		plier.close();
		return;
	}
	
	/**
	 * This function put the robot at a given distance from the 
	 * first object it is seeing through sonar.
	 * 
	 * @param dist	distance to put between robot and first object
	 */
	private void place_at_dist(float dist) {
		motor.setForwardSpeed(mediumFWSpeed);
		/* Let's check if we need to go backward or forward */
		int initial_sign = (dist - eye.watch())>0?1:-1;
		/* If we are too far, forward */
		if (eye.watch() > dist)
			motor.avanti();
		/* else, backward */
		else
			motor.indietro();
		while (true) {
			/* Then, stop when the sign between wanted dist and real dist has changed */
			int curr_sign = (dist - eye.watch())>0?1:-1;
			if (curr_sign != initial_sign)
				break;
			Delay.msDelay(100);
		}
		motor.stop();
		return;
	}	
	
	/**
	 * Find the next line (going forward), but ignoring the cross line
	 * This function tries to avoid collisions. The robot will rotate 
	 * of 120° if it is too close to an object (eg a wall).
	 * 
	 * @return 	The color index of the line we found 
	 */
	private int find_line_except_cross() {
		System.out.println("Find line except cross ...");
		motor.setForwardSpeed(fastRotateSpeed);
		int curr_color = color.color_on_spot();
		/* If we are not already on a line */
		if (curr_color == ColorSensor.BACKGROUND || curr_color == ColorSensor.CROSS) {
			/* Let's go forward */
			motor.avanti();
			/* Until we find a line that is not the cross */
			while (curr_color == ColorSensor.BACKGROUND || curr_color == ColorSensor.CROSS) {
				/* If we are too close to an object, let's rotate of 120° */
				if (eye.watch() < 0.25) {
					motor.rotate(120);
				}
				/* And resume the go-forward */
				motor.avanti();
				curr_color = color.color_on_spot();
				Delay.msDelay(10);
			}
			motor.stop();
		}
		return curr_color;
	}
	
	/**
	 * Compute the angle we need to rotate to align with the goal zone, 
	 * depending on the line we are currently on and the scan results 
	 * 
	 * @param scan_results	Scan measures of the environment perpendicularily to
	 * 						the line we are currently on
	 * @param line_crossed	Line we are currently on
	 * 
	 * @return	angle to rotate in order to align with scoring zone
	 */
	private int angle_to_align_on_goal(float[] scan_results, int line_crossed) {
		/* Depending on the line we are on */
		switch (line_crossed) {
		case ColorSensor.EAST:
			/* If we have a measure that is more than one meter */
			if (more_than(1F, scan_results)) {
				/* Means that we are looking WEST */
				return -90;
			} else {
				/* Else, means we are looking EAST */
				return +90;
			}
		case ColorSensor.WEST:
			/* If we have a measure that is more than one meter */
			if (more_than(1F, scan_results)) {
				/* Means that we are looking EAST */
				return 90;
			} else {
				/* Else we are looking WEST */
				return -90;
			}
		case ColorSensor.NORTH:
			/* If we have a measure that is more than one and half meter */
			if (more_than(1.5F, scan_results)) {
				/* Means that we are looking SOUTH */
				return 180;
			} else {
				/* Else we are looking NORTH */
				return 0;
			}
		case ColorSensor.SOUTH:
			/* If we have a measure that is more than one and half meter */
			if (more_than(1.5F, scan_results)) {
				/* Means that we are looking NORTH */
				return 0;
			} else {
				/* Else we are looking SOUTH */
				return 180;
			}
		}
		return 0;
	}

	/**
	 * Test if at least one measure in the mes is higher than a given value 
	 * 
	 * @param v		The limit value to be tested
	 * @param mes	The set of values
	 * @return 		true iif at least one value of mes is higher than v
	 */
	private boolean more_than(float v, float[] mes) {
		for(int j = 1; j < mes.length; j++) {
			if (mes[j] > v){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Scan all objects whithin deg degrees (scan means get distances)
	 * 
	 * @param 	deg	The angle to scan
	 * @return	a set of measures of distances
	 */
	private float[] scan(int deg) {
		System.out.println("Scan ...");
		/* Array List to temporary store the values */
		ArrayList<Float> mes_list = new ArrayList<Float>();
		/* Let's rotate of deg degrees */
		motor.setRotateSpeed(fastRotateSpeed);
		motor.rotate(deg, true);
		/* WHile the robot is rotating */
		while (motor.is_rotating()) {
			/* Let's take a measure every 5 ms */
			mes_list.add(eye.watch());
			Delay.msDelay(5);
		}
		/* Creation of an array to store the final values */
		float mes[] = new float[mes_list.size()];
		try {
			/* If possible, write the values in a file */
			FileWriter out = new FileWriter("mesures.out");
			/* Copy each element from array list to float array */
			for (int i = 0; i < mes_list.size(); i++) {
				out.write(mes_list.get(i).toString() + "\n");
				mes[i] = mes_list.get(i);
			}
			out.close();
		} catch (IOException e) {
			
		}
		/* Return the float array */
		return mes;
	}
	
	/**
	 * This function is in charge to orientate the robot to the north (ie to the scoring area), 
	 * with no initial knowledge on current position
	 */
	public void orientate_north() {
		System.out.println("Orientate North ...");
		int trigo = (Math.random() < 0.5)?-1:1;
		/* First, let's find a line to get information about current position */
		int line = find_line_except_cross();
		/* Then we align with the line we found */
		align_line();
		/* We rotate of 70 degrees */
		motor.setRotateSpeed(fastRotateSpeed);
		motor.rotate(trigo * 70);
		/* Then we perform a scan on 40 degrees */
		float mes[] = scan(trigo * 40);
		motor.setRotateSpeed(fastRotateSpeed);
		/* We go back to 90 degrees from the line we were aligned with 
		 * (we rotated of 70 + 40 degrees, so know we are at 110 => -20 is needed) 
		 * */
		motor.rotate((-trigo) * 20);
		motor.setRotateSpeed(fastRotateSpeed);
		/* Then we compute the angle needed to orientate north, depending on the line we are on 
		 * and the scan result 
		 */
		if (angle_to_align_on_goal(mes, line)  == 180 || angle_to_align_on_goal(mes, line) == -180)
			motor.rotate(180);
		else if (angle_to_align_on_goal(mes, line) < 0) {
			motor.rotate_right();
			while (color.color_on_spot() != line) { Delay.msDelay(10); }
			motor.stop();
		} else if (angle_to_align_on_goal(mes, line) > 0) {
			motor.rotate();
			while (color.color_on_spot() != line) { Delay.msDelay(10); }
			motor.stop();
		}
		return;
	}
	
	/**
	 * Scan over 360 degrees, measure the distances, and perform 
	 * computation to determine where is the nearest puck.
	 * 
	 * @return distance to the nearest puck
	 */
	public float best_search_puck() {
		/* First, let's get data from a scan */
		float dist[] = scan(360);
		float prev_dist = dist[0];
		int obj_start = 0;
		int nb_obj = 0;
		Object first_object = null;
		/* Queue of objects detected, ordered by distance */
		PriorityQueue<Object> obj = new PriorityQueue<Object>();
		/* Iteration on distance measures */
		for (int i = 0; i < dist.length; i++) {
			/* If measure is Infinity */
			if (dist[i] == Float.POSITIVE_INFINITY) {
				/* If previous mesure was not infinity, this is a discontinuity */
				if (prev_dist != Float.POSITIVE_INFINITY) {
					/* Then, we have a new object from the previous discontinuity to
					 * the measure before this one 
					 */
					float[] obj_mes = new float[i - 1 - obj_start];
					/* Let's copy the measures corresponding to the object in a new float array */
					for (int j = 0; j < i - 1 - obj_start; j++) {
						obj_mes[j] = dist[obj_start + j];
					}
					/* Creation of a new object with related measures */
					Object o = new Object(obj_start, i - 1, obj_mes);
					/* From the related measures and the total number of measures, 
					 * the object can compute its size, average distance, etc.
					 */
					o.compute(dist.length);
					/* If it is the first object, we need to keep it in mind, 
					 * because we will probably need to take into account the 
					 * last measures of the scan too
					 */
					if (nb_obj == 0) {
						first_object = o;
					} else {
						obj.add(o);
					}
					nb_obj += 1;
				}
			} else {
				/* If we do not measure infinity but the previous measure was infinity, 
				 * then we are looking at a new object 
				 */
				if (prev_dist == Float.POSITIVE_INFINITY)
					obj_start = 0;
				/* If two measures are too far one from the other */
				else if (Math.abs(dist[i] - prev_dist) > SEUIL) {
					/* We consider that this is a limit between two objects, 
					 * so let's compute things relatively to the previous one
					 */
					/* The following code is the same as the one commented in the previous case
					 * a few lines earlier 
					 */
					float[] obj_mes = new float[i - 1 - obj_start];
					for (int j = 0; j < i - 1 - obj_start; j++) {
						obj_mes[j] = dist[obj_start + j];
					}
					Object o = new Object(obj_start, i - 1, obj_mes);
					o.compute(dist.length);
					if (nb_obj == 0) {
						first_object = o;
					} else {
						obj.add(o);
					}
					nb_obj += 1;
					obj_start = i;
				}	
			}
			prev_dist = dist[i];
		}
	
		System.out.println("Getting values for last object (dist length: " + dist.length + " , obj_start: " + obj_start + ")");
		/* Special case for last object */
		float[] obj_mes = new float[dist.length - obj_start];
		for (int j = 0; j < dist.length - obj_start; j++) {
			obj_mes[j] = dist[obj_start + j];
		}
		System.out.println("Getting last discontinuity ");
		if (Math.abs(dist[0] - dist[dist.length - 1]) < SEUIL) {
			first_object.add_dist_values(obj_mes, obj_start);
			first_object.compute(dist.length);
		} else {
			Object o = new Object(obj_start, dist.length - 1, obj_mes);
			o.compute(dist.length);
			obj.add(o);
		}
		System.out.println("Add first object ");
		obj.add(first_object);	
		
		/* Now that we have computed data relatively to objects we detected,
		 * we need to find out which one is a puck 
		 */
		float puck_dist = -1F;
		/* While there are still objects in queue */
		while (!obj.isEmpty()) {
			Object o = obj.poll();
			System.out.println("Object at " + o.distance() + " of size " + o.size());
			/* We test if this object can be a puck based on its size */
			if (o.size() < PUCK_MAX_SIZE && o.size() > PUCK_MIN_SIZE) {
				/* If it is a puck, we break */
				puck_dist = (float) o.distance();
				break;
			}
		}
		
		/* If still -1, no puck was detected */
		if (puck_dist <= 0) 
			return -1F;
		
		System.out.println("Puck detected at " + puck_dist);
		
		/* Here we found a puck at a given distance, now we need to retrieve it
		 * in order to align with it 
		 */
		motor.rotate(-360, true);
		while (Math.abs(eye.watch() - puck_dist) > 0.015 * puck_dist) {
			Delay.msDelay(10);
		}
		Delay.msDelay(200);
		motor.stop();
		return puck_dist;
	}
	
	/**
	 * This function leads the robot to the center of the area, knowing 
	 * that it is aligned to a scoring area
	 */
	public void go_to_center() {
		/* Let's find the goal line */
		this.find_line();
		/* Align with the goal line */
		this.align_line();
		/* Place the robot at 60cm from the nearest wall */
		this.place_at_dist(0.60F);
		motor.setRotateSpeed(fastRotateSpeed);
		/* Rotate 90 degrees ; now we are oriented to the center of the map */
		motor.rotate(90);
		/* Let's go forward until we reach the cross */
		this.go_to_cross();
		/* Let's align with the cross */
		this.align_line();
		/* And place the robot at one meter from wall */
		this.place_at_dist(1.00F);
		return;
	}

}
