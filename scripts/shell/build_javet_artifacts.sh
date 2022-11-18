#!/usr/bin/env bash

set -euo pipefail

pushd /Javet/cpp
sh ./build-linux.sh -DV8_DIR=/google/v8
sh ./build-linux.sh -DNODE_DIR=/node
popd

touch src/main/resources/libjavet-v8*
gradle build test --rerun-tasks --debug
# gradle build test --rerun-tasks

touch src/main/resources/libjavet-node*
gradle test --rerun-tasks --debug
# gradle test --rerun-tasks