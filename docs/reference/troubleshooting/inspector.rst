============
V8 Inspector
============

Javet exposes the `Chrome DevTools Protocol <https://chromedevtools.github.io/devtools-protocol/>`_ (CDP) through the V8 Inspector API, allowing Java applications to debug and profile JavaScript running inside a V8 runtime. This page documents every supported feature with demo code, and lists features that are not yet implemented.

.. note::

    The V8 Inspector is only available in **V8 mode**. Node.js has its own inspector protocol and is not covered here.

Getting Started
===============

The entry point is ``V8Inspector``, obtained from a ``V8Runtime``. You register one or more ``IV8InspectorListener`` implementations to receive protocol responses and notifications, then send CDP requests as JSON strings. A single ``V8Runtime`` can host multiple independent inspector sessions — see `Multiple Inspector Sessions`_ for details.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
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
    * - ``consoleAPIMessage(int contextGroupId, int level, String message, String url, int lineNumber, int columnNumber)``
      - Called when JavaScript calls a console API method (``console.log()``, ``console.warn()``, ``console.error()``, etc.). The ``level`` indicates severity: 1 = log, 2 = debug, 4 = info, 8 = error, 16 = warning. Has a default empty implementation so existing listeners do not need to override it.
    * - ``receiveResponse(String message)``
      - Called when V8 sends a response to a request you sent (matched by ``id``).
    * - ``receiveNotification(String message)``
      - Called when V8 sends an unsolicited event (e.g., ``Debugger.paused``, ``Debugger.scriptParsed``).
    * - ``flushProtocolNotifications()``
      - Called by V8 to signal that all pending notifications have been flushed.
    * - ``installAdditionalCommandLineAPI(IV8ValueObject commandLineAPI)``
      - Called when V8 sets up the command-line API for a context. Properties set on ``commandLineAPI`` become available as "magic" variables during ``Runtime.evaluate`` with ``includeCommandLineAPI: true``. This allows custom DevTools console helpers without polluting the global scope. Has a default empty implementation so existing listeners do not need to override it.
    * - ``runIfWaitingForDebugger(int contextGroupId)``
      - Called when V8 is ready to run but may be waiting for a debugger to attach.
    * - ``sendRequest(String message)``
      - Called before the request is dispatched to V8, allowing listeners to observe outgoing messages.

Runtime Evaluation
==================

Use ``Runtime.evaluate`` to evaluate JavaScript expressions and receive the result through the CDP protocol. This is the same mechanism Chrome DevTools uses for the Console panel.

Javet pumps the V8 microtask queue after dispatching each protocol message, so promise-based responses (e.g., ``replMode: true`` or ``awaitPromise: true``) are delivered immediately.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
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

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
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

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
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

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
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

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
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

    try (V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
        IV8InspectorListener listener1 = new MyListener();
        IV8InspectorListener listener2 = new MyListener();

        // Add multiple listeners.
        v8Inspector.addListeners(listener1, listener2);

        // Remove a specific listener.
        v8Inspector.removeListeners(listener1);
    }

Custom Command Line API
=======================

You can install custom helper functions in the DevTools console scope by overriding ``installAdditionalCommandLineAPI()``. Properties set on the ``commandLineAPI`` object are available during ``Runtime.evaluate`` with ``includeCommandLineAPI: true``, but do **not** pollute the JavaScript global scope.

V8 calls this callback each time an evaluation with ``includeCommandLineAPI: true`` is about to execute, so the helpers are installed fresh each time.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
        v8Inspector.addListeners(new IV8InspectorListener() {
            @Override public void flushProtocolNotifications() { }
            @Override public void receiveNotification(String message) { }
            @Override public void receiveResponse(String message) { }
            @Override public void runIfWaitingForDebugger(int contextGroupId) { }
            @Override public void sendRequest(String message) { }

            @Override
            public void installAdditionalCommandLineAPI(IV8ValueObject commandLineAPI) {
                try {
                    // Define a custom $myHelper variable visible in DevTools console.
                    commandLineAPI.set("$myHelper", 42);
                    // Define a custom function.
                    commandLineAPI.set("$greet", v8Runtime.createV8ValueFunction(
                        "return 'Hello, ' + name;", "name"));
                } catch (JavetException e) {
                    // handle error
                }
            }
        });

        // When evaluating with includeCommandLineAPI:true, $myHelper and $greet are available.
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
        v8Inspector.sendRequest(
            "{\"id\":2,\"method\":\"Runtime.evaluate\","
            + "\"params\":{\"expression\":\"$myHelper\","
            + "\"includeCommandLineAPI\":true,"
            + "\"replMode\":true}}");
        // Response: {"result":{"type":"number","value":42}}
    }

