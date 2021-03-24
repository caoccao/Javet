=================
Javet Performance
=================

In V8, a context is an execution environment that allows separate, unrelated, JavaScript applications to run in a single instance of V8.

It's recommended to reset context if a new execution environment is required.

Result
======

Here is the performance test result from i7 10700K + Windows 10. Test case is just ``1 + 1 = 2``.

======== ===============================  ===============
Type     Case                             TPS
======== ===============================  ===============
Node     Single Context with 1 Thread     744,324
Node     Ad-hoc Context with 1 Thread     64
Node     Single Context with 8 Threads    4,548,038
Node     Ad-hoc Context with 8 Threads    365
Node     Ad-hoc Isolate with 1 Thread     51
Node     Ad-hoc Isolate with 8 Threads    273
V8       Single Context with 1 Thread     642,673
V8       Ad-hoc Context with 1 Thread     2,850
V8       Single Context with 8 Threads    4,004,004
V8       Ad-hoc Context with 8 Threads    16,501
V8       Ad-hoc Isolate with 1 Thread     735
V8       Ad-hoc Isolate with 8 Threads    3,546
======== ===============================  ===============

Highlights
==========

* Node.js performs slightly better in **Single Context** mode mainly because the V8 embedded in Node.js is much older than the built-in V8.
* Built-in V8 dramatically outperforms Node.js in **Ad-hoc** mode mainly because Node.js comes with a huge overhead that seriously slows down the context and isolate creation and recycle.
* It's recommended to use built-in V8 unless Node.js is required in some particular cases.

Reference: https://v8.dev/docs/embed#contexts

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
