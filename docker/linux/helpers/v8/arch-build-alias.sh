#!/usr/bin/env sh
arch="$(arch)"
if [ $arch = "aarch64" ]; then
	echo "arm64"
elif [ $arch = "x86_64" ]; then
	echo "x64"
elif [ $arch = "riscv64" ]; then
	echo "riscv64"
elif [ $arch = "armv7l" ]; then
	echo "arm"
fi
