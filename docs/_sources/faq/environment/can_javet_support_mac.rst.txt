======================
Can Javet Support Mac?
======================

Now
===

My `MacBook Air mid-2012 <https://caoccao.blogspot.com/2021/09/macbook-air-mid-2012-from-lion-to-mojave.html>`_ was revived with Mac OS Catalina which is the lowest version supported by latest V8. I managed to include it in the regular release cycle.

Known Issue
-----------

Some of the Node.js symbols might not be exposed correctly. That might cause native node modules malfunction. Quick fix will be served per issue reported.

Will the Mac OS Build be LTS?
-----------------------------

I doubt because when next time V8 abandons Catalina, I won't be able to create any new builds unless someone takes care of the Mac OS build or I get enough donation for a new device. |Donate|

How about arm64?
----------------

That is still not supported. Please |Donate|

.. |Donate| image:: https://img.shields.io/badge/Donate-PayPal-green.svg
    :target: https://paypal.me/caoccao?locale.x=en_US

Before Aug 10, 2021
===================

Short answer is **Yes and No**.

Yes
---

There is no technical difficulties. The code is written in cross-platform manner. Someone needs to pay considerable effort creating and maintaining the Mac build.

No
--

I don't own a decent Mac device. To be more precisely, I have Mac Mini and MacBook Air, but they are too old (building Node.js and V8 would take many hours). Besides, there are both x86 and arm64 to be supported.

I have no plan on buying 2 new Mac devices for supporting Javet only in the near future. If I could be well funded, I would consider supporting Mac.
