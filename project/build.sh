#!/usr/bin/env bash

mvn clean install -DskipTests

docker build -f ./Dockerfile.order -t orderserver:dev .
docker build -f ./Dockerfile.rest -t restserver:dev .

