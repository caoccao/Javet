/*
 *   Copyright (c) 2021-2026. caoccao.com Sam Cao
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
#include "javet_jni_compiler.h"

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptCompile
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (!mIsModule) {
        const auto compileResult = Javet::Compiler::compileScript(
            jniEnv,
            v8Runtime,
            v8Context,
            mScript,
            mCachedData,
            mResourceName,
            mResourceLineOffset,
            mResourceColumnOffset,
            mScriptId,
            mIsWASM,
            mIsModule);
        if (mResultRequired) {
            return Javet::Compiler::toExternal(
                jniEnv,
                v8Runtime,
                v8Context,
                compileResult);
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptExecute
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto compileResult = Javet::Compiler::compileScript(
        jniEnv,
        v8Runtime,
        v8Context,
        mScript,
        mCachedData,
        mResourceName,
        mResourceLineOffset,
        mResourceColumnOffset,
        mScriptId,
        mIsWASM,
        false);
    if (compileResult.failed) {
        return nullptr;
    }
    if (!compileResult.compiledValue.IsEmpty()) {
        V8TryCatch v8TryCatch(v8Isolate);
        auto compliedScript = compileResult.compiledValue.ToLocalChecked();
        auto v8MaybeLocalValueResult = compliedScript->Run(v8Context);
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            Javet::Exceptions::ClearJNIException(jniEnv);
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    Javet::Exceptions::ClearJNIException(jniEnv);
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jbyteArray JNICALL Java_com_caoccao_javet_interop_V8Native_scriptGetCachedData
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    if (IS_V8_SCRIPT(v8ValueType)) {
        RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
        if (!v8LocalScript.IsEmpty()) {
            return Javet::Compiler::getCachedData(
                jniEnv,
                v8Runtime,
                v8Context,
                v8LocalScript);
        }
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_scriptGetResourceName
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    if (IS_V8_SCRIPT(v8ValueType)) {
        RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
        if (!v8LocalScript.IsEmpty()) {
            auto v8ValueResourceName = v8LocalScript->GetResourceName();
            if (!v8ValueResourceName->IsUndefined()) {
                return Javet::Converter::ToJavaStringFromV8String(
                    jniEnv, v8Isolate, v8ValueResourceName);
            }
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptRun
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mResultRequired) {
    RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (!v8LocalScript.IsEmpty()) {
        V8TryCatch v8TryCatch(v8Isolate);
        auto v8MaybeLocalValueResult = v8LocalScript->Run(v8Context);
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
            Javet::Exceptions::ClearJNIException(jniEnv);
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    Javet::Exceptions::ClearJNIException(jniEnv);
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}
