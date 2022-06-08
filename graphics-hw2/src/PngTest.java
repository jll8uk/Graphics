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
 * 10/08/21
 */

public class PngTest {

	public static void main(String[] args) throws IOException {
		try {
			File f = new File(args[0]);
			Scanner s = new Scanner(f);
			if (s.hasNextLine()) {
				String firstline = s.nextLine().trim();
				String style[] = firstline.split("\\s+");
				int width = Integer.parseInt(style[1]);
				int height = Integer.parseInt(style[2]);
				String base = style[3];
				int frames = Integer.parseInt(style[4]);

				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
				WritableRaster raster = img.getRaster();

				Png myPng = new Png(width, height);
				myPng.clearImage(raster);
				
				int frame_num = 0;
				
				while (s.hasNextLine()) {
					String line = s.nextLine().trim();
					String data[] = line.split("\\s+");
					String keyword = data[0];
					int x, y, r, g, b;

					switch (keyword) {
					case "xy":
						x = Integer.parseInt(data[1]);
						y = Integer.parseInt(data[2]);
						raster.setPixel(x, y, new int[] { 255, 255, 255, 255 });
						break;
					case "xyrgb":
						x = Integer.parseInt(data[1]);
						y = Integer.parseInt(data[2]);
						r = Integer.parseInt(data[3]);
						g = Integer.parseInt(data[4]);
						b = Integer.parseInt(data[5]);
						raster.setPixel(x, y, new int[] { r, g, b, 255 });
						break;
					case "xyc":
						x = Integer.parseInt(data[1]);
						y = Integer.parseInt(data[2]);
						String colorStr = data[3];
						r = Integer.valueOf(colorStr.substring(1, 3), 16);
						g = Integer.valueOf(colorStr.substring(3, 5), 16);
						b = Integer.valueOf(colorStr.substring(5, 7), 16);
						raster.setPixel(x, y, new int[] { r, g, b, 255 });
						break;
					case "frame":
						int new_frame = Integer.parseInt(data[1]);
						if (new_frame != 0) {
				
							int frame_diff = new_frame - frame_num;
							
							for (int i = 0; i < frame_diff; i++) {
								String filename = base + myPng.getTag(frame_num + i) + ".png";
								ImageIO.write(img, "png", new File(filename));
							}
											
							myPng.clearImage(raster);
							frame_num = new_frame;
						

						}
						break;
					}

				}
				
				String filename = base + myPng.getTag(frame_num) + ".png";
				ImageIO.write(img, "png", new File(filename));		
			} else {
				System.out.println("Malformed input.");
			}
			s.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
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

	/* Same as drawPoints, but uses sun illumination for determining colors */
	public void illuminatePoints(ArrayList<Point> points, ArrayList<double[]> suns, WritableRaster raster,
			double[][] depthBuffer) {
		PngTest test = new PngTest();

		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			int newX = (int) (p.getX() + 0.5);
			int newY = (int) (p.getY() + 0.5);

			if (!onScreen(newX, newY, p.getZ(), raster.getWidth(), raster.getHeight())) {
				continue;
			}

			if (p.getZ() > depthBuffer[newX][newY]) {
				continue;
			}

			depthBuffer[newX][newY] = p.getZ();

			// illuminate point based on light sources
			double[] newColor = test.illuminate(p, suns);

			// round rgba and draw pixel
			int newR = (int) (newColor[0] + 0.5);
			int newG = (int) (newColor[1] + 0.5);
			int newB = (int) (newColor[2] + 0.5);
			int newA = (int) (p.getA() + 0.5);

			int c_0[] = new int[] { newR, newG, newB, newA };
			raster.setPixel(newX, newY, c_0);
		}
	}

	/* same as other drawpoints, but samples texture image for rgba */
	public void drawTexturePoints(ArrayList<Point> points, WritableRaster raster, WritableRaster textureRaster,
			double[][] depthBuffer) {

		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			int newX = (int) (p.getX() + 0.5);
			int newY = (int) (p.getY() + 0.5);

			if (!onScreen(newX, newY, p.getZ(), raster.getWidth(), raster.getHeight())) {
				continue;
			}
			if (p.getZ() > depthBuffer[newX][newY]) {
				continue;
			}

			depthBuffer[newX][newY] = p.getZ();

			// texture image width and height
			int tWidth = textureRaster.getWidth();
			int tHeight = textureRaster.getHeight();

			/*
			 * Wraps texture coordinate between 0 and 1 Source:
			 * https://stackoverflow.com/questions/14415753/wrap-value-into-range-min-max-
			 * without-division
			 */
			p.setS((((p.getS()) % (1)) + (1)) % (1));
			p.setT((((p.getT()) % (1)) + (1)) % (1));

			// scale texture coordinates up to specific image, and round to nearest texel
			// then use that texel's rgba for the point being drawn
			double sw = p.getS() * tWidth;
			double th = p.getT() * tHeight;

			int newS = (int) (sw + 0.5);
			int newT = (int) (th + 0.5);
			int[] textureColor = textureRaster.getPixel(newS, newT, (int[]) null);

			// set alpha to be 255 by default
			int c_0[] = new int[] { textureColor[0], textureColor[1], textureColor[2], 255 };
			raster.setPixel(newX, newY, c_0);
		}
	}

