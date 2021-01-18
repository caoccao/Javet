Javet
=====

Javet is Java + V8 (JAVa + V + EighT). It is yet another way of embedding V8 in Java. It was inspired by J2V8. I'll try to keep up with latest V8 in a slow pace. If you like my work, please **Star** this project. And, you may send messages to `@sjtucaocao <https://twitter.com/sjtucaocao>`_.

Why Javet?
==========

Please refer to `History with J2V8 <docs/history_with_j2v8.rst>`_ for detail.

Well, Why not Let Me Start from Scratch?
========================================

Sometimes starting from scratch implies lower cost than upgrading an existing solution. I think it might be true here in this project. I've learned quite a lot by manually fixing the Windows and Linux build system.

Also, I've got many ideas on how the API will look like. I think I would be able to write a new one from scratch and leave J2V8 behind.

Javet Project Status
====================

This is kind of a personal project, yet still on paper.

Javet TODO List
===============

* To start from scratch so that there is no legal issues to J2V8.
* To support Windows and Linux. Supporting MacOS calls for your help.
* To implement unified ``V8Object`` covering primitive types.
* To implement V8 runtime pool like DB connection pool. The performance test shows there is a huge gap (millions vs. hundreds) between pooled and non-pooled V8 runtime.
* To support more types that are not supported by ECMAScript, e.g. Long, BigDecimal, etc.
* To revive NodeJS.
* To implement runtime debugging capability.

Javet License
=============

For now, Javet follows `Eclipse Public License - v 2.0 <LICENSE>`_.
