#!/bin/sh

java \
  -Ddatasource.url=$DATASOURCE_URL \
  -Ddatasource.username=$DATASOURCE_USERNAME \
  -Ddatasource.password=$DATASOURCE_PASSWORD \
  -Dyoutube.apiKey=$YOUTUBE_API_KEY \
  -Dgoogle.app.clientId=$GOOGLE_APP_CLIENT_ID \
  -Dgoogle.app.clientSecret=$GOOGLE_APP_CLIENT_SECRET \
  -Dgoogle.app.refreshToken=$GOOGLE_APP_REFRESH_TOKEN \
  -jar /goleador-main.jar
