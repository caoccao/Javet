/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.enums;

import java.util.stream.Stream;

/**
 * The enum V8 value internal type.
 *
 * @since 0.9.13
 */
public enum V8ValueInternalType {
    /** The V8 Undefined type. */
    Undefined(0, "Undefined"),
    /** The V8 Null type. */
    Null(1, "Null"),
    /** The V8 NullOrUndefined type. */
    NullOrUndefined(2, "NullOrUndefined"),
    /** The V8 True type. */
    True(3, "True"),
    /** The V8 False type. */
    False(4, "False"),
    /** The V8 Name type. */
    Name(5, "Name"),
    /** The V8 String type. */
    String(6, "String"),
    /** The V8 Symbol type. */
    Symbol(7, "Symbol"),
    /** The V8 Function type. */
    Function(8, "Function"),
    /** The V8 Array type. */
    Array(9, "Array"),
    /** The V8 Object type. */
    Object(10, "Object"),
    /** The V8 BigInt type. */
    BigInt(11, "BigInt"),
    /** The V8 Boolean type. */
    Boolean(12, "Boolean"),
    /** The V8 Number type. */
    Number(13, "Number"),
    /** The V8 External type. */
    External(14, "External"),
    /** The V8 Int32 type. */
    Int32(15, "Int32"),
    /** The V8 Date type. */
    Date(16, "Date"),
    /** The V8 ArgumentsObject type. */
    ArgumentsObject(17, "ArgumentsObject"),
    /** The V8 BigIntObject type. */
    BigIntObject(18, "BigIntObject"),
    /** The V8 BooleanObject type. */
    BooleanObject(19, "BooleanObject"),
    /** The V8 NumberObject type. */
    NumberObject(20, "NumberObject"),
    /** The V8 StringObject type. */
    StringObject(21, "StringObject"),
    /** The V8 SymbolObject type. */
    SymbolObject(22, "SymbolObject"),
    /** The V8 NativeError type. */
    NativeError(23, "NativeError"),
    /** The V8 RegExp type. */
    RegExp(24, "RegExp"),
    /** The V8 AsyncFunction type. */
    AsyncFunction(25, "AsyncFunction"),
    /** The V8 GeneratorFunction type. */
    GeneratorFunction(26, "GeneratorFunction"),
    /** The V8 GeneratorObject type. */
    GeneratorObject(27, "GeneratorObject"),
    /** The V8 Promise type. */
    Promise(28, "Promise"),
    /** The V8 Map type. */
    Map(29, "Map"),
    /** The V8 Set type. */
    Set(30, "Set"),
    /** The V8 MapIterator type. */
    MapIterator(31, "MapIterator"),
    /** The V8 SetIterator type. */
    SetIterator(32, "SetIterator"),
    /** The V8 WeakMap type. */
    WeakMap(33, "WeakMap"),
    /** The V8 WeakSet type. */
    WeakSet(34, "WeakSet"),
    /** The V8 ArrayBuffer type. */
    ArrayBuffer(35, "ArrayBuffer"),
    /** The V8 ArrayBufferView type. */
    ArrayBufferView(36, "ArrayBufferView"),
    /** The V8 TypedArray type. */
    TypedArray(37, "TypedArray"),
    /** The V8 Uint8Array type. */
    Uint8Array(38, "Uint8Array"),
    /** The V8 Uint8ClampedArray type. */
    Uint8ClampedArray(39, "Uint8ClampedArray"),
    /** The V8 Int8Array type. */
    Int8Array(40, "Int8Array"),
    /** The V8 Uint16Array type. */
    Uint16Array(41, "Uint16Array"),
    /** The V8 Int16Array type. */
    Int16Array(42, "Int16Array"),
    /** The V8 Uint32Array type. */
    Uint32Array(43, "Uint32Array"),
    /** The V8 Int32Array type. */
    Int32Array(44, "Int32Array"),
    /** The V8 Float16Array type. */
    Float16Array(45, "Float16Array"),
    /** The V8 Float32Array type. */
    Float32Array(46, "Float32Array"),
    /** The V8 Float64Array type. */
    Float64Array(47, "Float64Array"),
    /** The V8 BigInt64Array type. */
    BigInt64Array(48, "BigInt64Array"),
    /** The V8 BigUint64Array type. */
    BigUint64Array(49, "BigUint64Array"),
    /** The V8 DataView type. */
    DataView(50, "DataView"),
    /** The V8 SharedArrayBuffer type. */
    SharedArrayBuffer(51, "SharedArrayBuffer"),
    /** The V8 Proxy type. */
    Proxy(52, "Proxy"),
    /** The V8 WasmModuleObject type. */
    WasmModuleObject(53, "WasmModuleObject"),
    /** The V8 ModuleNamespaceObject type. */
    ModuleNamespaceObject(54, "ModuleNamespaceObject");

    /** The total number of internal types. */
    private static final int LENGTH = values().length;
    /** The array of internal types indexed by ID. */
    private static final V8ValueInternalType[] TYPES = new V8ValueInternalType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    /** The type ID. */
    private final int id;
    /** The type name. */
    private final String name;

    V8ValueInternalType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse V8 value internal type.
     *
     * @param id the id
     * @return the V8 value internal type
     * @since 0.9.13
     */
    public static V8ValueInternalType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Undefined;
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 0.9.13
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 0.9.13
     */
    public String getName() {
        return name;
    }
}
