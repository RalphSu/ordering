#!/usr/bin/env bash

docker build -f ./Dockerfile.order -t orderserver:dev .
docker build -f ./Dockerfile.rest -t restserver:dev .

