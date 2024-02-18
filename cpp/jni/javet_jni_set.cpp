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

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setAdd
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SET(v8ValueType)) {
        auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
        auto v8MaybeLocalSet = v8LocalValue.As<v8::Set>()->Add(v8Context, v8ValueValue);
        if (v8MaybeLocalSet.IsEmpty()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_setAsArray
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SET(v8ValueType)) {
        auto v8LocalArray = v8LocalValue.As<v8::Set>()->AsArray();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalArray);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setClear
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SET(v8ValueType)) {
        v8LocalValue.As<v8::Set>()->Clear();
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_setCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalSet = v8::Set::New(v8Context->GetIsolate());
    if (!v8LocalSet.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalSet);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setDelete
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SET(v8ValueType)) {
        auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
        V8MaybeBool v8MaybeBool = v8LocalValue.As<v8::Set>()->Delete(v8Context, v8ValueKey);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        else {
            return v8MaybeBool.FromMaybe(false);
        }
    }
    return false;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_setGetSize
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SET(v8ValueType)) {
        return (jint)v8LocalValue.As<v8::Set>()->Size();
    }
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setHas
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SET(v8ValueType)) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
            return false;
        }
        if (!v8LocalValueKey.IsEmpty()) {
            V8MaybeBool v8MaybeBool = v8LocalValue.As<v8::Set>()->Has(v8Context, v8LocalValueKey);
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return false;
            }
            if (v8MaybeBool.IsNothing()) {
                Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
                return false;
            }
            return v8MaybeBool.FromMaybe(false);
        }
    }
    return false;
}
