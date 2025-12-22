/*
 * Copyright (c) 2021-2025. caoccao.com Sam Cao
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Unified JNI build script for Javet
 *
 * Usage:
 *   deno run build --os <os> --arch <arch> [options]
 *   Or: deno run --allow-all jni-build.ts --os <os> --arch <arch> [options]
 *   Or: deno run -A jni-build.ts --os <os> --arch <arch> [options]
 *
 * Examples:
 *   # V8 builds
 *   deno run build --os linux --arch x86_64 --v8-dir ${HOME}/v8
 *   deno run build --os linux --arch arm64 --v8-dir ${HOME}/v8
 *   deno run build --os macos --arch arm64 --v8-dir ${HOME}/v8
 *   deno run build --os windows --arch x86_64 --v8-dir C:\\v8
 *   deno run build --os android --arch arm64 --v8-dir ${HOME}/v8 --android-ndk ${HOME}/android
 *
 *   # Node builds
 *   deno run build --os linux --arch x86_64 --node-dir ${HOME}/node
 *
 *   # With custom CPU count
 *   deno run build --os linux --arch x86_64 --v8-dir ${HOME}/v8 --cpu-count 8
 *
 *   # With logging enabled
 *   deno run build --os linux --arch x86_64 --v8-dir ${HOME}/v8 --log-debug --log-error
 *
 *   # With i18n enabled
 *   deno run build --os linux --arch x86_64 --v8-dir ${HOME}/v8 --i18n
 */

import * as cli from "@std/cli";
import * as path from "@std/path";

const JAVET_VERSION = "5.0.3";

// Calculate script directory and project root
const SCRIPT_DIR = path.dirname(path.fromFileUrl(import.meta.url));
const PROJECT_ROOT = path.join(SCRIPT_DIR, "..");

enum OS {
  Linux = "linux",
  MacOS = "macos",
  Windows = "windows",
  Android = "android",
}

enum Arch {
  X86_64 = "x86_64",
  ARM64 = "arm64",
  ARM = "arm",
  X86 = "x86",
}

interface BuildConfig {
  os: OS;
  arch: Arch;
  i18n: boolean;
  v8Dir: string;
  nodeDir: string;
  androidNdk: string;
  cpuCount?: number;
  logDebug: boolean;
  logError: boolean;
  logInfo: boolean;
  logTrace: boolean;
}

function parseArgs(): BuildConfig {
  const parsed = cli.parseArgs(Deno.args, {
    string: ["os", "arch", "v8-dir", "node-dir", "android-ndk", "cpu-count"],
    boolean: ["i18n", "log-debug", "log-error", "log-info", "log-trace"],
    default: {
      "i18n": false,
      "v8-dir": "",
      "node-dir": "",
      "android-ndk": "",
      "cpu-count": undefined,
      "log-debug": false,
      "log-error": false,
      "log-info": false,
      "log-trace": false,
    },
  });

  const osStr = parsed.os;
  const archStr = parsed.arch;

  if (!osStr || !archStr) {
    console.error("Error: Both --os and --arch arguments are required");
    console.error("\nUsage: jni-build.ts --os <os> --arch <arch> [options]");
    console.error("\nRequired arguments:");
    console.error("  --os <os>           Target OS: linux, macos, windows, android");
    console.error("  --arch <arch>       Target architecture: x86_64, arm64, arm, x86");
    console.error("\nOptional arguments:");
    console.error("  --i18n              Enable V8 internationalization support (default: false)");
    console.error("  --v8-dir <path>     Path to V8 library directory");
    console.error("  --node-dir <path>   Path to Node.js library directory");
    console.error("  --android-ndk <path> Path to Android NDK (required for Android builds)");
    console.error("  --cpu-count <n>     Number of CPU cores to use for parallel builds (default: auto-detect)");
    console.error("  --log-debug         Enable debug logging (default: false)");
    console.error("  --log-error         Enable error logging (default: false)");
    console.error("  --log-info          Enable info logging (default: false)");
    console.error("  --log-trace         Enable trace logging (default: false)");
    Deno.exit(1);
  }

  // Validate and convert OS
  const os = Object.values(OS).find((o) => o === osStr);
  if (!os) {
    console.error(`Error: Invalid OS '${osStr}'`);
    console.error(`Supported OS: ${Object.values(OS).join(", ")}`);
    Deno.exit(1);
  }

  // Validate and convert Arch
  const arch = Object.values(Arch).find((a) => a === archStr);
  if (!arch) {
    console.error(`Error: Invalid architecture '${archStr}'`);
    console.error(`Supported architectures: ${Object.values(Arch).join(", ")}`);
    Deno.exit(1);
  }

  const v8Dir = parsed["v8-dir"];
  const nodeDir = parsed["node-dir"];

  // Validate v8-dir and node-dir are mutually exclusive
  if (!v8Dir && !nodeDir) {
    console.error("Error: Either --v8-dir or --node-dir must be specified");
    Deno.exit(1);
  }

  if (v8Dir && nodeDir) {
    console.error("Error: --v8-dir and --node-dir cannot be specified together");
    Deno.exit(1);
  }

  // Parse and validate cpu-count if provided
  let cpuCount: number | undefined = undefined;
  const cpuCountStr = parsed["cpu-count"];
  if (cpuCountStr) {
    cpuCount = parseInt(cpuCountStr, 10);
    if (isNaN(cpuCount) || cpuCount < 1) {
      console.error(`Error: Invalid cpu-count '${cpuCountStr}'. Must be a positive integer.`);
      Deno.exit(1);
    }
  }

  // Validate Android NDK is provided for Android builds
  const androidNdk = parsed["android-ndk"];
  if (os === OS.Android && !androidNdk) {
    console.error("Error: --android-ndk is required for Android builds");
    Deno.exit(1);
  }

  return {
    os: os as OS,
    arch: arch as Arch,
    i18n: parsed["i18n"],
    v8Dir,
    nodeDir,
    androidNdk,
    cpuCount,
    logDebug: parsed["log-debug"],
    logError: parsed["log-error"],
    logInfo: parsed["log-info"],
    logTrace: parsed["log-trace"],
  };
}

