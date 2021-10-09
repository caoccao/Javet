==============
Modularization
==============

Node.js Mode
============

Example
-------

In Node.js mode, Javet leaves Node.js with its own ways of handling modules. The coding experience is identical to the one in Node.js and applications can get all features supported by Javet, like function interception. Here is an example.

.. code-block:: java

    try (JavetEnginePool<NodeRuntime> javetEnginePool = new JavetEnginePool<NodeRuntime>()) {
        javetEnginePool.getConfig().setJSRuntimeType(JSRuntimeType.Node);
        try (IJavetEngine<NodeRuntime> iJavetEngine = javetEnginePool.getEngine()) {
            NodeRuntime nodeRuntime = iJavetEngine.getV8Runtime();
            Path workingDirectory = new File(JavetOSUtils.WORKING_DIRECTORY, "scripts/node/test-node").toPath();
            // Set the require root directory so that Node.js is able to locate node_modules.
            nodeRuntime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(workingDirectory);
            getLogger().logInfo("1.23 + 2.34 = {0}", nodeRuntime.getExecutor(
                    "const Decimal = require('decimal.js');" +
                            "const a = new Decimal(1.23);" +
                            "const b = new Decimal(2.34);" +
                            "a.add(b).toString();").executeString());
        }
    }

Gaps between Javet Node.js Mode and Native Node.js
--------------------------------------------------

=================== ======================================= ==============================================
Feature             Javet Node.js Mode                      Native Node.js
=================== ======================================= ==============================================
``require()`` Root  Java Application Working Directory      JavaScript Application Working Directory
Working Directory   Java Application Working Directory      JavaScript Application Working Directory
``__dirname``       N/A                                     Yes
``__filename``      N/A                                     Yes
Module Mode         default: false                          default: true
=================== ======================================= ==============================================

Usually the Java application working directory doesn't contain ``node_modules``. That for sure breaks Node.js. No worry, here are the steps on closing the gaps.

1. Set the ``require()`` root directory so that Node.js is able to locate ``node_modules``.
2. Set working directory to where the script is located.
3. Set ``__dirname``.
4. Set ``__filename``.

Luckily, in Javet, when ``getExecutor(File scriptFile)`` or ``getExecutor(Path scriptPath)`` is called, all these 4 steps are automatically performed. If ``getExecutor(String scriptString)`` is called, obviously Javet doesn't know what to do, but application may call ``IV8Executor.setResourceName(String resourceName)`` later to perform these 4 steps. So, Javet Node.js mode doesn't care where the script comes from. Application may feel free to virtualize Node.js.

Can Javet run script in Node.js Module Mode? Yes, just call ``IV8Executor.setModule(true)``.

The exciting thing is: in Javet, applications may have multiple instances of Node.js pointing to different ``node_modules`` and potentially these Node.js instances can share the same piece of data.

Deal with Native Modules
------------------------

Node.js native modules usually cannot be dynamically loaded to Javet. E.g. sqlite3. That issue also bothers Electron. Electron folks created project `electron-rebuild <https://github.com/electron/electron-rebuild>`_ which rebuilds the native modules from source code and its own native symbols.

Javet follows the same approach on Windows, and a simpler approach on Linux.

Patch ELF Native Modules on Linux
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The native modules on Linux don't know the existence of Javet. When they look up Node.js symbols which are provided by Javet, they just fail with errors like the following.

    com.caoccao.javet.exceptions.JavetExecutionException: Error: /....../node_modules/sqlite3/lib/binding/napi-v3-linux-x64/node_sqlite3.node: undefined symbol: napi_create_error

The fix is very simple. Here is a sample sqlite3.

.. code-block:: shell

    # Install patchelf on Ubuntu (Optional)
    sudo apt install patchelf
    cd scripts/node
    # Install sqlite3
    npm install
    cd javet-rebuild
    export NODE_MODULE_FILE="../node_modules/sqlite3/lib/binding/napi-v3-linux-x64/node_sqlite3.node"
    ./rebuild.sh

The `rebuild.sh <../../../scripts/node/javet-rebuild/rebuild.sh>`_ actually calls `patchelf <https://github.com/NixOS/patchelf>`_ to add Javet to the node module's dependency.

Rebuild Native Modules on Windows
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The native modules on Windows don't know the existence of Javet. Windows dynamic library loading API ``LoadLibraryExW`` throws the following error.

    A dynamic link library (DLL) initialization routine failed.

The fix is a bit complicated.

