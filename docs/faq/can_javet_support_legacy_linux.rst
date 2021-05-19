===============================
Can Javet Support Legacy Linux?
===============================

Yes, Javet can support some legacy linux distributions, eg: Ubuntu 16.04, Ubuntu 18.04, but not with the official builds which rely on more advanced tool chain.

Please download the private builds from this `drive <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_ and prepare your runtime environment as following.

.. code-block:: shell

    sudo apt update
    sudo apt upgrade -y
    sudo apt install build-essential -y
    sudo apt install software-properties-common -y
    sudo add-apt-repository ppa:ubuntu-toolchain-r/test
    sudo apt upgrade -y
    sudo apt install gcc-snapshot -y
    sudo apt upgrade -y
    sudo apt install gcc-7 g++-7 gcc-8 g++-8 gcc-9 g++-9 -y
    sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-9 90 --slave /usr/bin/g++ g++ /usr/bin/g++-9 --slave /usr/bin/gcov gcov /usr/bin/gcov-9
    sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-8 80 --slave /usr/bin/g++ g++ /usr/bin/g++-8 --slave /usr/bin/gcov gcov /usr/bin/gcov-8
    sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-7 70 --slave /usr/bin/g++ g++ /usr/bin/g++-7 --slave /usr/bin/gcov gcov /usr/bin/gcov-7
    sudo update-alternatives --config gcc

    There are 3 choices for the alternative gcc (providing /usr/bin/gcc).

    Selection    Path            Priority   Status
    ------------------------------------------------------------
    * 0            /usr/bin/gcc-9   90        auto mode
      1            /usr/bin/gcc-7   70        manual mode
      2            /usr/bin/gcc-8   80        manual mode
      3            /usr/bin/gcc-9   90        manual mode

Notes
=====

Private builds imply considerable additional effort, so there is no commitments. Please contact the maintainer for private builds wisely.

[`Home <../../README.rst>`_] [`FAQ <index.rst>`_]
