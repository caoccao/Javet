=======================
Build Javet with Docker
=======================

The Docker build supports building Javet for Linux and Windows. As Docker supports Linux and Windows with WSL2, Javet for Linux can also be built on Windows.

Regarding Docker build for Mac OS, contributors are welcome if you are interested. Or, you will have to wait for a long while.

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

Please make sure the network connection to the Docker Hub and Github is up and running. The Docker repository of the Javet images are available at https://hub.docker.com/repository/docker/sjtucaocao.

Build Javet for Linux on Linux or Windows
=========================================

1. Clone Javet.
2. Navigate to the root directory of the Javet repository.
3. Execute ``docker build -f docker/linux-x86_64/build.Dockerfile .`` (Be careful, please include the last ``.``).

   * Docker will pull the corresponding image (~10GB) from Docker Hub.
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
4. Clone Javet.
5. Navigate to the root directory of the Javet repository.
6. Execute ``docker build -t sjtucaocao/javet-windows:x.x.x -m 4G -f docker/windows-x86_64/base.Dockerfile .`` (Be careful, please include the last ``.``).
7. Execute ``docker build -f docker/windows-x86_64/build.Dockerfile .`` (Be careful, please include the last ``.``).

Note:

* The base image is so large (60+GB) that it's not efficient to push the base image to docker hub. Of course, without the base image at docker hub, it's not wise to enable the github workflow for Windows build.
* Building the base image takes many hours and may experience intermittent errors.

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
