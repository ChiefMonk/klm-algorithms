# Use a base image with Java and Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the Maven project files into the image
COPY pom.xml ./
COPY src ./src

# Build the application
RUN mvn clean package

# Use a lightweight image with Java for running the application
FROM eclipse-temurin:21

# Copy the built jar file from the build stage
COPY --from=build /app/target/klm-algorithms*.jar app.jar

# Set the entry point to run your application
ENTRYPOINT ["java","-jar","/app.jar"]