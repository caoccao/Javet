#!/usr/bin/env sh

fetch_depot_tools() {
	git clone -q --depth=10 \
		--branch=main https://chromium.googlesource.com/chromium/tools/depot_tools.git
	cd depot_tools
	git checkout -q remotes/origin/main
	cd ..
}

fetch_v8_source() {
	# reference - https://stackoverflow.com/a/47093174/30007
	mkdir v8

	export PATH=$PWD/depot_tools:$PATH

	cd v8
	git init .

	git fetch -q --depth 1 https://chromium.googlesource.com/v8/v8.git \
		+refs/tags/${JAVET_V8_VERSION}:v8_${JAVET_V8_VERSION}

	git checkout -q tags/${JAVET_V8_VERSION}
	cd ..

	maybe-verbose.sh gclient root
	local gclient_config='solutions = [{"name": "v8","url": "https://chromium.googlesource.com/v8/v8.git","deps_file": "DEPS","managed": False,"custom_deps": {},},]'
	gclient config --spec "$gclient_config"
	maybe-verbose.sh gclient sync --no-history
	maybe-verbose.sh gclient runhooks

	cd v8
	sed -i 's/snapcraft/nosnapcraft/g' ./build/install-build-deps.sh
	maybe-verbose.sh ./build/install-build-deps.sh
	sed -i 's/nosnapcraft/snapcraft/g' ./build/install-build-deps.sh
	cd ..

	echo "V8 source fetched"
}

run_final_sync() {
	cp -r -f /google-temp /google

	cd google

	export PATH=$PWD/depot_tools:$PATH
	chmod -R +x $PWD/depot_tools

	if [ "${ANDROID}" = "android" ]; then
		echo "\ntarget_os = ['android']" >>.gclient
	fi

	maybe-verbose.sh gclient sync --no-history
	cd ..
}

if [ "$1" = "fetch_depot_tools" ]; then
	shift
	fetch_depot_tools
elif [ "$1" = "fetch_v8_source" ]; then
	shift
	fetch_v8_source
elif [ "$1" = "run_final_sync" ]; then
	shift
	run_final_sync
else
	cat <<EOF
	v8/repo-tools.sh [fetch_depot_tools|fetch_v8_source|run_final_sync]
	USAGE:
		v8/repo-tools.sh fetch_depot_tools
			Clone the chromium project via Git

		v8/repo-tools.sh fetch_v8_source
			Git fetch the v8 codebase, run first GClient sync, and 
			configure the project settings to control what parts get
			"sync"ed in the next step

		v8/repo-tools.sh run_final_sync
			run final GClient sync with modified settings
EOF
fi
