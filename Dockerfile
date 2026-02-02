FROM eclipse-temurin:25-jre

WORKDIR /app
COPY /build/libs/openelifba-0.0.1-SNAPSHOT.jar /app/app.jar
#COPY credentials /root/.aws/credentials
CMD ["java", "-jar", "/app/app.jar"]
