#!/usr/bin/env bash
set -euo pipefail

RED='\033[0;31m'

# fmt
corepack enable pnpm
pnpm i prettier prettier-plugin-java

fmt_status=$?
if ! pnpx prettier -c "**/*.java"; then
    echo -e "${RED}Prettier failed. On your local machine, please run \`just backend-prettier-fix\`"
    exit 1
fi

# lint
./mvnw checkstyle:check

# compile
./mvnw -B verify -Dmaven.test.skip=true --no-transfer-progress
