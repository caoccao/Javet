/*
 *   Copyright (c) 2021-2023 caoccao.com Sam Cao
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
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

#include <libplatform/libplatform.h>
#include <v8.h>
#include <v8-inspector.h>

#ifdef _WIN32
#pragma comment(lib, "DbgHelp.lib")
#pragma comment(lib, "Iphlpapi.lib")
#pragma comment(lib, "Psapi.lib")
#pragma comment(lib, "Userenv.lib")
#pragma comment(lib, "Winmm.lib")
#pragma comment(lib, "Ws2_32.lib")
#endif

// Scope

using V8ContextScope = v8::Context::Scope;
using V8HandleScope = v8::HandleScope;
using V8IsolateScope = v8::Isolate::Scope;

// Local

using V8LocalArray = v8::Local<v8::Array>;
using V8LocalBigInt = v8::Local<v8::BigInt>;
using V8LocalBoolean = v8::Local<v8::Boolean>;
using V8LocalContext = v8::Local<v8::Context>;
using V8LocalData = v8::Local<v8::Data>;
using V8LocalFixedArray = v8::Local<v8::FixedArray>;
using V8LocalFunction = v8::Local<v8::Function>;
using V8LocalInteger = v8::Local<v8::Integer>;
using V8LocalMap = v8::Local<v8::Map>;
using V8LocalModule = v8::Local<v8::Module>;
using V8LocalName = v8::Local<v8::Name>;
using V8LocalNumber = v8::Local<v8::Number>;
using V8LocalObject = v8::Local<v8::Object>;
using V8LocalPrimitive = v8::Local<v8::Primitive>;
using V8LocalPrimitiveArray = v8::Local<v8::PrimitiveArray>;
using V8LocalPromise = v8::Local<v8::Promise>;
using V8LocalProxy = v8::Local<v8::Proxy>;
using V8LocalRegExp = v8::Local<v8::RegExp>;
using V8LocalScript = v8::Local<v8::Script>;
using V8LocalSet = v8::Local<v8::Set>;
using V8LocalString = v8::Local<v8::String>;
using V8LocalSymbol = v8::Local<v8::Symbol>;
using V8LocalSymbolObject = v8::Local<v8::SymbolObject>;
using V8LocalValue = v8::Local<v8::Value>;

// Maybe

using V8MaybeBool = v8::Maybe<bool>;

// Maybe Local

using V8MaybeLocalFunction = v8::MaybeLocal<v8::Function>;
using V8MaybeLocalModule = v8::MaybeLocal<v8::Module>;
using V8MaybeLocalPromise = v8::MaybeLocal<v8::Promise>;
using V8MaybeLocalPromiseResolver = v8::MaybeLocal<v8::Promise::Resolver>;
using V8MaybeLocalProxy = v8::MaybeLocal<v8::Proxy>;
using V8MaybeLocalString = v8::MaybeLocal<v8::String>;
using V8MaybeLocalValue = v8::MaybeLocal<v8::Value>;

// Persistent

using V8PersistentArray = v8::Persistent<v8::Array>;
using V8PersistentBigInt = v8::Persistent<v8::BigInt>;
using V8PersistentContext = v8::Persistent<v8::Context>;
using V8PersistentData = v8::Persistent<v8::Data>;
using V8PersistentFunction = v8::Persistent<v8::Function>;
using V8PersistentMap = v8::Persistent<v8::Map>;
using V8PersistentModule = v8::Persistent<v8::Module>;
using V8PersistentObject = v8::Persistent<v8::Object>;
using V8PersistentPromise = v8::Persistent<v8::Promise>;
using V8PersistentProxy = v8::Persistent<v8::Proxy>;
using V8PersistentRegExp = v8::Persistent<v8::RegExp>;
using V8PersistentScript = v8::Persistent<v8::Script>;
using V8PersistentSet = v8::Persistent<v8::Set>;
using V8PersistentSymbol = v8::Persistent<v8::Symbol>;
using V8PersistentSymbolObject = v8::Persistent<v8::SymbolObject>;
using V8PersistentValue = v8::Persistent<v8::Value>;

using V8Platform = v8::Platform;
using V8StringUtf8Value = v8::String::Utf8Value;
using V8StringValue = v8::String::Value;
using V8TryCatch = v8::TryCatch;
using V8ScriptCompilerCachedData = v8::ScriptCompiler::CachedData;
using V8ScriptCompilerCachedDataBufferPolicy = v8::ScriptCompiler::CachedData::BufferPolicy;
using V8ScriptCompilerSource = v8::ScriptCompiler::Source;

// To Java

#define TO_JAVA_LONG(handle) reinterpret_cast<jlong>(handle)
#define TO_JAVA_OBJECT(handle) reinterpret_cast<jobject>(handle)

// To Native

#define TO_NATIVE_INT_64(handle) reinterpret_cast<int64_t>(handle)

// To V8 Persistent

#define TO_V8_PERSISTENT_ARRAY_POINTER(handle) *reinterpret_cast<V8PersistentArray*>(handle)
#define TO_V8_PERSISTENT_CONTEXT_POINTER(handle) reinterpret_cast<V8PersistentContext*>(handle)
#define TO_V8_PERSISTENT_DATA_POINTER(handle) reinterpret_cast<V8PersistentData*>(handle)
#define TO_V8_PERSISTENT_FUNCTION_POINTER(handle) reinterpret_cast<V8PersistentFunction*>(handle)
#define TO_V8_PERSISTENT_MAP_POINTER(handle) *reinterpret_cast<V8PersistentMap*>(handle)
#define TO_V8_PERSISTENT_MODULE_POINTER(handle) reinterpret_cast<V8PersistentModule*>(handle)
#define TO_V8_PERSISTENT_OBJECT_POINTER(handle) *reinterpret_cast<V8PersistentObject*>(handle)
#define TO_V8_PERSISTENT_PROMISE_POINTER(handle) *reinterpret_cast<V8PersistentPromise*>(handle)
#define TO_V8_PERSISTENT_PROXY_POINTER(handle) *reinterpret_cast<V8PersistentProxy*>(handle)
#define TO_V8_PERSISTENT_REG_EXP_POINTER(handle) *reinterpret_cast<V8PersistentRegExp*>(handle)
#define TO_V8_PERSISTENT_SCRIPT_POINTER(handle) reinterpret_cast<V8PersistentScript*>(handle)
#define TO_V8_PERSISTENT_SET_POINTER(handle) *reinterpret_cast<V8PersistentSet*>(handle)
#define TO_V8_PERSISTENT_SYMBOL_POINTER(handle) *reinterpret_cast<V8PersistentSymbol*>(handle)
#define TO_V8_PERSISTENT_SYMBOL_OBJECT_POINTER(handle) *reinterpret_cast<V8PersistentSymbolObject*>(handle)
#define TO_V8_PERSISTENT_VALUE_POINTER(handle) reinterpret_cast<V8PersistentValue*>(handle)

