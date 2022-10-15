FROM openjdk:17

ARG JAR_FILE
ARG PORT

ADD target/${JAR_FILE} liquid_service.jar
EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "/liquid_service.jar"]