.. tip::

    Do not close the ``commandLineAPI`` object — its lifecycle is managed by the inspector. Properties you install are scoped to DevTools evaluation only; they are not visible to regular JavaScript execution.

Custom Logger
=============

The inspector uses the ``V8Runtime`` logger by default. You can replace it with a custom ``IJavetLogger``.

.. code-block:: java

    try (V8Inspector v8Inspector = v8Runtime.createV8Inspector("inspector")) {
        v8Inspector.setLogger(myCustomLogger);
    }

Named Inspector
===============

You can provide a custom name for the inspector, which appears in the DevTools context selector. Pass any name you like to ``createV8Inspector()``.

.. code-block:: java

    // Pass any name you want.
    try (V8Inspector v8Inspector = v8Runtime.createV8Inspector("My Application")) {
        // ...
    }

Multiple Inspector Sessions
===========================

A single ``V8Runtime`` can host multiple inspector sessions simultaneously, allowing several DevTools clients (or independent Java listeners) to connect at the same time. Each session has its own ``V8InspectorSession`` and ``Channel`` on the C++ side, so responses and notifications are routed independently.

Every call to ``V8Runtime.createV8Inspector()`` creates a new independent session. Each returned ``V8Inspector`` has a unique ``sessionId``. Sessions implement ``IJavetClosable`` and can be closed individually without affecting other sessions.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector session1 = v8Runtime.createV8Inspector("session-1");
         V8Inspector session2 = v8Runtime.createV8Inspector("session-2")) {
        v8Runtime.getExecutor("const a = 10; const b = 20;").executeVoid();

        session1.addListeners(listener1);
        session2.addListeners(listener2);

        // Enable Runtime on both.
        session1.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
        session2.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");

        // Evaluate on session 1 — only listener1 receives the response.
        session1.sendRequest(
            "{\"id\":2,\"method\":\"Runtime.evaluate\","
            + "\"params\":{\"expression\":\"a\",\"replMode\":true}}");

        // Evaluate on session 2 — only listener2 receives the response.
        session2.sendRequest(
            "{\"id\":2,\"method\":\"Runtime.evaluate\","
            + "\"params\":{\"expression\":\"b\",\"replMode\":true}}");
    }

.. tip::

    Every call to ``createV8Inspector()`` creates a new independent session. This makes it easy to connect multiple DevTools frontends, or run a logging session alongside a debugging session.

Breakpoints with Multiple Sessions
-----------------------------------

When multiple sessions have ``Debugger.enable`` active, all sessions receive ``Debugger.paused`` notifications when a breakpoint is hit. Any session can send ``Debugger.resume`` to resume execution.

.. code-block:: java

    try (V8Inspector debugSession = v8Runtime.createV8Inspector("debugger");
         V8Inspector logSession = v8Runtime.createV8Inspector("logger")) {
        debugSession.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
        logSession.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");

        // Set breakpoint on the debug session.
        debugSession.sendRequest(
            "{\"id\":2,\"method\":\"Debugger.setBreakpointByUrl\","
            + "\"params\":{\"lineNumber\":1,\"url\":\"app.js\"}}");

        // When the breakpoint is hit, BOTH sessions receive Debugger.paused.
        // Resume from either session.
        logSession.sendRequest("{\"id\":2,\"method\":\"Debugger.resume\"}");
    }

Break on Start (Wait for Debugger)
==================================

You can pause execution before the first JavaScript statement runs by creating the inspector with ``waitForDebugger = true``. This connects the inspector session with ``kWaitingForDebugger``, allowing a DevTools client to set breakpoints and configure the debugger before any user code is executed.

The calling thread blocks inside ``waitForDebugger()`` in a message-pumping loop, dispatching incoming CDP messages while waiting. Once the debugger sends ``Runtime.runIfWaitingForDebugger``, V8 invokes the ``runIfWaitingForDebugger()`` callback, the loop exits, and execution proceeds.

