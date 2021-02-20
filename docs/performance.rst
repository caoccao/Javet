=================
Javet Performance
=================

In V8, a context is an execution environment that allows separate, unrelated, JavaScript applications to run in a single instance of V8.

It's recommended to reset context if a new execution environment is required.

Here is the performance test result from i7 10700K + Windows 10. Test case is just ``1 + 1 = 2``.

===============================  ===============
Case                             TPS
===============================  ===============
Single Context with 1 Thread     759,589
Ad-hoc Context with 1 Thread     2,921
Single Context with 8 Threads    4,464,285
Ad-hoc Context with 8 Threads    15,209
===============================  ===============

Reference: https://v8.dev/docs/embed#contexts

[`Home <../README.rst>`_]
