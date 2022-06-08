public class Ball {
	private double radius, mass, elasticity, shininess;
	private Color color;
	private Vector position, velocity; 
	
	public Ball(Vector position, Vector velocity, double radius, double mass, double elasticity, double shininess, Color c) {
		this.position = position;
		this.velocity = velocity;
		this.radius = radius;
		this.mass = mass;
		this.elasticity = elasticity;
		this.shininess = shininess;
		this.color = new Color(c.getR(), c.getG(), c.getB(), 1);
	}
	
	public double getX() {
		return this.position.getX();
	}
	
	public double getY() {
		return this.position.getY();
	}
	
	public double getZ() {
		return this.position.getZ();
	}
	
	public double getRadius() { 
		return this.radius;
	}
	
	public Vector getPosition() {
		return this.position;
	}
	
	public void setPosition(Vector p) {
		this.position = p;
	}
	
	public Vector getVelocity() {
		return this.velocity;
	}
	
	public void setVelocity(Vector v) {
		this.velocity = v;
	}
	
	public void updatePosition(Vector a) {
		position = position.add(velocity);
		position = position.add(a.scale(0.5));
	}
	
	public void updateVelocity(Vector a) {
		velocity = velocity.add(a);
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	public double getElasticity() {
		return this.elasticity;
	}
	
	public double getShininess() {
		return this.shininess;
	}
	
}
