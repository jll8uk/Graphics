.PHONEY: build, run

build: Color.class Png.class PngTest.class Ray.class Sphere.class Vector.class

run: Color.class Png.class PngTest.class Ray.class Sphere.class Vector.class
	java PngTest $(file) 



Color.class: Color.java
	javac Color.java
	
Png.class: Png.java
	javac Png.java
	
PngTest.class: PngTest.java
	javac PngTest.java

Ray.class: Ray.java
	javac Ray.java
	
Sphere.class: Sphere.java
	javac Sphere.java
	
Vector.class: Vector.java
	javac Vector.java
	
	
clean:
	$(RM) *.class