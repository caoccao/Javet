Javet
=====

|Maven Central| |Discord| |Donate| |Linux Build|

.. |Maven Central| image:: https://img.shields.io/maven-central/v/com.caoccao.javet/javet?style=for-the-badge
    :target: https://search.maven.org/search?q=g:com.caoccao.javet

.. |Discord| image:: https://img.shields.io/discord/870518906115211305?label=join%20our%20Discord&style=for-the-badge
    :target: https://discord.gg/R4vvKU96gw

.. |Donate| image:: https://img.shields.io/badge/Donate-Paypal-green?style=for-the-badge
    :target: https://paypal.me/caoccao?locale.x=en_US

.. |Linux Build| image:: https://img.shields.io/github/workflow/status/caoccao/Javet/Linux%20Build?label=Linux%20Build&style=for-the-badge
    :target: https://github.com/caoccao/Javet/actions/workflows/linux_build.yml

Javet is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.

If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_, or visit http://caoccao.blogspot.com/. And the official support channel is at `discord <https://discord.gg/R4vvKU96gw>`_.

💖 If you use Mac OS (x86_64), please be aware that the Mac OS (x86_64) build will discontinue anytime because my MacBook Air mid-2012 will be soon deprecated by new version of V8. Please `donate <https://paypal.me/caoccao?locale.x=en_US>`_ to support me purchasing a new Mac OS (x86_64) device.

💖 If you use Mac OS (arm64), unfortunately there is no Mac OS (arm64) build because I don't have any Mac OS (arm64) device. Please `donate <https://paypal.me/caoccao?locale.x=en_US>`_ to support me purchasing a new Mac OS (arm64) device.

Major Features
==============

* Linux + Mac OS + ️Windows (x86_64)
* Node.js ``v14.17.6`` + V8 ``v9.3.345.16``
* Dynamic switch between Node.js and V8 mode
* Polyfill V8 mode with `Javenode <https://github.com/caoccao/Javenode>`_
* Exposure of the majority of V8 API in JVM
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

    <!-- Linux or Windows -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>0.9.11</version>
    </dependency>

    <!-- Mac OS (x86_64 Only) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>0.9.11</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:0.9.11") // Linux or Windows
    implementation("com.caoccao.javet:javet-macos:0.9.11") // Mac OS (x86_64 Only)

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:0.9.11' // Linux or Windows
    implementation 'com.caoccao.javet:javet-macos:0.9.11' // Mac OS (x86_64 Only)

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

Documents
=========

* `Javet Intro <https://docs.google.com/presentation/d/1lQ8xIHuywuE0ydqm2w6xq8OeQZO_WeTLYXW9bNflQb8/>`_
* `Tutorial <docs/tutorial/index.rst>`_
* `Reference <docs/reference/index.rst>`_
* `Release Notes <docs/release_notes.rst>`_
* `FAQ <docs/faq/index.rst>`_
* `Development <docs/development/index.rst>`_

License
=======

`APACHE LICENSE, VERSION 2.0 <LICENSE>`_.
