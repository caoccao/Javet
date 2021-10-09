:orphan:

===================
Release Notes 0.8.x
===================

.. caution::

    v0.8.x are deprecated.

0.8.10 Index in forEach
-----------------------

* Renamed ``IJavetConsumer`` to ``IJavetUniConsumer``
* Added ``IJavetUniIndexedConsumer`` and ``IJavetBiIndexedConsumer``
* Fixed a bug in ``V8FunctionCallback`` on varargs
* Deprecated ``bindFunctions()`` and ``bindProperties()``
* Added ``@CheckReturnValue`` to warn ignored return value

0.8.9 Declarative Property Interception
---------------------------------------

* Renamed ``setFunction()`` to ``bindFunction()`` in ``IV8ValueObject``
* Renamed ``setFunctions()`` to ``bindFunctions()`` in ``IV8ValueObject``
* Added ``disableInDebugMode()`` and ``enableInDebugMode()`` to ``JavetEngineGuard``
* Added ``bind()``, ``bindProperty()`` and ``bindProperties()`` in ``IV8ValueObject``
* Added ``@V8Property`` for ``IV8ValueObject.bindProperties()``

0.8.8 Hotfix the Source Code
----------------------------

* Re-organized doc.
* Added ``getJSFunctionType()``, ``getSourceCode()`` and ``setSourceCode()`` to ``IV8ValueFunction``

0.8.7 Native Modules for Node.js
--------------------------------

* Supported native modules with rebuilt scripts
* Fixed ``NodeRuntime.await()`` to emit tasks

0.8.6 Event Loop for Node.js
----------------------------

* Upgraded Linux build environment to Ubuntu 20.04 + GCC 9.3
* Enhanced ``NodeRuntime.await()`` to allow interaction from other threads

0.8.5 Error Codes
-----------------

* Universal object conversion is turned on for all API
* Added ``createV8ValueDouble()``, ``createV8ValueString()`` and ``createV8ValueZonedDateTime()`` to ``V8Runtime``
* Added ``JavetError`` with error codes
* Refactored ``invoke()``, ``call()``, ``set()``, ``has()`` to take ``Object`` instead of ``V8Value``
* Removed a few exceptions

0.8.4 V8 v9.0
-------------

* Added ``gcBeforeEngineClose`` to ``JavetEngineConfig``
* Added ``JavetCallbackException``
* Added ``IV8Value#isNullOrUndefined``
* Upgraded Node.js to ``v14.16.1`` `(2021-04-06) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.16.1>`_
* Upgraded V8 to ``v9.0.257`` `(2021-02-24) <https://v8.dev/blog/v8-release-90>`_

0.8.3 GC
--------

* Added ``V8Runtime.lowMemoryNotification()`` and ``V8Runtime.idleNotificationDeadline()``
* Added ``V8Host.setMemoryUsageThresholdRatio()``, ``V8Host.enableGCNotification()``, and ``V8Host.disableGCNotification()``
* Updated JavetEnginePool to be lock free
* Added ``autoSendGCNotification`` to ``JavetEngineConfig``
* Moved ``JavetCallbackContext`` management from ``V8ValueFunction`` to V8
* Added ``JavetPromiseRejectCallback`` for V8 mode

0.8.2 Conversion for Primitive
------------------------------

* Enabled auto type conversion in primitive types
* Fixed a memory leak issue during V8Runtime ``resetContext()``, ``resetIsolate()``, ``close()``

0.8.1 Declarative Function Interception
---------------------------------------

* Enabled declarative function interception
* Enabled custom object converter in function callback
* Added ``allowEval`` to ``JavetEngineConfig``
* Refactored built-in module API for Node.js mode

0.8.0 First Time with Node.js
-----------------------------

* Supported Node.js ``v14.16.0`` `(2021-02-23) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.16.0>`_
* Added ``V8Module`` and ``V8Script``
* Enhanced ``V8ValuePromise``
* Added dynamic switch between Node.js and V8
