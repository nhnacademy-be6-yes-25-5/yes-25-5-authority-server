# 로컬에서 '~/.ssh/id_rsa.pub' (공개키) = 원격에서 '~/.ssh/authorized_keys' (인증키)
# 로컬에서 '~/.ssh/id_rsa' (개인키)를 SSH_PRIVATE_KEY에 삽입

# Define build arguments
ARG JWT_SECRET
ARG EUREKA_SERVER_HOSTNAME
ARG EUREKA_GATEWAY_PORT

# Set environment variables from build arguments
ENV JWT_SECRET=${JWT_SECRET}
ENV EUREKA_SERVER_HOSTNAME=${EUREKA_SERVER_HOSTNAME}
ENV EUREKA_GATEWAY_PORT=${EUREKA_GATEWAY_PORT}

# Use the official Maven image with Java 21
FROM maven:3.8.8-eclipse-temurin-21

# Set the working directory
WORKDIR /authority

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code
COPY . .

# Build the application
RUN mvn package

# Default command
CMD ["java", "-jar", "authority-server-1.0-SNAPSHOT.jar"]
