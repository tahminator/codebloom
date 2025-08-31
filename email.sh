#!/usr/bin/env bash

cd email \
  && pnpm i \
  && pnpm email export --dir emails --outDir out \
  && mkdir -p ../src/main/resources/static \
  && cp out/template.html ../src/main/resources/static