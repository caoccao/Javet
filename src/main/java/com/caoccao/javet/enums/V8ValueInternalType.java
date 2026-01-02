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
    Undefined(0, "Undefined"),
    Null(1, "Null"),
    NullOrUndefined(2, "NullOrUndefined"),
    True(3, "True"),
    False(4, "False"),
    Name(5, "Name"),
    String(6, "String"),
    Symbol(7, "Symbol"),
    Function(8, "Function"),
    Array(9, "Array"),
    Object(10, "Object"),
    BigInt(11, "BigInt"),
    Boolean(12, "Boolean"),
    Number(13, "Number"),
    External(14, "External"),
    Int32(15, "Int32"),
    Date(16, "Date"),
    ArgumentsObject(17, "ArgumentsObject"),
    BigIntObject(18, "BigIntObject"),
    BooleanObject(19, "BooleanObject"),
    NumberObject(20, "NumberObject"),
    StringObject(21, "StringObject"),
    SymbolObject(22, "SymbolObject"),
    NativeError(23, "NativeError"),
    RegExp(24, "RegExp"),
    AsyncFunction(25, "AsyncFunction"),
    GeneratorFunction(26, "GeneratorFunction"),
    GeneratorObject(27, "GeneratorObject"),
    Promise(28, "Promise"),
    Map(29, "Map"),
    Set(30, "Set"),
    MapIterator(31, "MapIterator"),
    SetIterator(32, "SetIterator"),
    WeakMap(33, "WeakMap"),
    WeakSet(34, "WeakSet"),
    ArrayBuffer(35, "ArrayBuffer"),
    ArrayBufferView(36, "ArrayBufferView"),
    TypedArray(37, "TypedArray"),
    Uint8Array(38, "Uint8Array"),
    Uint8ClampedArray(39, "Uint8ClampedArray"),
    Int8Array(40, "Int8Array"),
    Uint16Array(41, "Uint16Array"),
    Int16Array(42, "Int16Array"),
    Uint32Array(43, "Uint32Array"),
    Int32Array(44, "Int32Array"),
    Float16Array(45, "Float16Array"),
    Float32Array(46, "Float32Array"),
    Float64Array(47, "Float64Array"),
    BigInt64Array(48, "BigInt64Array"),
    BigUint64Array(49, "BigUint64Array"),
    DataView(50, "DataView"),
    SharedArrayBuffer(51, "SharedArrayBuffer"),
    Proxy(52, "Proxy"),
    WasmModuleObject(53, "WasmModuleObject"),
    ModuleNamespaceObject(54, "ModuleNamespaceObject");

    private static final int LENGTH = values().length;
    private static final V8ValueInternalType[] TYPES = new V8ValueInternalType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
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
