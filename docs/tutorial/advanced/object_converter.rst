================
Object Converter
================

All Javet API allows inputting and outputting Java objects instead of ``V8Value`` so that the coding experience is close to the other JavaScript binding API. Behind the scenes, there is an object converter doing the bi-directionally object conversion. Here are a few examples.

Array
=====

.. code-block:: java

    // Create a string array in JVM.
    String[] stringArray = new String[]{"a", "b", "c"};
    // Bind that string array to a JavaScript variable x.
    v8Runtime.getGlobalObject().set("x", stringArray);
    // Print the JSON representation of the JavaScript variable x.
    System.out.println(v8Runtime.getExecutor("JSON.stringify(x);").executeString());
    // Output: ["a","b","c"]

    // Create a string array in V8.
    v8Runtime.getExecutor("const y = ['a', 'b', 'c'];").executeVoid();
    // Get that string array.
    List<String> stringList = v8Runtime.getExecutor("y;").executeObject();
    // Assert the returned string list.
    System.out.println(Arrays.equals(stringArray, stringList.toArray(new String[0])));
    // Output: true

List
====

.. code-block:: java

    // Create a string list.
    List<String> stringList = new ArrayList<String>();
    stringList.add("a");
    stringList.add("b");
    stringList.add("c");
    // Bind that string list to a JavaScript variable x.
    v8Runtime.getGlobalObject().set("x", stringList);
    // Print the JSON representation of the JavaScript variable x.
    System.out.println(v8Runtime.getExecutor("JSON.stringify(x);").executeString());
    // Output: ["a","b","c"]

Map
===

.. code-block:: java

    // Create a map in JVM.
    Map<String, Object> mapX = new HashMap<String, Object>() {{
        put("a", 1);
        put("b", true);
        put("c", "s");
    }};
    // Bind that map to a JavaScript variable x.
    v8Runtime.getGlobalObject().set("x", mapX);
    // Print the JSON representation of the JavaScript variable x.
    System.out.println(v8Runtime.getExecutor("JSON.stringify(x);").executeString());
    // Output: {"a":1,"b":true,"c":"s"}

    // Create an object in V8.
    v8Runtime.getExecutor("const y = {'a': 1, 'b': true, 'c': 's'};").executeVoid();
    // Get that string array.
    Map<String, Object> mapY = v8Runtime.getExecutor("y;").executeObject();
    // Assert the returned map.
    System.out.println(mapX.equals(mapY));
    // Output: true

Please refer to :extsource3:`source code <../../../src/test/java/com/caoccao/javet/tutorial/TestObjectConverter.java>` for detail.
