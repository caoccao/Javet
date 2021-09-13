==========
Test Javet
==========

Test is designed to completely reuse test cases for either Node.js or V8 mode.

How do test cases know which mode to test? The base test suite compares the timestamp of the Node.js and V8 libraries and set test target to the newer one. So, if a new library is built, test cases automatically set test target to that new library.
