==============================================
SIGSEGV at createV8Runtime() in V8 Mode on AWS
==============================================

Sometimes creating multiple Javet engines meets SIGSEGV at ``createV8Runtime()`` in V8 mode on AWS.

.. code-block:: java

    import com.caoccao.javet.enums.JSRuntimeType;
    import com.caoccao.javet.interop.V8Runtime;
    import com.caoccao.javet.interop.engine.IJavetEngine;
    import com.caoccao.javet.interop.engine.JavetEnginePool;

    import java.util.ArrayList;
    import java.util.List;

    public class TestCrash {
        public static void main(String[] args) throws Exception {
            final int threadCount;
            if (args.length > 0) {
                final String arg = args[0];
                threadCount = Integer.parseInt(arg);
                System.out.println("Using threads from args: " + threadCount);
            } else {
                threadCount = 5;
                System.out.println("Using threads from default: " + threadCount);
            }
            final List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                final Thread thread = new Thread(() -> {
                    try (final JavetEnginePool<V8Runtime> enginePool = new JavetEnginePool<>()) {
                        System.out.println("Created engine pool");
                        enginePool.getConfig().setJSRuntimeType(JSRuntimeType.V8);
                        try (final IJavetEngine<V8Runtime> engine = enginePool.getEngine()) {
                            // This will not be reached for threadCount >= 2
                            System.out.println("Created engine");
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                    }
                });
                threadList.add(thread);
                thread.start();
            }
            for (Thread thread : threadList) {
                thread.join();
            }
            System.out.println("Quit peacefully.");
        }
    }

The root cause seems that the V8 memory protection key is not properly initialized in very few VM environments with specific CPU models. The changes were committed on May 26, 2023. That causes Javet v3.0.0+ to have this issue.

As the SIGSEGV only occurs on Linux x86_64 with small set of CPUs, the solution is expected to be only applied to Linux x86_64 build.

Execute the following code in the main thread during the application initialization to force V8 to initialize.

.. code-block:: java

    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
    }

Why does this trick work?

V8 stores memory protection key flag in a global storage called ``ThreadIsolation``. ``ThreadIsolation``'s initialization is broken in some Fedora based Linux distributions inside KVM.

A full cycle of V8Runtime ensures ``ThreadIsolation``'s initialization is performed successfully.
