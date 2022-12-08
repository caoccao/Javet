#!/usr/bin/env sh

. normalized-ids.sh

node_make_dest_cpu() {
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

if [ "$1" = "node_make_dest_cpu" ]; then
	shift
	node_make_dest_cpu "$@"
else
	cat <<EOF
	nodejs/arch-build-alias.sh [node_make_dest_cpu] [target]
	USAGE:
		nodejs/arch-build-alias.sh node_make_dest_cpu [target]
			ensure target name/id is one of the aliases that
			"make" supports
EOF
fi
