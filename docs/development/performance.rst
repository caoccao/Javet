=================
Javet Performance
=================

In V8, a context is an execution environment that allows separate, unrelated, JavaScript applications to run in a single instance of V8.

It's recommended to reset context if a new execution environment is required.

Performance Comparisons of Node.js Mode and V8 Mode
===================================================

Here is the performance test result from i7 10700K + Windows 10. Test case is just ``1 + 1 = 2``.

======== ===============================  ===============
Type     Case                             TPS
======== ===============================  ===============
Node     Single Context with 1 Thread     751,032
Node     Ad-hoc Context with 1 Thread     65
Node     Single Context with 8 Threads    4,410,143
Node     Ad-hoc Context with 8 Threads    363
Node     Ad-hoc Isolate with 1 Thread     51
Node     Ad-hoc Isolate with 8 Threads    263
V8       Single Context with 1 Thread     647,458
V8       Ad-hoc Context with 1 Thread     2,846
V8       Single Context with 8 Threads    3,968,253
V8       Ad-hoc Context with 8 Threads    16,508
V8       Ad-hoc Isolate with 1 Thread     748
V8       Ad-hoc Isolate with 8 Threads    3,660
======== ===============================  ===============

Highlights
----------

* Node.js performs slightly better in **Single Context** mode mainly because the V8 embedded in Node.js is much older than the built-in V8.
* Built-in V8 dramatically outperforms Node.js in **Ad-hoc** mode mainly because Node.js comes with a huge overhead that seriously slows down the context and isolate creation and recycle.
* It's recommended to use built-in V8 unless Node.js is required in some particular cases.
* Node.js on Linux is built as `position independent code <https://en.wikipedia.org/wiki/Position-independent_code>`_ which comes with an additional performance overhead.

Reference: https://v8.dev/docs/embed#contexts

Java VS Wasm Benchmarks
=======================

When to use Java bindings and when to use Wasm (WebAssembly) is quite a concern. The `Programming-Language-Benchmarks <https://github.com/hanabi1224/Programming-Language-Benchmarks>`_ provides decent `benchmarks <https://programming-language-benchmarks.vercel.app/java-vs-wasm>`_ between Java and Wasm.

Here is a summary of the benchmarks generated on Jul 13, 2023.

===================== ========= ========= ===========
Problem               Java (ms) Wasm (ms) Wasm / Java
===================== ========= ========= ===========
binarytrees           672       2048      👍 305%
edigits               814       484       59%
fannkuch-redux        1660      4639      👍 279%
fasta                 512       367       72%
helloworld            72        7         10%
mandelbrot            1411      1095      78%
merkletrees           512       1134      👍 221%
nbody                 546       472       86%
nsieve                694       586       84%
pidigits              4975      2761      55%
spectral-norm         4198      4128      98%
coro-prime-sieve      796       105       13%
===================== ========= ========= ===========

.. note::

    Both benchmarks are not JIT optimized because there's no warm-up. So, the actual performance could be much higher. And further benchmarks are needed to measure the performance of the JIT.

Please choose the code behind technology based on the actual benchmarks because according to the tests the performance difference is not significant and depends on the particular algorithms. However, please be aware that Wasm comes with some drawbacks.

* The Wasm code size is considerable. e.g. An HelloWorld Wasm package written in rust is 21+KB (.js 4+KB + .wasm 16+KB). Fetching the files from storage, parsing and compiling the files also takes considerable amount of time which may eventually slow down the whole execution.
* Wasm code initialization and execution is asynchronous which means the business logic has to deal with ``async``, ``await`` or ``Promise`` and ``V8Runtime.await()`` has to be called. The Java bindings can be either synchronous or asynchronous. That's more flexible.
* External Wasm applications are like a black box to the server side applications. Enabling external Wasm execution implies some security vulnerabilities.

Javet Performance Improvements
==============================

The performance improvements among some major releases are listed at https://github.com/caoccao/JavetPerf.
