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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_arrayCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalArray = v8::Array::New(v8Context->GetIsolate());
    if (!v8LocalArray.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalArray);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_arrayGetLength
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

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_batchArrayGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType,
    jobjectArray v8Values, jint startIndex, jint endIndex) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_ARRAY(v8ValueType) || IS_V8_ARGUMENTS(v8ValueType) || v8LocalValue->IsTypedArray()) {
        return Javet::Converter::ToExternalV8ValueArray(
            jniEnv,
            v8Runtime,
            v8Context,
            v8LocalValue.As<v8::Array>(),
            v8Values,
            startIndex,
            endIndex);
    }
    return 0;
}
