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

#include "javet_jni.h"
#include "javet_v8_internal.h"

 /*
  * Development Guide:
  * 1. Omitting namespace is not recommended in this project.
  * 2. Methods are expected to be sorted alphabatically except JNI_OnLoad.
  */

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_allowCodeGenerationFromStrings
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean allow) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Context->AllowCodeGenerationFromStrings(allow);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_await
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint mAwaitMode) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto umAwaitMode = static_cast<Javet::Enums::V8AwaitMode::V8AwaitMode>(mAwaitMode);
    return (jboolean)v8Runtime->Await(umAwaitMode);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_cancelTerminateExecution
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->CancelTerminateExecution();
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_clearInternalStatistic
(JNIEnv* jniEnv, jobject caller) {
#ifdef ENABLE_MONITOR
    GlobalJavetNativeMonitor.Clear();
#endif
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_clearWeak
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (!v8PersistentDataPointer->IsEmpty() && v8PersistentDataPointer->IsWeak()) {
        auto v8ValueReference = v8PersistentDataPointer->ClearWeak<Javet::Callback::V8ValueReference>();
        v8ValueReference->Clear();
        delete v8ValueReference;
        INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteWeakCallbackReference);
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_cloneV8Value
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mReferenceCopy) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8TryCatch v8TryCatch(v8Isolate);
    V8LocalValue clonedV8LocalValue;
    if (!mReferenceCopy
        && !v8LocalValue->IsFunction()
        && !v8LocalValue->IsMap()
        && !v8LocalValue->IsSet()
        && !v8LocalValue->IsWeakMap()
        && !v8LocalValue->IsWeakSet()
        && v8LocalValue->IsObject()) {
        /*
         * Not all objects can be value copied.
         * V8 performs the CHECK(is_clonable_js_type || is_clonable_wasm_type).
         * The object actually is shadow copied.
         */
        clonedV8LocalValue = v8LocalValue.As<v8::Object>()->Clone();
    }
    else {
        // Reference copy
        clonedV8LocalValue = V8LocalValue::New(v8Isolate, v8LocalValue);
    }
    if (v8TryCatch.HasCaught()) {
        return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
    }
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, clonedV8LocalValue);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->ClearExternalException(jniEnv);
    v8Runtime->ClearExternalV8Runtime(jniEnv);
    delete v8Runtime;
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteV8Runtime);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Inspector
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mV8Inspector, jstring mName, jboolean waitForDebugger) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (!v8Runtime->v8Inspector) {
        char const* umName = jniEnv->GetStringUTFChars(mName, nullptr);
        std::string name(umName, jniEnv->GetStringUTFLength(mName));
        jniEnv->ReleaseStringUTFChars(mName, umName);
        v8Runtime->v8Inspector.reset(new Javet::Inspector::JavetInspector(v8Runtime, name));
    }
    return v8Runtime->v8Inspector->addSession(mV8Inspector, waitForDebugger);
}

/*
Creating multiple isolates allows running JavaScript code in multiple threads, truly parallel.
*/
JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Runtime
(JNIEnv* jniEnv, jobject caller, jobject mRuntimeOptions) {
#ifdef ENABLE_NODE
    auto v8Runtime = new Javet::V8Runtime(
        Javet::V8Native::GlobalV8Platform.get(),
        Javet::NodeNative::GlobalNodeArrayBufferAllocator);
#else
    auto v8Runtime = new Javet::V8Runtime(
        Javet::V8Native::GlobalV8Platform.get(),
        Javet::V8Native::GlobalV8ArrayBufferAllocator);
#endif
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewV8Runtime);
    v8Runtime->CreateV8Isolate(jniEnv, mRuntimeOptions);
    v8Runtime->CreateV8Context(jniEnv, mRuntimeOptions);
    return TO_JAVA_LONG(v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_equals
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
    RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
    V8MaybeBool v8MaybeBool = v8LocalValue1->Equals(v8Context, v8LocalValue2);
    if (v8MaybeBool.IsNothing()) {
        Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        return false;
    }
    return v8MaybeBool.FromMaybe(false);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getGlobalObject
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    return Javet::Converter::ToExternalV8ValueGlobalObject(jniEnv, v8Runtime);
}

