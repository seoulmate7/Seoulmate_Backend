#!/bin/bash

APP_DIR=/home/ubuntu/app

cd $APP_DIR

echo "Stopping old containers..."
docker-compose down

echo "Building and starting containers..."
docker-compose up --build -d