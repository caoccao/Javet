'''
  Copyright (c) 2021-2024. caoccao.com Sam Cao
  All rights reserved.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
'''

import importlib
import logging
import pathlib
import re
import sys

if importlib.util.find_spec('coloredlogs'):
  import coloredlogs
  coloredlogs.install(level=logging.DEBUG, fmt='%(asctime)-15s %(name)s %(levelname)s: %(message)s')

class ChangeVersion(object):

  def __init__(self, version) -> None:
    self._root_path = (pathlib.Path(__file__) / '../../../').resolve().absolute()
    self._version = version

  def update(self) -> None:
    pass

  def _update(self, relative_file_path: str, line_separator: str, *patterns: list) -> None:
    file_path = (self._root_path / relative_file_path).resolve().absolute()
    logging.info('Updating %s.', str(file_path))
    lines, line_number = [], 1
    original_buffer = file_path.read_bytes()
    for line in original_buffer.decode('utf-8').split(line_separator):
      for pattern in patterns:
        match_object = pattern.search(line)
        if match_object is not None:
          version = self._version
          if ',' in match_object.group('version'):
            version = version.replace('.', ',')
          logging.info(
            '  %d: %s -> %s',
            line_number,
            match_object.group('version'),
            version)
          line = '{prefix}{version}{suffix}'.format(
            prefix=line[:match_object.start('version')],
            version=version,
            suffix=line[match_object.end('version'):])
          break
      lines.append(line)
      line_number += 1
    new_buffer = line_separator.join(lines).encode('utf-8')
    if original_buffer == new_buffer:
      logging.warning('  Skipped.')
    else:
      file_path.write_bytes(new_buffer)
      logging.info('  Updated.')

class ChangeNodeVersion(ChangeVersion):

  def __init__(self, version) -> None:
    super().__init__(version)

  def update(self) -> None:
    self._update(
      'README.rst', '\n',
      re.compile(r'Node\.js ``v(?P<version>\d+\.\d+\.\d+)``'))
    self._update(
      '.github/workflows/android_node_build.yml', '\n',
      re.compile(r'JAVET_NODE_VERSION: (?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/linux_x86_64_build.yml', '\n',
      re.compile(r'JAVET_NODE_VERSION: (?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/linux_build_artifact.yml', '\n',
      re.compile(r'JAVET_NODE_VERSION: (?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/linux_build_node_v8_image.yml', '\n',
      re.compile(r'JAVET_NODE_VERSION: (?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/macos_arm64_build.yml', '\n',
      re.compile(r'JAVET_NODE_VERSION: (?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/macos_x86_64_build.yml', '\n',
      re.compile(r'JAVET_NODE_VERSION: (?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      'docker/linux-x86_64/base_all_in_one.Dockerfile', '\n',
      re.compile(r'JAVET_NODE_VERSION=(?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      'docker/linux-x86_64/base_node.Dockerfile', '\n',
      re.compile(r'node_(?P<version>\d+\.\d+\.\d+)'),
      re.compile(r'JAVET_NODE_VERSION=(?P<version>\d+\.\d+\.\d+)'))
    self._update(
      'docker/linux-x86_64/build.env', '\n',
      re.compile(r'JAVET_NODE_VERSION=(?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      'docker/windows-x86_64/base.Dockerfile', '\n',
      re.compile(r'JAVET_NODE_VERSION=(?P<version>\d+\.\d+\.\d+)$'))
    self._update(
      'src/test/java/com/caoccao/javet/interop/TestNodeRuntime.java', '\n',
      re.compile(r'"v(?P<version>\d+\.\d+\.\d+)",'))

class ChangeV8Version(ChangeVersion):

  def __init__(self, version) -> None:
    super().__init__(version)

  def update(self) -> None:
    self._update(
      'README.rst', '\n',
      re.compile(r'V8 ``v(?P<version>\d+\.\d+\.\d+\.\d+)``'))
    self._update(
      '.github/workflows/android_v8_build.yml', '\n',
      re.compile(r'JAVET_V8_VERSION: (?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/linux_x86_64_build.yml', '\n',
      re.compile(r'JAVET_V8_VERSION: (?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/linux_build_artifact.yml', '\n',
      re.compile(r'JAVET_V8_VERSION: (?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/linux_build_node_v8_image.yml', '\n',
      re.compile(r'JAVET_V8_VERSION: (?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/macos_arm64_build.yml', '\n',
      re.compile(r'JAVET_V8_VERSION: (?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      '.github/workflows/macos_x86_64_build.yml', '\n',
      re.compile(r'JAVET_V8_VERSION: (?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      'docker/android/base.Dockerfile', '\n',
      re.compile(r'JAVET_V8_VERSION=(?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      'docker/linux-arm64/base_all_in_one.Dockerfile', '\n',
      re.compile(r'JAVET_V8_VERSION=(?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      'docker/linux-arm64/base_v8.Dockerfile', '\n',
      re.compile(r'v8_(?P<version>\d+\.\d+\.\d+\.\d+)'),
      re.compile(r'JAVET_V8_VERSION=(?P<version>\d+\.\d+\.\d+\.\d+)'))
    self._update(
      'docker/linux-x86_64/base_all_in_one.Dockerfile', '\n',
      re.compile(r'JAVET_V8_VERSION=(?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      'docker/linux-x86_64/base_v8.Dockerfile', '\n',
      re.compile(r'v8_(?P<version>\d+\.\d+\.\d+\.\d+)'),
      re.compile(r'JAVET_V8_VERSION=(?P<version>\d+\.\d+\.\d+\.\d+)'))
    self._update(
      'docker/linux-x86_64/build.env', '\n',
      re.compile(r'JAVET_V8_VERSION=(?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      'docker/windows-x86_64/base.Dockerfile', '\n',
      re.compile(r'JAVET_V8_VERSION=(?P<version>\d+\.\d+\.\d+\.\d+)$'))
    self._update(
      'src/main/java/com/caoccao/javet/enums/JSRuntimeType.java', '\n',
      re.compile(r'"(?P<version>\d+\.\d+\.\d+\.\d+)",'))

def main():
  change_node_version = ChangeNodeVersion('20.17.0')
  change_node_version.update()
  change_v8_version = ChangeV8Version('12.8.374.17')
  change_v8_version.update()
  return 0

if __name__ == '__main__':
  sys.exit(int(main() or 0))
