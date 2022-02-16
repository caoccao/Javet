'''
  Copyright (c) 2021-2022 caoccao.com Sam Cao
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

import argparse
import importlib
import logging
import pathlib
import platform
import sys

if hasattr(importlib, 'util') and importlib.util.find_spec('coloredlogs'):
  import coloredlogs
  coloredlogs.install(level=logging.DEBUG, fmt='%(asctime)-15s %(name)s %(levelname)s: %(message)s')

'''
This Python script is for patching V8 on Linux and Windows.

1. Clone V8.
2. Checkout to proper version.
3. Generate args.gn.
4. Run `ninja -C out.gn/x64.release v8_monolith`.
5. Wait for errors.
6. Run the script: `python patch_v8_build.py -p root_path_to_v8`
7. Run `ninja -C out.gn/x64.release v8_monolith`.
'''
class PatchV8Build(object):
  def __init__(self) -> None:
    self._escape = '\\'
    self._line_separator = '\n'
    self._common_ninja_line_cflags = 'cflags ='
    self._common_ninja_exclude_cflags = [
      '-Werror',
    ]
    self._common_ninja_include_cflags = [
      '-Wno-invalid-offsetof',
      '-Wno-range-loop-construct',
      '-Wno-deprecated-copy-with-user-provided-copy',
    ]
    self._common_ninja_files = [
      'out.gn/arm.release/obj/v8_base_without_compiler.ninja',
      'out.gn/arm.release/obj/v8_compiler.ninja',
      'out.gn/arm.release/obj/v8_initializers.ninja',
      'out.gn/arm.release/obj/v8_init.ninja',
      'out.gn/arm.release/clang_x86_v8_arm/obj/v8_base_without_compiler.ninja',
      'out.gn/arm64.release/obj/v8_base_without_compiler.ninja',
      'out.gn/arm64.release/obj/v8_compiler.ninja',
      'out.gn/arm64.release/obj/v8_initializers.ninja',
      'out.gn/arm64.release/obj/v8_init.ninja',
      'out.gn/arm64.release/clang_x64_v8_arm64/obj/v8_base_without_compiler.ninja',
      'out.gn/ia32.release/obj/v8_base_without_compiler.ninja',
      'out.gn/ia32.release/obj/v8_compiler.ninja',
      'out.gn/ia32.release/obj/v8_initializers.ninja',
      'out.gn/ia32.release/obj/v8_init.ninja',
      'out.gn/ia32.release/clang_x86/obj/v8_base_without_compiler.ninja',
      'out.gn/x64.release/obj/v8_base_without_compiler.ninja',
      'out.gn/x64.release/obj/v8_compiler.ninja',
      'out.gn/x64.release/obj/v8_initializers.ninja',
      'out.gn/x64.release/obj/v8_init.ninja',
      'out.gn/x64.release/clang_x64/obj/v8_base_without_compiler.ninja',
    ]

    self._parse_args()

  def _parse_args(self):
    parser = argparse.ArgumentParser(
      description='arguments for patching V8',
    )
    parser.add_argument('-p', '--path',
      type=str,
      required=True,
      metavar='v8_repo_path',
      help='the path of the v8 repo')
    args = parser.parse_args()
    self._v8_repo_path = pathlib.Path(args.path).resolve().absolute()

  def _patch_monolith(self):
    for ninja in self._common_ninja_files:
      file_path = self._v8_repo_path.joinpath(ninja).resolve().absolute()
      if file_path.exists():
        original_buffer = file_path.read_bytes()
        lines = []
        for line in original_buffer.decode('utf-8').split(self._line_separator):
          if line.startswith(self._common_ninja_line_cflags):
            flags = line.split(' ')
            for include_flag in self._common_ninja_include_cflags:
              if include_flag not in flags:
                flags.append(include_flag)
            for exclude_flag in self._common_ninja_exclude_cflags:
              if exclude_flag in flags:
                flags.remove(exclude_flag)
            line = ' '.join(flags)
          lines.append(line)
        new_buffer = self._line_separator.join(lines).encode('utf-8')
        if original_buffer == new_buffer:
          logging.warning('Skipped %s.', str(file_path))
        else:
          file_path.write_bytes(new_buffer)
          logging.info('Patched %s.', str(file_path))

  def patch(self):
    self._patch_monolith()
    return 0

def main():
  if platform.system().startswith('Windows') or platform.system().startswith('Linux'):
    return PatchV8Build().patch()
  else:
    logging.error('This script is for Linux and Windows only.')
    return 1

if __name__ == '__main__':
  sys.exit(int(main() or 0))
