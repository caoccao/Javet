#!/usr/bin/env bash

set -euxo pipefail

fetch_node_source() {
  git clone --depth=1 --branch=v${JAVET_NODE_VERSION} https://github.com/nodejs/node.git
  echo "Node.js source fetched"
}

fetch_node_source