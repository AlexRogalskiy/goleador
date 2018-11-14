#!/bin/sh

echo "datasource.url=${DATASOURCE_URL}" >> app.properties
echo "datasource.username=${DATASOURCE_USERNAME}" >> app.properties
echo "datasource.password=${DATASOURCE_PASSWORD}" >> app.properties
echo "rabbitmq.uri=${RABBITMQ_URI}" >> app.properties

java ${JAVA_OPTS} -jar /goleador-worker.jar app.properties
