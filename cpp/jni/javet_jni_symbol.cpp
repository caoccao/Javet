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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_symbolCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mDescription) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalStringDescription = Javet::Converter::ToV8String(jniEnv, v8Context, mDescription);
    auto v8LocalSymbol = v8::Symbol::New(v8Context->GetIsolate(), v8LocalStringDescription);
    if (!v8LocalSymbol.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalSymbol);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_symbolDescription
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        auto v8String = v8LocalValue.As<v8::Symbol>()->Description(v8Context->GetIsolate()).As<v8::String>();
        return Javet::Converter::ToJavaString(jniEnv, v8Context, v8String);
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_symbolObjectValueOf
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL_OBJECT(v8ValueType)) {
        auto v8LocalSymbol = v8LocalValue.As<v8::SymbolObject>()->ValueOf();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalSymbol);
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_symbolToObject
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        auto v8LocalSymbolObject = v8::SymbolObject::New(v8Context->GetIsolate(), v8LocalValue.As<v8::Symbol>());
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalSymbolObject);
    }
    return nullptr;
}
