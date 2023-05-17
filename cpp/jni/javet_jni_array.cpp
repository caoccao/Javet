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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_arrayCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalArray = v8::Array::New(v8Context->GetIsolate());
    if (!v8LocalArray.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalArray);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_arrayDelete
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_ARRAY(v8ValueType)) {
        V8MaybeBool v8MaybeBool = v8::Just(false);
        auto v8LocalArray = v8LocalValue.As<v8::Array>();
        if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            v8MaybeBool = v8LocalArray->Delete(v8Context, integerKey);
        }
        else {
            auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
            v8MaybeBool = v8LocalArray->Delete(v8Context, v8ValueKey);
        }
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        else {
            return v8MaybeBool.FromMaybe(false);
        }
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_arrayGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_ARRAY(v8ValueType) || IS_V8_ARGUMENTS(v8ValueType) || v8LocalValue->IsTypedArray()) {
        V8MaybeLocalValue v8MaybeLocalValue;
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            if (integerKey >= 0) {
                v8MaybeLocalValue = v8LocalValue.As<v8::Array>()->Get(v8Context, integerKey);
            }
        }
        else {
            auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
            if (v8LocalValueKey.IsEmpty()) {
                if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                    return nullptr;
                }
            }
            else {
                v8MaybeLocalValue = v8LocalValue.As<v8::Array>()->Get(v8Context, v8LocalValueKey);
            }
        }
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValue.ToLocalChecked());
        }
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
