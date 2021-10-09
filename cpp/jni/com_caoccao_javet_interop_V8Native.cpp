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

#include "com_caoccao_javet_interop_V8Native.h"
#include "javet_callbacks.h"
#include "javet_converter.h"
#include "javet_enums.h"
#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_monitor.h"
#include "javet_logging.h"
#include "javet_native.h"
#include "javet_node.h"
#include "javet_v8.h"
#include "javet_v8_runtime.h"
#include "javet_v8_internal.h"

 /*
  * Development Guide:
  * 1. Omitting namespace is not recommended in this project.
  * 2. Methods are expected to be sorted alphabatically except JNI_OnLoad.
  */

#define IS_JAVA_INTEGER(jniEnv, obj) jniEnv->IsInstanceOf(obj, Javet::V8Native::jclassV8ValueInteger)
#define IS_JAVA_STRING(jniEnv, obj) jniEnv->IsInstanceOf(obj, Javet::V8Native::jclassV8ValueString)
#define IS_JAVA_SYMBOL(jniEnv, obj) jniEnv->IsInstanceOf(obj, Javet::V8Native::jclassV8ValueSymbol)
#define TO_JAVA_INTEGER(jniEnv, obj) jniEnv->CallIntMethod(obj, Javet::V8Native::jmethodIDV8ValueIntegerToPrimitive)
#define TO_JAVA_STRING(jniEnv, obj) (jstring)jniEnv->CallObjectMethod(obj, Javet::V8Native::jmethodIDV8ValueStringToPrimitive)

namespace Javet {
#ifdef ENABLE_NODE
    namespace NodeNative {
        static jclass jclassV8Host;
        static jmethodID jmethodIDV8HostIsLibraryReloadable;

        static std::shared_ptr<node::ArrayBufferAllocator> GlobalNodeArrayBufferAllocator;

        void Dispose(JNIEnv* jniEnv) {
            if (!jniEnv->CallStaticBooleanMethod(jclassV8Host, jmethodIDV8HostIsLibraryReloadable)) {
                GlobalNodeArrayBufferAllocator.reset();
            }
        }

        void Initialize(JNIEnv* jniEnv) {
            jclassV8Host = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Host"));
            jmethodIDV8HostIsLibraryReloadable = jniEnv->GetStaticMethodID(jclassV8Host, "isLibraryReloadable", "()Z");

            if (!GlobalNodeArrayBufferAllocator) {
                GlobalNodeArrayBufferAllocator = node::ArrayBufferAllocator::Create();
            }
        }
    }
#endif

    namespace V8Native {
#ifdef ENABLE_NODE
        static std::unique_ptr<node::MultiIsolatePlatform> GlobalV8Platform;
#else
        static std::unique_ptr<V8Platform> GlobalV8Platform;
#endif

        static jclass jclassV8Host;
        static jmethodID jmethodIDV8HostIsLibraryReloadable;

        static jclass jclassV8ValueInteger;
        static jmethodID jmethodIDV8ValueIntegerToPrimitive;

        static jclass jclassV8ValueString;
        static jmethodID jmethodIDV8ValueStringToPrimitive;

        static jclass jclassV8ValueSymbol;

        void Dispose(JNIEnv* jniEnv) {
            if (!jniEnv->CallStaticBooleanMethod(jclassV8Host, jmethodIDV8HostIsLibraryReloadable)) {
                v8::V8::Dispose();
                v8::V8::ShutdownPlatform();
                GlobalV8Platform.reset();
            }
        }

