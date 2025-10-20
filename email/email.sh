#!/usr/bin/env bash

# compiles react-email files and outputs it to static dir in backend
# mainly used in dev

set -euxo pipefail

pnpm i &&
    pnpm email export --dir emails --outDir out &&
    mkdir -p ../src/main/resources/static/email &&
    rm -f ../src/main/resources/static/email/*.html &&
    cp out/*.html ../src/main/resources/static/email
