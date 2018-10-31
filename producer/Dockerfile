FROM openjdk:8-jre-alpine
COPY /target/goleador-producer-*.jar /goleador-producer.jar
COPY ./entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
