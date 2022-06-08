
public class Plane {
	private double A, B, C, D;
	
	public Plane(double A, double B, double C, double D) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
	}
	
	public Vector getNormal() {
		Vector N = new Vector(A, B, C);
		N.normalize();
		return N;
	}
	
	public Vector getPoint() {
		return new Vector(0, 0, -D/C);
	}
}
