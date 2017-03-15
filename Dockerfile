FROM java:jre-alpine
ARG JARNAME
ADD /target/$JARNAME sample.jar
RUN sh -c 'touch /sample.jar'
ENTRYPOINT exec java $JAVA_OPTS -jar sample.jar
