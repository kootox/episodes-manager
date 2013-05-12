#!/bin/sh

MX=512M
java  -Xmx$MX -Xms$MX -jar ${project.build.finalName}.jar "$@"