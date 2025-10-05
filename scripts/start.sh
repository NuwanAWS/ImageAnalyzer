#!/bin/bash
set -e

APP_DIR=/home/ec2-user/app
cd $APP_DIR

# Find shaded jar (fat jar from Maven Shade Plugin)
JAR=$(ls *.jar | head -n 1)

if [ -z "$JAR" ]; then
  echo "ERROR: No shaded jar found in $APP_DIR"
  exit 1
fi

echo "Starting $JAR ..."
nohup java -jar "$JAR" > app.log 2>&1 &
