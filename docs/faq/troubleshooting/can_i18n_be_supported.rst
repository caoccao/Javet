======================
Can i18n be Supported?
======================

By default, i18n is not supported for the following reasons.

* To reduce the library size.
* To avoid embedding huge amount of i18n related files.
* To be able to host multiple Node.js runtimes. (Node.js with i18n only allows one runtime and the attempt on creating the second runtime causes the JVM to crash immediately.)

It can be enabled for V8 mode in private builds. Please contact the maintainer for details.
