#!/usr/bin/env bash
set -euo pipefail

RED='\033[0;31m'

# fmt
./mvnw spotless:check

# lint
./mvnw checkstyle:check

# compile
./mvnw -B verify -Dmaven.test.skip=true --no-transfer-progress
