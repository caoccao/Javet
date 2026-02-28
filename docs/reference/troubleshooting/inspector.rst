============
V8 Inspector
============

Javet exposes the `Chrome DevTools Protocol <https://chromedevtools.github.io/devtools-protocol/>`_ (CDP) through the V8 Inspector API, allowing Java applications to debug and profile JavaScript running inside a V8 runtime. This page documents every supported feature with demo code, and lists features that are not yet implemented.

.. note::

    The V8 Inspector is only available in **V8 mode**. Node.js has its own inspector protocol and is not covered here.

Getting Started
===============

The entry point is ``V8Inspector``, obtained from a ``V8Runtime``. You register one or more ``IV8InspectorListener`` implementations to receive protocol responses and notifications, then send CDP requests as JSON strings.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        V8Inspector v8Inspector = v8Runtime.getV8Inspector();
        v8Inspector.addListeners(new IV8InspectorListener() {
            @Override
            public void flushProtocolNotifications() { }

            @Override
            public void receiveNotification(String message) {
                System.out.println("Notification: " + message);
            }

            @Override
            public void receiveResponse(String message) {
                System.out.println("Response: " + message);
            }

            @Override
            public void runIfWaitingForDebugger(int contextGroupId) { }

            @Override
            public void sendRequest(String message) { }
        });
        // Now send CDP requests via v8Inspector.sendRequest(jsonString)
    }

Listener Interface
==================

``IV8InspectorListener`` is the callback interface that receives all communication from the inspector.

.. list-table::
    :header-rows: 1
    :widths: 30 70

    * - Method
      - Description
    * - ``receiveResponse(String message)``
      - Called when V8 sends a response to a request you sent (matched by ``id``).
    * - ``receiveNotification(String message)``
      - Called when V8 sends an unsolicited event (e.g., ``Debugger.paused``, ``Debugger.scriptParsed``).
    * - ``flushProtocolNotifications()``
      - Called by V8 to signal that all pending notifications have been flushed.
    * - ``runIfWaitingForDebugger(int contextGroupId)``
      - Called when V8 is ready to run but may be waiting for a debugger to attach.
    * - ``sendRequest(String message)``
      - Called before the request is dispatched to V8, allowing listeners to observe outgoing messages.

Runtime Evaluation
==================

Use ``Runtime.evaluate`` to evaluate JavaScript expressions and receive the result through the CDP protocol. This is the same mechanism Chrome DevTools uses for the Console panel.

Javet pumps the V8 microtask queue after dispatching each protocol message, so promise-based responses (e.g., ``replMode: true`` or ``awaitPromise: true``) are delivered immediately.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        V8Inspector v8Inspector = v8Runtime.getV8Inspector();
        List<String> responses = new ArrayList<>();
        v8Inspector.addListeners(new IV8InspectorListener() {
            @Override public void flushProtocolNotifications() { }
            @Override public void receiveNotification(String message) { }
            @Override public void receiveResponse(String message) { responses.add(message); }
            @Override public void runIfWaitingForDebugger(int contextGroupId) { }
            @Override public void sendRequest(String message) { }
        });
        // First, execute some JavaScript to define a variable.
        v8Runtime.getExecutor("const a = 3;").executeVoid();
        // Enable the Runtime domain.
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
        // Evaluate the variable using the inspector protocol.
        v8Inspector.sendRequest(
            "{\"id\":2,\"method\":\"Runtime.evaluate\","
            + "\"params\":{\"expression\":\"a\","
            + "\"includeCommandLineAPI\":true,"
            + "\"generatePreview\":true,"
            + "\"replMode\":true}}");
        // The response for id:2 contains: {"result":{"type":"number","value":3}}
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responses.get(1));
        int value = jsonNode.get("result").get("result").get("value").asInt();
        assertEquals(3, value);
    }

.. tip::

    When ``replMode`` is ``true``, V8 wraps the evaluation in a promise internally. Javet calls ``PerformMicrotaskCheckpoint()`` after dispatching the protocol message so that the promise resolves and the response is delivered without delay.

