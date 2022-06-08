.PHONEY: build, run

build: PngTest.class Png.class Point.class

run: PngTest.class Png.class Point.class
	java PngTest $(file)

PngTest.class: PngTest.java
	javac PngTest.java

Png.class: Png.java
	javac Png.java
	
Point.class: Point.java
	javac Point.java
	
clean:
	$(RM) *.class *.png