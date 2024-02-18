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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_proxyCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mTarget) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    V8LocalObject v8LocalObjectTaget;
    if (mTarget != nullptr) {
        auto v8LocalValue = Javet::Converter::ToV8Value(jniEnv, v8Context, mTarget);
        if (v8LocalValue->IsObject()) {
            v8LocalObjectTaget = v8LocalValue.As<v8::Object>();
        }
    }
    if (v8LocalObjectTaget.IsEmpty()) {
        v8LocalObjectTaget = v8::Object::New(v8Context->GetIsolate());
    }
    auto v8LocalObjectHandler = v8::Object::New(v8Context->GetIsolate());
    auto v8MaybeLocalProxy = v8::Proxy::New(v8Context, v8LocalObjectTaget, v8LocalObjectHandler);
    if (v8MaybeLocalProxy.IsEmpty()) {
        if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context, "Proxy allocation failed")) {
            return nullptr;
        }
    }
    else {
        auto v8LocalProxy = v8MaybeLocalProxy.ToLocalChecked();
        if (!v8LocalProxy.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalProxy);
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_proxyGetHandler
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValue.As<v8::Proxy>()->GetHandler());
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_proxyGetTarget
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValue.As<v8::Proxy>()->GetTarget());
    }
    return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_proxyIsRevoked
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        return v8LocalValue.As<v8::Proxy>()->IsRevoked();
    }
    return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_proxyRevoke
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROXY(v8ValueType)) {
        v8LocalValue.As<v8::Proxy>()->Revoke();
    }
}
