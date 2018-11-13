#!/bin/sh

echo "datasource.url=${DATASOURCE_URL}" >> app.properties
echo "datasource.username=${DATASOURCE_USERNAME}" >> app.properties
echo "datasource.password=${DATASOURCE_PASSWORD}" >> app.properties
echo "youtube.apiKey=${YOUTUBE_API_KEY}" >> app.properties
echo "google.app.clientId=${GOOGLE_APP_CLIENT_ID}" >> app.properties
echo "google.app.clientSecret=${GOOGLE_APP_CLIENT_SECRET}" >> app.properties
echo "google.app.refreshToken=${GOOGLE_APP_REFRESH_TOKEN}" >> app.properties
echo "rabbitmq.uri=${RABBITMQ_URI}" >> app.properties

java -jar /goleador-main.jar app.properties
