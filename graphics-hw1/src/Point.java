import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.math.*;

public class Point implements Comparable<Point> {
	private double x, y, z, w;
	private double r, g, b, a;

	public Point(double x, double y, double r, double g, double b, double a) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Point(double x, double y, double z, double w, double r, double g, double b, double a) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Point(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.r = 0;
		this.g = 0;
		this.b = 0;
		this.a = 0;
	}
	
	public Point(double x, double y, String c) {
		this.x = x;
		this.y = y;
		double r = Integer.valueOf(c.substring(1, 3), 16);
		int g = Integer.valueOf(c.substring(3, 5), 16);
		int b = Integer.valueOf(c.substring(5, 7), 16);
		//if provided alpha in hex string, set it accordingly
		int a;
		if (c.length() == 9) {
			a = Integer.valueOf(c.substring(7,9), 16);
		}else {
			a = 255;
		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public double getX() {
		return this.x;
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

	public double getA() {
		return this.a;
	}

	
	public String getColor() {
		return "(" + (this.getR() / 2.550) + "%R, " + (this.getG() / 2.550) + "%G, " + (this.getB() / 2.550) + "%B, " + (this.getA() / 2.550) + "%A)";
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public double getW() {
		return this.w;
	}

	public void setX(double x) {
		this.x = x;
		return;
	}

	public void setY(double y) {
		this.y = y;
		return;
	}

	public void setZ(double z) {
		this.z = z;
		return;
	}
	
	public void setW(double w) {
		this.w = w;
		return;
	}
	
	public void setR(double r) {
		this.r = r;
		return;
	}

	public void setG(double g) {
		this.g = g;
		return;
	}

	public void setB(double b) {
		this.b = b;
		return;
	}

	public void setA(double a) {
		this.a = a;
		return;
	}
	
		

	public void setColor(String c) {
		//set color, handle alpha if provided
		int r = Integer.valueOf(c.substring(1, 3), 16);
		int g = Integer.valueOf(c.substring(3, 5), 16);
		int b = Integer.valueOf(c.substring(5, 7), 16);
		int a;
		if (c.length() == 9) {
			a = Integer.valueOf(c.substring(7,9), 16);
		}else {
			a = 255;
		}
	
		this.setR(r);
		this.setG(g);
		this.setB(b);
		this.setA(a);
	}
	

	public Point subtractPoints(Point p2) {
		//vector subtract
		Point p = new Point(this.getX() - p2.getX(), this.getY() - p2.getY(), this.getZ() - p2.getZ(), this.getW() - p2.getW(), this.getR() - p2.getR(),
				this.getG() - p2.getG(), this.getB() - p2.getB(), this.getA() - p2.getA());
		return p;
	}

	public Point addPoints(Point p2) {
		//vector add
		double newR, newG, newB, newA;
		double r = this.getR() + p2.getR();
		double g = this.getG() + p2.getG();
		double b = this.getB() + p2.getB();
		double a = this.getA() + p2.getA();
		
		//handle overflows or underflows
		if (r < 0) {
			newR = 0;
		}else if (r > 255) {
			newR = 255;
		}else {
			newR = r;
		}
		
		
		if (g < 0) {
			newG = 0;
		}else if (g > 255) {
			newG = 255;
		}else {
			newG = g;
		}
		
		if (b < 0) {
			newB = 0;
		}else if (b > 255) {
			newB = 255;
		}else {
			newB = b;
		}
		
		if (a < 0) {
			newA = 0;
		}else if (a > 255) {
			newA = 255;
		}else {
			newA = a;
		}
		
		
		Point p = new Point(this.getX() + p2.getX(), this.getY() + p2.getY(), this.getZ() + p2.getZ(), this.getW() + p2.getW(), newR, 
				newG, newB, newA);
		return p;
	}

	public Point scalePoint(double c) {
		//scalar multiply
		Point p_prime = new Point(this.getX() * c, this.getY() * c, this.getZ() * c, this.getW() * c, this.getR() * c, this.getG() * c, this.getB() * c, this.getA() * c);
		return p_prime;
	}

	public String toString() {
		String out = "(" + this.x + ", " + this.y + ", " + this.r + ", " + this.g + ", " + this.b + ", " + this.a + ")";
		return out;
	}

	public ArrayList<Point> DDA(Point q) {
		// list of final points to fill
		ArrayList<Point> line = new ArrayList<Point>();

		// calculate dx and dy
		double dx = Math.abs(this.getX() - q.getX());
		double dy = Math.abs(this.getY() - q.getY());

		if (dx > dy) {
			// step in x

			// define p1 and p2 so p1 is to the left of p2
			Point p1, p2;
			if (this.getX() < q.getX()) {
				p1 = this;
				p2 = q;
			} else {
				p1 = q;
				p2 = this;
			}

			// calculate Delta
			Point Delta = p2.subtractPoints(p1);
			double c = (Math.ceil(p1.getX()) - p1.getX()) / dx;
			Point p = p1.addPoints(Delta.scalePoint(c));
			Point d = Delta.scalePoint(1 / dx);

			while (p.getX() < p2.getX()) {
				line.add(p);
				p = p.addPoints(d);
			}
		} else {
			// step in y

			// define p1 and p2 so p1 has smaller y
			Point p1, p2;
			if (this.getY() < q.getY()) {
				p1 = this;
				p2 = q;
			} else {
				p1 = q;
				p2 = this;
			}

			// calculate Delta
			Point Delta = p2.subtractPoints(p1);
			double c = (Math.ceil(p1.getY()) - p1.getY()) / dy;
			Point p = p1.addPoints(Delta.scalePoint(c));
			Point d = Delta.scalePoint(1 / dy);

			while (p.getY() < p2.getY()) {
				line.add(p);
				p = p.addPoints(d);
			}

		}

		return line;
	}

	public ArrayList<Point> DDA_trig(Point q) {

		// list of final points to fill
		ArrayList<Point> line = new ArrayList<Point>();

		// calculate dx and dy
		double dx = Math.abs(this.getX() - q.getX());
		double dy = Math.abs(this.getY() - q.getY());

		// step in y

		// define p1 and p2 so p1 has smaller y
		Point p1, p2;
		if (this.getY() < q.getY()) {
			p1 = this;
			p2 = q;
		} else {
			p1 = q;
			p2 = this;
		}

		// calculate Delta
		Point Delta = p2.subtractPoints(p1);

		double c = (Math.ceil(p1.getY()) - p1.getY()) / dy;

		Point p = p1.addPoints(Delta.scalePoint(c));

		Point d = Delta.scalePoint(1 / dy);

		while (p.getY() < p2.getY()) {
			line.add(p);
			p = p.addPoints(d);
		}

		return line;

	}
	


	@Override
	public int compareTo(Point p) {

		double d = this.getY() - p.getY();
		// For Ascending order
		if (d < 0) {
			return -1;
		} else if (d > 0) {
			return 1;
		} else {
			return 0;
		}

	}

	@Override
	public boolean equals(Object o) {

		// If the object is compared with itself then return true
		if (o == this) {
			return true;
		}

		if (!(o instanceof Point)) {
			return false;
		}

		// typecast o to Complex so that we can compare data members
		Point p = (Point) o;

		// Compare the data members and return accordingly
		return Double.compare(this.getX(), p.getX()) == 0 && Double.compare(this.getY(), p.getY()) == 0;
	}
}
