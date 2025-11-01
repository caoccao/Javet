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

// deno-lint-ignore-file no-regex-spaces
import * as path from "@std/path";

class ChangeJavetVersion {
  private rootPath: string;
  private version: string;

  constructor(version: string) {
    const scriptPath = path.fromFileUrl(import.meta.url);
    this.rootPath = path.resolve(path.dirname(scriptPath), "../../");
    this.version = version;
  }

  update(): void {
    this.updateFile(
      "README.rst",
      "\n",
      /^        <version>(?<version>\d+\.\d+\.\d+)<\/version>$/,
      /javet[\-\w]*:(?<version>\d+\.\d+\.\d+)["'@]{1}/,
      /version: '(?<version>\d+\.\d+\.\d+)'/,
    );
    this.updateFile(
      "build.gradle.kts",
      "\n",
      /^        const val JAVET = "(?<version>\d+\.\d+\.\d+)"$/,
    );
    this.updateFile(
      ".github/workflows/android_node_build.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      ".github/workflows/android_v8_build.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      ".github/workflows/linux_x86_64_build.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      ".github/workflows/linux_build_artifact.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      ".github/workflows/linux_build_node_v8_image.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      ".github/workflows/macos_arm64_build.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      ".github/workflows/macos_x86_64_build.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      ".github/workflows/windows_x86_64_build.yml",
      "\n",
      /JAVET_VERSION: (?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      "docker/android/base.Dockerfile",
      "\n",
      /javet-android:(?<version>\d+\.\d+\.\d+) /,
    );
    this.updateFile(
      "docker/android/build.Dockerfile",
      "\n",
      /javet-android:(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "docker/linux-arm64/base_all_in_one.Dockerfile",
      "\n",
      /javet-arm64:(?<version>\d+\.\d+\.\d+) /,
    );
    this.updateFile(
      "docker/linux-arm64/build_all_in_one.Dockerfile",
      "\n",
      /javet-arm64:(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "docker/linux-arm64/build_artifact.Dockerfile",
      "\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      "docker/linux-arm64/base_gradle.Dockerfile",
      "\n",
      /arm64-(?<version>\d+\.\d+\.\d+) /,
    );
    this.updateFile(
      "docker/linux-x86_64/base_all_in_one.Dockerfile",
      "\n",
      /javet:(?<version>\d+\.\d+\.\d+) /,
    );
    this.updateFile(
      "docker/linux-x86_64/build_all_in_one.Dockerfile",
      "\n",
      /javet:(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "docker/linux-x86_64/build_artifact.Dockerfile",
      "\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      "docker/linux-x86_64/base_gradle.Dockerfile",
      "\n",
      /x86_64-(?<version>\d+\.\d+\.\d+) /,
    );
    this.updateFile(
      "docker/linux-x86_64/build.env",
      "\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)/,
    );
    this.updateFile(
      "docker/windows-x86_64/base.Dockerfile",
      "\n",
      /javet-windows:(?<version>\d+\.\d+\.\d+) /,
    );
    this.updateFile(
      "docker/windows-x86_64/build.Dockerfile",
      "\n",
      /javet-windows:(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "android/javet-android/build.gradle.kts",
      "\n",
      /const val JAVET = "(?<version>\d+\.\d+\.\d+)"$/,
    );
    this.updateFile(
      "android/javet-android/src/main/AndroidManifest.xml",
      "\n",
      /versionName="(?<version>\d+\.\d+\.\d+)"$/,
    );
    this.updateFile(
      "docs/conf.py",
      "\n",
      /release\s*=\s*'(?<version>\d+\.\d+\.\d+)'$/,
    );
    this.updateFile(
      "docs/tutorial/basic/installation.rst",
      "\n",
      /<version>(?<version>\d+\.\d+\.\d+)<\/version>/,
      /<javet\.version>(?<version>\d+\.\d+\.\d+)<\/javet\.version>$/,
      /javet[\-\w$]*:(?<version>\d+\.\d+\.\d+)["'@]{1}/,
      /version: '(?<version>\d+\.\d+\.\d+)'/,
    );
    this.updateFile(
      "android/pom.xml",
      "\n",
      /^    <version>(?<version>\d+\.\d+\.\d+)<\/version>$/,
      /^        <tag>(?<version>\d+\.\d+\.\d+)<\/tag>$/,
    );
    this.updateFile(
      "cpp/build-android.sh",
      "\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "cpp/build-linux-arm64.sh",
      "\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "cpp/build-linux-x86_64.sh",
      "\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "cpp/build-macos.sh",
      "\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "cpp/build-windows.cmd",
      "\r\n",
      /JAVET_VERSION=(?<version>\d+\.\d+\.\d+)$/,
    );
    this.updateFile(
      "src/main/java/com/caoccao/javet/interop/loader/JavetLibLoader.java",
      "\n",
      /LIB_VERSION = "(?<version>\d+\.\d+\.\d+)";$/,
    );
    this.updateFile(
      "cpp/jni/javet_resource_node.rc",
      "\r\n",
      /"(?<version>\d+\.\d+\.\d+)/,
      /v\.(?<version>\d+\.\d+\.\d+)/,
      /(?<version>\d+,\d+,\d+)/,
    );
    this.updateFile(
      "cpp/jni/javet_resource_v8.rc",
      "\r\n",
      /"(?<version>\d+\.\d+\.\d+)/,
      /v\.(?<version>\d+\.\d+\.\d+)/,
      /(?<version>\d+,\d+,\d+)/,
    );
    this.updateFile(
      "scripts/node/javet-rebuild/rebuild.cmd",
      "\r\n",
      /v\.(?<version>\d+\.\d+\.\d+)\.lib/,
    );
    this.updateFile(
      "scripts/node/javet-rebuild/rebuild.sh",
      "\n",
      /v\.(?<version>\d+\.\d+\.\d+)\.so/,
    );
  }

  private updateFile(
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
            `INFO:   ${lineNumber}: ${match.groups.version} -> ${version}`,
          );
          const startIndex = match.index + match[0].indexOf(match.groups.version);
          const endIndex = startIndex + match.groups.version.length;
          updatedLine = updatedLine.substring(0, startIndex) + version +
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

function main(): number {
  const changeJavetVersion = new ChangeJavetVersion("5.0.2");
  changeJavetVersion.update();
  return 0;
}

if (import.meta.main) {
  Deno.exit(main());
}
