import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Png {
	
	private int width;
	private int height;
	
	
	//constructors
	public Png(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	
	
	int getWidth() {
		return this.width;
	}
	int getHeight() {
		return this.height;
	}
	void setWidth(int width) {
		this.width = width;
		return;
	}
	void setHeight(int height) {
		this.height = height;
		return;
	}
	
	
	void clearImage(WritableRaster raster) {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				raster.setPixel(x, y, new int[] { 0, 0, 0, 0 });
			}
		}
	}
	
	String getTag(int frame_num) {
		String tag = "";
		if (frame_num < 10) {
			tag += "00" + frame_num;
		} else if (frame_num < 100) {
			tag += "0" + frame_num;
		} else {
			tag += frame_num;
		}
		return tag;
	}
	
}

