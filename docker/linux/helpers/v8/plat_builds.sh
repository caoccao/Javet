#!/usr/bin/env sh

. normalized-ids.sh

run_platform_builds() {
	local v8_args="v8_monolithic=true v8_use_external_startup_data=false \
		is_component_build=false v8_enable_i18n_support=false \
		v8_enable_pointer_compression=false v8_static_library=true \
		symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false"

	local v8_flg_os="target_os"
	local v8_flg_cpu="target_cpu"
	local v8_flg_v8cpu="v8_target_os"
	local _qt='"'

	local plat_release
	local plat_cpu
	for target in $(normalize-targets.sh reduced); do
		plat_release=$(/cmds/v8/arch-build-alias.sh v8_release $target)
		plat_cpu=$(/cmds/v8/arch-build-alias.sh v8_flg_cpu $target)

		if [ ! $target = "$amd64_normalized" ]; then
			v8_args="$v8_flg_cpu=${_qt}$plat_cpu${_qt} $v8_flg_v8cpu=${_qt}$plat_cpu${_qt} $v8_args"
		fi

		[ "${ANDROID}" = "noandroid" ] ||
			v8_args="$v8_flg_os=${_qt}android${_qt} $v8_args"

		python3 tools/dev/v8gen.py ${plat_release}.release -- $v8_args
		maybe-verbose.sh ninja -C out.gn/${plat_release}.release v8_monolith ||
			python3 patch_v8_build.py -p ./
		maybe-verbose.sh ninja -C out.gn/${plat_release}.release v8_monolith
		rm patch_v8_build.py
	done
}

if [ "$1" = "run_platform_builds" ]; then
	shift
	run_platform_builds "$@"
else
	cat <<EOF
	v8/plat_builds.sh [run_platform_builds]
	USAGE:
		v8/plat_builds.sh run_platform_builds
			Run build pipeline for each platform specified in
			the envirionment variable that's read and normalized
			by the "normalize-targets.sh" script
EOF
fi
