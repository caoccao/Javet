==========
Converters
==========

.. toctree::
    :maxdepth: 1
    :glob:

    primitive_converter
    object_converter
    proxy_converter
    bridge_converter
    custom_converter
    proxy_plugins

Comparisons
===========

.. raw:: html

    <embed>
      <table class="docutils" style="text-align: center;">
        <thead>
          <tr class="row-odd">
            <th>From Java</th>
            <th>To V8</th>
            <th>Primitive Converter</th>
            <th>Object Converter</th>
            <th>Proxy Converter</th>
            <th>Bridge Converter</th>
            <th>Custom Converter</th>
          </tr>
        </thead>
        <tbody>
          <tr class="row-even">
            <td rowspan="2">Primitive</td>
            <td>Native</td>
            <td>✔️</td>
            <td>✔️</td>
            <td>✔️</td>
            <td>❌️</td>
            <td>✔️</td>
          </tr>
          <tr class="row-odd">
            <td>Proxy</td>
            <td>❌️</td>
            <td>❌️</td>
            <td>❌️</td>
            <td>✔️</td>
            <td>✔️</td>
          </tr>
          <tr class="row-even">
            <td rowspan="2">Array, List, Set, Map</td>
            <td>Native</td>
            <td>❌️</td>
            <td>✔️</td>
            <td>✔️</td>
            <td>❌️</td>
            <td>✔️</td>
          </tr>
          <tr class="row-odd">
            <td>Proxy</td>
            <td>❌️</td>
            <td>❌️</td>
            <td>❌️</td>
            <td>✔️</td>
            <td>✔️</td>
          </tr>
          <tr class="row-even">
            <td rowspan="2">Other</td>
            <td>Native</td>
            <td>❌️</td>
            <td>✔️</td>
            <td>❌️</td>
            <td>❌️</td>
            <td>✔️</td>
          </tr>
          <tr class="row-odd">
            <td>Proxy</td>
            <td>❌️</td>
            <td>❌️</td>
            <td>✔️</td>
            <td>✔️</td>
            <td>✔️</td>
          </tr>
        </tbody>
      </table>
    </embed>

Inside Converters
=================

Binding via Native
------------------

.. image:: ../../resources/images/javet_converter_binding_via_native.png
    :alt: Javet Converter - Binding via Native

Binding via Proxy
-----------------

.. image:: ../../resources/images/javet_converter_binding_via_proxy.png
    :alt: Javet Converter - Binding via Proxy

Null Safety
-----------

What if the object converter meets ``null`` or ``undefined`` when target type is primitive? This is a quite famous topic in Java because converting null to primitive type results in ``java.lang.NullPointerException``. Luckily, Javet object converter is null safe by injecting default primitive values to ``JavetConverterConfig`` and these default primitive values can be overridden.

Functions and Objects
---------------------

There are few challenges in the object conversion.

* V8 functions cannot be easily represented by Java objects.
* V8 objects and maps cannot be easily differentiated in Java.
* Sometimes unexpected functions from object conversion may break applications.

So, Javet introduced ``IJavetEntityFunction`` and ``IJavetEntityMap`` so that V8 functions and V8 maps can be precisely represented in Java.

Also, ``JavetConverterConfig`` exposes ``setSkipFunctionInObject(boolean)`` and ``setExtractFunctionSourceCode(boolean)`` to give application the opportunity to skip functions in objects or extract source code of functions.

If the source code is provided to a user defined function, Javet object converter will inject that function from the source code automatically. That makes sure Java object from V8 object can be smoothly converted back to V8 object at both property and function levels.

Circular Structure
------------------

It is inefficient and inconvenient for Javet to substantially detect circular structure during object conversion. Instead, Javet converter keeps increasing the depth of recursion and throws ``JavetConverterException`` when maximum depth is reach. Maximum depth can be changed before object conversion is started. This is a cheap operation with high performance.

Please avoid setting maximum depth to an unrealistic number because JVM will throw ``StackOverflowError`` which brings considerable performance overhead. The thing worse than that is there will be memory leak because resource recycling logic written in ``finally`` block sometimes won't be called when stack overflow occurs. Attackers may easily drain the server resource in minutes by sending tiny circular structure data.

When does the Config Take Effect?
---------------------------------

The config only takes effect when the actual conversion takes place. Once the binding between the JavaScript object and Java object is set, the config changes will no longer be applied to it by the converter. That behavior allows some fancy things to happen. E.g. a Java ``Map`` can be read-only in V8 when ``setProxyMapEnabled(false)`` is called whereas the next Java ``Map`` can be read-write in V8 when ``setProxyMapEnabled(true)`` is called.

Can Built-in Converter be Ignored?
----------------------------------

Yes. One of the beauties of the built-in converter is it is convenient to bi-directionally convert arbitrary objects. However, sometimes applications want to take fine-grained control over every conversion. E.g. ``a`` needs to use ``JavetObjectConverter``, ``b`` needs to use ``JavetProxyConverter``, etc.

No worry. Applications may use any converter to convert Java objects to ``V8Value`` objects or vice versa. Javet API is able to accept ``V8Value`` and return ``V8Value``. In this case, the built-in converter is ignored.
