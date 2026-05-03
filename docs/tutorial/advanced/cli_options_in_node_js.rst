======================
CLI Options in Node.js
======================

Node.js comes with a variety of `CLI options <https://nodejs.org/docs/latest/api/cli.html#command-line-api>`_. These options expose built-in debugging, multiple ways to execute scripts, and other helpful runtime options.

In this tutorial, we are going to learn how to specify CLI options in Javet.

In Node.js the CLI options consists of the following:

* Options - ``NodeFlags``
* V8 options - ``V8Flags``
* Arguments - ``NodeRuntimeOptions``

.. code-block:: sh

    node [options] [V8 options] [<program-entry-point> | -e "script" | -] [--] [arguments]

Options
=======

Options are often used to turn on some Node.js specific features. Let's take Temporal for instance.

Temporal is a stage-3 TC39 proposal exposed in Node.js behind ``--harmony-temporal``. Let's see how to enable it in Javet.

Step 1: Set Options
-------------------

* Execute the following Java code before the first ``NodeRuntime`` is created.

.. code-block:: java

    NodeRuntimeOptions.NODE_FLAGS.setHarmonyTemporal(true);

Step 2: Let's Go
----------------

* Run the following JavaScript code.

.. code-block:: js

    console.log(Temporal.Now.instant().toString());

* It works!

.. code-block:: js

    2026-05-03T12:34:56.789Z

.. note::

    ``NodeRuntimeOptions.NODE_FLAGS`` is a global config that applies to all the Node.js runtimes. Once the first Node.js runtime is initialized, ``NodeRuntimeOptions.NODE_FLAGS`` is sealed and no longer accepts further changes.

More Options
------------

There are more options available in ``NodeFlags``, e.g. ``--allow-fs-read``, ``--allow-fs-write``, ``--no-warnings``. And ``NodeFlags`` also support custom flags.

Arguments
=========

The arguments can be set as follows. It can be specified per Node.js runtime creation.

.. code-block:: java

    NodeRuntimeOptions nodeRuntimeOptions = new NodeRuntimeOptions();
    nodeRuntimeOptions.setConsoleArguments(new String[]{"--abc", "--def"});
    try (NodeRuntime testNodeRuntime = v8Host.createV8Runtime(nodeRuntimeOptions)) {
        List<String> consoleArguments = testNodeRuntime.getExecutor("process.argv;").executeObject();
        assertEquals(3, consoleArguments.size());
        assertEquals("--abc", consoleArguments.get(1));
        assertEquals("--def", consoleArguments.get(2));
    }
