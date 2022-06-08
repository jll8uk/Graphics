import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import javax.imageio.ImageIO;

/*
 * Yanni Leondaridis
 * jll8uk
 * 09/24/21
 */

public class PngTest {

	public static void main(String[] args) throws IOException {

		try {
			File f = new File(args[0]);
			Scanner s = new Scanner(f);

			if (s.hasNextLine()) {
				String firstline = s.nextLine().trim();
				String style[] = firstline.split("\\s+");
				if (style[0].equals("png")) {
					int width = Integer.parseInt(style[1]);
					int height = Integer.parseInt(style[2]);

					String filename = style[3];
					BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					WritableRaster raster = img.getRaster();

					Png myPng = new Png(width, height);
					myPng.clearImage(raster);

					ArrayList<Point> points = new ArrayList<Point>();
					PngTest test = new PngTest();

					while (s.hasNextLine()) {
						String line = s.nextLine().trim();
						String data[] = line.split("\\s+");
						String keyword = data[0];
						double x = 0, y = 0;
						double r = 0, g = 0, b = 0, a = 0;
						Point p = new Point(x, y, r, g, b, a);

						switch (keyword) {
						case "xyc":
							x = Double.parseDouble(data[1]);
							y = Double.parseDouble(data[2]);
							String colorStr = data[3];
							p.setX(x);
							p.setY(y);
							p.setColor(colorStr);
							points.add(p);
							break;
						case "xyrgb":
							x = Double.parseDouble(data[1]);
							y = Double.parseDouble(data[2]);
							r = Double.parseDouble(data[3]);
							g = Double.parseDouble(data[4]);
							b = Double.parseDouble(data[5]);
							a = 255;
							p.setX(x);
							p.setY(y);
							p.setR(r);
							p.setG(g);
							p.setB(b);
							p.setA(a);
							points.add(p);
							break;
						case "xyrgba":
							x = Double.parseDouble(data[1]);
							y = Double.parseDouble(data[2]);
							r = Double.parseDouble(data[3]);
							g = Double.parseDouble(data[4]);
							b = Double.parseDouble(data[5]);
							a = Double.parseDouble(data[6]);
							p.setX(x);
							p.setY(y);
							p.setR(r);
							p.setG(g);
							p.setB(b);
							p.setA(a);
							points.add(p);
							break;
						case "linec":
							//get points and call DDA after setting endpoints color
							int i1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int i2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							Point p1 = points.get(i1), p2 = points.get(i2);
							String c = data[3];
							p1.setColor(c);
							p2.setColor(c);
							ArrayList<Point> dda_line = p1.DDA(p2);

							test.drawPoints(dda_line, raster);
							break;

						case "trig":
							//call drawTrig() to run DDA between each pair of vertices
							int v1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int v2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							int v3 = test.getIndex(Integer.parseInt(data[3]), points.size());
							Point vertex1 = points.get(v1), vertex2 = points.get(v2), vertex3 = points.get(v3);
							test.drawTrig(vertex1, vertex2, vertex3, raster);
							break;
							
						case "lineg":
							//DDA without setting color will automatically linearly interpolate color
							int ii1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int ii2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							Point pt1 = points.get(ii1), pt2 = points.get(ii2);
							ArrayList<Point> dda_lineg = pt1.DDA(pt2);
							test.drawPoints(dda_lineg, raster);
							break;
							
						case "tric":
							//set each vertex color, then call drawTrig()
							int vv1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int vv2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							int vv3 = test.getIndex(Integer.parseInt(data[3]), points.size());
							Point vertex_1 = points.get(vv1), vertex_2 = points.get(vv2), vertex_3 = points.get(vv3);

							
							String color = data[4];
							vertex_1.setColor(color);
							vertex_2.setColor(color);
							vertex_3.setColor(color);
							

							test.drawTrig(vertex_1, vertex_2, vertex_3, raster);
							break;
						

						case "lineca":
							//set color/alpha of each endpoint, then draw with DDA
							int ind1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int ind2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							Point endpoint1 = points.get(ind1), endpoint2 = points.get(ind2);
							String ca = data[3];
							endpoint1.setColor(ca);
							endpoint2.setColor(ca);
							ArrayList<Point> lineca = endpoint1.DDA(endpoint2);

							test.drawPoints(lineca, raster);
							break;
						case "trica":
							//set color/alpha of each vertex, then draw with drawTrig()
							int vvv1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int vvv2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							int vvv3 = test.getIndex(Integer.parseInt(data[3]), points.size());
							Point vert_1 = points.get(vvv1), vert_2 = points.get(vvv2), vert_3 = points.get(vvv3);
		
							String tri_ca = data[4];
							vert_1.setColor(tri_ca);
							vert_2.setColor(tri_ca);
							vert_3.setColor(tri_ca);

							test.drawTrig(vert_1, vert_2, vert_3, raster);
							
						case "fann":
							test.drawFann(points, data, raster);
							break;
						case "stripn":
							test.drawStripn(points, data, raster);
							break;
						case "cubicc":
							// generic algorithm taken from https://www.cubic.org/docs/bezier.htm
							int b1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int b2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							int b3 = test.getIndex(Integer.parseInt(data[3]), points.size());
							int b4 = test.getIndex(Integer.parseInt(data[4]), points.size());
							
							String bezColor = data[5];
							
							
							Point bez1 = points.get(b1), bez2 = points.get(b2), bez3 = points.get(b3), bez4 = points.get(b4);
							//set each pixel's color
							bez1.setColor(bezColor);
							bez2.setColor(bezColor);
							bez3.setColor(bezColor);
							bez4.setColor(bezColor);
							
							ArrayList<Point> bezierCurve = new ArrayList<Point>();
							
							//loop and find points on bezier curve with 1000 iterations
							for (int i = 0; i < 10000; i++) {
								double t = i / 9999.0;
								Point bez_point = test.bezierPoint(bez1, bez2, bez3, bez4, t);
								bezierCurve.add(bez_point);
							}
							
							test.drawPoints(bezierCurve, raster);
							
							break;
						case "cubicg":
							//linear interpolate function within bezierGCurve() function handles linearly
							//interpolating colors
							int bg1 = test.getIndex(Integer.parseInt(data[1]), points.size());
							int bg2 = test.getIndex(Integer.parseInt(data[2]), points.size());
							int bg3 = test.getIndex(Integer.parseInt(data[3]), points.size());
							int bg4 = test.getIndex(Integer.parseInt(data[4]), points.size());
							

							Point bez_1 = points.get(bg1), bez_2 = points.get(bg2), bez_3 = points.get(bg3), bez_4 = points.get(bg4);
						
							
							ArrayList<Point> bezierGCurve = new ArrayList<Point>();
							
							for (int i = 0; i < 10000; i++) {
								double t = i / 9999.0;
								Point bez_point = test.bezierPoint(bez_1, bez_2, bez_3, bez_4, t);
								bezierGCurve.add(bez_point);
							}
							
							test.drawPoints(bezierGCurve, raster);
							
							break;
						default:
							break;

						
						}
					}
	
					ImageIO.write(img, "png", new File(filename));

				} else {
					System.out.println("Malformed input.");
				}
			}

			s.close();

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return;

	}
	
	
	public void drawPoints(ArrayList<Point> points, WritableRaster raster) {
		PngTest test = new PngTest();
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			//round to nearest x and y
			int newX = (int) (p.getX() + 0.5);
			int newY = (int) (p.getY() + 0.5);
			
			//get current pixel color
			double[] currentColor = raster.getPixel(newX, newY, (double[]) null);	
			//get p's color
			double[] pColor = new double[] {p.getR(), p.getG(), p.getB(), p.getA()};	
			
			//perform over operation to determine new color/alpha 
			double[] newColor = test.over(pColor, currentColor);
			
			
			int newR = (int) (newColor[0] + 0.5);
			int newG = (int) (newColor[1] + 0.5);
			int newB = (int) (newColor[2] + 0.5);
			int newA = (int) (newColor[3] + 0.5);
			
			int c_0[] = new int[] {newR, newG, newB, newA};
	
			raster.setPixel(newX, newY, c_0);
		}
	}
	
	
	
