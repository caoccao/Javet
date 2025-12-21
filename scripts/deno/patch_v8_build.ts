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
 * This Deno TypeScript script is for patching V8 on Linux and Windows.
 *
 * 1. Clone V8.
 * 2. Checkout to proper version.
 * 3. Generate args.gn.
 * 4. Run `ninja -C out.gn/x64.release v8_monolith`.
 * 5. Wait for errors.
 * 6. Run the script: `deno run --allow-read --allow-write patch_v8_build.ts -p root_path_to_v8`
 * 7. Run `ninja -C out.gn/x64.release v8_monolith`.
 */

class PatchV8Build {
  private readonly lineSeparator = "\n";
  private readonly commonNinjaLineCflags = "cflags =";
  private readonly commonNinjaExcludeCflags = ["-Werror"];
  private readonly commonNinjaIncludeCflags = [
    "-Wno-deprecated-copy-with-user-provided-copy",
    "-Wno-deprecated-declarations",
    "-Wno-invalid-offsetof",
    "-Wno-range-loop-construct",
    "-Wno-ctad-maybe-unsupported",
  ];
  private readonly commonNinjaFileRoot = "out.gn";
  private readonly commonNinjaFileExtension = ".ninja";

  private v8RepoPath: string;

  constructor() {
    this.v8RepoPath = this.parseArgs();
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
        "Usage: deno run --allow-read --allow-write patch_v8_build.ts -p <v8_repo_path>"
      );
      Deno.exit(1);
    }

    return path.resolve(args.path);
  }

  private async patchMonolith(): Promise<void> {
    try {
      for await (const entry of Deno.readDir(this.v8RepoPath)) {
        if (entry.isDirectory && entry.name.startsWith(this.commonNinjaFileRoot)) {
          const outGnPath = path.join(this.v8RepoPath, entry.name);
          console.info(`Processing folder: ${outGnPath}`);
          
          // Walk through the folder to find .ninja files
          for await (const fileEntry of fs.walk(outGnPath, {
            exts: [this.commonNinjaFileExtension.substring(1)],
            includeDirs: false,
          })) {
            if (fileEntry.isFile) {
              await this.patchNinjaFile(fileEntry.path);
            }
          }
        }
      }
    } catch (error) {
      console.error(`Error scanning directories in ${this.v8RepoPath}:`, error);
      throw error;
    }
  }

  private async patchNinjaFile(filePath: string): Promise<void> {
    try {
      const originalContent = await Deno.readTextFile(filePath);
      const lines = originalContent.split(this.lineSeparator);
      const modifiedLines: string[] = [];

      for (let line of lines) {
        if (line.startsWith(this.commonNinjaLineCflags)) {
          const flags = line.split(" ");

          // Add include flags if not present
          for (const includeFlag of this.commonNinjaIncludeCflags) {
            if (!flags.includes(includeFlag)) {
              flags.push(includeFlag);
            }
          }

          // Remove exclude flags if present
          const filteredFlags = flags.filter(
            (flag) => !this.commonNinjaExcludeCflags.includes(flag)
          );

          line = filteredFlags.join(" ");
        }
        modifiedLines.push(line);
      }

      const newContent = modifiedLines.join(this.lineSeparator);

      if (originalContent === newContent) {
        console.warn(`Skipped ${filePath}.`);
      } else {
        await Deno.writeTextFile(filePath, newContent);
        console.info(`Patched ${filePath}.`);
      }
    } catch (error) {
      console.error(`Error patching file ${filePath}:`, error);
      throw error;
    }
  }

  async patch(): Promise<number> {
    await this.patchMonolith();
    return 0;
  }
}

async function main(): Promise<number> {
  try {
    const patcher = new PatchV8Build();
    return await patcher.patch();
  } catch (error) {
    console.error("Fatal error:", error);
    return 1;
  }
}

if (import.meta.main) {
  Deno.exit(await main());
}
