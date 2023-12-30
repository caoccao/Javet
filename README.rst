Javet
=====

|Maven Central| |Discord| |Donate|

|Linux x86_64 Build| |MacOS x86_64 Build| |Android Build|

.. |Maven Central| image:: https://img.shields.io/maven-central/v/com.caoccao.javet/javet?style=for-the-badge
    :target: https://central.sonatype.com/search?q=g:com.caoccao.javet

.. |Discord| image:: https://img.shields.io/discord/870518906115211305?label=join%20our%20Discord&style=for-the-badge
    :target: https://discord.gg/R4vvKU96gw

.. |Donate| image:: https://img.shields.io/badge/Donate-green?style=for-the-badge
    :target: https://opencollective.com/javet

.. |Linux x86_64 Build| image:: https://github.com/caoccao/Javet/actions/workflows/linux_x86_64_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/linux_x86_64_build.yml

.. |MacOS x86_64 Build| image:: https://github.com/caoccao/Javet/actions/workflows/macos_x86_64_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/macos_x86_64_build.yml

.. |Android Build| image:: https://github.com/caoccao/Javet/actions/workflows/android_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/android_build.yml

`Javet <https://github.com/caoccao/Javet/>`_ is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.

If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_, or visit https://sjtucaocao.medium.com/ or https://caoccao.blogspot.com/. And the official support channel is at `discord <https://discord.gg/R4vvKU96gw>`_.

💖 If you like my work, please `donate <https://opencollective.com/javet>`_ to support me. If you have a retired Mac OS (x86_64) device and are fine with mailing it to me, that will be great because I don't have such device to support the community. Thank you for supporting Javet.

Major Features
==============

=========== ======= ======= ======= =======
CPU Arch    Android Linux   MacOS   Windows
=========== ======= ======= ======= =======
x86         ✔️        ❌      ❌       ❌
x86_64      ✔️        ✔️       ✔️        ✔️
arm         ✔️        ❌      ❌       ❌
arm64       ✔️        ✔️       ✔️        ❌
=========== ======= ======= ======= =======

* Node.js ``v20.10.0`` + V8 ``v12.1.285.17``
* Dynamic switch between Node.js and V8 mode (`Which mode do you prefer? <https://github.com/caoccao/Javet/discussions/92>`_)
* Polyfill V8 mode with `Javenode <https://github.com/caoccao/Javenode>`_
* V8 API exposure in JVM
* JavaScript and Java interop
* Native BigInt and Date
* Javet engine pool
* Easy spring integration
* Live debug with Chrome DevTools
* AST analysis with `JavetSanitizer <https://github.com/caoccao/JavetSanitizer>`_
* Live interaction with `JavetShell <https://github.com/caoccao/JavetShell>`_

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
        <version>3.0.3</version>
    </dependency>

    <!-- Linux (arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-linux-arm64</artifactId>
        <version>3.0.3</version>
    </dependency>

    <!-- Mac OS (x86_64 and arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>3.0.3</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:3.0.3") // Linux and Windows (x86_64)
    implementation("com.caoccao.javet:javet-linux-arm64:3.0.3") // Linux (arm64)
    implementation("com.caoccao.javet:javet-macos:3.0.3") // Mac OS (x86_64 and arm64)
    implementation("com.caoccao.javet:javet-android:3.0.3") // Android (arm, arm64, x86 and x86_64)

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:3.0.3' // Linux and Windows (x86_64)
    implementation 'com.caoccao.javet:javet-linux-arm64:3.0.3' // Linux (arm64)
    implementation 'com.caoccao.javet:javet-macos:3.0.3' // Mac OS (x86_64 and arm64)
    implementation 'com.caoccao.javet:javet-android:3.0.3' // Android (arm, arm64, x86 and x86_64)

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

Sponsors
========

`HiveMQ <https://www.hivemq.com/>`_

License
=======

`APACHE LICENSE, VERSION 2.0 <https://github.com/caoccao/Javet/blob/main/LICENSE>`_

Blog
====

* `Performance Comparison of GraalJS, Javet and Nashorn <https://medium.com/towardsdev/performance-comparison-of-graaljs-javet-and-nashorn-7bae6925826a>`_
* `How to Elegantly Expose JsonNode in V8 <https://medium.com/stackademic/how-to-elegantly-expose-jsonnode-in-v8-638aff9da549>`_
* `How to Compromise V8 on JVM <https://sjtucaocao.medium.com/how-to-compromise-v8-on-jvm-ceb385572461>`_
* `Is TypeScript a Good Choice for a Script Engine? <https://sjtucaocao.medium.com/is-typescript-a-good-choice-for-a-script-engine-01fe69921ace>`_

Documents
=========

* `Javet Intro <https://docs.google.com/presentation/d/1lQ8xIHuywuE0ydqm2w6xq8OeQZO_WeTLYXW9bNflQb8/>`_
* `Javet Javadoc <https://www.caoccao.com/Javet/reference/javadoc/index.html>`_
* `Javet Document Portal <https://www.caoccao.com/Javet/>`_
