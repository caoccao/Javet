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
        <version>0.7.1</version>
    </dependency>

Gradle Kotlin DSL
^^^^^^^^^^^^^^^^^

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:0.7.1")

Gradle Groovy DSL
^^^^^^^^^^^^^^^^^

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:0.7.1'

Hello Javet
-----------

.. code-block:: java

    try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime().lock()) {
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString());
    }

Documents
=========

* `Build <docs/build.rst>`_
* `Development <docs/development.rst>`_
* `Tutorial <docs/tutorial/index.rst>`_
* `Performance <docs/performance.rst>`_
* `Release Notes <docs/release_notes.rst>`_
* `FAQ <docs/faq/index.rst>`_
* `TODO List <docs/todo_list.rst>`_

Motivation
==========

I used to take a try of J2V8 and find it's quite compelling. However, J2V8 is slowly dying, with serious memory leak issues, V8 version issue, etc.

Sometimes starting from scratch implies lower cost than upgrading an existing solution. I think it might be true here in this project. I've learned quite a lot by manually fixing the Windows and Linux build system of J2V8.

Also, I had got many ideas on how the API will look like. At the end of 2020, I thought I would be able to write a new one from scratch and leave J2V8 behind. Indeed, I made it few months later.

Please refer to `History with J2V8 <docs/faq/history_with_j2v8.rst>`_ for detail.

License
=======

Javet follows `APACHE LICENSE, VERSION 2.0 <LICENSE>`_.
