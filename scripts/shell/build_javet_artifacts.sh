#!/usr/bin/env bash

set -euo pipefail

touch src/main/resources/libjavet-v8*
gradle build test --rerun-tasks --debug

touch src/main/resources/libjavet-node*
gradle test --rerun-tasks --debug