JNIEXPORT jlongArray JNICALL Java_com_caoccao_javet_interop_V8Native_getInternalStatistic
(JNIEnv* jniEnv, jobject caller) {
#ifdef ENABLE_MONITOR
    return GlobalJavetNativeMonitor.GetCounters(jniEnv);
#else
    return nullptr;
#endif
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getPriority
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
    return static_cast<jint>(v8InternalIsolate->priority());
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getV8HeapSpaceStatistics
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject allocationSpace) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return Javet::Monitor::GetHeapSpaceStatistics(jniEnv, v8Runtime->v8Isolate, allocationSpace);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getV8HeapStatistics
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return Javet::Monitor::GetHeapStatistics(jniEnv, v8Runtime->v8Isolate);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getV8SharedMemoryStatistics
(JNIEnv* jniEnv, jobject caller) {
    return Javet::Monitor::GetV8SharedMemoryStatistics(jniEnv);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
(JNIEnv* jniEnv, jobject caller) {
    return Javet::Converter::ToJavaString(jniEnv, v8::V8::GetVersion());
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasInternalType
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueInternalType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    using namespace Javet::Enums::V8ValueInternalType;
    switch (v8ValueInternalType)
    {
    case Undefined: return v8LocalValue->IsUndefined(); // 0
    case Null: return v8LocalValue->IsNull(); // 1
    case NullOrUndefined: return v8LocalValue->IsNullOrUndefined(); // 2
    case True: return v8LocalValue->IsTrue(); // 3
    case False: return v8LocalValue->IsFalse(); // 4
    case Name: return v8LocalValue->IsName(); // 5
    case String: return v8LocalValue->IsString(); // 6
    case Symbol: return v8LocalValue->IsSymbol(); // 7
    case Function: return v8LocalValue->IsFunction(); // 8
    case Array: return v8LocalValue->IsArray(); // 9
    case Object: return v8LocalValue->IsObject(); // 10
    case BigInt: return v8LocalValue->IsBigInt(); // 11
    case Boolean: return v8LocalValue->IsBoolean(); // 12
    case Number: return v8LocalValue->IsNumber(); // 13
    case External: return v8LocalValue->IsExternal(); // 14
    case Int32: return v8LocalValue->IsInt32(); // 15
    case Date: return v8LocalValue->IsDate(); // 16
    case ArgumentsObject: return v8LocalValue->IsArgumentsObject(); // 17
    case BigIntObject: return v8LocalValue->IsBigIntObject(); // 18
    case BooleanObject: return v8LocalValue->IsBooleanObject(); // 19
    case NumberObject: return v8LocalValue->IsNumberObject(); // 20
    case StringObject: return v8LocalValue->IsStringObject(); // 21
    case SymbolObject: return v8LocalValue->IsSymbolObject(); // 22
    case NativeError: return v8LocalValue->IsNativeError(); // 23
    case RegExp: return v8LocalValue->IsRegExp(); // 24
    case AsyncFunction: return v8LocalValue->IsAsyncFunction(); // 25
    case GeneratorFunction: return v8LocalValue->IsGeneratorFunction(); // 26
    case GeneratorObject: return v8LocalValue->IsGeneratorObject(); // 27
    case Promise: return v8LocalValue->IsPromise(); // 28
    case Map: return v8LocalValue->IsMap(); // 29
    case Set: return v8LocalValue->IsSet(); // 30
    case MapIterator: return v8LocalValue->IsMapIterator(); // 31
    case SetIterator: return v8LocalValue->IsSetIterator(); // 32
    case WeakMap: return v8LocalValue->IsWeakMap(); // 33
    case WeakSet: return v8LocalValue->IsWeakSet(); // 34
    case ArrayBuffer: return v8LocalValue->IsArrayBuffer(); // 35
    case ArrayBufferView: return v8LocalValue->IsArrayBufferView(); // 36
    case TypedArray: return v8LocalValue->IsTypedArray(); // 37
    case Uint8Array: return v8LocalValue->IsUint8Array(); // 38
    case Uint8ClampedArray: return v8LocalValue->IsUint8ClampedArray(); // 39
    case Int8Array: return v8LocalValue->IsInt8Array(); // 40
    case Uint16Array: return v8LocalValue->IsUint16Array(); // 41
    case Int16Array: return v8LocalValue->IsInt16Array(); // 42
    case Uint32Array: return v8LocalValue->IsUint32Array(); // 43
    case Int32Array: return v8LocalValue->IsInt32Array(); // 44
    case Float16Array: return v8LocalValue->IsFloat16Array(); // 45
    case Float32Array: return v8LocalValue->IsFloat32Array(); // 46
    case Float64Array: return v8LocalValue->IsFloat64Array(); // 47
    case BigInt64Array: return v8LocalValue->IsBigInt64Array(); // 48
    case BigUint64Array: return v8LocalValue->IsBigUint64Array(); // 49
    case DataView: return v8LocalValue->IsDataView(); // 50
    case SharedArrayBuffer: return v8LocalValue->IsSharedArrayBuffer(); // 51
    case Proxy: return v8LocalValue->IsProxy(); // 52
    case WasmModuleObject: return v8LocalValue->IsWasmModuleObject(); // 53
    case ModuleNamespaceObject: return v8LocalValue->IsModuleNamespaceObject(); // 54
    default:
        break;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isI18nEnabled
(JNIEnv* jniEnv, jobject caller) {
#ifdef ENABLE_I18N
    return true;
#else
    return false;
#endif
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isBatterySaverModeEnabled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
    return v8InternalIsolate->BatterySaverModeEnabled();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isDead
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return v8Runtime->v8Isolate->IsDead();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isEfficiencyModeEnabled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
    return v8InternalIsolate->EfficiencyModeEnabled();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isExecutionTerminating
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return v8Runtime->v8Isolate->IsExecutionTerminating();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isInUse
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return v8Runtime->v8Isolate->IsInUse();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isMemorySaverModeEnabled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
    return v8InternalIsolate->MemorySaverModeEnabled();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isWeak
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (!v8PersistentDataPointer->IsEmpty()) {
        return (jboolean)v8PersistentDataPointer->IsWeak();
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_lockV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    if (v8Runtime->IsLocked()) {
        return false;
    }
    v8Runtime->Lock();
    if (v8Runtime->v8Inspector) {
        v8Runtime->v8Inspector->idleFinished();
    }
    return true;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lowMemoryNotification
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Isolate->LowMemoryNotification();
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerGCEpilogueCallback
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->AddGCEpilogueCallback(Javet::Callback::JavetGCEpilogueCallback);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerGCPrologueCallback
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->AddGCPrologueCallback(Javet::Callback::JavetGCPrologueCallback);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerNearHeapLimitCallback
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    LOG_DEBUG("registerNearHeapLimitCallback");
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->AddNearHeapLimitCallback(Javet::Callback::JavetNearHeapLimitCallback, v8Runtime);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mV8Runtime) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->ClearExternalException(jniEnv);
    v8Runtime->ClearExternalV8Runtime(jniEnv);
    v8Runtime->externalV8Runtime = jniEnv->NewGlobalRef(mV8Runtime);
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeJNIGlobalRef
(JNIEnv* jniEnv, jobject caller, jlong handle) {
    jniEnv->DeleteGlobalRef((jobject)handle);
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeRawPointer
(JNIEnv* jniEnv, jobject caller, jlong handle, jint rawPointerTypeId) {
    using namespace Javet::Enums::RawPointerType;
    switch (rawPointerTypeId) {
    case HeapStatisticsContext:
        Javet::Monitor::RemoveHeapStatisticsContext(handle);
        break;
    case HeapSpaceStatisticsContext:
        Javet::Monitor::RemoveHeapSpaceStatisticsContext(handle);
        break;
    default:
        break;
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong referenceHandle, jint referenceType) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8Locker = v8Runtime->GetSharedV8Locker();
    auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(referenceHandle);
    v8PersistentDataPointer->Reset();
    delete v8PersistentDataPointer;
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeletePersistentReference);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_requestGarbageCollectionForTesting
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean fullGC) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Isolate->RequestGarbageCollectionForTesting(fullGC
        ? v8::Isolate::GarbageCollectionType::kFullGarbageCollection
        : v8::Isolate::GarbageCollectionType::kMinorGarbageCollection);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Context
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mRuntimeOptions) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->CloseV8Context();
    v8Runtime->CreateV8Context(jniEnv, mRuntimeOptions);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Isolate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mRuntimeOptions) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->CloseV8Context();
    v8Runtime->CloseV8Isolate();
    v8Runtime->CreateV8Isolate(jniEnv, mRuntimeOptions);
    v8Runtime->CreateV8Context(jniEnv, mRuntimeOptions);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_sameValue
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
    RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
    return v8LocalValue1->SameValue(v8LocalValue2);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setBatterySaverModeEnabled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean enabled) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
    v8InternalIsolate->set_battery_saver_mode_enabled(enabled);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setMemorySaverModeEnabled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean enabled) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
    v8InternalIsolate->set_memory_saver_mode_enabled(enabled);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setPriority
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint mPriority) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
    v8InternalIsolate->SetPriority(static_cast<v8::Isolate::Priority>(mPriority));
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setWeak
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject objectReference) {
    RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (!v8PersistentDataPointer->IsEmpty() && !v8PersistentDataPointer->IsWeak()) {
        auto v8ValueReference = new Javet::Callback::V8ValueReference(jniEnv, objectReference);
        INCREASE_COUNTER(Javet::Monitor::CounterType::NewWeakCallbackReference);
        v8ValueReference->v8PersistentDataPointer = v8PersistentDataPointer;
        v8PersistentDataPointer->SetWeak(v8ValueReference, Javet::Callback::JavetCloseWeakDataReference, v8::WeakCallbackType::kParameter);
    }
}

