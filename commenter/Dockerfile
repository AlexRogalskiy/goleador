FROM openjdk:8-jre-alpine
COPY /target/goleador-commenter-*.jar /goleador-commenter.jar
COPY ./entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
