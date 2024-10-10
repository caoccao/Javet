======================
Can i18n be Supported?
======================

By default, i18n is not supported for the following reasons.

* To reduce the library size.
* To avoid embedding huge amount of i18n related files.
* To be able to host multiple Node.js runtimes. (Node.js with i18n only allows one runtime and the attempt on creating the second runtime causes the JVM to crash immediately.)

It can be enabled for V8 mode in snapshot builds. Please contact the maintainer for details.

How to Enable i18n in Node.js Mode?
===================================

* Make sure you have the snapshot build with i18n enabled downloaded.
* Download ``icudt*.dat`` from somewhere. If you don't have any idea, please Google.
* Call ``NodeRuntimeOptions.NODE_FLAGS.setIcuDataDir(/* where the icu data dir that contains icudt*.dat files is located */)`` before the first ``NodeRuntime`` is created.
