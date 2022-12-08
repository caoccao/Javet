#!/usr/bin/env bash

set -euxo pipefail

build_node(){
	python3 patch_node_build.py -p ./
	./configure --enable-static --without-intl
	python3 patch_node_build.py -p ./
	rm patch_node_build.py
	make -j4
	echo "Node.js build is completed"
}

build_node