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

#include "javet_jni.h"

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_contextGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jint index) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_CONTEXT(v8ValueType)) {
        V8LocalContext v8ContextValue = v8LocalValue.As<v8::Context>();
        V8InternalContext v8InternalContext = Javet::Converter::ToV8InternalContext(v8ContextValue);
        if (index >= 0 && index < v8InternalContext.length()) {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            auto v8InternalObject = v8InternalContext.get(index);
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8InternalObject);
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_contextGetLength
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_CONTEXT(v8ValueType)) {
        V8LocalContext v8ContextValue = v8LocalValue.As<v8::Context>();
        V8InternalContext v8InternalContext = Javet::Converter::ToV8InternalContext(v8ContextValue);
        return v8InternalContext.length();
    }
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_contextIsContextType
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jint contextTypeId) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_CONTEXT(v8ValueType)) {
        V8LocalContext v8ContextValue = v8LocalValue.As<v8::Context>();
        V8InternalContext v8InternalContext = Javet::Converter::ToV8InternalContext(v8ContextValue);
        using namespace Javet::Enums::V8ContextType;
        switch (contextTypeId) {
        case Await: return v8InternalContext.IsAwaitContext(); // 0
        case Block: return v8InternalContext.IsBlockContext(); // 1
        case Catch: return v8InternalContext.IsCatchContext(); // 2
        case DebugEvaluate: return v8InternalContext.IsDebugEvaluateContext(); // 3
        case Declaration: return v8InternalContext.is_declaration_context(); // 4
        case Eval: return v8InternalContext.IsEvalContext(); // 5
        case Function: return v8InternalContext.IsFunctionContext(); // 6
        case Module: return v8InternalContext.IsModuleContext(); // 7
        case Script: return v8InternalContext.IsScriptContext(); // 8
        case With: return v8InternalContext.IsWithContext(); // 9
        default:return false;
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_contextSetLength
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jint length) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    jboolean success = false;
    if (IS_V8_CONTEXT(v8ValueType)) {
        V8LocalContext v8ContextValue = v8LocalValue.As<v8::Context>();
        V8InternalContext v8InternalContext = Javet::Converter::ToV8InternalContext(v8ContextValue);
        v8InternalContext.set_length(length);
        success = true;
    }
    return success;
}