.. code-block:: java

    CountDownLatch waitingLatch = new CountDownLatch(1);
    CountDownLatch completedLatch = new CountDownLatch(1);

    IV8InspectorListener listener = new IV8InspectorListener() {
        @Override public void flushProtocolNotifications() { }
        @Override public void receiveNotification(String message) { }
        @Override public void receiveResponse(String message) { }
        @Override public void sendRequest(String message) { }

        @Override
        public void runIfWaitingForDebugger(int contextGroupId) {
            waitingLatch.countDown();
        }
    };

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("my-app", true)) {
        v8Inspector.addListeners(listener);

        // Execute JavaScript on a separate thread.
        Thread executionThread = new Thread(() -> {
            try {
                // Blocks until Runtime.runIfWaitingForDebugger is received.
                v8Inspector.waitForDebugger();
                v8Runtime.getExecutor("const x = 42;").executeVoid();
                completedLatch.countDown();
            } catch (JavetException e) {
                e.printStackTrace(System.err);
            }
        });
        executionThread.start();

        // At this point the execution thread is blocked.
        // Configure the debugger (set breakpoints, enable domains, etc.).
        v8Inspector.sendRequest(
            "{\"id\":1,\"method\":\"Debugger.enable\"}");

        // Release the wait — execution begins.
        v8Inspector.sendRequest(
            "{\"id\":2,\"method\":\"Runtime.runIfWaitingForDebugger\"}");

        assertTrue(waitingLatch.await(5, TimeUnit.SECONDS));
        assertTrue(completedLatch.await(5, TimeUnit.SECONDS));
        executionThread.join(5000);
    }

.. tip::

    This is the standard "break on start" pattern used by Chrome DevTools. The debugger connects, configures breakpoints and domains, then sends ``Runtime.runIfWaitingForDebugger`` to signal that it is ready. You can combine this with breakpoints to pause at the very first statement.

Threading Model
===============

The V8 Inspector in Javet has a specific threading model that you need to understand:

1. **Non-paused state**: ``sendRequest()`` acquires the V8 isolate lock, dispatches the protocol message, and pumps microtasks. This all happens on the calling thread.

2. **Paused state** (breakpoint hit): The execution thread is blocked inside ``runMessageLoopOnPause()``. Calls to ``sendRequest()`` from another thread enqueue the message into a thread-safe queue. The pause loop drains this queue and dispatches messages without acquiring an additional V8 lock (the execution thread already holds it).

3. **Rule of thumb**: Always run JavaScript on a separate thread when using breakpoints. Send debugger commands (resume, step, evaluate) from your main/control thread.

.. warning::

    Calling ``sendRequest()`` from the same thread that is executing JavaScript while paused will deadlock. Always use a separate thread for sending debugger commands during a pause.

Direct Session API
==================

In addition to the CDP JSON protocol (``sendRequest`` / ``receiveResponse``), ``V8Inspector`` exposes several ``V8InspectorSession`` methods directly. These avoid the overhead of JSON serialization and give the embedder programmatic control over debugging without constructing protocol messages.

.. list-table::
    :header-rows: 1
    :widths: 35 65

    * - Method
      - Description
    * - ``schedulePauseOnNextStatement(String breakReason, String breakDetails)``
      - Schedules a pause on the next JavaScript statement. The pause fires asynchronously — V8 will trigger a ``Debugger.paused`` notification the next time it is about to execute a statement. Use this for programmatic "break on next" functionality.
    * - ``cancelPauseOnNextStatement()``
      - Cancels a previously scheduled pause before it fires.
    * - ``breakProgram(String breakReason, String breakDetails)``
      - Forces an immediate break (pause). Unlike ``schedulePauseOnNextStatement()``, this breaks immediately and must be called while V8 is executing JavaScript (e.g., from a Java callback invoked by JS).
    * - ``setSkipAllPauses(boolean skip)``
      - Temporarily disables all breakpoints without removing them. Call ``setSkipAllPauses(false)`` to re-enable pausing.
    * - ``evaluate(String expression, boolean includeCommandLineAPI)``
      - Evaluates a JavaScript expression directly through the inspector session, returning a Javet ``V8Value``. This bypasses CDP JSON serialization and is faster than ``Runtime.evaluate`` via ``sendRequest()``. The returned value must be closed when no longer needed (if it is a reference type).

Programmatic Pause
------------------

