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

#include <chrono>
#include <thread>
#include "javet_callbacks.h"
#include "javet_converter.h"
#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_v8_internal.h"
#include "javet_v8_runtime.h"

namespace Javet {
    jclass jclassRuntimeOptions;
    jmethodID jmethodRuntimeOptionsIsCreateSnapshotEnabled;
    jmethodID jmethodRuntimeOptionsGetSnapshotBlob;
#ifdef ENABLE_NODE
    jmethodID jmethodNodeRuntimeOptionsGetConsoleArguments;
    std::mutex mutexForNodeResetEnvrironment;
    auto oneMillisecond = std::chrono::milliseconds(1);
#else
    jmethodID jmethodV8RuntimeOptionsGetGlobalName;
#endif

    void Initialize(JNIEnv* jniEnv) noexcept {
#ifdef ENABLE_NODE
        jclassRuntimeOptions = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/options/NodeRuntimeOptions");
        jmethodNodeRuntimeOptionsGetConsoleArguments = jniEnv->GetMethodID(jclassRuntimeOptions, "getConsoleArguments", "()[Ljava/lang/String;");
#else
        jclassRuntimeOptions = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/options/V8RuntimeOptions");
        jmethodV8RuntimeOptionsGetGlobalName = jniEnv->GetMethodID(jclassRuntimeOptions, "getGlobalName", "()Ljava/lang/String;");
#endif
        jmethodRuntimeOptionsIsCreateSnapshotEnabled = jniEnv->GetMethodID(jclassRuntimeOptions, "isCreateSnapshotEnabled", "()Z");
        jmethodRuntimeOptionsGetSnapshotBlob = jniEnv->GetMethodID(jclassRuntimeOptions, "getSnapshotBlob", "()[B");
        // Set V8 flags
        bool isFrozen = V8InternalFlagList::IsFrozen(); // Since V8 v10.5
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
        const v8::PropertyCallbackInfo<v8::Value>& args) noexcept {
        args.GetReturnValue().Set(args.GetIsolate()->GetCurrentContext()->Global());
    }

#ifdef ENABLE_NODE
    V8Runtime::V8Runtime(
        node::MultiIsolatePlatform* v8PlatformPointer,
        std::shared_ptr<node::ArrayBufferAllocator> nodeArrayBufferAllocator) noexcept
        : nodeEnvironment(nullptr, node::FreeEnvironment), nodeIsolateData(nullptr, node::FreeIsolateData), uvLoop(),
#else
    V8Runtime::V8Runtime(
        V8Platform* v8PlatformPointer,
        std::shared_ptr<V8ArrayBufferAllocator> v8ArrayBufferAllocator) noexcept
        :
#endif
        v8SnapshotCreator(nullptr), v8StartupData(nullptr, [](v8::StartupData* x) { if (x->raw_size > 0) { delete[] x->data; } }), v8Locker(nullptr) {
#ifdef ENABLE_NODE
        this->nodeArrayBufferAllocator = nodeArrayBufferAllocator;
#else
        this->v8ArrayBufferAllocator = v8ArrayBufferAllocator;
#endif
        externalV8Runtime = nullptr;
        externalException = nullptr;
        v8Isolate = nullptr;
        this->v8PlatformPointer = v8PlatformPointer;
    }

