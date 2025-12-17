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

import * as path from "@std/path";

abstract class ChangeVersion {
  protected rootPath: string;
  protected version: string;

  constructor(version: string) {
    const scriptPath = path.fromFileUrl(import.meta.url);
    this.rootPath = path.resolve(path.dirname(scriptPath), "../../");
    this.version = version;
  }

  abstract update(): void;

  protected updateFile(
    relativeFilePath: string,
    lineSeparator: string,
    ...patterns: RegExp[]
  ): void {
    const filePath = path.resolve(this.rootPath, relativeFilePath);
    console.log(`INFO: Updating ${filePath}.`);

    const lines: string[] = [];
    let lineNumber = 1;

    const originalBuffer = Deno.readFileSync(filePath);
    const decoder = new TextDecoder("utf-8");
    const content = decoder.decode(originalBuffer);

    for (const line of content.split(lineSeparator)) {
      let updatedLine = line;

      for (const pattern of patterns) {
        const match = pattern.exec(updatedLine);
        if (match !== null && match.groups?.version !== undefined) {
          let version = this.version;
          if (match.groups.version.includes(",")) {
            version = version.replace(/\./g, ",");
          }
          console.log(
            `INFO:   ${lineNumber}: ${match.groups.version} -> ${version}`
          );
          const startIndex =
            match.index + match[0].indexOf(match.groups.version);
          const endIndex = startIndex + match.groups.version.length;
          updatedLine =
            updatedLine.substring(0, startIndex) +
            version +
            updatedLine.substring(endIndex);
          break;
        }
      }

      lines.push(updatedLine);
      lineNumber++;
    }

    const encoder = new TextEncoder();
    const newBuffer = encoder.encode(lines.join(lineSeparator));

    if (this.buffersEqual(originalBuffer, newBuffer)) {
      console.warn("WARN:   Skipped.");
    } else {
      Deno.writeFileSync(filePath, newBuffer);
      console.log("INFO:   Updated.");
    }
  }

  private buffersEqual(a: Uint8Array, b: Uint8Array): boolean {
    if (a.length !== b.length) return false;
    for (let i = 0; i < a.length; i++) {
      if (a[i] !== b[i]) return false;
    }
    return true;
  }
}

class ChangeNodeVersion extends ChangeVersion {
  constructor(version: string) {
    super(version);
  }

  update(): void {
    this.updateFile(
      "README.rst",
      "\n",
      /Node\.js ``v(?<version>\d+\.\d+\.\d+)``/
    );
    this.updateFile(
      ".github/workflows/android_node_build.yml",
      "\n",
      /JAVET_NODE_VERSION: (?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/linux_x86_64_build.yml",
      "\n",
      /JAVET_NODE_VERSION: (?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/linux_x86_64_docker.yml",
      "\n",
      /JAVET_NODE_VERSION: (?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/macos_arm64_build.yml",
      "\n",
      /JAVET_NODE_VERSION: (?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/macos_x86_64_build.yml",
      "\n",
      /JAVET_NODE_VERSION: (?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/windows_x86_64_build.yml",
      "\n",
      /JAVET_NODE_VERSION: (?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "docker/linux-x86_64/build.Dockerfile",
      "\n",
      /JAVET_NODE_VERSION=(?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "docker/windows-x86_64/build.Dockerfile",
      "\n",
      /JAVET_NODE_VERSION=(?<version>\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "src/test/java/com/caoccao/javet/interop/TestNodeRuntime.java",
      "\n",
      /"v(?<version>\d+\.\d+\.\d+)",/
    );
  }
}

class ChangeV8Version extends ChangeVersion {
  constructor(version: string) {
    super(version);
  }

  update(): void {
    this.updateFile(
      "README.rst",
      "\n",
      /V8 ``v(?<version>\d+\.\d+\.\d+\.\d+)``/
    );
    this.updateFile(
      ".github/workflows/android_v8_build.yml",
      "\n",
      /JAVET_V8_VERSION: (?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/linux_x86_64_build.yml",
      "\n",
      /JAVET_V8_VERSION: (?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/linux_x86_64_docker.yml",
      "\n",
      /JAVET_V8_VERSION: (?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/macos_arm64_build.yml",
      "\n",
      /JAVET_V8_VERSION: (?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/macos_x86_64_build.yml",
      "\n",
      /JAVET_V8_VERSION: (?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      ".github/workflows/windows_x86_64_build.yml",
      "\n",
      /JAVET_V8_VERSION: (?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "docker/android/base.Dockerfile",
      "\n",
      /JAVET_V8_VERSION=(?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "docker/linux-arm64/base_all_in_one.Dockerfile",
      "\n",
      /JAVET_V8_VERSION=(?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "docker/linux-arm64/base_v8.Dockerfile",
      "\n",
      /v8_(?<version>\d+\.\d+\.\d+\.\d+)/,
      /JAVET_V8_VERSION=(?<version>\d+\.\d+\.\d+\.\d+)/
    );
    this.updateFile(
      "docker/linux-x86_64/build.Dockerfile",
      "\n",
      /JAVET_V8_VERSION=(?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "docker/windows-x86_64/build.Dockerfile",
      "\n",
      /JAVET_V8_VERSION=(?<version>\d+\.\d+\.\d+\.\d+)$/
    );
    this.updateFile(
      "src/main/java/com/caoccao/javet/enums/JSRuntimeType.java",
      "\n",
      /"(?<version>\d+\.\d+\.\d+\.\d+)",/
    );
  }
}

function main(): number {
  const changeNodeVersion = new ChangeNodeVersion("24.12.0");
  changeNodeVersion.update();
  const changeV8Version = new ChangeV8Version("14.4.258.13");
  changeV8Version.update();
  return 0;
}

if (import.meta.main) {
  Deno.exit(main());
}
