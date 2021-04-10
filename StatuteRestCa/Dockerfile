# Use AdoptOpenJDK for base image.
# It's important to use OpenJDK 8u191 or above that has container support enabled.
# https://hub.docker.com/r/adoptopenjdk/openjdk8
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM adoptopenjdk/openjdk8:jdk8u282-b08-alpine-slim

VOLUME /CaliforniaStatutes
# Copy the jar to the production image from the builder stage.
COPY target/statute-rest-ca-*.jar /statute-rest-ca.jar
ENV californiastatutesloc=/opcastorage/CaliforniaStatutes
EXPOSE 8090

# Run the web service on container startup.
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=${PORT}","-jar","/statute-rest-ca.jar"]