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

/*
 * This Deno TypeScript script patches the Node.js source tree for Javet.
 *
 * 1. Clone Node.js.
 * 2. Checkout to proper version.
 * 3. Run the script:
 *    `deno run --allow-read --allow-write patch_node_build.ts -p root_path_to_node_js --os <os> [--i18n]`
 * 4. Configure and build Node.js.
 *
 *    Linux   ./configure --enable-static --without-intl --v8-enable-temporal-support
 *            export CFLAGS="-fPIC -ftls-model=global-dynamic -Wno-return-type"
 *            export CXXFLAGS="${CFLAGS}"
 *            export LDFLAGS="${CFLAGS}"
 *            make -j4
 *
 *            GYP appends the flags to every target of the target toolset via
 *            `CFLAGS.target`, `CXXFLAGS.target` and `LDFLAGS.target`, so the
 *            generated make files no longer need to be patched for position
 *            independent code.
 *
 *    Mac OS  ./configure --enable-static --without-intl --v8-enable-temporal-support
 *            make -j4
 *
 *    Windows vcbuild.bat static without-intl v8temporal vs2026
 *
 *    Android ./android-configure <ndk> <sdk_version> <arch>
 *            make -j4
 *
 *            android-configure holds the configure flags, so `--i18n` decides
 *            which ICU the patched android_configure.py asks for. Every other
 *            OS takes the ICU choice on its own configure command line and
 *            ignores the flag.
 *
 * `--os` is the OS being built for, not the host: Android is cross compiled
 * from Linux, so it has to be told rather than detected.
 *
 * Every patch is idempotent: a patch whose result is already in the file is
 * reported as skipped, which keeps repeated runs (the Docker builds configure
 * Node.js more than once) safe.
 */

import * as cli from "@std/cli";
import { green, red } from "@std/fmt/colors";
import * as path from "@std/path";

enum OS {
  Linux = "linux",
  MacOS = "macos",
  Windows = "windows",
  Android = "android",
}

interface PatchConfig {
  i18n: boolean;
  nodeDir: string;
  os: OS;
}

/** A single verbatim text substitution inside one file. */
interface Replacement {
  /** The text to look for. It must appear exactly once unless `all` is set. */
  readonly from: string;
  /** The text to put in its place. */
  readonly to: string;
  /**
   * Text that is only in the file once the replacement has been made. It is
   * checked before `from`, so that a repeated run is a no-op. Replacements
   * that wrap or extend `from` rather than consume it leave `from` in place
   * and must set this, or the second run patches the file again. Defaults to
   * `to`, which is enough whenever `from` disappears.
   */
  readonly applied?: string;
  /**
   * Replace every occurrence instead of requiring exactly one, and treat no
   * occurrence as nothing left to do. For replacements whose result is too
   * plain to be recognized, such as dropping an attribute.
   */
  readonly all?: boolean;
}

interface Patch {
  /** Path of the file to patch, relative to the Node.js repository root. */
  readonly file: string;
  /** Why Javet needs it. */
  readonly reason: string;
  /** The target OS that need it. */
  readonly os: readonly OS[];
  /** Restrict the patch to the i18n build (true) or the non-i18n one (false). */
  readonly i18n?: boolean;
  readonly replacements: readonly Replacement[];
}

const ALL_OS: readonly OS[] = Object.values(OS);

/*
 * V8 reads zoneinfo64.res through ICU4C's udata when it has ICU, and links a
 * baked-in copy of that resource when it doesn't. Only V8's GN build knows how
 * to produce that copy (the make_temporal_zoneinfo_cpp action), so a gyp build
 * without ICU fails to link on zoneinfo64_static_data, and configure.py turns
 * Temporal off for --with-intl=none. Port the action so Temporal survives a
 * non-i18n build. The action is inert for an i18n build.
 * @see: deps/v8/BUILD.gn (make_temporal_zoneinfo_cpp)
 */
