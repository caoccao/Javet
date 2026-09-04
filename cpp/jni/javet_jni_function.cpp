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

#include <string_view>
#include "javet_jni.h"
#include "javet_jni_compiler.h"

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCall
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mReceiver, jint receiverType, jboolean mResultRequired, jobjectArray mValues, jintArray valueTypes) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsFunction()) {
        V8TryCatch v8TryCatch(v8Isolate);
        V8MaybeLocalValue v8MaybeLocalValueResult;
        auto umReceiver = Javet::Converter::ToV8Value(
            jniEnv,
            v8Isolate,
            v8Context,
            mReceiver,
            receiverType);
        uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
        if (valueCount > 0) {
            auto v8Values = Javet::Converter::ToV8Values(
                jniEnv,
                v8Isolate,
                v8Context,
                mValues,
                valueTypes);
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->Call(
                v8Context,
                umReceiver,
                valueCount,
                v8Values.empty() ? nullptr : v8Values.data());
        }
        else {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->Call(v8Context, umReceiver, 0, nullptr);
        }
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCallAsConstructor
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray mValues, jintArray valueTypes) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsFunction()) {
        V8TryCatch v8TryCatch(v8Isolate);
        V8MaybeLocalValue v8MaybeLocalValueResult;
        uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
        if (valueCount > 0) {
            auto v8Values = Javet::Converter::ToV8Values(
                jniEnv,
                v8Isolate,
                v8Context,
                mValues,
                valueTypes);
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->CallAsConstructor(
                v8Context,
                valueCount,
                v8Values.empty() ? nullptr : v8Values.data());
        }
        else {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Function>()->CallAsConstructor(v8Context, 0, nullptr);
        }
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        else if (!v8MaybeLocalValueResult.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionCanDiscardCompiled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction->shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            return v8InternalShared->CanDiscardCompiled();
        }
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCompile
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM,
    jobjectArray mArguments, jobjectArray mContextExtensions, jintArray contextExtensionTypes) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    const auto compileResult = Javet::Compiler::compileFunction(
        jniEnv,
        v8Runtime,
        v8Context,
        mScript,
        mCachedData,
        mResourceName,
        mResourceLineOffset,
        mResourceColumnOffset,
        mScriptId,
        mIsWASM,
        mArguments,
        mContextExtensions,
        contextExtensionTypes);
    return Javet::Compiler::toExternal(
        jniEnv,
        v8Runtime,
        v8Context,
        compileResult);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionCopyScopeInfoFrom
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle,
    jlong targetV8ValueHandle, jint targetV8ValueType,
    jlong sourceV8ValueHandle, jint sourceV8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, targetV8ValueHandle);
    jboolean success = false;
    if (IS_V8_FUNCTION(targetV8ValueType) && IS_V8_FUNCTION(sourceV8ValueType)) {
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Isolate);
        auto sourceV8PersistentValuePointer = TO_V8_PERSISTENT_VALUE_POINTER(sourceV8ValueHandle);
        auto sourceV8LocalValue = sourceV8PersistentValuePointer->Get(v8Isolate);
        auto targetV8InternalFunction = v8::internal::Cast<V8InternalJSFunction>(
            v8::Utils::OpenHandle(*v8LocalValue));
        auto sourceV8InternalFunction = v8::internal::Cast<V8InternalJSFunction>(
            v8::Utils::OpenHandle(*sourceV8LocalValue));
        auto targetV8InternalShared = v8::internal::handle(
            targetV8InternalFunction->shared(), v8InternalIsolate);
        auto sourceV8InternalShared = v8::internal::handle(
            sourceV8InternalFunction->shared(), v8InternalIsolate);
        if (IS_USER_DEFINED_FUNCTION(sourceV8InternalShared) && IS_USER_DEFINED_FUNCTION(targetV8InternalShared)) {
            // Clone the shared function info
            auto clonedV8InternalShared = v8InternalIsolate->factory()->CloneSharedFunctionInfo(
                sourceV8InternalShared);
            targetV8InternalFunction->set_shared(
                *clonedV8InternalShared, V8InternalWriteBarrierMode::UPDATE_WRITE_BARRIER);
            success = true;
        }
    }
    return success;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mCallbackContext) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto javetCallbackContextReferencePointer =
        new Javet::Callback::JavetCallbackContextReference(v8Runtime);
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
    auto v8LocalContextHandle =
        v8::BigInt::New(v8Isolate, TO_NATIVE_INT_64(javetCallbackContextReferencePointer));
    javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer =
        new V8PersistentBigInt(v8Isolate, v8LocalContextHandle);
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
    auto v8MaybeLocalFunction =
        v8::Function::New(v8Context, Javet::Callback::JavetFunctionCallback, v8LocalContextHandle);
    if (v8MaybeLocalFunction.IsEmpty()) {
        delete javetCallbackContextReferencePointer;
        INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteJavetCallbackContextReference);
        javetCallbackContextReferencePointer = nullptr;
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
            jobject externalV8Function = v8Runtime->SafeToExternalV8Value(
                jniEnv, v8Isolate, v8Context, v8LocalFunction);
            if (externalV8Function != nullptr && !jniEnv->ExceptionCheck()) {
                javetCallbackContextReferencePointer->SetHandle(jniEnv, mCallbackContext);
                return externalV8Function;
            }
            delete javetCallbackContextReferencePointer;
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteJavetCallbackContextReference);
            return externalV8Function;
        }
    }
    if (javetCallbackContextReferencePointer != nullptr) {
        delete javetCallbackContextReferencePointer;
        INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteJavetCallbackContextReference);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionDiscardCompiled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction->shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            if (v8InternalShared->CanDiscardCompiled()) {
                auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Isolate);
                V8InternalSharedFunctionInfo::DiscardCompiled(v8InternalIsolate, v8::internal::handle(v8InternalShared, v8InternalIsolate));
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
        auto v8InternalShared = v8InternalFunction->shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared) && v8InternalShared->is_wrapped()) {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Isolate);
            auto v8InternalScript = v8::internal::Cast<V8InternalScript>(v8InternalShared->script());
            auto wrappedArguments = v8InternalScript->wrapped_arguments();
            // FixedArray::length() returns a plain int in the V8 that Node.js
            // bundles, and a SafeHeapObjectSize in the newer standalone V8.
