=======
Logging
=======

As Javet is a fundamental SDK, it doesn't rely on any other libraries except JDK so that Javet users don't get dependency hell. That also means Javet has to use the JDK logging API, but Javet allows injecting 3rd party logging API.

Step 1: Implement IJavetLogger
==============================

``IJavetLogger`` is the the only logging interface accepted by Javet. You may implement ``IJavetLogger`` with ``slf4j`` as following.

.. code-block:: java

    import com.caoccao.javet.interfaces.IJavetLogger;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    public class MyJavetLogger implements IJavetLogger {
        protected Logger logger;

        public MyJavetLogger(String name) {
            logger = LoggerFactory.getLogger(name);
        }

        @Override
        public void debug(String message) {
            if (logger.isDebugEnabled()) {
                logger.debug(message);
            }
        }

        @Override
        public void error(String message) {
            if (logger.isDebugEnabled()) {
                logger.error(message);
            }
        }

        @Override
        public void error(String message, Throwable throwable) {
            if (logger.isDebugEnabled()) {
                logger.error(message, throwable);
            }
        }

        @Override
        public void info(String message) {
            if (logger.isInfoEnabled()) {
                logger.info(message);
            }
        }

        @Override
        public void warn(String message) {
            if (logger.isWarnEnabled()) {
                logger.warn(message);
            }
        }
    }

Step 2: Inject the Logger
=========================

Injecting the logger is quite simple.

* Create an instance of the logger.
* Set the logger to a config.
* Set the config to a pool.

.. code-block:: java

    MyJavetLogger javetLogger = new MyJavetLogger("TestLogger");
    JavetEngineConfig javetEngineConfig = new JavetEngineConfig();
    javetEngineConfig.setJavetLogger(javetLogger);
    JavetEnginePool javetEnginePool = new JavetEnginePool(javetEngineConfig);

Now, Javet is integrated into your logging system.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
