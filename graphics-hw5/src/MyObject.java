import java.util.ArrayList;
import java.util.Arrays;

public class MyObject {
	private String parent;
	private Vector origin, scale, position;
	private Quaternion orientation;
	private ArrayList<Point> vertices;
	private boolean isBone;
	private Point boneTip;
	private Matrix pointToMatrix;
	
	public MyObject() {
		this.parent = "world";
		this.origin = new Vector(0, 0, 0);
		this.scale = new Vector(1, 1, 1);
		this.position = new Vector(0, 0, 0);
		this.orientation = new Quaternion(1, 0, 0, 0);
		this.vertices = new ArrayList<Point>();
		this.isBone = false;
		this.boneTip = new Point(0,0,0,0);
		this.pointToMatrix = new Matrix();
	}
	
	public MyObject(String parent) {
		this.parent = parent;
		this.origin = new Vector(0, 0, 0);
		this.scale = new Vector(1, 1, 1);
		this.position = new Vector(0, 0, 0);
		this.orientation = new Quaternion(1, 0, 0, 0);
		this.vertices = new ArrayList<Point>();
		this.isBone = false;
		this.boneTip = new Point(0,0,0,0);
		this.pointToMatrix = new Matrix();
	}
	
	public String getParent() {
		return this.parent;
	}
	
	public Vector getOrigin() {
		return this.origin;
	}
	
	public Vector getScale() {
		return this.scale;
	}
	
	public Vector getPosition() {
		return this.position;
	}
	
	public Quaternion getOrientation() {
		return this.orientation;
	}
	
	public ArrayList<Point> getVertices() {
		return this.vertices;
	}
	
	public void setOrigin(Vector origin) {
		this.origin = origin;
	}
	
	public void setScale(Vector scale) {
		this.scale = scale;
	}
	
	public void setPosition(Vector position) {
		this.position = position;
	}
	
	public void setOrientation(Quaternion orientation) {
		orientation.normalize();
		this.orientation = orientation;
	}
	public void setPointTo(Matrix pointToMatrix) {
		this.pointToMatrix = pointToMatrix;
	}
	
	public Matrix getMv() {
		PngTest test = new PngTest();
		
		Matrix T_0_inv = new Matrix();
		Matrix S = new Matrix();
		Matrix R = this.orientation.getMatrix();
		Matrix T_P = new Matrix();
		Matrix T_0 = new Matrix();
		
		T_0_inv.translateMatrix(-this.origin.getX(), -this.origin.getY(), -this.origin.getZ());
		S.scaleMatrix(this.scale.getX(), this.scale.getY(), this.scale.getZ());
		T_P.translateMatrix(this.position.getX(), this.position.getY(), this.position.getZ());
		T_0.translateMatrix(this.origin.getX(), this.origin.getY(), this.origin.getZ());
	
		Matrix mv = test.matrixMultiply(T_0, test.matrixMultiply(T_P, test.matrixMultiply(this.pointToMatrix, test.matrixMultiply(R, test.matrixMultiply(S, T_0_inv)))));
		return mv;
	}
	
	public Matrix getInverseMv() {
		PngTest test = new PngTest();
		
		Matrix T_0_inv = new Matrix();
		Matrix S = new Matrix();
		Matrix R = this.orientation.inverse().getMatrix();
		Matrix T_P = new Matrix();
		Matrix T_0 = new Matrix();
		
		T_0_inv.translateMatrix(this.origin.getX(), this.origin.getY(), this.origin.getZ());
		S.scaleMatrix(1/this.scale.getX(), 1/this.scale.getY(), 1/this.scale.getZ());
		T_P.translateMatrix(-this.position.getX(), -this.position.getY(), -this.position.getZ());
		T_0.translateMatrix(-this.origin.getX(), -this.origin.getY(), -this.origin.getZ());
	
		Matrix mv = test.matrixMultiply(T_0_inv, test.matrixMultiply(S, test.matrixMultiply(R, test.matrixMultiply(this.pointToMatrix.transpose(), test.matrixMultiply(T_P, T_0)))));
		return mv;
	}
	
	
	public void addPoint(Point p) {
		this.vertices.add(p);
		return;
	}
	
	public void setBone(double d) {
		this.isBone = true;
		this.boneTip = new Point(0, 0, d, 1);
	}
	
	public Point getBoneTip() {
		return this.boneTip;
	}
	
	public boolean isBone() {
		return this.isBone;
	}
	
	public void setIsBone(boolean isbone) {
		this.isBone = isbone;
	}
}
