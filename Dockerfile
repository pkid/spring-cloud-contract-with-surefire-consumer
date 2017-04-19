FROM java:jre-alpine
ARG JARNAME
ENV PROJECTNAME = sample.jar
ADD /service/target/$JARNAME $PROJECTNAME
RUN sh -c 'touch /' + $PROJECTNAME
ENTRYPOINT exec java $JAVA_OPTS -jar sample.jar
