===================
Release Notes 0.9.x
===================

0.9.14
------

* Added ``IJavetMappable``
* Enhanced ``JavetObjectConverter`` to look up custom objects recursively
* Renamed ``V8BindEnabler`` to ``V8BindingEnabler``

0.9.13 V8 v9.4
--------------

* Upgraded V8 to ``v9.4.146.16`` `(2021-09-14) <https://v8.dev/blog/v8-release-94>`_
* Added support to generator object
* Added ``hasInternalType()`` and ``isGeneratorObject()`` to ``IV8ValueObject``
* Added ``isGeneratorFunction()`` and ``isAsyncFunction()`` to ``IV8ValueFunction``
* Added support to ``Optional`` and ``Stream`` in converters and callbacks
* Added ``IJavetAnonymous`` creating ad-hoc annotation based callback receiver
* Added ``V8VirtualEscapableValue``
* Added ``getMethodNameFromLambda`` and ``getMethodNameSetFromLambdas`` to ``JavetReflectionUtils``
* Enabled decorating a function with both ``@V8Function`` and ``@V8Property``
* Enhanced ``V8Flags`` to accept custom flags

0.9.12 Private Properties
-------------------------

* Added error code 503
* Enhanced ``V8Property`` to support built-in symbols
* Added ``V8ValueSharedArrayBuffer``
* Added ``registerCustomObject()`` and ``unregisterCustomObject()`` to ``JavetObjectConverter``
* Added private property support to ``IV8ValueObject``
* Fixed potential core dump issue in V8 runtime close.
* Added Javadoc

0.9.11 V8 v9.3
--------------

* Upgraded Node.js to ``v14.17.6`` `(2021-08-31) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.6>`_
* Upgraded V8 to ``v9.3.345.16`` `(2021-08-17) <https://v8.dev/blog/v8-release-93>`_
* Added ``unbind()``, ``unbindProperty()``, ``unbindFunction()`` to ``IV8ValueObject``
* Updated ``V8Runtime`` to be completely thread-safe
* Added ``V8ValueBuiltInSymbol``
* Added ``getOwnPropertySymbols()`` to ``V8ValueBuiltInObject``
* Added ``createV8ValueSymbol()`` to ``V8Runtime``
* Added ``symbol`` to ``V8Property`` to enable getter and setter on symbol
* Added error code 407 and 805

0.9.10 Polyfill Java Interfaces
-------------------------------

* Added ``JavetVirtualObject``
* Updated ``JavetUniversalProxyHandler`` to allow passing ``V8Value``
* Updated ``JavetUniversalProxyHandler`` to allow passing ``V8ValueFunction`` as anonymous function
* Updated ``JavetUniversalProxyHandler`` to allow passing ``V8ValueObject`` as anonymous object
* Added ``isClosed()`` to ``IJavetClosable``
* Added error code 602 and 603

0.9.9 Mac OS x86_64
-------------------

* Upgraded Node.js to ``v14.17.4`` `(2021-07-29) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.4>`_
* Added support to Mac OS x86_64
* Removed ``staticClassEnabled`` from ``JavetProxyConverter``

0.9.8 Empower the Promise
-------------------------

* Added ``resolve()`` and ``reject()`` to ``V8ValuePromise``
* Added ``staticClassEnabled`` to ``JavetProxyConverter``
* Added ``construct()`` to ``JavetUniversalProxyHandler``
* Added Dockerfile for Linux x86-64
* Refactored document for how to build Javet

0.9.7 Proxy Converter v2
------------------------

* Added static mode to ``JavetUniversalProxyHandler``
* Added ``ownKeys()`` to ``JavetUniversalProxyHandler``

0.9.6 Proxy Converter v1
------------------------

* Added ``IV8ValueProxy`` and ``V8ValueProxy``
* Added ``getTarget()``, ``getHandler()``, ``isRevoked()`` and ``revoke()`` to ``IV8ValueProxy``
* Added ``createV8ValueProxy()`` to ``V8Runtime``
* Added ``JavetUniversalProxyHandler`` and ``JavetProxyConverter``

0.9.5 V8 v9.2
-------------

* Upgraded V8 to ``v9.2.230.21`` `(2021-07-19) <https://v8.dev/blog/v8-release-92>`_

0.9.4 Multi-process Safe
------------------------

* Added ``JavetConverterConfig``
* Added ``JavetEntityFunction`` for ``JavetObjectConverter``
* Updated ``JavetObjectConverter`` to allow skipping functions
* Added ``getPrototype()`` and ``setPrototype()`` to ``IV8ValueObject``
* Changed the way that Javet libraries are deployed to be multi-process safe

0.9.3 Module Resolver
---------------------

* Upgraded Node.js to ``v14.17.2`` `(2021-07-01) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.2>`_
* Added ``IV8ModuleResolver`` to allow intercepting module resolving callback
* Added ``V8BindEnabler`` to determine whether certain injection is enabled or not
* Added ``NodeModuleProcess#getVersion``
* Updated ``JavetPrimitiveConverter`` and ``JavetObjectConverter`` to check recursion depth for circular structure detection

0.9.2 Anonymous Functions in Converter
--------------------------------------

* Upgraded Node.js to ``v14.17.1`` `(2021-06-15) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.1>`_
* Updated ``JavetObjectConverter`` to handle anonymous functions more efficiently
* Added ``V8ValueBuiltInObject#assign``

0.9.1 V8 in Classloader
-----------------------

* Moved V8 to a custom classloader
* Enabled ``unloadLibrary()`` and ``loadLibrary()`` in ``V8Host``

0.9.0 V8 v9.1
-------------

* Upgraded Node.js to ``v14.17.0`` `(2021-05-11) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V14.md#14.17.0>`_
* Upgraded V8 to ``v9.1.269.28`` `(2021-05-10) <https://v8.dev/blog/v8-release-91>`_
* Added ``thisObjectRequired`` to ``@V8Function`` and ``@V8Property``
