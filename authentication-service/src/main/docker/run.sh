#!/bin/bash
set -e
function start_app {
  java -Xmx1024m -jar /usr/src/app/app.jar
}
start_app