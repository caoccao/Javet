===========================
Java and JavaScript Interop
===========================

Javet allows injecting arbitrary Java objects into V8 which enables the complete interop between Java and JavaScript. To enable this feature, application just needs to call ``v8Runtime.setConverter(new JavetProxyConverter());``. Here are 3 examples.

Inject a Static Class
=====================

.. code-block:: java

    v8Runtime.getGlobalObject().set("System", System.class);
    v8Runtime.getExecutor("function main() {\n" +
            // Java reference can be directly called in JavaScript.
            "  System.out.println('Hello from Java');\n" +
            // Java reference can be directly assigned to JavaScript variable.
            "  const println = System.out.println;\n" +
            // Java reference can be directly assigned to JavaScript variable.
            "  println('Hello from JavaScript');\n" +
            "}\n" +
            "main();").executeVoid();
    v8Runtime.getGlobalObject().delete("System");

    /*
     * Output:
     *   Hello from Java
     *   Hello from JavaScript
     */

Inject an Enum
==============

.. code-block:: java

    v8Runtime.getGlobalObject().set("Color", Color.class);
    System.out.println(v8Runtime.getExecutor("Color.pink.toString();").executeString());
    System.out.println("The enum in JavaScript is the one in Java: " +
            (Color.pink == (Color) v8Runtime.getExecutor("Color.pink;").executeObject()));
    v8Runtime.getGlobalObject().delete("Color");

    /*
     * Output:
     *   java.awt.Color[r=255,g=175,b=175]
     *   The enum in JavaScript is the one in Java: true
     */

Inject a Pattern
================

.. code-block:: java

    Pattern pattern = Pattern.compile("^\\d+$");
    v8Runtime.getExecutor("function main(pattern) {\n" +
            "  return [\n" +
            "    pattern.matcher('123').matches(),\n" +
            "    pattern.matcher('abc').matches(),\n" +
            "  ];\n" +
            "}").executeVoid();
    System.out.println(v8Runtime.getGlobalObject().invokeObject("main", pattern).toString());

    /*
     * Output:
     *   [true, false]
     */

[`Home <../../README.rst>`_] [`Javet Tutorial <index.rst>`_]
