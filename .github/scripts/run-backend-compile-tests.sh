#!/usr/bin/env bash
set -euo pipefail

RED='\033[0;31m'

# fmt
corepack enable pnpm
pnpm i --frozen-lockfile

fmt_status=$?
if ! pnpm run fmt; then
    echo -e "${RED}Prettier failed. On your local machine, please run \`just backend-fmt-fix\`"
    exit 1
fi

# lint
./mvnw checkstyle:check

# compile
./mvnw -B verify -Dmaven.test.skip=true --no-transfer-progress
