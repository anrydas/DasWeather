#!/bin/sh

JAVA_PARAMS="
  -Dspring.profiles.active=PROD
  -Dlogging.file.name=logs/DasWeather.log
"

java -jar ${JAVA_PARAMS} DasWeather.jar &

exit 0