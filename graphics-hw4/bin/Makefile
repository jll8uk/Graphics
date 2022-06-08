.PHONEY: build, run

build: PngTest.class Png.class Point.class Matrix.class MyObject.class Quaternion.class Vector.class

run: PngTest.class Png.class Point.class Matrix.class MyObject.class Quaternion.class Vector.class
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

Vector.class: Vector.java
	javac Vector.java
	
clean:
	$(RM) *.class
