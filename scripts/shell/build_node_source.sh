#!/usr/bin/env bash

set -euxo pipefail

build_node(){
	python3 patch_node_build.py -p ./
	./configure --enable-static --without-intl
	python3 patch_node_build.py -p ./
	rm patch_node_build.py
	num_jobs=$(command -v nproc > /dev/null 2>&1 && nproc || echo 4)
	make -j $num_jobs
	echo "Node.js build is completed"
}

build_node