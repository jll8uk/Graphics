public class Quaternion {
	private double w, x, y, z;
	
	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Matrix getMatrix() {
		double[][] m = new double[4][4];
		m[0][0] = (w * w)  + (x * x) - (y * y) - (z * z);
		m[0][1] = 2 * ((x * y) - (z * w));
		m[0][2] = 2 * ((x * z) + (y * w));
		m[0][3] = 0;
		m[1][0] = 2 * ((x * y) + (z * w));
		m[1][1] = (w * w)  - (x * x) + (y * y) - (z * z);
		m[1][2] = 2 * ((y * z) - (x * w));
		m[1][3] = 0;
		m[2][0] = 2 * ((x * z) - (y * w));
		m[2][1] = 2 * ((y * z) + (x * w));
		m[2][2] = (w * w)  - (x * x) - (y * y) + (z * z);
		m[2][3] = 0;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 0;
		m[3][3] = 1;
		return new Matrix(m);
	}
	
	public void normalize() {
		double m = Math.sqrt(w*w + x*x + y*y + z*z);
		this.w /= m;
		this.x /= m;
		this.y /= m;
		this.z /= m;
	}
	
	public Quaternion inverse() {
		return new Quaternion(this.w, -this.x, -this.y, -this.z);
	}
}
