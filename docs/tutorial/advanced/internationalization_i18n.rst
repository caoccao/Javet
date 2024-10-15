===========================
Internationalization (i18n)
===========================

Why isn't i18n Supported by Default?
====================================

The default Javet releases don't support i18n for the following reasons.

* Tens of MBs can be saved.
* Performance is slightly better.
* No additional deployment is required.

How to Enable i18n?
===================

* Follow :doc:`../basic/installation` to turn i18n on.
* Download and save ``icudt*.dat`` files from somewhere to a local directory. E.g. ``v8/third_party/icu``, ``node/deps/icu-tmp``.
* Set a flag as follows before the first Node.js or V8 runtime is created.

.. code-block:: java

    // Node.js Mode
    NodeRuntimeOptions.NODE_FLAGS.setIcuDataDir("/dir/of/the/icudt*.dat");
    // Note: It must be a directory.

    // V8 Mode
    V8RuntimeOptions.V8_FLAGS.setIcuDataFile("/path/of/the/icudt*.dat");
    // Note: It must be a file.

Let's Go!
=========

Node.js Mode
------------

.. code-block:: java

    File icuDataDir = new File(JavetOSUtils.WORKING_DIRECTORY)
            .toPath()
            .resolve("../node/deps/icu-tmp")
            .normalize()
            .toFile();
    NodeRuntimeOptions.NODE_FLAGS.setIcuDataDir(icuDataDir.getAbsolutePath());
    try (NodeRuntime nodeRuntime = V8Host.getNodeI18nInstance().createV8Runtime()) {
        System.out.println(nodeRuntime.getExecutor("const a = 123456; a.toLocaleString('en-US');").executeString());
        // 123,456
        System.out.println(nodeRuntime.getExecutor("const us = new Intl.Locale('en-US'); us.language;").executeString());
        // en
        System.out.println(nodeRuntime.getExecutor("JSON.stringify(['Z', 'a', 'z', 'ä'].sort(new Intl.Collator('de').compare));").executeString());
        // ["a","ä","z","Z"]
    }

V8 Mode
-------

.. code-block:: java

    File icuDataFile = new File(JavetOSUtils.WORKING_DIRECTORY)
            .toPath()
            .resolve("../google/v8/third_party/icu/common/icudtl.dat")
            .normalize()
            .toFile();
    V8RuntimeOptions.V8_FLAGS.setIcuDataFile(icuDataFile.getAbsolutePath());
    try (V8Runtime v8Runtime = V8Host.getV8I18nInstance().createV8Runtime()) {
        System.out.println(v8Runtime.getExecutor("const a = 123456; a.toLocaleString('en-US');").executeString());
        // 123,456
        System.out.println(v8Runtime.getExecutor("const us = new Intl.Locale('en-US'); us.language;").executeString());
        // en
        System.out.println(v8Runtime.getExecutor("JSON.stringify(['Z', 'a', 'z', 'ä'].sort(new Intl.Collator('de').compare));").executeString());
        // ["a","ä","z","Z"]
    }
