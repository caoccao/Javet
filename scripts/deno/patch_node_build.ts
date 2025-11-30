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

/*
 * This TypeScript script is for patching Node.js on Linux.
 *
 * 1. Clone Node.js.
 * 2. Checkout to proper version.
 * 3. Run the script: `deno run --allow-read --allow-write patch_node_build.ts -p root_path_to_node_js`
 * 4. Run `./configure --enable-static --without-intl` under `root_path_to_node_js`.
 * 5. Run the script again: `deno run --allow-read --allow-write patch_node_build.ts -p root_path_to_node_js`
 * 6. Run `make -j4` under `root_path_to_node_js`.
 */

import * as cli from "@std/cli";
import * as path from "@std/path";

class PatchNodeBuild {
  private escape = "\\";
  private lineSeparator = "\n";

  // Patch ./common.gypi to generate arch static libraries.
  private commonFile = "common.gypi";
  private commonOldKey = '_type=="static_library" and OS=="solaris"';
  private commonNewKey = '_type=="static_library"';

  // Patch make files to generate position independent code.
  private makeKeys = [
    "CFLAGS_Release :=",
    "CFLAGS_C_Release :=",
    "CFLAGS_CC_Release :=",
    "LDFLAGS_Release :=",
  ];
  private makeProperty =
    "    -fPIC -ftls-model=global-dynamic -Wno-return-type \\";
  private makePropertyInline =
    " -fPIC -ftls-model=global-dynamic -Wno-return-type ";

  private nodeRepoPath: string;

  constructor(nodeRepoPath: string) {
    this.nodeRepoPath = path.resolve(nodeRepoPath);
  }

  private patchCommon(): void {
    const filePath = path.resolve(this.nodeRepoPath, this.commonFile);

    try {
      const stat = Deno.statSync(filePath);
      if (!stat.isFile) {
        console.error(`ERROR: Failed to locate ${filePath}.`);
        return;
      }

      const originalBuffer = Deno.readFileSync(filePath);
      const decoder = new TextDecoder("utf-8");
      const content = decoder.decode(originalBuffer);

      const lines: string[] = [];
      for (const line of content.split(this.lineSeparator)) {
        let updatedLine = line;
        if (line.includes(this.commonOldKey)) {
          updatedLine = line.replace(this.commonOldKey, this.commonNewKey);
        }
        lines.push(updatedLine);
      }

      const encoder = new TextEncoder();
      const newBuffer = encoder.encode(lines.join(this.lineSeparator));

      if (this.buffersEqual(originalBuffer, newBuffer)) {
        console.warn(`WARN: Skipped ${filePath}.`);
      } else {
        Deno.writeFileSync(filePath, newBuffer);
        console.log(`INFO: Patched ${filePath}.`);
      }
    } catch (_error) {
      console.error(`ERROR: Failed to locate ${filePath}.`);
    }
  }

  private async *walkDir(dir: string): AsyncGenerator<string> {
    try {
      for await (const entry of Deno.readDir(dir)) {
        const entryPath = path.resolve(dir, entry.name);
        if (entry.isDirectory) {
          yield* this.walkDir(entryPath);
        } else if (entry.isFile && entry.name.endsWith(".mk")) {
          yield entryPath;
        }
      }
    } catch {
      // Directory doesn't exist or can't be read
      return;
    }
  }

  private async patchMakeFiles(): Promise<void> {
    const outDir = path.resolve(this.nodeRepoPath, "out");

    for await (const filePath of this.walkDir(outDir)) {
      try {
        const stat = Deno.statSync(filePath);
        if (!stat.isFile) {
          continue;
        }

        const originalBuffer = Deno.readFileSync(filePath);
        const decoder = new TextDecoder("utf-8");
        const content = decoder.decode(originalBuffer);

        const lines: string[] = [];
        let patchRequired = false;

        for (const line of content.split(this.lineSeparator)) {
          if (patchRequired) {
            patchRequired = false;
            if (line !== this.makeProperty) {
              lines.push(this.makeProperty);
            }
          }

          let updatedLine = line;
          for (const makeKey of this.makeKeys) {
            if (line.startsWith(makeKey)) {
              if (!line.endsWith(this.escape)) {
                if (!line.endsWith(this.makePropertyInline)) {
                  updatedLine += this.makePropertyInline;
                }
              } else {
                patchRequired = true;
              }
              break;
            }
          }

          lines.push(updatedLine);
        }

        const encoder = new TextEncoder();
        const newBuffer = encoder.encode(lines.join(this.lineSeparator));

        if (this.buffersEqual(originalBuffer, newBuffer)) {
          console.warn(`WARN: Skipped ${filePath}.`);
        } else {
          Deno.writeFileSync(filePath, newBuffer);
          console.log(`INFO: Patched ${filePath}.`);
        }
      } catch {
        // Skip files that can't be read
        continue;
      }
    }
  }

  private buffersEqual(a: Uint8Array, b: Uint8Array): boolean {
    if (a.length !== b.length) return false;
    for (let i = 0; i < a.length; i++) {
      if (a[i] !== b[i]) return false;
    }
    return true;
  }

  async patch(): Promise<number> {
    this.patchCommon();
    await this.patchMakeFiles();
    return 0;
  }
}

async function main(): Promise<number> {
  if (Deno.build.os !== "linux") {
    console.error("ERROR: This script is for Linux only.");
    return 1;
  }

  const args = cli.parseArgs(Deno.args, {
    string: ["path", "p"],
    alias: { p: "path" },
  });

  const nodeRepoPath = args.path || args.p;
  if (!nodeRepoPath) {
    console.error("ERROR: Missing required argument: -p/--path");
    console.error(
      "Usage: deno run --allow-read --allow-write patch_node_build.ts -p <node_repo_path>"
    );
    return 1;
  }

  const patchNodeBuild = new PatchNodeBuild(nodeRepoPath as string);
  return await patchNodeBuild.patch();
}

if (import.meta.main) {
  Deno.exit(await main());
}
