#!/usr/bin/env bash

mvn clean package -Djavacpp.platform=linux-x86_64

NAME="ris58h/goleador-worker"
TAG=$(git log -1 --pretty=%h)
docker build -t ${NAME}:${TAG} .
docker tag ${NAME}:${TAG} ${NAME}:latest
