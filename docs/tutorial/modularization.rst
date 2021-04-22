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
        try (IJavetEngine<NodeRuntime> javetEngine = javetEnginePool.getEngine()) {
            NodeRuntime nodeRuntime = iJavetEngine.getV8Runtime();
            Path workingDirectory = new File(JavetOSUtils.WORKING_DIRECTORY, "scripts/node/test-node").toPath();
            // Set the require root directory so that Node.js is able to locate node_modules.
            nodeRuntime.getNodeModuleModule().setRequireRootDirectory(workingDirectory);
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

Known Issue on Native Modules
-----------------------------

Node.js native modules usually cannot be dynamically loaded to Javet. E.g. sqlite3. That issue also bothers Electron. Electron folks created project `electron-rebuild <https://github.com/electron/electron-rebuild>`_ which rebuilds the native modules from source code and its own native symbols.

Javet may follow the same approach in the future. For now, there is no development resource planned for that because most of the use cases don't require native modules.

V8 Mode
=======

In V8 mode, there is no out-of-box support to module. But, Javet provides complete support to ES6 module on top of V8. Here is an example. Assuming ``test.js`` depends on ``module.js``, the code looks like the following.

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

Internals
=========

How Javet and V8 work internally for supporting modules can be found at `here <../development/design.rst>`_.

.. image:: ../resources/images/javet_module_system.png?raw=true
    :alt: Javet Module System

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
