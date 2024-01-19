==============================================
Why is the Inspector Disabled in Node.js Mode?
==============================================

Javet does not support Node.js ``--inspect`` because **Node.js is designed to be singleton.**

It is fine to create and close the NodeRuntime one by one (I have tested). But if applications create multiple NodeRuntime instances at the same time, Javet will crash immediately. Why?

Node.js has only one IO thread serving the inspector agent. In side the function ``Agent::Start(...)``, ``CHECK_EQ(start_io_thread_async_initialized.exchange(true), false);`` makes sure that ``static std::atomic_bool start_io_thread_async_initialized;`` is flipped from ``false`` to ``true``, then crashes if ``Agent::Start(...)`` is called again. Creating a NodeRuntime instance triggers that call and there is no workaround. The crash is inevitable.

The hacky option is to change the Node.js source code. But that scope would be too large because that implies changing the fundamental design of the inspector. I don't think the Node.js community would accept that.

Here are 3 options.

1. Debug the JavaScript applications in Node.js with the inspector.
2. Use the Javet V8 inspector instead. Please visit :doc:`../../development/debug_with_chrome_developer_tools` for more details.
3. Contact the maintainer for a private build with the inspector enabled.