Setting and Hitting Breakpoints
===============================

You can set breakpoints via ``Debugger.setBreakpointByUrl`` and V8 will pause execution when the breakpoint is hit. Because JavaScript execution blocks on the paused thread, you must run the script on a **separate thread** and send ``Debugger.resume`` from the main thread.

When V8 is paused, Javet routes incoming protocol messages through a thread-safe queue that the pause loop drains, avoiding deadlocks on the V8 isolate lock.

.. code-block:: java

    ObjectMapper objectMapper = new ObjectMapper();
    CountDownLatch pausedLatch = new CountDownLatch(1);
    CountDownLatch resumedLatch = new CountDownLatch(1);

    IV8InspectorListener listener = new IV8InspectorListener() {
        @Override public void flushProtocolNotifications() { }
        @Override public void receiveResponse(String message) { }
        @Override public void runIfWaitingForDebugger(int contextGroupId) { }
        @Override public void sendRequest(String message) { }

        @Override
        public void receiveNotification(String message) {
            try {
                JsonNode node = objectMapper.readTree(message);
                String method = node.has("method") ? node.get("method").asText() : "";
                if ("Debugger.paused".equals(method)) {
                    pausedLatch.countDown();
                } else if ("Debugger.resumed".equals(method)) {
                    resumedLatch.countDown();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    };

    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        V8Inspector v8Inspector = v8Runtime.getV8Inspector();
        v8Inspector.addListeners(listener);

        // 1. Enable the Debugger domain.
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");

        // 2. Set a breakpoint on line 1 (0-based) of "test.js".
        v8Inspector.sendRequest(
            "{\"id\":2,\"method\":\"Debugger.setBreakpointByUrl\","
            + "\"params\":{\"lineNumber\":1,\"url\":\"test.js\","
            + "\"columnNumber\":0,\"condition\":\"\"}}");

        // 3. Execute JavaScript on a separate thread.
        Thread executionThread = new Thread(() -> {
            try {
                v8Runtime.getExecutor(
                        "const x = 1;\n"       // line 0
                        + "const y = x + 2;\n"  // line 1 — breakpoint here
                        + "const z = y + 3;")   // line 2
                    .setResourceName("test.js")
                    .executeVoid();
            } catch (JavetException e) {
                e.printStackTrace(System.err);
            }
        });
        executionThread.start();

        // 4. Wait for the Debugger.paused notification.
        assertTrue(pausedLatch.await(5, TimeUnit.SECONDS));

        // 5. Resume execution from the main thread.
        v8Inspector.sendRequest("{\"id\":3,\"method\":\"Debugger.resume\"}");

        // 6. Wait for the execution thread to finish.
        executionThread.join(5000);
        assertTrue(resumedLatch.await(5, TimeUnit.SECONDS));
    }

.. tip::

    Always execute JavaScript on a **separate thread** when breakpoints are set, because V8 blocks inside ``runMessageLoopOnPause()`` until a ``Debugger.resume`` (or step command) is received.

Stepping Through Code
=====================

Once the debugger is paused at a breakpoint, you can step through code using:

- ``Debugger.stepOver`` — step to the next statement in the same function.
- ``Debugger.stepInto`` — step into the next function call.
- ``Debugger.stepOut`` — step out of the current function.
- ``Debugger.resume`` — continue execution until the next breakpoint or end.

.. code-block:: java

    // Assume the debugger is already paused (Debugger.paused received).

    // Step over to the next line.
    v8Inspector.sendRequest("{\"id\":10,\"method\":\"Debugger.stepOver\"}");

    // Or step into a function call.
    v8Inspector.sendRequest("{\"id\":11,\"method\":\"Debugger.stepInto\"}");

    // Or step out of the current function.
    v8Inspector.sendRequest("{\"id\":12,\"method\":\"Debugger.stepOut\"}");

    // Or resume execution entirely.
    v8Inspector.sendRequest("{\"id\":13,\"method\":\"Debugger.resume\"}");

Inspecting Variables at a Breakpoint
====================================

While paused, you can evaluate expressions in the current scope using ``Runtime.evaluate`` or inspect scope variables using ``Debugger.evaluateOnCallFrame``.

.. code-block:: java

    // Assume Debugger.paused was received with a callFrameId in the notification.
    // Extract the callFrameId from the Debugger.paused params.
    // For example: params.callFrames[0].callFrameId = "0"

    // Evaluate an expression on the top call frame.
    v8Inspector.sendRequest(
        "{\"id\":20,\"method\":\"Debugger.evaluateOnCallFrame\","
        + "\"params\":{\"callFrameId\":\"0\","
        + "\"expression\":\"x + y\"}}");

    // The response contains the evaluated result with type and value.

Enabling the Profiler
=====================

Use the ``Profiler`` domain to collect CPU profiling data.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        V8Inspector v8Inspector = v8Runtime.getV8Inspector();
        // ... add listener to capture responses ...

        // Enable the Profiler domain.
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"Profiler.enable\"}");

        // Start profiling.
        v8Inspector.sendRequest("{\"id\":2,\"method\":\"Profiler.start\"}");

        // Execute some JavaScript.
        v8Runtime.getExecutor(
            "function fib(n) { return n < 2 ? n : fib(n-1) + fib(n-2); }\n"
            + "fib(30);").executeVoid();

        // Stop profiling and receive the profile in the response.
        v8Inspector.sendRequest("{\"id\":3,\"method\":\"Profiler.stop\"}");

        // The response for id:3 contains the CPU profile with nodes,
        // timestamps, and samples.
    }

