public class Ray {
	private Vector origin, direction;
	private double r;
	
	public Ray(int x, int y, int width, int height, Vector forward, Vector right, Vector up, Vector eye, boolean fisheye) {
		double sx = ((2.0 * x) - width) / Math.max(width, height);
		double sy = (height - (2.0 * y)) / Math.max(width, height);
		// do fisheye calculation for origin and direction if appropriate
		if (fisheye) {
			double f = forward.mag();
			sx /= f;
			sy /= f;
			forward.normalize();
			double r_sq = sx * sx + sy * sy;
			this.r = Math.sqrt(r_sq);
			this.origin = new Vector(eye.getX(), eye.getY(), eye.getZ());
			Vector sx_right = right.scale(sx);
			Vector sy_up = up.scale(sy);
			this.direction = forward.scale(Math.sqrt(1 - r_sq)).add(sx_right.add(sy_up));
			this.direction.normalize();		
		} else {
			this.origin = new Vector(eye.getX(), eye.getY(), eye.getZ());
			Vector sx_right = right.scale(sx);
			Vector sy_up = up.scale(sy);
			this.direction = forward.add(sx_right.add(sy_up));
			this.direction.normalize();
			this.r = 0;
		}	
	}
	
	
	public Ray(Vector origin, Vector direction) {
		this.origin = origin;
		this.direction = direction;
		this.r = 0;
	}
	public String toString() {
		String out = "Origin: (" + this.origin.getX() + ", " + this.origin.getY() + ", " + this.origin.getZ() + ")\n";
		out += "Direction: (" + this.direction.getX() + ", " + this.direction.getY() + ", " + this.direction.getZ() + ")\n\n";
		return out;
	}
	
	public Vector getOrigin() {
		return this.origin;
	}
	
	public Vector getDirection() {
		return this.direction;
	}
	
	public double getR() {
		return this.r;
	}
}
