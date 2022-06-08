import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

/*
 * Yanni Leondaridis
 * jll8uk
 * 11/08/21
 */

public class PngTest {

	public static void main(String[] args) throws IOException {
		int l = 0;
		
		File myfile = new File(args[0]);
		Scanner mysc = new Scanner(myfile);
		if (mysc.hasNextLine()) {
			String firstline = mysc.nextLine().trim();
			String style[] = firstline.split("\\s+");
			l = Integer.parseInt(style[4]);
		}else {
			System.out.println("Malformed input");
		}
		mysc.close();
		
		int f = 0;
		
		while (f < l) {
			try {
				File file = new File(args[0]);
				Scanner sc = new Scanner(file);
				if (sc.hasNextLine()) {
					String firstline = sc.nextLine().trim();
					String style[] = firstline.split("\\s+");
					int width = Integer.parseInt(style[1]);
					int height = Integer.parseInt(style[2]);
					String base = style[3];
					

					BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					WritableRaster raster = img.getRaster();

					Png myPng = new Png(width, height);
					myPng.clearImage(raster);


					// point list, and test object, which gives access to methods below
					ArrayList<Point> points = new ArrayList<Point>();
					PngTest test = new PngTest();

					// Matrices to store model-view and projection
					Matrix mv = new Matrix();
					Matrix proj = new Matrix();

					// vertex components
					double x, y, z, w, r = -1, g = -1, b = -1, s = -1, t = -1;

					// triangle indices
					int i1, i2, i3;

					// keeping track of color string
					String c;

					// vertices as mv, proj, and viewport transformations are applied
					Point p, v1, v2, v3, v1_device, v2_device, v3_device;


					// keep track of normal and light sources
					double[] normal = { 0, 0, 0 };

					// depth buffer
					double[][] depthBuffer = new double[width][height];
					// Fill each row with 1.0
					for (double[] row : depthBuffer)
						Arrays.fill(row, 1.0);
					
					String objName = "", dest = "";
					HashMap<String, MyObject> objects = new HashMap<String, MyObject>();
					HashMap<String, Double> vars = new HashMap<String, Double>();
					vars.put("f", (double) f);
					vars.put("l", (double) l);
					double a1, a2;

					while (sc.hasNextLine()) {
						String line = sc.nextLine().trim();
						String data[] = line.split("\\s+");
						String keyword = data[0];
						switch (keyword) {
						case "object":
							objName = data[1];
							objects.put(objName, new MyObject(data[2]));
							break;
						case "position":
							x = assign(data[1], vars);
							y = assign(data[2], vars);
							z = assign(data[3], vars);
							
							Vector position = new Vector(x, y, z);
							objects.get(objName).setPosition(position);
							break;
						case "origin":
							x = assign(data[1], vars);
							y = assign(data[2], vars);
							z = assign(data[3], vars);
							
							Vector origin = new Vector(x, y, z);
							objects.get(objName).setOrigin(origin);
							break;
						case "scale":
							x = assign(data[1], vars);
							y = assign(data[2], vars);
							z = assign(data[3], vars);
							
							Vector scale = new Vector(x, y, z);
							objects.get(objName).setScale(scale);
							break;
						case "quaternion":
							w = assign(data[1], vars);
							x = assign(data[2], vars);
							y = assign(data[3], vars);
							z = assign(data[4], vars);
						
							Quaternion q = new Quaternion(w, x, y, z);
							objects.get(objName).setOrientation(q);
							break;
						case "piecewise":
							dest = data[1];
							double new_val = Double.NaN;
							for (int i = 2; i < (data.length - 2); i += 2) {
								double v_i = assign(data[i], vars);
								double t_i = assign(data[i + 1], vars);
								if (f <= t_i) {
									new_val = v_i;
									break;
								}
							}
							if (Double.isNaN(new_val)) {
								new_val = assign(data[data.length - 1], vars);
							}
							vars.put(dest, new_val);
							break;
						case "lerp":
							dest = data[1];
							new_val = Double.NaN;
							
							if (f <= assign(data[2], vars)) {
								new_val = assign(data[3], vars);
								vars.put(dest, new_val);
								break;
							} else if (f >= assign(data[data.length - 2], vars)) {
								new_val = assign(data[data.length - 1], vars);
								vars.put(dest, new_val);
								break;
							}
							for (int i = 2; i < (data.length - 3); i += 2) {
								double t_1 = assign(data[i], vars);
								double v_1 = assign(data[i + 1], vars);
								double t_2 = assign(data[i + 2], vars);
								double v_2 = assign(data[i + 3], vars);
								if ((f >= t_1) && (f <= t_2)) {
									new_val = v_1 + (f - t_1) * (v_2 - v_1) / (t_2 - t_1);
									break;
								}
							}
							
							vars.put(dest, new_val);
							
							break;
						/*
						 * Read in (x,y,z), give it w = 1 and rgb/texture coordinates if they exist,
						 * otherwise pass in default values
						 */
						case "xyz":
							x = assign(data[1], vars);
							y = assign(data[2], vars);
							z = assign(data[3], vars);
							if ((r == -1) && (g == -1) && (b == -1)) {
								r = 255;
								g = 255;
								b = 255;
							}
							if ((s == -1) && (t == -1)) {
								s = 0;
								t = 0;
							}
							// default normal is {0, 0, 0}, otherwise pass in most recent normal
							p = new Point(x, y, z, 1, r, g, b, 255, s, t, normal[0], normal[1], normal[2]);
							objects.get(objName).addPoint(p);
							break;
						/* Same as xyz, reading in w value */
						case "xyzw":
							x = Double.parseDouble(data[1]);
							y = Double.parseDouble(data[2]);
							z = Double.parseDouble(data[3]);
							w = Double.parseDouble(data[4]);
							if ((r == -1) && (g == -1) && (b == -1)) {
								r = 255;
								g = 255;
								b = 255;
							}
							if ((s == -1) && (t == -1)) {
								s = 0;
								t = 0;
							}
							p = new Point(x, y, z, w, r, g, b, 255, s, t, normal[0], normal[1], normal[2]);
							objects.get(objName).addPoint(p);
							break;
						/*
						 * read in and update current rgb, making sure to check values are in bounds
						 * [0-255]
						 */
						case "color":
							r = test.verifyColor(255 * assign(data[1], vars));
							g = test.verifyColor(255 * assign(data[2], vars));
							b = test.verifyColor(255 * assign(data[3], vars));
							break;
						/* draw flat triangle */
						case "trif":
							// get indices and corresponding vertices
							i1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							i2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							i3 = test.getIndex(Integer.parseInt(data[3]), points.size());

							v1 = objects.get(objName).getVertices().get(i1);
							v2 = objects.get(objName).getVertices().get(i2);
							v3 = objects.get(objName).getVertices().get(i3);
							
							/*
							 * if color is not provided set to white if color isn't set yet; otherwise set
							 * to current color
							 */
							if (data.length == 4) {
								if ((r == -1) && (g == -1) && (b == -1)) {
									v1.setColor("#ffffff");
									v2.setColor("#ffffff");
									v3.setColor("#ffffff");
								} else {
									v1.setR(r);
									v1.setG(g);
									v1.setB(b);

									v2.setR(r);
									v2.setG(g);
									v2.setB(b);

									v3.setR(r);
									v3.setG(g);
									v3.setB(b);
								}
							}
							// otherwise set to provided color
							else {
								c = data[4];
								v1.setColor(c);
								v2.setColor(c);
								v3.setColor(c);
							}
							
							mv = objects.get(objName).getMv();
							MyObject currentObject = objects.get(objName);
							while (!currentObject.getParent().equals("world")) {
								mv = test.matrixMultiply(objects.get(currentObject.getParent()).getMv(), mv);
								currentObject = objects.get(currentObject.getParent());
							}
							
							v1_device = test.toDevice(v1, mv, proj, width, height);
							v2_device = test.toDevice(v2, mv, proj, width, height);
							v3_device = test.toDevice(v3, mv, proj, width, height);
							
							
							test.drawTrig(v1_device, v2_device, v3_device, raster, depthBuffer);
							
							break;
						/* read and set current proj matrix */
						case "loadp":
							proj = test.readMatrix(data);
							break;
						case "add":
							dest = data[1];
							a1 = assign(data[2], vars);
							a2 = assign(data[3], vars);
							vars.put(dest, a1 + a2);
							break;	
						case "sub":
							dest = data[1];
							a1 = assign(data[2], vars);
							a2 = assign(data[3], vars);
							
							vars.put(dest, a1 - a2);
							break;	
						case "mul":
							dest = data[1];
							a1 = assign(data[2], vars);
							a2 = assign(data[3], vars);
							vars.put(dest, a1 * a2);
							break;	
						case "div":
							dest = data[1];
							a1 = assign(data[2], vars);
							a2 = assign(data[3], vars);
							vars.put(dest, a1 / a2);
							break;	
						case "pow":
							dest = data[1];
							a1 = assign(data[2], vars);
							a2 = assign(data[3], vars);
							vars.put(dest, Math.pow(a1, a2));
							break;	
						case "sin":
							dest = data[1];
							a1 = assign(data[2], vars);
							vars.put(dest, Math.sin(Math.toRadians(a1)));
							break;	
						case "cos":
							dest = data[1];
							a1 = assign(data[2], vars);
							vars.put(dest, Math.cos(Math.toRadians(a1)));
							break;
						}

					}

					String filename = base + myPng.getTag(f) + ".png";
					ImageIO.write(img, "png", new File(filename));
				} else

				{
					System.out.println("Malformed input.");
				}
				sc.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
			f++;
		
		}

		return;

	}

	/* makes sure rgb is between 0 and 255 */
	public double verifyColor(double rgb) {
		if (rgb > 255) {
			return 255;
		} else if (rgb < 0) {
			return 0;
		} else {
			return rgb;
		}
	}

	/*
	 * multiplies point by mv matrix, then proj matrix, then applies viewport
	 * transformation
	 */
	public Point toDevice(Point v, Matrix mv, Matrix proj, int width, int height) {
		Point v_view = mv.matrixVectorMultiply(v);
		Point v_proj = proj.matrixVectorMultiply(v_view);
		divideW(v_proj);
		Point v_device = viewport(v_proj, width, height);
		return v_device;
	}

	/* returns matrix based on the provided array of 16 values */
	public Matrix readMatrix(String[] data) {
		Matrix m = new Matrix();
		m.getMatrix()[0][0] = Double.parseDouble(data[1]);
		m.getMatrix()[0][1] = Double.parseDouble(data[2]);
		m.getMatrix()[0][2] = Double.parseDouble(data[3]);
		m.getMatrix()[0][3] = Double.parseDouble(data[4]);

		m.getMatrix()[1][0] = Double.parseDouble(data[5]);
		m.getMatrix()[1][1] = Double.parseDouble(data[6]);
		m.getMatrix()[1][2] = Double.parseDouble(data[7]);
		m.getMatrix()[1][3] = Double.parseDouble(data[8]);

		m.getMatrix()[2][0] = Double.parseDouble(data[9]);
		m.getMatrix()[2][1] = Double.parseDouble(data[10]);
		m.getMatrix()[2][2] = Double.parseDouble(data[11]);
		m.getMatrix()[2][3] = Double.parseDouble(data[12]);

		m.getMatrix()[3][0] = Double.parseDouble(data[13]);
		m.getMatrix()[3][1] = Double.parseDouble(data[14]);
		m.getMatrix()[3][2] = Double.parseDouble(data[15]);
		m.getMatrix()[3][3] = Double.parseDouble(data[16]);

		return m;
	}

	// Matrix multiplication (taken from hw page)
	public Matrix matrixMultiply(Matrix a, Matrix b) {
		// result is [a] * [b]
		Matrix ans = new Matrix();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				ans.getMatrix()[i][j] = 0;
				for (int k = 0; k < 4; k++) {
					ans.getMatrix()[i][j] += a.getMatrix()[i][k] * b.getMatrix()[k][j];
				}
			}
		}
		return ans;
	}

	// returns magnitude of vector
	public double mag(double[] v) {
		return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	// normalizes given vector
	public void normalize(double[] v) {
		double v_mag = mag(v);
		v[0] /= v_mag;
		v[1] /= v_mag;
		v[2] /= v_mag;
		return;
	}

	/*
	 * Returns the cross product of the two provided vectors Source:
	 * https://www.geeksforgeeks.org/program-dot-product-cross-product-two-vector/
	 */
	public double[] crossProduct(double[] vect_A, double[] vect_B) {
		double[] cross_P = new double[3];
		cross_P[0] = vect_A[1] * vect_B[2] - vect_A[2] * vect_B[1];
		cross_P[1] = vect_A[2] * vect_B[0] - vect_A[0] * vect_B[2];
		cross_P[2] = vect_A[0] * vect_B[1] - vect_A[1] * vect_B[0];
		return cross_P;
	}

	// returns the dot product of two vectors
	public double dotProduct(double[] a, double[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	// checks if point is on screen
	public boolean onScreen(int x, int y, double z, int width, int height) {
		if ((x >= width) || (x < 0)) {
			return false;
		} else if ((y >= height) || (y < 0)) {
			return false;
		} else if ((z < 0) || (z > 1)) {
			return false;
		} else {
			return true;
		}
	}

	// draws the provided list of points on the given raster
	public void drawPoints(ArrayList<Point> points, WritableRaster raster, double[][] depthBuffer) {
		PngTest test = new PngTest();
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			// round to nearest x and y
			int newX = (int) (p.getX() + 0.5);
			int newY = (int) (p.getY() + 0.5);

			// make sure x and y are within image bounds
			if (!onScreen(newX, newY, p.getZ(), raster.getWidth(), raster.getHeight())) {
				continue;
			}

			// compare against depth buffer
			if (p.getZ() > depthBuffer[newX][newY]) {
				continue;
			}

			// update depth buffer
			depthBuffer[newX][newY] = p.getZ();

			// get current pixel color
			double[] currentColor = raster.getPixel(newX, newY, (double[]) null);

			// get p's color
			double[] pColor = new double[] { p.getR(), p.getG(), p.getB(), p.getA() };

			// perform over operation to determine new color/alpha
			double[] newColor = test.over(pColor, currentColor);

			// round rgba and set pixel
			int newR = (int) (newColor[0] + 0.5);
			int newG = (int) (newColor[1] + 0.5);
			int newB = (int) (newColor[2] + 0.5);
			int newA = (int) (newColor[3] + 0.5);

			int c_0[] = new int[] { newR, newG, newB, newA };

			raster.setPixel(newX, newY, c_0);
		}
	}


	// performs over operation for pixels
	public double[] over(double[] A, double[] B) {
		/*
		 * formula for alpha_0 and c_0 taken from
		 * https://en.wikipedia.org/wiki/Alpha_compositing divide by 255 to get
		 * percentage of RGBA
		 */
		double alpha_a = A[3] / 255.0;
		double alpha_b = B[3] / 255.0;

		double red_a = A[0] / 255.0;
		double red_b = B[0] / 255.0;

		double green_a = A[1] / 255.0;
		double green_b = B[1] / 255.0;

		double blue_a = A[2] / 255.0;
		double blue_b = B[2] / 255.0;

		double alpha_0 = alpha_a + alpha_b * (1 - alpha_a);

		double alpha_a0 = alpha_a / alpha_0;
		double c_b_coeff = 1 - (alpha_a / alpha_0);

		double red_0 = (255 * ((alpha_a0 * red_a) + (c_b_coeff * red_b)));
		double green_0 = (255 * ((alpha_a0 * green_a) + (c_b_coeff * green_b)));
		double blue_0 = (255 * ((alpha_a0 * blue_a) + (c_b_coeff * blue_b)));
		double new_alpha = (255 * alpha_0);

		double[] ans = new double[] { red_0, green_0, blue_0, new_alpha };
		return ans;
	}

	// gets index of array based on input value i with array of size n
	public int getIndex(int i, int n) {
		int ans;
		if (i > 0) {
			ans = i - 1;
		} else {
			ans = n + i;
		}
		return ans;
	}

	// draws triangle using DDA scanline fill
	public void drawTrig(Point v1, Point v2, Point v3, WritableRaster raster, double[][] depthBuffer) {
		PngTest test = new PngTest();
		// define edges with DDA_trig(), which always steps in y
		ArrayList<Point> edge1 = v1.DDA_trig(v2);
		ArrayList<Point> edge2 = v1.DDA_trig(v3);
		ArrayList<Point> edge3 = v2.DDA_trig(v3);

		// add each edge and sort by y coordinate
		ArrayList<Point> trig_points = new ArrayList<Point>();
		trig_points.addAll(edge1);
		trig_points.addAll(edge2);
		trig_points.addAll(edge3);
		Collections.sort(trig_points);

		// dda horizontally between each pair of points of the same y
		for (int i = 0; i < (trig_points.size() - 1); i += 2) {
			ArrayList<Point> scanline = trig_points.get(i).DDA(trig_points.get(i + 1));
			test.drawPoints(scanline, raster, depthBuffer);
		}
		return;
	}


	// applies viewport transformation to point
	public Point viewport(Point p, double width, double height) {
		double x = p.getX();
		x += 1;
		x *= width / 2.0;

		double y = p.getY();
		y += 1;
		y *= height / 2.0;
		return new Point(x, y, p.getZ(), p.getW(), p.getR(), p.getG(), p.getB(), p.getA(), p.getS(), p.getT(),
				p.getNormal()[0], p.getNormal()[1], p.getNormal()[2]);
	}

	// normalizes point by dividing w coordinate
	public void divideW(Point p) {
		p.setX(p.getX() / p.getW());
		p.setY(p.getY() / p.getW());
		p.setZ(p.getZ() / p.getW());
		p.setW(1);
		return;
	}

	/*
	 * https://www.baeldung.com/java-check-string-number
	 */
	public static Double assign(String strNum, HashMap<String, Double> vars) {
	    if (strNum == null) {
	        return Double.NaN;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	        return d;
	    } catch (NumberFormatException nfe) {
	        return vars.get(strNum);
	    }
	}


}
