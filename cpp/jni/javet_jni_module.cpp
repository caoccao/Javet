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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleCompile
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
    if (mIsModule) {
        RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
        auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
            jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
        v8::MaybeLocal<v8::Module> v8MaybeLocalCompiledModule;
        if (mCachedData) {
            V8ScriptCompilerSource scriptSource(
                umScript,
                *scriptOriginPointer.get(),
                Javet::Converter::ToCachedDataPointer(jniEnv, mCachedData));
            v8MaybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(
                v8Context->GetIsolate(),
                &scriptSource,
                v8::ScriptCompiler::kConsumeCodeCache);
            LOG_DEBUG("Module cache is " << (scriptSource.GetCachedData()->rejected ? "rejected" : "accepted") << ".");
        }
        else {
            V8ScriptCompilerSource scriptSource(umScript, *scriptOriginPointer.get());
            v8MaybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(v8Context->GetIsolate(), &scriptSource);
        }
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        else if (mResultRequired && !v8MaybeLocalCompiledModule.IsEmpty()) {
            return Javet::Converter::ToExternalV8Module(jniEnv, v8Runtime, v8Context, v8MaybeLocalCompiledModule.ToLocalChecked());
        }
    }
    return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mModuleName, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8MaybeLocalArray = v8LocalObject->GetPropertyNames(v8Context);
        if (v8MaybeLocalArray.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            auto v8LocalArray = v8MaybeLocalArray.ToLocalChecked();
            std::vector<V8LocalString> exportNames;
            int length = v8LocalArray->Length();
            for (int i = 0; i < length; ++i) {
                auto v8MaybeLocalValue = v8LocalArray->Get(v8Context, i);
                if (!v8MaybeLocalValue.IsEmpty()) {
                    auto v8LocalValueKey = v8MaybeLocalValue.ToLocalChecked();
                    if (v8LocalValueKey->IsString() || v8LocalValueKey->IsStringObject()) {
                        exportNames.emplace_back(v8LocalValueKey.As<v8::String>());
                    }
                }
            }
            auto v8LocalModule = v8::Module::CreateSyntheticModule(
                v8Context->GetIsolate(),
                Javet::Converter::ToV8String(jniEnv, v8Context, mModuleName),
                exportNames,
                Javet::Callback::JavetSyntheticModuleEvaluationStepsCallback);
            std::string stringKey("module:{}" + std::to_string(v8LocalModule->GetIdentityHash()));
            auto v8LocalStringKey = Javet::Converter::ToV8String(v8Context, stringKey.c_str());
            auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
            auto v8GlobalObject = v8Runtime->v8GlobalObject.Get(v8Context->GetIsolate());
            v8GlobalObject->SetPrivate(v8Context, v8LocalPrivateKey, v8LocalObject);
            if (v8TryCatch.HasCaught()) {
                return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
            }
            return Javet::Converter::ToExternalV8Module(jniEnv, v8Runtime, v8Context, v8LocalModule);
        }
    }
    Javet::Exceptions::ClearJNIException(jniEnv);
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleExecute
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jstring mScript, jbyteArray mCachedData, jboolean mResultRequired,
    jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    V8TryCatch v8TryCatch(v8Context->GetIsolate());
    auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
    auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
        jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, true);
    v8::MaybeLocal<v8::Module> v8MaybeLocalCompiledModule;
    if (mCachedData) {
        V8ScriptCompilerSource scriptSource(
            umScript, *scriptOriginPointer.get(), Javet::Converter::ToCachedDataPointer(jniEnv, mCachedData));
        v8MaybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(
            v8Context->GetIsolate(), &scriptSource, v8::ScriptCompiler::kConsumeCodeCache);
        LOG_DEBUG("Module cache is " << (scriptSource.GetCachedData()->rejected ? "rejected" : "accepted") << ".");
    }
    else {
        V8ScriptCompilerSource scriptSource(umScript, *scriptOriginPointer.get());
        v8MaybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(v8Context->GetIsolate(), &scriptSource);
    }
    if (v8TryCatch.HasCaught()) {
        return Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Runtime, v8Context, v8TryCatch);
    }
    else if (!v8MaybeLocalCompiledModule.IsEmpty()) {
        auto compliedModule = v8MaybeLocalCompiledModule.ToLocalChecked();
        auto v8MaybeBool = compliedModule->InstantiateModule(v8Context, Javet::Callback::JavetModuleResolveCallback);
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        else if (v8MaybeBool.FromMaybe(false)) {
            auto v8MaybeLocalValueResult = compliedModule->Evaluate(v8Context);
            if (v8TryCatch.HasCaught()) {
                return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
            }
            if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
                Javet::Exceptions::ClearJNIException(jniEnv);
                return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
            }
        }
    }
    Javet::Exceptions::ClearJNIException(jniEnv);
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jbyteArray JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetCachedData
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    if (IS_V8_MODULE(v8ValueType)) {
        RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        std::unique_ptr<V8ScriptCompilerCachedData> cachedDataPointer;
        cachedDataPointer.reset(v8::ScriptCompiler::CreateCodeCache(v8LocalModule->GetUnboundModuleScript()));
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
            return nullptr;
        }
        if (cachedDataPointer) {
            return Javet::Converter::ToJavaByteArray(jniEnv, cachedDataPointer.get());
        }
    }
    return nullptr;
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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_moduleIsSourceTextModule
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return v8LocalModule->IsSourceTextModule();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_moduleIsSyntheticModule
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return v8LocalModule->IsSyntheticModule();
}
