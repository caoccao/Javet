#!/usr/bin/env sh

. normalized-ids.sh

v8_release() {
	case "$1" in
	"$amd64_normalized")
		echo "x64"
		;;
	"$ia32_normalized")
		echo "ia32"
		;;
	"$arm64_normalized")
		echo "arm64"
		;;
	"$arm_normalized")
		echo "arm"
		;;
		#"$riscv64_normalized")
		#	echo "riscv64"
		#	;;
	esac
}

v8_flg_cpu() {
	case "$1" in
	"$amd64_normalized")
		echo "x64"
		;;
	"$ia32_normalized")
		echo "x86"
		;;
	"$arm64_normalized")
		echo "arm64"
		;;
	"$arm_normalized")
		echo "arm"
		;;
		#"$riscv64_normalized")
		#	echo "riscv64"
		#	;;
	esac
}

if [ "$1" = "v8_release" ]; then
	shift
	v8_release "$@"
elif [ "$1" = "v8_flg_cpu" ]; then
	shift
	v8_flg_cpu "$@"
else
	cat <<EOF
	v8/arch-build-alias.sh [v8_release|v8_flg_cpu] [target]
	USAGE:
		v8/arch-build-alias.sh v8_release target
			ensure target name/id is one of the aliases that
			the v8 python build scripts support

		v8/arch-build-alias.sh v8_flg_cpu target
			ensure target name/id is one of the aliases that
			the v8 build tools flags support
EOF
fi
