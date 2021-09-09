========
Polyfill
========

Node.js Mode
============

Yes, you can polyfill Javet with Node.js modules.

decimal.js
----------

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

Please refer to the :extsource3:`source code <../../../src/test/java/com/caoccao/javet/tutorial/DecimalJavetInV8Mode.java>` for more detail.

V8 Mode
=======

Polyfilling V8 mode is at another project `Javenode <https://github.com/caoccao/Javenode>`_ which aims at simulating Node.js with Java in Javet V8 mode. Why? Because Javet V8 mode is much more secure than the Node.js mode, but lacks of some basic features, e.g. setTimeout, setInterval, etc. So, these must-have API can be found in Javenode.
