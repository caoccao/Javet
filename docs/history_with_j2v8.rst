History with J2V8
=================

J2V8 Issues
===========

J2V8 is an excellent project on embedding V8 in Java. However, J2V8 community hasn't been active since 2017.

The last Windows version 4.6.0 was released on 2016 and the last Linux version 4.8.0 was released on 2017. The V8 in Windows v4.6.0 doesn't even fully support ES6.

The latest community activities were around Android versions. The NodeJS API was dropped. The Windows build has been seriously broken for years. The Linux build is nearly broken.

Its API has stopped evolving for years. Many new features I expect just don't get any chances to be implemented. Obviously, if the build system was broken and couldn't be easily fixed, almost no one would like to contribute. That includes me. When I was trying to fix the build system for Windows and Linux, I had to admit that's so, so, so, challenging. Why is that? I think it's not merely J2V8 community's problem because in the meanwhile V8, NodeJS and ECMAScript move forward rapidly causing many unexpected challenges. Someone or some team needs to look after J2V8 from time to time. Unfortunately, reality is cruel.

J2V8 Latest Version
===================

I managed to unofficially built the latest `J2V8 v6.2.0 <https://github.com/caoccao/Javet/releases/tag/0.6.2.0>`_.

* V8 is upgraded to v8.3.110.9 which was released in May, 2020.
* Windows and Linux are supported.
* NodeJS is dropped temporarily.

I've tested the performance between ``j2v8_win32_x86_64-4.6.0.jar`` and ``j2v8_win32_x86_64-6.2.0.jar`` on a Windows machine with CPU i7 10700K. The test code is just ``1+1 -> 2``. Here are the comparisons.

=============================== ============== =============
 Case                             4.6.0 (TPS)   6.2.0 (TPS)  
=============================== ============== =============
 Single Session with 1 Thread       1,003,009     1,338,688 
 Ad-hoc Session with 1 Thread              35           299 
 4 Sessions with 4 threads          2,274,019     4,571,428 
=============================== ============== =============

With this kind of performance improvement, what are the reasons of sticking to ``v4.6.0``?

Why Windows and Linux only?
===========================

* I don't own a decent Mac device. To be more precisely, I have Mac Mini and MacBook Air, but they are too old (building V8 would take many hours). And I have no plan on buying a new one in the near future. Call for donation? Don't be joking. So there's no MacOS release.
* I don't intend to support Android for now.

Why not Automate the J2V8 Build System?
=======================================

TL;DR: It's too hard.

* V8 is evolving rapidly. v8.3.110.9 is a relatively easy one to be mastered. I didn't move forward well with v8.9.213 which placed a different set of *solvable* challenges to me. I plan to revisit the latest V8 in near future.
* NodeJS was removed early. I haven't got time reviving it in J2V8.
* J2V8 build system is too old.
  * Gradle v2.14.1 is far from the lowest supported gradle version in my latest IntelliJ IDEA. And I don't have interest in installing a legacy Eclipse to play with that version of gradle. Hey, why not upgrade gradle to satisfy IDEA? I tried, but all was bad luck. You may take a try, then understand what I have suffered from.
  * CMake is old and seriously broken on Windows. Nowadays, V8 only supports VS 2017 or 2019, but ``CMakeLists.txt`` is still at the VS 2015 age. No surprise, it doesn't work at all.
  * Docker build is deeply broken as well. The dependent docker image was gone. There are many errors in many steps. Sitting there, watching the docker build breaks made me full of frustration because I thought it would take me a few months fixing the problems, but I don't have a few months. No one pays me to do that.
  * Python2 scripts form the outer layer of the build system, also hide the actual building logic from someone who tries to fix the build system. I don't want to spend my precious time fixing Python2 scripts, because I've been on Python3 for many years. I wish my hair could be as much as Guido van Rossum's. Obviously, I am not, so no more Python2.
  * Maven is old but the least problematic. At least it allows me to package the jar files with my hack to the ``pom.xml``.

With these uncertainties, to me, automating the build system is something with ROI closing to 0. Supposing I achieved it in a particular version of V8, let's say v8.3.110.9, it would for sure break in v8.9.213. Yes, I've confirmed that.

Why not Deploy J2V8 to Maven Repository?
========================================

* I don't have the permission to its official repository.
* There has been no Windows / Linux releases deployed since 2016 / 2017. I really don't know who to contact with.
* You may easily integrate the jar files in your local maven repository.

[`Home <../README.rst>`_]
