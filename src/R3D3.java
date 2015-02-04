import lejos.hardware.Button;

public class R3D3 {

	/**
	 * Fonction principale: contient l'algorithme général du robot
	 */
	public static void main(String[] args) {
	
		/*
		 * Dans sa version actuelle, le robot marquera au mieux un point.
		 * Il est évident que dans le cadre d'un match, l'algorithme présenté
		 * ici est à répéter tant qu'il y a des palets.
		 */
		
		try {
			/* Création d'un objet robot */
			Robot robot = new Robot();
			/* Premièrement, le robot va au centre du plateau */
			robot.go_to_center();
			/* Ensuite, il cherche un palet */
			float puck_dist = robot.best_search_puck();
			/* S'il n'en a pas trouvé */
			while (puck_dist ==-1F) {
				/* Il pivote sur lui-même d'un angle aléatoire */
				robot.motor.rotate((int) Math.round(Math.random() * 360));
				/* Il avance de 15 cm */
				robot.motor.avanti(15);
				/* Et cherche à nouveau un palet */
				puck_dist = robot.best_search_puck();
			}
			/* Une fois un palet trouvé, prend le palet */
			robot.take_puck(puck_dist);
			/* S'oriente au nord (c'est-à-dire face à l'enbut où déposer le palet) */
			robot.orientate_north();	
			/* Va déposer le palet dans l'enbut */
			robot.score();
			/* Retourne au centre du plateau */
			robot.go_to_center();
			
		} catch (Throwable e) {
			System.out.println(e.toString());
			Button.ENTER.waitForPress();
		}
	}	
}
