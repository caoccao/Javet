===================
Release Notes 2.2.x
===================

2.2.2 V8 v11.6
--------------

* Upgraded V8 to ``v11.6.189.18`` (2023-08-08)
* Upgraded Node.js to ``v18.17.1`` `(2023-08-09) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V18.md#18.17.1>`_
* Fixed a path resolution bug in the executor for Node.js mode
* Fixed ``v8::internal::NativeContext`` for V8 mode

2.2.1 V8 v11.5
--------------

* Upgraded V8 to ``v11.5.150.12`` (2023-06-23)
* Upgraded Node.js to ``v18.16.1`` `(2023-06-20) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V18.md#18.16.1>`_
* Added ``register***()`` to ``IJavetDirectProxyHandler``
* Added ``V8VirtualIterator``
* Enhanced ``JavetProxySymbolIterableConverter`` to support ``Symbol.iterator``

2.2.0 V8 v11.4
--------------

* Upgraded V8 to ``v11.4.183.14`` (2023-05-23)
* Improved the performance of ``V8FunctionCallback.receiveCallback()``, ``IV8ValueObject.forEach()``, ``IV8ValueMap.forEach()``, ``IV8ValueArray.forEach()``, ``IV8ValueArray.push()``, ``IV8ValueObject.set()``, ``IV8ValueMap.set()``,
* Added ``IV8ValueObject.batchGet()``, ``IV8ValueMap.batchGet()``, ``IV8ValueArray.batchGet()``
* Added ``JavetCallbackType``, ``IJavetDirectCallable``, ``IJavetDirectProxyHandler`` to allow callback without reflection
* Redesigned ``IV8ValueObject.bindFunction()``, ``IV8ValueObject.bindProperty()``, ``IV8ValueObject.unbindFunction()``, ``IV8ValueObject.unbindProperty()``, ``JavetCallbackContext``
* Renamed ``JavetDynamicProxy*`` to ``JavetReflectionProxy*``
* Added error code 408 CallbackTypeNotSupported
* Created `JavetPerf <https://github.com/caoccao/JavetPerf>`_ for tracking the performance among various Javet releases
