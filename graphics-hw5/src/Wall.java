
public class Wall {
	private double A, B, C, D;
	
	public Wall(double A, double B, double C, double D) {
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
	
	public double getA() {
		return this.A;
	}
	
	public double getB() {
		return this.B;
	}
	
	public double getC() {
		return this.C;
	}
	
	public double getD() {
		return this.D;
	}
	
	public Vector getPoint() {
		return new Vector(0, 0, -D/C);
	}
}
