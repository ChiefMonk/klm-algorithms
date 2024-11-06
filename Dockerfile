# Use a base image with Java and Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR ./

RUN mvn clean package

# Use a base image with Java
FROM eclipse-temurin:21

# Copy the built jar file into the image
COPY --from=build target/klm-algorithms*.jar app.jar

# Set the entry point to run your application
ENTRYPOINT ["java","-jar","/app.jar"]