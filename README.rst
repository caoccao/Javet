Javet
=====

.. image:: https://badges.gitter.im/caoccao/Javet.svg
   :alt: Join the chat at https://gitter.im/caoccao/Javet
   :target: https://gitter.im/caoccao/Javet?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge

|Maven Central|

.. |Maven Central| image:: https://img.shields.io/maven-central/v/com.caoccao.javet/javet.svg
    :target: https://search.maven.org/search?q=g:com.caoccao.javet

Javet is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.

If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_ or visit http://caoccao.blogspot.com/.

Major Features
==============

* Linux + Windows
* Node.js ``14.16.0`` + V8 ``v8.9.255``
* Dynamic switch between Node.js and V8
* Exposure of the majority of V8 API in JVM
* JS function interception
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

    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>0.8.0</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:0.8.0")

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:0.8.0'

Hello Javet
-----------

.. code-block:: java

    // Node Mode
    try (V8Runtime v8Runtime = V8Host.getNodeInstance().createV8Runtime()) {
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString());
    }

    // V8 Mode
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString());
    }

Documents
=========

* `Development <docs/development/index.rst>`_

    * `Build <docs/development/build.rst>`_
    * `Design <docs/development/design.rst>`_
    * `Performance <docs/development/performance.rst>`_

* `Tutorial <docs/tutorial/index.rst>`_
* `Release Notes <docs/release_notes.rst>`_
* `FAQ <docs/faq/index.rst>`_
* `TODO List <docs/todo_list.rst>`_

License
=======

Javet follows `APACHE LICENSE, VERSION 2.0 <LICENSE>`_.
