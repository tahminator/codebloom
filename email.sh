#!/usr/bin/env bash
set -euxo pipefail

cd email \
  && pnpm i \
  && pnpm email export --dir emails --outDir out \
  && mkdir -p ../src/main/resources/static/email \
  && cp out/template.html ../src/main/resources/static/email