function buildCMakeArgs(config: BuildConfig): string[] {
  const args: string[] = [];

  // Add V8 or Node directory
  if (config.v8Dir) {
    args.push(`-DV8_DIR=${config.v8Dir}`);
  }
  if (config.nodeDir) {
    args.push(`-DNODE_DIR=${config.nodeDir}`);
  }

  // Add i18n flag
  if (config.i18n) {
    args.push("-DV8_ENABLE_I18N=1");
  }

  // Add logging flags
  if (config.logDebug) {
    args.push("-DJAVET_DEBUG=1");
  }
  if (config.logError) {
    args.push("-DJAVET_ERROR=1");
  }
  if (config.logInfo) {
    args.push("-DJAVET_INFO=1");
  }
  if (config.logTrace) {
    args.push("-DJAVET_TRACE=1");
  }

  return args;
}

async function runCommand(cmd: string[]): Promise<boolean> {
  const command = new Deno.Command(cmd[0], {
    args: cmd.slice(1),
    stdout: "inherit",
    stderr: "inherit",
  });

  const { code } = await command.output();
  return code === 0;
}

async function getCpuCount(config: BuildConfig): Promise<number> {
  // Use override if provided
  if (config.cpuCount !== undefined) {
    return config.cpuCount;
  }

  if (Deno.build.os === "windows") {
    return navigator.hardwareConcurrency || 4;
  }

  try {
    const command = new Deno.Command("nproc", {
      stdout: "piped",
    });
    const { code, stdout } = await command.output();

    if (code === 0) {
      const output = new TextDecoder().decode(stdout).trim();
      return parseInt(output) || 4;
    }
  } catch {
    // nproc not available, fallback
  }

  return navigator.hardwareConcurrency || 4;
}

async function removeDir(path: string) {
  try {
    await Deno.remove(path, { recursive: true });
  } catch (error) {
    if (!(error instanceof Deno.errors.NotFound)) {
      throw error;
    }
  }
}

async function ensureDir(path: string) {
  try {
    await Deno.mkdir(path, { recursive: true });
  } catch (error) {
    if (!(error instanceof Deno.errors.AlreadyExists)) {
      throw error;
    }
  }
}

function getLibraryFileName(config: BuildConfig): string {
  // Determine engine type
  const engine = config.v8Dir ? "v8" : "node";

  // Determine file extension
  let extension: string;
  switch (config.os) {
    case OS.Linux:
    case OS.Android:
      extension = "so";
      break;
    case OS.MacOS:
      extension = "dylib";
      break;
    case OS.Windows:
      extension = "dll";
      break;
  }

  // Build i18n suffix
  const i18nSuffix = config.i18n ? "-i18n" : "";

  // Build complete filename
  return `libjavet-${engine}-${config.os}-${config.arch}${i18nSuffix}.v.${JAVET_VERSION}.${extension}`;
}

