import os

print('''Running `arm-patch.py` to patch a miss-handling of
arm compiler flags that also happen to change from gcc v9 onward

Please remove if no longer needed and older versions of node don't
needed to be compiled that contain the problematic `./configure.py` code''')

ptr_flg = 'sign-return-address=all'
post_gcc8 = 'branch-protection=standard'
gyp_add_seg = '''
      ['target_arch=="arm64"', {{
        'cflags': ['-m{ptr_flg}'],  # Pointer authentication.
      }}],'''
gyp_src = ''''conditions': [{mid_text}
      ['OS=="aix"', {{
        'ldflags': [
          '-Wl,-bnoerrmsg',
        ],
      }}],'''
gcc_version = int(os.environ['GCC_MAJOR_VERSION'])
print('detected GCC_MAJOR_VERSION='+str(gcc_version))
if gcc_version > 8:
    ptr_flg = post_gcc8
gyp_from = gyp_src.format(mid_text='')
gyp_to = gyp_src.format(mid_text=gyp_add_seg.format(ptr_flg=ptr_flg))

file_name = './node.gyp'
with open(file_name, 'r') as file:
    filedata = file.read()

if gyp_from in filedata:
    print("Found: \n"+gyp_from)
    print("replacing with:"+gyp_to)
    filedata = filedata.replace(gyp_from, gyp_to)


with open(file_name, 'w') as file:
    file.write(filedata)

py_from = '''    o['cflags']+=['-msign-return-address=all']'''

file_name = './configure.py'
with open(file_name, 'r') as file:
    filedata = file.read().splitlines()

i = filedata.index(py_from)
if i > 1:
    print("Found: \n"+py_from)
    filedata.pop(i)
    if filedata[i] == "":
        filedata.pop(i - 1)
    with open(file_name, 'w') as file:
        file.write("\n".join(filedata))
