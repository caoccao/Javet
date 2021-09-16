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
        <version>0.9.13</version>
    </dependency>

    <!-- Mac OS (x86_64 Only) -->
    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet-macos</artifactId>
        <version>0.9.13</version>
    </dependency>

Gradle Kotlin DSL
-----------------

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet:0.9.13") // Linux or Windows
    implementation("com.caoccao.javet:javet-macos:0.9.13") // Mac OS (x86_64 Only)

Gradle Groovy DSL
-----------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:0.9.13' // Linux or Windows
    implementation 'com.caoccao.javet:javet-macos:0.9.13' // Mac OS (x86_64 Only)

OS Compatibility
================

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Windows 10                  Yes
Windows 7                   Yes
Windows Server              Not Tested
Ubuntu 20.04                Yes
Ubuntu 18.04                Yes (`Private Build <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_)
Ubuntu 16.04                Yes (`Private Build <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_)
Other Linux Distributions   Not Tested
MacOS x86_64                Yes
MacOS arm64                 No (:doc:`../../faq/environment/can_javet_support_mac`)
=========================== =======================================================================================================================