Heap Snapshots
==============

Use the ``HeapProfiler`` domain to take heap snapshots for memory analysis.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        V8Inspector v8Inspector = v8Runtime.getV8Inspector();
        StringBuilder heapSnapshot = new StringBuilder();
        v8Inspector.addListeners(new IV8InspectorListener() {
            @Override public void flushProtocolNotifications() { }
            @Override public void receiveResponse(String message) { }
            @Override public void runIfWaitingForDebugger(int contextGroupId) { }
            @Override public void sendRequest(String message) { }

            @Override
            public void receiveNotification(String message) {
                // HeapProfiler.addHeapSnapshotChunk delivers the snapshot in chunks.
                try {
                    ObjectMapper om = new ObjectMapper();
                    JsonNode node = om.readTree(message);
                    if ("HeapProfiler.addHeapSnapshotChunk".equals(
                            node.path("method").asText())) {
                        heapSnapshot.append(
                            node.path("params").path("chunk").asText());
                    }
                } catch (Exception e) { }
            }
        });

        // Execute some JavaScript to populate the heap.
        v8Runtime.getExecutor("var arr = []; for (var i = 0; i < 1000; i++) arr.push({x: i});")
            .executeVoid();

        // Enable HeapProfiler and take a snapshot.
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"HeapProfiler.enable\"}");
        v8Inspector.sendRequest("{\"id\":2,\"method\":\"HeapProfiler.takeHeapSnapshot\","
            + "\"params\":{\"reportProgress\":false}}");

        // heapSnapshot now contains the full V8 heap snapshot in JSON format.
    }

Code Coverage
=============

Use the ``Profiler`` domain to collect precise code coverage data, useful for identifying which parts of your JavaScript code were executed.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        V8Inspector v8Inspector = v8Runtime.getV8Inspector();
        // ... add listener to capture responses ...

        // Enable Profiler and start precise coverage.
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"Profiler.enable\"}");
        v8Inspector.sendRequest(
            "{\"id\":2,\"method\":\"Profiler.startPreciseCoverage\","
            + "\"params\":{\"callCount\":true,\"detailed\":true}}");

        // Execute JavaScript.
        v8Runtime.getExecutor(
            "function used() { return 42; }\n"
            + "function unused() { return 0; }\n"
            + "used();").executeVoid();

        // Collect coverage data.
        v8Inspector.sendRequest(
            "{\"id\":3,\"method\":\"Profiler.takePreciseCoverage\"}");

        // The response for id:3 contains per-function and per-block
        // coverage ranges with execution counts.

        // Stop coverage collection.
        v8Inspector.sendRequest(
            "{\"id\":4,\"method\":\"Profiler.stopPreciseCoverage\"}");
    }

