#!/bin/sh

SCRIPTPATH=$( cd -- $(dirname $0) >/dev/null 2>&1 ; pwd -P )
cd $SCRIPTPATH

JAVA_PARAMS="
  -Dspring.profiles.active=PROD
  -Dlogging.file.name=logs/DasWeather.log
"

java -jar ${JAVA_PARAMS} DasWeather.jar &

exit 0