#!/usr/bin/env sh

. normalized-ids.sh

run_platform_builds() {

	# Temporary fix for Nodejs arm builds to bypass missing checks
	# for support for the specific flag and its variations.
	# See issue: nodejs/node#42888
	# Ultimately the conclusion was to update this flag for newer gcc versions
	# and remove in older ones
	python3 /cmds/nodejs/arm-patch.py

	local cc_target
	local prefix='./'
	local config_args
	for target in $(/cmds/utils/normalize-targets.sh reduced); do
		cc_target=$(/cmds/nodejs/arch-build-alias.sh node_make_dest_cpu $target)
		if [ $target = "$amd64_normalized" ]; then
			prefix="./"
		else
			prefix="./$cc_target/"
		fi

		python3 patch_node_build.py -p $prefix

		case "$target" in
		"$amd64_normalized")
			./configure --enable-static --without-intl --dest-cpu="$cc_target" --dest-os=linux
			;;
		"$ia32_normalized")
			./configure --enable-static --without-intl --prefix="$cc_target" --dest-cpu="$cc_target" --cross-compiling --dest-os=linux
			;;
		"$arm64_normalized")
			CC=aarch64-linux-gnu-gcc CXX=aarch64-linux-gnu-g++ CC_host="gcc" CXX_host="g++" \
				./configure --enable-static --without-intl --prefix="$cc_target" --dest-cpu="$cc_target" --cross-compiling --dest-os=linux \
				--with-arm-float-abi=hard
			;;
		"$arm_normalized")
			CC="arm-rpi-linux-gnueabihf-gcc -march=armv7-a" \
				CXX="arm-rpi-linux-gnueabihf-g++ -march=armv7-a" \
				CC_host="gcc -m32" CXX_host="g++ -m32" \
				./configure --enable-static --without-intl --prefix="$cc_target" --dest-cpu="$cc_target" --cross-compiling --dest-os=linux \
				--with-arm-float-abi=hard
			;;
			#"$riscv64_normalized")
			#	./configure --enable-static --without-intl --prefix="$cc_target" --dest-cpu="$cc_target" --cross-compiling --dest-os=linux
			#	;;
		esac

		python3 patch_node_build.py -p $prefix
		rm patch_node_build.py
		maybe-verbose.sh make -j4
	done
}

if [ "$1" = "run_platform_builds" ]; then
	shift
	run_platform_builds "$@"
else
	cat <<EOF
	nodejs/plat_builds.sh [run_platform_builds]
	USAGE:
		nodejs/plat_builds.sh run_platform_builds
			Run build pipeline for each platform specified in
			the envirionment variable that's read and normalized
			by the "normalize-targets.sh" script
EOF
fi
