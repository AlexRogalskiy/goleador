#!/usr/bin/env bash

mvn clean package && docker build -t ris58h/goleador-main .