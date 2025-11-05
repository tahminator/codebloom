#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/local-db.sh"
source "$DIR/run-backend-instance.sh"
trap 'backend_cleanup; db_cleanup' EXIT

db_startup
backend_startup

corepack enable pnpm
pnpm --dir js i --frozen-lockfile
pnpm --dir js run generate
pnpm --dir js run test