        /*
        These Java classes and methods need to be initialized within this file
        because the memory address probed changes in another file,
        or runtime memory corruption will take place.
        */
        void Initialize(JNIEnv* jniEnv) {
            jclassV8Host = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Host"));
            jmethodIDV8HostIsLibraryReloadable = jniEnv->GetStaticMethodID(jclassV8Host, "isLibraryReloadable", "()Z");

            jclassV8ValueInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueInteger"));
            jmethodIDV8ValueIntegerToPrimitive = jniEnv->GetMethodID(jclassV8ValueInteger, JAVA_METHOD_TO_PRIMITIVE, "()I");

            jclassV8ValueString = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueString"));
            jmethodIDV8ValueStringToPrimitive = jniEnv->GetMethodID(jclassV8ValueString, JAVA_METHOD_TO_PRIMITIVE, "()Ljava/lang/String;");

            jclassV8ValueSymbol = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSymbol"));

            LOG_INFO("V8::Initialize() begins.");
#ifdef ENABLE_I18N
            v8::V8::InitializeICU();
#endif
            if (Javet::V8Native::GlobalV8Platform) {
                LOG_INFO("V8::Initialize() is skipped.");
            }
            else {
#ifdef ENABLE_NODE
                uv_setup_args(0, nullptr);
                std::vector<std::string> args{ "" };
                std::vector<std::string> execArgs;
                std::vector<std::string> errors;
                int exitCode = node::InitializeNodeWithArgs(&args, &execArgs, &errors);
                if (exitCode != 0) {
                    LOG_ERROR("Failed to call node::InitializeNodeWithArgs().");
                }
                Javet::V8Native::GlobalV8Platform = node::MultiIsolatePlatform::Create(4);
#else
                Javet::V8Native::GlobalV8Platform = v8::platform::NewDefaultPlatform();
#endif
                v8::V8::InitializePlatform(Javet::V8Native::GlobalV8Platform.get());
                v8::V8::Initialize();
            }
            LOG_INFO("V8::Initialize() ends.");
        }
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_add
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SET(v8ValueType)) {
        auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
        auto unusedSet = v8LocalValue.As<v8::Set>()->Add(v8Context, v8ValueValue);
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_allowCodeGenerationFromStrings
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean allow) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Context->AllowCodeGenerationFromStrings(allow);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_await
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Runtime->Await();
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_call
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mReceiver, jboolean mResultRequired, jobjectArray mValues) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsFunction()) {
        V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
        V8MaybeLocalValue v8MaybeLocalValueResult;
        auto umReceiver = Javet::Converter::ToV8Value(jniEnv, v8Context, mReceiver);
        uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
        if (valueCount > 0) {
            auto umValuesPointer = Javet::Converter::ToV8Values(jniEnv, v8Context, mValues);
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->Call(v8Context, umReceiver, valueCount, umValuesPointer.get());
        }
        else {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->Call(v8Context, umReceiver, 0, nullptr);
        }
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_callAsConstructor
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray mValues) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsFunction()) {
        V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
        V8MaybeLocalValue v8MaybeLocalValueResult;
        uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
        if (valueCount > 0) {
            auto umValuesPointer = Javet::Converter::ToV8Values(jniEnv, v8Context, mValues);
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->CallAsConstructor(v8Context, valueCount, umValuesPointer.get());
        }
        else {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->CallAsConstructor(v8Context, 0, nullptr);
        }
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
        }
        else if (!v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
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
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    auto clonedV8LocalValue = V8LocalValue::New(v8Context->GetIsolate(), v8LocalValue);
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, clonedV8LocalValue);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    if (v8Runtime->externalV8Runtime != nullptr) {
        jniEnv->DeleteGlobalRef(v8Runtime->externalV8Runtime);
        INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
    }
    delete v8Runtime;
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_compile
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
    auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
    auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
        jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
    if (mIsModule) {
        V8ScriptCompilerSource scriptSource(umScript, *scriptOriginPointer.get());
        auto v8MaybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(v8Runtime->v8Isolate, &scriptSource);
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalCompiledModule.IsEmpty()) {
            try {
                return Javet::Converter::ToExternalV8Module(jniEnv, v8Runtime->externalV8Runtime, v8Context, v8MaybeLocalCompiledModule.ToLocalChecked());
            }
            catch (const std::exception& e) {
                LOG_ERROR(e.what());
                Javet::Exceptions::ThrowJavetConverterException(jniEnv, e.what());
            }
        }
    }
    else {
        auto v8MaybeLocalCompiledScript = v8::Script::Compile(v8Context, umScript, scriptOriginPointer.get());
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalCompiledScript.IsEmpty()) {
            try {
                return Javet::Converter::ToExternalV8Script(jniEnv, v8Runtime->externalV8Runtime, v8Context, v8MaybeLocalCompiledScript.ToLocalChecked());
            }
            catch (const std::exception& e) {
                LOG_ERROR(e.what());
                Javet::Exceptions::ThrowJavetConverterException(jniEnv, e.what());
            }
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
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
    auto v8Runtime = new Javet::V8Runtime(Javet::V8Native::GlobalV8Platform.get(), Javet::NodeNative::GlobalNodeArrayBufferAllocator);
#else
    auto v8Runtime = new Javet::V8Runtime(Javet::V8Native::GlobalV8Platform.get());
#endif
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewV8Runtime);
    v8Runtime->CreateV8Isolate();
    v8Runtime->CreateV8Context(jniEnv, mRuntimeOptions);
    return TO_JAVA_LONG(v8Runtime);
}

