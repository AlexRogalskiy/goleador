#!/bin/sh

java \
  -Ddatasource.url=$DATASOURCE_URL \
  -Ddatasource.username=$DATASOURCE_USERNAME \
  -Ddatasource.password=$DATASOURCE_PASSWORD \
  -Dyoutube.apiKey=$YOUTUBE_API_KEY \
  -jar /youtube-goleador-producer-1.0-SNAPSHOT.jar
