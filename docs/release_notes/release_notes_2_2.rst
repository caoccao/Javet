===================
Release Notes 2.2.x
===================

2.2.0 V8 v11.4
--------------

* Improved the performance of ``V8FunctionCallback.receiveCallback()``, ``IV8ValueObject.forEach()``, ``IV8ValueMap.forEach()``, ``IV8ValueArray.forEach()``, ``IV8ValueArray.push()``, ``IV8ValueObject.set()``, ``IV8ValueMap.set()``,
* Added ``IV8ValueObject.batchGet()``, ``IV8ValueMap.batchGet()``, ``IV8ValueArray.batchGet()``
* Added ``JavetCallbackType``, ``IJavetDirectCallable`` to allow callback without reflection
* Redesigned ``IV8ValueObject.bindFunction()``, ``IV8ValueObject.bindProperty()``, ``JavetCallbackContext``
* Created `JavetPerf <https://github.com/caoccao/JavetPerf>`_ for tracking the performance among various Javet releases
