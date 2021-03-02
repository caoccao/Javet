==================
Spring Integration
==================

As Javet is a fundamental SDK, it doesn't rely on Spring Framework so that Javet users don't get dependency hell. But, Javet can be integrated with Spring easily.

Configuration
=============

* Create a Spring configuration.
* Declare ``IJavetEnginePool`` as ``@Bean``.
* Set the pool implement in ``@PostConstruct``.

.. code-block:: java

    @Configuration
    @PropertySource("classpath:javet-engine.properties")
    @ConfigurationProperties(prefix = "javet.engine")
    public class MyJavetEngineConfig {
        @Value("32")
        private int poolMaxSize;
        @Value("8")
        private int poolMinSize;
        @Value("60")
        private int poolIdleTimeoutSeconds;
        @Value("1000")
        private int poolDaemonCheckIntervalMillis;
        @Value("3600")
        private int resetEngineTimeoutSeconds;
        private IJavetLogger javetLogger;
        private IJavetEnginePool javetEnginePool;

        @PostConstruct
        public void postConstruct() {
            initializeJavet();
        }

        @PreDestroy
        public void preDestroy() throws JavetException {
            // There is no need to close Javet engine pool explicitly because spring does so.
        }

        @Bean
        public IJavetEnginePool getJavetEnginePool() {
            return javetEnginePool;
        }

        private void initializeJavet() {
            javetLogger = new MyJavetLogger("SampleLogger");
            JavetEngineConfig javetEngineConfig = new JavetEngineConfig();
            javetEngineConfig.setPoolDaemonCheckIntervalMillis(getPoolDaemonCheckIntervalMillis());
            javetEngineConfig.setPoolIdleTimeoutSeconds(getPoolIdleTimeoutSeconds());
            javetEngineConfig.setPoolMinSize(getPoolMinSize());
            javetEngineConfig.setPoolMaxSize(getPoolMaxSize());
            javetEngineConfig.setResetEngineTimeoutSeconds(getResetEngineTimeoutSeconds());
            javetEngineConfig.setJavetLogger(javetLogger);
            javetEnginePool = new MyJavetEnginePool(javetEngineConfig);
        }

Injection
=========

You may easily inject your engine pool in the Spring way.

.. code-block:: java

    @Resource
    protected IJavetEnginePool javetEnginePool;

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
