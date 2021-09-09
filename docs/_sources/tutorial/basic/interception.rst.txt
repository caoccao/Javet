============
Interception
============

Javet provides ``@V8Property`` and ``@V8Function`` which allow Java applications to intercept JavaScript properties and functions in an automatic way as following.

* Decorate a Java class with ``@V8Property`` or ``@V8Function``.
* Bind an instance of that Java class to a V8 value object.
* Call the properties or functions of that V8 value object in JavaScript.
* The calls are intercepted by that instance of the Java class.

Sample
======

``@V8Property`` and ``@V8Function``
-----------------------------------

``@V8Property`` is for registering getters and setters. Javet is good at guessing the property name, e.g. getName => name, setValue => value.

``@V8Function`` is for registering functions. By default, the Java function name is identical to the JavaScript function name, e.g. increaseAndGet => increaseAndGet, add => add.

If the default name is not suitable, please tell Javet which one to bind via ``@V8Property(name = "...")`` and ``@V8Function(name = "...")``.

.. code-block:: java

    public class TestInterception {
        private String name;
        private int value;

        @V8Property
        public String getName() {
            return name;
        }
    
        @V8Property
        public void setName(String name) {
            this.name = name;
        }
    
        @V8Property
        public int getValue() {
            return value;
        }
    
        @V8Property
        public void setValue(int value) {
            this.value = value;
        }
    
        @V8Function
        public int increaseAndGet() {
            return ++value;
        }
    
        @V8Function
        public int add(int delta) {
            value += delta;
            return value;
        }
    }

Test
----

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try-with-resource.
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        // Step 2: Register console.
        JavetStandardConsoleInterceptor javetStandardConsoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
        javetStandardConsoleInterceptor.register(v8Runtime.getGlobalObject());
        // Step 3: Create an interceptor.
        TestInterception testInterceptor = new TestInterception();
        // Step 4: Bind the interceptor to a variable.
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(testInterceptor);
        }

        // Test property name
        v8Runtime.getExecutor("console.log(`a.name is initially ${a.name}.`);").executeVoid(); // null
        // a.name setter => setName(String name)
        v8Runtime.getExecutor("a.name = 'Javet';").executeVoid();
        // name is changed
        System.out.println("Interceptor name is " + testInterceptor.getName() + "."); // Javet
        // a.name getter => getName()
        v8Runtime.getExecutor("console.log(`a.name is now ${a.name}.`);").executeVoid(); // Javet

        // Test property value
        v8Runtime.getExecutor("console.log(`a.value is initially ${a.value}.`);").executeVoid(); // 0
        // a.value setter => setValue(String value)
        v8Runtime.getExecutor("a.value = 123;").executeVoid();
        // value is changed
        System.out.println("Interceptor value is " + testInterceptor.getValue() + "."); // 123
        // a.value getter => getValue()
        v8Runtime.getExecutor("console.log(`a.value is now ${a.value}.`);").executeVoid(); // 123

        // Test functions
        v8Runtime.getExecutor("console.log(`a.increaseAndGet() is ${a.increaseAndGet()}.`);").executeVoid(); // 124
        v8Runtime.getExecutor("console.log(`a.add(76) is ${a.add(76)}.`);").executeVoid(); // 200

        // Step 5: Delete the interceptor.
        v8Runtime.getGlobalObject().delete("a");
        // Step 6: Unregister console.
        javetStandardConsoleInterceptor.unregister(v8Runtime.getGlobalObject());
        // Step 7: Notify V8 to perform GC. (Optional)
        v8Runtime.lowMemoryNotification();
    }

Please refer to the :extsource3:`source code <../../../src/test/java/com/caoccao/javet/tutorial/TestInterception.java>` for more detail.
