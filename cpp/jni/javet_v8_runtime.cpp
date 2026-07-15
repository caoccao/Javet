/*
 *   Copyright (c) 2021-2026. caoccao.com Sam Cao
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
#include <unordered_map>
#include "javet_callbacks.h"
#include "javet_converter.h"
#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_v8_internal.h"
#include "javet_v8_runtime.h"

namespace Javet {
    static thread_local std::unordered_map<V8Runtime*, size_t> ExternalExceptionScopeDepths;

    jclass jclassRuntimeOptions;
    jclass jclassV8Runtime;
    jmethodID jmethodRuntimeOptionsIsCreateSnapshotEnabled;
    jmethodID jmethodRuntimeOptionsGetSnapshotBlob;
#ifdef ENABLE_NODE
    jmethodID jmethodNodeRuntimeOptionsGetConsoleArguments;
    jmethodID jmethodNodeRuntimeOptionsIsBuiltInModuleResolution;
    jmethodID jmethodV8RuntimeGetRuntimeOptions;
    std::mutex mutexForNodeResetEnvrironment;
    auto oneMillisecond = std::chrono::milliseconds(1);
#else
    jmethodID jmethodV8RuntimeOptionsGetGlobalName;
#endif

    void Initialize(JNIEnv* jniEnv) noexcept {
#ifdef ENABLE_NODE
        jclassRuntimeOptions = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/options/NodeRuntimeOptions");
        jmethodNodeRuntimeOptionsGetConsoleArguments = jniEnv->GetMethodID(jclassRuntimeOptions, "getConsoleArguments", "()[Ljava/lang/String;");
        jmethodNodeRuntimeOptionsIsBuiltInModuleResolution = jniEnv->GetMethodID(jclassRuntimeOptions, "isBuiltInModuleResolution", "()Z");
        jclassV8Runtime = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/V8Runtime");
        jmethodV8RuntimeGetRuntimeOptions = jniEnv->GetMethodID(jclassV8Runtime, "getRuntimeOptions", "()Lcom/caoccao/javet/interop/options/RuntimeOptions;");
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

    ExternalExceptionScope::ExternalExceptionScope(
        JNIEnv* jniEnv,
        V8Runtime* v8Runtime) noexcept
        : jniEnv(jniEnv), v8Runtime(v8Runtime) {
        if (v8Runtime != nullptr) {
            ++ExternalExceptionScopeDepths[v8Runtime];
        }
    }

    ExternalExceptionScope::~ExternalExceptionScope() {
        if (v8Runtime == nullptr) {
            return;
        }
        auto iterator = ExternalExceptionScopeDepths.find(v8Runtime);
        if (iterator != ExternalExceptionScopeDepths.end()) {
            if (--iterator->second == 0) {
                ExternalExceptionScopeDepths.erase(iterator);
                if (jniEnv != nullptr) {
                    v8Runtime->ClearExternalException(jniEnv);
                }
            }
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
        : closeState(V8RuntimeState::Open), nodeEnvironment(nullptr, node::FreeEnvironment), nodeIsolateData(nullptr, node::FreeIsolateData), nodeStopping(false), uvLoop(), uvLoopInitialized(false),
#else
    V8Runtime::V8Runtime(
        V8Platform* v8PlatformPointer,
        std::shared_ptr<V8ArrayBufferAllocator> v8ArrayBufferAllocator) noexcept
        : closeState(V8RuntimeState::Open),
#endif
        v8SnapshotCreator(nullptr), v8StartupData(nullptr, [](v8::StartupData* x) {
            delete[] x->data;
            delete x;
        }), v8Locker(nullptr) {
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

#ifdef ENABLE_NODE
    bool V8Runtime::Await(const Javet::Enums::V8AwaitMode::V8AwaitMode awaitMode) noexcept {
        bool hasMoreTasks = false;
        using namespace Javet::Enums::V8AwaitMode;
        uv_run_mode uvRunMode;
        switch (awaitMode)
        {
        case RunOnce:
            uvRunMode = UV_RUN_ONCE;
            break;
        default:
            uvRunMode = UV_RUN_NOWAIT;
            break;
        }
        uv_loop_t* loop = nodeCommonSetup ? nodeCommonSetup->event_loop() : &uvLoop;
        node::Environment* env = nodeCommonSetup ? nodeCommonSetup->env() : nodeEnvironment.get();
        do {
            {
                // Reduce the locking granularity so that Node.js can respond to requests from other threads.
                auto v8Locker = GetUniqueV8Locker();
                auto v8IsolateScope = GetV8IsolateScope();
                V8HandleScope v8HandleScope(v8Isolate);
                auto v8Context = GetV8LocalContext();
                auto v8ContextScope = GetV8ContextScope(v8Context);
                uv_run(loop, uvRunMode);
                // DrainTasks is thread-safe.
                v8PlatformPointer->DrainTasks(v8Isolate);
            }
            hasMoreTasks = uv_loop_alive(loop);
            if (awaitMode == RunTillNoMoreTasks && hasMoreTasks) {
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
                node::EmitProcessBeforeExit(env);
                hasMoreTasks = uv_loop_alive(loop);
            }
        } while (awaitMode == RunTillNoMoreTasks && hasMoreTasks);
        return hasMoreTasks;
    }
#else
    bool V8Runtime::Await(const Javet::Enums::V8AwaitMode::V8AwaitMode awaitMode) noexcept {
        // It has to be v8::platform::MessageLoopBehavior::kDoNotWait, otherwise it blockes;
        v8::platform::PumpMessageLoop(v8PlatformPointer, v8Isolate);
        return false;
    }
#endif

    bool V8Runtime::Close(JNIEnv* jniEnv) noexcept {
        V8RuntimeState expectedState = V8RuntimeState::Open;
        if (!closeState.compare_exchange_strong(expectedState, V8RuntimeState::Closing)) {
            return false;
        }
        // V8 and Node teardown may invoke Java callbacks, so their global
        // references must remain valid until all native resources are closed.
        CloseV8Context();
        CloseV8Isolate();
        ClearExternalException(jniEnv);
        ClearExternalV8Runtime(jniEnv);
        closeState.store(V8RuntimeState::Closed);
        return true;
    }

    void V8Runtime::CloseV8Context() noexcept {
        v8Locker.reset();
        if (!v8GlobalContext.IsEmpty()) {
            auto internalV8Locker = GetUniqueV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Isolate);
            auto v8LocalContext = GetV8LocalContext();
            CloseCallbackContextReferences();
            if (v8Inspector) {
                v8Inspector->contextDestroyed();
            }
            Unregister(v8LocalContext);
            v8GlobalObject.Reset();
        }
#ifdef ENABLE_NODE
        if (nodeCommonSetup) {
            if (!nodeCommonSetup->snapshot_creator()) {
                // Restoration path: drain event loop and stop before setup destructor.
                int errorCode = 0;
                if (!IsStopping()) {
                    auto internalV8Locker = GetUniqueV8Locker();
                    auto v8IsolateScope = GetV8IsolateScope();
                    V8HandleScope v8HandleScope(v8Isolate);
                    auto v8LocalContext = GetV8LocalContext();
                    auto v8ContextScope = GetV8ContextScope(v8LocalContext);
                    errorCode = node::SpinEventLoop(nodeCommonSetup->env()).FromMaybe(1);
                }
                if (errorCode == 0) {
                    node::Stop(nodeCommonSetup->env());
                }
            }
        }
        else if (nodeEnvironment) {
            int errorCode = 0;
            if (!IsStopping()) {
                auto internalV8Locker = GetUniqueV8Locker();
                auto v8IsolateScope = GetV8IsolateScope();
                V8HandleScope v8HandleScope(v8Isolate);
                auto v8LocalContext = GetV8LocalContext();
                auto v8ContextScope = GetV8ContextScope(v8LocalContext);
                errorCode = node::SpinEventLoop(nodeEnvironment.get()).FromMaybe(1);
            }
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
        }
#endif
        v8GlobalContext.Reset();
    }

    void V8Runtime::CloseV8Isolate() noexcept {
        if (v8Isolate != nullptr) {
            auto internalV8Locker = GetUniqueV8Locker();
            auto v8IsolateScope = GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Isolate);
            CloseCallbackContextReferences();
        }
        if (v8Inspector) {
            auto internalV8Locker = GetSharedV8Locker();
            v8Inspector.reset();
        }
        v8GlobalObject.Reset();
        v8GlobalContext.Reset();
        v8Locker.reset();
#ifdef ENABLE_NODE
        if (nodeCommonSetup) {
            // CommonEnvironmentSetup owns the isolate, environment, isolate data,
            // and event loop. Its destructor handles all cleanup in the right order.
            nodeCommonSetup.reset();
            nodeSnapshotData.reset();
            v8StartupData.reset();
            v8Isolate = nullptr;
        }
        else {
            // node::FreeIsolateData is thread-safe.
            nodeIsolateData.reset();
            // Isolate must be the last one to be disposed.
            if (v8Isolate != nullptr) {
                bool isIsolateFinished = false;
                // AddIsolateFinishedCallback is thread-safe.
                v8PlatformPointer->AddIsolateFinishedCallback(v8Isolate, [](void* data) {
                    *static_cast<bool*>(data) = true;
                    }, &isIsolateFinished);
                // UnregisterIsolate is thread-safe.
                v8PlatformPointer->DisposeIsolate(v8Isolate);
                if (v8SnapshotCreator) {
                    v8SnapshotCreator.reset();
                }
                while (!isIsolateFinished) {
                    uv_run(&uvLoop, UV_RUN_ONCE);
                }
                v8Isolate = nullptr;
            }
            CloseUVLoop();
            // Free snapshot data after isolate is disposed because the
            // isolate may reference the V8 blob inside SnapshotData.
            nodeSnapshotData.reset();
            v8StartupData.reset();
        }
#else
        // Isolate must be the last one to be disposed.
        if (v8Isolate != nullptr) {
            if (v8SnapshotCreator) {
                v8SnapshotCreator.reset();
            }
            else {
                v8Isolate->Dispose();
            }
            v8StartupData.reset();
            v8Isolate = nullptr;
        }
#endif
    }

    void V8Runtime::CloseCallbackContextReferences() noexcept {
        std::unordered_set<Callback::JavetCallbackContextReference*> references;
        {
            std::lock_guard<std::mutex> lock(callbackContextReferencesMutex);
            references.swap(callbackContextReferences);
        }
        for (auto callbackContextReference : references) {
            callbackContextReference->v8Runtime = nullptr;
            delete callbackContextReference;
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteJavetCallbackContextReference);
        }
    }

#ifdef ENABLE_NODE
    void V8Runtime::CloseUVLoop() noexcept {
        if (!uvLoopInitialized) {
            return;
        }
        int errorCode = uv_loop_close(&uvLoop);
        if (errorCode == UV_EBUSY) {
            uv_walk(&uvLoop, [](uv_handle_t* handle, void*) {
                if (!uv_is_closing(handle)) {
                    uv_close(handle, nullptr);
                }
            }, nullptr);
            uv_run(&uvLoop, UV_RUN_DEFAULT);
            errorCode = uv_loop_close(&uvLoop);
        }
        if (errorCode != 0) {
            LOG_ERROR("Failed to close uv loop. Reason: " << uv_err_name(errorCode));
        }
        else {
            uvLoopInitialized = false;
        }
    }
#endif

    void V8Runtime::RegisterCallbackContextReference(
        Callback::JavetCallbackContextReference* callbackContextReference) noexcept {
        std::lock_guard<std::mutex> lock(callbackContextReferencesMutex);
        callbackContextReferences.insert(callbackContextReference);
    }

    void V8Runtime::UnregisterCallbackContextReference(
        Callback::JavetCallbackContextReference* callbackContextReference) noexcept {
        std::lock_guard<std::mutex> lock(callbackContextReferencesMutex);
        callbackContextReferences.erase(callbackContextReference);
    }

    jbyteArray V8Runtime::CreateSnapshot(JNIEnv* jniEnv) noexcept {
        jbyteArray jbytes = nullptr;
#ifdef ENABLE_NODE
        if (nodeCommonSetup && nodeCommonSetup->snapshot_creator()) {
            // Node.js snapshot via CommonEnvironmentSetup::CreateSnapshot().
            // This properly serializes IsolateData (eternal handles, private symbols),
            // Environment state, and all contexts with the correct callbacks.
            // Reset Javet's global handles BEFORE CreateSnapshot. The SnapshotCreator
            // iterates all v8::Global handles during CreateBlob() and crashes on any
            // handle it doesn't know about (like our JSGlobalProxy).
            v8GlobalContext.Reset();
            v8GlobalObject.Reset();
            // Drain the event loop before creating the snapshot.
            node::SpinEventLoop(nodeCommonSetup->env());
            auto snapshot = nodeCommonSetup->CreateSnapshot();
            if (snapshot) {
                auto blobVec = snapshot->ToBlob();
                jbytes = jniEnv->NewByteArray(static_cast<jsize>(blobVec.size()));
                jboolean isCopy;
                void* data = jniEnv->GetPrimitiveArrayCritical(jbytes, &isCopy);
                memcpy(data, blobVec.data(), blobVec.size());
                jniEnv->ReleasePrimitiveArrayCritical(jbytes, data, JNI_ABORT);
            }
        }
#else
        if (v8SnapshotCreator) {
            // V8 mode snapshot.
            auto v8LocalContext = GetV8LocalContext();
            v8GlobalContext.Reset();
            v8GlobalObject.Reset();
            v8SnapshotCreator->SetDefaultContext(v8LocalContext);
            v8::StartupData newV8StartupData = v8SnapshotCreator->CreateBlob(v8::SnapshotCreator::FunctionCodeHandling::kKeep);
            if (newV8StartupData.IsValid()) {
                jbytes = jniEnv->NewByteArray(newV8StartupData.raw_size);
                jboolean isCopy;
                void* data = jniEnv->GetPrimitiveArrayCritical(jbytes, &isCopy);
                memcpy(data, newV8StartupData.data, newV8StartupData.raw_size);
                jniEnv->ReleasePrimitiveArrayCritical(jbytes, data, JNI_ABORT);
                delete[] newV8StartupData.data;
            }
            // Restore context and global object.
            v8GlobalContext.Reset(v8Isolate, v8LocalContext);
            v8GlobalObject.Reset(v8Isolate, v8LocalContext->Global()->ToObject(v8LocalContext).ToLocalChecked());
        }
#endif
        return jbytes;
    }

    void V8Runtime::CreateV8Context(JNIEnv* jniEnv, const jobject mRuntimeOptions) noexcept {
        auto internalV8Locker = GetSharedV8Locker();
        auto v8IsolateScope = GetV8IsolateScope();
        V8HandleScope v8HandleScope(v8Isolate);
#ifdef ENABLE_NODE
        v8::Local<v8::Context> v8LocalContext;
        if (nodeCommonSetup) {
            // Snapshot creation: context already created by CommonEnvironmentSetup.
            v8LocalContext = nodeCommonSetup->context();
        }
        else if (nodeSnapshotData && !nodeIsolateData) {
            // Snapshot restoration: CreateIsolateData and CreateEnvironment must happen
            // in the same Locker/HandleScope, matching CommonEnvironmentSetup's constructor.
            nodeIsolateData.reset(node::CreateIsolateData(
                v8Isolate, &uvLoop, v8PlatformPointer,
                nodeArrayBufferAllocator.get(), nodeSnapshotData.get()));
            node::crypto::InitCryptoOnce(v8Isolate);
            std::vector<std::string> args{ DEFAULT_SCRIPT_NAME };
            std::vector<std::string> execArgs;
            if (mRuntimeOptions != nullptr) {
                jobjectArray mConsoleArguments = (jobjectArray)jniEnv->CallObjectMethod(mRuntimeOptions, jmethodNodeRuntimeOptionsGetConsoleArguments);
                if (mConsoleArguments != nullptr) {
                    int consoleArgumentCount = jniEnv->GetArrayLength(mConsoleArguments);
                    for (int i = 0; i < consoleArgumentCount; ++i) {
                        jstring mConsoleArgument = (jstring)jniEnv->GetObjectArrayElement(mConsoleArguments, i);
                        auto consoleArgumentPointer = Javet::Converter::ToStdString(jniEnv, mConsoleArgument);
                        args.push_back(*consoleArgumentPointer.get());
                        DELETE_LOCAL_REF(jniEnv, mConsoleArgument);
                    }
                    DELETE_LOCAL_REF(jniEnv, mConsoleArguments);
                }
            }
            // node::CreateEnvironment is not thread-safe.
            std::lock_guard<std::mutex> lock(mutexForNodeResetEnvrironment);
            auto flags = static_cast<node::EnvironmentFlags::Flags>(
                node::EnvironmentFlags::kOwnsProcessState
                | node::EnvironmentFlags::kNoCreateInspector);
            // Pass an empty context to trigger snapshot restoration path.
            // Node.js detects snapshot_data() on IsolateData and calls
            // Context::FromSnapshot with the proper deserialization callbacks.
            nodeEnvironment.reset(node::CreateEnvironment(
                nodeIsolateData.get(),
                v8::Local<v8::Context>(),
                args,
                execArgs,
                flags));
            if (nodeEnvironment) {
                v8LocalContext = nodeEnvironment->context();
            }
        }
        else {
            // Normal path: create context via node::NewContext.
            v8LocalContext = node::NewContext(v8Isolate);
        }
        auto v8ContextScope = GetV8ContextScope(v8LocalContext);
        if (nodeCommonSetup) {
            // Snapshot creation: bootstrap the environment.
            node::LoadEnvironment(nodeCommonSetup->env(), INIT_SCRIPT_WITH_SNAPSHOT);
        }
        else if (nodeSnapshotData && nodeEnvironment) {
            // Snapshot restoration: load with empty callback (state restored from snapshot).
            node::LoadEnvironment(nodeEnvironment.get(), node::StartExecutionCallback{});
        }
        else if (!nodeEnvironment) {
            // Normal path: create and load the environment.
            std::vector<std::string> args{ DEFAULT_SCRIPT_NAME };
            std::vector<std::string> execArgs;
            if (mRuntimeOptions != nullptr) {
                jobjectArray mConsoleArguments = (jobjectArray)jniEnv->CallObjectMethod(mRuntimeOptions, jmethodNodeRuntimeOptionsGetConsoleArguments);
                if (mConsoleArguments != nullptr) {
                    int consoleArgumentCount = jniEnv->GetArrayLength(mConsoleArguments);
                    LOG_DEBUG("Node.js console argument count is " << consoleArgumentCount);
                    if (consoleArgumentCount > 0) {
                        for (int i = 0; i < consoleArgumentCount; ++i) {
                            jstring mConsoleArgument = (jstring)jniEnv->GetObjectArrayElement(mConsoleArguments, i);
                            auto consoleArgumentPointer = Javet::Converter::ToStdString(jniEnv, mConsoleArgument);
                            auto umConsoleArgument = consoleArgumentPointer.get();
                            LOG_DEBUG("    " << i << ": " << *umConsoleArgument);
                            args.push_back(*umConsoleArgument);
                            DELETE_LOCAL_REF(jniEnv, mConsoleArgument);
                        }
                    }
                    DELETE_LOCAL_REF(jniEnv, mConsoleArguments);
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
                INIT_SCRIPT_WITHOUT_SNAPSHOT
            );
        }
#else
        auto v8ObjectTemplate = v8::ObjectTemplate::New(v8Isolate);
        if (mRuntimeOptions != nullptr) {
            jstring mGlobalName = (jstring)jniEnv->CallObjectMethod(mRuntimeOptions, jmethodV8RuntimeOptionsGetGlobalName);
            if (mGlobalName != nullptr) {
                auto umGlobalName = Javet::Converter::ToV8String(jniEnv, v8Isolate, mGlobalName);
                v8ObjectTemplate->SetNativeDataProperty(umGlobalName, GlobalAccessorGetterCallback);
                DELETE_LOCAL_REF(jniEnv, mGlobalName);
            }
        }
        auto v8LocalContext = v8::Context::New(v8Isolate, nullptr, v8ObjectTemplate);
        auto v8ContextScope = GetV8ContextScope(v8LocalContext);
#endif
        Register(v8LocalContext);
        v8GlobalContext.Reset(v8Isolate, v8LocalContext);
        v8GlobalObject.Reset(
            v8Isolate, v8LocalContext->Global()->ToObject(v8LocalContext).ToLocalChecked());
        // Notify the inspector about the new context.
        if (v8Inspector) {
            v8Inspector->contextCreated();
        }
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
            DELETE_LOCAL_REF(jniEnv, snapshotBlob);
        }
#ifdef ENABLE_NODE
        if (createSnapshotEnabled) {
            // Snapshot creation: use CommonEnvironmentSetup::CreateForSnapshotting.
            // This is the official Node.js embedder API that properly handles
            // SnapshotCreator setup, IsolateData serialization (eternal handles),
            // and the is_building_snapshot() flag for code cache guards.
            std::vector<std::string> errors;
            std::vector<std::string> args{ DEFAULT_SCRIPT_NAME, node::GetAnonymousMainPath() };
            std::vector<std::string> execArgs;
            if (mRuntimeOptions != nullptr) {
                jobjectArray mConsoleArguments = (jobjectArray)jniEnv->CallObjectMethod(mRuntimeOptions, jmethodNodeRuntimeOptionsGetConsoleArguments);
                if (mConsoleArguments != nullptr) {
                    int consoleArgumentCount = jniEnv->GetArrayLength(mConsoleArguments);
                    for (int i = 0; i < consoleArgumentCount; ++i) {
                        jstring mConsoleArgument = (jstring)jniEnv->GetObjectArrayElement(mConsoleArguments, i);
                        auto consoleArgumentPointer = Javet::Converter::ToStdString(jniEnv, mConsoleArgument);
                        args.push_back(*consoleArgumentPointer.get());
                        DELETE_LOCAL_REF(jniEnv, mConsoleArgument);
                    }
                    DELETE_LOCAL_REF(jniEnv, mConsoleArguments);
                }
            }
            nodeCommonSetup = node::CommonEnvironmentSetup::CreateForSnapshotting(
                v8PlatformPointer, &errors, args, execArgs);
            if (nodeCommonSetup) {
                v8Isolate = nodeCommonSetup->isolate();
            }
            else {
                LOG_ERROR("CreateForSnapshotting failed with " << errors.size() << " error(s).");
            }
        }
        else if (v8StartupData) {
            // Snapshot restoration: bypass CommonEnvironmentSetup::CreateFromSnapshot
            // because node::NewIsolate has a static variable that forces all isolates
            // to use the first-ever snapshot blob (CanUseCustomSnapshotPerIsolate() == false).
            // We create the isolate manually with the correct snapshot blob.
            // Note: CreateIsolateData and CreateEnvironment are deferred to CreateV8Context
            // so they execute in a single Locker/HandleScope, matching the
            // CommonEnvironmentSetup constructor pattern.
            std::vector<char> blobVec(v8StartupData->data, v8StartupData->data + v8StartupData->raw_size);
            nodeSnapshotData = node::EmbedderSnapshotData::FromBlob(blobVec);
            if (nodeSnapshotData) {
                int errorCode = uv_loop_init(&uvLoop);
                if (errorCode != 0) {
                    LOG_ERROR("Failed to init uv loop. Reason: " << uv_err_name(errorCode));
                }
                else {
                    uvLoopInitialized = true;
                    v8Isolate = Javet::NewIsolateForSnapshotRestore(
                        v8PlatformPointer, &uvLoop, nodeSnapshotData.get(), nodeArrayBufferAllocator);
                    if (v8Isolate != nullptr) {
                        v8Isolate->SetModifyCodeGenerationFromStringsCallback(nullptr);
                    }
                }
            }
            else {
                LOG_ERROR("Failed to parse EmbedderSnapshotData from blob.");
            }
        }
        else {
            int errorCode = uv_loop_init(&uvLoop);
            if (errorCode != 0) {
                LOG_ERROR("Failed to init uv loop. Reason: " << uv_err_name(errorCode));
            }
            else {
                uvLoopInitialized = true;
                // node::NewIsolate is thread-safe.
                v8Isolate = node::NewIsolate(nodeArrayBufferAllocator, &uvLoop, v8PlatformPointer);
                if (v8Isolate != nullptr) {
                    auto internalV8Locker = GetUniqueV8Locker();
                    auto v8IsolateScope = GetV8IsolateScope();
                    V8HandleScope v8HandleScope(v8Isolate);
                    // node::CreateIsolateData is thread-safe.
                    nodeIsolateData.reset(node::CreateIsolateData(v8Isolate, &uvLoop, v8PlatformPointer, nodeArrayBufferAllocator.get()));
                    node::crypto::InitCryptoOnce(v8Isolate);
                    v8Isolate->SetModifyCodeGenerationFromStringsCallback(nullptr);
                }
            }
        }
#else
        // V8 mode + pointer compression in multi-cage mode: allocate a fresh
        // IsolateGroup so each isolate gets its own 4 GB pointer-compression
        // cage instead of sharing the process-wide cage. The IsolateGroup is
        // reference counted; Isolate::Allocate/New acquires a reference, so
        // the local can drop its reference on scope exit. The cage's virtual
        // memory mapping is released when the isolate is disposed.
#if defined(V8_COMPRESS_POINTERS) && defined(V8_COMPRESS_POINTERS_IN_MULTIPLE_CAGES)
        v8::IsolateGroup v8IsolateGroup = v8::IsolateGroup::Create();
        // With V8_ENABLE_SANDBOX the array buffer backing stores must live
        // inside the IsolateGroup's sandbox address space. The process-wide
        // GlobalV8ArrayBufferAllocator (NewDefaultAllocator() with no group)
        // falls back to malloc/free and triggers
        // "ArrayBuffer backing stores must be allocated inside the sandbox".
        // Override with the group-aware overload so this runtime's backing
        // stores land in the right sandbox. The allocator must outlive the
        // isolate; storing it on the V8Runtime via reset() achieves that.
        v8ArrayBufferAllocator.reset(V8ArrayBufferAllocator::NewDefaultAllocator(v8IsolateGroup));
        if (createSnapshotEnabled) {
            v8Isolate = v8::Isolate::Allocate(v8IsolateGroup);
            v8SnapshotCreator.reset(new v8::SnapshotCreator(v8Isolate, nullptr, v8StartupData.get(), true));
        }
        else {
            v8::Isolate::CreateParams createParams;
            createParams.array_buffer_allocator = v8ArrayBufferAllocator.get();
            createParams.oom_error_callback = Javet::Callback::OOMErrorCallback;
            createParams.snapshot_blob = v8StartupData.get();
            v8Isolate = v8::Isolate::New(v8IsolateGroup, createParams);
        }
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
#endif
        v8Isolate->SetPromiseRejectCallback(Javet::Callback::JavetPromiseRejectCallback);
#endif
    }

    jobject V8Runtime::SafeToExternalV8Value(
        JNIEnv* jniEnv,
        V8Isolate* v8Isolate,
        const V8LocalContext& v8Context,
        const v8::internal::Tagged<V8InternalObject>& v8InternalObject) noexcept {
        V8TryCatch v8TryCatch(v8Isolate);
        jobject externalV8Value = Javet::Converter::ToExternalV8Value(jniEnv, this, v8Context, v8InternalObject);
        if (v8TryCatch.HasCaught()) {
            DELETE_LOCAL_REF(jniEnv, externalV8Value);
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, this, v8Context, v8TryCatch);
        }
        return externalV8Value;
    }

    jobject V8Runtime::SafeToExternalV8Value(
        JNIEnv* jniEnv,
        V8Isolate* v8Isolate,
        const V8LocalContext& v8Context,
        const V8LocalValue& v8Value) noexcept {
        V8TryCatch v8TryCatch(v8Isolate);
        jobject externalV8Value = Javet::Converter::ToExternalV8Value(jniEnv, this, v8Context, v8Value);
        if (v8TryCatch.HasCaught()) {
            DELETE_LOCAL_REF(jniEnv, externalV8Value);
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, this, v8Context, v8TryCatch);
        }
        return externalV8Value;
    }

#ifdef ENABLE_NODE
    bool V8Runtime::IsBuiltInModuleResolution(JNIEnv* jniEnv) const noexcept {
        bool result = false;
        jobject mRuntimeOptions = jniEnv->CallObjectMethod(
            externalV8Runtime, jmethodV8RuntimeGetRuntimeOptions);
        if (mRuntimeOptions != nullptr) {
            result = jniEnv->CallBooleanMethod(
                mRuntimeOptions, jmethodNodeRuntimeOptionsIsBuiltInModuleResolution);
            DELETE_LOCAL_REF(jniEnv, mRuntimeOptions);
        }
        return result;
    }
#endif

    V8Runtime::~V8Runtime() {
        V8RuntimeState expectedState = V8RuntimeState::Open;
        if (closeState.compare_exchange_strong(expectedState, V8RuntimeState::Closing)) {
            CloseV8Context();
            CloseV8Isolate();
            closeState.store(V8RuntimeState::Closed);
        }
    }
}

