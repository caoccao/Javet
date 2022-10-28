#!/usr/bin/env bash

contains() {
	local target="$1"
	shift
	local arr="$@"
	[[ "$arr" =~ (^|[[:space:]])$target($|[[:space:]]) ]]
}

reduced() {
	local amd64_aliases="amd64 x86_64 x64"
	local arm64_aliases="arm64 aarch64 armv8-a armv8-m armv8-r"
	local ia32_aliases="x86 ia32"
	local arm_aliases="arm armv7 armv7l"

	local amd64_normalized="amd64"
	local arm64_normalized="arm64"
	local ia32_normalized="ia32"
	local arm_normalized="arm"

	local lTargets=()
	for target in $(echo "$TARGETS" | sed "s/,/ /g"); do
		if contains $target "$amd64_aliases"; then
			! contains "$amd64_normalized" "${lTargets[@]}" &&
				lTargets+=("$amd64_normalized")
			continue
		elif contains $target "$arm64_aliases"; then
			! contains "$arm64_normalized" "${lTargets[@]}" &&
				lTargets+=("$arm64_normalized")
			continue
		elif contains $target "$ia32_aliases"; then
			! contains "$ia32_normalized" "${lTargets[@]}" &&
				lTargets+=("$ia32_normalized")
			continue
		elif contains $target "$arm_aliases"; then
			! contains "$arm_normalized" "${lTargets[@]}" &&
				lTargets+=("$arm_normalized")
			continue
		else
			echo "Error: invalid target specified ( $target )" 1>&2
			exit 1
		fi
	done
	echo "${lTargets[@]}"
}

has_kind() {
	local reduced_t="$(reduced)"
	echo $1 "$reduced_t"
	contains $1 "$reduced_t"
}

if [ "$1" = "reduced" ]; then
	shift
	reduced
elif [ "$1" = "has_kind" ]; then
	shift
	has_kind "$@"
fi
