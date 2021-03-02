Javet
=====

|Maven Central|

.. |Maven Central| image:: https://img.shields.io/maven-central/v/com.caoccao.javet/javet.svg
    :target: https://search.maven.org/search?q=g:com.caoccao.javet

Javet is Java + V8 (JAVa + V + EighT). It is yet another way of embedding V8 in Java. It was inspired by J2V8. I'll try to keep up with latest V8 in a slow pace. If you like my work, please **Star** this project. And, you may follow me `@sjtucaocao <https://twitter.com/sjtucaocao>`_ or visit http://caoccao.blogspot.com/.

Major Features
==============

* Linux + Windows
* V8 ``v8.9.255``
* Exposure of the majority of V8 API in JVM
* JS functions interception
* Native BigInt and Date
* Javet engine pool
* Easy spring integration

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
        <version>0.7.2</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:0.7.2")

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:0.7.2'

Hello Javet
-----------

.. code-block:: java

    try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime().lock()) {
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString());
    }

Documents
=========

* `Development <docs/development/index.rst>`_
    * `Build <docs/build.rst>`_
    * `Design <docs/design.rst>`_
    * `Performance <docs/performance.rst>`_
* `Tutorial <docs/tutorial/index.rst>`_
* `Release Notes <docs/release_notes.rst>`_
* `FAQ <docs/faq/index.rst>`_
* `TODO List <docs/todo_list.rst>`_

License
=======

Javet follows `APACHE LICENSE, VERSION 2.0 <LICENSE>`_.
