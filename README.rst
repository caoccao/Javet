Javet
=====

|Maven Central| |Discord| |Donate|

|Linux Build| |Android Build|

.. |Maven Central| image:: https://img.shields.io/maven-central/v/com.caoccao.javet/javet?style=for-the-badge
    :target: https://search.maven.org/search?q=g:com.caoccao.javet

.. |Discord| image:: https://img.shields.io/discord/870518906115211305?label=join%20our%20Discord&style=for-the-badge
    :target: https://discord.gg/R4vvKU96gw

.. |Donate| image:: https://img.shields.io/badge/Donate-green?style=for-the-badge
    :target: https://opencollective.com/javet

.. |Linux Build| image:: https://img.shields.io/github/workflow/status/caoccao/Javet/Linux%20Build%20Artifact?label=Linux%20Build&style=for-the-badge
    :target: https://github.com/caoccao/Javet/actions/workflows/linux_build_artifact.yml

.. |Android Build| image:: https://img.shields.io/github/workflow/status/caoccao/Javet/Android%20Build?label=Android%20Build&style=for-the-badge
    :target: https://github.com/caoccao/Javet/actions/workflows/android_build.yml

`Javet <https://github.com/caoccao/Javet/>`_ is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.

If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_, or visit http://caoccao.blogspot.com/. And the official support channel is at `discord <https://discord.gg/R4vvKU96gw>`_.

💖 If you use Mac OS (x86_64), please be aware that the Mac OS (x86_64) build will discontinue anytime because my `MacBook Air mid-2012 <https://caoccao.blogspot.com/2021/09/macbook-air-mid-2012-from-lion-to-mojave.html>`_ will be soon deprecated by new version of V8. Please `donate <https://opencollective.com/javet>`_ to support me purchasing a new Mac OS (x86_64) device. Or, if you have a retired Mac OS (x86_64) device and are fine with mailing it to me, that will also be great. Thank you for supporting Javet. There is no new official releases after v2.0.2. Please contact the maintainer for the new private releases.

💖 If you use Mac OS (arm64), there is no new releases after v2.0.2. That's because I don't have any Mac OS (arm64) device to produce any new builds. Please `donate <https://opencollective.com/javet>`_ to support me purchasing a new Mac OS (arm64) device.

Major Features
==============

* Linux (x86_64) + Mac OS (x86_64, arm64) + ️Windows (x86_64)
* Android (arm, arm64, x86 and x86_64)
* Node.js ``v18.12.1`` + V8 ``v10.8.168.20``
* Dynamic switch between Node.js and V8 mode (`Which mode do you prefer? <https://github.com/caoccao/Javet/discussions/92>`_)
* Polyfill V8 mode with `Javenode <https://github.com/caoccao/Javenode>`_
* V8 API exposure in JVM
* JavaScript and Java interop
* Native BigInt and Date
* Javet engine pool
* Easy spring integration
* Live debug with Chrome DevTools

Quick Start
===========

Dependency
----------

Maven
^^^^^

.. code-block:: xml

    <!-- Linux and Windows (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>2.0.3</version>
    </dependency>

    <!-- Mac OS (x86_64 and arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>2.0.2</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:2.0.3") // Linux and Windows (x86_64)
    implementation("com.caoccao.javet:javet-macos:2.0.2") // Mac OS (x86_64 and arm64)
    implementation("com.caoccao.javet:javet-android:2.0.3") // Android (arm, arm64, x86 and x86_64)

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:2.0.3' // Linux and Windows (x86_64)
    implementation 'com.caoccao.javet:javet-macos:2.0.2' // Mac OS (x86_64 and arm64)
    implementation 'com.caoccao.javet:javet-android:2.0.3' // Android (arm, arm64, x86 and x86_64)

Hello Javet
-----------

.. code-block:: java

    // Node.js Mode
    try (V8Runtime v8Runtime = V8Host.getNodeInstance().createV8Runtime()) {
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString());
    }

    // V8 Mode
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString());
    }

License
=======

`APACHE LICENSE, VERSION 2.0 <LICENSE>`_.

Documents
=========

* `Javet Intro <https://docs.google.com/presentation/d/1lQ8xIHuywuE0ydqm2w6xq8OeQZO_WeTLYXW9bNflQb8/>`_
* `Javet Javadoc <https://www.caoccao.com/Javet/reference/javadoc/index.html>`_
* `Javet Document Portal <https://www.caoccao.com/Javet/>`_
