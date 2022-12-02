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
        <version>2.0.3</version>
    </dependency>

    <!-- Mac OS (x86_64 and arm64) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>2.0.3</version>
    </dependency>

Gradle Kotlin DSL
-----------------

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:2.0.3") // Linux and Windows (x86_64)
    implementation("com.caoccao.javet:javet-macos:2.0.3") // Mac OS (x86_64 and arm64)
    implementation("com.caoccao.javet:javet-android:2.0.3") // Android (arm, arm64, x86 and x86_64)

Gradle Groovy DSL
-----------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:2.0.3' // Linux and Windows (x86_64)
    implementation 'com.caoccao.javet:javet-macos:2.0.3' // Mac OS (x86_64 and arm64)
    implementation 'com.caoccao.javet:javet-android:2.0.3' // Android (arm, arm64, x86 and x86_64)

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

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Android arm                 Yes (ABI >= 23)
Android arm64               Yes (ABI >= 23)
Android x86                 Yes (ABI >= 23)
Android x86_64              Yes (ABI >= 23)
=========================== =======================================================================================================================

.. caution::

    * Only V8 mode is supported for Android. Supporting Node.js mode implies huge amount of work, but is not mission impossible. Please contact the maintainer for detail.
    * If you need Node.js features on Android, please refer to project `Javenode <https://github.com/caoccao/Javenode>`_.
    * If you need ABI version 21/22, please use Javet v1.0.x or contact the maintainer for details.
