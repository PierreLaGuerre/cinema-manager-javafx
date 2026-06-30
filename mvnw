#!/bin/sh
set -e
BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
VERSION=3.9.9
MAVEN_HOME="$BASE_DIR/.mvn/apache-maven-$VERSION"
if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  ARCHIVE="${TMPDIR:-/tmp}/apache-maven-$VERSION-bin.tar.gz"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$VERSION/apache-maven-$VERSION-bin.tar.gz"
  if command -v curl >/dev/null 2>&1; then curl -fsSL "$URL" -o "$ARCHIVE"; else wget -q "$URL" -O "$ARCHIVE"; fi
  tar -xzf "$ARCHIVE" -C "$BASE_DIR/.mvn"
fi
exec "$MAVEN_HOME/bin/mvn" "$@"