const V8_GYP_TEMPORAL_ZONEINFO: Patch = {
  file: "tools/v8_gypfiles/v8.gyp",
  reason: "Bake zoneinfo64.res in so that a non-i18n build keeps V8 Temporal",
  os: ALL_OS,
  replacements: [
    {
      from: `            ['node_shared_temporal_capi=="false"', {
              'dependencies': [
                '../../deps/crates/crates.gyp:temporal_capi',
              ],
            }],
          ],
`,
      to: `            ['node_shared_temporal_capi=="false"', {
              'dependencies': [
                '../../deps/crates/crates.gyp:temporal_capi',
              ],
            }],
            ['v8_enable_i18n_support==0', {
              'actions': [
                {
                  'action_name': 'make_temporal_zoneinfo_cpp',
                  'inputs': [
                    '../../deps/crates/vendor/zoneinfo64-v0_3/src/data/zoneinfo64.res',
                  ],
                  'outputs': [
                    '<(SHARED_INTERMEDIATE_DIR)/src/builtins/builtins-temporal-zoneinfo64-data.cc',
                  ],
                  'action': [
                    '<(python)',
                    '<(V8_ROOT)/tools/include-file-as-bytes.py',
                    '<@(_inputs)',
                    '<@(_outputs)',
                    'zoneinfo64_static_data',
                  ],
                },
              ],
              'sources': [
                '<(SHARED_INTERMEDIATE_DIR)/src/builtins/builtins-temporal-zoneinfo64-data.cc',
              ],
            }],
          ],
`,
      applied: `'action_name': 'make_temporal_zoneinfo_cpp',`,
    },
  ],
};

/*
 * All static Node.js libraries are <thin> libraries, which Javet cannot link.
 * The condition is written for SmartOS only, so widen it to every static
 * library.
 */
const COMMON_GYPI_STATIC_LIBRARY: Patch = {
  file: "common.gypi",
  reason: "Produce non-thin static libraries",
  os: [OS.Android, OS.Linux],
  replacements: [
    {
      from: `['_type=="static_library" and OS=="solaris"', {`,
      to: `['_type=="static_library"', {`,
    },
  ],
};

/*
 * Node.js v24.15.0 (PR #61010 / commit e0220f0c35c) auto-enables gdbjit on
 * Linux x64/ia32/ppc64. The resulting deps/v8/src/diagnostics/gdb-jit.cc does
 * not compile under GCC with V8 14.8, only under clang.
 */
const CONFIGURE_PY_GDBJIT: Patch = {
  file: "configure.py",
  reason: "Disable v8_enable_gdbjit, which does not compile under GCC",
  os: [OS.Linux],
  replacements: [
    {
      from: `o['variables']['v8_enable_gdbjit'] = 1 if is_gdbjit_supported_arch and is_linux else 0`,
      to: `o['variables']['v8_enable_gdbjit'] = 0`,
    },
  ],
};

/*
 * V8's thread local storage attribute makes the linker reject the shared
 * library with `relocation R_X86_64_TPOFF32 ... can not be used when making a
 * shared object`. Javet builds with -ftls-model=global-dynamic instead.
 */
const V8_TLS_MODEL: Patch = {
  file: "deps/v8/src/execution/isolate.h",
  reason: "Drop the V8_TLS_MODEL attribute so that -ftls-model wins",
  os: [OS.Linux],
  replacements: [
    {
      from: "__attribute__((tls_model(V8_TLS_MODEL)))",
      to: " ",
      all: true,
    },
  ],
};

const V8_TLS_MODEL_LOCAL_HEAP: Patch = {
  ...V8_TLS_MODEL,
  file: "deps/v8/src/heap/local-heap.h",
};

/*
 * The Android NDK toolchain only provides a cross compiler, so the host
 * toolchain has to be pointed at the system GCC. v8_target_arch has to be the
 * Node.js CPU name rather than the Android one, and Javet needs a static
 * non-i18n build.
 *
 * Temporal needs more than --v8-enable-temporal-support here. Its Rust half,
 * node_crates, is built by cargo, and configure.py only pins cargo_rust_target
 * for Windows and macOS x86-64, so an Android build would compile the crate for
 * the Linux host and fail to link. Pass the Android triple and point cargo at
 * the NDK linker for it. The Rust standard library for that triple has to be
 * installed too, e.g. `rustup target add aarch64-linux-android`.
 */
