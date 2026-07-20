#!/usr/bin/env bash
set -euo pipefail

mkdir -p out
javac -d out $(find src/main/java -name '*.java')
java -cp out com.beingful.camera.admin.CameraManagementAdmin