#ifdef ENABLE_NODE
            auto length = wrappedArguments->length();
#else
            auto length = static_cast<int>(wrappedArguments->length().value());
#endif
            if (length > 0) {
                jobjectArray arguments = jniEnv->NewObjectArray(length, Javet::Converter::jclassString, nullptr);
                for (int i = 0; i < length; ++i) {
                    auto v8InternalObjectHandle = v8::internal::handle(wrappedArguments->get(i), v8InternalIsolate);
                    auto v8LocalString = v8::Utils::ToLocal(v8InternalObjectHandle).As<v8::String>();
                    jstring argument = Javet::Converter::ToJavaStringFromV8String(
                        jniEnv, v8Isolate, v8LocalString);
                    jniEnv->SetObjectArrayElement(arguments, i, argument);
                    DELETE_LOCAL_REF(jniEnv, argument);
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
        auto v8InternalShared = v8InternalFunction->shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScript = v8::internal::Cast<V8InternalScript>(v8InternalShared->script());
            if (v8InternalScript->is_wrapped()) {
                return Javet::Compiler::getCachedData(
                    jniEnv,
                    v8Runtime,
                    v8Context,
                    v8LocalValue.As<v8::Function>());
            }
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetContext
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        if (v8InternalFunction->has_context()) {
            return Javet::Converter::ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8InternalFunction->context());
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
        v8_inspector::V8InspectorImpl v8InspectorImpl(v8Isolate, &v8InspectorClient);
        v8_inspector::V8Debugger v8Debugger(v8Isolate, &v8InspectorImpl);
        auto v8MaybeLocalArray = v8Debugger.internalProperties(v8Context, v8LocalValue.As<v8::Function>());
        if (v8MaybeLocalArray.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalArray.ToLocalChecked());
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
        auto v8InternalShared = v8InternalFunction->shared();
        if (v8InternalShared->native()) {
            return Native;
        }
        else if (v8InternalShared->IsApiFunction()) {
            return API;
        }
        else if (v8InternalShared->IsUserJavaScript()) {
            return UserDefined;
        }
    }
    return JSFunctionType::Unknown;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetJSScopeType
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction->shared();
        auto v8InternalScopeInfo = v8InternalShared->scope_info(v8::kAcquireLoad);
        if (v8InternalScopeInfo != nullptr && !v8InternalScopeInfo.is_null()) {
            using namespace Javet::Enums::JSScopeType;
            switch (v8InternalScopeInfo->scope_type()) {
            case V8InternalScopeType::SCRIPT_SCOPE: return Script;
            case V8InternalScopeType::REPL_MODE_SCOPE: return ReplMode;
            case V8InternalScopeType::CLASS_SCOPE: return Class;
            case V8InternalScopeType::EVAL_SCOPE: return Eval;
            case V8InternalScopeType::FUNCTION_SCOPE: return Function;
            case V8InternalScopeType::MODULE_SCOPE: return Module;
            case V8InternalScopeType::CATCH_SCOPE: return Catch;
            case V8InternalScopeType::BLOCK_SCOPE: return Block;
            case V8InternalScopeType::WITH_SCOPE: return With;
            case V8InternalScopeType::SHADOW_REALM_SCOPE: return ShadowRealm;
            default: return Javet::Enums::JSScopeType::Unknown;
            }
        }
    }
    return Javet::Enums::JSScopeType::Unknown;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetScopeInfos
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType,
    jboolean includeGlobalVariables, jboolean includeScopeTypeGlobal) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8LocalArray v8LocalArray = v8::Array::New(v8Isolate);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Isolate);
        auto v8InternalFunction = v8::internal::Cast<V8InternalJSFunction>(
            v8::Utils::OpenHandle(*v8LocalValue));
        V8InternalScopeIterator scopeIterator(v8InternalIsolate, v8InternalFunction);
        uint32_t index = 0;
        for (; !scopeIterator.Done(); scopeIterator.Next()) {
            auto type = scopeIterator.Type();
            if (!includeScopeTypeGlobal && type == V8InternalScopeIterator::ScopeTypeGlobal) {
                continue;
            }
            V8LocalArray innerV8LocalArray = v8::Array::New(v8Isolate, INDEX_SCOPE_SIZE);
            auto mode = includeGlobalVariables ? V8InternalScopeIterator::Mode::ALL : V8InternalScopeIterator::Mode::STACK;
            auto scopeObject = scopeIterator.ScopeObject(mode);
            auto v8LocalScopeObject = v8::Utils::ToLocal(scopeObject);
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_TYPE, Javet::Converter::ToV8Integer(v8Isolate, (int)type)).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_OBJECT, v8LocalScopeObject).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_HAS_CONTEXT, Javet::Converter::ToV8Boolean(v8Isolate, scopeIterator.HasContext())).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_START_POSITION, Javet::Converter::ToV8Integer(v8Isolate, scopeIterator.start_position())).ToChecked();
            innerV8LocalArray->Set(v8Context, INDEX_SCOPE_END_POSITION, Javet::Converter::ToV8Integer(v8Isolate, scopeIterator.end_position())).ToChecked();
            v8LocalArray->Set(v8Context, index, innerV8LocalArray).ToChecked();
            ++index;
        }
    }
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8LocalArray);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetScriptSource
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Isolate);
        auto v8InternalFunction = v8::internal::Cast<V8InternalJSFunction>(
            v8::Utils::OpenHandle(*v8LocalValue));
        auto v8InternalShared = v8::internal::handle(
            v8InternalFunction->shared(), v8InternalIsolate);
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScript = v8::internal::handle(
                v8::internal::Cast<V8InternalScript>(v8InternalShared->script()), v8InternalIsolate);
            auto v8InternalSource = v8::internal::handle(
                v8::internal::Cast<V8InternalString>(v8InternalScript->source()), v8InternalIsolate);
            const int startPosition = v8InternalShared->StartPosition();
            const int endPosition = v8InternalShared->EndPosition();
            const int sourceLength = v8InternalSource->length();
            size_t utf8Length = 0;
            auto sourceCode = v8InternalSource->ToCString(0, sourceLength, &utf8Length);
            jstring sourceString = sourceCode
                ? Javet::Converter::ToJavaStringFromUtf8(
                    jniEnv, std::string_view(sourceCode.get(), utf8Length))
                : nullptr;
            jobject scriptSource = jniEnv->NewObject(
                Javet::Converter::jclassIV8ValueFunctionScriptSource,
                Javet::Converter::jmethodIDIV8ValueFunctionScriptSourceConstructor,
                sourceString,
                startPosition,
                endPosition);
            DELETE_LOCAL_REF(jniEnv, sourceString);
            return scriptSource;
        }
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetSourceCode
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction->shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScript = v8::internal::Cast<V8InternalScript>(v8InternalShared->script());
            auto v8InternalSource = v8::internal::Cast<V8InternalString>(v8InternalScript->source());
            const int startPosition = v8InternalShared->StartPosition();
            const int endPosition = v8InternalShared->EndPosition();
            size_t utf8Length = 0;
            auto sourceCode = v8InternalSource->ToCString(startPosition, endPosition - startPosition, &utf8Length);
            return sourceCode
                ? Javet::Converter::ToJavaStringFromUtf8(
                    jniEnv, std::string_view(sourceCode.get(), utf8Length))
                : nullptr;
        }
    }
    return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionIsCompiled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction->shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            return v8InternalShared->is_compiled();
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionIsWrapped
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalFunction = Javet::Converter::ToV8InternalJSFunction(v8LocalValue);
        auto v8InternalShared = v8InternalFunction->shared();
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            return v8InternalShared->is_wrapped();
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetContext
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mV8ContextValue, jint v8ContextValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    jboolean success = false;
    if (IS_V8_FUNCTION(v8ValueType)) {
        V8LocalContext v8ContextValue = Javet::Converter::ToV8Context(
            jniEnv, v8Isolate, mV8ContextValue, v8ContextValueType);
        auto v8InternalContext = v8::internal::Cast<V8InternalNativeContext>(
            v8::Utils::OpenHandle(*v8ContextValue));
        auto v8InternalFunction = v8::internal::Cast<V8InternalJSFunction>(
            v8::Utils::OpenHandle(*v8LocalValue));
        v8InternalFunction->set_context(*v8InternalContext);
        success = true;
    }
    return success;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetScriptSource
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mScriptSource, jboolean mCloneScript) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    jboolean success = false;
    if (IS_V8_FUNCTION(v8ValueType)) {
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Isolate);
        auto v8InternalFunction = v8::internal::Cast<V8InternalJSFunction>(
            v8::Utils::OpenHandle(*v8LocalValue));
        auto v8InternalShared = v8::internal::handle(
            v8InternalFunction->shared(), v8InternalIsolate);
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScopeInfo = v8InternalShared->scope_info(v8::kAcquireLoad);
            if (v8InternalScopeInfo != nullptr
                && !v8InternalScopeInfo.is_null()
                && v8InternalScopeInfo->scope_type() == V8InternalScopeType::FUNCTION_SCOPE) {
                auto v8InternalScopeInfoHandle = v8::internal::handle(
                    v8InternalScopeInfo, v8InternalIsolate);
                auto mSourceCode = (jstring)jniEnv->CallObjectMethod(mScriptSource, Javet::Converter::jmethodIDIV8ValueFunctionScriptGetCode);
                auto umSourceCode = Javet::Converter::ToV8String(jniEnv, v8Isolate, mSourceCode);
                const int startPosition = jniEnv->CallIntMethod(mScriptSource, Javet::Converter::jmethodIDIV8ValueFunctionScriptGetStartPosition);
                const int endPosition = jniEnv->CallIntMethod(mScriptSource, Javet::Converter::jmethodIDIV8ValueFunctionScriptGetEndPosition);
                auto v8InternalScript = v8::internal::handle(
                    v8::internal::Cast<V8InternalScript>(v8InternalShared->script()), v8InternalIsolate);
                auto v8InternalSource = v8::Utils::OpenHandle(*umSourceCode);
                bool sourceCodeEquals = V8InternalObject::StrictEquals(v8InternalScript->source(), *v8InternalSource);
                bool positionEquals = startPosition == v8InternalShared->StartPosition() && endPosition == v8InternalShared->EndPosition();
                if (!sourceCodeEquals || !positionEquals) {
                    if (v8InternalShared->CanDiscardCompiled()) {
                        V8InternalSharedFunctionInfo::DiscardCompiled(v8InternalIsolate, v8InternalShared);
                        v8InternalShared->set_allows_lazy_compilation(true);
                    }
                    if (!sourceCodeEquals) {
                        if (mCloneScript) {
                            auto clonedV8InternalScript = v8InternalIsolate->factory()->CloneScript(v8InternalScript, v8InternalSource);
                            v8InternalShared->set_script(*clonedV8InternalScript, v8::kReleaseStore);
                        }
                        else {
                            V8InternalScript::SetSource(v8InternalIsolate, v8InternalScript, v8InternalSource);
                        }
                    }
                    if (!positionEquals) {
                        v8InternalScopeInfoHandle->SetPositionInfo(startPosition, endPosition);
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
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Isolate);
        auto v8InternalFunction = v8::internal::Cast<V8InternalJSFunction>(
            v8::Utils::OpenHandle(*v8LocalValue));
        auto v8InternalShared = v8::internal::handle(
            v8InternalFunction->shared(), v8InternalIsolate);
        if (IS_USER_DEFINED_FUNCTION(v8InternalShared)) {
            auto v8InternalScopeInfo = v8InternalShared->scope_info(v8::kAcquireLoad);
            while (v8InternalScopeInfo != nullptr
                && !v8InternalScopeInfo.is_null()
                && v8InternalScopeInfo->scope_type() == V8InternalScopeType::FUNCTION_SCOPE) {
                auto v8InternalScopeInfoHandle = v8::internal::handle(
                    v8InternalScopeInfo, v8InternalIsolate);
                auto v8InternalScript = v8::internal::handle(
                    v8::internal::Cast<V8InternalScript>(v8InternalShared->script()), v8InternalIsolate);
                auto v8InternalSource = v8::internal::handle(
                    v8::internal::Cast<V8InternalString>(v8InternalScript->source()), v8InternalIsolate);
                const int startPosition = v8InternalShared->StartPosition();
                const int endPosition = v8InternalShared->EndPosition();
                const int sourceLength = v8InternalSource->length();

                // Build the new source code.
                auto umSourceCode = Javet::Converter::ToV8String(jniEnv, v8Isolate, mSourceCode);

                V8LocalString newSourceCode;
                if (startPosition > 0) {
                    size_t utf8Length = 0;
                    auto stdStringHeader(v8InternalSource->ToCString(0, startPosition, &utf8Length));
                    auto v8MaybeLocalStringHeader = v8::String::NewFromUtf8(
                        v8Isolate, stdStringHeader.get(), v8::NewStringType::kNormal, utf8Length);
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
                    newSourceCode = v8::String::Concat(v8Isolate, newSourceCode, umSourceCode);
                }
                if (endPosition < sourceLength) {
                    size_t utf8Length = 0;
                    auto stdStringFooter(v8InternalSource->ToCString(endPosition, sourceLength - endPosition, &utf8Length));
                    auto v8MaybeLocalStringFooter = v8::String::NewFromUtf8(
                        v8Isolate, stdStringFooter.get(), v8::NewStringType::kNormal, utf8Length);
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
                        newSourceCode = v8::String::Concat(v8Isolate, newSourceCode, v8LocalStringFooter);
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
                bool sourceCodeEquals = V8InternalObject::StrictEquals(*v8InternalSource, *newV8InternalSource);
                bool positionEquals = newEndPosition == v8InternalShared->EndPosition();

                if (!sourceCodeEquals || !positionEquals) {
                    // Discard compiled data and set lazy compile.
                    if (v8InternalShared->CanDiscardCompiled()) {
                        V8InternalSharedFunctionInfo::DiscardCompiled(v8InternalIsolate, v8InternalShared);
                        v8InternalShared->set_allows_lazy_compilation(true);
                    }
                    if (!sourceCodeEquals) {
                        if (mCloneScript) {
                            auto clonedV8InternalScript = v8InternalIsolate->factory()->CloneScript(v8InternalScript, newV8InternalSource);
                            v8InternalShared->set_script(*clonedV8InternalScript, v8::kReleaseStore);
                        }
                        else {
                            V8InternalScript::SetSource(v8InternalIsolate, v8InternalScript, newV8InternalSource);
                        }
                    }
                    if (!positionEquals) {
                        v8InternalScopeInfoHandle->SetPositionInfo(startPosition, newEndPosition);
                    }
                    success = true;
                }
                break;
            }
        }
    }
    return success;
}
