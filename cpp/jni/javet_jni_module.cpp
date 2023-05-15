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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleEvaluate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mResultRequired) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalModule->GetStatus() == v8::Module::Status::kInstantiated) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8MaybeLocalValueResult = v8LocalModule->Evaluate(v8Context);
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            Javet::Exceptions::ClearJNIException(jniEnv);
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    Javet::Exceptions::ClearJNIException(jniEnv);
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalModule->GetStatus() == v8::Module::Status::kErrored) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalModule->GetException());
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetNamespace
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalModule->GetModuleNamespace());
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetScriptId
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return (jint)v8LocalModule->ScriptId();
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetStatus
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return (jint)v8LocalModule->GetStatus();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_moduleInstantiate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalModule->GetStatus() == v8::Module::Status::kUninstantiated) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8MaybeBool = v8LocalModule->InstantiateModule(v8Context, Javet::Callback::JavetModuleResolveCallback);
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}
