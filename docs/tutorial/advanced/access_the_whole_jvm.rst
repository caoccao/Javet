====================
Access the Whole JVM
====================

The built-in ``JavetJVMInterceptor`` allows JavaScript to access the whole JVM in both Node.js mode and V8 mode.  ``JavetJVMInterceptor`` injects a global object ``javet`` which exposes some API for accessing Java packages and classes.

Let's see how to play with it.

Play with StringBuilder
=======================

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try-with-resource.
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        // Step 2: Create a proxy converter.
        JavetProxyConverter javetProxyConverter = new JavetProxyConverter();
        // Step 3: Enable the dynamic object capability. (Optional)
        javetProxyConverter.getConfig().setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance());
        // Step 4: Set the proxy converter to the V8 runtime.
        v8Runtime.setConverter(javetProxyConverter);
        // Step 5: Create and register the JVM interceptor.
        JavetJVMInterceptor javetJVMInterceptor = new JavetJVMInterceptor(v8Runtime);
        javetJVMInterceptor.register(v8Runtime.getGlobalObject());
        // Step 6: Create package 'java'.
        v8Runtime.getExecutor("let java = javet.package.java").executeVoid();

        // Play with StringBuilder.
        System.out.println(v8Runtime.getExecutor("let sb = new java.lang.StringBuilder();" +
                "sb.append('abc').append(123);" +
                "sb.toString();").executeString());
        // Output: abc123
    }

Once ``let java = javet.package.java`` is executed, the all the packages under ``java`` are open. Just instantiate whatever packages or classes you want to use, e.g. ``java.lang.StringBuilder``, ``java.io.File``, etc. There are a few API available for querying some meta data of those packages.

.. code-block:: java

    System.out.println(v8Runtime.getExecutor("java.lang['.name']").executeString());
    // Output: java.lang
    System.out.println(v8Runtime.getExecutor("java.lang.StringBuilder['.name']").executeString());
    // Output: java.lang.StringBuilder
    System.out.println(v8Runtime.getExecutor("java.io['.valid']").executeBoolean());
    // Output: true
    System.out.println(v8Runtime.getExecutor("java.abc['.valid']").executeBoolean());
    // Output: false
    System.out.println(v8Runtime.getExecutor("javet.package.javax.annotation['.name']").executeString());
    // Output: javax.annotation

Play with Dynamic Interface
===========================

``JavetProxyConverter`` allows implementing Java interfaces dynamically with JavaScript functions. As the following sample code snippet shows, the JavaScript function ``() => { count++; }`` can be executed by ``java.lang.Thread`` and still can access V8. Behind the scene, Javet finds out the constructor of ``Thread`` takes an interface ``Runnable``, then dynamically implements that interface by injecting the JavaScript function.

.. code-block:: java

    // Play with dynamic interface.
    Thread thread = v8Runtime.getExecutor(
            "let count = 0;" +
            "let thread = new java.lang.Thread(() => { count++; });" +
            "thread.start();" +
            "thread; "
    ).executeObject();
    thread.join();
    System.out.println(v8Runtime.getExecutor("count").executeInteger());
    // Output: 1

Play with Dynamic Object
========================

``JavetReflectionObjectFactory`` allows implementing Java non-final objects dynamically with JavaScript objects. Be careful, it relies on library ``byte-buddy``. Please refer to :doc:`../../reference/converters/proxy_converter` for details. As the following sample code snippet shows, the JavaScript object ``{ add: (a, b) => a + b }`` replaces the corresponding Java object ``DynamicClass`` by Javet.

.. code-block:: java

    // Play with dynamic object. (Optional)
    IJavetAnonymous anonymous = new IJavetAnonymous() {
        @V8Function
        public void test(DynamicClass dynamicClass) {
            System.out.println(dynamicClass.add(1, 2));
        }
    };
    v8Runtime.getGlobalObject().set("a", anonymous);
    v8Runtime.getExecutor("a.test({ add: (a, b) => a + b });").executeVoid();
    v8Runtime.getGlobalObject().delete("a");
    // Output: 3

Cleanup
=======

As the tutorial leaves a couple of Java objects in the V8 runtime and a couple of JavaScript objects in the JVM, properly cleaning up both V8 and JVM sometimes is necessary. The following code snippet shows how to elegantly clean up both V8 and JVM before closing the V8 runtime. Of course, these steps are optional because both Javet and JVM will eventually ensure there is no resource leak via the garbage collectors.

.. code-block:: java

    // Step 7: Dispose everything.
    v8Runtime.getExecutor("java = sb = thread = undefined;").executeVoid();
    // Step 8: Unregister the JVM interceptor.
    javetJVMInterceptor.unregister(v8Runtime.getGlobalObject());
    // Step 9: Enforce the GC to avoid memory leak. (Optional)
    System.gc();
    System.runFinalization();
    v8Runtime.lowMemoryNotification();

Summary
=======

``JavetJVMInterceptor`` gives V8 the capability of dynamically execute arbitrary Java code at runtime without compilation. That opens door to another project called `JavetShell <https://github.com/caoccao/JavetShell>`_ which is a console or application that provides Node.js flavored console interactions. That is usually used at a hotfix solution without re-compiling, re-deploying the Java applications.

Please refer to the :extsource3:`source code <../../../src/test/java/com/caoccao/javet/tutorial/TestAccessTheWholeJVM.java>` for details.
