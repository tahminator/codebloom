#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/local-db.sh"

trap db_cleanup EXIT

java -version
javac -version
echo "JAVA_HOME=$JAVA_HOME"

./mvnw -B install -D skipTests --no-transfer-progress -Denv.SKIP=true

./mvnw checkstyle:check -Denv.SKIP=true

./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"
./mvnw -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

corepack enable pnpm
cd email
pnpm i --frozen-lockfile
cd ..

./email.sh

db_startup

./mvnw test -Dspring.profiles.active=ci
