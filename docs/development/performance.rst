=================
Javet Performance
=================

In V8, a context is an execution environment that allows separate, unrelated, JavaScript applications to run in a single instance of V8.

It's recommended to reset context if a new execution environment is required.

Here is the performance test result from i7 10700K + Windows 10. Test case is just ``1 + 1 = 2``.

===============================  ===============
Case                             TPS
===============================  ===============
Single Context with 1 Thread     752,728
Ad-hoc Context with 1 Thread     2,895
Single Context with 8 Threads    4,268,943
Ad-hoc Context with 8 Threads    15,278
===============================  ===============

Reference: https://v8.dev/docs/embed#contexts

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
