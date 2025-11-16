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

import * as cli from "@std/cli";
import * as fs from "@std/fs";
import * as path from "@std/path";

/*
 * This Deno TypeScript script generates args GN files for V8 builds across different platforms.
 *
 * Usage:
 *   # Generate all .gn files
 *   deno run --allow-read --allow-write generate_args_gn.ts
 *
 *   # Generate specific file
 *   deno run --allow-read --allow-write generate_args_gn.ts --os android --arch arm64
 *   deno run --allow-read --allow-write generate_args_gn.ts --os linux --arch x86_64 --i18n
 *
 *   # Dry run (preview without writing)
 *   deno run generate_args_gn.ts --dry-run
 */

type OS = "android" | "linux" | "macos" | "windows";
type Arch = "arm" | "arm64" | "x86" | "x86_64";
type ConfigValue = true | false | "hidden";
type OptionalStringValue = string | "hidden";

interface GnConfig {
  clang_use_chrome_plugins: boolean;
  compiler_timing: boolean;
  dcheck_always_on: boolean;
  is_component_build: boolean;
  is_debug: boolean;
  is_official_build: boolean;
  symbol_level: number;
  target_cpu: string;
  target_os?: string;
  use_blink: boolean;
  use_clang_modules?: boolean;
  use_custom_libcxx: boolean;
  use_custom_libunwind?: boolean;
  use_safe_libstdcxx?: boolean;
  v8_enable_i18n_support: boolean;
  v8_enable_pointer_compression: boolean;
  v8_enable_sandbox: boolean;
  v8_enable_temporal_support: boolean;
  v8_enable_webassembly: boolean;
  v8_monolithic: boolean;
  v8_monolithic_for_shared_library: boolean;
  v8_static_library: boolean;
  v8_target_cpu: string;
  v8_use_external_startup_data: boolean;
}

interface PlatformConfig {
  architectures: Arch[];
  targetOS: OptionalStringValue;
  clangModules: ConfigValue;
  customLibCxx: ConfigValue;
  customLibUnwind: ConfigValue;
  safeLibStdcxx: ConfigValue;
}

class GnArgsGenerator {
  private readonly outputDir: string;
  private readonly lineSeparator = "\n";

  private readonly platformConfigs: Record<OS, PlatformConfig> = {
    android: {
      architectures: ["arm", "arm64", "x86", "x86_64"],
      targetOS: "android",
      clangModules: "hidden",
      customLibCxx: false,
      customLibUnwind: "hidden",
      safeLibStdcxx: "hidden",
    },
    linux: {
      architectures: ["arm64", "x86_64"],
      targetOS: "hidden",
      clangModules: false,
      customLibCxx: true,
      customLibUnwind: true,
      safeLibStdcxx: false,
    },
    macos: {
      architectures: ["arm64", "x86_64"],
      targetOS: "hidden",
      clangModules: false,
      customLibCxx: false,
      customLibUnwind: "hidden",
      safeLibStdcxx: "hidden",
    },
    windows: {
      architectures: ["x86_64"],
      targetOS: "hidden",
      clangModules: "hidden",
      customLibCxx: false,
      customLibUnwind: "hidden",
      safeLibStdcxx: "hidden",
    },
  };

  constructor() {
    const currentDir = path.dirname(path.fromFileUrl(import.meta.url));
    this.outputDir = path.resolve(currentDir, "../v8/gn");
  }

  private archToTargetCpu(arch: Arch): string {
    const mapping: Record<Arch, string> = {
      arm: "arm",
      arm64: "arm64",
      x86: "x86",
      x86_64: "x64",
    };
    return mapping[arch];
  }

  private generateConfig(os: OS, arch: Arch, i18n: boolean): GnConfig {
    const platformConfig = this.platformConfigs[os];
    const targetCpu = this.archToTargetCpu(arch);

    const config: GnConfig = {
      clang_use_chrome_plugins: false,
      compiler_timing: true,
      dcheck_always_on: false,
      is_component_build: false,
      is_debug: false,
      is_official_build: false,
      symbol_level: 0,
      target_cpu: targetCpu,
      use_blink: false,
      use_custom_libcxx: platformConfig.customLibCxx === true,
      v8_enable_i18n_support: i18n,
      v8_enable_pointer_compression: false,
      v8_enable_sandbox: false,
      v8_enable_temporal_support: false,
      v8_enable_webassembly: true,
      v8_monolithic: true,
      v8_monolithic_for_shared_library: true,
      v8_static_library: true,
      v8_target_cpu: targetCpu,
      v8_use_external_startup_data: false,
    };

    // Add optional fields based on platform
    if (
      platformConfig.targetOS !== "hidden" &&
      typeof platformConfig.targetOS === "string"
    ) {
      config.target_os = platformConfig.targetOS;
    }

    if (platformConfig.clangModules !== "hidden") {
      config.use_clang_modules = platformConfig.clangModules === true;
    }

    if (platformConfig.customLibUnwind !== "hidden") {
      config.use_custom_libunwind = platformConfig.customLibUnwind === true;
    }

    if (platformConfig.safeLibStdcxx !== "hidden") {
      config.use_safe_libstdcxx = platformConfig.safeLibStdcxx === true;
    }

    return config;
  }

  private formatValue(value: boolean | number | string): string {
    if (typeof value === "boolean") {
      return value ? "true" : "false";
    }
    if (typeof value === "number") {
      return value.toString();
    }
    return `"${value}"`;
  }

