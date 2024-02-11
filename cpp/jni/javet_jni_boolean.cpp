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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_booleanObjectCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean mBoolean) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalBooleanObject = v8::BooleanObject::New(v8Context->GetIsolate(), mBoolean);
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalBooleanObject);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_booleanObjectValueOf
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_BOOLEAN_OBJECT(v8ValueType)) {
        auto booleanValue = v8LocalValue.As<v8::BooleanObject>()->ValueOf();
        auto v8LocalBoolean = Javet::Converter::ToV8Boolean(v8Context, booleanValue);
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalBoolean);
    }
    return nullptr;
}