/*
It only supports Object, Array, Function, Map, Set for now.
Error, Promise, RegExp, Proxy, Symbol, etc. are not supported.
*/
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Value
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint v8ValueType, jobject mContext) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    V8LocalValue v8LocalValueResult;
    if (IS_V8_OBJECT(v8ValueType)) {
        v8LocalValueResult = v8::Object::New(v8Context->GetIsolate());
    }
    else if (IS_V8_ARRAY(v8ValueType)) {
        v8LocalValueResult = v8::Array::New(v8Context->GetIsolate());
    }
    else if (IS_V8_ARRAY_BUFFER(v8ValueType)) {
        if (IS_JAVA_INTEGER(jniEnv, mContext)) {
            v8LocalValueResult = v8::ArrayBuffer::New(v8Context->GetIsolate(), TO_JAVA_INTEGER(jniEnv, mContext));
        }
    }
    else if (IS_V8_FUNCTION(v8ValueType)) {
        auto javetCallbackContextReferencePointer = new Javet::Callback::JavetCallbackContextReference(jniEnv, mContext);
        INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
        auto v8LocalContextHandle = v8::BigInt::New(v8Context->GetIsolate(), TO_NATIVE_INT_64(javetCallbackContextReferencePointer));
        javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer = new V8PersistentBigInt(v8Runtime->v8Isolate, v8LocalContextHandle);
        INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
        v8LocalValueResult = v8::Function::New(v8Context, Javet::Callback::JavetFunctionCallback, v8LocalContextHandle).ToLocalChecked();
        javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer->SetWeak(
            javetCallbackContextReferencePointer, Javet::Callback::JavetCloseWeakCallbackContextHandle, v8::WeakCallbackType::kParameter);
    }
    else if (IS_V8_MAP(v8ValueType)) {
        v8LocalValueResult = v8::Map::New(v8Context->GetIsolate());
    }
    else if (IS_V8_PROMISE(v8ValueType)) {
        v8LocalValueResult = v8::Promise::Resolver::New(v8Context).ToLocalChecked();
    }
    else if (IS_V8_PROXY(v8ValueType)) {
        V8LocalObject v8LocalObjectObject = mContext == nullptr
            ? v8::Object::New(v8Context->GetIsolate())
            : Javet::Converter::ToV8Value(jniEnv, v8Context, mContext).As<v8::Object>();
        auto v8LocalObjectHandler = v8::Object::New(v8Context->GetIsolate());
        v8LocalValueResult = v8::Proxy::New(v8Context, v8LocalObjectObject, v8LocalObjectHandler).ToLocalChecked();
    }
    else if (IS_V8_SET(v8ValueType)) {
        v8LocalValueResult = v8::Set::New(v8Context->GetIsolate());
    }
    else if (IS_V8_SYMBOL(v8ValueType)) {
        auto mDescription = (jstring)mContext;
        auto v8LocalValueDescription = Javet::Converter::ToV8String(jniEnv, v8Context, mDescription);
        v8LocalValueResult = v8::Symbol::New(v8Context->GetIsolate(), v8LocalValueDescription);
    }
    if (!v8LocalValueResult.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValueResult);
    }
    return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_delete
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
    if (IS_V8_ARRAY(v8ValueType)) {
        if (IS_JAVA_INTEGER(jniEnv, key)) {
            jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
            return v8LocalValue.As<v8::Array>()->Delete(v8Context, integerKey).FromMaybe(false);
        }
        else {
            return v8LocalValue.As<v8::Array>()->Delete(v8Context, v8ValueKey).FromMaybe(false);
        }
    }
    else if (IS_V8_MAP(v8ValueType)) {
        return v8LocalValue.As<v8::Map>()->Delete(v8Context, v8ValueKey).FromMaybe(false);
    }
    else if (IS_V8_SET(v8ValueType)) {
        return v8LocalValue.As<v8::Set>()->Delete(v8Context, v8ValueKey).FromMaybe(false);
    }
    else if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        if (IS_JAVA_INTEGER(jniEnv, key)) {
            jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
            return v8LocalObject->Delete(v8Context, integerKey).FromMaybe(false);
        }
        else {
            return v8LocalObject->Delete(v8Context, v8ValueKey).FromMaybe(false);
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_deletePrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->DeletePrivate(v8Context, v8LocalPrivateKey);
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_equals
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
    RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
    return v8LocalValue1->Equals(v8Context, v8LocalValue2).FromMaybe(false);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
    auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
    auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
        jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
    if (mIsModule) {
        V8ScriptCompilerSource scriptSource(umScript, *scriptOriginPointer.get());
        auto v8MaybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(v8Runtime->v8Isolate, &scriptSource);
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
        }
        else if (!v8MaybeLocalCompiledModule.IsEmpty()) {
            auto compliedModule = v8MaybeLocalCompiledModule.ToLocalChecked();
            auto maybeResult = compliedModule->InstantiateModule(v8Context, Javet::Callback::JavetModuleResolveCallback);
            if (maybeResult.FromMaybe(false)) {
                auto v8MaybeLocalValueResult = compliedModule->Evaluate(v8Context);
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
                }
                else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
                    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
                }
            }
        }
    }
    else {
        auto v8MaybeLocalCompiledScript = v8::Script::Compile(v8Context, umScript, scriptOriginPointer.get());
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
        }
        else if (!v8MaybeLocalCompiledScript.IsEmpty()) {
            auto compliedScript = v8MaybeLocalCompiledScript.ToLocalChecked();
            auto v8MaybeLocalValueResult = compliedScript->Run(v8Context);
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
            }
            else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
                return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
            }
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_get
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
    V8MaybeLocalValue v8MaybeLocalValueResult;
    if (IS_V8_ARGUMENTS(v8ValueType) || IS_V8_ARRAY(v8ValueType) || v8LocalValue->IsTypedArray()) {
        if (IS_JAVA_INTEGER(jniEnv, key)) {
            jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
            if (integerKey >= 0) {
                v8MaybeLocalValueResult = v8LocalValue.As<v8::Array>()->Get(v8Context, integerKey);
            }
        }
        else if (!v8LocalValueKey.IsEmpty()) {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Array>()->Get(v8Context, v8LocalValueKey);
        }
    }
    else if (!v8LocalValueKey.IsEmpty()) {
        if (IS_V8_SYMBOL(v8ValueType)) {
            v8LocalValue = v8LocalValue->ToObject(v8Context).ToLocalChecked();
        }
        if (IS_V8_MAP(v8ValueType)) {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Map>()->Get(v8Context, v8LocalValueKey);
        }
        else if (v8LocalValue->IsObject()) {
            auto v8LocalObject = v8LocalValue.As<v8::Object>();
            if (IS_JAVA_INTEGER(jniEnv, key)) {
                jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
                v8MaybeLocalValueResult = v8LocalObject->Get(v8Context, integerKey);
            }
            else {
                v8MaybeLocalValueResult = v8LocalObject->Get(v8Context, v8LocalValueKey);
            }
        }
    }
    if (!v8MaybeLocalValueResult.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getGlobalObject
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    return Javet::Converter::ToExternalV8ValueGlobalObject(jniEnv, v8Runtime->v8GlobalObject);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getIdentityHash
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        return v8LocalValue.As<v8::Object>()->GetIdentityHash();
    }
    return 0;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getInternalProperties
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        // This feature is not enabled yet.
        v8_inspector::V8InspectorClient v8InspectorClient;
        v8_inspector::V8InspectorImpl v8InspectorImpl(v8Runtime->v8Isolate, &v8InspectorClient);
        v8_inspector::V8Debugger v8Debugger(v8Runtime->v8Isolate, &v8InspectorImpl);
        auto v8InternalProperties = v8Debugger.internalProperties(v8Context, v8LocalValue.As<v8::Function>()).ToLocalChecked();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8InternalProperties);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jlongArray JNICALL Java_com_caoccao_javet_interop_V8Native_getInternalStatistic
