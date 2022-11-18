=======================
Build Javet with Docker
=======================

The Docker build supports building Javet for Linux, Windows and Android. As Docker supports Linux and Windows with WSL2, Javet for Linux and Android can also be built on Windows.

Regarding the Docker build for Mac OS, contributors are welcome if you are interested. Or, you will have to wait for a long while.

Build Environment
=================

Linux Environment
-----------------

* Ubuntu 20.04
* Git
* Docker

Windows Environment
-------------------

* Latest Windows 10
* WSL2 + Ubuntu 20.04
* Git
* Docker

Docker Hub and Github
---------------------

Please make sure the network connection to the Docker Hub and Github is up and running. The Docker repository of the Javet images are available at https://hub.docker.com/r/docker/sjtucaocao.

Build Javet for Linux on Linux or Windows
=========================================

.. code-block:: shell

    git clone https://github.com/caoccao/Javet.git
    cd Javet
    docker build -f docker/linux-x86_64/artifact.Dockerfile .

.. note::

   * Docker will pull the corresponding image (~5GB) from Docker Hub.
   * The actual build takes few minutes including pulling dependent libraries from Maven Central, building and testing.

Build Javet for Windows on Windows
==================================

1. Update daemon.json

.. code-block:: json

    "storage-opts": [
      "dm.basesize=120GB",
      "size=120GB"
    ]

2. Restart WSL2
3. Restart docker

.. code-block:: shell

    git clone https://github.com/caoccao/Javet.git
    cd Javet
    docker build -m 4G -f docker/windows-x86_64/base.Dockerfile .
    docker build -f docker/windows-x86_64/build.Dockerfile .

.. note::

    * It takes roughly 5-10 hours to build the base image successfully. If the internet connection is not that stable, it may take a few days or just fails `forever <https://www.youtube.com/watch?v=Y-rAi-2hZ6U>`_ 😭.
    * The base image is so large (60+GB) 😭 that it's not efficient to push the base image to the docker hub. Of course, without the base image at the docker hub, it's not wise to enable the github workflow for the Windows build.

Build Javet for Android on Linux or Windows
===========================================

.. code-block:: shell

    git clone https://github.com/caoccao/Javet.git
    cd Javet
    docker build -f docker/android/build.Dockerfile .

.. note::

    * Docker will pull the corresponding image (~11GB) from Docker Hub.
    * The actual build takes few minutes including pulling dependent libraries from Maven Central, building and testing.
