============
Installation
============

Dependency
==========

Maven
-----

.. code-block:: xml

    <!-- Linux and Windows (x86_64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>2.1.0</version>
    </dependency>

    <!-- Mac OS (x86_64 and arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>2.1.0</version>
    </dependency>

Gradle Kotlin DSL
-----------------

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:2.1.0") // Linux and Windows (x86_64)
    implementation("com.caoccao.javet:javet-macos:2.1.0") // Mac OS (x86_64 and arm64)
    implementation("com.caoccao.javet:javet-android:2.1.0") // Android (arm, arm64, x86 and x86_64)

Gradle Groovy DSL
-----------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:2.1.0' // Linux and Windows (x86_64)
    implementation 'com.caoccao.javet:javet-macos:2.1.0' // Mac OS (x86_64 and arm64)
    implementation 'com.caoccao.javet:javet-android:2.1.0' // Android (arm, arm64, x86 and x86_64)

OS Compatibility
================

Windows
-------

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Windows 10                  Yes
Windows 7                   Yes
Windows Server              Not Tested
=========================== =======================================================================================================================

Linux
-----

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Ubuntu 20.04                Yes
Ubuntu 18.04                Yes (since v1.1.0)
Ubuntu 16.04                Yes (since v1.1.0)
Cent OS 8                   Yes (since v1.1.0)
Cent OS 7                   Yes (since v1.1.0)
Other Linux Distributions   Not Tested
=========================== =======================================================================================================================

.. caution::

    * Private builds imply considerable additional effort, so there is no commitments. Please contact the maintainer for private builds wisely. 

Mac OS
------

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Mac OS x86_64               Yes
Mac OS arm64                Temporary (:doc:`../../faq/environment/can_javet_support_mac`)
=========================== =======================================================================================================================

.. caution::

    * The lowest supported version is Catalina.

Android
-------

Android arm, arm64, x86, x86_64 are supported.

==== ================== ====================
ABI  Javet Version      V8 Version
==== ================== ====================
21+  v1.0.3 - v1.0.7    v9.5 - v9.6
23+  v1.1.0+            v9.7+
==== ================== ====================

.. caution::

    * Only V8 mode is supported for Android. Supporting Node.js mode implies huge amount of work, but is not mission impossible. Please contact the maintainer for detail.
    * If you need Node.js features on Android, please refer to project `Javenode <https://github.com/caoccao/Javenode>`_.
