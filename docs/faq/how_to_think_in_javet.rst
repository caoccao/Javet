======================
How to Think in Javet?
======================

There are folks migrating projects from other libraries (e.g. J2V8) to Javet. Some experienced certain challenges which from Javet perspective required **Thinking in Javet**. So, how?

``V8Runtime`` and ``V8ValueGlobalObject``
=========================================

In Javet, ``V8Runtime`` and ``V8ValueGlobalObject`` are separate concepts.

``V8Runtime``
-------------

``V8Runtime`` is only a representative of the V8 isolate and V8 context. It has nothing to do with ``globalThis`` or ``window``. However, in few other libraries these concepts are mixed up.

``V8ValueGlobalObject``
-----------------------

As ``V8Runtime`` no longer represents ``globalThis`` or ``window``, ``V8ValueGlobalObject`` from ``V8Runtime.getGlobalObject()`` is the one.

If you want to access global objects or call top level functions, ``globalObject.get('...')`` and ``globalObject.invoke('...')`` are the Javet ways.

Executor and Execute
====================

In Javet, executing a script involves 2 steps.

1. Get the executor.
2. Call certain execute method of that executor.

This is quite different from the common practice of few other libraries where execute is just a 1-step thing.

Why? Javet intends to offer a builder pattern in the script execution. With an executor, application may do every other things before the actual execution. For instance: security check, enable / disable certain V8 features, hack the AST, set up interception, etc. The scope may be limited to that executor only. Also, the executor can be reused to avoid the duplicated resource loading overhead.

Declarative Function Interception
=================================

Javet offers an easy way of registering function interception in a declarative way. Just decorate a function of arbitrary object with ``@V8Function(name = "...")``, Javet is able to register it and that function will be called back from V8. Application is free from managing those tedious things like resource leakage, object lifecycle, type conversion, etc. Javet just handles that behind the scene and everything goes smoothly.

The coding experience is a little bit close to working with Spring Framework.

Learn from Unit Test
====================

Most of the Javet API is unit test coverted. Learning from Javet unit test is an express and effective way to Thinking in Javet.