	public double[] over(double[] A, double[] B) {
		
		//formula for alpha_0 and c_0 taken from https://en.wikipedia.org/wiki/Alpha_compositing
		
		//divide by 255 to get percentage of RGBA 
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
		
		
		double[] ans = new double[] {red_0, green_0, blue_0, new_alpha};
		return ans;
	}
	
	public int getIndex(int i, int n) {
		//gets index of array based on input value i with array of size n
		int ans;
		if (i > 0) {
			ans = i - 1;
		}else {
			ans = n + i;
		}
		
		return ans;
	}
	
	public void drawTrig(Point v1, Point v2, Point v3, WritableRaster raster) {
		
		PngTest test = new PngTest();
		//define edges with DDA_trig(), which always steps in y
		ArrayList<Point> edge1 = v1.DDA_trig(v2);		
		ArrayList<Point> edge2 = v1.DDA_trig(v3);					
		ArrayList<Point> edge3 = v2.DDA_trig(v3);	

		ArrayList<Point> trig_points = new ArrayList<Point>();
		
		//add each edge and sort by y coordinate
		trig_points.addAll(edge1);
		trig_points.addAll(edge2);
		trig_points.addAll(edge3);
		
		Collections.sort(trig_points);

		//dda horizontally between each pair of points of the same y
		for (int i = 0; i < (trig_points.size() - 1); i+=2){							
			ArrayList<Point> scanline = trig_points.get(i).DDA(trig_points.get(i+1));
			test.drawPoints(scanline, raster);
		}
		
		return;
	}
	
