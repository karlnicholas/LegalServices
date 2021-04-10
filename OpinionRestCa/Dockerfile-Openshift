FROM registry.redhat.io/openjdk/openjdk-11-rhel7
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8091
CMD [ "java", "-XX:+UseContainerSupport", "-jar", "app.jar" ]