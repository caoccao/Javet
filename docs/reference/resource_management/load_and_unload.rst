===============
Load and Unload
===============

As documented in :doc:`../../development/design`, Javet supports loading and unloading the JNI libraries during runtime in both Node.js and V8 modes.

Can Javet Native Library be Loaded or Unloaded On-demand?
=========================================================

Yes, Javet supports that.

Unload
------

Assuming the JNI library per mode is already loaded, here are the step-by-step on how to unload it.

.. code-block:: java

    // Step 1: Set library reloadable. Why? Because Javet defaults that switch to false.
    V8Host.setLibraryReloadable(true);
    // Step 2: Get V8Host per JS runtime type.
    V8Host v8Host = V8Host.getInstance(jsRuntimeType);
    // Step 3: Unload the library.
    v8Host.unloadLibrary();
    // Step 4: Restore the switch.
    V8Host.setLibraryReloadable(false);

How does ``unloadLibrary()`` work? There is no API that allows unloading a JNI library explicitly. The only way is GC will automatically unload the library if all references to that library are garbage collectable. So, application is supposed to close all V8 values, V8 runtimes prior to calling ``unloadLibrary()``. 

Load
----

Assuming the JNI library per mode is already unloaded, here are the step-by-step on how to load it again.

.. code-block:: java

    // Step 1: Get V8Host per JS runtime type.
    V8Host v8Host = V8Host.getInstance(jsRuntimeType);
    // Step 2: Load the library.
    v8Host.loadLibrary();

.. note::

    * ``unloadLibrary()`` can only take effect after all references are garbage collectable.
    * ``loadLibrary()`` is internally called by Javet at the first time and only takes effect after ``unloadLibrary()`` is called.
    * ``loadLibrary()`` and ``unloadLibrary()`` are for experiment only. **They may be unstable and crash JVM. Please use this feature at your own risk.**

Can Javet Native Library be Deployed to a Custom Location?
==========================================================

Yes. By default, the native library is deployed to system temp which might not be accessible in some cases. Here is a simple way of telling Javet where to deploy the library.

.. code-block:: java

    JavetLibLoader.setLibLoadingListener(new IJavetLibLoadingListener() {
        @Override
        public File getLibPath(JSRuntimeType jsRuntimeType) {
            return new File("/../anywhere");
        }
    });

By default, the native library is deployed by Javet. To bypass the deployment, one more function is required to be overridden. That also means the applications are responsible for deploying the native library to the right location.

.. code-block:: java

    JavetLibLoader.setLibLoadingListener(new IJavetLibLoadingListener() {
        @Override
        public File getLibPath(JSRuntimeType jsRuntimeType) {
            return new File("/../anywhere");
        }

        @Override
        boolean isDeploy(JSRuntimeType jsRuntimeType) {
            return false;
        }
    });

.. caution::

    * ``JavetLibLoader.setLibLoadingListener()`` must be called before ``V8Host`` is called, otherwise it won't take effect.
    * The return path from ``getLibPath()`` does not include the library file name because Javet will prepare it.

Can Javet Native Library Deployment be Skipped?
===============================================

Yes. In some cases, the native library can be directly deployed to system library path to avoid dynamic deployment. That brings better performance and less jar file size. Here is a sample way of telling Javet to skip the deployment.

.. code-block:: java

    JavetLibLoader.setLibLoadingListener(new IJavetLibLoadingListener() {
        @Override
        public boolean isLibInSystemPath(JSRuntimeType jsRuntimeType) {
            return true;
        }
    });

.. caution::

    ``JavetLibLoader.setLibLoadingListener()`` must be called before ``V8Host`` is called, otherwise it won't take effect.
