FROM amazoncorretto:21.0.4-alpine3.18

WORKDIR /app

ENV DB_URL=jdbc:mongodb://vehicle:27017/vehicle

COPY target/vehicle-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]