===========
Hello Javet
===========

Reference Javet
===============

Maven
-----

.. code-block:: xml

    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>0.7.4</version>
    </dependency>

Gradle Kotlin DSL
-----------------

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:0.7.4")

Gradle Groovy DSL
-----------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:0.7.4'

Print **Hello Javet**
=====================

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try resource.
    try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime()) {
        // Step 2: Execute a string as JavaScript code and print the result to console.
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString()); // Hello Javet
        // Step 3: Resource is recycled automatically at the end of the try resource block.
    }

Print **1 + 1**
===============

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try resource.
    try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime()) {
        // Step 2: Execute a string as JavaScript code and print the result to console.
        System.out.println("1 + 1 = " + v8Runtime.getExecutor("1 + 1").executeInteger()); // 2
        // Step 3: Resource is recycled automatically at the end of the try resource block.
    }

Play with Pool and Console
==========================

.. code-block:: java

    // Create a Javet engine pool.
    try (IJavetEnginePool javetEnginePool = new JavetEnginePool()) {
        // Get a Javet engine from the pool.
        try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
            // Get a V8 runtime from the engine.
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            // Create a Javet console interceptor.
            JavetConsoleInterceptor javetConsoleInterceptor = new JavetConsoleInterceptor(v8Runtime);
            // Register the Javet console to V8 global object.
            javetConsoleInterceptor.register(v8Runtime.getGlobalObject());
            // V8 console log is redirected to JVM console log.
            v8Runtime.getExecutor("console.log('Hello Javet from Pool');").executeVoid();
            // Unregister the Javet console to V8 global object.
            javetConsoleInterceptor.unregister(v8Runtime.getGlobalObject());
            // close() is not necessary because the Javet pool handles that.
        }
    }

Please refer to `source code <../../src/test/java/com/caoccao/javet/tutorial/HelloJavet.java>`_ for more detail.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
