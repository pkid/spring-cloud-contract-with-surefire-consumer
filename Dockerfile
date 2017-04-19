FROM java:jre-alpine
ARG JARNAME
ADD /service/target/$JARNAME service.jar
RUN sh -c 'touch /service.jar'
ENTRYPOINT exec java $JAVA_OPTS -jar service.jar
