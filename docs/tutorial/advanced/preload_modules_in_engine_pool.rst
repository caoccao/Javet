==============================
Preload Modules in Engine Pool
==============================

Is it Possible to Preload Node.js Modules in V8 Mode in Javet Engine Pool?
==========================================================================

This is a common question asked by many Javet users. The answer is YES.

How to Do That?
===============

* Find minified version of the Node.js module we want to load. Usually after running ``npm install``, we may find that minified version somewhere under ``node_modules``.
* Write a new module resolver that can load the minified Node.js module. Be careful with the module type, it may be CMD or ESM.
* Create a new engine pool by subclassing the built-in engine pool.
* Create a new engine by subclassing the built-in engine.
* The new engine pool shall return the new engine.
* The new engine shall have the new module resolver set to ``V8Runtime``.
* We are all set.

The complete example can be found at `JavetExamples/PreloadNodeJSModulesInJavetEnginePool <https://github.com/caoccao/JavetExamples/tree/main/PreloadNodeJSModulesInJavetEnginePool>`_. Clone and run that project.

.. note::
  * How about native Node.js module? Yes, that is possible but quite hard in practice. Please contact the maintainer if you really want to do that.
