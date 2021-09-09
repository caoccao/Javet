===================
Release Notes 0.7.x
===================

0.7.4 Live Debug
----------------

* Added V8 primitive value cache
* Added live debug for Chrome Developer Tools

0.7.3 Locker
------------

* Added ``V8ValueWeakMap`` and ``V8ValueWeakSet``
* Added ``forEach()`` to Collection
* Added ``V8Locker`` for Performance Sensitive Scenarios

0.7.2 Engine Guard
------------------

* Added ``setFunction(String functionName, String codeString)`` to ``IV8ValueObject``
* Added ``equals()`` and ``strictEquals()`` and ``sameValue()`` to ``IV8Value``
* Added ``getIdentityHash()`` to ``IV8ValueReference``
* Added ``isDead()``, ``isInUse()``, ``callAsConstructor()`` and ``terminateExecution()`` to ``V8Runtime``
* Added V8 typed array and data view
* Added ``IJavetEngineGuard``

0.7.1 Bug Fixes
---------------

* Easy spring integration
* Performance test cases
* Few bug fixes

0.7.0 Birth with V8 v8.9
------------------------

* First release of Javet
* Linux + Windows
* V8 ``v8.9.255`` `(2021-02-04) <https://v8.dev/blog/v8-release-89>`_
* Exposure of the majority of V8 API in JVM
* JS function interception
* Native BigInt and Date
* Javet engine pool
