.PHONEY: build, run

build: PngTest.class Png.class Point.class Matrix.class

run: PngTest.class Png.class Point.class Matrix.class
	java PngTest $(file)

PngTest.class: PngTest.java
	javac PngTest.java

Png.class: Png.java
	javac Png.java
	
Point.class: Point.java
	javac Point.java

Matrix.class: Matrix.java
	javac Matrix.java
	
clean:
	$(RM) *.class
