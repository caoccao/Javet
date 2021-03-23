=================
Javet Performance
=================

In V8, a context is an execution environment that allows separate, unrelated, JavaScript applications to run in a single instance of V8.

It's recommended to reset context if a new execution environment is required.

Here is the performance test result from i7 10700K + Windows 10. Test case is just ``1 + 1 = 2``.

======== ===============================  ===============
Type     Case                             TPS
======== ===============================  ===============
Node     Single Context with 1 Thread     756,143
Node     Ad-hoc Context with 1 Thread     66
Node     Single Context with 8 Threads    4,248,539
Node     Ad-hoc Context with 8 Threads    284
V8       Single Context with 1 Thread     673,400
V8       Ad-hoc Context with 1 Thread     2,883
V8       Single Context with 8 Threads    3,550,821
V8       Ad-hoc Context with 8 Threads    15,791
======== ===============================  ===============

Reference: https://v8.dev/docs/embed#contexts

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
