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

import * as cli from "@std/cli";
import * as path from "@std/path";

/*
 * This Deno TypeScript script is for patching temporal rust repo.
 *
 * 1. Clone temporal repo.
 * 2. Checkout to proper version.
 * 3. Run the script: `deno run --allow-read --allow-write patch_v8_temporal.ts -p root_path_to_temporal`
 */

class PatchV8Temporal {
  private readonly libSection = "\n[lib]\ncrate-type = [\"staticlib\", \"rlib\"]\n";

  private temporalRepoPath: string;

  constructor() {
    this.temporalRepoPath = this.parseArgs();
  }

  private parseArgs(): string {
    const args = cli.parseArgs(Deno.args, {
      string: ["path"],
      alias: {
        p: "path",
      },
    });

    if (!args.path) {
      console.error("Error: -p/--path argument is required");
      console.error(
        "Usage: deno run --allow-read --allow-write patch_v8_temporal.ts -p <temporal_repo_path>"
      );
      Deno.exit(1);
    }

    return path.resolve(args.path);
  }

  private async addLibSection(filePath: string): Promise<void> {
    try {
      const originalContent = await Deno.readTextFile(filePath);
      
      // Check if [lib] section already exists
      if (originalContent.includes("[lib]")) {
        console.warn(`[lib] section already exists in ${filePath}, skipping.`);
        return;
      }

      // Find [package] section (case-insensitive)
      const packageMatch = originalContent.match(/\[package\]/i);
      if (!packageMatch) {
        console.warn(`[package] section not found in ${filePath}, skipping.`);
        return;
      }

      const packageIndex = packageMatch.index!;
      
      // Find the end of the [package] section (next section or end of file)
      let sectionEndIndex = originalContent.length;
      const nextSectionMatch = originalContent.substring(packageIndex + 9).match(/\n\[/);
      if (nextSectionMatch) {
        sectionEndIndex = packageIndex + 9 + nextSectionMatch.index!;
      }

      // Insert [lib] section after [package] section
      const newContent = originalContent.substring(0, sectionEndIndex) +
        this.libSection +
        originalContent.substring(sectionEndIndex);

      await Deno.writeTextFile(filePath, newContent);
      console.info(`Patched ${filePath}: Added [lib] section.`);
    } catch (error) {
      console.error(`Error patching file ${filePath}:`, error);
      throw error;
    }
  }

  private async patchCargoToml(): Promise<void> {
    const filePath = path.join(this.temporalRepoPath, "Cargo.toml");
    await this.addLibSection(filePath);
  }

  private async patchTemporalCapiCargoToml(): Promise<void> {
    const filePath = path.join(
      this.temporalRepoPath,
      "temporal_capi/Cargo.toml"
    );
    await this.addLibSection(filePath);
  }

  async patch(): Promise<number> {
    await this.patchCargoToml();
    await this.patchTemporalCapiCargoToml();
    return 0;
  }
}

async function main(): Promise<number> {
  try {
    const patcher = new PatchV8Temporal();
    return await patcher.patch();
  } catch (error) {
    console.error("Fatal error:", error);
    return 1;
  }
}

if (import.meta.main) {
  Deno.exit(await main());
}
