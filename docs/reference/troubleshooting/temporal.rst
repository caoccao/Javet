========
Temporal
========

Overview
========

Temporal is a modern date and time API for JavaScript that provides a more robust and comprehensive solution for working with dates, times, and time zones. Javet provides support for the Temporal API in both Node.js and V8 modes.

Platform Support
================

The following table shows the current support status for Temporal across different platforms and modes:

========  =========  ================  ================  =======  =========
Mode      Android    Linux x86_64      Linux arm64       MacOS    Windows
========  =========  ================  ================  =======  =========
Node.js   ✓          ✓                 ✓                 ✓        ✓
V8        ✗          ✓                 ✗                 ✓        ✓
========  =========  ================  ================  =======  =========

Key Points
==========

* **Node.js Mode**: Temporal is fully supported on all operating systems (Linux, MacOS, and Windows).
* **V8 Mode**: Temporal is currently supported on MacOS, Windows. Support for Linux is not yet available.

How to Enable Temporal
======================

* **Node.js Mode**: Temporal is disabled by default. To enable it, you need to set ``NodeFlags.setHarmonyTemporal(true)`` before creating the V8 runtime first time.
* **V8 Mode**: Temporal is enabled by default on MacOS and Windows.

Usage
=====

Once Temporal is available in your environment, you can use it directly in your JavaScript code:

.. code-block:: javascript

    // Create a Temporal.PlainDateTime
    const temporal = Temporal.PlainDateTime.from('2024-06-15T10:30:45');
    
    // Convert to JavaScript Date
    const date = new Date(temporal.toZonedDateTime('UTC').epochMilliseconds);
    
    console.log(date);

For more information about the Temporal API, please refer to the `TC39 Temporal proposal <https://tc39.es/proposal-temporal/docs/>`_.