	public void drawFann(ArrayList<Point> points, String[] data, WritableRaster raster) {
		PngTest test = new PngTest();
		int n = Integer.parseInt(data[1]);
		int[] indices = new int[n];
		
		int ind;
		//convert inputs to array indices
		for (int j = 2; j < (n + 2); j++) {
			ind = test.getIndex(Integer.parseInt(data[j]), points.size());
			indices[j - 2] = ind;
		}
		
		Point v1 = points.get(indices[0]);
		
		//draw/shade triangles between (i1, i2, i3), (i1, i3, i4), ...
		Point v2, v3;
		for (int i = 1; i < (n - 1); i++) {
			v2 = points.get(indices[i]);
			v3 = points.get(indices[i + 1]);
			test.drawTrig(v1, v2, v3, raster);
		}
	}
	
	public void drawStripn(ArrayList<Point> points, String[] data, WritableRaster raster) {
		PngTest test = new PngTest();
		int n = Integer.parseInt(data[1]);
		int[] indices = new int[n];
		
		int ind;
		//convert inputs to array indices
		for (int j = 2; j < (n + 2); j++) {
			ind = test.getIndex(Integer.parseInt(data[j]), points.size());
			indices[j - 2] = ind;
		}
		
		
		//draw/shade triangles between (i1, i2, i3), (i2, i3, i4), ...
		Point v1, v2, v3;
		for (int i = 0; i < (n - 2); i++) {
			v1 = points.get(indices[i]);
			v2 = points.get(indices[i + 1]);
			v3 = points.get(indices[i + 2]);
			test.drawTrig(v1, v2, v3, raster);
		}
	}
	
	public Point lerp(Point a, Point b, double t) {
		//linear interpolate function, based off algorithm from https://www.cubic.org/docs/bezier.htm
		Point ab = b.subtractPoints(a);
		
		double newX = a.getX() + ab.getX() * t;
		double newY = a.getY() + ab.getY() * t;
		double newR = a.getR() + ab.getR() * t, 
			   newG = a.getG() + ab.getG() * t, 
			   newB = a.getB() + ab.getB() * t, 
			   newA = a.getA() + ab.getA() * t;
		
		return new Point(newX, newY, newR, newG, newB, newA);
	}
	
	public Point bezierPoint(Point a, Point b, Point c, Point d, double t) {
		//calculates point on bezier curve, based off algorithm from https://www.cubic.org/docs/bezier.htm
		PngTest test = new PngTest();
		Point ab, bc, cd, abbc, bccd, ans;
		ab = test.lerp(a, b, t);
		bc = test.lerp(b, c, t);
		cd = test.lerp(c, d, t);
		abbc = test.lerp(ab, bc, t);
		bccd = test.lerp(bc, cd, t);
		ans = test.lerp(abbc, bccd, t);
		return ans;
	}
}

