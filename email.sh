#!/usr/bin/env bash
set -euxo pipefail

cd email \
  && pnpm i \
  && pnpm email export --dir emails --outDir out \
  && mkdir -p ../src/main/resources/static/email \
  && cp out/*.html ../src/main/resources/static/email