
public class Matrix {
	private double[][] matrix = new double[4][4];

	// default identity matrix constructor
	public Matrix() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.matrix[i][j] = 0;
			}
		}
		// set diagonal elements to be 1
		this.matrix[0][0] = 1;
		this.matrix[1][1] = 1;
		this.matrix[2][2] = 1;
		this.matrix[3][3] = 1;
	}

	// constructor with matrix passed in as parameter
	public Matrix(double[][] m) {
		this.matrix = m;
	}

	// matrix getter method
	public double[][] getMatrix() {
		return this.matrix;
	}

	// copies matrix
	public void copyMatrix(Matrix m) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.matrix[i][j] = m.getMatrix()[i][j];
			}
		}
	}
	
	// clears matrix back to identity
	public void clearMatrix() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.matrix[i][j] = 0;
			}
		}
		// set diagonal elements to be 1
		this.matrix[0][0] = 1;
		this.matrix[1][1] = 1;
		this.matrix[2][2] = 1;
		this.matrix[3][3] = 1;
	}

	// scaling matrix
	public void scaleMatrix(double sx, double sy, double sz) {
		this.clearMatrix();
		// set x, y, and z diagonal elements to be scaling factors sx, sy, and sz
		this.matrix[0][0] = sx;
		this.matrix[1][1] = sy;
		this.matrix[2][2] = sz;
	}

	// translation matrix
	public void translateMatrix(double dx, double dy, double dz) {
		this.clearMatrix();
		this.matrix[0][3] = dx;
		this.matrix[1][3] = dy;
		this.matrix[2][3] = dz;
	}
	
	// rotation around X matrix
	public void rotateXMatrix(double theta) {
		this.clearMatrix();
		// calculate cos and sin
		double cos = Math.cos(Math.toRadians(theta));
		double sin = Math.sin(Math.toRadians(theta));
		// set rotation matrix elements
		this.matrix[1][1] = cos;
		this.matrix[1][2] = (-1) * sin;
		this.matrix[2][1] = sin;
		this.matrix[2][2] = cos;
	}
	
	// rotation around Y matrix
	public void rotateYMatrix(double theta) {
		this.clearMatrix();
		// calculate cos and sin
		double cos = Math.cos(Math.toRadians(theta));
		double sin = Math.sin(Math.toRadians(theta));
		// set rotation matrix elements
		this.matrix[0][0] = cos;
		this.matrix[2][0] = (-1) * sin;
		this.matrix[0][2] = sin;
		this.matrix[2][2] = cos;
	}
	
	// rotation around Z matrix
	public void rotateZMatrix(double theta) {
		this.clearMatrix();
		// calculate cos and sin
		double cos = Math.cos(Math.toRadians(theta));
		double sin = Math.sin(Math.toRadians(theta));
		// set rotation matrix elements
		this.matrix[0][0] = cos;
		this.matrix[0][1] = (-1) * sin;
		this.matrix[1][0] = sin;
		this.matrix[1][1] = cos;
	}
	
	/*
	 * Generic rotation matrix
	 * Source: https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glRotate.xml
	 */
	public void rotation(double theta, double x, double y, double z) {
		double mag = Math.sqrt(x*x + y*y + z*z);
		x /= mag;
		y /= mag;
		z /= mag;
		theta = Math.toRadians(theta);
		double c = Math.cos(theta);
		double s = Math.sin(theta);
				
		this.clearMatrix();
		double[][] R = {
				{x * x * (1 - c) + c, x * y * (1 - c) - (z * s), x * z * (1 - c) + (y * s), 0},
				{y * x * (1 - c) + (z * s),  y * y * (1 - c) + c, y * z * (1 - c) - (x * s), 0},
				{x * z * (1 - c) - (y * s), y * z * (1 - c) + (x * s), z * z * (1 - c) + c, 0},
				{0, 0, 0, 1}						
		};
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.matrix[i][j] = R[i][j];
			}
		}
		
	}
	
	public Matrix transpose() {
		
		double[][] m_arr = new double[4][4];
		double[][] curr_arr = this.matrix;
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j< 3; j++) {
				m_arr[i][j] = curr_arr[j][i];
			}
		}
		
		Matrix m = new Matrix(m_arr);
		return m;
		
	}
	


	// matrix vector multiply
	public Point matrixVectorMultiply(Point v) {
		double[] vec = { v.getX(), v.getY(), v.getZ(), v.getW() };
		double[] ans = { 0, 0, 0, 0 };

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				ans[i] += this.getMatrix()[i][j] * vec[j];
			}
		}
		
		Point p = new Point(ans[0], ans[1], ans[2], ans[3], v.getR(), v.getG(), v.getB(), v.getA(), v.getS(), v.getT(), v.getNormal()[0], v.getNormal()[1], v.getNormal()[2]);
		return p;
	}

	
	// print matrix for debugging
	public void print() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(this.matrix[i][j] + "   ");
			}
			System.out.println();
			System.out.println();
		}
		System.out.println("\n\n\n");
	}

	public boolean equals(Object o) {

		// If the object is compared with itself then return true
		if (o == this) {
			return true;
		}

		if (!(o instanceof Matrix)) {
			return false;
		}

		// typecast o to Complex so that we can compare data members
		Matrix m = (Matrix) o;

		// Compare the data members and return accordingly
		boolean ans = true;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (this.matrix[i][j] != m.getMatrix()[i][j]) {
					ans = false;
					break;
				}
				if (!ans) {
					break;
				}
			}
		}
		return ans;

	}
}
