public class Vector {
	private double x, y, z;
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public double mag() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	public double mag_sq() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	// normalizes given vector
	public void normalize() {
		double v_mag = this.mag();
		this.x /= v_mag;
		this.y /= v_mag;
		this.z /= v_mag;
		return;
	}

	/* Returns the cross product of the two provided vectors
	 * Source: https://www.geeksforgeeks.org/program-dot-product-cross-product-two-vector/
	 */
	public Vector crossProduct(Vector v) {
		double x = this.y * v.z - this.z * v.y;
		double y = this.z * v.x - this.x * v.z;
		double z = this.x * v.y - this.y * v.x;
		return new Vector(x, y, z);
	}

	// returns the dot product of two vectors
	public double dotProduct(Vector v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	// component subtraction
	public Vector subtract(Vector v) {
		return new Vector(this.x - v.x, this.y - v.y, this.z - v.z);
	}
	
	// component addition
	public Vector add(Vector v) {
		return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	
	// scalar multiplication
	public Vector scale(double c) {
		return new Vector(this.x * c, this.y * c, this.z * c);
	}
	
	// component-wise multiplication
	public Vector componentMultiply(Vector v) {
		return new Vector (this.x * v.x, this.y * v.y, this.z * v.z);
	}
	
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z  + ")";
	}
	
}