JNIEXPORT jbyteArray JNICALL Java_com_caoccao_javet_interop_V8Native_snapshotCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    return v8Runtime->CreateSnapshot(jniEnv);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_strictEquals
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
    RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
    return v8LocalValue1->StrictEquals(v8LocalValue2);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_terminateExecution
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->TerminateExecution();
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (!IS_V8_MODULE(v8ValueType) && !IS_V8_SCRIPT(v8ValueType)) {
        V8MaybeLocalString v8MaybeLocalString = v8LocalValue->ToString(v8Context);
        if (v8MaybeLocalString.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return Javet::Converter::ToJavaString(jniEnv, v8Isolate, v8MaybeLocalString.ToLocalChecked());
        }
    }
    V8LocalString v8LocalString;
    return Javet::Converter::ToJavaString(jniEnv, v8Isolate, v8LocalString);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    if (!v8Runtime->IsLocked()) {
        return false;
    }
    if (v8Runtime->v8Inspector) {
        v8Runtime->v8Inspector->idleStarted();
    }
    v8Runtime->Unlock();
    return true;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unregisterGCEpilogueCallback
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->RemoveGCEpilogueCallback(Javet::Callback::JavetGCEpilogueCallback);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unregisterGCPrologueCallback
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->RemoveGCPrologueCallback(Javet::Callback::JavetGCPrologueCallback);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unregisterNearHeapLimitCallback
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong heapLimit) {
    LOG_DEBUG("unregisterNearHeapLimitCallback");
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->v8Isolate->RemoveNearHeapLimitCallback(Javet::Callback::JavetNearHeapLimitCallback, (size_t)heapLimit);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorSend
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint sessionId, jstring mMessage) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    char const* umMessage = jniEnv->GetStringUTFChars(mMessage, nullptr);
    std::string message(umMessage, jniEnv->GetStringUTFLength(mMessage));
    jniEnv->ReleaseStringUTFChars(mMessage, umMessage);
    if (v8Runtime->v8Inspector) {
        // Always enqueue first (lock-free). If a message loop is active
        // (paused at breakpoint or waiting for debugger), the loop on the
        // execution thread dispatches the message from the queue — no lock
        // needed.  If no loop is active, acquire the V8 lock to drain the
        // queue and pump microtasks ourselves.
        // V8's Locker is reentrant, so the lock-acquire path is also safe
        // when called from a thread that already holds the lock (e.g.
        // during a JS→Java callback).
        v8Runtime->v8Inspector->postMessage(sessionId, message);
        if (!v8Runtime->v8Inspector->isMessageLoopActive()) {
            auto v8Locker = v8Runtime->GetUniqueV8Locker();
            auto v8IsolateScope = v8Runtime->GetV8IsolateScope();
            V8HandleScope v8HandleScope(v8Runtime->v8Isolate);
            auto v8Context = v8Runtime->GetV8LocalContext();
            auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context);
            v8Runtime->v8Inspector->drainQueue();
        }
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorBreakProgram
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint sessionId, jstring mBreakReason, jstring mBreakDetails) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (v8Runtime->v8Inspector) {
        char const* umBreakReason = jniEnv->GetStringUTFChars(mBreakReason, nullptr);
        std::string breakReason(umBreakReason, jniEnv->GetStringUTFLength(mBreakReason));
        jniEnv->ReleaseStringUTFChars(mBreakReason, umBreakReason);
        char const* umBreakDetails = jniEnv->GetStringUTFChars(mBreakDetails, nullptr);
        std::string breakDetails(umBreakDetails, jniEnv->GetStringUTFLength(mBreakDetails));
        jniEnv->ReleaseStringUTFChars(mBreakDetails, umBreakDetails);
        v8Runtime->v8Inspector->breakProgram(sessionId, breakReason, breakDetails);
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorCancelPauseOnNextStatement
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint sessionId) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (v8Runtime->v8Inspector) {
        v8Runtime->v8Inspector->cancelPauseOnNextStatement(sessionId);
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorCloseSession
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint sessionId) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (v8Runtime->v8Inspector) {
        v8Runtime->v8Inspector->removeSession(sessionId);
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorEvaluate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint sessionId, jstring mExpression, jboolean includeCommandLineAPI) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (v8Runtime->v8Inspector) {
        char const* umExpression = jniEnv->GetStringUTFChars(mExpression, nullptr);
        std::string expression(umExpression, jniEnv->GetStringUTFLength(mExpression));
        jniEnv->ReleaseStringUTFChars(mExpression, umExpression);
        return v8Runtime->v8Inspector->evaluate(jniEnv, sessionId, expression, includeCommandLineAPI);
    }
    return nullptr;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorSchedulePauseOnNextStatement
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint sessionId, jstring mBreakReason, jstring mBreakDetails) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (v8Runtime->v8Inspector) {
        char const* umBreakReason = jniEnv->GetStringUTFChars(mBreakReason, nullptr);
        std::string breakReason(umBreakReason, jniEnv->GetStringUTFLength(mBreakReason));
        jniEnv->ReleaseStringUTFChars(mBreakReason, umBreakReason);
        char const* umBreakDetails = jniEnv->GetStringUTFChars(mBreakDetails, nullptr);
        std::string breakDetails(umBreakDetails, jniEnv->GetStringUTFLength(mBreakDetails));
        jniEnv->ReleaseStringUTFChars(mBreakDetails, umBreakDetails);
        v8Runtime->v8Inspector->schedulePauseOnNextStatement(sessionId, breakReason, breakDetails);
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorSetSkipAllPauses
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint sessionId, jboolean skip) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (v8Runtime->v8Inspector) {
        v8Runtime->v8Inspector->setSkipAllPauses(sessionId, skip);
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorWaitForDebugger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (v8Runtime->v8Inspector) {
        v8Runtime->v8Inspector->waitForDebugger();
    }
}
