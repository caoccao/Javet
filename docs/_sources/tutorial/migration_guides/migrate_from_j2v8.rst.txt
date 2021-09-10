=================
Migrate from J2V8
=================

How to migrate from J2V8 to Javet is a frequently asked question, especially when people are evaluating Javet. I created Javet in Jan, 2021 for various reasons (:doc:`../../faq/background/what_is_the_motivation`, :doc:`../../faq/background/history_with_j2v8`). After the first release v0.7.0 was published, I started migrating from J2V8 to Javet. It was quite smooth, though it took a week.

Why Migrate from J2V8 to Javet?
===============================

* Its Linux, Mac OS and Windows releases have been abandoned for years.
* Its type hierarchy is inconsistent because primitive types are out of the hierarchy so that tedious ``if-else`` sentences have to be repeated all over the code base.
* Its function registration API is kind of verbose.
* Segfaults take place so frequently and don't get maintainers' attention for years.
* Its locking mechanism heavily increases mental pressure in the code base.
* Its V8 runtime is not multi-threaded friendly unless application adds a synchronous layer on top of it.

Migration Guides
================

V8 ⟶ V8Runtime
---------------

* ``V8`` in J2V8 is ``V8Runtime`` in Javet.
* ``V8`` in J2V8 carries 2 roles: 1 as the V8 runtime and 1 as the global object (``globalThis`` or ``global``). In Javet,  ``V8Runtime`` no longer inherits from ``V8Value`` so that it literally represents the V8 runtime. ``V8Runtime.getGlobalObject()`` is dedicated to the global object.
* ``V8Runtime`` has much richer API than ``V8`` has. E.g. ``compileV8Module()``, ``lowMemoryNotification()``, ``terminateExecution()``.

Primitive Types
---------------

* Primitive types in Javet inherit from ``V8ValuePrimitive`` ⟶ ``V8Value`` ⟶ ``V8Data``.
* The Javet type hierarchy is consistent so that ``V8Value`` in all supported API can represent all V8 types. This is hard in J2V8 because ``Object`` has to be used to represent all types, however, by using ``Object`` the type check during compilation doesn't work at all and that is a rich source of runtime bugs or even segfaults.

registerJavaMethod() ⟶ @V8Function
-----------------------------------

* It is quite painful to register many functions in J2V8. Javet makes that a declarative one instead of the imperative one. Just decorate the target function with ``@V8Function``, then call ``V8ValueObject.bind(javaObject)`` to bind that Java object, it's done.
* In addition, Javet provides ``@V8Property`` which allows registering getters and setters in the same manner. That feature has never been delivered by J2V8.
* Javet also allows unbinding the registration. Just call ``V8ValueObject.unbind(javaObject)``.

Please refer to :doc:`../../reference/v8_function` for more details.

V8Locker
--------

* Javet introduced **Implicit Mode** which allows applications to eliminate ``V8Locker`` from the code base and still be able to share the same ``V8Runtime`` among multiple threads, because Javet does the synchronization automatically. That frees applications developers from the tedious ``acquire()`` and ``release()`` calls, and gets the rid of the runtime exceptions caused by multiple threads.
* Javet also has **Explicit Mode** for performance sensitive scenarios.

Please refer to :doc:`../../reference/lock` for more details.

Type Conversion
---------------

* Javet has built-in ``JavetObjectConverter`` which covers the majority cases on type conversion so that the arguments of Javet API can be of any type and the converter just does the conversion transparently. That frees application developers from writing tedious type conversion code everywhere.
* Javet also provides ``JavetProxyConverter`` which allows injecting arbitrary Java objects in V8 and polyfilling Java interfaces with JavaScript functions or objects. Especially the polyfilling feature implies hotfixing business logic without restarting the JVM.

Please refer to :doc:`../advanced/object_converter` for more details.

Node.js and V8
--------------

* Javet provides both Node.js mode and V8 mode for various usages. Each mode stays at a dedicated classloader so that both modes don't cross each other, and are completely isolated. If the application only uses one mode, it doesn't need to pay extra amount of memory for the other mode because the other mode is not loaded at all. Of course, both modes can be unloaded as well without shutting down the JVM.
* In Node.js mode, all node modules can be directly used including the native modules. Please refer to :doc:`../../reference/modularization` for more detail.
* In V8 mode, it is much more secure than the Node.js mode is, but lacks of some basic ES API, e.g. ``setTimeout()``. Project `Javenode <https://github.com/caoccao/Javenode>`_ is the one that aims at simulating Node.js with Java in Javet V8 mode.

Please refer to :doc:`../../development/design` for more details.

ES6 Module
----------

* Javet supports ``import { *** } from '***.js'`` and exposes module resolve event for applications to specify where to locate the modules.

Please refer to :doc:`../../reference/modularization` for more detail.

Blessing
========

In case this migration guide couldn't cover all your use cases, please contact the maintainer at `discord <https://discord.gg/R4vvKU96gw>`_. Wish you a successful migration!
