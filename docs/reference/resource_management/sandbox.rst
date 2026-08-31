=======
Sandbox
=======

The V8 sandbox is a security boundary around V8's heap. Everything V8 allocates for JavaScript lives inside one reserved region of the virtual address space, and V8 treats every pointer inside that region as untrusted. A memory corruption bug in the JavaScript engine therefore cannot easily be turned into an attack on the rest of the process, because a corrupted pointer can only ever address memory that is already inside the sandbox.

Javet enables the sandbox in V8 mode on the desktop platforms. It costs almost nothing in physical memory and a great deal in virtual address space. That trade-off is invisible to most applications and surprising to a few, so this page describes what it costs, where the ceiling is, and how to stay under it.

Where the Sandbox Is Enabled
============================

+-------------------------+-------------------+
| Platform                | Sandbox (V8 mode) |
+=========================+===================+
| Linux x86_64, arm64     | Yes               |
+-------------------------+-------------------+
| macOS x86_64, arm64     | Yes               |
+-------------------------+-------------------+
| Windows x86_64          | Yes               |
+-------------------------+-------------------+
| Android (all)           | No                |
+-------------------------+-------------------+

V8 Mode
-------

Since v5.0.8, Javet runs V8 mode with multi-cage pointer compression. Every ``V8Runtime`` is created in its own ``v8::IsolateGroup``, which gives it a private 4 GB pointer compression cage instead of sharing one cage across the process. The sandbox follows the cage: **one sandbox per V8 runtime**.

This is what makes the address space arithmetic below matter. It is also what makes it bounded and predictable, because the reservation is released when the runtime is closed.

Node.js Mode
------------

Node.js mode does not use multi-cage pointer compression. All runtimes are allocated in a single shared isolate group, so the per-runtime multiplication described here does not apply.

What a Sandbox Costs
====================

A sandbox is one contiguous reservation, sized entirely by compile-time V8 constants:

+-------------------------+-----------+-------------------------------------------------------------+
| Part                    | Size      | Purpose                                                     |
+=========================+===========+=============================================================+
| Leading guard region    | 64 GB     | Absorbs out-of-bounds accesses below the sandbox            |
+-------------------------+-----------+-------------------------------------------------------------+
| The sandbox itself      | 1024 GB   | Everything V8 allocates for JavaScript                      |
+-------------------------+-----------+-------------------------------------------------------------+
| Trailing guard region   | 288 GB    | Absorbs out-of-bounds accesses above the sandbox, including |
|                         |           | the worst case of a 32-bit index into an array of 64-bit    |
|                         |           | values at a 32 GB offset                                    |
+-------------------------+-----------+-------------------------------------------------------------+
| **Total per runtime**   |**1376 GB**|                                                             |
+-------------------------+-----------+-------------------------------------------------------------+

So every V8 mode runtime costs roughly **1.34 TiB of virtual address space**.

.. note::

    Reserved is not committed. The reservation is mapped with no access permissions and no backing store: it consumes no physical memory, no swap and no page table entries until V8 commits pages inside it. A process holding dozens of runtimes can show a virtual size in the tens of terabytes while its resident set stays in the hundreds of megabytes. Tools that report ``VSZ`` will look alarming and are not telling you about a memory leak.

How Many Runtimes Fit in a Process?
===================================

On 64-bit Linux the kernel hands out user mappings from a 128 TiB window by default, regardless of how much virtual address space the CPU can actually address. That window is the real limit:

.. code-block:: text

    128 TiB / 1.34 TiB per runtime  ~=  95 runtimes

Call it **roughly 90 to 100 concurrent V8 mode runtimes per process**, less in practice because the reservations must be contiguous and 4 GB aligned, so fragmentation costs some of them. macOS and Windows are in the same order of magnitude.

This is a limit on *concurrently live* runtimes, not on runtimes created over the lifetime of the process. Closing a runtime releases its reservation immediately, so an application that closes what it opens can create millions of them and never come close to the ceiling.

What Happens at the Limit
=========================

V8 does not fail cleanly when the reservation cannot be satisfied. It halves the request and retries as a *partially reserved* sandbox, and that fallback path chooses a base address at random across the whole address space the CPU reports rather than staying in the low range the ordinary path uses.

