#!/usr/bin/env bash

set -euo pipefail

# BaseTestJavet tests the library with the newest mtime, so each library is touched before its tests run.
for library in src/main/resources/libjavet-*.so; do
  touch "${library}"
  gradle test --rerun-tasks --debug
done

gradle build --debug
