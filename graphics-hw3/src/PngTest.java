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
 * 10/28/21
 */

public class PngTest {

	public static void main(String[] args) throws IOException {

		try {
			// open file
			File f = new File(args[0]);
			Scanner sc = new Scanner(f);

			// read file
			if (sc.hasNextLine()) {
				String firstline = sc.nextLine().trim();
				String style[] = firstline.split("\\s+");
				
				// create png
				if (style[0].equals("png")) {
					// read png data and create BufferedImg
					int width = Integer.parseInt(style[1]);
					int height = Integer.parseInt(style[2]);
					String filename = style[3];
					
					BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					WritableRaster raster = img.getRaster();
					
					// create and clear png object
					Png myPng = new Png(width, height);
					myPng.clearImage(raster);

					PngTest test = new PngTest();

					// keeps track of current color (default white)
					Color c = new Color(1, 1, 1, 1);
					
					
					// eye, forward, right, and up vectors with their corresponding default values
					Vector eye = new Vector(0, 0, 0);
					Vector forward = new Vector(0, 0, -1);
					Vector right = new Vector(1, 0, 0);
					Vector up = new Vector(0, 1, 0);
					
					// list keeping track of objects in the scene (only spheres were implemented)
					ArrayList<Sphere> objects = new ArrayList<Sphere>();
					
					// list of light sources (bulbs and suns)
					ArrayList<double[]> suns = new ArrayList<double[]>();
					ArrayList<double[]> bulbs = new ArrayList<double[]>();
					
					// variables to keep track of x, y, z, and exposure level v
					double x, y, z;	
					double v = 0;
					boolean exposed = false;
					
					// keeps track of if fisheye parameter is enabled
					boolean fisheye = false;
					
					/* Read File */
					while (sc.hasNextLine()) {
						// get keyword, and perform corresponding action based on switch statements
						String line = sc.nextLine().trim();
						String data[] = line.split("\\s+");
						String keyword = data[0];

						switch (keyword) {
						/*
						 * create sphere with the appropriate radius, center (cx, cy, cz), and current color and
						 * add it to the objects list
						 */
						case "sphere":		
							Sphere s = new Sphere(Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]), c);
							objects.add(s);
							break;
						// add a sun light source to the suns list with the current color
						case "sun":
							x = Double.parseDouble(data[1]);
							y = Double.parseDouble(data[2]);
							z = Double.parseDouble(data[3]);	
							double[] sun = {x, y, z, c.getR(), c.getG(), c.getB()};	
							suns.add(sun);
							break;
						// add a bulb light source to the bulbs list with the current color
						case "bulb":
							x = Double.parseDouble(data[1]);
							y = Double.parseDouble(data[2]);
							z = Double.parseDouble(data[3]);	
							double[] bulb = {x, y, z, c.getR(), c.getG(), c.getB()};
							bulbs.add(bulb);
							break;
						// update the current color variable with the given r, g, and b components
						case "color":
							double r = Double.parseDouble(data[1]);
							double g = Double.parseDouble(data[2]);
							double b = Double.parseDouble(data[3]);
							c.setR(r);
							c.setG(g);
							c.setB(b);
							break;
						// sets the exposure level to the given value
						case "expose":
							v = Double.parseDouble(data[1]);
							exposed = true;
							break;
						// updates the eye vector based on the given values
						case "eye":
							double eye_x = Double.parseDouble(data[1]);
							double eye_y = Double.parseDouble(data[2]);
							double eye_z = Double.parseDouble(data[3]);
							eye = new Vector(eye_x, eye_y, eye_z);
							break;	
						/*
						 * overwrites the current forward vector and updates the up and right vectors by:
						 * up => cross the new forward with up, and cross that result with forward again, then normalize
						 * right => cross product of new forward and up vectors
						 */
						case "forward":
							double forward_x = Double.parseDouble(data[1]);
							double forward_y = Double.parseDouble(data[2]);
							double forward_z = Double.parseDouble(data[3]);
							// update forward
							forward = new Vector(forward_x, forward_y, forward_z);
							// get new up vector orthogonal to forward
							Vector p = forward.crossProduct(up);
							up = p.crossProduct(forward);
							up.normalize();
							
							//get new right vector mutually orthogonal to forward and up
							right = forward.crossProduct(up);
							right.normalize();
							break;
						/*
						 * overwrites the current up vector and updates the right vector by:
						 * up => cross forward with the new up, and cross that result with forward again, then normalize
						 * right => cross product of forward and the new up vector
						 */
						case "up":
							double up_x = Double.parseDouble(data[1]);
							double up_y = Double.parseDouble(data[2]);
							double up_z = Double.parseDouble(data[3]);
							Vector up_prime = new Vector(up_x, up_y, up_z);
							
							p = forward.crossProduct(up_prime);
							up = p.crossProduct(forward);
							up.normalize();
							
							right = forward.crossProduct(up);
							right.normalize();
							break;
						// set fisheye parameter to true
						case "fisheye":
							fisheye = true;
							break;
						default:
							break;
						}
					}

					// loop through each pixel and shoot a ray
					for (int i = 0; i < width; i++) {
						for (int j = 0; j < height; j++) {	
							// compute ray, and normalize forward vector if fisheye was set
							Ray r = new Ray(i, j, width, height, forward, right, up, eye, fisheye);
							if (fisheye) {
								forward.normalize();
							}
							
							// check if the new ray had r > 1, if so, don't shoot a ray
							if (r.getR() > 1) {
								continue;
							}
							
							// keep track of the closest object that the ray collided with, as well as the corresponding value of t
							Sphere closest = null;
							int closest_index = -1;
							double smallest_T = Double.MAX_VALUE;
							
							// keep track of if a collision occurred inside a sphere
							boolean inside = false;
							
							// check the ray against each object
							for (int n = 0; n < objects.size(); n++) {
								// find t using ray-sphere intersection
								double t = test.raySphereIntersection(r, objects.get(n));
								
								// don't set a pixel if there was no collision
								if (Double.isNaN(t)) {
									continue;
								}	
								
								// update t and the closest object if t was less than the current smallest value of t
								if (t < smallest_T) {	
									smallest_T = t;
									closest = objects.get(n);
									closest_index = n;
								}
								// check if collision occurred inside a sphere
								inside = test.raySphereInside(r, objects.get(n));
							}
							
							// if there was no collision, don't do anything
							if (smallest_T == Double.MAX_VALUE) {
								continue;
							} 
							// otherwise, illuminate and set the pixel
							else { 
								// collision is (ray origin) + t * (ray direction)
								Vector collision = r.getOrigin().add(r.getDirection().scale(smallest_T));
								// normal is from the sphere center to the collision point 
								Vector normal = collision.subtract(closest.getCenter());
								
								// if the collision was inside, invert the normal vector used for lighting
								if (inside) {
									normal = normal.scale(-1);
								}
								// get the color based on Lambert's Law illumination
								Color newColor = test.illuminate(closest.getColor(), normal, collision, suns, bulbs, objects, closest_index);
								
								// apply the exposure function if necessary
								if (exposed) {
									newColor = test.expose(newColor, v);
								}
								
								// apply gamma correction
								Color corrected = test.gammaCorrect(newColor);
								// round colors and set the pixel
								int[] illuminated = corrected.roundColor();
								raster.setPixel(i, j, illuminated);	
							}

						}
					}
					// write the image
					ImageIO.write(img, "png", new File(filename));
				} else {
					System.out.println("Malformed input.");
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return;
	}

	/* returns the given RGB if it was between 0 and 1.
	 * if it was greater than 1, returns 1
	 * if it was less than 0, returns 0
	 * */
	public double verifyColor(double rgb) {
		if (rgb > 1) {
			return 1;
		}
		else if (rgb < 0) {
			return 0;
		} 
		else {
			return rgb;
		}
	}
	


	// sums up contribution of light sources according to Lambert's law
	public Color illuminate(Color objColor, Vector normal, Vector collision, ArrayList<double[]> suns, ArrayList<double[]> bulbs, ArrayList<Sphere> objects, int closestIndex) {
		double[] newColor = {0,0,0};
		double[] objectColors = objColor.getColor();
		// color of the object that the ray collided with
		Vector objectColorVec = new Vector(objectColors[0], objectColors[1], objectColors[2]);
		// make sure the given normal is normalized
		normal.normalize();
		
		// loop through each sun source
		for (int i = 0; i < suns.size(); i++) {
			// get sun color and normalized direction
			Vector sunColor = new Vector(suns.get(i)[3], suns.get(i)[4], suns.get(i)[5]);
			Vector sunDirection = new Vector(suns.get(i)[0], suns.get(i)[1], suns.get(i)[2]);
			sunDirection.normalize();
			
			// check if light source is behind the object
			double cos = sunDirection.dotProduct(normal);
			if (cos < 0) {
				continue;
			}
			
			// compute shadow ray with its origin at the collision point and its direction parallel to the sun direction
			Ray shadowRay = new Ray(collision, sunDirection);
			boolean inShadow = false;
			// check if shadow ray hits anything before the light source
			for (int j = 0; j < objects.size(); j++) {
				// don't check intersections with the collided object itself
				if (j == closestIndex) {
					continue;
				}
				// compute intersection
				double t = raySphereIntersection(shadowRay, objects.get(j));

				// if there was an intersection, then the object is in shadow
				if (!Double.isNaN(t)) {
					inShadow = true;
					break;
				}
			}
			
			// don't do lighting if object was in shadow for that light source
			if (inShadow) {
				continue;
			}	
			
			// add light's color contribution according to Lambert's Law
			Vector lambert = sunColor.componentMultiply(objectColorVec);
			Vector lambert_scaled = lambert.scale(cos);
			newColor[0] += lambert_scaled.getX();
			newColor[1] += lambert_scaled.getY();
			newColor[2] += lambert_scaled.getZ();
		}
		
		// loop through each bulb source
		for (int i = 0; i < bulbs.size(); i++) {
			// get bulb color and normalized direction from the collision point to the light source
			Vector bulbColor = new Vector(bulbs.get(i)[3], bulbs.get(i)[4], bulbs.get(i)[5]);
			Vector bulbDirection =  new Vector(bulbs.get(i)[0], bulbs.get(i)[1], bulbs.get(i)[2]);
			Vector light = bulbDirection.subtract(collision);
			
			// compute distance to light source, then normalize the light vector
			double lightDistance = light.mag_sq();
			light.normalize();
			
			// check if light source is behind object
			double cos = light.dotProduct(normal);
			if (cos < 0) {
				continue;
			}
			
			// compute shadow ray with origin at the collision point, and direction towards the light source
			Ray shadowRay = new Ray(collision, light);
			boolean inShadow = false;
			
			// check if shadow ray hits anything before the light source
			for (int j = 0; j < objects.size(); j++) {
				// don't check intersections with the collided object itself
				if (j == closestIndex) {
					continue;
				}
				// compute intersection
				double t = raySphereIntersection(shadowRay, objects.get(j));

				// if there was a collision, check if it was closer than the light source itself
				if (!Double.isNaN(t)) {
					// get distance from original collision to the shadow ray's collision
					Vector shadowPoint = shadowRay.getOrigin().add(shadowRay.getDirection().scale(t));
					Vector shadowDiff = collision.subtract(shadowPoint);
					double shadowDistance = shadowDiff.mag_sq();
					
					// if shadow ray's collision was less than the distance to the light, the object is in shadow
					if (shadowDistance < lightDistance) {
						inShadow = true;
						break;
					}
				}
			}
			
			// don't illuminate object when it is in shadow
			if (inShadow) {
				continue;
			}
			
			// add light's color contribution according to Lambert's Law
			Vector lambert = bulbColor.componentMultiply(objectColorVec);
			Vector lambert_scaled = lambert.scale(cos / lightDistance);
			newColor[0] += lambert_scaled.getX();
			newColor[1] += lambert_scaled.getY();
			newColor[2] += lambert_scaled.getZ();
		}
		
		// verify the color values and return
		Color ans = new Color(verifyColor(newColor[0]), verifyColor(newColor[1]), verifyColor(newColor[2]), 1);
		return ans;
	}	
	
	/*
	 * Applies the sRGB gamma function to each componenet of the given color
	 */
	public Color gammaCorrect(Color c) {
		double[] rgb = c.getColor();
		double r_linear = rgb[0], g_linear = rgb[1], b_linear = rgb[2];
		double r_sRGB, g_sRGB , b_sRGB;
		
		// update R 
		if (r_linear <= 0.0031308) {
			r_sRGB = 12.92 * r_linear;
		} 
		else {
			r_sRGB = 1.055 * Math.pow(r_linear, 1 / 2.4) - 0.055;
		}
		
		// update G
		if (g_linear <= 0.0031308) {
			g_sRGB = 12.92 * g_linear;
		} 
		else {
			g_sRGB = 1.055 * Math.pow(g_linear, 1 / 2.4) - 0.055;
		}
		
		//update B
		if (b_linear <= 0.0031308) {
			b_sRGB = 12.92 * b_linear;
		} 
		else {
			b_sRGB = 1.055 * Math.pow(b_linear, 1 / 2.4) - 0.055;
		}
		
		return new Color(r_sRGB, g_sRGB, b_sRGB, rgb[3]);
		
	}
	
	/*
	 * Computes the distance to the ray-sphere intersection, if it occurs.
	 * If there is no collision, returns NaN.
	 * 
	 * Code based on algorithm in Implementer's guide:
	 * http://www.cs.virginia.edu/luther/4810/F2021/files/implguide_2010.pdf
	 */
	public double raySphereIntersection(Ray ray, Sphere s) {
		Vector o = ray.getOrigin();
		Vector c = s.getCenter();
		Vector rd = ray.getDirection();
		
		// compute r^2
		double r = s.getRadius();
		double r_sq = r * r;
		
		Vector x = c.subtract(o);
		
		// whether or not collision occured inside of a sphere
		boolean inside = x.mag_sq() < r_sq ? true : false;
		
		double t_c = x.dotProduct(rd) / rd.mag();
		
		// no collision when not inside and t_c < 0
		if ((!inside) && (t_c < 0)) {
			return Double.NaN;
		}
		
		Vector d = o.add(rd.scale(t_c).subtract(c));
		double d_sq = d.mag_sq();
		
		// no collision if not inside and d^2 > r^2
		if ((!inside) && (d_sq > r_sq)) {
			return Double.NaN;
		}
		
		double t_offset = Math.sqrt(r_sq - d_sq) / rd.mag();
		
		if (inside) {
			return t_c + t_offset;
		} else {
			return t_c - t_offset;
		}		
	}
	

	/*
	 * Returns true if the ray-sphere intersection occurs inside the sphere
	 * Returns false otherwise
	 */
	public boolean raySphereInside(Ray ray, Sphere s) {
		Vector o = ray.getOrigin();
		Vector c = s.getCenter();
		
		double r = s.getRadius();
		double r_sq = r * r;
		
		Vector x = c.subtract(o);
		boolean inside = x.mag_sq() < r_sq ? true : false;
		return inside;	
	}
	
	// Applies simple exponential exposure function to the given color, with exponent value of v
	public Color expose(Color c, double v) {
		double[] rgb = c.getColor();
		double r_linear = rgb[0], g_linear = rgb[1], b_linear = rgb[2];
		double r_sRGB, g_sRGB , b_sRGB;
		
		r_sRGB = 1 - Math.pow(Math.E, -r_linear * v);
		g_sRGB = 1 - Math.pow(Math.E, -g_linear * v);
		b_sRGB = 1 - Math.pow(Math.E, -b_linear * v);
		
		return new Color(r_sRGB, g_sRGB, b_sRGB, rgb[3]);
		
	}
	
	
}
