#!/usr/bin/env bash
set -euo pipefail

APP_SPEC_PATH="${1:-.do/prod/app.yml}"
ENV_PATH="${2:-.env}"
TMP_ENVS_YAML="$(mktemp 2>/dev/null || echo "/tmp/tmp_envs_$$.yml")"
if [[ -z "$TMP_ENVS_YAML" ]]; then
    echo "Failed to create temp file" >&2
    exit 1
fi
echo "Parsing environment variables..."
: >"$TMP_ENVS_YAML"

while IFS='=' read -r key val; do
    # Skip empty lines or comments
    [[ -z "$key" || "$key" =~ ^# ]] && continue
    key=$(echo "$key" | xargs)
    val=$(echo "$val" | xargs)
    printf "  - key: %s\n    value: \"%s\"\n    scope: RUN_AND_BUILD_TIME\n    type: SECRET\n" "$key" "$val" >>"$TMP_ENVS_YAML"
done <"$ENV_PATH"

echo "Replacing root .envs in $APP_SPEC_PATH ..."
yq -i '.envs = load("'"$TMP_ENVS_YAML"'")' "$APP_SPEC_PATH"

echo "Updated $APP_SPEC_PATH with $(grep -c 'key:' "$TMP_ENVS_YAML") env vars."
rm -f "$TMP_ENVS_YAML"
