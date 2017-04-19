FROM java:jre-alpine
ARG JARNAME
ADD /service/target/$JARNAME $JARNAME
RUN sh -c 'touch /' + $JARNAME
ENTRYPOINT exec java $JAVA_OPTS -jar $JARNAME
