=============
Release Notes
=============

0.8.0
-----

* Supported Node.js ``v14.16.0``
* Added ``V8Module`` and ``V8Script``
* Enhanced ``V8ValuePromise``

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
* V8 ``v8.9.255``
* Exposure of the majority of V8 API in JVM
* JS function interception
* Native BigInt and Date
* Javet engine pool

[`Home <../README.rst>`_]
