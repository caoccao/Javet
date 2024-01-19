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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptCompile
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (!mIsModule) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
        auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
            jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
        v8::MaybeLocal<v8::Script> v8MaybeLocalScript;
        if (mCachedData) {
            V8ScriptCompilerSource scriptSource(
                umScript, *scriptOriginPointer.get(), Javet::Converter::ToCachedDataPointer(jniEnv, mCachedData));
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            V8InternalDisallowCompilation v8InternalDisallowCompilation(v8InternalIsolate);
            v8MaybeLocalScript = v8::ScriptCompiler::Compile(v8Context, &scriptSource, v8::ScriptCompiler::kConsumeCodeCache);
            LOG_DEBUG("Script cache is " << (scriptSource.GetCachedData()->rejected ? "rejected" : "accepted") << ".");
        }
        else {
            V8ScriptCompilerSource scriptSource(umScript, *scriptOriginPointer.get());
            v8MaybeLocalScript = v8::ScriptCompiler::Compile(v8Context, &scriptSource);
        }
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalScript.IsEmpty()) {
            return Javet::Converter::ToExternalV8Script(
                jniEnv, v8Runtime, v8Context, v8MaybeLocalScript.ToLocalChecked());
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptExecute
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    V8TryCatch v8TryCatch(v8Context->GetIsolate());
    auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
    auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
        jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, false);
    v8::MaybeLocal<v8::Script> v8MaybeLocalScript;
    if (mCachedData) {
        V8ScriptCompilerSource scriptSource(
            umScript, *scriptOriginPointer.get(), Javet::Converter::ToCachedDataPointer(jniEnv, mCachedData));
        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
        V8InternalDisallowCompilation v8InternalDisallowCompilation(v8InternalIsolate);
        v8MaybeLocalScript = v8::ScriptCompiler::Compile(v8Context, &scriptSource, v8::ScriptCompiler::kConsumeCodeCache);
        LOG_DEBUG("Script cache is " << (scriptSource.GetCachedData()->rejected ? "rejected" : "accepted") << ".");
    }
    else {
        V8ScriptCompilerSource scriptSource(umScript, *scriptOriginPointer.get());
        v8MaybeLocalScript = v8::ScriptCompiler::Compile(v8Context, &scriptSource);
    }
    if (v8TryCatch.HasCaught()) {
        return Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Runtime, v8Context, v8TryCatch);
    }
    else if (!v8MaybeLocalScript.IsEmpty()) {
        auto compliedScript = v8MaybeLocalScript.ToLocalChecked();
        auto v8MaybeLocalValueResult = compliedScript->Run(v8Context);
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

JNIEXPORT jbyteArray JNICALL Java_com_caoccao_javet_interop_V8Native_scriptGetCachedData
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    if (IS_V8_SCRIPT(v8ValueType)) {
        RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
        if (!v8LocalScript.IsEmpty()) {
            V8TryCatch v8TryCatch(v8Context->GetIsolate());
            std::unique_ptr<V8ScriptCompilerCachedData> cachedDataPointer;
            cachedDataPointer.reset(v8::ScriptCompiler::CreateCodeCache(v8LocalScript->GetUnboundScript()));
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return nullptr;
            }
            if (cachedDataPointer) {
                return Javet::Converter::ToJavaByteArray(jniEnv, cachedDataPointer.get());
            }
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptRun
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mResultRequired) {
    RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (!v8LocalScript.IsEmpty()) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8MaybeLocalValueResult = v8LocalScript->Run(v8Context);
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
