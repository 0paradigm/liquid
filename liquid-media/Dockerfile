FROM openjdk:17

ARG JAR_FILE
ARG PORT

ADD target/${JAR_FILE} /liquid_media.jar
EXPOSE ${PORT}

ENTRYPOINT [
    "java", "-jar", "/liquid_media.jar",
    "--add-opens", "java.base/java.lang=ALL-UNNAMED",
    "--add-opens", "java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED",
    "--add-opens", "java.base/java.math=ALL-UNNAMED"
]
