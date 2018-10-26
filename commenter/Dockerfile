FROM maven:3.5-jdk-8 as build
COPY src /usr/src/myapp/src
COPY pom.xml /usr/src/myapp
RUN mvn -f /usr/src/myapp/pom.xml clean package

FROM openjdk:8-jre-alpine
COPY --from=build /usr/src/myapp/target/goleador-commenter-1.0-SNAPSHOT.jar /goleador-commenter-1.0-SNAPSHOT.jar
COPY ./entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
