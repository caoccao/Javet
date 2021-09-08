==========================================================
A dynamic link library (DLL) initialization routine failed
==========================================================

This failure happens when Javet Node.js mode tries to load a Node.js native module. The root cause is some ``NAPI`` symbols cannot be found on Windows. The fix is is available at :doc:`../reference/modularization`.
