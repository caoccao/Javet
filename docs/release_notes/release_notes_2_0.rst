===================
Release Notes 2.0.x
===================

2.0.3 V8 v10.9
--------------

* Added ``compileV8ValueFunction`` to ``IV8Executor``
* Added ``getCachedData`` to ``IV8Executor``, ``V8Module`` and ``V8Script``
* Added ``getCachedData``, ``isWrapped`` to ``IV8ValueFunction``
* Added ``cachedData`` to ``V8StringExecutor`` so that function, module and script support cached data

2.0.2 V8 v10.8
--------------

* Upgraded Node.js to ``v18.12.1`` `(2022-11-03) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V18.md#18.12.1>`_
* Upgraded V8 to ``v10.8.168.20`` `(2022-11-15) <https://v8.dev/blog/v8-release-108>`_
* Revised ``IV8ValueFunction.copyScopeInfoFrom()`` to clone the scope info
* Added ``ScopeInfos``, ``ScopeInfos``, ``getScopeInfos`` to ``IV8ValueFunction``
* Added ``getOwnPropertyNameStrings`` to ``IV8ValueObject``
* Added ``referenceCopy`` to ``IV8Value.toClone()``

2.0.1 V8 v10.7
--------------

* Upgraded Node.js to ``v18.12.0`` `(2022-10-25) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V18.md#18.12.0>`_
* Upgraded V8 to ``v10.7.193.16`` `(2022-10-14) <https://v8.dev/blog/v8-release-107>`_
* Renamed ``JavetUniversalProxy*`` to ``JavetDynamicProxy*``
* Added ``JavetDynamicObjectFactory`` based on ``ByteBuddy``
* Added ``copyContextFrom``, ``copyScopeInfoFrom``, ``getScriptSource``, ``setScriptSource``, ``SetSourceCodeOptions``, ``isCompiled``, ``canDiscardCompiled``, ``discardCompiled`` to ``IV8ValueFunction``

2.0.0 Node.js v18.10
--------------------

* Upgraded Node.js to ``v18.10.0`` `(2022-09-28) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V18.md#18.10.0>`_
* Upgraded V8 to ``v10.6.194.14`` `(2022-09-23) <https://v8.dev/blog/v8-release-106>`_
* Upgraded to Visual Studio 2022
