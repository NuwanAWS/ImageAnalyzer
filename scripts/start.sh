#!/bin/bash
cd /home/ec2-user/app
java -jar $(ls *.jar | head -n 1) > app.log 2>&1 &
