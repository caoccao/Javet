Javet
=====

|Maven Central| |Discord| |Donate|

|Linux Build|

.. |Maven Central| image:: https://img.shields.io/maven-central/v/com.caoccao.javet/javet.svg
    :target: https://search.maven.org/search?q=g:com.caoccao.javet

.. |Gitter Chatroom| image:: https://badges.gitter.im/caoccao/Javet.svg
    :target: https://gitter.im/caoccao/Javet?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge

.. |Discord| image:: https://img.shields.io/badge/join%20our-Discord-%237289DA%20
    :target: https://discord.gg/R4vvKU96gw

.. |Donate| image:: https://img.shields.io/badge/Donate-PayPal-green.svg
    :target: https://paypal.me/caoccao?locale.x=en_US

.. |Linux Build| image:: https://github.com/caoccao/Javet/actions/workflows/linux_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/linux_build.yml

Javet is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.

If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_, or visit http://caoccao.blogspot.com/. And the official support channel is at `discord <https://discord.gg/R4vvKU96gw>`_.

Major Features
==============

* Linux + Mac OS + ️Windows (x86_64)
* Node.js ``v14.17.4`` + V8 ``v9.2.230.21``
* Dynamic switch between Node.js and V8
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
        <version>0.9.9</version>
    </dependency>

    <!-- Mac OS (x86_64 Only) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>0.9.9</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:0.9.9") // Linux or Windows
    implementation("com.caoccao.javet:javet-macos:0.9.9") // Mac OS (x86_64 Only)

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:0.9.9' // Linux or Windows
    implementation 'com.caoccao.javet:javet-macos:0.9.9' // Mac OS (x86_64 Only)

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