    bool V8Runtime::Await(const Javet::Enums::V8AwaitMode::V8AwaitMode awaitMode) noexcept {
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

    void V8Runtime::CloseV8Context() noexcept {
        v8Locker.reset();
        {
            auto internalV8Locker = GetUniqueV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Isolate);
            auto v8LocalContext = GetV8LocalContext();
            Unregister(v8LocalContext);
            v8GlobalObject.Reset();
        }
#ifdef ENABLE_NODE
        int errorCode = 0;
        {
            auto internalV8Locker = GetUniqueV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Isolate);
            auto v8LocalContext = GetV8LocalContext();
            auto v8ContextScope = GetV8ContextScope(v8LocalContext);
            errorCode = node::SpinEventLoop(nodeEnvironment.get()).FromMaybe(1);
        }
        v8GlobalContext.Reset();
        if (errorCode != 0) {
            LOG_ERROR("node::EmitProcessExit() returns " << errorCode << ".");
        }
        else {
            // node::Stop is thread-safe.
            errorCode = node::Stop(nodeEnvironment.get());
            if (errorCode != 0) {
                LOG_ERROR("node::Stop() returns " << errorCode << ".");
            }
            std::lock_guard<std::mutex> lock(mutexForNodeResetEnvrironment);
            auto internalV8Locker = GetUniqueV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            LOG_DEBUG("nodeEnvironment.reset() begin");
            nodeEnvironment.reset();
            LOG_DEBUG("nodeEnvironment.reset() end");
        }
#else
        v8GlobalContext.Reset();
#endif
    }

    void V8Runtime::CloseV8Isolate() noexcept {
        if (v8Inspector) {
            auto internalV8Locker = GetSharedV8Locker();
            v8Inspector.reset();
        }
        v8GlobalObject.Reset();
        v8GlobalContext.Reset();
        v8Locker.reset();
#ifdef ENABLE_NODE
        // node::FreeIsolateData is thread-safe.
        nodeIsolateData.reset();
#endif
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
            if (v8SnapshotCreator) {
                v8SnapshotCreator.reset();
            }
            else {
                v8Isolate->Dispose();
            }
            v8StartupData.reset();
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

    jbyteArray V8Runtime::CreateSnapshot(JNIEnv* jniEnv) noexcept {
        jbyteArray jbytes = nullptr;
        if (v8SnapshotCreator) {
            // Backup context and global object (Begin)
            auto v8LocalContext = GetV8LocalContext();
            v8GlobalContext.Reset();
            v8GlobalObject.Reset();
            // Backup context and global object (End)
#ifdef ENABLE_NODE
            nodeIsolateData->Serialize(v8SnapshotCreator.get());
            nodeEnvironment->Serialize(v8SnapshotCreator.get());
            v8SnapshotCreator->SetDefaultContext(v8LocalContext, { node::SerializeNodeContextInternalFields, nodeEnvironment.get() });
#else
            v8SnapshotCreator->SetDefaultContext(v8LocalContext);
#endif
            v8::StartupData newV8StartupData = v8SnapshotCreator->CreateBlob(v8::SnapshotCreator::FunctionCodeHandling::kKeep);
            if (newV8StartupData.IsValid()) {
                jbytes = jniEnv->NewByteArray(newV8StartupData.raw_size);
                jboolean isCopy;
                void* data = jniEnv->GetPrimitiveArrayCritical(jbytes, &isCopy);
                memcpy(data, newV8StartupData.data, newV8StartupData.raw_size);
                jniEnv->ReleasePrimitiveArrayCritical(jbytes, data, JNI_ABORT);
                delete[] newV8StartupData.data;
            }
            // Restore context and global object (Begin)
            v8GlobalContext.Reset(v8Isolate, v8LocalContext);
            v8GlobalObject.Reset(
                v8Isolate, v8LocalContext->Global()->GetPrototype()->ToObject(v8LocalContext).ToLocalChecked());
            // Restore context and global object (End)
        }
        return jbytes;
    }

    void V8Runtime::CreateV8Context(JNIEnv* jniEnv, const jobject mRuntimeOptions) noexcept {
        auto internalV8Locker = GetSharedV8Locker();
        auto v8IsolateScope = GetV8IsolateScope();
        V8HandleScope v8HandleScope(v8Isolate);
#ifdef ENABLE_NODE
        // node::NewContext is thread-safe.
        auto v8LocalContext = node::NewContext(v8Isolate);
        auto v8ContextScope = GetV8ContextScope(v8LocalContext);
        // Create and load the environment only once per isolate.
        if (!nodeEnvironment) {
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
            auto flags = static_cast<node::EnvironmentFlags::Flags>(
                node::EnvironmentFlags::kOwnsProcessState
                | node::EnvironmentFlags::kNoCreateInspector);
            nodeEnvironment.reset(node::CreateEnvironment(
                nodeIsolateData.get(),
                v8LocalContext,
                args,
                execArgs,
                flags));
            // node::LoadEnvironment is thread-safe.
            V8MaybeLocalValue v8MaybeLocalValue;
            v8MaybeLocalValue = node::LoadEnvironment(
                nodeEnvironment.get(),
                v8SnapshotCreator ? INIT_SCRIPT_WITH_SNAPSHOT : INIT_SCRIPT_WITHOUT_SNAPSHOT
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
        auto v8ContextScope = GetV8ContextScope(v8LocalContext);
#endif
        Register(v8LocalContext);
        v8GlobalContext.Reset(v8Isolate, v8LocalContext);
        v8GlobalObject.Reset(
            v8Isolate, v8LocalContext->Global()->GetPrototype()->ToObject(v8LocalContext).ToLocalChecked());
    }

    void V8Runtime::CreateV8Isolate(JNIEnv* jniEnv, const jobject mRuntimeOptions) noexcept {
        bool createSnapshotEnabled = false;
        jbyteArray snapshotBlob = nullptr;
        if (mRuntimeOptions != nullptr) {
            createSnapshotEnabled = jniEnv->CallBooleanMethod(mRuntimeOptions, jmethodRuntimeOptionsIsCreateSnapshotEnabled);
            snapshotBlob = (jbyteArray)jniEnv->CallObjectMethod(mRuntimeOptions, jmethodRuntimeOptionsGetSnapshotBlob);
            if (snapshotBlob) {
                jsize snapshotBlobSize = jniEnv->GetArrayLength(snapshotBlob);
                jboolean isCopy;
                jbyte* snapshotBlobElements = jniEnv->GetByteArrayElements(snapshotBlob, &isCopy);
                v8StartupData.reset(new v8::StartupData());
                v8StartupData->data = new char[snapshotBlobSize];
                v8StartupData->raw_size = snapshotBlobSize;
                memcpy((void*)v8StartupData->data, (void*)snapshotBlobElements, snapshotBlobSize);
                jniEnv->ReleaseByteArrayElements(snapshotBlob, snapshotBlobElements, JNI_ABORT);
            }
        }
#ifdef ENABLE_NODE
        int errorCode = uv_loop_init(&uvLoop);
        if (errorCode != 0) {
            LOG_ERROR("Failed to init uv loop. Reason: " << uv_err_name(errorCode));
        }
        if (createSnapshotEnabled) {
            const std::vector<intptr_t>& externalReferences = node::SnapshotBuilder::CollectExternalReferences();
            v8Isolate = v8::Isolate::Allocate();
            v8PlatformPointer->RegisterIsolate(v8Isolate, &uvLoop);
            v8SnapshotCreator.reset(new v8::SnapshotCreator(v8Isolate, externalReferences.data(), v8StartupData.get()));
            v8Isolate->SetCaptureStackTraceForUncaughtExceptions(true, 10, v8::StackTrace::StackTraceOptions::kDetailed);
            v8Isolate->SetMicrotasksPolicy(v8::MicrotasksPolicy::kExplicit);
        }
        else {
            // node::NewIsolate is thread-safe.
            v8Isolate = node::NewIsolate(nodeArrayBufferAllocator, &uvLoop, v8PlatformPointer);
        }
        {
            auto internalV8Locker = GetUniqueV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Isolate);
            // node::CreateIsolateData is thread-safe.
            nodeIsolateData.reset(node::CreateIsolateData(v8Isolate, &uvLoop, v8PlatformPointer, nodeArrayBufferAllocator.get()));
            // nodeIsolateData->set_is_building_snapshot(createSnapshotEnabled);
            node::crypto::InitCryptoOnce(v8Isolate);
        }
        v8Isolate->SetModifyCodeGenerationFromStringsCallback(nullptr);
#else
        if (createSnapshotEnabled) {
            v8Isolate = v8::Isolate::Allocate();
            v8SnapshotCreator.reset(new v8::SnapshotCreator(v8Isolate, nullptr, v8StartupData.get(), true));
        }
        else {
            v8::Isolate::CreateParams createParams;
            createParams.array_buffer_allocator = v8ArrayBufferAllocator.get();
            createParams.oom_error_callback = Javet::Callback::OOMErrorCallback;
            createParams.snapshot_blob = v8StartupData.get();
            v8Isolate = v8::Isolate::New(createParams);
        }
        v8Isolate->SetPromiseRejectCallback(Javet::Callback::JavetPromiseRejectCallback);
#endif
    }

    jobject V8Runtime::SafeToExternalV8Value(
        JNIEnv* jniEnv,
        const V8LocalContext& v8Context,
#ifdef ENABLE_NODE
        const V8InternalObject& v8InternalObject) noexcept {
#else
        const v8::internal::Tagged<V8InternalObject>& v8InternalObject) noexcept {
#endif
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        jobject externalV8Value = Javet::Converter::ToExternalV8Value(jniEnv, this, v8Context, v8InternalObject);
        if (v8TryCatch.HasCaught()) {
            DELETE_LOCAL_REF(jniEnv, externalV8Value);
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, this, v8Context, v8TryCatch);
        }
        return externalV8Value;
    }

    jobject V8Runtime::SafeToExternalV8Value(
        JNIEnv * jniEnv,
        const V8LocalContext & v8Context,
        const V8LocalValue & v8Value) noexcept {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        jobject externalV8Value = Javet::Converter::ToExternalV8Value(jniEnv, this, v8Context, v8Value);
        if (v8TryCatch.HasCaught()) {
            DELETE_LOCAL_REF(jniEnv, externalV8Value);
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, this, v8Context, v8TryCatch);
        }
        return externalV8Value;
    }

    V8Runtime::~V8Runtime() {
        CloseV8Context();
        CloseV8Isolate();
    }
}

