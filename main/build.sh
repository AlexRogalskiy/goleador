#!/usr/bin/env bash

mvn clean package

NAME="ris58h/goleador-main"
TAG=$(git log -1 --pretty=%h)
docker build -t ${NAME}:${TAG} .
docker tag ${NAME}:${TAG} ${NAME}:latest