On a CPU that reports 57-bit virtual addresses — five-level paging, common on recent server parts and on cloud instances built from them — that random address is very likely to land above the 2\ :sup:`48` boundary. Some of V8's internal pointer encodings have room for only 48 bits, so a heap address above that boundary is silently truncated on the way in. The truncated pointer is then dereferenced by the first script the runtime executes, and the process dies with a ``SIGSEGV`` inside the Javet native library.

.. caution::

    This crash is a symptom of holding too many live runtimes. It is not caused by the script that happened to be running when it fired, and the crashing script is usually an innocent one that merely had the bad luck of being first into a newly created runtime. When you see a ``SIGSEGV`` inside the native library after a long-running process has created many runtimes, count the runtimes before you study the JavaScript.

Two conditions have to coincide — a CPU that reports more than 48 virtual address bits, and enough address space pressure to reach the fallback — which is why the failure is intermittent. A process may run for a long time, or crash on one machine and not another, on the same code.

.. note::

    A ``SIGSEGV`` at ``createV8Runtime()`` is not necessarily this problem. See :doc:`../../faq/troubleshooting/sigsegv_at_createv8runtime_in_v8_mode_on_aws` for an unrelated crash with the same signature and a different cause.

Diagnosing
==========

Count the live runtimes first. If the number grows over the lifetime of the process, the runtimes are being leaked and everything below is a confirmation rather than an investigation.

Count the sandbox reservations on Linux:

.. code-block:: shell

    grep -c 'v8-sandbox' /proc/<pid>/maps

Read the peak virtual size, which is what actually runs out:

.. code-block:: shell

    grep -E 'VmPeak|VmSize|VmRSS' /proc/<pid>/status

A JVM crash log tells the same story without a live process. ``Virtual Size`` far into the terabytes with a small ``Resident Set Size`` means the address space is exhausted while physical memory is not:

.. code-block:: text

    Process Memory:
    Virtual Size: 96636764160K (peak: 96636764160K)
    Resident Set Size: 655360K (peak: 655360K)

That is 90 TiB reserved against 640 MB resident: about 67 live runtimes, and only a handful of reservations away from the ceiling.

Staying Under the Limit
=======================

Close What You Open
-------------------

A ``V8Runtime`` holds its reservation until it is closed. Use try-with-resources so that an exception cannot skip the close:

.. code-block:: java

    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        // The reservation is released at the end of the block.
    }

Use the Engine Pool
-------------------

An engine pool bounds the number of live runtimes by construction, which is the most reliable way to stay under the ceiling. Size it deliberately rather than deriving it from the host, and remember that the pool itself holds its runtimes until it is closed:

.. code-block:: java

    try (IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>()) {
        javetEnginePool.getConfig().setPoolMaxSize(8);
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            // ...
        }
        // Engines return to the pool here, they are not destroyed.
    }
    // The pool is closed, and every runtime it held is released.

See :doc:`../../tutorial/basic/engine_pool` for the full configuration.

.. caution::

    Several pools, each individually well sized, still add up. Applications that build a pool per tenant, per module or per test are the ones that reach the ceiling, because no single pool looks unreasonable. What matters is the total across the process.

Watch Out for Short-Lived Owners
--------------------------------

Anything that creates a runtime or a pool in a setup step and relies on garbage collection to clean it up will leak the reservation, because nothing in the JVM heap is large enough to make the collector feel any urgency. The JVM sees a small object; the operating system sees 1.34 TiB. Test suites are a common example: a fresh runtime per test case, never closed, accumulates for the lifetime of the test JVM.

Other Things the Reservation Affects
====================================

* ``ulimit -v`` caps virtual memory, not physical memory. Any limit below about 1.4 TiB will stop a single V8 mode runtime from starting, and the failure looks like an out-of-memory error on a machine with plenty of memory free. Leave it unlimited and control memory with ``ulimit -m``, cgroups or container limits, which count resident pages.
* Container memory limits are resident-set based and are not affected by the reservation.
* Monitoring that alarms on ``VSZ`` will fire constantly. Alarm on ``RSS`` instead, and if you want an early warning for this failure mode, track the number of live runtimes directly.
