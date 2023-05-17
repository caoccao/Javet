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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCall
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mReceiver, jboolean mResultRequired, jobjectArray mValues) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsFunction()) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
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
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCallAsConstructor
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray mValues) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsFunction()) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
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
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        else if (!v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionCanDiscardCompiled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            return v8InternalShared.CanDiscardCompiled();
        }
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCompile
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM,
    jobjectArray mArguments, jobjectArray mContextExtensions) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    V8TryCatch v8TryCatch(v8Context->GetIsolate());
    auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
    jboolean isModule = false;
    auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
        jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, isModule);
    size_t argumentCount = 0;
    size_t contextExtensionCount = 0;
    std::unique_ptr<V8LocalString[]> argumentsPointer;
    std::unique_ptr<V8LocalObject[]> contextExtensionsPointer;
    if (mArguments != nullptr) {
        argumentCount = jniEnv->GetArrayLength(mArguments);
        if (argumentCount > 0) {
            argumentsPointer = Javet::Converter::ToV8Strings(jniEnv, v8Context, mArguments);
        }
    }
    if (mContextExtensions != nullptr) {
        contextExtensionCount = jniEnv->GetArrayLength(mContextExtensions);
        if (contextExtensionCount > 0) {
            contextExtensionsPointer = Javet::Converter::ToV8Objects(jniEnv, v8Context, mContextExtensions);
        }
    }
    v8::MaybeLocal<v8::Function> v8MaybeLocalFunction;
    if (mCachedData) {
        V8ScriptCompilerSource scriptSource(
            umScript, *scriptOriginPointer.get(), Javet::Converter::ToCachedDataPointer(jniEnv, mCachedData));
        v8MaybeLocalFunction = v8::ScriptCompiler::CompileFunction(
            v8Context, &scriptSource,
            argumentCount, argumentsPointer.get(),
            contextExtensionCount, contextExtensionsPointer.get(),
            v8::ScriptCompiler::kConsumeCodeCache);
        LOG_DEBUG("Function cache is " << (scriptSource.GetCachedData()->rejected ? "rejected" : "accepted") << ".");
    }
    else {
        V8ScriptCompilerSource scriptSource(umScript, *scriptOriginPointer.get());
        v8MaybeLocalFunction = v8::ScriptCompiler::CompileFunction(
            v8Context, &scriptSource,
            argumentCount, argumentsPointer.get(),
            contextExtensionCount, contextExtensionsPointer.get());
    }
    if (v8TryCatch.HasCaught()) {
        return Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Runtime, v8Context, v8TryCatch);
    }
    else if (!v8MaybeLocalFunction.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalFunction.ToLocalChecked());
    }
    return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionCopyScopeInfoFrom
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle,
    jlong targetV8ValueHandle, jint targetV8ValueType,
    jlong sourceV8ValueHandle, jint sourceV8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, targetV8ValueHandle);
    jboolean success = false;
    if (IS_V8_FUNCTION(targetV8ValueType) && IS_V8_FUNCTION(sourceV8ValueType)) {
        auto sourceV8PersistentValuePointer = TO_V8_PERSISTENT_VALUE_POINTER(sourceV8ValueHandle);
        auto sourceV8LocalValue = sourceV8PersistentValuePointer->Get(v8Context->GetIsolate());
        auto targetV8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto sourceV8InternalFunction = Javet::Converter::ToV8InternalJSFunction(sourceV8LocalValue);
        auto targetV8InternalShared = targetV8InternalFunction.shared();
        auto sourceV8InternalShared = sourceV8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(sourceV8InternalShared) && IS_USER_DEFINED_FUNCTION(targetV8InternalShared)) {
            V8InternalDisallowGarbageCollection disallowGarbageCollection;
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            // Clone the shared function info
            targetV8InternalShared = *v8InternalIsolate->factory()->CloneSharedFunctionInfo(
                v8::internal::Handle(sourceV8InternalShared, v8InternalIsolate));
            // Clone the scope info
            auto sourceScopeInfo = sourceV8InternalShared.scope_info();
            auto emptyBlocklistHandle = V8InternalStringSet::New(v8InternalIsolate);
            auto targetScopeInfo = *V8InternalScopeInfo::RecreateWithBlockList(
                v8InternalIsolate, v8::internal::Handle(sourceScopeInfo, v8InternalIsolate), emptyBlocklistHandle);
            targetV8InternalShared.set_raw_scope_info(targetScopeInfo);
            targetV8InternalFunction.set_shared(targetV8InternalShared, V8InternalWriteBarrierMode::UPDATE_WRITE_BARRIER);
            success = true;
        }
    }
    return success;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mCallbackContext) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto javetCallbackContextReferencePointer =
        new Javet::Callback::JavetCallbackContextReference(jniEnv, mCallbackContext);
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
    auto v8LocalContextHandle =
        v8::BigInt::New(v8Context->GetIsolate(), TO_NATIVE_INT_64(javetCallbackContextReferencePointer));
    javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer =
        new V8PersistentBigInt(v8Context->GetIsolate(), v8LocalContextHandle);
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
    auto v8MaybeLocalFunction =
        v8::Function::New(v8Context, Javet::Callback::JavetFunctionCallback, v8LocalContextHandle);
    if (v8MaybeLocalFunction.IsEmpty()) {
        if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context, "Function allocation failed")) {
            return nullptr;
        }
    }
    else {
        auto v8LocalFunction = v8MaybeLocalFunction.ToLocalChecked();
        if (!v8LocalFunction.IsEmpty()) {
            javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer->SetWeak(
                javetCallbackContextReferencePointer,
                Javet::Callback::JavetCloseWeakCallbackContextHandle,
                v8::WeakCallbackType::kParameter);
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalFunction);
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionDiscardCompiled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            if (v8InternalShared.CanDiscardCompiled()) {
                auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
                V8InternalSharedFunctionInfo::DiscardCompiled(v8InternalIsolate, v8::internal::Handle(v8InternalShared, v8InternalIsolate));
                return true;
            }
        }
    }
    return false;
}

