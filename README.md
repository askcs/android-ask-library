You'll need to add libs/jackson-2.2.0-custom.jar to your local MVN repo:

mvn install:install-file -Dfile=libs/jackson-2.2.0-custom.jar -DgroupId=com.fasterxml -DartifactId=jackson -Dversion=1 -Dpackaging=jar

