FROM maven:3.5-jdk-8 as build
COPY src /usr/src/myapp/src
COPY pom.xml /usr/src/myapp
RUN mvn -f /usr/src/myapp/pom.xml clean package -Djavacpp.platform=linux-x86_64

# See https://github.com/tesseract-ocr/tesseract/issues/1860
#FROM openjdk:8-jre-alpine
#RUN apk update && apk add --no-cache ffmpeg curl python \
#    && curl -L https://yt-dl.org/downloads/latest/youtube-dl -o /usr/local/bin/youtube-dl \
#    && chmod a+rx /usr/local/bin/youtube-dl
#COPY --from=build /usr/src/myapp/target/youtube-goleador-worker-1.0-SNAPSHOT.jar /youtube-goleador-worker-1.0-SNAPSHOT.jar
#COPY tessdata /tessdata
#COPY ./entrypoint.sh /
#ENTRYPOINT ["/entrypoint.sh"]

FROM openjdk:8-jre-slim
RUN apt update && apt install -y ffmpeg youtube-dl
COPY --from=build /usr/src/myapp/target/youtube-goleador-worker-1.0-SNAPSHOT.jar /youtube-goleador-worker-1.0-SNAPSHOT.jar
COPY tessdata /tessdata
COPY ./entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
