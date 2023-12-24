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

#include <mutex>
#include "javet_enums.h"
#include "javet_logging.h"
#include "javet_native.h"

namespace Javet {
    class V8Runtime;
    class V8Scope;

    namespace Inspector {
        class JavetInspector;
    }

    void Initialize(JNIEnv* jniEnv) noexcept;

    class V8Runtime {
    public:
#ifdef ENABLE_NODE
        node::MultiIsolatePlatform* v8PlatformPointer;
        bool purgeEventLoopBeforeClose;
#else
        V8Platform* v8PlatformPointer;
#endif
        v8::Isolate* v8Isolate;
        jobject externalV8Runtime;
        jthrowable externalException;
        V8PersistentObject v8GlobalObject;
        std::unique_ptr<Javet::Inspector::JavetInspector> v8Inspector;

#ifdef ENABLE_NODE
        V8Runtime(
            node::MultiIsolatePlatform* v8PlatformPointer,
            std::shared_ptr<node::ArrayBufferAllocator> nodeArrayBufferAllocator) noexcept;
#else
        V8Runtime(
            V8Platform* v8PlatformPointer,
            std::shared_ptr<V8ArrayBufferAllocator> v8ArrayBufferAllocator) noexcept;
#endif

        bool Await(const Javet::Enums::V8AwaitMode::V8AwaitMode awaitMode) noexcept;

        inline bool ClearExternalException(JNIEnv* jniEnv) noexcept {
            if (HasExternalException()) {
                jniEnv->DeleteGlobalRef(externalException);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                externalException = nullptr;
                return true;
            }
            return false;
        }

        inline bool ClearExternalV8Runtime(JNIEnv* jniEnv) noexcept {
            if (HasExternalV8Runtime()) {
                jniEnv->DeleteGlobalRef(externalV8Runtime);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                externalV8Runtime = nullptr;
                return true;
            }
            return false;
        }

        void CloseV8Context() noexcept;
        void CloseV8Isolate() noexcept;

        jbyteArray CreateSnapshot(JNIEnv* jniEnv) noexcept;

        void CreateV8Context(JNIEnv* jniEnv, const jobject mRuntimeOptions) noexcept;
        void CreateV8Isolate(JNIEnv* jniEnv, const jobject mRuntimeOptions) noexcept;

        static inline V8Runtime* FromHandle(jlong handle) noexcept {
            return reinterpret_cast<V8Runtime*>(handle);
        }

        static inline V8Runtime* FromV8Context(const V8LocalContext& v8Context) noexcept {
            auto v8RuntimePointer = v8Context->GetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME)
                ->ToBigInt(v8Context).ToLocalChecked()->Int64Value();
            return reinterpret_cast<V8Runtime*>(v8RuntimePointer);
        }

        /*
         * Shared V8 locker is for implicit mode.
         * Javet manages the lock automatically.
         */
        inline auto GetSharedV8Locker() const noexcept {
            return v8Locker ? v8Locker : std::make_shared<v8::Locker>(v8Isolate);
        }

        /*
         * Unique V8 locker is for explicit mode.
         * Application manages the lock.
         */
        inline auto GetUniqueV8Locker() const noexcept {
            return std::make_unique<v8::Locker>(v8Isolate);
        }

        inline auto GetV8ContextScope(const V8LocalContext& v8LocalContext) const noexcept {
            return std::make_unique<V8ContextScope>(v8LocalContext);
        }

        inline V8LocalContext GetV8LocalContext() const noexcept {
            return v8PersistentContext.Get(v8Isolate);
        }

        inline auto GetV8IsolateScope() const noexcept {
            return std::make_unique<V8IsolateScope>(v8Isolate);
        }

        inline bool HasExternalException() const noexcept {
            return externalException != nullptr;
        }

        inline bool HasExternalV8Runtime() const noexcept {
            return externalV8Runtime != nullptr;
        }

        inline bool IsLocked() const noexcept {
            return (bool)v8Locker;
        }

        inline void Lock() noexcept {
            v8Locker.reset(new v8::Locker(v8Isolate));
        }

        inline void Register(const V8LocalContext& v8Context) noexcept {
            v8Context->SetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME, v8::BigInt::New(v8Isolate, TO_NATIVE_INT_64(this)));
        }

        jobject SafeToExternalV8Value(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
#ifdef ENABLE_NODE
            const V8InternalObject& v8InternalObject) noexcept;
#else
            const v8::internal::Tagged<V8InternalObject>& v8InternalObject) noexcept;
#endif

        jobject SafeToExternalV8Value(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const V8LocalValue& v8Value) noexcept;

        inline void Unlock() noexcept {
            v8Locker.reset();
        }

        inline void Unregister(const V8LocalContext& v8Context) noexcept {
            v8Context->SetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME, v8::BigInt::New(v8Isolate, 0));
        }

        virtual ~V8Runtime();

    private:
        std::unique_ptr<v8::SnapshotCreator> v8SnapshotCreator;
        std::unique_ptr<v8::StartupData, std::function<void(v8::StartupData*)>> v8StartupData;
        std::shared_ptr<v8::Locker> v8Locker;
        V8PersistentContext v8PersistentContext;
#ifdef ENABLE_NODE
        // The following Node objects must be live as long as V8 context lives.
        std::shared_ptr<node::ArrayBufferAllocator> nodeArrayBufferAllocator;
        std::unique_ptr<node::Environment, decltype(&node::FreeEnvironment)> nodeEnvironment;
        std::unique_ptr<node::IsolateData, decltype(&node::FreeIsolateData)> nodeIsolateData;
        uv_loop_t uvLoop;
#else
        std::shared_ptr<V8ArrayBufferAllocator> v8ArrayBufferAllocator;
#endif
    };
}