const ANDROID_CONFIGURE_PY: Patch = {
  file: "android_configure.py",
  reason: "Set the host toolchain, the Rust target, v8_target_arch and Javet's configure flags",
  os: [OS.Android],
  replacements: [
    {
      from: `os.environ['CXX'] = toolchain_path + "/bin/" + TOOLCHAIN_PREFIX + android_sdk_version + "-" + "clang++"
`,
      to: `os.environ['CXX'] = toolchain_path + "/bin/" + TOOLCHAIN_PREFIX + android_sdk_version + "-" + "clang++"
import shutil
os.environ['CC_host'] = shutil.which("gcc")
os.environ['CXX_host'] = shutil.which("g++")
# Rust spells armv7 without the trailing 'a' that the NDK uses.
RUST_TARGET = TOOLCHAIN_PREFIX.replace("armv7a-", "armv7-")
os.environ['CARGO_TARGET_' + RUST_TARGET.upper().replace('-', '_') + '_LINKER'] = os.environ['CC']
`,
      applied: `os.environ['CC_host'] = shutil.which("gcc")`,
    },
    {
      from: `GYP_DEFINES += " v8_target_arch=" + arch`,
      to: `GYP_DEFINES += " v8_target_arch=" + DEST_CPU`,
    },
    {
      from: `GYP_DEFINES += " android_ndk_path=" + android_ndk_path
`,
      to: `GYP_DEFINES += " android_ndk_path=" + android_ndk_path
GYP_DEFINES += " cargo_rust_target=" + RUST_TARGET
`,
      applied: `GYP_DEFINES += " cargo_rust_target=" + RUST_TARGET`,
    },
  ],
};

/*
 * android-configure runs ./configure itself, so unlike every other OS the ICU
 * choice has to be baked into android_configure.py rather than passed on a
 * command line. Hence one variant per build.
 */
function androidConfigurePyIntl(i18n: boolean): Patch {
  const intl = i18n ? "full-icu" : "none";
  return {
    file: "android_configure.py",
    reason: `Configure a static --with-intl=${intl} build with Temporal`,
    os: [OS.Android],
    i18n,
    replacements: [
      {
        from: `    os.system("./configure --dest-cpu=" + DEST_CPU + " --dest-os=android --openssl-no-asm --cross-compiling")`,
        to: `    os.system("./configure --dest-cpu=" + DEST_CPU + " --dest-os=android --openssl-no-asm --cross-compiling --enable-static --with-intl=${intl} --v8-enable-temporal-support")`,
      },
    ],
  };
}

/* The NDK headers define arm_fpu themselves. */
const ANDROID_CONFIGURE_PY_ARM_FPU: Patch = {
  file: "configure.py",
  reason: "Leave arm_fpu to the Android NDK",
  os: [OS.Android],
  replacements: [
    {
      from: `  if target_arch == 'arm64':
    o['variables']['arm_fpu'] = options.arm_fpu or 'neon'`,
      to: `  if target_arch == 'arm64':
    pass`,
    },
  ],
};

/* The NDK headers define B0 as a termios flag, which collides with V8's. */
const ANDROID_CONSTANTS_ARM: Patch = {
  file: "deps/v8/src/codegen/arm/constants-arm.h",
  reason: "Resolve the B0 collision with the Android termios header",
  os: [OS.Android],
  replacements: [
    {
      from: `constexpr int B0 = 1 << 0;
`,
      to: `#ifdef B0
#undef B0
// ensure safe undef
#define B0 undefined
#else
constexpr int B0 = 1 << 0;
#endif
`,
      applied: `#define B0 undefined`,
    },
  ],
};

