#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_BIN="${MAVEN_BIN:-$ROOT_DIR/mvnw}"

if [[ ! -x "$MAVEN_BIN" ]]; then
  echo "Maven executable was not found: $MAVEN_BIN"
  echo "Run ./mvnw or set MAVEN_BIN to a Maven executable."
  exit 1
fi

cd "$ROOT_DIR"
"$MAVEN_BIN" -f ../camera-management-service/pom.xml -DskipTests install
"$MAVEN_BIN" clean test