async function buildLinux(config: BuildConfig): Promise<boolean> {
  const buildDir = path.join(SCRIPT_DIR, `build_${config.os}_${config.arch}`);
  const buildLibsDir = path.join(PROJECT_ROOT, "build", "libs");
  const resourcesDir = path.join(PROJECT_ROOT, "src", "main", "resources");

  console.log(`Building for Linux ${config.arch}...`);

  await removeDir(buildDir);
  await ensureDir(buildDir);
  await ensureDir(buildLibsDir);

  // Change to build directory
  const originalDir = Deno.cwd();
  Deno.chdir(buildDir);

  try {
    const cpuCount = await getCpuCount(config);
    const cmakeArgs = buildCMakeArgs(config);

    // Run cmake
    const cmakeCmd = [
      "cmake",
      SCRIPT_DIR,
      `-DJAVET_VERSION=${JAVET_VERSION}`,
      ...cmakeArgs,
    ];

    console.log(`Running: ${cmakeCmd.join(" ")}`);
    if (!await runCommand(cmakeCmd)) {
      return false;
    }

    // Run make
    const makeCmd = ["make", "-j", cpuCount.toString()];
    console.log(`Running: ${makeCmd.join(" ")}`);
    if (!await runCommand(makeCmd)) {
      return false;
    }

    const libraryPath = path.join(resourcesDir, getLibraryFileName(config));

    // Run execstack for x86_64
    if (config.arch === Arch.X86_64) {
      const execstackCmd = [
        "execstack",
        "-c",
        libraryPath,
      ];
      console.log(`Running: ${execstackCmd.join(" ")}`);
      if (!await runCommand(execstackCmd)) {
        console.warn("Warning: execstack command failed, continuing anyway...");
      }
    }

    // Run strip
    const stripCmd = [
      "strip",
      "--strip-unneeded",
      "-R", ".note",
      "-R", ".comment",
      libraryPath,
    ];
    console.log(`Running: ${stripCmd.join(" ")}`);
    if (!await runCommand(stripCmd)) {
      console.warn("Warning: strip command failed, continuing anyway...");
    }

    // Copy .a files
    console.log(`Copying static libraries to ${buildLibsDir}`);
    for await (const entry of Deno.readDir(".")) {
      if (entry.isFile && entry.name.endsWith(".a")) {
        await Deno.copyFile(entry.name, path.join(buildLibsDir, entry.name));
      }
    }

    console.log(`\n✓ Generated library: ${getLibraryFileName(config)}`);
    return true;
  } finally {
    Deno.chdir(originalDir);
  }
}

async function buildMacOS(config: BuildConfig): Promise<boolean> {
  const buildDir = path.join(SCRIPT_DIR, `build_${config.os}_${config.arch}`);
  const buildLibsDir = path.join(PROJECT_ROOT, "build", "libs");

  console.log("Building for macOS...");

  await removeDir(buildDir);
  await ensureDir(buildDir);
  await ensureDir(buildLibsDir);

  const originalDir = Deno.cwd();
  Deno.chdir(buildDir);

  try {
    const cpuCount = await getCpuCount(config);
    const cmakeArgs = buildCMakeArgs(config);

    // Run cmake
    const cmakeCmd = [
      "cmake",
      SCRIPT_DIR,
      `-DJAVET_VERSION=${JAVET_VERSION}`,
      ...cmakeArgs,
    ];

    console.log(`Running: ${cmakeCmd.join(" ")}`);
    if (!await runCommand(cmakeCmd)) {
      return false;
    }

    // Run make
    const makeCmd = ["make", "-j", cpuCount.toString()];
    console.log(`Running: ${makeCmd.join(" ")}`);
    if (!await runCommand(makeCmd)) {
      return false;
    }

    // Copy .a files
    console.log(`Copying static libraries to ${buildLibsDir}`);
    for await (const entry of Deno.readDir(".")) {
      if (entry.isFile && entry.name.endsWith(".a")) {
        await Deno.copyFile(entry.name, path.join(buildLibsDir, entry.name));
      }
    }

    console.log(`\n✓ Generated library: ${getLibraryFileName(config)}`);
    return true;
  } finally {
    Deno.chdir(originalDir);
  }
}

async function buildWindows(config: BuildConfig): Promise<boolean> {
  const buildDir = path.join(SCRIPT_DIR, `build_${config.os}_${config.arch}`);
  const buildLibsDir = path.join(PROJECT_ROOT, "build", "libs");

  console.log("Building for Windows...");

  await removeDir(buildDir);
  await ensureDir(buildDir);
  await ensureDir(buildLibsDir);

  const originalDir = Deno.cwd();
  Deno.chdir(buildDir);

  try {
    const cmakeArgs = buildCMakeArgs(config);

    // Run cmake with Visual Studio generator
    const cmakeCmd = [
      "cmake",
      SCRIPT_DIR,
      "-G", "Visual Studio 17 2022",
      "-A", "x64",
      `-DJAVET_VERSION=${JAVET_VERSION}`,
      ...cmakeArgs,
    ];

    console.log(`Running: ${cmakeCmd.join(" ")}`);
    if (!await runCommand(cmakeCmd)) {
      return false;
    }

    // Run cmake --build
    const buildCmd = [
      "cmake",
      "--build",
      ".",
      "--",
      "/p:CharacterSet=Unicode",
      "/p:Configuration=Release",
      "/p:Platform=x64",
    ];

    console.log(`Running: ${buildCmd.join(" ")}`);
    if (!await runCommand(buildCmd)) {
      return false;
    }

    // Copy .lib files
    console.log(`Copying static libraries to ${buildLibsDir}`);
    for await (const entry of Deno.readDir("Release")) {
      if (entry.isFile && entry.name.endsWith(".lib")) {
        await Deno.copyFile(`Release/${entry.name}`, path.join(buildLibsDir, entry.name));
      }
    }

    console.log(`\n✓ Generated library: ${getLibraryFileName(config)}`);
    return true;
  } finally {
    Deno.chdir(originalDir);
  }
}

