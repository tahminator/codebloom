#!/usr/bin/env bash
set -euo pipefail

git-crypt unlock

# UNLOAD_ENVIRONMENTS="prod,staging,dev"
IFS=',' read -ra ENVS <<<"${UNLOAD_ENVIRONMENTS:-}"

declare -A LOADED

for v in "${ENVS[@]}"; do
    ENV_FILE=".env.${v}"
    if [[ -f "$ENV_FILE" ]]; then
        echo "Loading $ENV_FILE"
        declare -A BEFORE
        for VAR in $(compgen -v); do
            BEFORE["$VAR"]=1
        done

        source "$ENV_FILE"

        for VAR in $(compgen -v); do
            if [[ -z "${BEFORE["$VAR"]:-}" ]]; then
                LOADED["$VAR"]=1
            fi
        done
    else
        echo "Warning: $ENV_FILE not found"
    fi
done

EXCLUDED_VARS=(
    "PATH"
    "HOME"
    "PWD"
    "SHELL"
    "USER"
    "DEBUG"
    "LOG_LEVEL"
    "CI"
    "JAVA_HOME"
)

for VAR in "${!LOADED[@]}"; do
    VALUE="${!VAR-}"

    if [[ "$VAR" == "VAR" ]]; then # weird bug
        continue
    fi

    echo "$VAR=$VALUE" >>"$GITHUB_ENV"

    for EX in "${EXCLUDED_VARS[@]}"; do
        if [[ "$VAR" == "$EX" ]]; then
            echo "Not masking $VAR: Excluded"
            continue 2
        fi
    done

    if [[ "$VALUE" == "true" || "$VALUE" == "false" || -z "$VALUE" ]]; then
        echo "Not masking $VAR: true/false/empty value"
        continue
    fi

    echo "Masking $VAR"
    echo "::add-mask::$VALUE"
done