	/*
	 * same as texture draw, but performs over operation with texture rgba and
	 * triangle rgba
	 */
	public void drawTextureDecalPoints(ArrayList<Point> points, WritableRaster raster, WritableRaster textureRaster,
			double[][] depthBuffer) {
		PngTest test = new PngTest();
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			int newX = (int) (p.getX() + 0.5);
			int newY = (int) (p.getY() + 0.5);

			if (!onScreen(newX, newY, p.getZ(), raster.getWidth(), raster.getHeight())) {
				continue;
			}

			if (p.getZ() > depthBuffer[newX][newY]) {
				continue;
			}

			depthBuffer[newX][newY] = p.getZ();

			int tWidth = textureRaster.getWidth();
			int tHeight = textureRaster.getHeight();

			p.setS((((p.getS()) % (1)) + (1)) % (1));
			p.setT((((p.getT()) % (1)) + (1)) % (1));

			double sw = p.getS() * tWidth;
			double th = p.getT() * tHeight;

			int newS = (int) (sw + 0.5);
			int newT = (int) (th + 0.5);

			double[] textC = textureRaster.getPixel(newS, newT, (double[]) null);
			double current[] = new double[] { p.getR(), p.getG(), p.getB(), p.getA() };

			// lay texture color over current triangle color
			double[] newColor = test.over(textC, current);

			// round rgba and draw pixel
			int c_0[] = new int[] { (int) (newColor[0] + 0.5), (int) (newColor[1] + 0.5), (int) (newColor[2] + 0.5),
					(int) (newColor[3] + 0.5) };
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

	// same as drawTrig, but uses illuminatePoints method to draw pixels
	public void illuminateTrig(Point v1, Point v2, Point v3, ArrayList<double[]> suns, WritableRaster raster,
			double[][] depthBuffer) {
		PngTest test = new PngTest();
		ArrayList<Point> edge1 = v1.DDA_trig(v2);
		ArrayList<Point> edge2 = v1.DDA_trig(v3);
		ArrayList<Point> edge3 = v2.DDA_trig(v3);

		ArrayList<Point> trig_points = new ArrayList<Point>();
		trig_points.addAll(edge1);
		trig_points.addAll(edge2);
		trig_points.addAll(edge3);
		Collections.sort(trig_points);

		for (int i = 0; i < (trig_points.size() - 1); i += 2) {
			ArrayList<Point> scanline = trig_points.get(i).DDA(trig_points.get(i + 1));
			test.illuminatePoints(scanline, suns, raster, depthBuffer);
		}
		return;
	}

	// same as drawTrig, but uses drawTexturePoints method to draw pixels
	public void drawTrit(Point v1, Point v2, Point v3, WritableRaster raster, WritableRaster textureRaster,
			double[][] depthBuffer) {
		PngTest test = new PngTest();

		ArrayList<Point> edge1 = v1.DDA_trit(v2);
		ArrayList<Point> edge2 = v1.DDA_trit(v3);
		ArrayList<Point> edge3 = v2.DDA_trit(v3);

		ArrayList<Point> trig_points = new ArrayList<Point>();
		trig_points.addAll(edge1);
		trig_points.addAll(edge2);
		trig_points.addAll(edge3);
		Collections.sort(trig_points);

		for (int i = 0; i < (trig_points.size() - 1); i += 2) {
			ArrayList<Point> scanline = trig_points.get(i).DDA_t(trig_points.get(i + 1));
			test.drawTexturePoints(scanline, raster, textureRaster, depthBuffer);
		}
		return;
	}

	// same as drawTrig, but uses drawTextureDecalPoints method to draw pixels
	public void drawTritDecal(Point v1, Point v2, Point v3, WritableRaster raster, WritableRaster textureRaster,
			double[][] depthBuffer) {
		PngTest test = new PngTest();

		ArrayList<Point> edge1 = v1.DDA_trit(v2);
		ArrayList<Point> edge2 = v1.DDA_trit(v3);
		ArrayList<Point> edge3 = v2.DDA_trit(v3);

		ArrayList<Point> trig_points = new ArrayList<Point>();
		trig_points.addAll(edge1);
		trig_points.addAll(edge2);
		trig_points.addAll(edge3);
		Collections.sort(trig_points);

		for (int i = 0; i < (trig_points.size() - 1); i += 2) {
			ArrayList<Point> scanline = trig_points.get(i).DDA_t(trig_points.get(i + 1));
			test.drawTextureDecalPoints(scanline, raster, textureRaster, depthBuffer);
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
	 * returns frustum transformation Source:
	 * https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glFrustum.xml
	 */
	public Matrix frustum(double l, double r, double b, double t, double n, double f) {
		double A = (r + l) / (r - l);
		double B = (t + b) / (t - b);
		double C = -((f + n) / (f - n));
		double D = (-2 * f * n) / (f - n);
		Matrix ans = new Matrix();
		ans.getMatrix()[0][0] = (2 * n) / (r - l);
		ans.getMatrix()[0][2] = A;
		ans.getMatrix()[1][1] = (2 * n) / (t - b);
		ans.getMatrix()[1][2] = B;
		ans.getMatrix()[2][2] = C;
		ans.getMatrix()[2][3] = D;
		ans.getMatrix()[3][2] = -1;
		ans.getMatrix()[3][3] = 0;
		return ans;
	}

	/*
	 * returns ortho transformation Source:
	 * https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glOrtho.xml
	 */
	public Matrix ortho(double l, double r, double b, double t, double n, double f) {
		// correction for near value based on writeup
		n = (2 * n) - f;
		Matrix ans = new Matrix();
		ans.getMatrix()[0][0] = 2 / (r - l);
		ans.getMatrix()[0][3] = -((r + l) / (r - l));
		ans.getMatrix()[1][1] = (2) / (t - b);
		ans.getMatrix()[1][3] = -((t + b) / (t - b));
		ans.getMatrix()[2][2] = (-2.0) / (f - n);
		ans.getMatrix()[2][3] = -((f + n) / (f - n));
		return ans;
	}

	/*
	 * returns lookAt transformation Source:
	 * https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/gluLookAt.xml
	 */
	public Matrix lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ,
			double upX, double upY, double upZ) {
		double[] F = { centerX - eyeX, centerY - eyeY, centerZ - eyeZ };
		normalize(F);

		double[] up = { upX, upY, upZ };
		normalize(up);

		double[] s = crossProduct(F, up);
		normalize(s);

		double[] u = crossProduct(s, F);

		Matrix M = new Matrix();
		M.getMatrix()[0][0] = s[0];
		M.getMatrix()[0][1] = s[1];
		M.getMatrix()[0][2] = s[2];

		M.getMatrix()[1][0] = u[0];
		M.getMatrix()[1][1] = u[1];
		M.getMatrix()[1][2] = u[2];

		M.getMatrix()[2][0] = -F[0];
		M.getMatrix()[2][1] = -F[1];
		M.getMatrix()[2][2] = -F[2];

		return M;
	}

	// sums up contribution of light sources according to Lambert's law
	public double[] illuminate(Point p, ArrayList<double[]> suns) {
		double[] ans = { 0, 0, 0 };
		double[] n = p.getNormal();
		normalize(n);
		double lR, lG, lB;
		double[] pcolor = new double[] { p.getR() / 255.0, p.getG() / 255.0, p.getB() / 255.0 };

		for (int i = 0; i < suns.size(); i++) {
			// cos is dot product with normalized vectors
			double cos = dotProduct(n, suns.get(i));
			// don't illuminate when light source is behind object
			if (cos < 0) {
				continue;
			}

			// sum up contributions of light sources using component-wise multiplication
			lR = suns.get(i)[3];
			lG = suns.get(i)[4];
			lB = suns.get(i)[5];

			ans[0] += cos * pcolor[0] * lR;
			ans[1] += cos * pcolor[1] * lG;
			ans[2] += cos * pcolor[2] * lB;
		}

		// convert color back to value between [0-255]
		ans[0] *= 255;
		ans[1] *= 255;
		ans[2] *= 255;
		return ans;
	}
}
