
public class Color {
	private double r, g, b, a;
	
	public Color(double r, double g, double b, double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public double[] getColor() {
		return new double[] {this.r, this.g, this.b, this.a};
	}
	
	public double getR() {
		return this.r;
	}
	
	public double getG() {
		return this.g;
	}
	
	public double getB() {
		return this.b;
	}
	
	public int[] roundColor() {
		int newR = (int) (255 * this.r + 0.5);
		int newG = (int) (255 * this.g + 0.5);
		int newB = (int) (255 * this.b + 0.5);
		int newA = (int) (255 * this.a + 0.5);
		return new int[] {newR, newG, newB, newA};
	}
	
	public void setR(double r) {
		this.r = r;
	}
	
	public void setG(double g) {
		this.g = g;
	}
	
	public void setB(double b) {
		this.b = b;
	}
	
	public void setA(double a) {
		this.a = a;
	}
	
	public String toString() {
		String out = "(" + this.r + ", " + this.g + ", " + this.b + ", " + this.a + ")";
		return out;
	}
	
}