Managing Listeners
==================

You can add and remove listeners dynamically. Multiple listeners receive the same messages.

.. code-block:: java

    V8Inspector v8Inspector = v8Runtime.getV8Inspector();

    IV8InspectorListener listener1 = new MyListener();
    IV8InspectorListener listener2 = new MyListener();

    // Add multiple listeners.
    v8Inspector.addListeners(listener1, listener2);

    // Remove a specific listener.
    v8Inspector.removeListeners(listener1);

Custom Logger
=============

The inspector uses the ``V8Runtime`` logger by default. You can replace it with a custom ``IJavetLogger``.

.. code-block:: java

    V8Inspector v8Inspector = v8Runtime.getV8Inspector();
    v8Inspector.setLogger(myCustomLogger);

Named Inspector
===============

You can provide a custom name for the inspector, which appears in the DevTools context selector.

.. code-block:: java

    // Default name is "Javet Inspector <handle>".
    V8Inspector v8Inspector = v8Runtime.getV8Inspector();

    // Custom name.
    V8Inspector v8Inspector = v8Runtime.getV8Inspector("My Application");

Threading Model
===============

The V8 Inspector in Javet has a specific threading model that you need to understand:

1. **Non-paused state**: ``sendRequest()`` acquires the V8 isolate lock, dispatches the protocol message, and pumps microtasks. This all happens on the calling thread.

2. **Paused state** (breakpoint hit): The execution thread is blocked inside ``runMessageLoopOnPause()``. Calls to ``sendRequest()`` from another thread enqueue the message into a thread-safe queue. The pause loop drains this queue and dispatches messages without acquiring an additional V8 lock (the execution thread already holds it).

3. **Rule of thumb**: Always run JavaScript on a separate thread when using breakpoints. Send debugger commands (resume, step, evaluate) from your main/control thread.

.. warning::

    Calling ``sendRequest()`` from the same thread that is executing JavaScript while paused will deadlock. Always use a separate thread for sending debugger commands during a pause.

Chrome DevTools Protocol Domains
================================

The following CDP domains are supported through V8's built-in inspector:

.. list-table::
    :header-rows: 1
    :widths: 20 80

    * - Domain
      - Description
    * - ``Debugger``
      - Breakpoints, stepping, pause/resume, call frames, scope inspection.
    * - ``Runtime``
      - Expression evaluation, object inspection, execution contexts, exceptions.
    * - ``Profiler``
      - CPU profiling, code coverage.
    * - ``HeapProfiler``
      - Heap snapshots, allocation tracking.
    * - ``Schema``
      - Protocol schema discovery.

Todo Features
=============

The following V8 Inspector features are not yet implemented in Javet. They are listed in priority order.

Console API Message Forwarding
------------------------------

**Priority**: High

**Risk**: Console output lost.

``V8InspectorClient::consoleAPIMessage()`` is never overridden. This means ``console.log()``, ``console.warn()``, ``console.error()``, etc. issued from JavaScript are not forwarded to Java listeners. DevTools relies on this callback to populate the Console panel. Without it, console output is only available through protocol-level ``Runtime.consoleAPICalled`` events (which requires ``Runtime.enable`` first).

Multiple Inspector Sessions
----------------------------

**Priority**: High

**Risk**: Cannot support multiple DevTools clients.

The implementation creates exactly one ``V8InspectorSession`` in the constructor and stores it as a ``unique_ptr``. The V8 API supports multiple concurrent sessions (multiple DevTools frontends connecting simultaneously). There is no way to create additional sessions or disconnect/reconnect without destroying the entire inspector.

