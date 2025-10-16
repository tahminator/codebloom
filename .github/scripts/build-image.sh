#!/usr/bin/env bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/local-db.sh"
source "$DIR/run-backend-instance.sh"
trap 'backend_cleanup; db_cleanup' EXIT

export TZ="America/New_York"

db_startup
backend_startup

cd js
npm install -g corepack@latest
corepack enable pnpm
pnpm i --frozen-lockfile
pnpm run generate
cd ..

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

docker login -u "tahminator" -p "${DOCKER_HUB_PAT}"

docker buildx create --use --name codebloom-builder || docker buildx use codebloom-builder

docker buildx build \
    --push \
    --file infra/Dockerfile \
    --build-arg SENTRY_AUTH_TOKEN="${SENTRY_AUTH_TOKEN:-}" \
    --build-arg SENTRY_DSN="${SENTRY_DSN:-}" \
    --build-arg SKIP="${SKIP:-}" \
    --build-arg VITE_DSN="${VITE_DSN:-}" \
    $(printf -- '--tag %s ' "${TAGS[@]}") \
    .

echo "Image pushed successfully."
