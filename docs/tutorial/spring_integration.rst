==================
Spring Integration
==================

As Javet is a fundamental SDK, it doesn't rely on Spring Framework so that Javet users don't get dependency hell. But, Javet can be integrated with Spring easily.

Configuration
=============

* Create a Spring configuration.
* Declare ``IJavetEnginePool`` as ``@Bean``.

.. code-block:: java

    @Configuration
    @PropertySource("classpath:javet-engine.properties")
    @ConfigurationProperties(prefix = "javet.engine")
    public class MyJavetEngineConfig {

        @Bean(name = "JavetEnginePoolNode")
        public IJavetEnginePool getJavetEnginePoolNode() {
            JavetEngineConfig javetEngineConfigNode = new JavetEngineConfig();
            javetEngineConfigNode.setAllowEval(...);
            javetEngineConfigNode.setAutoSendGCNotification(...);
            javetEngineConfigNode.setDefaultEngineGuardTimeoutMillis(...);
            javetEngineConfigNode.setEngineGuardCheckIntervalMillis(...);
            javetEngineConfigNode.setPoolDaemonCheckIntervalMillis(...);
            javetEngineConfigNode.setPoolIdleTimeoutSeconds(...);
            javetEngineConfigNode.setPoolMinSize(...);
            javetEngineConfigNode.setPoolMaxSize(...);
            javetEngineConfigNode.setPoolMaxSize(...);
            javetEngineConfigNode.setResetEngineTimeoutSeconds(...);
            javetEngineConfigNode.setJavetLogger(new MyJavetLogger(MyJavetLogger.class.getName()));
            javetEngineConfigNode.setJSRuntimeType(JSRuntimeType.Node);
            return new JavetEnginePool<>(javetEngineConfigNode);
        }

        @Bean(name = "JavetEnginePoolV8")
        public IJavetEnginePool getJavetEnginePoolV8() {
            JavetEngineConfig javetEngineConfigV8 = new JavetEngineConfig();
            javetEngineConfigV8.setAllowEval(...);
            javetEngineConfigV8.setAutoSendGCNotification(...);
            javetEngineConfigV8.setDefaultEngineGuardTimeoutMillis(...);
            javetEngineConfigV8.setEngineGuardCheckIntervalMillis(...);
            javetEngineConfigV8.setPoolDaemonCheckIntervalMillis(...);
            javetEngineConfigV8.setPoolIdleTimeoutSeconds(...);
            javetEngineConfigV8.setPoolMinSize(...);
            javetEngineConfigV8.setPoolMaxSize(...);
            javetEngineConfigV8.setPoolMaxSize(...);
            javetEngineConfigV8.setResetEngineTimeoutSeconds(...);
            javetEngineConfigV8.setJavetLogger(new MyJavetLogger(MyJavetLogger.class.getName()));
            javetEngineConfigV8.setJSRuntimeType(JSRuntimeType.V8);
            return new JavetEnginePool<>(javetEngineConfigV8);
        }

Injection
=========

You may easily inject your engine pool in the Spring way.

.. code-block:: java

    @Resource
    protected IJavetEnginePool javetEnginePool;

[`Home <../../README.rst>`_] [`Javet Tutorial <index.rst>`_]
