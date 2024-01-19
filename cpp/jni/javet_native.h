/*
 *   Copyright (c) 2021-2024. caoccao.com Sam Cao
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

#include <jni.h>
#include "javet_node.h"
#include "javet_v8.h"

#ifdef __ANDROID__
#define SUPPORTED_JNI_VERSION JNI_VERSION_1_6
#else
#define SUPPORTED_JNI_VERSION JNI_VERSION_1_8
#endif

#define FETCH_JNI_ENV(javaVMPointer) \
    JNIEnv* jniEnv; \
    javaVMPointer->GetEnv((void**)&jniEnv, SUPPORTED_JNI_VERSION); \
    javaVMPointer->AttachCurrentThread((void**)&jniEnv, nullptr);

#define DELETE_LOCAL_REF(jniEnv, localRef) if (localRef != nullptr) { jniEnv->DeleteLocalRef(localRef); }

#define RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context);

#define RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8DataHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(v8DataHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalData = v8PersistentDataPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    auto v8PersistentModulePointer = TO_V8_PERSISTENT_MODULE_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalModule = v8PersistentModulePointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    auto v8PersistentScriptPointer = TO_V8_PERSISTENT_SCRIPT_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalScript = v8PersistentScriptPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    auto v8PersistentValuePointer = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalValue = v8PersistentValuePointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE_WITH_UNIQUE_LOCKER(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    auto v8PersistentValuePointer = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetUniqueV8Locker(); \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalValue = v8PersistentValuePointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    auto v8PersistentValuePointer1 = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle1); \
    auto v8PersistentValuePointer2 = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle2); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalValue1 = v8PersistentValuePointer1->Get(v8Context->GetIsolate()); \
    auto v8LocalValue2 = v8PersistentValuePointer2->Get(v8Context->GetIsolate());

extern JavaVM* GlobalJavaVM;

namespace Javet {

#ifdef ENABLE_NODE
    namespace NodeNative {
        extern std::shared_ptr<node::ArrayBufferAllocator> GlobalNodeArrayBufferAllocator;

        void Dispose(JNIEnv* jniEnv) noexcept;
        void Initialize(JNIEnv* jniEnv) noexcept;
    }
#endif

    namespace V8Native {
#ifdef ENABLE_NODE
        extern std::unique_ptr<node::MultiIsolatePlatform> GlobalV8Platform;
#else
        extern std::unique_ptr<V8Platform> GlobalV8Platform;
        extern std::shared_ptr<V8ArrayBufferAllocator> GlobalV8ArrayBufferAllocator;
#endif

        void Dispose(JNIEnv* jniEnv) noexcept;
        void Initialize(JNIEnv* jniEnv) noexcept;
    }
}
