/*
 *   Copyright (c) 2021 caoccao.com Sam Cao
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
#include <mutex>
#include "javet_converter.h"
#include "javet_exceptions.h"
#include "javet_logging.h"
#include "javet_native.h"
#include "javet_node.h"
#include "javet_v8.h"

namespace Javet {
    namespace Inspector {
        class JavetInspector;
    }

    class V8Runtime;
    class V8Scope;

    static jclass jclassRuntimeOptions;
#ifdef ENABLE_NODE
    static jmethodID jmethodNodeRuntimeOptionsGetConsoleArguments;
#else
    static jmethodID jmethodV8RuntimeOptionsGetGlobalName;
#endif

    void Initialize(JNIEnv* jniEnv);

    class V8Runtime {
    public:
#ifdef ENABLE_NODE
        node::MultiIsolatePlatform* v8PlatformPointer;
#else
        V8Platform* v8PlatformPointer;
#endif
        v8::Isolate* v8Isolate;
        jobject externalV8Runtime;
        V8PersistentObject v8GlobalObject;
        std::unique_ptr<Javet::Inspector::JavetInspector> v8Inspector;

#ifdef ENABLE_NODE
        V8Runtime(node::MultiIsolatePlatform* v8PlatformPointer, std::shared_ptr<node::ArrayBufferAllocator> nodeArrayBufferAllocator);
#else
        V8Runtime(V8Platform* v8PlatformPointer);
#endif

        void Await();

        void CloseV8Context();
        void CloseV8Isolate();

        void CreateV8Context(JNIEnv* jniEnv, const jobject& mRuntimeOptions);
        void CreateV8Isolate();

        static inline V8Runtime* FromHandle(jlong handle) {
            return reinterpret_cast<V8Runtime*>(handle);
        }

        static inline V8Runtime* FromV8Context(const V8LocalContext& v8Context) {
            return reinterpret_cast<V8Runtime*>(v8Context->GetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME)->ToBigInt(v8Context).ToLocalChecked()->Int64Value());
        }

        /*
        * Shared V8 locker is for implicit mode.
        * Javet manages the lock automatically.
        */
        inline auto GetSharedV8Locker() {
            return v8Locker ? v8Locker : std::make_shared<v8::Locker>(v8Isolate);
        }

        /*
        * Unique V8 locker is for explicit mode.
        * Application manages the lock.
        */
        inline auto GetUniqueV8Locker() {
            return std::make_unique<v8::Locker>(v8Isolate);
        }

        inline auto GetV8ContextScope(const V8LocalContext& v8LocalContext) {
            return std::make_unique<V8ContextScope>(v8LocalContext);
        }

        inline V8LocalContext GetV8LocalContext() {
            return v8PersistentContext.Get(v8Isolate);
        }

        inline auto GetV8IsolateScope() {
            return std::make_unique<V8IsolateScope>(v8Isolate);
        }

        inline bool IsLocked() {
            return (bool)v8Locker;
        }

        inline void Lock() {
            v8Locker.reset(new v8::Locker(v8Isolate));
        }

        inline void Register(const V8LocalContext& v8Context) {
            v8Context->SetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME, v8::BigInt::New(v8Isolate, TO_NATIVE_INT_64(this)));
        }

        inline jobject SafeToExternalV8Value(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8LocalValue& v8Value) {
            try {
                return Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, v8Value);
            }
            catch (const std::exception& e) {
                LOG_ERROR(e.what());
                Javet::Exceptions::ThrowJavetConverterException(jniEnv, e.what());
            }
            return nullptr;
        }

        inline void Unlock() {
            v8Locker.reset();
        }

        inline void Unregister(const V8LocalContext& v8Context) {
            v8Context->SetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME, v8::BigInt::New(v8Isolate, 0));
        }

        virtual ~V8Runtime();

    private:
        std::shared_ptr<v8::Locker> v8Locker;
        V8PersistentContext v8PersistentContext;
#ifdef ENABLE_NODE
        // The following Node objects must be live as long as V8 context lives.
        std::shared_ptr<node::ArrayBufferAllocator> nodeArrayBufferAllocator;
        std::unique_ptr<node::Environment, decltype(&node::FreeEnvironment)> nodeEnvironment;
        std::unique_ptr<node::IsolateData, decltype(&node::FreeIsolateData)> nodeIsolateData;
        uv_loop_t uvLoop;
#endif
    };
}

