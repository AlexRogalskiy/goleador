FROM openjdk:8-jre-alpine
COPY /target/goleador-main-*.jar /goleador-main.jar
COPY ./entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
