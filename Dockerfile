FROM amazoncorretto:8
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY credentials.json credentials.json
ENTRYPOINT ["java","-jar","/app.jar"]