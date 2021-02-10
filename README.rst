Javet
=====

Javet is Java + V8 (JAVa + V + EighT). It is yet another way of embedding V8 in Java. It was inspired by J2V8. I'll try to keep up with latest V8 in a slow pace. If you like my work, please **Star** this project. And, you may visit http://caoccao.blogspot.com/.

Features
========

* It supports both Windows and Linux with V8 v8.3.110.9.
* It exposes the majority of V8 API in JVM.
* It allows injecting JS functions into V8.
* It supports ``BigInt <-> Long``, ``Date <-> ZonedDateTime``.
* It supports Javet engine pool like DB connection pool with a huge performance improvement.

Project Status
==============

This is a personal project for now. It supports limited features compared to J2V8.

* `Build <docs/build.rst>`_
* `Development <docs/development.rst>`_
* `Tutorial <docs/tutorial/index.rst>`_
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
