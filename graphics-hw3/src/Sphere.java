
public class Sphere {
	private double x, y, z, radius;
	private Color color;
	
	public Sphere(double x, double y, double z, double radius, Color c) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.color = new Color(c.getR(), c.getG(), c.getB(), 1);
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
	
	public double getRadius() { 
		return this.radius;
	}
	
	public Vector getCenter() {
		return new Vector(this.x, this.y, this.z);
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public String toString() {
		String out = "Center Point: (" + this.x + ", " + this.y + ", " + this.z + ")\nRadius: " + this.radius + "\nColor: " + this.color.toString() + "\n\n";
		return out;
	}
	
}
