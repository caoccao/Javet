#!/usr/bin/env bash

set -euxo pipefail

build_node(){
	/usr/bin/python3 patch_node_build.py -p ./
	./configure --enable-static --without-intl
	/usr/bin/python3 patch_node_build.py -p ./
	rm patch_node_build.py
	make -j4
	echo "Node.js build is completed"
}

build_node