Break on Start (waitingForDebugger)
-----------------------------------

**Priority**: High

**Risk**: Cannot pause before first statement.

``V8Inspector::connect()`` always passes ``kNotWaitingForDebugger``. For "break on start" debugging (common in DevTools when you want to pause before any user code runs), the session should be connected with ``kWaitingForDebugger``. Then V8 calls ``runIfWaitingForDebugger()`` and the embedder holds execution until the client sends ``Runtime.runIfWaitingForDebugger``. There is currently no Java API to control this.

Migrate to connectShared()
--------------------------

**Priority**: Medium

**Risk**: Deprecated API may be removed in a future V8 version.

The code uses the deprecated ``v8Inspector->connect()`` returning ``unique_ptr``. V8 recommends ``connectShared()`` returning ``shared_ptr``, which allows safer concurrent access to the session.

Context Origin and Auxiliary Data
---------------------------------

**Priority**: Medium

**Risk**: DevTools shows incomplete context information.

When calling ``v8Inspector->contextCreated()``, the ``origin`` and ``auxData`` fields of ``V8ContextInfo`` are never set. DevTools uses ``auxData`` to identify context type (e.g., ``{"isDefault": true}``) and ``origin`` for security-origin display. Without these, the DevTools context selector shows incomplete information.

Async Stack Traces
------------------

**Priority**: Medium

**Risk**: Debugging async code is difficult.

None of the async task tracking APIs are called: ``asyncTaskScheduled()``, ``asyncTaskStarted()``, ``asyncTaskFinished()``, ``asyncTaskCanceled()``, ``storeCurrentStackTrace()``, ``externalAsyncTaskStarted()``, ``externalAsyncTaskFinished()``. This means DevTools shows no async stack traces. When debugging promise chains or ``setTimeout`` callbacks, the call stack stops at the async boundary instead of showing the originating call site.

Session-Level API Exposure
--------------------------

**Priority**: Medium

**Risk**: Forces everything through CDP JSON.

``V8InspectorSession`` has useful direct methods that could be exposed to Java without going through protocol JSON:

- ``schedulePauseOnNextStatement()`` / ``cancelPauseOnNextStatement()`` — programmatic pause.
- ``breakProgram()`` — force-break from the embedder.
- ``setSkipAllPauses()`` — disable all breakpoints temporarily.
- ``evaluate()`` — direct evaluation returning a ``v8::Value`` (avoids CDP JSON overhead).
- ``state()`` — serialize session state for later reconnection.
- ``wrapObject()`` / ``unwrapObject()`` — direct object remoting.

Idle Notifications
------------------

**Priority**: Low

**Risk**: Profiler accuracy.

``idleStarted()`` / ``idleFinished()`` are never called on the ``V8Inspector`` instance. V8's CPU profiler uses these to distinguish idle time from active execution. Profiling results in DevTools will attribute idle time as active CPU time.

Resource Name to URL Mapping
-----------------------------

**Priority**: Low

**Risk**: Source URL display.

``V8InspectorClient::resourceNameToUrl()`` is not overridden. Without this, V8 cannot translate internal resource names to URLs. DevTools may show raw resource names instead of clickable source URLs.

Additional Command Line API
----------------------------

**Priority**: Low

**Risk**: Console API enrichment.

``V8InspectorClient::installAdditionalCommandLineAPI()`` is not overridden. This callback lets embedders add custom functions to the DevTools console scope (like custom helpers), providing a richer debugging experience.

Graceful Session Shutdown
-------------------------

**Priority**: Low

**Risk**: Potential callbacks during teardown.

When ``V8Runtime`` is closed, the inspector ``unique_ptr`` is simply destroyed. There is no explicit ``v8InspectorSession->stop()`` call, which is the recommended way to gracefully disconnect and prevent callbacks during teardown.
