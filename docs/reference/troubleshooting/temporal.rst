========
Temporal
========

Overview
========

Temporal is a modern date and time API for JavaScript that provides a more robust and comprehensive solution for working with dates, times, and time zones. Javet provides support for the Temporal API in both Node.js and V8 modes.

Platform Support
================

The following table shows the current support status for Temporal across different platforms and modes:

========  =======  =======  =========
Mode      Linux    macOS    Windows
========  =======  =======  =========
Node.js   ✓        ✓        ✓
V8        ✗        ✓        ✗
========  =======  =======  =========

Key Points
==========

* **Node.js Mode**: Temporal is fully supported on all operating systems (Linux, macOS, and Windows).
* **V8 Mode**: Temporal is currently only supported on macOS. Support for Linux and Windows is not yet available.

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