* Prepare the Windows build environment by following :doc:`../../development/build`.
* Install the node modules from source code ``npm install --build-from-source``.
* Download the corresponding Javet library file from this `drive <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_.
* Unzip the Javet library file somewhere.
* Create a rebuild script pointing to the Javet library file by referencing `rebuild-sqlite3.cmd <../../../scripts/node/javet-rebuild/rebuild-sqlite3.cmd>`_ and `rebuild.cmd <../../scripts/node/javet-rebuild/rebuild.cmd>`_.
* Run the rebuild script.

The rebuild script actually replaces ``node.lib`` with ``libjavet....lib`` during the rebuild so that the new node modules can tell ``LoadLibraryExW`` to look for Javet instead of Node.js.

Javet calls for someone who can voluntarily host the Javet libraries and Javet compatible node modules so that major Javet users don't need to go through these. For now, it has to be a pretty manual work.

.. caution:: Make Backups

    Once the node modules are patched or rebuilt, they can only be loaded by that particular version of Javet and they cannot be loaded by Node.js any more.

V8 Mode
=======

In V8 mode, there is no out-of-box support to ES6 dynamic import. But, Javet provides complete support on top of V8. There are 2 ways of playing around with the ES6 dynamic import: Pre-load and On-demand.

Pre-load
--------

Javet stores compiled modules in a map with key = module path, value = compiled module. When V8 meets a new module to be imported, Javet will look up the map and return the compiled module to V8. So, in order to simulate dynamic import, application needs to compile those required modules before the final execution.

For instance: The dependency is as following.

.. code-block::

    Application
    ├─A
    │ ├─a.js (depends on b.js)
    │ └─B
    │   └─b.js
    ├─C
    │ └─c.js
    └─d.js

The execution steps are as following.

1. Compile module ./A/B/b.js
2. Compile module ./A/a.js
3. Compile module ./C/c.js
4. Compile module ./d.js
5. Launch the application

Here is an example. Assuming ``test.js`` depends on ``module.js``, the code looks like the following.

.. code-block:: java

    String codeString = "export function testFromModule() { return { a: 1 }; };";
    // Step 1: Assign a resource name to a piece of code.
    IV8Executor iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./module.js");
    // Step 2: Compile the module.js.
    try (V8Module v8Module = iV8Executor.compileModule()) {
        // Step 3: Evaluate the module.js.
        v8Module.executeVoid();
        if (v8Runtime.containsModule("./module.js")) {
            System.out.println("./module.js is registered as a module.");
        }
        codeString = "import { testFromModule } from './module.js'; testFromModule();";
        // Step 4: Do the same to test.js.
        iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./test.js").setModule(true);
        // Step 5: Compile and evaluate test.js and Javet will automatically feed V8 with module.js.
        try (V8ValueObject v8ValueObject = iV8Executor.execute()) {
            // Step 6: Verify the module.js taking effect.
            System.out.println("Variable a = " + v8ValueObject.getInteger("a") + ".");
        }
    }

On-demand
---------

Obviously, pre-loading modules requires application to analyze the code for complete dependency. That is too heavy in most of the cases. Luckily, Javet also supports registering a module resolver which is called back when the modules are being imported. With the module resolver, application doesn't need to analyze the code for dependency. Of course, application is responsible for security check.

Here is an example. Assuming ``test.js`` depends on ``module.js``, the code looks like the following.

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try-with-resource.
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        // Step 2: Register a custom module resolver.
        v8Runtime.setV8ModuleResolver((runtime, resourceName, v8ModuleReferrer) -> {
            // Step 3: Compile module.js from source code if the resource name matches.
            if ("./module.js".equals(resourceName)) {
                return runtime.getExecutor("export function test() { return 1; }")
                        .setResourceName(resourceName).compileV8Module();
            } else {
                return null;
            }
        });
        // Step 4: Import module.js in test.js and expose test() in global context.
        v8Runtime.getExecutor("import { test } from './module.js'; globalThis.test = test;")
                .setModule(true).setResourceName("./test.js").executeVoid();
        // Step 5: Call test() in global context.
        System.out.println("test() -> " + v8Runtime.getExecutor("test()").executeInteger());
    }

It is V8 that performs the dependency analysis. Javet just relays the callback to application and actively caches the compiled modules so that the module resolver is only called one time per module.

Internals
=========

How Javet and V8 work internally for supporting modules can be found at :doc:`../../development/design`.

.. image:: ../../resources/images/javet_module_system.png
    :alt: Javet Module System

Please note that the way Javet handles dynamic import in V8 mode can be applied to Node.js mode. That means all Node.js modules can be virtualized by Javet.
