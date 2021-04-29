=====================
Interact with Node.js
=====================

Is That Possible?
=====================

In native Node.js, once a JavaScript server application (e.g. express server) is up, there is no normal way of interacting with the Node.js runtime via console. The reasons are quite simple:

* V8 is single-threaded.
* Node.js event loop is dedicated to that JavaScript server application.

So, that almost closes the door to interacting with Node.js runtime from JVM. No worry, Javet is able to hijack the Node.js event loop to allow interaction from JVM. In other words, Java application can seamlessly interact with Node.js runtime as usual. That applies to all Javet API.

How?
====

Step 1: JavaScript Server
-------------------------

* Create a JavaScript server application. Here is a sample.

.. code-block:: js

    /*
    npm install express body-parser cookie-parser multer --save
    */

    const express = require("express");
    const app = express();

    // This is the callback function that takes call from JVM.
    var test = (count) => {
    console.log(`Call #${count}`);
    }

    // This is the express handler.
    app.get('/', function (req, res) {
    res.send('Hello');
    console.log('GET /');
    })

    // Start the express server.
    const server = app.listen(8991, "0.0.0.0", () => {
    const host = server.address().address;
    const port = server.address().port;
    console.log(`Listening at http://${host}:${port}`);
    });

Step 2: Worker Thread for the JavaScript Server
-----------------------------------------------

* Create a worker thread hosting that JavaScript server application.
* Call ``NodeRuntime.await()`` in that worker thread once the Node.js runtime is up.
* Make sure that Node.js runtime is shared with the main thread.

Step 3: Main Thread for the Interaction
---------------------------------------

* Start that worker thread.
* Wait for the Node.js runtime completely initialized.
* Perform the interaction as usual.

.. code-block:: java

    public class TestExpress {
        public static void main(String[] args) throws JavetException, InterruptedException {
            // Make sure node_modules and test folders stay together.
            File codeFile = Path.of(JavetOSUtils.WORKING_DIRECTORY)
                    .resolve("test/test-express.js").toFile();
            AtomicBoolean serverUp = new AtomicBoolean(false);
            // Make sure Node.js runtime is shared with all threads.
            try (NodeRuntime nodeRuntime = V8Host.getNodeInstance().createV8Runtime()) {
                // Create a worker thread.
                Thread thread = new Thread(() -> {
                    try {
                        System.out.println("Starting the server.");
                        nodeRuntime.getExecutor(codeFile).executeVoid();
                        serverUp.set(true);
                        System.out.println("Awaiting...");
                        nodeRuntime.await();
                    } catch (JavetException e) {
                        e.printStackTrace();
                    }
                });
                // Start the worker thread.
                thread.start();
                while (!serverUp.get()) {
                    System.out.println("Waiting for server getting up.");
                    TimeUnit.MILLISECONDS.sleep(500);
                }
                System.out.println("Server is up.");
                // Make the call.
                for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                    try (V8ValueFunction v8ValueFunction = nodeRuntime.getGlobalObject().get("test")) {
                        v8ValueFunction.callVoid(null, i);
                    }
                    TimeUnit.MILLISECONDS.sleep(1000);
                }
            }
        }
    }

Voilà! The calls (``Call #``) from JVM work. And in the meanwhile, calls (``GET /``) to that JavaScript server also work. Here is the console output.

.. code-block:: shell

    Waiting for server getting up.
    Starting the server.
    Awaiting...
    Listening at http://0.0.0.0:8991
    Server is up.
    Call #0
    Call #1
    Call #2
    Call #3
    Call #4
    GET /
    Call #5
    Call #6
    Call #7
    Call #8
    Call #9

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
