.PHONEY: build, run

build: Ball.class Color.class Main.class Vector.class Wall.class Plane.class PngTest.class Png.class Point.class Matrix.class MyObject.class Quaternion.class

springs: Ball.class Color.class Main.class Vector.class Wall.class Plane.class
	java Main $(file)

bones: PngTest.class Png.class Point.class Matrix.class MyObject.class Quaternion.class Vector.class
	java PngTest $(file)

PngTest.class: PngTest.java
	javac PngTest.java

Png.class: Png.java
	javac Png.java
	
Point.class: Point.java
	javac Point.java

Matrix.class: Matrix.java
	javac Matrix.java

MyObject.class: MyObject.java
	javac MyObject.java

Quaternion.class: Quaternion.java
	javac Quaternion.java

Ball.class: Ball.java
	javac Ball.java

Color.class: Color.java
	javac Color.java
	
Main.class: Main.java
	javac Main.java
	
Vector.class: Vector.java
	javac Vector.java
	
Wall.class: Wall.java
	javac Wall.java
	
Plane.class: Plane.java
	javac Plane.java
	
clean:
	$(RM) *.class
	



