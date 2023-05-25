===================
Release Notes 2.2.x
===================

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