JNIEXPORT jobjectArray JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetArguments
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared) && v8InternalShared.is_wrapped()) {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            auto v8InternalScript = V8InternalScript::cast(v8InternalShared.script());
            auto wrappedArguments = v8InternalScript.wrapped_arguments();
            auto length = wrappedArguments.length();
            if (length > 0) {
                jobjectArray arguments = jniEnv->NewObjectArray(length, Javet::Converter::jclassString, nullptr);
                for (int i = 0; i < length; ++i) {
                    auto v8InternalObjectHandle = v8::internal::Handle(wrappedArguments.get(i), v8InternalIsolate);
                    auto v8LocalString = v8::Utils::ToLocal(v8InternalObjectHandle).As<v8::String>();
                    jstring argument = Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalString);
                    jniEnv->SetObjectArrayElement(arguments, i, argument);
                }
                return arguments;
            }
        }
    }
    return nullptr;
}

JNIEXPORT jbyteArray JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetCachedData
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    if (IS_V8_FUNCTION(v8ValueType)) {
        RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScript = V8InternalScript::cast(v8InternalShared.script());
            if (v8InternalScript.is_wrapped()) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                std::unique_ptr<V8ScriptCompilerCachedData> cachedDataPointer;
                cachedDataPointer.reset(v8::ScriptCompiler::CreateCodeCacheForFunction(v8LocalValue.As<v8::Function>()));
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                    return nullptr;
                }
                if (cachedDataPointer) {
                    return Javet::Converter::ToJavaByteArray(jniEnv, cachedDataPointer.get());
                }
            }
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetContext
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        if (v8InternalFunction.has_context()) {
            auto v8InternalContextHandle = v8::internal::Handle(v8InternalFunction.context(), v8InternalIsolate);
            auto v8LocalContext = v8::Utils::ToLocal(v8InternalContextHandle);
            return Javet::Converter::ToExternalV8Context(jniEnv, v8Runtime, v8Context, v8LocalContext);
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetInternalProperties
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        // This feature is not enabled yet.
        v8_inspector::V8InspectorClient v8InspectorClient;
        v8_inspector::V8InspectorImpl v8InspectorImpl(v8Context->GetIsolate(), &v8InspectorClient);
        v8_inspector::V8Debugger v8Debugger(v8Context->GetIsolate(), &v8InspectorImpl);
        auto v8MaybeLocalArray = v8Debugger.internalProperties(v8Context, v8LocalValue.As<v8::Function>());
        if (v8MaybeLocalArray.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalArray.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetJSFunctionType
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    using namespace Javet::Enums::JSFunctionType;
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
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

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetJSScopeType
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        auto v8InternalScopeInfo = v8InternalShared.scope_info();
        return v8InternalScopeInfo.scope_type();
    }
    return Javet::Enums::JSScopeType::Unknown;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetScopeInfos
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType,
    jboolean includeGlobalVariables, jboolean includeScopeTypeGlobal) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8LocalArray v8LocalArray = v8::Array::New(v8Context->GetIsolate());
    if (IS_V8_FUNCTION(v8ValueType)) {
        V8InternalDisallowGarbageCollection disallowGarbageCollection;
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        auto v8InternalScopeInfo = v8InternalShared.scope_info();
        V8InternalScopeIterator scopeIterator(v8InternalIsolate, v8::internal::Handle(v8InternalFunction, v8InternalIsolate));
        uint32_t index = 0;
        for (; !scopeIterator.Done(); scopeIterator.Next()) {
            auto type = scopeIterator.Type();
            if (!includeScopeTypeGlobal && type == V8InternalScopeIterator::ScopeTypeGlobal) {
                continue;
            }
            V8LocalArray innerV8LocalArray = v8::Array::New(v8Context->GetIsolate(), INDEX_SCOPE_SIZE);
            auto mode = includeGlobalVariables ? V8InternalScopeIterator::Mode::ALL : V8InternalScopeIterator::Mode::STACK;
            auto scopeObject = scopeIterator.ScopeObject(mode);
            auto v8LocalScopeObject = v8::Utils::ToLocal(scopeObject);
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_TYPE, Javet::Converter::ToV8Integer(v8Context, (int)type)).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_OBJECT, v8LocalScopeObject).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_HAS_CONTEXT, Javet::Converter::ToV8Boolean(v8Context, scopeIterator.HasContext())).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_START_POSITION, Javet::Converter::ToV8Integer(v8Context, scopeIterator.start_position())).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_END_POSITION, Javet::Converter::ToV8Integer(v8Context, scopeIterator.end_position())).ToChecked();
            v8LocalArray->Set(v8Context, index, innerV8LocalArray).ToChecked();
            ++index;
        }
    }
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalArray);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetScriptSource
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        V8InternalDisallowGarbageCollection disallowGarbageCollection;
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScript = V8InternalScript::cast(v8InternalShared.script());
            auto v8InternalSource = V8InternalString::cast(v8InternalScript.source());
            const int startPosition = v8InternalShared.StartPosition();
            const int endPosition = v8InternalShared.EndPosition();
            const int sourceLength = v8InternalSource.length();
            auto sourceCode = v8InternalSource.ToCString(
                V8InternalAllowNullsFlag::DISALLOW_NULLS, V8InternalRobustnessFlag::ROBUST_STRING_TRAVERSAL,
                0, sourceLength);
            return jniEnv->NewObject(
                Javet::Converter::jclassIV8ValueFunctionScriptSource,
                Javet::Converter::jmethodIDIV8ValueFunctionScriptSourceConstructor,
                Javet::Converter::ToJavaString(jniEnv, sourceCode.get()),
                startPosition,
                endPosition);
        }
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetSourceCode
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionIsCompiled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            return v8InternalShared.is_compiled();
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionIsWrapped
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            return v8InternalShared.is_wrapped();
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetContext
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mV8ContextValue) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    jboolean success = false;
    if (IS_V8_FUNCTION(v8ValueType)) {
        V8InternalDisallowGarbageCollection disallowGarbageCollection;
        V8LocalContext v8ContextValue = Javet::Converter::ToV8Context(jniEnv, v8Context, mV8ContextValue);
        V8InternalContext v8InternalContext = Javet::Converter::ToV8InternalContext(v8ContextValue);
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        v8InternalFunction.set_context(v8InternalContext);
        success = true;
    }
    return success;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetScriptSource
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mScriptSource, jboolean mCloneScript) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    jboolean success = false;
    if (IS_V8_FUNCTION(v8ValueType)) {
        V8InternalDisallowGarbageCollection disallowGarbageCollection;
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScopeInfo = v8InternalShared.scope_info();
            if (v8InternalScopeInfo.scope_type() == V8InternalScopeType::FUNCTION_SCOPE) {
                auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
                auto mSourceCode = (jstring)jniEnv->CallObjectMethod(mScriptSource, Javet::Converter::jmethodIDIV8ValueFunctionScriptGetCode);
                auto umSourceCode = Javet::Converter::ToV8String(jniEnv, v8Context, mSourceCode);
                const int startPosition = jniEnv->CallIntMethod(mScriptSource, Javet::Converter::jmethodIDIV8ValueFunctionScriptGetStartPosition);
                const int endPosition = jniEnv->CallIntMethod(mScriptSource, Javet::Converter::jmethodIDIV8ValueFunctionScriptGetEndPosition);
                auto v8InternalScript = V8InternalScript::cast(v8InternalShared.script());
                auto v8InternalSource = v8::Utils::OpenHandle(*umSourceCode);
                bool sourceCodeEquals = v8InternalScript.source().StrictEquals(*v8InternalSource);
                bool positionEquals = startPosition == v8InternalShared.StartPosition() && endPosition == v8InternalShared.EndPosition();
                if (!sourceCodeEquals || !positionEquals) {
                    if (v8InternalShared.CanDiscardCompiled()) {
                        V8InternalSharedFunctionInfo::DiscardCompiled(v8InternalIsolate, v8::internal::Handle(v8InternalShared, v8InternalIsolate));
                        v8InternalShared.set_allows_lazy_compilation(true);
                    }
                    if (!sourceCodeEquals) {
                        if (mCloneScript) {
                            auto clonedV8InternalScript = v8InternalIsolate->factory()->CloneScript(v8::internal::Handle(v8InternalScript, v8InternalIsolate));
                            clonedV8InternalScript->set_source(*v8InternalSource, V8InternalWriteBarrierMode::UPDATE_WRITE_BARRIER);
                            v8InternalShared.set_script(*clonedV8InternalScript);
                        }
                        else {
                            v8InternalScript.set_source(*v8InternalSource, V8InternalWriteBarrierMode::UPDATE_WRITE_BARRIER);
                        }
                    }
                    if (!positionEquals) {
                        v8InternalScopeInfo.SetPositionInfo(startPosition, endPosition);
                    }
                    success = true;
                }
                DELETE_LOCAL_REF(jniEnv, mSourceCode);
            }
        }
    }
    return success;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetSourceCode
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mSourceCode, jboolean mCloneScript) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    jboolean success = false;
    if (IS_V8_FUNCTION(v8ValueType)) {
        V8InternalDisallowGarbageCollection disallowGarbageCollection;
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction.shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScopeInfo = v8InternalShared.scope_info();
            while (v8InternalScopeInfo.scope_type() == V8InternalScopeType::FUNCTION_SCOPE) {
                auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
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
                    auto v8MaybeLocalStringHeader = v8::String::NewFromUtf8(
                        v8Context->GetIsolate(), stdStringHeader.get(), v8::NewStringType::kNormal, utf8Length);
                    if (v8MaybeLocalStringHeader.IsEmpty()) {
                        Javet::Exceptions::HandlePendingException(
                            jniEnv, v8Runtime, v8Context, "header could not be extracted from the source code");
                        break;
                    }
                    newSourceCode = v8MaybeLocalStringHeader.ToLocalChecked();
                }
                if (newSourceCode.IsEmpty()) {
                    newSourceCode = umSourceCode;
                }
                else {
                    newSourceCode = v8::String::Concat(v8Context->GetIsolate(), newSourceCode, umSourceCode);
                }
                if (endPosition < sourceLength) {
                    int utf8Length = 0;
                    auto stdStringFooter(v8InternalSource.ToCString(
                        V8InternalAllowNullsFlag::DISALLOW_NULLS, V8InternalRobustnessFlag::ROBUST_STRING_TRAVERSAL,
                        endPosition, sourceLength - endPosition, &utf8Length));
                    auto v8MaybeLocalStringFooter = v8::String::NewFromUtf8(
                        v8Context->GetIsolate(), stdStringFooter.get(), v8::NewStringType::kNormal, utf8Length);
                    if (v8MaybeLocalStringFooter.IsEmpty()) {
                        Javet::Exceptions::HandlePendingException(
                            jniEnv, v8Runtime, v8Context, "footer could not be extracted from the source code");
                        break;
                    }
                    auto v8LocalStringFooter = v8MaybeLocalStringFooter.ToLocalChecked();
                    if (newSourceCode.IsEmpty()) {
                        newSourceCode = v8LocalStringFooter;
                    }
                    else {
                        newSourceCode = v8::String::Concat(v8Context->GetIsolate(), newSourceCode, v8LocalStringFooter);
                    }
                }

                /*
                 * Set the source and update the start and end position.
                 * Note: The source code is shared among all script objects, but position info is not.
                 * So the caller is responsible for restoring the original source code,
                 * otherwise the next script execution will likely fail because the position info
                 * of the next script is incorrect.
                 */
                const int newSourceLength = umSourceCode->Length();
                const int newEndPosition = startPosition + newSourceLength;

                auto newV8InternalSource = v8::Utils::OpenHandle(*newSourceCode);
                bool sourceCodeEquals = v8InternalSource.StrictEquals(*newV8InternalSource);
                bool positionEquals = newEndPosition == v8InternalShared.EndPosition();

                if (!sourceCodeEquals || !positionEquals) {
                    // Discard compiled data and set lazy compile.
                    if (v8InternalShared.CanDiscardCompiled()) {
                        V8InternalSharedFunctionInfo::DiscardCompiled(v8InternalIsolate, v8::internal::Handle(v8InternalShared, v8InternalIsolate));
                        v8InternalShared.set_allows_lazy_compilation(true);
                    }
                    if (!sourceCodeEquals) {
                        if (mCloneScript) {
                            auto clonedV8InternalScript = v8InternalIsolate->factory()->CloneScript(v8::internal::Handle(v8InternalScript, v8InternalIsolate));
                            clonedV8InternalScript->set_source(*newV8InternalSource, V8InternalWriteBarrierMode::UPDATE_WRITE_BARRIER);
                            v8InternalShared.set_script(*clonedV8InternalScript);
                        }
                        else {
                            v8InternalScript.set_source(*newV8InternalSource, V8InternalWriteBarrierMode::UPDATE_WRITE_BARRIER);
                        }
                    }
                    if (!positionEquals) {
                        v8InternalScopeInfo.SetPositionInfo(startPosition, newEndPosition);
                    }
                    success = true;
                }
                break;
            }
        }
    }
    return success;
}
