=============
Release Notes
=============

0.9.4
-----

* Added ``JavetEntityFunction`` for ``JavetObjectConverter``
* Updated ``JavetObjectConverter`` to allow skipping functions

0.9.3
-----

* Upgraded Node.js to ``v14.17.2`` `(2021-07-01) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.2>`_
* Added ``IV8ModuleResolver`` to allow intercepting module resolving callback
* Added ``V8BindEnabler`` to determine whether certain injection is enabled or not
* Added ``NodeModuleProcess#getVersion``
* Updated ``JavetPrimitiveConverter`` and ``JavetObjectConverter`` to check recursion depth for circular structure detection

0.9.2
-----

* Upgraded Node.js to ``v14.17.1`` `(2021-06-15) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.1>`_
* Updated ``JavetObjectConverter`` to handle anonymous functions more efficiently
* Added ``V8ValueBuiltInObject#assign``

0.9.1
-----

* Moved V8 to a custom classloader
* Enabled ``unloadLibrary()`` and ``loadLibrary()`` in ``V8Host``

0.9.0
-----

* Upgraded Node.js to ``v14.17.0`` `(2021-05-11) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.0>`_
* Upgraded V8 to ``v9.1.269.28`` `(2021-05-10) <https://v8.dev/blog/v8-release-91>`_
* Added ``thisObjectRequired`` to ``@V8Function`` and ``@V8Property``

0.8.10
------

* Renamed ``IJavetConsumer`` to ``IJavetUniConsumer``
* Added ``IJavetUniIndexedConsumer`` and ``IJavetBiIndexedConsumer``
* Fixed a bug in ``V8FunctionCallback`` on varargs
* Deprecated ``bindFunctions()`` and ``bindProperties()``
* Added ``@CheckReturnValue`` to warn ignored return value

0.8.9
-----

* Renamed ``setFunction()`` to ``bindFunction()`` in ``IV8ValueObject``
* Renamed ``setFunctions()`` to ``bindFunctions()`` in ``IV8ValueObject``
* Added ``disableInDebugMode()`` and ``enableInDebugMode()`` to ``JavetEngineGuard``
* Added ``bind()``, ``bindProperty()`` and ``bindProperties()`` in ``IV8ValueObject``
* Added ``@V8Property`` for ``IV8ValueObject.bindProperties()``

0.8.8
-----

* Re-organized doc.
* Added ``getJSFunctionType()``, ``getSourceCode()`` and ``setSourceCode()`` to ``IV8ValueFunction``

0.8.7
-----

* Supported native modules with rebuilt scripts
* Fixed ``NodeRuntime.await()`` to emit tasks

0.8.6
-----

* Upgraded Linux build environment to Ubuntu 20.04 + GCC 9.3
* Enhanced ``NodeRuntime.await()`` to allow interaction from other threads

0.8.5
-----

* Universal object conversion is turned on for all API
* Added ``createV8ValueDouble()``,``createV8ValueString()`` and ``createV8ValueZonedDateTime()`` to ``V8Runtime``
* Added ``JavetError`` with error codes
* Refactored ``invoke()``, ``call()``, ``set()``, ``has()`` to take ``Object`` instead of ``V8Value``
* Removed a few exceptions

0.8.4
-----

* Added ``gcBeforeEngineClose`` to ``JavetEngineConfig``
* Added ``JavetCallbackException``
* Added ``IV8Value#isNullOrUndefined``
* Upgraded Node.js to ``v14.16.1`` `(2021-04-06) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.16.1>`_
* Upgraded V8 to ``v9.0.257`` `(2021-02-24) <https://v8.dev/blog/v8-release-90>`_

0.8.3
-----

* Added ``V8Runtime.lowMemoryNotification()`` and ``V8Runtime.idleNotificationDeadline()``
* Added ``V8Host.setMemoryUsageThresholdRatio()``, ``V8Host.enableGCNotification()``, and ``V8Host.disableGCNotification()``
* Updated JavetEnginePool to be lock free
* Added ``autoSendGCNotification`` to ``JavetEngineConfig``
* Moved ``JavetCallbackContext`` management from ``V8ValueFunction`` to V8
* Added ``JavetPromiseRejectCallback`` for V8 mode

0.8.2
-----

* Enabled auto type conversion in primitive types
* Fixed a memory leak issue during V8Runtime ``resetContext()``, ``resetIsolate()``, ``close()``

0.8.1
-----

* Enabled declarative function interception
* Enabled custom object converter in function callback
* Added ``allowEval`` to ``JavetEngineConfig``
* Refactored built-in module API for Node.js mode

0.8.0
-----

* Supported Node.js ``v14.16.0`` `(2021-02-23) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.16.0>`_
* Added ``V8Module`` and ``V8Script``
* Enhanced ``V8ValuePromise``
* Added dynamic switch between Node.js and V8

0.7.4
-----

* Added V8 primitive value cache
* Added live debug for Chrome Developer Tools

0.7.3
-----

* Added ``V8ValueWeakMap`` and ``V8ValueWeakSet``
* Added ``forEach()`` to Collection
* Added ``V8Locker`` for Performance Sensitive Scenarios

0.7.2
-----

* Added ``setFunction(String functionName, String codeString)`` to ``IV8ValueObject``
* Added ``equals()`` and ``strictEquals()`` and ``sameValue()`` to ``IV8Value``
* Added ``getIdentityHash()`` to ``IV8ValueReference``
* Added ``isDead()``, ``isInUse()``, ``callAsConstructor()`` and ``terminateExecution()`` to ``V8Runtime``
* Added V8 typed array and data view
* Added ``IJavetEngineGuard``

0.7.1
-----

* Easy spring integration
* Performance test cases
* Few bug fixes

0.7.0
-----

* First release of Javet
* Linux + Windows
* V8 ``v8.9.255`` `(2021-02-04) <https://v8.dev/blog/v8-release-89>`_
* Exposure of the majority of V8 API in JVM
* JS function interception
* Native BigInt and Date
* Javet engine pool

[`Home <../README.rst>`_]
