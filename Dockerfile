FROM openjdk:17 AS builder
COPY . /usr/src/house-backend
WORKDIR /usr/src/house-backend
RUN ./mvnw clean package

FROM openjdk:17
COPY --from=builder /usr/src/house-backend/target/house-0.0.1-SNAPSHOT.jar /usr/src/house-backend/
WORKDIR /usr/src/house-backend
EXPOSE 8080
CMD ["java", "-jar", "house-0.0.1-SNAPSHOT.jar"]
