﻿/*
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

#include <chrono>
#include <thread>
#include "javet_callbacks.h"
#include "javet_converter.h"
#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_v8_internal.h"
#include "javet_v8_runtime.h"

namespace Javet {
#ifdef ENABLE_NODE
    static std::mutex mutexForNodeResetEnvrironment;
    static auto oneMillisecond = std::chrono::milliseconds(1);
#endif

    void Initialize(JNIEnv* jniEnv) {
#ifdef ENABLE_NODE
        jclassRuntimeOptions = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/options/NodeRuntimeOptions"));
        jmethodNodeRuntimeOptionsGetConsoleArguments = jniEnv->GetMethodID(jclassRuntimeOptions, "getConsoleArguments", "()[Ljava/lang/String;");
        bool isFrozen = false;
#else
        jclassRuntimeOptions = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/options/V8RuntimeOptions"));
        jmethodV8RuntimeOptionsGetGlobalName = jniEnv->GetMethodID(jclassRuntimeOptions, "getGlobalName", "()Ljava/lang/String;");
        bool isFrozen = V8InternalFlagList::IsFrozen(); // Since V8 v10.5
#endif
        // Set V8 flags
        if (!isFrozen) {
            jclass jclassV8Flags = jniEnv->FindClass("com/caoccao/javet/interop/options/V8Flags");
            jmethodID jmethodIDV8FlagsToString = jniEnv->GetMethodID(jclassV8Flags, "toString", "()Ljava/lang/String;");
            jmethodID jmethodIDV8FlagsSeal = jniEnv->GetMethodID(jclassV8Flags, "seal", "()Lcom/caoccao/javet/interop/options/V8Flags;");
            jfieldID jfieldIDRuntimeOptionsV8Flags = jniEnv->GetStaticFieldID(jclassRuntimeOptions, "V8_FLAGS", "Lcom/caoccao/javet/interop/options/V8Flags;");
            jobject mV8Flags = jniEnv->GetStaticObjectField(jclassRuntimeOptions, jfieldIDRuntimeOptionsV8Flags);
            jstring mV8FlagsString = (jstring)jniEnv->CallObjectMethod(mV8Flags, jmethodIDV8FlagsToString);
            jniEnv->DeleteLocalRef(jniEnv->CallObjectMethod(mV8Flags, jmethodIDV8FlagsSeal));
            char const* utfChars = jniEnv->GetStringUTFChars(mV8FlagsString, nullptr);
            v8::V8::SetFlagsFromString(utfChars, jniEnv->GetStringUTFLength(mV8FlagsString));
            jniEnv->ReleaseStringUTFChars(mV8FlagsString, utfChars);
            DELETE_LOCAL_REF(jniEnv, mV8FlagsString);
            DELETE_LOCAL_REF(jniEnv, mV8Flags);
            jniEnv->DeleteLocalRef(jclassV8Flags);
        }
    }

    void GlobalAccessorGetterCallback(
        V8LocalName propertyName,
        const v8::PropertyCallbackInfo<v8::Value>& args) {
        args.GetReturnValue().Set(args.GetIsolate()->GetCurrentContext()->Global());
    }

#ifdef ENABLE_NODE
    V8Runtime::V8Runtime(node::MultiIsolatePlatform* v8PlatformPointer, std::shared_ptr<node::ArrayBufferAllocator> nodeArrayBufferAllocator)
        : nodeEnvironment(nullptr, node::FreeEnvironment), nodeIsolateData(nullptr, node::FreeIsolateData), v8Locker(nullptr), uvLoop() {
        purgeEventLoopBeforeClose = false;
        this->nodeArrayBufferAllocator = nodeArrayBufferAllocator;
#else
    V8Runtime::V8Runtime(V8Platform * v8PlatformPointer)
        : v8Locker(nullptr) {
#endif
        externalV8Runtime = nullptr;
        externalException = nullptr;
        v8Isolate = nullptr;
        this->v8PlatformPointer = v8PlatformPointer;
    }

    bool V8Runtime::Await(const Javet::Enums::V8AwaitMode::V8AwaitMode awaitMode) {
        bool hasMoreTasks = false;
#ifdef ENABLE_NODE
        do {
            {
                // Reduce the locking granularity so that Node.js can respond to requests from other threads.
                auto v8Locker = GetUniqueV8Locker();
                auto v8IsolateScope = GetV8IsolateScope();
                V8HandleScope v8HandleScope(v8Isolate);
                auto v8Context = GetV8LocalContext();
                auto v8ContextScope = GetV8ContextScope(v8Context);
                uv_run(&uvLoop, UV_RUN_NOWAIT);
                // DrainTasks is thread-safe.
                v8PlatformPointer->DrainTasks(v8Isolate);
            }
            hasMoreTasks = uv_loop_alive(&uvLoop);
            if (hasMoreTasks) {
                // Sleep a while to give CPU cycles to other threads.
                std::this_thread::sleep_for(oneMillisecond);
            }
            else {
                auto v8Locker = GetUniqueV8Locker();
                auto v8IsolateScope = GetV8IsolateScope();
                V8HandleScope v8HandleScope(v8Isolate);
                auto v8Context = GetV8LocalContext();
                auto v8ContextScope = GetV8ContextScope(v8Context);
                // node::EmitProcessBeforeExit is thread-safe.
                node::EmitProcessBeforeExit(nodeEnvironment.get());
                hasMoreTasks = uv_loop_alive(&uvLoop);
            }
        } while (awaitMode == Javet::Enums::V8AwaitMode::RunTillNoMoreTasks && hasMoreTasks);
#else
        // It has to be v8::platform::MessageLoopBehavior::kDoNotWait, otherwise it blockes;
        v8::platform::PumpMessageLoop(v8PlatformPointer, v8Isolate);
#endif
        return hasMoreTasks;
    }

    void V8Runtime::CloseV8Context() {
        auto internalV8Locker = GetSharedV8Locker();
        auto v8IsolateScope = GetV8IsolateScope();
        V8HandleScope v8HandleScope(v8Isolate);
        auto v8LocalContext = GetV8LocalContext();
        Unregister(v8LocalContext);
        v8GlobalObject.Reset();
        v8::SealHandleScope v8SealHandleScope(v8Isolate);
#ifdef ENABLE_NODE
        if (!purgeEventLoopBeforeClose) {
            auto v8ContextScope = GetV8ContextScope(v8LocalContext);
            if (!nodeEnvironment->is_stopping()) {
                nodeEnvironment->set_trace_sync_io(nodeEnvironment->options()->trace_sync_io);
                bool hasMoreTasks;
                nodeEnvironment->performance_state()->Mark(node::performance::NODE_PERFORMANCE_MILESTONE_LOOP_START);
                do {
                    if (nodeEnvironment->is_stopping()) { break; }
                    uv_run(&uvLoop, UV_RUN_DEFAULT);
                    if (nodeEnvironment->is_stopping()) { break; }
                    // DrainTasks is thread-safe.
                    v8PlatformPointer->DrainTasks(v8Isolate);
                    hasMoreTasks = uv_loop_alive(&uvLoop);
                    if (!hasMoreTasks && !nodeEnvironment->is_stopping()) {
                        // node::EmitProcessBeforeExit is thread-safe.
                        if (node::EmitProcessBeforeExit(nodeEnvironment.get()).IsNothing()) { break; }
                        // Do not call { V8HandleScope innerHandleScope(v8Isolate); if (nodeEnvironment->RunSnapshotSerializeCallback().IsEmpty()) { break; } }
                        hasMoreTasks = uv_loop_alive(&uvLoop);
                    }
                } while (hasMoreTasks && !nodeEnvironment->is_stopping());
                nodeEnvironment->performance_state()->Mark(node::performance::NODE_PERFORMANCE_MILESTONE_LOOP_EXIT);
            }
        }
        int errorCode = 0;
        if (!nodeEnvironment->is_stopping()) {
            nodeEnvironment->set_trace_sync_io(false);
            // Do not call nodeEnvironment->set_snapshot_serialize_callback(V8LocalFunction());
            // Do not call nodeEnvironment->PrintInfoForSnapshotIfDebug();
            // Do not call nodeEnvironment->ForEachRealm([](node::Realm* realm) { realm->VerifyNoStrongBaseObjects(); });
            // node::EmitExit is thread-safe.
            errorCode = node::EmitProcessExit(nodeEnvironment.get()).FromMaybe(1);
        }
        if (errorCode != 0) {
            LOG_ERROR("node::EmitExit() returns " << errorCode << ".");
        }
        else {
            // node::Stop is thread-safe.
            errorCode = node::Stop(nodeEnvironment.get());
            if (errorCode != 0) {
                LOG_ERROR("node::Stop() returns " << errorCode << ".");
            }
        }
#endif
        v8PersistentContext.Reset();
    }

    void V8Runtime::CloseV8Isolate() {
        if (v8Inspector) {
            auto internalV8Locker = GetSharedV8Locker();
            v8Inspector.reset();
        }
        v8GlobalObject.Reset();
        v8PersistentContext.Reset();
#ifdef ENABLE_NODE
        if (v8Isolate != nullptr && nodeEnvironment.get() != nullptr) {
            auto internalV8Locker = GetSharedV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Isolate);
            // node::FreeEnvironment is not thread-safe.
            std::lock_guard<std::mutex> lock(mutexForNodeResetEnvrironment);
            nodeEnvironment.reset();
        }
        // node::FreeIsolateData is thread-safe.
        nodeIsolateData.reset();
#endif
        v8Locker.reset();
        // Isolate must be the last one to be disposed.
        if (v8Isolate != nullptr) {
#ifdef ENABLE_NODE
            bool isIsolateFinished = false;
            // AddIsolateFinishedCallback is thread-safe.
            v8PlatformPointer->AddIsolateFinishedCallback(v8Isolate, [](void* data) {
                *static_cast<bool*>(data) = true;
                }, &isIsolateFinished);
            // UnregisterIsolate is thread-safe.
            v8PlatformPointer->UnregisterIsolate(v8Isolate);
#endif
            v8Isolate->Dispose();
#ifdef ENABLE_NODE
            while (!isIsolateFinished) {
                uv_run(&uvLoop, UV_RUN_ONCE);
            }
            int errorCode = uv_loop_close(&uvLoop);
            if (errorCode != 0) {
                LOG_ERROR("Failed to close uv loop. Reason: " << uv_err_name(errorCode));
            }
#endif
            v8Isolate = nullptr;
        }
    }

    void V8Runtime::CreateV8Context(JNIEnv * jniEnv, const jobject & mRuntimeOptions) {
        auto internalV8Locker = GetSharedV8Locker();
        auto v8IsolateScope = GetV8IsolateScope();
        V8HandleScope v8HandleScope(v8Isolate);
#ifdef ENABLE_NODE
        // node::NewContext is thread-safe.
        V8LocalContext v8LocalContext = node::NewContext(v8Isolate);
        auto v8ContextScope = GetV8ContextScope(v8LocalContext);
        // Create and load the environment only once per isolate.
        if (nodeEnvironment.get() == nullptr) {
            std::vector<std::string> args{ DEFAULT_SCRIPT_NAME };
            std::vector<std::string> execArgs;
            if (mRuntimeOptions != nullptr) {
                jobjectArray mConsoleArguments = (jobjectArray)jniEnv->CallObjectMethod(mRuntimeOptions, jmethodNodeRuntimeOptionsGetConsoleArguments);
                if (mConsoleArguments != nullptr) {
                    int consoleArgumentCount = jniEnv->GetArrayLength(mConsoleArguments);
                    LOG_DEBUG("Node.js console argument count is " << consoleArgumentCount);
                    for (int i = 0; i < consoleArgumentCount; ++i) {
                        jstring mConsoleArgument = (jstring)jniEnv->GetObjectArrayElement(mConsoleArguments, i);
                        auto consoleArgumentPointer = Javet::Converter::ToStdString(jniEnv, mConsoleArgument);
                        auto umConsoleArgument = *consoleArgumentPointer.get();
                        LOG_DEBUG("    " << i << ": " << umConsoleArgument);
                        if (umConsoleArgument == "-v" || umConsoleArgument == "--version") {
                            LOG_DIRECT(NODE_VERSION);
                        }
                        args.push_back(umConsoleArgument);
                    }
                }
            }
            // node::CreateEnvironment is not thread-safe.
            std::lock_guard<std::mutex> lock(mutexForNodeResetEnvrironment);
            nodeEnvironment.reset(node::CreateEnvironment(nodeIsolateData.get(), v8LocalContext, args, execArgs));
            // node::LoadEnvironment is thread-safe.
            auto v8MaybeLocalValue = node::LoadEnvironment(
                nodeEnvironment.get(),
                "const publicRequire = require('module').createRequire(process.cwd() + '/');"
                "globalThis.require = publicRequire;"
            );
        }
#else
        auto v8ObjectTemplate = v8::ObjectTemplate::New(v8Isolate);
        if (mRuntimeOptions != nullptr) {
            jstring mGlobalName = (jstring)jniEnv->CallObjectMethod(mRuntimeOptions, jmethodV8RuntimeOptionsGetGlobalName);
            if (mGlobalName != nullptr) {
                auto umGlobalName = Javet::Converter::ToV8String(jniEnv, v8::Context::New(v8Isolate), mGlobalName);
                v8ObjectTemplate->SetAccessor(umGlobalName, GlobalAccessorGetterCallback);
            }
        }
        auto v8LocalContext = v8::Context::New(v8Isolate, nullptr, v8ObjectTemplate);
#endif
        Register(v8LocalContext);
        v8PersistentContext.Reset(v8Isolate, v8LocalContext);
        v8GlobalObject.Reset(
            v8Isolate, v8LocalContext->Global()->GetPrototype()->ToObject(v8LocalContext).ToLocalChecked());
    }

    void V8Runtime::CreateV8Isolate() {
#ifdef ENABLE_NODE
        int errorCode = uv_loop_init(&uvLoop);
        if (errorCode != 0) {
            LOG_ERROR("Failed to init uv loop. Reason: " << uv_err_name(errorCode));
        }
        // node::NewIsolate is thread-safe.
        v8Isolate = node::NewIsolate(nodeArrayBufferAllocator, &uvLoop, v8PlatformPointer);
        {
            auto internalV8Locker = GetUniqueV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Isolate);
            // node::CreateIsolateData is thread-safe.
            nodeIsolateData.reset(node::CreateIsolateData(v8Isolate, &uvLoop, v8PlatformPointer, nodeArrayBufferAllocator.get()));
        }
        v8Isolate->SetModifyCodeGenerationFromStringsCallback(nullptr);
#else
        v8::Isolate::CreateParams createParams;
        createParams.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();
        v8Isolate = v8::Isolate::New(createParams);
        v8Isolate->SetPromiseRejectCallback(Javet::Callback::JavetPromiseRejectCallback);
#endif
    }

    inline void V8Runtime::Register(const V8LocalContext & v8Context) {
        v8Context->SetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME, v8::BigInt::New(v8Isolate, TO_NATIVE_INT_64(this)));
    }

    jobject V8Runtime::SafeToExternalV8Value(JNIEnv * jniEnv, const V8LocalContext & v8Context, const V8InternalObject & v8InternalObject) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        jobject externalV8Value = Javet::Converter::ToExternalV8Value(jniEnv, this, v8Context, v8InternalObject);
        if (v8TryCatch.HasCaught()) {
            DELETE_LOCAL_REF(jniEnv, externalV8Value);
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, this, v8Context, v8TryCatch);
        }
        return externalV8Value;
    }

    jobject V8Runtime::SafeToExternalV8Value(JNIEnv * jniEnv, const V8LocalContext & v8Context, const V8LocalValue & v8Value) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        jobject externalV8Value = Javet::Converter::ToExternalV8Value(jniEnv, this, v8Context, v8Value);
        if (v8TryCatch.HasCaught()) {
            DELETE_LOCAL_REF(jniEnv, externalV8Value);
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, this, v8Context, v8TryCatch);
        }
        return externalV8Value;
    }

    inline void V8Runtime::Unregister(const V8LocalContext & v8Context) {
        v8Context->SetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME, v8::BigInt::New(v8Isolate, 0));
    }

    V8Runtime::~V8Runtime() {
        CloseV8Context();
        CloseV8Isolate();
    }
}

