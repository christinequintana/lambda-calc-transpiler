all: clean jar run

jar:
	mvn package

test:
	mvn test

clean:
	rm -rf target

run:
	java -jar target/lctjt-1.0-SNAPSHOT-jar-with-dependencies.jar lc target/javacode
	cd target/javacode; javac *.java
	cd target/javacode; java Main
