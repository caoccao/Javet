============
Installation
============

Dependency - Desktop
====================

The Javet dependency design for the desktop consists of the following:

* A core library that contains the Java code.
* Various libraries each of which contains a native binary for various OS / JS Runtime Type / i18n Type.

With this design, only the libraries needed are loaded.

Maven
-----

The following dependency must be added.

.. code-block:: xml

    <dependency>
        <groupId>com.caoccao.javet</groupId>
        <artifactId>javet</artifactId>
        <version>4.1.1</version>
    </dependency>

The following dependencies contain the non-i18n binaries for various OS / JS Runtime Type.

.. tab:: Node (Express)

    .. code-block:: xml

        <!-- Linux (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-linux-x86_64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Linux (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-linux-arm64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-macos-x86_64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-macos-arm64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Windows (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-windows-x86_64</artifactId>
            <version>4.1.1</version>
        </dependency>

.. tab:: V8 (Express)

    .. code-block:: xml

        <!-- Linux (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-linux-x86_64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Linux (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-linux-arm64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-macos-x86_64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-macos-arm64</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Windows (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-windows-x86_64</artifactId>
            <version>4.1.1</version>
        </dependency>

.. tab:: Node (Complete)

    .. code-block:: xml

        <properties>
          <javet.version>4.1.1</javet.version>
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
                <artifactId>javet-node-windows-x86_64</artifactId>
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
                <artifactId>javet-node-linux-x86_64</artifactId>
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
                <artifactId>javet-node-linux-arm64</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>x86</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-node-macos-x86_64</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos-arm64</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>arm64</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-node-macos-arm64</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
        </profiles>

.. tab:: V8 (Complete)

    .. code-block:: xml

        <properties>
          <javet.version>4.1.1</javet.version>
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
                <artifactId>javet-v8-windows-x86_64</artifactId>
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
                <artifactId>javet-v8-linux-x86_64</artifactId>
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
                <artifactId>javet-v8-linux-arm64</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>x86</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-v8-macos-x86_64</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos-arm64</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>arm64</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-v8-macos-arm64</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
        </profiles>

The following dependencies contain the i18n binaries for various OS / JS Runtime Type.

.. tab:: Node i18n (Express)

    .. code-block:: xml

        <!-- Linux (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-linux-x86_64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Linux (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-linux-arm64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-macos-x86_64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-macos-arm64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Windows (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-node-windows-x86_64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

.. tab:: V8 i18n (Express)

    .. code-block:: xml

        <!-- Linux (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-linux-x86_64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Linux (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-linux-arm64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-macos-x86_64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Mac OS (arm64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-macos-arm64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

        <!-- Windows (x86_64) -->
        <dependency>
            <groupId>com.caoccao.javet</groupId>
            <artifactId>javet-v8-windows-x86_64-i18n</artifactId>
            <version>4.1.1</version>
        </dependency>

.. tab:: Node i18n (Complete)

    .. code-block:: xml

        <properties>
          <javet.version>4.1.1</javet.version>
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
                <artifactId>javet-node-windows-x86_64-i18n</artifactId>
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
                <artifactId>javet-node-linux-x86_64-i18n</artifactId>
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
                <artifactId>javet-node-linux-arm64-i18n</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>x86</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-node-macos-x86_64-i18n</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos-arm64</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>arm64</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-node-macos-arm64-i18n</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
        </profiles>

.. tab:: V8 i18n (Complete)

    .. code-block:: xml

        <properties>
          <javet.version>4.1.1</javet.version>
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
                <artifactId>javet-v8-windows-x86_64-i18n</artifactId>
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
                <artifactId>javet-v8-linux-x86_64-i18n</artifactId>
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
                <artifactId>javet-v8-linux-arm64-i18n</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>x86</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-v8-macos-x86_64-i18n</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
          <profile>
            <id>macos-arm64</id>
            <activation>
              <os>
                <family>mac</family>
                <arch>arm64</arch>
              </os>
            </activation>
            <dependencies>
              <dependency>
                <groupId>com.caoccao.javet</groupId>
                <artifactId>javet-v8-macos-arm64-i18n</artifactId>
                <version>${javet.version}</version>
              </dependency>
            </dependencies>
          </profile>
        </profiles>

Gradle Kotlin DSL - Desktop
---------------------------

.. tab:: Express

    .. code-block:: kotlin

        implementation("com.caoccao.javet:javet:4.1.1") // Must-have
        implementation("com.caoccao.javet:javet-node-linux-arm64:4.1.1")
        implementation("com.caoccao.javet:javet-node-linux-arm64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-node-linux-x86_64:4.1.1")
        implementation("com.caoccao.javet:javet-node-linux-x86_64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-node-macos-arm64:4.1.1")
        implementation("com.caoccao.javet:javet-node-macos-arm64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-node-macos-x86_64:4.1.1")
        implementation("com.caoccao.javet:javet-node-macos-x86_64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-node-windows-x86_64:4.1.1")
        implementation("com.caoccao.javet:javet-node-windows-x86_64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-v8-linux-arm64:4.1.1")
        implementation("com.caoccao.javet:javet-v8-linux-arm64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-v8-linux-x86_64:4.1.1")
        implementation("com.caoccao.javet:javet-v8-linux-x86_64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-v8-macos-arm64:4.1.1")
        implementation("com.caoccao.javet:javet-v8-macos-arm64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-v8-macos-x86_64:4.1.1")
        implementation("com.caoccao.javet:javet-v8-macos-x86_64-i18n:4.1.1")
        implementation("com.caoccao.javet:javet-v8-windows-x86_64:4.1.1")
        implementation("com.caoccao.javet:javet-v8-windows-x86_64-i18n:4.1.1")

.. tab:: Complete

    .. code-block:: kotlin

        import org.gradle.internal.os.OperatingSystem

        val os = OperatingSystem.current()
        val arch = System.getProperty("os.arch")
        val isI18n = false
        val isNode = false
        val i18nType = if (isI18n) "-i18n" else ""
        val jsRuntimeTimeType = if (isNode) "node" else "v8"
        val osType = if (os.isWindows) "windows" else
            if (os.isMacOsX) "macos" else
            if (os.isLinux) "linux" else ""
        val archType = if (arch == "aarch64" || arch == "arm64") "arm64" else "x86_64"
        implementation("com.caoccao.javet:javet:4.1.1")
        implementation("com.caoccao.javet:javet-$jsRuntimeTimeType-$osType-$archType$i18nType:4.1.1")

Gradle Groovy DSL - Desktop
---------------------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet:4.1.1' // Must-have
    implementation 'com.caoccao.javet:javet-node-linux-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-linux-arm64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-node-linux-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-linux-x86_64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-node-macos-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-macos-arm64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-node-macos-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-macos-x86_64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-node-windows-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-node-windows-x86_64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-linux-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-linux-arm64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-linux-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-linux-x86_64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-macos-arm64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-macos-arm64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-macos-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-macos-x86_64-i18n:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-windows-x86_64:4.1.1'
    implementation 'com.caoccao.javet:javet-v8-windows-x86_64-i18n:4.1.1'

Dependencies - Android
======================

Gradle Kotlin DSL - Android
---------------------------

.. code-block:: kotlin

    implementation("com.caoccao.javet:javet-node-android:4.1.1") // Android Node (arm, arm64, x86 and x86_64)
    implementation("com.caoccao.javet:javet-node-android-i18n:4.1.1") // Android Node (arm64 and x86_64)
    implementation("com.caoccao.javet:javet-v8-android:4.1.1") // Android V8 (arm, arm64, x86 and x86_64)
    implementation("com.caoccao.javet:javet-v8-android-i18n:4.1.1") // Android V8 (arm, arm64, x86 and x86_64)

Gradle Groovy DSL - Android
---------------------------

.. code-block:: groovy

    implementation 'com.caoccao.javet:javet-node-android:4.1.1' // Android Node (arm, arm64, x86 and x86_64)
    implementation 'com.caoccao.javet:javet-node-android-i18n:4.1.1' // Android Node (arm64 and x86_64)
    implementation 'com.caoccao.javet:javet-v8-android:4.1.1' // Android V8 (arm, arm64, x86 and x86_64)
    implementation 'com.caoccao.javet:javet-v8-android-i18n:4.1.1' // Android V8 (arm, arm64, x86 and x86_64)

OS Compatibility
================

Windows
-------

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Windows 11                  Yes
Windows 10                  Yes
Windows 7                   Yes
Windows Server              Not Tested
=========================== =======================================================================================================================

Linux
-----

=========================== =======================================================================================================================
OS                          Compatible
=========================== =======================================================================================================================
Ubuntu 24.04                Yes
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

Internationalization (i18n)
===========================

If i18n is enabled, please make sure the steps in :doc:`../advanced/internationalization_i18n` are well implemented otherwise Javet will crash.
