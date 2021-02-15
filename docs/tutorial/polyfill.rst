========
Polyfill
========

Yes, you can polyfill Javet with NodeJS modules.

decimal.js
==========

.. code-block:: java

    public void loadJS() throws JavetException {
        File decimalJSFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/node_modules/decimal.js/decimal.js");
        if (decimalJSFile.exists() && decimalJSFile.canRead()) {
            logger.logInfo("Loading {0}.", decimalJSFile.getAbsolutePath());
            v8Runtime = V8Host.getInstance().createV8Runtime();
            v8Runtime.lock();
            v8Runtime.getExecutor(decimalJSFile).executeVoid();
        } else {
            logger.logError("{0} is not found.", decimalJSFile.getAbsolutePath());
            logger.logError("Please make sure NodeJS is installed, then visit script/node directory and run npm install.");
        }
    }

    public void test() throws JavetException {
        logger.logInfo("1.23 + 2.34 = {0}", v8Runtime.getExecutor(
                "const a = new Decimal(1.23);" +
                        "const b = new Decimal(2.34);" +
                        "a.add(b).toString();").executeString());
    }

Please refer to `source code <../../src/test/java/com/caoccao/javet/tutorial/DecimalJavet.java>`_ for more detail.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