(JNIEnv* jniEnv, jobject caller) {
#ifdef ENABLE_MONITOR
    return GlobalJavetNativeMonitor.GetCounters(jniEnv);
#else
    return nullptr;
#endif
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getJSFunctionType
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    using namespace Javet::Enums::JSFunctionType;
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = V8InternalJSFunction::cast(*v8::Utils::OpenHandle(*v8LocalValue));
        auto v8InternalShared = v8InternalFunction.shared();
        if (v8InternalShared.native()) {
            return JSFunctionType::Native;
        }
        else if (v8InternalShared.IsApiFunction()) {
            return JSFunctionType::API;
        }
        else if (v8InternalShared.IsUserJavaScript()) {
            return JSFunctionType::UserDefined;
        }
    }
    return JSFunctionType::Unknown;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getJSScopeType
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = V8InternalJSFunction::cast(*v8::Utils::OpenHandle(*v8LocalValue));
        auto v8InternalShared = v8InternalFunction.shared();
        auto v8InternalScopeInfo = v8InternalShared.scope_info();
        return v8InternalScopeInfo.scope_type();
    }
    return Javet::Enums::JSScopeType::Unknown;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getLength
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_ARRAY(v8ValueType)) {
        return (jint)v8LocalValue.As<v8::Array>()->Length();
    }
    if (v8LocalValue->IsTypedArray()) {
        return (jint)v8LocalValue.As<v8::TypedArray>()->Length();
    }
    return 0;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getOwnPropertyNames
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        v8LocalValue = v8LocalValue->ToObject(v8Context).ToLocalChecked();
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8MaybeLocalArray = v8LocalObject->GetOwnPropertyNames(v8Context);
        if (!v8MaybeLocalArray.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalArray.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
        auto v8MaybeLocalValue = v8LocalValue.As<v8::Object>()->GetPrivate(v8Context, v8LocalPrivateKey);
        if (!v8MaybeLocalValue.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValue.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPropertyNames
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        v8LocalValue = v8LocalValue->ToObject(v8Context).ToLocalChecked();
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalObject->GetPropertyNames(v8Context).ToLocalChecked());
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        v8LocalValue = v8LocalValue->ToObject(v8Context).ToLocalChecked();
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        V8MaybeLocalValue v8MaybeLocalValueValue;
        if (IS_JAVA_INTEGER(jniEnv, key)) {
            jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
            v8MaybeLocalValueValue = v8LocalObject->Get(v8Context, integerKey);
        }
        else {
            auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
            if (!v8ValueKey.IsEmpty()) {
                v8MaybeLocalValueValue = v8LocalObject->Get(v8Context, v8ValueKey);
            }
        }
        if (!v8MaybeLocalValueValue.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueValue.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPrototype
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        V8LocalValue v8LocalValueResult = v8LocalObject->GetPrototype();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValueResult);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getSize
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        return (jint)v8LocalValue.As<v8::Map>()->Size();
    }
    if (IS_V8_SET(v8ValueType)) {
        return (jint)v8LocalValue.As<v8::Set>()->Size();
    }
    return 0;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getSourceCode
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = V8InternalJSFunction::cast(*v8::Utils::OpenHandle(*v8LocalValue));
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScript = V8InternalScript::cast(v8InternalShared.script());
            auto v8InternalSource = V8InternalString::cast(v8InternalScript.source());
            const int startPosition = v8InternalShared.StartPosition();
            const int endPosition = v8InternalShared.EndPosition();
            auto sourceCode = v8InternalSource.ToCString(
                V8InternalAllowNullsFlag::DISALLOW_NULLS, V8InternalRobustnessFlag::ROBUST_STRING_TRAVERSAL,
                startPosition, endPosition - startPosition);
            return Javet::Converter::ToJavaString(jniEnv, sourceCode.get());
        }
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
(JNIEnv* jniEnv, jobject caller) {
    return Javet::Converter::ToJavaString(jniEnv, v8::V8::GetVersion());
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_has
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
    if (!v8LocalValueKey.IsEmpty()) {
        if (IS_V8_MAP(v8ValueType)) {
            return v8LocalValue.As<v8::Map>()->Has(v8Context, v8LocalValueKey).FromMaybe(false);
        }
        else if (IS_V8_SET(v8ValueType)) {
            return v8LocalValue.As<v8::Set>()->Has(v8Context, v8LocalValueKey).FromMaybe(false);
        }
        else if (v8LocalValue->IsObject()) {
            auto v8LocalObject = v8LocalValue.As<v8::Object>();
            if (IS_JAVA_INTEGER(jniEnv, value)) {
                jint integerKey = TO_JAVA_INTEGER(jniEnv, value);
                return v8LocalObject->Has(v8Context, integerKey).FromMaybe(false);
            }
            else {
                return v8LocalObject->Has(v8Context, v8LocalValueKey).FromMaybe(false);
            }
        }
    }
    return false;
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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasOwnProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        v8LocalValue = v8LocalValue->ToObject(v8Context).ToLocalChecked();
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        if (IS_JAVA_INTEGER(jniEnv, key)) {
            jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
            return v8LocalObject->HasOwnProperty(v8Context, integerKey).FromMaybe(false);
        }
        else if (IS_JAVA_STRING(jniEnv, key)) {
            jstring stringKey = TO_JAVA_STRING(jniEnv, key);
            auto v8ValueKey = Javet::Converter::ToV8String(jniEnv, v8Context, stringKey);
            return v8LocalObject->HasOwnProperty(v8Context, v8ValueKey).FromMaybe(false);
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->HasPrivate(v8Context, v8LocalPrivateKey);
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_idleNotificationDeadline
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong deadlineInMillis) {
    if (deadlineInMillis > 0) {
        RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
        v8Runtime->v8Isolate->IdleNotificationDeadline(((long)deadlineInMillis) / 1000.0);
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_invoke
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mFunctionName, jboolean mResultRequired, jobjectArray mValues) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        v8LocalValue = v8LocalValue->ToObject(v8Context).ToLocalChecked();
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8MaybeLocalValue = v8LocalObject->Get(v8Context, Javet::Converter::ToV8String(jniEnv, v8Context, mFunctionName));
        if (!v8MaybeLocalValue.IsEmpty()) {
            auto v8Function = v8MaybeLocalValue.ToLocalChecked();
            if (v8Function->IsFunction()) {
                V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
                V8MaybeLocalValue v8MaybeLocalValueResult;
                uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
                if (valueCount > 0) {
                    auto umValuesPointer = Javet::Converter::ToV8Values(jniEnv, v8Context, mValues);
                    v8MaybeLocalValueResult = v8Function.As<v8::Function>()->Call(v8Context, v8LocalObject, valueCount, umValuesPointer.get());
                }
                else {
                    v8MaybeLocalValueResult = v8Function.As<v8::Function>()->Call(v8Context, v8LocalObject, 0, nullptr);
                }
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
                }
                else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
                    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
                }
            }
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
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
    v8Runtime->v8Isolate->LowMemoryNotification();
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleEvaluate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mResultRequired) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalModule->GetStatus() == v8::Module::Status::kInstantiated) {
        V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
        auto v8MaybeLocalValueResult = v8LocalModule->Evaluate(v8Context);
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalModule->GetStatus() == v8::Module::Status::kErrored) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalModule->GetException());
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetNamespace
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalModule->GetModuleNamespace());
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetScriptId
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return (jint)v8LocalModule->ScriptId();
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetStatus
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return (jint)v8LocalModule->GetStatus();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_moduleInstantiate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalModule->GetStatus() == v8::Module::Status::kUninstantiated) {
        return v8LocalModule->InstantiateModule(v8Context, Javet::Callback::JavetModuleResolveCallback).FromMaybe(false);
    }
    return false;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetState
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        return (jint)v8LocalValue.As<v8::Promise>()->State();
    }
    return -1;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseCatch
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueFunctionHandle) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8PersistentFunctionPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionHandle);
        auto v8LocalFunction = v8PersistentFunctionPointer->Get(v8Context->GetIsolate());
        auto v8MaybeLocalValueResult = v8LocalValue.As<v8::Promise>()->Catch(v8Context, v8LocalFunction);
        if (!v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetResult
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromise = v8LocalValue.As<v8::Promise>();
        if (v8LocalPromise->State() != v8::Promise::PromiseState::kPending) {
            auto v8ValueResult = v8LocalPromise->Result();
            if (!v8ValueResult.IsEmpty()) {
                return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8ValueResult);
            }
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseHasHandler
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        return v8LocalValue.As<v8::Promise>()->HasHandler();
    }
    return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_promiseMarkAsHandled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        v8LocalValue.As<v8::Promise>()->MarkAsHandled();
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseThen
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueFunctionFulfilledHandle, jlong v8ValueFunctionRejectedHandle) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8PersistentFunctionFulfilledPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionFulfilledHandle);
        auto v8LocalFunctionFulfilled = v8PersistentFunctionFulfilledPointer->Get(v8Context->GetIsolate());
        V8MaybeLocalPromise v8MaybeLocalValueResult;
        if (v8ValueFunctionRejectedHandle == 0L) {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Promise>()->Then(v8Context, v8LocalFunctionFulfilled);
        }
        else {
            auto v8PersistentFunctionRejectedPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionRejectedHandle);
            auto v8LocalFunctionRejected = v8PersistentFunctionRejectedPointer->Get(v8Context->GetIsolate());
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Promise>()->Then(v8Context, v8LocalFunctionFulfilled, v8LocalFunctionRejected);
        }
        if (!v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetPromise
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromiseResolver = v8LocalValue.As<v8::Promise::Resolver>();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalPromiseResolver->GetPromise());
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseReject
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE_WITH_UNIQUE_LOCKER(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromiseResolver = v8LocalValue.As<v8::Promise::Resolver>();
        auto v8MaybeBool = v8LocalPromiseResolver->Reject(v8Context, Javet::Converter::ToV8Value(jniEnv, v8Context, value));
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseResolve
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE_WITH_UNIQUE_LOCKER(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromiseResolver = v8LocalValue.As<v8::Promise::Resolver>();
        return v8LocalPromiseResolver->Resolve(v8Context, Javet::Converter::ToV8Value(jniEnv, v8Context, value)).ToChecked();
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_proxyGetHandler
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValue.As<v8::Proxy>()->GetHandler());
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_proxyGetTarget
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValue.As<v8::Proxy>()->GetTarget());
    }
    return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_proxyIsRevoked
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        return v8LocalValue.As<v8::Proxy>()->IsRevoked();
    }
    return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_proxyRevoke
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        v8LocalValue.As<v8::Proxy>()->Revoke();
    }
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerV8Runtime
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mV8Runtime) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    if (v8Runtime->externalV8Runtime != nullptr) {
        jniEnv->DeleteGlobalRef(v8Runtime->externalV8Runtime);
        INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
    }
    v8Runtime->externalV8Runtime = jniEnv->NewGlobalRef(mV8Runtime);
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeJNIGlobalRef
(JNIEnv* jniEnv, jobject caller, jlong handle) {
    jniEnv->DeleteGlobalRef((jobject)handle);
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
(JNIEnv* jniEnv, jobject caller, jlong referenceHandle, jint referenceType) {
    auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(referenceHandle);
    v8PersistentDataPointer->Reset();
    delete v8PersistentDataPointer;
    INCREASE_COUNTER(Javet::Monitor::CounterType::DeletePersistentReference);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_requestGarbageCollectionForTesting
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean fullGC) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    v8Runtime->v8Isolate->RequestGarbageCollectionForTesting(fullGC
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
    v8Runtime->CreateV8Isolate();
    v8Runtime->CreateV8Context(jniEnv, mRuntimeOptions);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_sameValue
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
    RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
    return v8LocalValue1->SameValue(v8LocalValue2);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptRun
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mResultRequired) {
    RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (!v8LocalScript.IsEmpty()) {
        V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
        auto v8MaybeLocalValueResult = v8LocalScript->Run(v8Context);
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_set
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
    auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
    if (IS_V8_ARRAY(v8ValueType)) {
        if (IS_JAVA_INTEGER(jniEnv, key)) {
            jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
            return v8LocalValue.As<v8::Array>()->Set(v8Context, integerKey, v8ValueValue).FromMaybe(false);
        }
        else if (!v8ValueKey.IsEmpty()) {
            return v8LocalValue.As<v8::Array>()->Set(v8Context, v8ValueKey, v8ValueValue).FromMaybe(false);
        }
    }
    else if (!v8ValueKey.IsEmpty()) {
        if (IS_V8_MAP(v8ValueType)) {
            auto unusedSet = v8LocalValue.As<v8::Map>()->Set(v8Context, v8ValueKey, v8ValueValue);
            return true;
        }
        else if (v8LocalValue->IsObject()) {
            auto v8LocalObject = v8LocalValue.As<v8::Object>();
            if (IS_JAVA_INTEGER(jniEnv, key)) {
                jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
                return v8LocalObject->Set(v8Context, integerKey, v8ValueValue).FromMaybe(false);
            }
            else {
                return v8LocalObject->Set(v8Context, v8ValueKey, v8ValueValue).FromMaybe(false);
            }
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setAccessor
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mPropertyName, jobject mContextGetter, jobject mContextSetter) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        V8LocalName v8LocalName;
        if (IS_JAVA_STRING(jniEnv, mPropertyName)) {
            v8LocalName = Javet::Converter::ToV8Value(jniEnv, v8Context, mPropertyName).As<v8::String>();
        }
        else if (IS_JAVA_SYMBOL(jniEnv, mPropertyName)) {
            v8LocalName = Javet::Converter::ToV8Value(jniEnv, v8Context, mPropertyName).As<v8::Symbol>();
        }
        else {
            return false;
        }
        if (mContextGetter == nullptr) {
            return v8LocalObject.As<v8::Object>()->SetAccessor(v8Context, v8LocalName, nullptr).ToChecked();
        }
        else {
            auto v8LocalArrayContext = v8::Array::New(v8Runtime->v8Isolate, 2);
            auto javetCallbackContextReferencePointer = new Javet::Callback::JavetCallbackContextReference(jniEnv, mContextGetter);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
            auto v8LocalContextGetterHandle = v8::BigInt::New(v8Context->GetIsolate(), TO_NATIVE_INT_64(javetCallbackContextReferencePointer));
            javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer =
                new V8PersistentBigInt(v8Runtime->v8Isolate, v8LocalContextGetterHandle);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
            javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer->SetWeak(
                javetCallbackContextReferencePointer, Javet::Callback::JavetCloseWeakCallbackContextHandle, v8::WeakCallbackType::kParameter);
            auto maybeResult = v8LocalArrayContext->Set(v8Context, 0, v8LocalContextGetterHandle);
            v8::AccessorNameGetterCallback getter = Javet::Callback::JavetPropertyGetterCallback;
            v8::AccessorNameSetterCallback setter = nullptr;
            if (mContextSetter != nullptr) {
                javetCallbackContextReferencePointer = new Javet::Callback::JavetCallbackContextReference(jniEnv, mContextSetter);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
                auto v8LocalContextSetterHandle = v8::BigInt::New(v8Context->GetIsolate(), TO_NATIVE_INT_64(javetCallbackContextReferencePointer));
                javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer =
                    new V8PersistentBigInt(v8Runtime->v8Isolate, v8LocalContextSetterHandle);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
                javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer->SetWeak(
                    javetCallbackContextReferencePointer, Javet::Callback::JavetCloseWeakCallbackContextHandle, v8::WeakCallbackType::kParameter);
                maybeResult = v8LocalArrayContext->Set(v8Context, 1, v8LocalContextSetterHandle);
                setter = Javet::Callback::JavetPropertySetterCallback;
            }
            return v8LocalObject.As<v8::Object>()->SetAccessor(v8Context, v8LocalName, getter, setter, v8LocalArrayContext).ToChecked();
        }
    }
    return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setFlags
(JNIEnv* jniEnv, jobject caller, jstring flags) {
    if (flags) {
        char const* str = jniEnv->GetStringUTFChars(flags, nullptr);
        v8::V8::SetFlagsFromString(str, jniEnv->GetStringUTFLength(flags));
        jniEnv->ReleaseStringUTFChars(flags, str);
        v8::V8::Initialize();
    }
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey, jobject mValue) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
        auto v8LocalPrivateValue = Javet::Converter::ToV8Value(jniEnv, v8Context, mValue);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->SetPrivate(v8Context, v8LocalPrivateKey, v8LocalPrivateValue);
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
        if (IS_JAVA_INTEGER(jniEnv, key)) {
            jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
            return v8LocalObject->Set(v8Context, integerKey, v8ValueValue).FromMaybe(false);
        }
        else {
            auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
            if (!v8ValueKey.IsEmpty()) {
                return v8LocalObject->Set(v8Context, v8ValueKey, v8ValueValue).FromMaybe(false);
            }
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setPrototype
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueHandlePrototype) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8PersistentObjectPrototypePointer = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandlePrototype);
        auto v8LocalObjectPrototype = v8PersistentObjectPrototypePointer->Get(v8Context->GetIsolate());
        return v8LocalObject->SetPrototype(v8Context, v8LocalObjectPrototype).ToChecked();
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setSourceCode
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mSourceCode) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = V8InternalJSFunction::cast(*v8::Utils::OpenHandle(*v8LocalValue));
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScopeInfo = v8InternalShared.scope_info();
            if (v8InternalScopeInfo.scope_type() == V8InternalScopeType::FUNCTION_SCOPE && v8InternalScopeInfo.HasPositionInfo()) {
                auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
                auto v8InternalScript = V8InternalScript::cast(v8InternalShared.script());
                auto v8InternalSource = V8InternalString::cast(v8InternalScript.source());
                const int startPosition = v8InternalShared.StartPosition();
                const int endPosition = v8InternalShared.EndPosition();
                const int sourceLength = v8InternalSource.length();

                // Build the new source code.
                auto umSourceCode = Javet::Converter::ToV8String(jniEnv, v8Context, mSourceCode);
                V8LocalString newSourceCode;
                if (startPosition > 0) {
                    int utf8Length = 0;
                    auto stdStringHeader(v8InternalSource.ToCString(
                        V8InternalAllowNullsFlag::DISALLOW_NULLS, V8InternalRobustnessFlag::ROBUST_STRING_TRAVERSAL,
                        0, startPosition, &utf8Length));
                    auto v8LocalStringHeader = v8::String::NewFromUtf8(
                        v8Context->GetIsolate(), stdStringHeader.get(), v8::NewStringType::kNormal, utf8Length).ToLocalChecked();
                    newSourceCode = v8LocalStringHeader;
                }
                if (newSourceCode.IsEmpty()) {
                    newSourceCode = umSourceCode;
                }
                else {
                    newSourceCode = v8::String::Concat(v8Runtime->v8Isolate, newSourceCode, umSourceCode);
                }
                if (endPosition < sourceLength) {
                    int utf8Length = 0;
                    auto stdStringFooter(v8InternalSource.ToCString(
                        V8InternalAllowNullsFlag::DISALLOW_NULLS, V8InternalRobustnessFlag::ROBUST_STRING_TRAVERSAL,
                        endPosition, sourceLength, &utf8Length));
                    auto v8LocalStringFooter = v8::String::NewFromUtf8(
                        v8Context->GetIsolate(), stdStringFooter.get(), v8::NewStringType::kNormal, utf8Length).ToLocalChecked();
                    if (newSourceCode.IsEmpty()) {
                        newSourceCode = v8LocalStringFooter;
                    }
                    else {
                        newSourceCode = v8::String::Concat(v8Runtime->v8Isolate, newSourceCode, v8LocalStringFooter);
                    }
                }

                // Discard compiled data and set lazy compile.
                if (v8InternalShared.CanDiscardCompiled() && v8InternalShared.is_compiled()) {
                    V8InternalSharedFunctionInfo::DiscardCompiled(v8InternalIsolate, v8::internal::handle(v8InternalShared, v8InternalIsolate));
                    v8InternalFunction.set_code(v8InternalIsolate->builtins()->code(V8InternalBuiltin::kCompileLazy));
                }

                /*
                 * Set the source and update the start and end position.
                 * Note: The source code is shared among all script objects, but position info is not.
                 * So the caller is responsible for restoring the original source code,
                 * otherwise the next script execution will likely fail because the position info
                 * of the next script is incorrect.
                 */
                v8InternalScript.set_source(*v8::Utils::OpenHandle(*newSourceCode), V8InternalWriteBarrierMode::UPDATE_WRITE_BARRIER);
                const int newEndPosition = startPosition + umSourceCode->Length();
                v8InternalShared.scope_info().SetPositionInfo(startPosition, newEndPosition);
                return true;
            }
        }
    }
    return false;
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

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toProtoString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8MaybeLocalString v8MaybeLocalString;
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        v8MaybeLocalString = v8LocalObject->ObjectProtoToString(v8Context);
    }
    V8LocalString v8LocalString = v8MaybeLocalString.IsEmpty() ? V8LocalString() : v8MaybeLocalString.ToLocalChecked();
    return Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalString);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8MaybeLocalString v8MaybeLocalString;
    if (!IS_V8_MODULE(v8ValueType) && !IS_V8_SCRIPT(v8ValueType)) {
        if (IS_V8_ARRAY(v8ValueType)) {
            v8MaybeLocalString = v8LocalValue.As<v8::Array>()->ToString(v8Context);
        }
        else if (IS_V8_MAP(v8ValueType)) {
            v8MaybeLocalString = v8LocalValue.As<v8::Map>()->ToString(v8Context);
        }
        else if (IS_V8_SET(v8ValueType)) {
            v8MaybeLocalString = v8LocalValue.As<v8::Set>()->ToString(v8Context);
        }
        else if (v8LocalValue->IsObject()) {
            v8MaybeLocalString = v8LocalValue.As<v8::Object>()->ToString(v8Context);
        }
        else {
            auto v8MaybeLocalObject = v8LocalValue->ToObject(v8Context);
            if (!v8MaybeLocalObject.IsEmpty()) {
                v8MaybeLocalString = v8MaybeLocalObject.ToLocalChecked()->ToString(v8Context);
            }
        }
    }
    V8LocalString v8LocalString = v8MaybeLocalString.IsEmpty() ? V8LocalString() : v8MaybeLocalString.ToLocalChecked();
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

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorSend
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mMessage) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    char const* umMessage = jniEnv->GetStringUTFChars(mMessage, nullptr);
    std::string message(umMessage, jniEnv->GetStringUTFLength(mMessage));
    v8Runtime->v8Inspector->send(message);
    jniEnv->ReleaseStringUTFChars(mMessage, umMessage);
}

