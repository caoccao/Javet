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
