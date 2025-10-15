#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/local-db.sh"
source "$DIR/run-backend-instance.sh"
trap 'backend_cleanup; db_cleanup' EXIT

db_startup
backend_startup

cd js
corepack enable pnpm && pnpm i --frozen-lockfile

pnpm run generate

pnpm run test
