
public class Object implements Comparable<Object> {

	private double size;
	private double angle;
	private double mean_dist;
	private float[] dist;
	private int start;
	private int stop;
	private int idx;
	
	public Object(int idx, double distance, double angle) {
		this.idx = idx;
		this.angle = angle;
		this.mean_dist = distance;
	}
	
	public Object(int start, int stop, float[] dist) {
		this.start = start;
		this.stop = stop;
		this.dist = dist;
	}
	
	public int idx() {
		return idx;
	}
	
	public double size() {
		return this.size;
	}
	
	public double distance() {
		return mean_dist;
	}
	
	public double angle() {
		return angle;
	}
	
	public void add_dist_values(float[] more_dist, int new_start) {
		float[] new_dist = new float[this.dist.length + more_dist.length];
		for (int i = 0; i < more_dist.length; i++)
			new_dist[i] = more_dist[i];
		for (int i = 0; i < this.dist.length; i++)
			new_dist[more_dist.length + i] = this.dist[i];
		this.dist = new_dist;
		this.start = new_start;
	}
	
	public void compute(int nb_tot) {
		this.mean_dist = 0;
		for (float mes: this.dist)
			this.mean_dist += mes;
		this.mean_dist /= this.dist.length;
		this.angle = (this.stop - this.start) * 2 * Math.PI / nb_tot;
		this.size = 2 * this.mean_dist * Math.tan(angle / 2);
	}
	
	@Override
	public int compareTo(Object y) {
		if (this.mean_dist < y.mean_dist)
			return -1;
		else if (this.mean_dist == y.mean_dist)
			return 0;
		else
			return 1;
	}

}