Use ``schedulePauseOnNextStatement()`` to pause V8 on the next JavaScript statement without setting a breakpoint at a specific source location. This is useful for "break on next" scenarios.

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("pause-test")) {
        v8Inspector.addListeners(new IV8InspectorListener() { /* ... */ });
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");

        // Schedule a pause — the next JS statement will trigger Debugger.paused.
        v8Inspector.schedulePauseOnNextStatement("ambiguous", "{}");

        // Execute on another thread (execution will pause immediately).
        Thread executionThread = new Thread(() -> {
            v8Runtime.getExecutor("const x = 1; const y = 2;").executeVoid();
        });
        executionThread.start();
        // ... wait for Debugger.paused, then resume via sendRequest.
    }

To cancel a scheduled pause before it fires:

.. code-block:: java

    v8Inspector.schedulePauseOnNextStatement("ambiguous", "{}");
    // Changed our mind — cancel the pause.
    v8Inspector.cancelPauseOnNextStatement();

Immediate Break (breakProgram)
------------------------------

Use ``breakProgram()`` to force an immediate pause from inside a Java callback that is being invoked by JavaScript. Unlike ``schedulePauseOnNextStatement()``, this breaks synchronously — V8 enters the pause loop right away and sends a ``Debugger.paused`` notification. The call **must** happen on the V8 execution thread (i.e., with JS frames on the stack).

A typical pattern is to register a Javet direct-call callback as a global JS function, then call it from JavaScript:

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("break-test")) {
        v8Inspector.addListeners(listener);
        v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");

        // Register a Java callback that triggers an immediate break.
        JavetCallbackContext callbackContext = new JavetCallbackContext(
                "triggerBreak", JavetCallbackType.DirectCallNoThisAndNoResult,
                (IJavetDirectCallable.NoThisAndNoResult<Exception>) (v8Values) ->
                        v8Inspector.breakProgram("embedder-break", "{}"));
        try (V8ValueFunction fn = v8Runtime.createV8ValueFunction(callbackContext)) {
            v8Runtime.getGlobalObject().set("triggerBreak", fn);
        }

        // Execute on a separate thread — triggerBreak() pauses immediately.
        Thread executionThread = new Thread(() -> {
            try {
                v8Runtime.getExecutor("triggerBreak();\nconst y = 2;")
                        .setResourceName("app.js")
                        .executeVoid();
            } catch (JavetException e) {
                e.printStackTrace(System.err);
            }
        });
        executionThread.start();
        // ... wait for Debugger.paused, then send Debugger.resume from main thread.
    }

.. tip::

    ``breakProgram()`` is the V8 equivalent of a programmatic ``debugger;`` statement. Use it when you need to break at a point controlled by Java logic rather than by JavaScript source location.

Skip All Pauses
---------------

Temporarily disable all breakpoints without removing them:

.. code-block:: java

    // Disable all breakpoints temporarily.
    v8Inspector.setSkipAllPauses(true);
    v8Runtime.getExecutor("/* breakpoints in here will not fire */").executeVoid();

    // Re-enable breakpoints.
    v8Inspector.setSkipAllPauses(false);

Direct Evaluation
-----------------

Use ``evaluate()`` to evaluate expressions directly, returning a Javet ``V8Value`` instead of CDP JSON:

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime();
         V8Inspector v8Inspector = v8Runtime.createV8Inspector("eval-test")) {
        v8Runtime.getExecutor("const answer = 42;").executeVoid();

        // Evaluate directly — no JSON overhead.
        try (V8ValueInteger result = v8Inspector.evaluate("answer", false)) {
            System.out.println(result.getValue()); // 42
        }

        // With command-line API scope (same as includeCommandLineAPI in CDP).
        try (V8ValueInteger result = v8Inspector.evaluate("1 + 2", true)) {
            System.out.println(result.getValue()); // 3
        }
    }

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

Async Stack Traces
------------------

**Priority**: Medium

**Risk**: Debugging async code is difficult.

None of the async task tracking APIs are called: ``asyncTaskScheduled()``, ``asyncTaskStarted()``, ``asyncTaskFinished()``, ``asyncTaskCanceled()``, ``storeCurrentStackTrace()``, ``externalAsyncTaskStarted()``, ``externalAsyncTaskFinished()``. This means DevTools shows no async stack traces. When debugging promise chains or ``setTimeout`` callbacks, the call stack stops at the async boundary instead of showing the originating call site.