  private generateFileContent(os: OS, arch: Arch, i18n: boolean): string {
    const config = this.generateConfig(os, arch, i18n);
    const lines: string[] = [];

    // Convert config to lines in a specific order
    const orderedKeys: (keyof GnConfig)[] = [
      "clang_use_chrome_plugins",
      "compiler_timing",
      "dcheck_always_on",
      "is_component_build",
      "is_debug",
      "is_official_build",
      "symbol_level",
      "target_cpu",
      "target_os",
      "use_blink",
      "use_clang_modules",
      "use_custom_libcxx",
      "use_custom_libunwind",
      "use_safe_libstdcxx",
      "v8_enable_i18n_support",
      "v8_enable_pointer_compression",
      "v8_enable_sandbox",
      "v8_enable_temporal_support",
      "v8_enable_webassembly",
      "v8_monolithic",
      "v8_monolithic_for_shared_library",
      "v8_static_library",
      "v8_target_cpu",
      "v8_use_external_startup_data",
    ];

    for (const key of orderedKeys) {
      const value = config[key];
      if (value !== undefined) {
        lines.push(`${key} = ${this.formatValue(value)}`);
      }
    }

    return lines.join(this.lineSeparator) + this.lineSeparator;
  }

  private getFileName(os: OS, arch: Arch, i18n: boolean): string {
    const i18nSuffix = i18n ? "i18n" : "non-i18n";
    return `${os}-${arch}-${i18nSuffix}-args.gn`;
  }

  private async generateSingleFile(
    os: OS,
    arch: Arch,
    i18n: boolean,
    dryRun: boolean
  ): Promise<void> {
    const fileName = this.getFileName(os, arch, i18n);
    const filePath = path.join(this.outputDir, fileName);
    const content = this.generateFileContent(os, arch, i18n);

    if (dryRun) {
      console.log(`[DRY RUN] Would generate: ${fileName}`);
      console.log(content);
      console.log("---");
    } else {
      await fs.ensureDir(this.outputDir);
      await Deno.writeTextFile(filePath, content);
      console.log(`Generated: ${fileName}`);
    }
  }

  private async generateAllFiles(dryRun: boolean): Promise<void> {
    let totalFiles = 0;

    for (const [os, platformConfig] of Object.entries(this.platformConfigs)) {
      for (const arch of platformConfig.architectures) {
        for (const i18n of [true, false]) {
          await this.generateSingleFile(os as OS, arch, i18n, dryRun);
          totalFiles++;
        }
      }
    }

    console.log(
      `\n${
        dryRun ? "[DRY RUN] Would generate" : "Generated"
      } ${totalFiles} files total.`
    );
  }

  private validateArgs(os: OS, arch: Arch): void {
    const platformConfig = this.platformConfigs[os];
    if (!platformConfig) {
      throw new Error(
        `Invalid OS: ${os}. Valid options: ${Object.keys(
          this.platformConfigs
        ).join(", ")}`
      );
    }

    if (!platformConfig.architectures.includes(arch)) {
      throw new Error(
        `Invalid architecture ${arch} for OS ${os}. Valid options: ${platformConfig.architectures.join(
          ", "
        )}`
      );
    }
  }

  async main(): Promise<number> {
    const args = cli.parseArgs(Deno.args, {
      boolean: ["i18n", "dry-run", "help"],
      string: ["os", "arch"],
      default: {
        "dry-run": false,
      },
    });

    if (args.help) {
      this.printUsage();
      return 0;
    }

    const dryRun = args["dry-run"];

    if (args.os && args.arch) {
      const os = args.os as OS;
      const arch = args.arch as Arch;
      const i18n = args.i18n; // Default to false (disabled)

      try {
        this.validateArgs(os, arch);
        await this.generateSingleFile(os, arch, i18n, dryRun);
        return 0;
      } catch (error) {
        console.error(
          `Error: ${error instanceof Error ? error.message : String(error)}`
        );
        return 1;
      }
    } else if (!args.os && !args.arch) {
      // No arguments means generate all files
      try {
        await this.generateAllFiles(dryRun);
        return 0;
      } catch (error) {
        console.error(
          `Error: ${error instanceof Error ? error.message : String(error)}`
        );
        return 1;
      }
    } else {
      console.error(
        "Error: Both --os and --arch must be specified together.\n"
      );
      this.printUsage();
      return 1;
    }
  }

  private printUsage(): void {
    console.log(`
Args GN Generator - Generate V8 GN configuration files

Usage:
  Generate all files:
    deno run --allow-read --allow-write generate_args_gn.ts

  Generate specific file:
    deno run --allow-read --allow-write generate_args_gn.ts --os <os> --arch <arch> [--i18n]

Options:
  --os <os>          Target OS (android, linux, macos, windows)
  --arch <arch>      Target architecture (arm, arm64, x86, x86_64)
  --i18n             Enable i18n support (disabled by default)
  --dry-run          Preview output without writing files
  --help             Show this help message

Examples:
  deno run --allow-read --allow-write generate_args_gn.ts
  deno run --allow-read --allow-write generate_args_gn.ts --os android --arch arm64
  deno run --allow-read --allow-write generate_args_gn.ts --os linux --arch x86_64 --i18n
  deno run generate_args_gn.ts --dry-run

Supported combinations:
  android: arm, arm64, x86, x86_64
  linux:   arm64, x86_64
  macos:   arm64, x86_64
  windows: x86_64
`);
  }
}

// Run the generator
if (import.meta.main) {
  const generator = new GnArgsGenerator();
  Deno.exit(await generator.main());
}
