#!/usr/bin/env sh

fetch_depot_tools() {
	maybe-verbose git clone --depth=10 \
		--branch=main https://chromium.googlesource.com/chromium/tools/depot_tools.git
	cd depot_tools
	maybe-verbose git checkout remotes/origin/main
	popd
}

fetch_v8_source() {
	# reference - https://stackoverflow.com/a/47093174/30007
	mkdir v8

	cd v8
	git init . &&
		maybe-verbose git fetch https://chromium.googlesource.com/v8/v8.git +refs/tags/${JAVET_V8_VERSION}:v8_${JAVET_V8_VERSION} \
			--depth 1
	maybe-verbose git checkout tags/${JAVET_V8_VERSION}
	cd ..

	export PATH=$PWD/depot_tools:$PATH

	maybe-verbose gclient root
	gclient config --spec 'solutions = [{"name": "v8","url": "https://chromium.googlesource.com/v8/v8.git","deps_file": "DEPS","managed": False,"custom_deps": {},},]'
	maybe-verbose gclient sync --no-history
	maybe-verbose gclient runhooks

	cd v8
	sed -i 's/snapcraft/nosnapcraft/g' ./build/install-build-deps.sh
	maybe-verbose ./build/install-build-deps.sh
	sed -i 's/nosnapcraft/snapcraft/g' ./build/install-build-deps.sh
	cd ..

	echo "V8 source fetched"
}

run_final_sync() {
	cp -r -f /google-temp /google

	export PATH=$PWD/depot_tools:$PATH

	cd google
	maybe-verbose gclient sync --no-history
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
fi