async function buildAndroid(config: BuildConfig): Promise<boolean> {
  const buildDir = path.join(SCRIPT_DIR, `build_${config.os}_${config.arch}`);
  const buildLibsDir = path.join(PROJECT_ROOT, "build", "libs");

  console.log(`Building for Android ${config.arch}...`);

  await removeDir(buildDir);
  await ensureDir(buildDir);
  await ensureDir(buildLibsDir);

  const originalDir = Deno.cwd();
  Deno.chdir(buildDir);

  try {
    const cpuCount = await getCpuCount(config);
    const cmakeArgs = buildCMakeArgs(config);

    // Build Android-specific cmake arguments
    const androidArgs = [
      "-DCMAKE_SYSTEM_NAME=Android",
      `-DCMAKE_ANDROID_ARCH=${config.arch}`,
    ];

    // Add Android NDK path if provided
    if (config.androidNdk) {
      androidArgs.push(`-DCMAKE_ANDROID_NDK=${config.androidNdk}`);
    }

    // Run cmake with Android settings
    const cmakeCmd = [
      "cmake",
      SCRIPT_DIR,
      ...androidArgs,
      `-DJAVET_VERSION=${JAVET_VERSION}`,
      ...cmakeArgs,
    ];

    console.log(`Running: ${cmakeCmd.join(" ")}`);
    if (!await runCommand(cmakeCmd)) {
      return false;
    }

    // Run make
    const makeCmd = ["make", "-j", cpuCount.toString()];
    console.log(`Running: ${makeCmd.join(" ")}`);
    if (!await runCommand(makeCmd)) {
      return false;
    }

    // Copy .a files
    console.log(`Copying static libraries to ${buildLibsDir}`);
    for await (const entry of Deno.readDir(".")) {
      if (entry.isFile && entry.name.endsWith(".a")) {
        await Deno.copyFile(entry.name, path.join(buildLibsDir, entry.name));
      }
    }

    console.log(`\n✓ Generated library: ${getLibraryFileName(config)}`);
    return true;
  } finally {
    Deno.chdir(originalDir);
  }
}

async function main() {
  const config = parseArgs();

  console.log(`Build configuration:`);
  console.log(`  OS: ${config.os}`);
  console.log(`  Architecture: ${config.arch}`);
  console.log(`  Javet version: ${JAVET_VERSION}`);
  if (config.v8Dir) {
    console.log(`  V8 directory: ${config.v8Dir}`);
  }
  if (config.nodeDir) {
    console.log(`  Node directory: ${config.nodeDir}`);
  }
  if (config.androidNdk) {
    console.log(`  Android NDK: ${config.androidNdk}`);
  }
  if (config.cpuCount !== undefined) {
    console.log(`  CPU count: ${config.cpuCount}`);
  } else {
    console.log(`  CPU count: auto-detect`);
  }
  console.log(`  I18N: ${config.i18n}`);
  console.log(`  Log debug: ${config.logDebug}`);
  console.log(`  Log error: ${config.logError}`);
  console.log(`  Log info: ${config.logInfo}`);
  console.log(`  Log trace: ${config.logTrace}`);
  console.log();

  let success = false;

  switch (config.os) {
    case OS.Linux:
      success = await buildLinux(config);
      break;
    case OS.MacOS:
      success = await buildMacOS(config);
      break;
    case OS.Windows:
      success = await buildWindows(config);
      break;
    case OS.Android:
      success = await buildAndroid(config);
      break;
    default:
      console.error(`Error: Unsupported OS '${config.os}'`);
      console.error(`Supported OS: ${Object.values(OS).join(", ")}`);
      Deno.exit(1);
  }

  if (success) {
    console.log("\n✓ Build Completed");
    Deno.exit(0);
  } else {
    console.error("\n✗ Build Failed");
    Deno.exit(1);
  }
}

if (import.meta.main) {
  main();
}
