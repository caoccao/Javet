@echo off
SET NODE_LIB_FILE="..\..\..\..\..\..\build\libs\libjavet-node-windows-x86_64.v.3.0.1.lib"
cd %NODE_MODULE_ROOT%
call node-gyp clean
call node-gyp configure --module_name=%NODE_MODULE_NAME% --module_path=%NODE_MODULE_PATH% --node_lib_file=%NODE_LIB_FILE%
call robocopy /e deps build/deps
call node-gyp build --release
cd ..\..\javet-rebuild
