FROM openjdk:17

ARG JAR_FILE
ARG PORT

ADD target/${JAR_FILE} /liquid_core.jar
EXPOSE ${PORT}

ENTRYPOINT [ \
    "java", "-jar", "/liquid_auth.jar", \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "--add-opens", "java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED", \
    "--add-opens", "java.base/java.math=ALL-UNNAMED" \
]
