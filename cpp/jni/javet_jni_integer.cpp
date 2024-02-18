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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_integerObjectCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint mInt) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalDoubleObject = v8::NumberObject::New(v8Context->GetIsolate(), mInt);
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalDoubleObject);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_integerObjectValueOf
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_INTEGER_OBJECT(v8ValueType)) {
        auto intValue = (int)(v8LocalValue.As<v8::NumberObject>()->ValueOf());
        auto v8LocalInteger = Javet::Converter::ToV8Integer(v8Context, intValue);
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalInteger);
    }
    return nullptr;
}
