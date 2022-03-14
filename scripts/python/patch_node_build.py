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
This Python script is for patching Node.js on Linux.

1. Clone Node.js.
2. Checkout to proper version.
3. Run the script: `python3 patch_node_build.py -p root_path_to_node_js`
4. Run `./configure --enable-static --without-intl` under `root_path_to_node_js`.
5. Run the script again: `python3 patch_node_build.py -p root_path_to_node_js`
6. Run `make -j4` under `root_path_to_node_js`.

'''
class PatchNodeBuild(object):
  def __init__(self) -> None:
    self._escape = '\\'
    self._line_separator = '\n'

    # Patch ./common.gypi to generate arch static libraries.
    self._common_file = 'common.gypi'
    self._common_old_key = '_type=="static_library" and OS=="solaris"'
    self._common_new_key = '_type=="static_library"'

    # Patch make files to generate position independent code.
    self._make_keys = [
      'CFLAGS_Release :=',
      'CFLAGS_C_Release :=',
      'CFLAGS_CC_Release :=',
      'LDFLAGS_Release :=',
    ]
    self._make_files = [
      'out/libnode.target.mk',
      'out/tools/v8_gypfiles/torque_base.target.mk',
      'out/tools/v8_gypfiles/v8_base_without_compiler.target.mk',
      'out/deps/cares/cares.target.mk',
      'out/deps/uv/libuv.target.mk',
      'out/deps/openssl/openssl.target.mk',
      'out/deps/nghttp2/nghttp2.target.mk',
      'out/deps/ngtcp2/nghttp3.target.mk',
      'out/deps/ngtcp2/ngtcp2.target.mk',
      'out/tools/icu/icutools.host.mk',
      'out/tools/v8_gypfiles/v8_compiler.target.mk',
      'out/tools/v8_gypfiles/v8_libbase.target.mk',
      'out/deps/llhttp/llhttp.target.mk',
      'out/deps/zlib/zlib.target.mk',
      'out/deps/brotli/brotli.target.mk',
      'out/tools/v8_gypfiles/v8_zlib.target.mk',
      'out/tools/v8_gypfiles/v8_libsampler.target.mk',
      'out/tools/v8_gypfiles/v8_libplatform.target.mk',
      'out/tools/v8_gypfiles/v8_initializers.target.mk',
      'out/deps/histogram/histogram.target.mk',
      'out/deps/uvwasi/uvwasi.target.mk',
      'out/tools/v8_gypfiles/v8_snapshot.target.mk',
    ]
    self._make_property = '    -fPIC \\'
    self._make_property_inline = ' -fPIC '

    self._parse_args()

  def _parse_args(self):
    parser = argparse.ArgumentParser(
      description='arguments for patching Node.js',
    )
    parser.add_argument('-p', '--path',
      type=str,
      required=True,
      metavar='node_repo_path',
      help='the path of the node repo')
    args = parser.parse_args()
    self._node_repo_path = pathlib.Path(args.path).resolve().absolute()

  def _patch_common(self):
    file_path = self._node_repo_path.joinpath(self._common_file).resolve().absolute()
    if file_path.exists():
      original_buffer = file_path.read_bytes()
      lines = []
      for line in original_buffer.decode('utf-8').split(self._line_separator):
        if self._common_old_key in line:
          line = line.replace(self._common_old_key, self._common_new_key)
        lines.append(line)
      new_buffer = self._line_separator.join(lines).encode('utf-8')
      if original_buffer == new_buffer:
        logging.warning('Skipped %s.', str(file_path))
      else:
        file_path.write_bytes(new_buffer)
        logging.info('Patched %s.', str(file_path))
    else:
      logging.error('Failed to locate %s.', str(file_path))

  def _patch_make_files(self):
    for make_file in self._make_files:
      file_path = self._node_repo_path.joinpath(make_file).resolve().absolute()
      if file_path.exists():
        original_buffer = file_path.read_bytes()
        lines = []
        patch_required = False
        for line in original_buffer.decode('utf-8').split(self._line_separator):
          if patch_required:
            patch_required = False
            if line != self._make_property:
              lines.append(self._make_property)
          for make_key in self._make_keys:
            if line.startswith(make_key):
              if not line.endswith(self._escape):
                if not line.endswith(self._make_property_inline):
                  line += self._make_property_inline
              else:
                patch_required = True
              break
          lines.append(line)
        new_buffer = self._line_separator.join(lines).encode('utf-8')
        if original_buffer == new_buffer:
          logging.warning('Skipped %s.', str(file_path))
        else:
          file_path.write_bytes(new_buffer)
          logging.info('Patched %s.', str(file_path))

  def patch(self):
    self._patch_common()
    self._patch_make_files()
    return 0

def main():
  if platform.system().startswith('Linux'):
    return PatchNodeBuild().patch()
  else:
    logging.error('This script is for Linux only.')
    return 1

if __name__ == '__main__':
  sys.exit(int(main() or 0))
