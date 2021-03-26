FROM registry.redhat.io/openjdk/openjdk-11-rhel7
VOLUME /tmp
VOLUME /opcastorage
ENV gsindexloc=/opcastorage/index
ENV gsindextaxoloc=/opcastorage/indextaxo
COPY target/*.jar app.jar
EXPOSE 8080
CMD [ "java", "-Xmx48m", "-XX:+UseContainerSupport", "-jar", "app.jar" ]