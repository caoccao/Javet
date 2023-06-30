Javet
=====

|Maven Central| |Discord| |Donate|

|Linux Build| |MacOS Build| |Android Build|

.. |Maven Central| image:: https://img.shields.io/maven-central/v/com.caoccao.javet/javet?style=for-the-badge
    :target: https://search.maven.org/search?q=g:com.caoccao.javet

.. |Discord| image:: https://img.shields.io/discord/870518906115211305?label=join%20our%20Discord&style=for-the-badge
    :target: https://discord.gg/R4vvKU96gw

.. |Donate| image:: https://img.shields.io/badge/Donate-green?style=for-the-badge
    :target: https://opencollective.com/javet

.. |Linux Build| image:: https://github.com/caoccao/Javet/actions/workflows/linux_build_artifact.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/linux_build_artifact.yml

.. |MacOS Build| image:: https://github.com/caoccao/Javet/actions/workflows/macos_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/macos_build.yml

.. |Android Build| image:: https://github.com/caoccao/Javet/actions/workflows/android_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/android_build.yml

`Javet <https://github.com/caoccao/Javet/>`_ is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.

If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_, or visit http://caoccao.blogspot.com/. And the official support channel is at `discord <https://discord.gg/R4vvKU96gw>`_.

💖 If you like my work, please `donate <https://opencollective.com/javet>`_ to support me. If you have a retired Mac OS (x86_64) device and are fine with mailing it to me, that will be great because I don't have such device to support the community. Thank you for supporting Javet.

Major Features
==============

* Linux (x86_64) + Mac OS (x86_64, arm64) + ️Windows (x86_64)
* Android (arm, arm64, x86 and x86_64)
* Node.js ``v18.16.1`` + V8 ``v11.5.150.12``
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
        <version>2.2.2</version>
    </dependency>

    <!-- Mac OS (x86_64 and arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>2.2.2</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:2.2.2") // Linux and Windows (x86_64)
    implementation("com.caoccao.javet:javet-macos:2.2.2") // Mac OS (x86_64 and arm64)
    implementation("com.caoccao.javet:javet-android:2.2.2") // Android (arm, arm64, x86 and x86_64)

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:2.2.2' // Linux and Windows (x86_64)
    implementation 'com.caoccao.javet:javet-macos:2.2.2' // Mac OS (x86_64 and arm64)
    implementation 'com.caoccao.javet:javet-android:2.2.2' // Android (arm, arm64, x86 and x86_64)

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
