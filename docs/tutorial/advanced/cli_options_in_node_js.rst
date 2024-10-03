======================
CLI Options in Node.js
======================

Node.js comes with a variety of `CLI options <https://nodejs.org/docs/latest/api/cli.html#command-line-api>`_. These options expose built-in debugging, multiple ways to execute scripts, and other helpful runtime options.

In this tutorial, we are going to learn how to specify CLI options in Javet.

sqlite
======

Starting from Node.js v22, sqlite has been a built-in module. However, it is not enabled by default. Let's see how to enable it in Javet.

Step 1: Set Options
-------------------

* Execute the following Java code before the first ``NodeRuntime`` is created.

.. code-block:: java

    NodeRuntimeOptions.NODE_FLAGS.setExperimentalSqlite(true);

Step 2: Let's Go
----------------

* Run the following JavaScript code.

.. code-block:: js

    const sqlite = require("node:sqlite");

    const db = new sqlite.DatabaseSync(":memory:");
    db.exec(`
    CREATE TABLE data(
        key INTEGER PRIMARY KEY,
        value TEXT
    ) STRICT
    `);
    const insert = db.prepare("INSERT INTO data (key, value) VALUES (?, ?)");
    insert.run(1, "a");
    insert.run(2, "b");
    const query = db.prepare("SELECT * FROM data ORDER BY key");
    console.log(query.all());
    db.close();

* It works!

.. code-block:: js

    [
        [Object: null prototype] { key: 1, value: 'a' },
        [Object: null prototype] { key: 2, value: 'b' }
    ]
    (node:18204) ExperimentalWarning: SQLite is an experimental feature and might change at any time
    (Use `node --trace-warnings ...` to show where the warning was created)

More Options
============

There are more options available in ``NodeFlags``, e.g. ``--allow-fs-read``, ``--allow-fs-write``, ``--no-warnings``. And ``NodeFlags`` also support custom flags.