/* V8 only ships the x64 and arm64 stack scanners, so add the ia32 one. */
const ANDROID_PUSH_REGISTERS_ASM: Patch = {
  file: "deps/v8/src/heap/base/asm/x64/push_registers_asm.cc",
  reason: "Add the ia32 stack scanning routine",
  os: [OS.Android],
  replacements: [
    {
      from: `#ifdef _WIN64
#error "The masm based version must be used for Windows"
#endif
`,
      to: `#if defined(V8_TARGET_ARCH_ARM64) || defined(V8_TARGET_ARCH_X64)
#ifdef _WIN64
#error "The masm based version must be used for Windows"
#endif
#else
asm(
#ifdef _WIN32
    ".globl _PushAllRegistersAndIterateStack            \\n"
    "_PushAllRegistersAndIterateStack:                  \\n"
#else   // !_WIN32
    ".globl PushAllRegistersAndIterateStack             \\n"
    ".type PushAllRegistersAndIterateStack, %function   \\n"
    ".hidden PushAllRegistersAndIterateStack            \\n"
    "PushAllRegistersAndIterateStack:                   \\n"
#endif  // !_WIN32
    // [ IterateStackCallback ]
    // [ StackVisitor*        ]
    // [ Stack*               ]
    // [ ret                  ]
    // ebp is callee-saved. Maintain proper frame pointer for debugging.
    "  push %ebp                                        \\n"
    "  movl %esp, %ebp                                  \\n"
    "  push %ebx                                        \\n"
    "  push %esi                                        \\n"
    "  push %edi                                        \\n"
    // Save 3rd parameter (IterateStackCallback).
    "  movl 28(%esp), %ecx                              \\n"
    // Pass 3rd parameter as esp (stack pointer).
    "  push %esp                                        \\n"
    // Pass 2nd parameter (StackVisitor*).
    "  push 28(%esp)                                    \\n"
    // Pass 1st parameter (Stack*).
    "  push 28(%esp)                                    \\n"
    "  call *%ecx                                       \\n"
    // Pop the callee-saved registers.
    "  addl $24, %esp                                   \\n"
    // Restore rbp as it was used as frame pointer.
    "  pop %ebp                                         \\n"
    "  ret                                              \\n");
#endif
`,
      applied: `#if defined(V8_TARGET_ARCH_ARM64) || defined(V8_TARGET_ARCH_X64)`,
    },
  ],
};

/*
 * Android is cross compiled from an x64 or arm64 Linux host and some of the
 * branches that pick V8_TRAP_HANDLER_SUPPORTED key off the host architecture,
 * which lands on V8's "should not be enabled on Android" #error. Override the
 * whole decision instead of rewriting the branches, so that the override
 * survives V8 reshuffling them.
 */
const ANDROID_TRAP_HANDLER: Patch = {
  file: "deps/v8/src/trap-handler/trap-handler.h",
  reason: "Force the trap handler off for Android",
  os: [OS.Android],
  replacements: [
    {
      from: `#if V8_OS_ANDROID && V8_TRAP_HANDLER_SUPPORTED
`,
      to: `#undef V8_TRAP_HANDLER_VIA_SIMULATOR
#undef V8_TRAP_HANDLER_SUPPORTED
#define V8_TRAP_HANDLER_SUPPORTED false

#if V8_OS_ANDROID && V8_TRAP_HANDLER_SUPPORTED
`,
      applied: `#undef V8_TRAP_HANDLER_VIA_SIMULATOR`,
    },
  ],
};

const PATCHES: readonly Patch[] = [
  V8_GYP_TEMPORAL_ZONEINFO,
  COMMON_GYPI_STATIC_LIBRARY,
  CONFIGURE_PY_GDBJIT,
  V8_TLS_MODEL,
  V8_TLS_MODEL_LOCAL_HEAP,
  ANDROID_CONFIGURE_PY,
  androidConfigurePyIntl(false),
  androidConfigurePyIntl(true),
  ANDROID_CONFIGURE_PY_ARM_FPU,
  ANDROID_CONSTANTS_ARM,
  ANDROID_PUSH_REGISTERS_ASM,
  ANDROID_TRAP_HANDLER,
];

class PatchNodeBuild {
  private readonly i18n: boolean;
  private readonly nodeDir: string;
  private readonly os: OS;
  private patched = 0;
  private skipped = 0;
  private failed = 0;

