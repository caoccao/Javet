'''
  Copyright (c) 2021 caoccao.com Sam Cao
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

if importlib.util.find_spec('coloredlogs'):
  import coloredlogs
  coloredlogs.install(level=logging.DEBUG, fmt='%(asctime)-15s %(name)s %(levelname)s: %(message)s')

'''
This Python script is for patching V8 on Windows.

1. Clone V8.
2. Checkout to proper version.
3. Generate args.gn.
4. Run `ninja -C out.gn/x64.release v8_wrappers`.
5. Run the script: `python patch_v8_build.py -p root_path_to_v8`
6. Run `ninja -C out.gn/x64.release v8_wrappers`.
7. Run the script: `python patch_v8_build.py -p root_path_to_v8`
8. Run `ninja -C out.gn/x64.release v8_monolith`.
'''
class PatchV8Build(object):
  def __init__(self) -> None:
    self._escape = '\\'
    self._line_separator = '\n'
    self._wrappers_cc_file = 'src/base/platform/wrappers.cc'
    self._wrappers_cc_content = '#include "src/base/platform/wrappers.h"'
    self._wrappers_ninja_file = 'out.gn/x64.release/obj/v8_wrappers.ninja'
    self._wrappers_ninja_line_obj = 'build obj/v8_wrappers.obj: cxx ../../src/base/platform/wrappers.cc'
    self._wrappers_ninja_line_lib = 'build obj/v8_wrappers.lib: alink obj/v8_wrappers.obj'
    self._common_ninja_line_cflags = 'cflags ='
    self._common_ninja_cflags = ['-Wno-invalid-offsetof', '-Wno-range-loop-construct']
    self._common_ninja_files = [
      'out.gn/x64.release/obj/v8_base_without_compiler.ninja',
      'out.gn/x64.release/obj/v8_compiler.ninja',
      'out.gn/x64.release/obj/v8_initializers.ninja',
      'out.gn/x64.release/obj/v8_init.ninja',
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

  def _patch_wrappers(self):
    file_path = self._v8_repo_path.joinpath(self._wrappers_cc_file).resolve().absolute()
    original_buffer = None
    if file_path.exists():
      original_buffer = file_path.read_bytes()
    new_buffer = self._wrappers_cc_content.encode('utf-8')
    if original_buffer == new_buffer:
      logging.warning('Skipped %s.', str(file_path))
    else:
      file_path.write_bytes(new_buffer)
      logging.info('Patched %s.', str(file_path))
    file_path = self._v8_repo_path.joinpath(self._wrappers_ninja_file).resolve().absolute()
    original_buffer = file_path.read_bytes()
    lines = []
    line_obj_patched = False
    for line in original_buffer.decode('utf-8').split(self._line_separator):
      if len(line) > 25:
        if self._wrappers_ninja_line_obj.startswith(line):
          line = self._wrappers_ninja_line_obj
          line_obj_patched = True
        elif self._wrappers_ninja_line_lib.startswith(line):
          if not line_obj_patched:
            lines.append(self._wrappers_ninja_line_obj)
          line = self._wrappers_ninja_line_lib
      lines.append(line)
    new_buffer = self._line_separator.join(lines).encode('utf-8')
    if original_buffer == new_buffer:
      logging.warning('Skipped %s.', str(file_path))
    else:
      file_path.write_bytes(new_buffer)
      logging.info('Patched %s.', str(file_path))

  def _patch_monolith(self):
    for ninja in self._common_ninja_files:
      file_path = self._v8_repo_path.joinpath(ninja).resolve().absolute()
      if file_path.exists():
        original_buffer = file_path.read_bytes()
        lines = []
        for line in original_buffer.decode('utf-8').split(self._line_separator):
          if line.startswith(self._common_ninja_line_cflags):
            flags = line.split(' ')
            for warning_flag in self._common_ninja_cflags:
              if warning_flag not in flags:
                line += ' ' + warning_flag
          lines.append(line)
        new_buffer = self._line_separator.join(lines).encode('utf-8')
        if original_buffer == new_buffer:
          logging.warning('Skipped %s.', str(file_path))
        else:
          file_path.write_bytes(new_buffer)
          logging.info('Patched %s.', str(file_path))

  def patch(self):
    self._patch_wrappers()
    self._patch_monolith()
    return 0

def main():
  if platform.system().startswith('Windows'):
    return PatchV8Build().patch()
  else:
    logging.error('This script is for Windows only.')
    return 1

if __name__ == '__main__':
  sys.exit(int(main() or 0))
