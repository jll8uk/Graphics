import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		
		
		File myfile = new File(args[0]);
		Scanner sc = new Scanner(myfile);
		String base = "";
		int frames = 0;
		if (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			String data[] = line.split("\\s+");
			String keyword = data[0];

			if (keyword.equals("txts")) {
				int w = Integer.parseInt(data[1]);
				int h = Integer.parseInt(data[2]);
				base = data[3];
				frames = Integer.parseInt(data[4]);
				for (int i = 0; i < frames; i++) {
					String frameNum = getFileNum(i);
					String filename = base + frameNum;
					File myObj = new File(filename + ".txt");
					if (myObj.createNewFile()) {
						
						FileWriter myWriter = new FileWriter(myObj.getName());
						String output = "png " + w + " " + h + " " + filename + ".png\n";
						myWriter.write(output);
						myWriter.close();
					} else {
				
					}
				}
			}

		} else {
			System.out.println("Malformed input.");
		}

		ArrayList<Ball> balls = new ArrayList<Ball>();
		ArrayList<Wall> walls = new ArrayList<Wall>();
		ArrayList<Ball> anchors = new ArrayList<Ball>();

		double radius = 0;
		double mass = 1;
		double elasticity = 1;
		double shininess = 0;
		double px, py, pz, vx, vy, vz;
		Color c = new Color(1, 1, 1, 1);
		Vector gravity = new Vector(0, 0, 0);

		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			String data[] = line.split("\\s+");
			String keyword = data[0];

			switch (keyword) {
			case "radius":
				radius = Double.parseDouble(data[1]);
				break;
			case "elasticity":
				elasticity = Double.parseDouble(data[1]);
				break;
			case "mass":
				mass = Double.parseDouble(data[1]);
				break;
			case "color":
				c = new Color(Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), 1);
				writeToFiles(base, frames, line);
				break;
			case "ball":
				px = Double.parseDouble(data[1]);
				py = Double.parseDouble(data[2]);
				pz = Double.parseDouble(data[3]);
				vx = Double.parseDouble(data[4]);
				vy = Double.parseDouble(data[5]);
				vz = Double.parseDouble(data[6]);

				Vector p = new Vector(px, py, pz);
				Vector v = new Vector(vx, vy, vz);

				Ball b = new Ball(p, v, radius, mass, elasticity, shininess, c);
				balls.add(b);
				break;
			case "anchor":
				double anchor_px = Double.parseDouble(data[1]);
				double anchor_py = Double.parseDouble(data[2]);
				double anchor_pz = Double.parseDouble(data[3]);
				double anchor_vx = Double.parseDouble(data[4]);
				double anchor_vy = Double.parseDouble(data[5]);
				double anchor_vz = Double.parseDouble(data[6]);

				Vector anchor_p = new Vector(anchor_px, anchor_py, anchor_pz);
				Vector anchor_v = new Vector(anchor_vx, anchor_vy, anchor_vz);

				Ball anchor = new Ball(anchor_p, anchor_v, radius, mass, elasticity, shininess, c);
				anchors.add(anchor);
				break;
			case "wall":
				double A = Double.parseDouble(data[1]);
				double B = Double.parseDouble(data[2]);
				double C = Double.parseDouble(data[3]);
				double D = Double.parseDouble(data[4]);
				walls.add(new Wall(A, B, C, D));
				break;
			case "gravity":
				double gx = Double.parseDouble(data[1]);
				double gy = Double.parseDouble(data[2]);
				double gz = Double.parseDouble(data[3]);

				gravity = new Vector(gx, gy, gz);
				break;
			case "shininess":
				shininess = Double.parseDouble(data[1]);
				break;
			default:
				writeToFiles(base, frames, line);
				break;
			}

		}

		for (int i = 0; i < frames; i++) {
			
			for (Ball ball : balls) {
				
				Color ballColor = ball.getColor();
				double r = ballColor.getR();
				double g = ballColor.getG();
				double b = ballColor.getB();
				String shininess_str = (ball.getShininess() != 0) ? ("shininess " + ball.getShininess() + "\n") : "";
				String output = "color " + r + " " + g + " " + b + "\n" + shininess_str + "sphere " + ball.getX() + " " + ball.getY()
						+ " " + ball.getZ() + " " + ball.getRadius();
				Files.write(Paths.get(base + getFileNum(i) + ".txt"), (output + "\n").getBytes(),
						StandardOpenOption.APPEND);
				ball.updatePosition(gravity);
				ball.updateVelocity(gravity);
				
			}
			
			for (Ball ball : anchors) {
				Color ballColor = ball.getColor();
				double r = ballColor.getR();
				double g = ballColor.getG();
				double b = ballColor.getB();
				String shininess_str = (ball.getShininess() != 0) ? ("shininess " + ball.getShininess() + "\n") : "";
				String output = "color " + r + " " + g + " " + b + "\n" + shininess_str + "sphere " + ball.getX() + " " + ball.getY()
						+ " " + ball.getZ() + " " + ball.getRadius();
				Files.write(Paths.get(base + getFileNum(i) + ".txt"), (output + "\n").getBytes(),
						StandardOpenOption.APPEND);
				moveAnchor(ball);
			}

			
			
			
			for (int a = 0; a < (balls.size() - 1); a ++) {
				for (int b = (a + 1); b < balls.size(); b++) {
					Ball ball_A = balls.get(a);
					Ball ball_B = balls.get(b);
					
					double dist = distanceBetween(ball_A.getPosition(), ball_B.getPosition());
					
					if (dist < (ball_A.getRadius() + ball_B.getRadius())) {
						double delP = ((ball_A.getRadius() + ball_B.getRadius()) - dist);	
						
						Vector collision = (ball_B.getPosition()).subtract(ball_A.getPosition());
						collision.normalize();
						
						
						Vector parallel_a = project(ball_A.getVelocity(), collision);
						Vector parallel_b = project(ball_B.getVelocity(), collision);
						
						
						Vector perp_a = (ball_A.getVelocity()).subtract(parallel_a);
						Vector perp_b = (ball_B.getVelocity()).subtract(parallel_b);
					
			
						double u_a = componentAlong(ball_A.getVelocity(), collision);
						double u_b = componentAlong(ball_B.getVelocity(), collision);
				
						
						double m_a = ball_A.getMass();
						double m_b = ball_B.getMass();
						
						double ratio_a = 1 - (m_a / (m_a + m_b));
						double ratio_b = 1 - (m_b / (m_a + m_b));
						
						
						System.out.println("Overlap detected. Velocity of ball A: " + ball_A.getVelocity());
						System.out.println("\nVelocity of ball B: " + ball_B.getVelocity());
						System.out.println("Position of ball A: " + ball_A.getPosition());
						System.out.println("\nPosition of ball B: " + ball_B.getPosition());
						System.out.println("\nCollision vector: " + collision);
						
						System.out.println("\nBall A's color: " + ball_A.getColor());
						System.out.println("\nBall B's color: " + ball_B.getColor() + "\n\n\n");
						
						ball_B.setPosition(ball_B.getPosition().add(collision.scale(delP * ratio_a)));
						ball_A.setPosition(ball_A.getPosition().add(collision.scale(-delP * ratio_b)));
						
						
						
						
					
						
						if ((u_a - u_b) < 0) {
							break;
						} 
						
						double C_R = (ball_A.getElasticity() + ball_B.getElasticity()) / 2.0;
						
						double v_a = getVa(u_a, u_b, ball_A.getMass(), ball_B.getMass(), C_R);
						double v_b = getVb(u_a, u_b, ball_A.getMass(), ball_B.getMass(), C_R);
								
						Vector newParallel_A = collision.scale(v_a);
						Vector newParallel_B = collision.scale(v_b);
						
						Vector newV_a = newParallel_A.add(perp_a);
						Vector newV_b = newParallel_B.add(perp_b);
						
						System.out.println("Original relative velocity: " + (u_a - u_b));
						System.out.println("New relative velocity: " + (v_a - v_b));
						
						ball_A.setVelocity(newV_a);
						ball_B.setVelocity(newV_b);
					}
					
				}
			}
			
			
			
			for (int a = 0; a < balls.size(); a ++) {
				for (int b = 0; b < anchors.size(); b++) {
					Ball ball = balls.get(a);
					Ball anchor = anchors.get(b);
					
					double dist = distanceBetween(ball.getPosition(), anchor.getPosition());
					
					
					if (dist < (ball.getRadius() + anchor.getRadius())) {
						
						double delP = ((ball.getRadius() + anchor.getRadius()) - dist);	

						Vector collision = anchor.getPosition().subtract(ball.getPosition());
						collision.normalize();

					
						ball.setPosition(ball.getPosition().add(collision.scale(-delP)));

						
						Vector parallel_a = project(ball.getVelocity(), collision);
						
						Vector perp_a = ball.getVelocity().subtract(parallel_a);
						
						double u_a = componentAlong(ball.getVelocity(), collision);
						double u_b = componentAlong(anchor.getVelocity(), collision);
					
						
						double C_R = ball.getElasticity();
			
						
						double v_a = getVa(u_a, u_b, 0, 1, C_R);
					
		
						Vector newParallel_A = collision.scale(v_a);				
						Vector newV_a = newParallel_A.add(perp_a);
						
						ball.setVelocity(newV_a);
					}
					
				}
			}
			
			for (Ball ball : balls) {
				for (Wall w : walls) {
					Vector closestPoint = ball.getPosition().add(w.getNormal().scale(-ball.getRadius()));
					double planeDist = wallIntersection(closestPoint, w);
					
					if (planeDist < 0) {
						// wall collision
						
						Vector newP = ball.getPosition().add(w.getNormal().scale(-planeDist));
						ball.setPosition(newP);
						
						
						Vector along = project(ball.getVelocity(), w.getNormal());
						Vector change = along.scale(-1 - ball.getElasticity());
						Vector newV = ball.getVelocity().add(change);
						ball.setVelocity(newV);
						
						
					}
				}
			}

		}

		sc.close();

	}

	public static String getFileNum(int i) {
		String frameNum = "";
		if (i < 10) {
			frameNum = "00" + i;
		} else if (i < 100) {
			frameNum = "0" + i;
		} else {
			frameNum = "" + i;
		}
		return frameNum;
	}

	public static void writeToFiles(String base, int frames, String input) throws IOException {
		for (int i = 0; i < frames; i++) {
			String frameNum = getFileNum(i);
			String filename = base + frameNum;
			Files.write(Paths.get(filename + ".txt"), (input + "\n").getBytes(), StandardOpenOption.APPEND);
		}
		return;
	}

	public static double wallIntersection(Vector p, Wall w) {
		
		double x = p.getX();
		double y = p.getY();
		double z = p.getZ();

		double A = w.getA();
		double B = w.getB();
		double C = w.getC();
		double D = w.getD();

		double result = (A * x) + (B * y) + (C * z) + D;
		return result;
	}

	public static Vector project(Vector a, Vector b) {
		b.normalize();
		double componentAlong = a.dotProduct(b);
		return b.scale(componentAlong);
	}
	
	public static double componentAlong(Vector a, Vector b) {
		b.normalize();
		double ans = a.dotProduct(b);
		return ans;
	}
	
	public static double distanceBetween(Vector a, Vector b) {
		Vector ab = a.subtract(b);
		return ab.mag();
	}
	
	public static double getVa(double u_a, double u_b, double m_a, double m_b, double C_R) {
		return (((C_R * m_b) * (u_b - u_a)) + (m_a * u_a) + (m_b * u_b)) / (m_a + m_b);
	}
	
	public static double getVb(double u_a, double u_b, double m_a, double m_b, double C_R) {
		return (((C_R * m_a) * (u_a - u_b)) + (m_a * u_a) + (m_b * u_b)) / (m_a + m_b);
	}
	
	public static void moveAnchor(Ball b) {
		b.setPosition(b.getPosition().add(b.getVelocity()));
	}
	
	

}
