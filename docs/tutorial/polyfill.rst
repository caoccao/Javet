========
Polyfill
========

Yes, you can polyfill Javet with Node.js modules.

decimal.js
==========

.. code-block:: java

    public void loadJS() throws JavetException {
        File decimalJSFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/node_modules/decimal.js/decimal.js");
        if (decimalJSFile.exists() && decimalJSFile.canRead()) {
            getLogger().logInfo("Loading {0}.", decimalJSFile.getAbsolutePath());
            V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
            v8Runtime.getExecutor(decimalJSFile).executeVoid();
        } else {
            getLogger().logError("{0} is not found.", decimalJSFile.getAbsolutePath());
            getLogger().logError("Please make sure Node.js is installed, then visit script/node directory and run npm install.");
        }
    }

    public void test() throws JavetException {
        V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
        getLogger().logInfo("1.23 + 2.34 = {0}", v8Runtime.getExecutor(
                "const a = new Decimal(1.23);" +
                        "const b = new Decimal(2.34);" +
                        "a.add(b).toString();").executeString());
        try (V8ValueFunction v8ValueFunctionDecimal = v8Runtime.getGlobalObject().get("Decimal")) {
            try (V8ValueObject v8ValueObjectDecimal = v8ValueFunctionDecimal.callAsConstructor("123.45")) {
                getLogger().logInfo(v8ValueObjectDecimal.toString());
                if (v8ValueObjectDecimal.hasOwnProperty("constructor")) {
                    try (V8ValueFunction v8ValueFunction = v8ValueObjectDecimal.get("constructor")) {
                        String name = v8ValueFunction.getString("name");
                        if ("Decimal".equals(name)) {
                            BigDecimal bigDecimal = new BigDecimal(v8ValueObjectDecimal.toString());
                            getLogger().logInfo("BigDecimal: {0}", bigDecimal.toString());
                        }
                    }
                }
            }
        }
    }

Please refer to `source code <../../src/test/java/com/caoccao/javet/tutorial/DecimalJavetInV8Mode.java>`_ for more detail.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