  constructor(config: PatchConfig) {
    this.i18n = config.i18n;
    this.nodeDir = config.nodeDir;
    this.os = config.os;
  }

  private applyPatch(patch: Patch): void {
    const filePath = path.resolve(this.nodeDir, patch.file);

    let content: string;
    try {
      content = Deno.readTextFileSync(filePath);
    } catch (_error) {
      console.error(`ERROR: Failed to read ${filePath}.`);
      ++this.failed;
      return;
    }

    const originalContent = content;
    for (const replacement of patch.replacements) {
      if (replacement.all) {
        // No occurrence left means there is nothing to do either way.
        content = content.replaceAll(replacement.from, replacement.to);
        continue;
      }
      if (content.includes(replacement.applied ?? replacement.to)) {
        // The file already holds the result, so this is a repeated run.
        continue;
      }
      const count = content.split(replacement.from).length - 1;
      if (count === 1) {
        content = content.replace(replacement.from, replacement.to);
      } else if (count === 0) {
        console.error(
          `ERROR: ${patch.file} does not contain the text to replace. ` +
            `Node.js has moved on, or the file was already patched with different options.`
        );
        ++this.failed;
        return;
      } else {
        console.error(
          `ERROR: ${patch.file} contains the text to replace ${count} times, expected 1. ` +
            `Node.js has moved on and the patch needs to be updated.`
        );
        ++this.failed;
        return;
      }
    }

    if (content === originalContent) {
      console.warn(`WARN: Skipped ${patch.file} because it is already patched.`);
      ++this.skipped;
      return;
    }

    Deno.writeTextFileSync(filePath, content);
    console.log(`INFO: Patched ${patch.file}: ${patch.reason}.`);
    ++this.patched;
  }

  patch(): boolean {
    for (const patch of PATCHES) {
      if (patch.os.includes(this.os) && (patch.i18n ?? this.i18n) === this.i18n) {
        this.applyPatch(patch);
      }
    }
    console.log(
      `INFO: ${this.patched} patched, ${this.skipped} skipped, ${this.failed} failed.`
    );
    return this.failed === 0;
  }
}

function parseArgs(): PatchConfig {
  const parsed = cli.parseArgs(Deno.args, {
    string: ["os", "path"],
    boolean: ["i18n"],
    alias: { p: "path" },
    default: {
      "i18n": false,
    },
  });

  const osStr = parsed.os;
  const pathStr = parsed.path;

  if (!osStr || !pathStr) {
    console.info("Error: Both --os and --path arguments are required");
    console.info("\nUsage: patch_node_build.ts --os <os> --path <path>");
    console.info("\nRequired arguments:");
    console.info("  --os <os>           Target OS: linux, macos, windows, android");
    console.info("  --path, -p <path>   Path to the Node.js repository");
    console.info("\nOptional arguments:");
    console.info("  --i18n              Patch for the i18n build (default: false, android only)");
    Deno.exit(1);
  }

  // Validate and convert OS. The target OS is never detected from the host
  // because Android is cross compiled from Linux.
  const os = Object.values(OS).find((o) => o === osStr);
  if (!os) {
    console.error(red(`Error: Invalid OS '${osStr}'`));
    console.error(red(`Supported OS: ${Object.values(OS).join(", ")}`));
    Deno.exit(1);
  }

  return {
    i18n: parsed["i18n"],
    nodeDir: path.resolve(pathStr),
    os: os as OS,
  };
}

function main(): void {
  const config = parseArgs();

  console.log("Patch configuration:");
  console.log(`  OS: ${config.os}`);
  console.log(`  I18n: ${config.i18n}`);
  console.log(`  Node.js: ${config.nodeDir}`);
  console.log();

  if (new PatchNodeBuild(config).patch()) {
    console.log(green("\n✓ Patch Completed"));
    Deno.exit(0);
  } else {
    console.error(red("\n✗ Patch Failed"));
    Deno.exit(1);
  }
}

if (import.meta.main) {
  main();
}
