#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/local-db.sh"
source "$DIR/run-backend-instance.sh"
trap 'backend_cleanup; db_cleanup' EXIT

export TZ="America/New_York"

db_startup
backend_startup

corepack enable pnpm
pnpm --dir js i --frozen-lockfile
pnpm --dir js run generate
pnpm --dir js run test

TIMESTAMP="$(date +%Y.%m.%d-%H.%M.%S)"
GIT_SHA="$(git rev-parse --short HEAD)"
TAG_PREFIX="${TAG_PREFIX:-}"
TAGS=(
    "tahminator/codebloom:${TAG_PREFIX}latest"
    "tahminator/codebloom:${TAG_PREFIX}${TIMESTAMP}"
    "tahminator/codebloom:${TAG_PREFIX}${GIT_SHA}"
)

echo "Building image with tags:"
printf '  - %s\n' "${TAGS[@]}"

[ -n "${DOCKER_HUB_PAT:-}" ] && echo "DOCKER_HUB_PAT found" || echo "DOCKER_HUB_PAT missing or empty"

echo "${DOCKER_HUB_PAT}" | docker login -u "tahminator" --password-stdin

docker buildx create --use --name codebloom-builder || docker buildx use codebloom-builder

if [[ "${DOCKER_UPLOAD:-true}" == "true" ]]; then
    BUILD_MODE="--push"
else
    BUILD_MODE="--load"
fi
SERVER_PROFILES="${SERVER_PROFILES:-prod}"

docker buildx build \
    $BUILD_MODE \
    --file infra/Dockerfile \
    --build-arg SERVER_PROFILES="$SERVER_PROFILES" \
    $(printf -- '--tag %s ' "${TAGS[@]}") \
    .

echo "Image pushed successfully."
