# Use AdoptOpenJDK for base image.
# It's important to use OpenJDK 8u191 or above that has container support enabled.
# https://hub.docker.com/r/adoptopenjdk/openjdk8
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine-slim

VOLUME /CaliforniaStatutes
# Copy the jar to the production image from the builder stage.
COPY target/opinion-rest-ca-*.jar /opinion-rest-ca.jar
EXPOSE 8091

# Run the web service on container startup.
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=${PORT}","-jar","/opinion-rest-ca.jar"]