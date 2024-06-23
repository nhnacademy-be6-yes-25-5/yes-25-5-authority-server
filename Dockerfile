# Use the official Maven image with Java 21 as base image
FROM maven:3.8.8-openjdk-21-slim AS build

# Set the working directory inside the container
WORKDIR /authority

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code
COPY . .

# Build the application
RUN mvn package

# Start with a new stage to reduce the size of the final image
FROM adoptopenjdk:11-jre-hotspot

# Set environment variables
ARG JWT_SECRET
ARG EUREKA_SERVER_HOSTNAME
ARG EUREKA_GATEWAY_PORT
ENV JWT_SECRET=${JWT_SECRET}
ENV EUREKA_SERVER_HOSTNAME=${EUREKA_SERVER_HOSTNAME}
ENV EUREKA_GATEWAY_PORT=${EUREKA_GATEWAY_PORT}

# Set the working directory inside the container
WORKDIR /app

# Copy the built artifact from the previous stage
COPY --from=build /authority/target/authority-server-1.0-SNAPSHOT.jar .

# Specify the command to run on container start
CMD ["java", "-jar", "authority-server-1.0-SNAPSHOT.jar"]
