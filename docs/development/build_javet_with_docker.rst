=======================
Build Javet with Docker
=======================

The Docker build supports building Javet for Linux, Windows and Android. As Docker supports Linux and Windows with WSL2, Javet for Linux and Android can also be built on Windows.

Regarding the Docker build for Mac OS, contributors are welcome if you are interested.

Build Environment
=================

Linux Environment
-----------------

* Ubuntu 20.04+
* Git
* Docker

Windows Environment
-------------------

* Latest Windows 10
* WSL2 + Ubuntu 20.04+
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
    docker build -f docker/linux-x86_64/build.Dockerfile .

.. note::

   * It takes roughly a few hours to build the image successfully depending on the network connection.

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
    docker build -m 4G -f docker/windows-x86_64/build.Dockerfile .

.. note::

    * It takes roughly 5-10 hours to build the image successfully. If the internet connection is not that stable, it may take a few days or just fails `forever <https://www.youtube.com/watch?v=Y-rAi-2hZ6U>`_ 😭.
    * The image is so large (60+GB). 😭

Build Javet for Android on Linux or Windows
===========================================

.. code-block:: shell

    git clone https://github.com/caoccao/Javet.git
    cd Javet
    docker build -f docker/android/build.Dockerfile .
