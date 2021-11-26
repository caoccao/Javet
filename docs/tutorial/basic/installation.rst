============
Installation
============

Dependency
==========

Maven
-----

.. code-block:: xml

    <!-- Linux or Windows -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>1.0.6</version>
    </dependency>

    <!-- Mac OS (x86_64 Only) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>1.0.6</version>
    </dependency>

Gradle Kotlin DSL
-----------------

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:1.0.6") // Linux or Windows
    implementation("com.caoccao.javet:javet-macos:1.0.6") // Mac OS (x86_64 Only)
    implementation("com.caoccao.javet:javet-android:1.0.6") // Android (arm, arm64, x86 and x86_64)

Gradle Groovy DSL
-----------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:1.0.6' // Linux or Windows
    implementation 'com.caoccao.javet:javet-macos:1.0.6' // Mac OS (x86_64 Only)
    implementation 'com.caoccao.javet:javet-android:1.0.6' // Android (arm, arm64, x86 and x86_64)

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
Ubuntu 18.04                Yes (`Private Build <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_)
Ubuntu 16.04                Yes (`Private Build <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_)
Cent OS 8                   Yes
Cent OS 7                   Yes (`Private Build <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_)
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
Mac OS arm64                No (:doc:`../../faq/environment/can_javet_support_mac`)
=========================== =======================================================================================================================

.. caution::

    * The lowest supported version is Catalina.

Android
-------

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Android arm                 Yes (ABI >= 21)
Android arm64               Yes (ABI >= 21)
Android x86                 Yes (ABI >= 21)
Android x86_64              Yes (ABI >= 21)
=========================== =======================================================================================================================

.. caution::

    * Only V8 mode is supported for Android. Supporting Node.js mode implies huge amount of work, but is not mission impossible. Please contact the maintainer for detail.
    * If you need Node.js features on Android, please refer to project `Javenode <https://github.com/caoccao/Javenode>`_.
    * `ZonedDateTime <https://developer.android.com/reference/java/time/ZonedDateTime>`_ (since ABI 26) is polyfilled by ``org.threeten.bp.ZonedDateTime`` so that Javet can work well with low ABI version.
    * `Stream` and `Optional` are not supported because they are not available with ABI version 21-24.
    * More Android CPU arch support implies considerable effort. Please contact the maintainer for detail.
