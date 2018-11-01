# See https://github.com/tesseract-ocr/tesseract/issues/1860
#FROM openjdk:8-jre-alpine
FROM frolvlad/alpine-oraclejdk8:slim
RUN apk update && apk add --no-cache ffmpeg curl python \
    && curl -L https://yt-dl.org/downloads/latest/youtube-dl -o /usr/local/bin/youtube-dl \
    && chmod a+rx /usr/local/bin/youtube-dl

COPY tessdata /tessdata
COPY /target/goleador-worker-*.jar /goleador-worker.jar
COPY ./entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
