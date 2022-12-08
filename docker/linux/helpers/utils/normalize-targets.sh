#!/usr/bin/env bash

. normalized-ids.sh

contains() {
	local target="$1"
	shift
	local arr="$@"
	[[ "$arr" =~ (^|[[:space:]])$target($|[[:space:]]) ]]
}

reduced() {
	local amd64_aliases="amd64 x86_64 x64"
	local arm64_aliases="arm64 aarch64 armv8-a armv8-m armv8-r"
	#local ia32_aliases="x86 ia32"
	local arm_aliases="arm armv7 armv7l"

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
		#elif contains $target "$ia32_aliases"; then
		#	(! contains "$ia32_normalized" "${lTargets[@]}") &&
		#		lTargets+=("$ia32_normalized")
		#	continue
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
	contains $1 "$reduced_t"
}

has_only() {
	local reduced_t="$(reduced)"
	[ $1 = "$reduced_t" ]
}

if [ "$1" = "reduced" ]; then
	shift
	reduced
elif [ "$1" = "has_kind" ]; then
	shift
	has_kind "$@"
elif [ "$1" = "has_only" ]; then
	shift
	has_only "$@"
elif [ "$1" = "silent" ]; then
	shift
else
	cat <<EOF
	normalize-targets.sh [reduced|has_kind|has_only]
	USAGE:
		normalize-targets.sh reduced
			Parse the [TARGETS] environment variable and return
			a deduplicated list of just cpu archetecture names
			used and supported internally

		normalize-targets.sh has_kind [target]
			Return or fail if the reduced target list includes the
			specified target

		normalize-targets.sh has_only [target]
			Retrun or fail depending on whether there is only the
			specified target in the list
EOF
fi
