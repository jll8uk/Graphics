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
						System.out.println("File created: " + myObj.getName());
						FileWriter myWriter = new FileWriter(myObj.getName());
						String output = "png " + w + " " + h + " " + filename + ".png\n";
						myWriter.write(output);
						myWriter.close();
					} else {
						System.out.println("File already exists.");
					}
				}
			}

		} else {
			System.out.println("Malformed input.");
		}

		ArrayList<Ball> balls = new ArrayList<Ball>();
		ArrayList<Wall> walls = new ArrayList<Wall>();
		
		double radius = 0;
		double mass = 1;
		double elasticity = 1;
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
				
				Ball b = new Ball(p, v, radius, mass, elasticity, c);
				balls.add(b);
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
				String output = "color " + r + " " + g + " " + b + "\n" + "sphere " + ball.getX() + " " + ball.getY() + " " + ball.getZ() + " " + ball.getRadius();
				Files.write(Paths.get(base + getFileNum(i) + ".txt"), (output + "\n").getBytes(), StandardOpenOption.APPEND);
				ball.updatePosition(gravity);
				ball.updateVelocity(gravity);
			}
						
		}
		
		
//		for (Ball ball : balls) {
//			Color ballColor = ball.getColor();
//			double r = ballColor.getR();
//			double g = ballColor.getG();
//			double b = ballColor.getB();
//			writeToFiles(base, frames, "color " + r + " " + g + " " + b + " ");
//			writeToFiles(base, frames, "sphere " + ball.getX() + " " + ball.getY() + " " + ball.getZ() + " " + ball.getRadius());
//		}

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
	
	public double planeDistance(Vector p, Wall w) {
		Vector n = w.getNormal();
		Vector v = p.subtract(w.getPoint());
		double distance = Math.abs(v.dotProduct(n));
		return distance;
	}
	
	
}
