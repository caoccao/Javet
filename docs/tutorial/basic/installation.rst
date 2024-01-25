============
Installation
============

Dependency
==========

Maven
-----

.. tab:: Express

    .. code-block:: xml

        <!-- Linux and Windows (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet</artifactId>
            <version>3.0.4</version>
        </dependency>

        <!-- Linux (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-linux-arm64</artifactId>
            <version>3.0.4</version>
        </dependency>

        <!-- Mac OS (x86_64 and arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-macos</artifactId>
            <version>3.0.4</version>
        </dependency>

.. tab:: Complete

    .. code-block:: xml

        <properties>
          <javet.version>3.0.4</javet.version>
        </properties>

        <profiles>
          <profile>
            <id>windows</id>
            <activation>
              <os>
                <family>windows</family>
                <arch>x86</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>linux</id>
            <activation>
              <os>
                <family>unix</family>
                <arch>x86</arch>
              </os>
              <activeByDefault>true</activeByDefault>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>linux-arm64</id>
            <activation>
              <os>
                <family>unix</family>
                <arch>arm64</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-linux-arm64</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos</id>
            <activation>
              <os>
                <family>mac</family>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-macos</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
        </profiles>

Gradle Kotlin DSL
-----------------

.. tab:: Express

    .. code-block:: kotlin

        implementation("com.caoccao.javet:javet:3.0.4") // Linux and Windows (x86_64)
        implementation("com.caoccao.javet:javet-linux-arm64:3.0.4") // Linux (arm64)
        implementation("com.caoccao.javet:javet-macos:3.0.4") // Mac OS (x86_64 and arm64)
        implementation("com.caoccao.javet:javet-android:3.0.4") // Android (arm, arm64, x86 and x86_64)

.. tab:: Complete

    .. code-block:: kotlin

        import org.gradle.internal.os.OperatingSystem

        val os = OperatingSystem.current()
        val cpuArch = System.getProperty("os.arch")
        if (os.isMacOsX) {
            implementation("com.caoccao.javet:javet:3.0.4")
        } else if (os.isLinux && (cpuArch == "aarch64" || cpuArch == "arm64")) {
            implementation("com.caoccao.javet:javet-linux-arm64:3.0.4")
        } else {
            implementation("com.caoccao.javet:javet-macos:3.0.4")
        }

Gradle Groovy DSL
-----------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:3.0.4' // Linux and Windows (x86_64)
    implementation 'com.caoccao.javet:javet-linux-arm64:3.0.4' // Linux (arm64)
    implementation 'com.caoccao.javet:javet-macos:3.0.4' // Mac OS (x86_64 and arm64)
    implementation 'com.caoccao.javet:javet-android:3.0.4' // Android (arm, arm64, x86 and x86_64)

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
Ubuntu 22.04                Yes
Ubuntu 20.04                Yes
Ubuntu 18.04                Yes (since v1.1.0 and private builds)
Ubuntu 16.04                Yes (since v1.1.0 and private builds)
Cent OS 8                   Yes (since v1.1.0 and private builds)
Cent OS 7                   Yes (since v1.1.0 and private builds)
Other Linux Distributions   Not Tested
=========================== =======================================================================================================================

.. caution::

    * Private builds imply considerable additional effort, so there is no commitments. Please contact the maintainer for private builds wisely. 

=============== ========================
glibc Version   Javet Version
=============== ========================
2.29            v3.0.3+
2.34            v3.0.1 - v3.0.2
2.29            v0.8.6 - v3.0.0
2.25            v0.8.0 - v0.8.5
2.14            v0.7.0 - v0.7.4
=============== ========================

Mac OS
------

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Mac OS x86_64               Yes
Mac OS arm64                Yes
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
23+  v1.1.0 - v2.1.1    v9.7 - v11.2
24+  v2.1.2+            v11.3+
==== ================== ====================

.. caution::

    * Only V8 mode is supported for Android. Supporting Node.js mode implies huge amount of work, but is not mission impossible. Please contact the maintainer for details.
    * If you need Node.js features on Android, please refer to project `Javenode <https://github.com/caoccao/Javenode>`_.
