==============
Modularization
==============

Node.js Mode
============

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

The only thing to do is to set the ``require()`` root directory so that Node.js is able to locate ``node_modules``. With Javet, applications may have multiple instances of Node.js pointing to different ``node_modules`` and potentially these Node.js instances can share the same piece of data.

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
