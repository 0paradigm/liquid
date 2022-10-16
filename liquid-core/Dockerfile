FROM openjdk:17

ARG JAR_FILE
ARG PORT

ADD target/${JAR_FILE} /${JAR_FILE}
EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "/${JAR_FILE}"]
