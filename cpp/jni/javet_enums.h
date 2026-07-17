/*
 *   Copyright (c) 2021-2026. caoccao.com Sam Cao
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License"),
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

#pragma once

#include <jni.h>

namespace Javet {
    namespace Enums {
        namespace NodeScriptMode {
            enum NodeScriptMode {
                String = 0,
                File = 1,
            };
        };

        namespace RawPointerType {
            enum RawPointerType {
                HeapStatisticsContext = 1,
                HeapSpaceStatisticsContext = 2,
                SharedMemoryStatisticsContext = 3,
                Invalid = 0,
            };
        }

        namespace V8AwaitMode {
            enum V8AwaitMode {
                RunNoWait = 2,
                RunOnce = 1,
                RunTillNoMoreTasks = 0,
            };
        };

        namespace V8ContextType {
            enum V8ContextType {
                Await = 0,
                Block = 1,
                Catch = 2,
                DebugEvaluate = 3,
                Declaration = 4,
                Eval = 5,
                Function = 6,
                Module = 7,
                Script = 8,
                With = 9,
            };
        };

        namespace V8ValueErrorType {
            enum V8ValueErrorType {
                Error = 0,
                RangeError = 1,
                ReferenceError = 2,
                SyntaxError = 3,
                TypeError = 4,
                WasmCompileError = 5,
                WasmLinkError = 6,
                WasmRuntimeError = 7,
            };
        };

        namespace V8ValueInternalType {
            enum V8ValueInternalType {
                Undefined = 0,
                Null = 1,
                NullOrUndefined = 2,
                True = 3,
                False = 4,
                Name = 5,
                String = 6,
                Symbol = 7,
                Function = 8,
                Array = 9,
                Object = 10,
                BigInt = 11,
                Boolean = 12,
                Number = 13,
                External = 14,
                Int32 = 15,
                Date = 16,
                ArgumentsObject = 17,
                BigIntObject = 18,
                BooleanObject = 19,
                NumberObject = 20,
                StringObject = 21,
                SymbolObject = 22,
                NativeError = 23,
                RegExp = 24,
                AsyncFunction = 25,
                GeneratorFunction = 26,
                GeneratorObject = 27,
                Promise = 28,
                Map = 29,
                Set = 30,
                MapIterator = 31,
                SetIterator = 32,
                WeakMap = 33,
                WeakSet = 34,
                ArrayBuffer = 35,
                ArrayBufferView = 36,
                TypedArray = 37,
                Uint8Array = 38,
                Uint8ClampedArray = 39,
                Int8Array = 40,
                Uint16Array = 41,
                Int16Array = 42,
                Uint32Array = 43,
                Int32Array = 44,
                Float16Array = 45,
                Float32Array = 46,
                Float64Array = 47,
                BigInt64Array = 48,
                BigUint64Array = 49,
                DataView = 50,
                SharedArrayBuffer = 51,
                Proxy = 52,
                WasmModuleObject = 53,
                ModuleNamespaceObject = 54,
            };
        };

        enum class V8ValueType : jint {
            Invalid = 0,
            Null = 1,
            Undefined = 2,
            Boolean = 3,
            Integer = 4,
            Double = 5,
            Long = 6,
            BigInteger = 7,
            String = 8,
            ZonedDateTime = 9,
            Object = 10,
            Error = 11,
            RegExp = 12,
            Promise = 13,
            Proxy = 14,
            Symbol = 15,
            SymbolObject = 16,
            Arguments = 17,
            Map = 18,
            Set = 19,
            Array = 20,
            Function = 21,
            Iterator = 22,
            WeakMap = 23,
            WeakSet = 24,
            Script = 29,
            Module = 30,
            Context = 31,
            BigIntObject = 32,
            BooleanObject = 33,
            IntegerObject = 34,
            DoubleObject = 35,
            StringObject = 36,
            DataView = 39,
            ArrayBuffer = 40,
            SharedArrayBuffer = 41,
            Int8Array = 42,
            Uint8Array = 43,
            Uint8ClampedArray = 44,
            Int16Array = 45,
            Uint16Array = 46,
            Int32Array = 47,
            Uint32Array = 48,
            Float16Array = 49,
            Float32Array = 50,
            Float64Array = 51,
            BigInt64Array = 52,
            BigUint64Array = 53,
        };

        namespace JSFunctionType {
            enum JSFunctionType {
                Native = 0,
                API = 1,
                UserDefined = 2,
                Unknown = 3,
            };
        };

        namespace JSScopeType {
            enum JSScopeType {
                Script = 0, // The top-level scope for a script or a top-level eval.
                ReplMode = 1, // The top-level scope for a repl-mode script.
                Class = 2, // The scope introduced by a class.
                Eval = 3, // The top-level scope for an eval source.
                Function = 4, // The top-level scope for a function.
                Module = 5, // The scope introduced by a module literal
                Catch = 6, // The scope introduced by catch.
                Block = 7, // The scope introduced by a new block.
                With = 8, // The scope introduced by with.
                ShadowRealm = 9, // Synthetic scope for ShadowRealm NativeContexts.
                Unknown = 10,
            };
        };
    }
}

constexpr auto IS_V8_ARRAY(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Array); }
constexpr auto IS_V8_ARRAY_BUFFER(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::ArrayBuffer); }
constexpr auto IS_V8_ARGUMENTS(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Arguments); }
constexpr auto IS_V8_BIG_INT_OBJECT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::BigIntObject); }
constexpr auto IS_V8_BOOLEAN_OBJECT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::BooleanObject); }
constexpr auto IS_V8_CONTEXT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Context); }
constexpr auto IS_V8_DOUBLE_OBJECT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::DoubleObject); }
constexpr auto IS_V8_FUNCTION(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Function); }
constexpr auto IS_V8_INTEGER_OBJECT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::IntegerObject); }
constexpr auto IS_V8_MAP(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Map); }
constexpr auto IS_V8_MODULE(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Module); }
constexpr auto IS_V8_OBJECT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Object); }
constexpr auto IS_V8_PROMISE(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Promise); }
constexpr auto IS_V8_PROXY(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Proxy); }
constexpr auto IS_V8_SCRIPT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Script); }
constexpr auto IS_V8_SET(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Set); }
constexpr auto IS_V8_STRING_OBJECT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::StringObject); }
constexpr auto IS_V8_SYMBOL(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::Symbol); }
constexpr auto IS_V8_SYMBOL_OBJECT(jint type) { return type == static_cast<jint>(Javet::Enums::V8ValueType::SymbolObject); }
