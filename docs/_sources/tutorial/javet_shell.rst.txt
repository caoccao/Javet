===========
Javet Shell
===========

It is very easy to create a node flavored interactive shell application with a few lines of code.

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try resource.
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        System.out.println("Welcome to CDT Shell!");
        System.out.println("Input the script or '.exit' to exit.");
        // Step 2: Create a scanner to take console input line by line.
        try (Scanner scanner = new Scanner(System.in)) {
            // Step 3: Create an infinite loop.
            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();
                // Step 4: If the command is ".exit", exit the loop.
                if (".exit".equals(command)) {
                    break;
                }
                // Step 5: Execute the command and capture the result.
                try (V8Value v8Value = v8Runtime.getExecutor(command).execute()) {
                    if (v8Value != null) {
                        // Step 6: Print the result as string.
                        System.out.println(v8Value.toString());
                    }
                } catch (Throwable t) {
                    System.err.println(t.getMessage());
                }
            }
        }
    }

The user experience is very much like the one in node.

.. code-block:: js

    > let a = 3
    undefined
    > let b = 4
    undefined
    > a + b
    7

Please refer to the :extsource2:`source code <../../src/test/java/com/caoccao/javet/tutorial/cdt/CDTShell.java>` for more detail.
