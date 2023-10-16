=====================
Expose JsonNode in V8
=====================

With the ``JavetProxyConverter`` it's possible to expose ``JsonNode`` directly in V8. However, how to elegantly expose the ``JsonNode`` in V8 needs to be considered wisely.

2 Approaches
============

There are generally 2 approaches: Java flavor and JS flavor. Let's take a simple performance test to see how they work and what is the difference between them in terms of performance.

The test is simple: keep incrementing the leaf node of the following JSON structure and measure the time elapsed.

.. code-block:: json

    {
      "a": {
        "b": {
          "c": 0
        }
      }
    }

Java Flavor (Direct Exposure)
-----------------------------

The Java flavor leverages the built-in capabilities of the ``JavetProxyConverter`` to inject a ``JsonNode`` directly in V8. Obviously, the following JS code is quite verbose because it is the same way of playing with Jackson API in Java.

.. code-block:: js

    function testJavaFlavor(node, rounds) {
      for (let i = 0; i < rounds; i++) {
        if (node.has('a')) {
          const a = node.get('a');
          if (a.isObject() && a.has('b')) {
            const b = a.get('b');
            if (b.isObject() && b.has('c')) {
              const c = b.get('c');
              if (c.isInt()) {
                b.put('c', c.asInt() + 1);
              }
            }
          }
        }
      }
    }

JS Flavor (Wrapped Exposure)
----------------------------

The JS flavor relies on a wrapper class recognized by the ``JavetProxyConverter`` to inject a wrapped ``JsonNode`` in V8. In return, the following JS code is neat and pure JS flavored.

.. code-block:: js

    function testJSFlavor(node, rounds) {
      for (let i = 0; i < rounds; i++) {
        const b = node?.a?.b;
        if (b?.c !== undefined) {
          b.c = b.c + 1;
        }
      }
    }

The wrapper class is as follows:

.. code-block:: java

    /*
     * The key is to implement IJavetDirectProxyHandler
     * which is recognized by the JavetProxyConverter.
     * The JS object injected actually is a JS proxy
     * where all the proxy API calls are redirected
     * to the wrapper class. E.g. proxyGet, proxySet.
     */
    class JsonNodeWrapper implements IJavetDirectProxyHandler<Exception> {
        private final JsonNode jsonNode;
        private final V8Runtime v8Runtime;

        public JsonNodeWrapper(V8Runtime v8Runtime, JsonNode jsonNode) {
            this.v8Runtime = Objects.requireNonNull(v8Runtime);
            this.jsonNode = Objects.requireNonNull(jsonNode);
        }

        public JsonNode getJsonNode() {
            return jsonNode;
        }

        @Override
        public V8Runtime getV8Runtime() {
            return v8Runtime;
        }

        @Override
        public V8Value proxyGet(
                V8Value target,
                V8Value property,
                V8Value receiver)
                throws JavetException, Exception {
            if (property instanceof V8ValueString) {
                String name = ((V8ValueString) property).getValue();
                if (jsonNode.has(name)) {
                    JsonNode childJsonNode = jsonNode.get(name);
                    if (childJsonNode.isInt()) {
                        return v8Runtime.createV8ValueInteger(childJsonNode.asInt());
                    } else {
                        return v8Runtime.toV8Value(new JsonNodeWrapper(v8Runtime, childJsonNode));
                    }
                }
            }
            return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
        }

        @Override
        public V8ValueBoolean proxySet(
                V8Value target,
                V8Value propertyKey,
                V8Value propertyValue,
                V8Value receiver)
                throws JavetException, Exception {
            if (propertyKey instanceof V8ValueString && propertyValue instanceof V8ValueInteger) {
                String name = ((V8ValueString) propertyKey).getValue();
                int value = ((V8ValueInteger) propertyValue).getValue();
                if (jsonNode.isObject()) {
                    ((ObjectNode) jsonNode).put(name, value);
                    return v8Runtime.createV8ValueBoolean(true);
                }
            }
            return IJavetDirectProxyHandler.super.proxySet(target, propertyKey, propertyValue, receiver);
        }

        @Override
        public String toString() {
            return jsonNode.toString();
        }
    }

Test
====

After the 2 JS functions are executed in V8, the actual test will simply be calling these 2 functions with 2 types of inputs. The test method is as follows.

.. code-block:: java

    void test(
            V8Runtime v8Runtime,
            String testName,
            Object jsonNode,
            int rounds)
            throws JavetException {
        System.out.println("--- " + testName + " ---");
        final long startTime = System.currentTimeMillis();
        v8Runtime.getGlobalObject().invokeVoid(testName, jsonNode, rounds);
        final long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) + "ms");
        System.out.println(jsonNode);
        System.out.println();
    }

The test body is as follows.

.. code-block:: java

    final int rounds = 100_000;

    // Create a V8 runtime from V8 host in try-with-resource.
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        // Set converter to proxy based one to unlock the interoperability.
        v8Runtime.setConverter(new JavetProxyConverter());
        v8Runtime.getExecutor(testScriptJavaFlavor).executeVoid();
        v8Runtime.getExecutor(testScriptJSFlavor).executeVoid();

        // Test the Java Flavor.
        test(v8Runtime,
                "testJavaFlavor",
                objectMapper.readTree(jsonString),
                rounds);
        v8Runtime.lowMemoryNotification();
        System.gc();
        System.gc();

        // Test the JS Flavor.
        test(v8Runtime,
                "testJSFlavor",
                new JsonNodeWrapper(v8Runtime, objectMapper.readTree(jsonString)),
                rounds);

        // Notify V8 to perform GC. (Optional)
        v8Runtime.lowMemoryNotification();
    }

The console output is as follows.

* CPU: AMD 5950X
* Memory: 128GB
* OS: Windows 10 22H2
* JDK: Corretto-1.8.0_282

.. code-block:: text

    --- testJavaFlavor ---
    Time elapsed: 19464ms
    {"a":{"b":{"c":100000}}}

    --- testJSFlavor ---
    Time elapsed: 5214ms
    {"a":{"b":{"c":100000}}}

Conclusion
==========

The JS flavored approach is strongly recommended.

* The JS code is neat.
* The performance is much better (370+%).
* There is no intrusion of the Java JSON implementation so that it's easy to replace the underlying implementation without breaking the existing JS code, e.g. Jackson to Gson.

The only drawback is that some time has to be spent on the development of the wrapper class.

Please refer to the :extsource3:`source code <../../../src/test/java/com/caoccao/javet/tutorial/TestJsonNodeInV8.java>` for details.
