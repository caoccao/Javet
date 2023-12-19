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

#include "javet_jni.h"

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
    V8TryCatch v8TryCatch(v8Context->GetIsolate());
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
        clonedV8LocalValue = V8LocalValue::New(v8Context->GetIsolate(), v8LocalValue);
    }
    if (v8TryCatch.HasCaught()) {
        return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
    }
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, clonedV8LocalValue);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->ClearExternalException(jniEnv);
    v8Runtime->ClearExternalV8Runtime(jniEnv);
    delete v8Runtime;
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteV8Runtime);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Inspector
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mV8Inspector) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Runtime->v8Inspector.reset(new Javet::Inspector::JavetInspector(v8Runtime, mV8Inspector));
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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getV8HeapSpaceStatistics
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint allocationSpace) {
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
    case V8ValueInternalType::Undefined: return v8LocalValue->IsUndefined(); // 0
    case V8ValueInternalType::Null: return v8LocalValue->IsNull(); // 1
    case V8ValueInternalType::NullOrUndefined: return v8LocalValue->IsNullOrUndefined(); // 2
    case V8ValueInternalType::True: return v8LocalValue->IsTrue(); // 3
    case V8ValueInternalType::False: return v8LocalValue->IsFalse(); // 4
    case V8ValueInternalType::Name: return v8LocalValue->IsName(); // 5
    case V8ValueInternalType::String: return v8LocalValue->IsString(); // 6
    case V8ValueInternalType::Symbol: return v8LocalValue->IsSymbol(); // 7
    case V8ValueInternalType::Function: return v8LocalValue->IsFunction(); // 8
    case V8ValueInternalType::Array: return v8LocalValue->IsArray(); // 9
    case V8ValueInternalType::Object: return v8LocalValue->IsObject(); // 10
    case V8ValueInternalType::BigInt: return v8LocalValue->IsBigInt(); // 11
    case V8ValueInternalType::Boolean: return v8LocalValue->IsBoolean(); // 12
    case V8ValueInternalType::Number: return v8LocalValue->IsNumber(); // 13
    case V8ValueInternalType::External: return v8LocalValue->IsExternal(); // 14
    case V8ValueInternalType::Int32: return v8LocalValue->IsInt32(); // 15
    case V8ValueInternalType::Date: return v8LocalValue->IsDate(); // 16
    case V8ValueInternalType::ArgumentsObject: return v8LocalValue->IsArgumentsObject(); // 17
    case V8ValueInternalType::BigIntObject: return v8LocalValue->IsBigIntObject(); // 18
    case V8ValueInternalType::BooleanObject: return v8LocalValue->IsBooleanObject(); // 19
    case V8ValueInternalType::NumberObject: return v8LocalValue->IsNumberObject(); // 20
    case V8ValueInternalType::StringObject: return v8LocalValue->IsStringObject(); // 21
    case V8ValueInternalType::SymbolObject: return v8LocalValue->IsSymbolObject(); // 22
    case V8ValueInternalType::NativeError: return v8LocalValue->IsNativeError(); // 23
    case V8ValueInternalType::RegExp: return v8LocalValue->IsRegExp(); // 24
    case V8ValueInternalType::AsyncFunction: return v8LocalValue->IsAsyncFunction(); // 25
    case V8ValueInternalType::GeneratorFunction: return v8LocalValue->IsGeneratorFunction(); // 26
    case V8ValueInternalType::GeneratorObject: return v8LocalValue->IsGeneratorObject(); // 27
    case V8ValueInternalType::Promise: return v8LocalValue->IsPromise(); // 28
    case V8ValueInternalType::Map: return v8LocalValue->IsMap(); // 29
    case V8ValueInternalType::Set: return v8LocalValue->IsSet(); // 30
    case V8ValueInternalType::MapIterator: return v8LocalValue->IsMapIterator(); // 31
    case V8ValueInternalType::SetIterator: return v8LocalValue->IsSetIterator(); // 32
    case V8ValueInternalType::WeakMap: return v8LocalValue->IsWeakMap(); // 33
    case V8ValueInternalType::WeakSet: return v8LocalValue->IsWeakSet(); // 34
    case V8ValueInternalType::ArrayBuffer: return v8LocalValue->IsArrayBuffer(); // 35
    case V8ValueInternalType::ArrayBufferView: return v8LocalValue->IsArrayBufferView(); // 36
    case V8ValueInternalType::TypedArray: return v8LocalValue->IsTypedArray(); // 37
    case V8ValueInternalType::Uint8Array: return v8LocalValue->IsUint8Array(); // 38
    case V8ValueInternalType::Uint8ClampedArray: return v8LocalValue->IsUint8ClampedArray(); // 39
    case V8ValueInternalType::Int8Array: return v8LocalValue->IsInt8Array(); // 40
    case V8ValueInternalType::Uint16Array: return v8LocalValue->IsUint16Array(); // 41
    case V8ValueInternalType::Int16Array: return v8LocalValue->IsInt16Array(); // 42
    case V8ValueInternalType::Uint32Array: return v8LocalValue->IsUint32Array(); // 43
    case V8ValueInternalType::Int32Array: return v8LocalValue->IsInt32Array(); // 44
    case V8ValueInternalType::Float32Array: return v8LocalValue->IsFloat32Array(); // 45
    case V8ValueInternalType::Float64Array: return v8LocalValue->IsFloat64Array(); // 46
    case V8ValueInternalType::BigInt64Array: return v8LocalValue->IsBigInt64Array(); // 47
    case V8ValueInternalType::BigUint64Array: return v8LocalValue->IsBigUint64Array(); // 48
    case V8ValueInternalType::DataView: return v8LocalValue->IsDataView(); // 49
    case V8ValueInternalType::SharedArrayBuffer: return v8LocalValue->IsSharedArrayBuffer(); // 50
    case V8ValueInternalType::Proxy: return v8LocalValue->IsProxy(); // 51
    case V8ValueInternalType::WasmModuleObject: return v8LocalValue->IsWasmModuleObject(); // 52
    case V8ValueInternalType::ModuleNamespaceObject: return v8LocalValue->IsModuleNamespaceObject(); // 53
    default:
        break;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPendingException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    return v8InternalIsolate->has_pending_exception();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPendingMessage
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    return v8InternalIsolate->has_pending_message();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasScheduledException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    return v8InternalIsolate->has_scheduled_exception();
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_idleNotificationDeadline
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong deadlineInMillis) {
    if (deadlineInMillis > 0) {
        RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
        v8Context->GetIsolate()->IdleNotificationDeadline(((long)deadlineInMillis) / 1000.0);
    }
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isDead
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return v8Runtime->v8Isolate->IsDead();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isInUse
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return v8Runtime->v8Isolate->IsInUse();
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
    return true;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lowMemoryNotification
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Context->GetIsolate()->LowMemoryNotification();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promoteScheduledException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    if (v8InternalIsolate->has_scheduled_exception()) {
        v8InternalIsolate->PromoteScheduledException();
        return true;
    }
    return false;
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

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong referenceHandle, jint referenceType) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto v8Locker = v8Runtime->GetSharedV8Locker();
    auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(referenceHandle);
    v8PersistentDataPointer->Reset();
    delete v8PersistentDataPointer;
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeletePersistentReference);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_reportPendingMessages
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    return Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_requestGarbageCollectionForTesting
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean fullGC) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Context->GetIsolate()->RequestGarbageCollectionForTesting(fullGC
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
    return v8Runtime->createSnapshot(jniEnv);
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
            return Javet::Converter::ToJavaString(jniEnv, v8Context, v8MaybeLocalString.ToLocalChecked());
        }
    }
    V8LocalString v8LocalString;
    return Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalString);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    if (!v8Runtime->IsLocked()) {
        return false;
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

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorSend
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mMessage) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    char const* umMessage = jniEnv->GetStringUTFChars(mMessage, nullptr);
    std::string message(umMessage, jniEnv->GetStringUTFLength(mMessage));
    v8Runtime->v8Inspector->send(message);
    jniEnv->ReleaseStringUTFChars(mMessage, umMessage);
}
