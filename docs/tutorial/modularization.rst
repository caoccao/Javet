==============
Modularization
==============

ES6 Module
==========

Javet provides complete support to ES6 module. Here is an example. Assuming ``test.js`` depends on ``module.js``, the code looks like the following.

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
