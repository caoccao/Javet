/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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
import { green, red, yellow } from "@std/fmt/colors";
import * as path from "@std/path";

const JAVET_VERSION = "6.0.0";

// Calculate script directory and project root
const SCRIPT_DIR = path.dirname(path.fromFileUrl(import.meta.url));
const PROJECT_ROOT = path.join(SCRIPT_DIR, "..");
const BUILD_LIBS_DIR = path.join(PROJECT_ROOT, "build", "libs");
const RESOURCES_DIR = path.join(PROJECT_ROOT, "src", "main", "resources");

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
  clean: boolean;
  logDebug: boolean;
  logError: boolean;
  logInfo: boolean;
  logTrace: boolean;
}

function parseArgs(): BuildConfig {
  const parsed = cli.parseArgs(Deno.args, {
    string: ["os", "arch", "v8-dir", "node-dir", "android-ndk", "cpu-count"],
    boolean: ["i18n", "clean", "log-debug", "log-error", "log-info", "log-trace"],
    default: {
      "i18n": false,
      "clean": false,
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
    console.info("Error: Both --os and --arch arguments are required");
    console.info("\nUsage: jni-build.ts --os <os> --arch <arch> [options]");
    console.info("\nRequired arguments:");
    console.info("  --os <os>           Target OS: linux, macos, windows, android");
    console.info("  --arch <arch>       Target architecture: x86_64, arm64, arm, x86");
    console.info("\nOptional arguments:");
    console.info("  --i18n              Enable V8 internationalization support (default: false)");
    console.info("  --clean             Clean build directory before building (default: false)");
    console.info("  --v8-dir <path>     Path to V8 library directory");
    console.info("  --node-dir <path>   Path to Node.js library directory");
    console.info("  --android-ndk <path> Path to Android NDK (required for Android builds)");
    console.info("  --cpu-count <n>     Number of CPU cores to use for parallel builds (default: auto-detect)");
    console.info("  --log-debug         Enable debug logging (default: false)");
    console.info("  --log-error         Enable error logging (default: false)");
    console.info("  --log-info          Enable info logging (default: false)");
    console.info("  --log-trace         Enable trace logging (default: false)");
    Deno.exit(1);
  }

  // Validate and convert OS
  const os = Object.values(OS).find((o) => o === osStr);
  if (!os) {
    console.error(red(`Error: Invalid OS '${osStr}'`));
    console.error(red(`Supported OS: ${Object.values(OS).join(", ")}`));
    Deno.exit(1);
  }

  // Validate and convert Arch
  const arch = Object.values(Arch).find((a) => a === archStr);
  if (!arch) {
    console.error(red(`Error: Invalid architecture '${archStr}'`));
    console.error(red(`Supported architectures: ${Object.values(Arch).join(", ")}`));
    Deno.exit(1);
  }

  const v8Dir = parsed["v8-dir"];
  const nodeDir = parsed["node-dir"];

  // Validate v8-dir and node-dir are mutually exclusive
  if (!v8Dir && !nodeDir) {
    console.error(red("Error: Either --v8-dir or --node-dir must be specified"));
    Deno.exit(1);
  }

  if (v8Dir && nodeDir) {
    console.error(red("Error: --v8-dir and --node-dir cannot be specified together"));
    Deno.exit(1);
  }

  // Parse and validate cpu-count if provided
  let cpuCount: number | undefined = undefined;
  const cpuCountStr = parsed["cpu-count"];
  if (cpuCountStr) {
    cpuCount = parseInt(cpuCountStr, 10);
    if (isNaN(cpuCount) || cpuCount < 1) {
      console.error(red(`Error: Invalid cpu-count '${cpuCountStr}'. Must be a positive integer.`));
      Deno.exit(1);
    }
  }

  // Validate Android NDK is provided for Android builds
  const androidNdk = parsed["android-ndk"];
  if (os === OS.Android && !androidNdk) {
    console.error(red("Error: --android-ndk is required for Android builds"));
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
    clean: parsed["clean"],
    logDebug: parsed["log-debug"],
    logError: parsed["log-error"],
    logInfo: parsed["log-info"],
    logTrace: parsed["log-trace"],
  };
}

/**
 * CMake re-parses some paths as CMake code. `CMAKE_C_COMPILER` for instance is
 * written verbatim into `CMakeFiles/<version>/CMakeCCompiler.cmake`, where a
 * backslash starts an escape sequence, so a Windows path like
 * `D:\a\Javet\google\v8` fails with `Invalid character escape '\a'`.
 * CMake accepts forward slashes on every platform, so normalize every path
 * before handing it to cmake.
 */
function toCMakePath(filePath: string): string {
  return filePath.replaceAll("\\", "/");
}

function buildCMakeArgs(config: BuildConfig): string[] {
  const args: string[] = [];

  // Add V8 or Node directory
  if (config.v8Dir) {
    args.push(`-DV8_DIR=${toCMakePath(config.v8Dir)}`);
  }
  if (config.nodeDir) {
    args.push(`-DNODE_DIR=${toCMakePath(config.nodeDir)}`);
  }

  // Add i18n flag
  if (config.i18n) {
    args.push("-DENABLE_I18N=1");
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

function getBuildDir(config: BuildConfig): string {
  const i18n = config.i18n ? "i18n": "non-i18n";
  const type = config.v8Dir ? "v8": "node";
  return path.join(SCRIPT_DIR, `build-${type}-${config.os}-${config.arch}-${i18n}`);
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

async function prepareBuild(config: BuildConfig): Promise<void> {
  const buildDir = getBuildDir(config);
  if (config.clean) {
    await removeDir(buildDir);
  }
  await ensureDir(buildDir);
  await ensureDir(BUILD_LIBS_DIR);
}

async function buildLinux(config: BuildConfig): Promise<boolean> {
  console.log(`Building for Linux ${config.arch}...`);

  // Change to build directory
  const originalDir = Deno.cwd();
  Deno.chdir(getBuildDir(config));

  try {
    const cpuCount = await getCpuCount(config);
    const cmakeArgs = buildCMakeArgs(config);

    // Run cmake
    const cmakeCmd = [
      "cmake",
      toCMakePath(SCRIPT_DIR),
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

    const libraryPath = path.join(RESOURCES_DIR, getLibraryFileName(config));

    // Run execstack for x86_64
    if (config.arch === Arch.X86_64) {
      const execstackCmd = [
        "execstack",
        "-c",
        libraryPath,
      ];
      console.log(`Running: ${execstackCmd.join(" ")}`);
      if (!await runCommand(execstackCmd)) {
        console.warn(yellow("Warning: execstack command failed, continuing anyway..."));
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
      console.warn(yellow("Warning: strip command failed, continuing anyway..."));
    }

    // Verify shared library dependencies
    const lddCmd = ["ldd", libraryPath];
    console.log(`Running: ${lddCmd.join(" ")}`);
    const lddProcess = new Deno.Command(lddCmd[0], {
      args: lddCmd.slice(1),
      stdout: "piped",
      stderr: "inherit",
    });
    const lddResult = await lddProcess.output();
    if (lddResult.code !== 0) {
      console.error(red("Error: ldd command failed"));
      return false;
    }
    const lddOutput = new TextDecoder().decode(lddResult.stdout);
    const permittedDeps = [
      "linux-vdso.so",
      "librt.so",
      "libm.so",
      "libc.so",
      "ld-linux-aarch64.so",
      "ld-linux-x86-64.so",
      "libpthread.so",
    ];
    const unexpectedDeps: string[] = [];
    for (const line of lddOutput.split("\n")) {
      const trimmed = line.trim();
      if (!trimmed) {
        continue;
      }
      if (!permittedDeps.some((dep) => trimmed.includes(dep))) {
        unexpectedDeps.push(trimmed);
      }
    }
    if (unexpectedDeps.length > 0) {
      console.error(red("Error: Unexpected shared library dependencies found:"));
      for (const dep of unexpectedDeps) {
        console.error(red(`  ${dep}`));
      }
      return false;
    }

    // Copy .a files
    console.log(`Copying static libraries to ${BUILD_LIBS_DIR}`);
    for await (const entry of Deno.readDir(".")) {
      if (entry.isFile && entry.name.endsWith(".a")) {
        await Deno.copyFile(entry.name, path.join(BUILD_LIBS_DIR, entry.name));
      }
    }

    console.log(green(`\n✓ Generated library: ${getLibraryFileName(config)}`));
    return true;
  } finally {
    Deno.chdir(originalDir);
  }
}

async function buildMacOS(config: BuildConfig): Promise<boolean> {
  console.log("Building for macOS...");

  const originalDir = Deno.cwd();
  Deno.chdir(getBuildDir(config));

  try {
    const cpuCount = await getCpuCount(config);
    const cmakeArgs = buildCMakeArgs(config);

    // Run cmake
    const cmakeCmd = [
      "cmake",
      toCMakePath(SCRIPT_DIR),
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

    // Verify dynamic library dependencies
    const libraryPath = path.join(RESOURCES_DIR, getLibraryFileName(config));
    const dyldInfoCmd = ["dyld_info", "-dependents", libraryPath];
    console.log(`Running: ${dyldInfoCmd.join(" ")}`);
    const dyldInfoProcess = new Deno.Command(dyldInfoCmd[0], {
      args: dyldInfoCmd.slice(1),
      stdout: "piped",
      stderr: "inherit",
    });
    const dyldInfoResult = await dyldInfoProcess.output();
    if (dyldInfoResult.code !== 0) {
      console.error(red("Error: dyld_info command failed"));
      return false;
    }
    const dyldInfoOutput = new TextDecoder().decode(dyldInfoResult.stdout);
    const lines = dyldInfoOutput.split("\n");
    // Skip header lines until after "attributes     load path"
    const headerIndex = lines.findIndex((line) => line.trim().startsWith("attributes"));
    if (headerIndex === -1) {
      console.error(red("Error: Unexpected dyld_info output format"));
      return false;
    }
    const depLines = lines.slice(headerIndex + 1);
    const permittedDeps = [
      /^\/usr\/lib\/libc\+\+\.\d+\.dylib$/,
      /^\/usr\/lib\/libobjc\.\w+\.dylib$/,
      /^\/System\/Library\/Frameworks\/CoreFoundation\.framework\/Versions\/\w+\/CoreFoundation$/,
      /^\/System\/Library\/Frameworks\/Foundation\.framework\/Versions\/\w+\/Foundation$/,
      /^\/System\/Library\/Frameworks\/Security\.framework\/Versions\/\w+\/Security$/,
      /^\/usr\/lib\/libSystem\.\w+\.dylib$/,
    ];
    const unexpectedDeps: string[] = [];
    for (const line of depLines) {
      const trimmed = line.trim();
      if (!trimmed) {
        continue;
      }
      if (!permittedDeps.some((regex) => regex.test(trimmed))) {
        unexpectedDeps.push(trimmed);
      }
    }
    if (unexpectedDeps.length > 0) {
      console.error(red("Error: Unexpected dynamic library dependencies found:"));
      for (const dep of unexpectedDeps) {
        console.error(red(`  ${dep}`));
      }
      return false;
    }

    // Copy .a files
    console.log(`Copying static libraries to ${BUILD_LIBS_DIR}`);
    for await (const entry of Deno.readDir(".")) {
      if (entry.isFile && entry.name.endsWith(".a")) {
        await Deno.copyFile(entry.name, path.join(BUILD_LIBS_DIR, entry.name));
      }
    }

    console.log(green(`\n✓ Generated library: ${getLibraryFileName(config)}`));
    return true;
  } finally {
    Deno.chdir(originalDir);
  }
}

async function buildWindows(config: BuildConfig): Promise<boolean> {
  console.log("Building for Windows...");

  const originalDir = Deno.cwd();
  Deno.chdir(getBuildDir(config));

  try {
    const cmakeArgs = buildCMakeArgs(config);

    // V8 on Windows is built by V8's bundled clang-cl against Chromium's
    // custom libc++ (use_custom_libcxx = true), which puts std symbols in the
    // std::__Cr ABI namespace. Compile Javet with the same toolchain to match
    // the ABI. Node.js mode keeps MSVC's STL (matches Node's vcbuild.bat).
    if (config.v8Dir) {
      return await buildWindowsV8(config, cmakeArgs);
    } else {
      return await buildWindowsNode(config, cmakeArgs);
    }
  } finally {
    Deno.chdir(originalDir);
  }
}

async function importVcvars(): Promise<boolean> {
  // Locate Visual Studio via vswhere so we don't depend on the caller having
  // already run vcvars64.bat. We need vcvars's env (PATH for rc.exe/mt.exe,
  // LIB for kernel32.lib et al., INCLUDE for MSVC headers) even though V8's
  // clang-cl can auto-detect MSVC paths — lld-link and CMake's manifest
  // wrappers cannot.
  const programFilesX86 = Deno.env.get("ProgramFiles(x86)") ??
    "C:\\Program Files (x86)";
  const vswhereExe = path.join(
    programFilesX86,
    "Microsoft Visual Studio",
    "Installer",
    "vswhere.exe",
  );

  console.log(`Locating Visual Studio with: ${vswhereExe}`);
  const vswhere = new Deno.Command(vswhereExe, {
    args: [
      "-latest",
      "-products", "*",
      "-requires", "Microsoft.VisualStudio.Component.VC.Tools.x86.x64",
      "-property", "installationPath",
    ],
    stdout: "piped",
    stderr: "inherit",
  });
  const vswhereResult = await vswhere.output();
  if (vswhereResult.code !== 0) {
    console.error(red("vswhere.exe failed"));
    return false;
  }
  const vsInstallPath = new TextDecoder()
    .decode(vswhereResult.stdout)
    .split(/\r?\n/)[0]
    .trim();
  if (!vsInstallPath) {
    console.error(red("vswhere returned no Visual Studio installation"));
    return false;
  }
  const vcvars = path.join(
    vsInstallPath,
    "VC", "Auxiliary", "Build", "vcvars64.bat",
  );
  console.log(`Importing environment from: ${vcvars}`);

  // Marker isolates `set` output from vcvars64.bat's banner.
  // Deno mangles quotes when forwarding /c args to cmd.exe (the vcvars path
  // contains spaces), so write a temp .bat and run that instead.
  const marker = "__JAVET_VCVARS_BEGIN__";
  const tempBat = await Deno.makeTempFile({ suffix: ".bat" });
  let cmdResult;
  try {
    await Deno.writeTextFile(
      tempBat,
      `@echo off\r\ncall "${vcvars}" >nul\r\nif errorlevel 1 exit /b %errorlevel%\r\necho ${marker}\r\nset\r\n`,
    );
    const cmd = new Deno.Command("cmd.exe", {
      args: ["/c", tempBat],
      stdout: "piped",
      stderr: "inherit",
    });
    cmdResult = await cmd.output();
  } finally {
    try {
      await Deno.remove(tempBat);
    } catch {
      // Best-effort cleanup.
    }
  }
  if (cmdResult.code !== 0) {
    console.error(red(`vcvars64.bat failed with exit code ${cmdResult.code}`));
    return false;
  }
  const out = new TextDecoder().decode(cmdResult.stdout);
  const markerIdx = out.indexOf(marker);
  if (markerIdx < 0) {
    console.error(red("vcvars marker not found in cmd output"));
    return false;
  }
  let imported = 0;
  for (const line of out.slice(markerIdx + marker.length).split(/\r?\n/)) {
    const eq = line.indexOf("=");
    if (eq <= 0) continue;
    const name = line.slice(0, eq);
    const value = line.slice(eq + 1);
    // Skip env names Deno.env can't represent (e.g., "ProgramFiles(x86)").
    if (!/^[A-Za-z_][A-Za-z0-9_]*$/.test(name)) continue;
    try {
      Deno.env.set(name, value);
      imported++;
    } catch {
      // Ignore names Deno refuses.
    }
  }
  console.log(`Imported ${imported} environment variables from vcvars64.bat`);
  return true;
}

async function buildWindowsV8(config: BuildConfig, cmakeArgs: string[]): Promise<boolean> {
  const v8ClangBinDir = path.join(
    config.v8Dir,
    "third_party",
    "llvm-build",
    "Release+Asserts",
    "bin",
  );
  const clangCl = path.join(v8ClangBinDir, "clang-cl.exe");
  const lldLink = path.join(v8ClangBinDir, "lld-link.exe");
  const ninjaExe = path.join(config.v8Dir, "third_party", "ninja", "ninja.exe");

  const originalPath = Deno.env.get("PATH") ?? "";

  // Import vcvars first (sets PATH/INCLUDE/LIB), then prepend V8's LLVM bin
  // so lld-link.exe wins over MSVC's link.exe when cmake/clang-cl resolves
  // tools by name.
  if (!await importVcvars()) {
    return false;
  }
  Deno.env.set("PATH", `${v8ClangBinDir};${Deno.env.get("PATH") ?? ""}`);

  try {
    const cmakeCmd = [
      "cmake",
      toCMakePath(SCRIPT_DIR),
      "-G", "Ninja",
      "-DCMAKE_BUILD_TYPE=Release",
      `-DCMAKE_MAKE_PROGRAM=${toCMakePath(ninjaExe)}`,
      `-DCMAKE_C_COMPILER=${toCMakePath(clangCl)}`,
      `-DCMAKE_CXX_COMPILER=${toCMakePath(clangCl)}`,
      `-DCMAKE_LINKER=${toCMakePath(lldLink)}`,
      `-DJAVET_VERSION=${JAVET_VERSION}`,
      ...cmakeArgs,
    ];

    console.log(`Running: ${cmakeCmd.join(" ")}`);
    if (!await runCommand(cmakeCmd)) {
      return false;
    }

    const buildCmd = ["cmake", "--build", ".", "--config", "Release"];
    console.log(`Running: ${buildCmd.join(" ")}`);
    if (!await runCommand(buildCmd)) {
      return false;
    }

    // Ninja outputs at the build root (no Release\ subdir).
    console.log(`Copying static libraries to ${BUILD_LIBS_DIR}`);
    for await (const entry of Deno.readDir(".")) {
      if (entry.isFile && entry.name.endsWith(".lib")) {
        await Deno.copyFile(entry.name, path.join(BUILD_LIBS_DIR, entry.name));
      }
    }

    console.log(green(`\n✓ Generated library: ${getLibraryFileName(config)}`));
    return true;
  } finally {
    Deno.env.set("PATH", originalPath);
  }
}

async function buildWindowsNode(config: BuildConfig, cmakeArgs: string[]): Promise<boolean> {
  // Run cmake with Visual Studio generator
  const cmakeCmd = [
    "cmake",
    toCMakePath(SCRIPT_DIR),
    "-G", "Visual Studio 18 2026",
    "-A", "x64",
    `-DJAVET_VERSION=${JAVET_VERSION}`,
    "-T", "ClangCL",
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
  console.log(`Copying static libraries to ${BUILD_LIBS_DIR}`);
  for await (const entry of Deno.readDir("Release")) {
    if (entry.isFile && entry.name.endsWith(".lib")) {
      await Deno.copyFile(`Release/${entry.name}`, path.join(BUILD_LIBS_DIR, entry.name));
    }
  }

  console.log(green(`\n✓ Generated library: ${getLibraryFileName(config)}`));
  return true;
}

async function buildAndroid(config: BuildConfig): Promise<boolean> {
  console.log(`Building for Android ${config.arch}...`);

  const originalDir = Deno.cwd();
  Deno.chdir(getBuildDir(config));

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
      androidArgs.push(`-DCMAKE_ANDROID_NDK=${toCMakePath(config.androidNdk)}`);
    }

    // Run cmake with Android settings
    const cmakeCmd = [
      "cmake",
      toCMakePath(SCRIPT_DIR),
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
    console.log(`Copying static libraries to ${BUILD_LIBS_DIR}`);
    for await (const entry of Deno.readDir(".")) {
      if (entry.isFile && entry.name.endsWith(".a")) {
        await Deno.copyFile(entry.name, path.join(BUILD_LIBS_DIR, entry.name));
      }
    }

    console.log(green(`\n✓ Generated library: ${getLibraryFileName(config)}`));
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
  console.log(`  Clean build: ${config.clean}`);
  console.log(`  I18N: ${config.i18n}`);
  console.log(`  Log debug: ${config.logDebug}`);
  console.log(`  Log error: ${config.logError}`);
  console.log(`  Log info: ${config.logInfo}`);
  console.log(`  Log trace: ${config.logTrace}`);
  console.log();

  await prepareBuild(config);

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
      console.error(red(`Error: Unsupported OS '${config.os}'`));
      console.error(red(`Supported OS: ${Object.values(OS).join(", ")}`));
      Deno.exit(1);
  }

  if (success) {
    console.log(green("\n✓ Build Completed"));
    Deno.exit(0);
  } else {
    console.error(red("\n✗ Build Failed"));
    Deno.exit(1);
  }
}

if (import.meta.main) {
  main();
}
