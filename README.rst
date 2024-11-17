Javet
=====

|Maven Central| |Discord| |Donate|

|Linux x86_64 Build| |MacOS x86_64 Build| |MacOS arm64 Build| |Windows x86_64 Build| |Android Node Build| |Android V8 Build|

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

.. |MacOS arm64 Build| image:: https://github.com/caoccao/Javet/actions/workflows/macos_arm64_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/macos_arm64_build.yml

.. |Windows x86_64 Build| image:: https://github.com/caoccao/Javet/actions/workflows/windows_x86_64_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/windows_x86_64_build.yml

.. |Android Node Build| image:: https://github.com/caoccao/Javet/actions/workflows/android_node_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/android_node_build.yml

.. |Android V8 Build| image:: https://github.com/caoccao/Javet/actions/workflows/android_v8_build.yml/badge.svg
    :target: https://github.com/caoccao/Javet/actions/workflows/android_v8_build.yml

`Javet <https://github.com/caoccao/Javet/>`_ is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.

If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_, or visit https://blog.caoccao.com/ or https://caoccao.blogspot.com/. And the official support channel is at `discord <https://discord.gg/R4vvKU96gw>`_.

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

* Node.js ``v22.11.0`` + V8 ``v13.1.201.8``
* i18n and non-i18n
* Dynamic switch between Node.js and V8 mode
* Polyfill V8 mode with `Javenode <https://github.com/caoccao/Javenode>`_
* V8 API exposure in JVM
* JavaScript and Java interop
* Native BigInt and Date
* Javet engine pool
* Easy spring integration
* Live debug with Chrome DevTools
* AST analysis with `swc4j <https://github.com/caoccao/swc4j>`_
* JS, TS, JSX, TSX transformation and transpilation with `swc4j <https://github.com/caoccao/swc4j>`_
* Enhance JVM via Byte-code with `JavetBuddy <https://github.com/caoccao/JavetBuddy>`_
* Live interaction with `JavetShell <https://github.com/caoccao/JavetShell>`_

Quick Start
===========

Dependency
----------

Maven
^^^^^

.. code-block:: xml

    <!-- Core (Must-have) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- Node.js Linux (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-node-linux-x86_64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- Node.js Linux (arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-node-linux-arm64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- Node.js Mac OS (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-node-macos-x86_64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- Node.js Mac OS (arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-node-macos-arm64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- Node.js Windows (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-node-windows-x86_64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- V8 Linux (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-v8-linux-x86_64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- V8 Linux (arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-v8-linux-arm64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- V8 Mac OS (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-v8-macos-x86_64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- V8 Mac OS (arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-v8-macos-arm64</artifactId>
        <version>4.1.1</version>
    </dependency>

    <!-- V8 Windows (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-v8-windows-x86_64</artifactId>
        <version>4.1.1</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:4.1.1") // Core (Must-have)
    implementation("com.caoccao.javet:javet-node-linux-arm64:4.1.1")
    implementation("com.caoccao.javet:javet-node-linux-x86_64:4.1.1")
    implementation("com.caoccao.javet:javet-node-macos-arm64:4.1.1")
    implementation("com.caoccao.javet:javet-node-macos-x86_64:4.1.1")
    implementation("com.caoccao.javet:javet-node-windows-x86_64:4.1.1")
    implementation("com.caoccao.javet:javet-v8-linux-arm64:4.1.1")
    implementation("com.caoccao.javet:javet-v8-linux-x86_64:4.1.1")
    implementation("com.caoccao.javet:javet-v8-macos-arm64:4.1.1")
    implementation("com.caoccao.javet:javet-v8-macos-x86_64:4.1.1")
    implementation("com.caoccao.javet:javet-v8-windows-x86_64:4.1.1")

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:4.1.1' // Core (Must-have)
    implementation 'com.caoccao.javet:javet-node-linux-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-linux-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-macos-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-macos-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-windows-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-linux-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-linux-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-macos-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-macos-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-windows-x86_64:4.1.1'

For more detail, please visit the `installation <https://www.caoccao.com/Javet/tutorial/basic/installation.html>`_ page.

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

`HiveMQ <https://www.hivemq.com/>`_ | `SheetJS <https://www.sheetjs.com/>`_

License
=======

`APACHE LICENSE, VERSION 2.0 <https://github.com/caoccao/Javet/blob/main/LICENSE>`_

Blog
====

* `Performance Comparison of GraalJS, Javet and Nashorn <https://blog.caoccao.com/performance-comparison-of-graaljs-javet-and-nashorn-7bae6925826a>`_
* `How to Elegantly Expose JsonNode in V8 <https://blog.caoccao.com/how-to-elegantly-expose-jsonnode-in-v8-638aff9da549>`_
* `How to Compromise V8 on JVM <https://blog.caoccao.com/how-to-compromise-v8-on-jvm-ceb385572461>`_
* `Is TypeScript a Good Choice for a Script Engine? <https://blog.caoccao.com/is-typescript-a-good-choice-for-a-script-engine-01fe69921ace>`_
* `Run TypeScript Directly in Java <https://blog.caoccao.com/run-typescript-directly-in-java-82b7003b44b8>`_

Documents
=========

* `Javet Intro <https://docs.google.com/presentation/d/1lQ8xIHuywuE0ydqm2w6xq8OeQZO_WeTLYXW9bNflQb8/>`_
* `Javet Javadoc <https://www.caoccao.com/Javet/reference/javadoc/index.html>`_
* `Javet Document Portal <https://www.caoccao.com/Javet/>`_
