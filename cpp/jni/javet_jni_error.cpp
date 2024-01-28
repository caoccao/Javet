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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_errorCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint mErrorTypeId, jstring mMessage) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto errorMessage = Javet::Converter::ToV8String(jniEnv, v8Context, mMessage);
    using namespace Javet::Enums::V8ValueErrorType;
    switch (mErrorTypeId) {
    case V8ValueErrorType::Error: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::Error(errorMessage));
    case V8ValueErrorType::RangeError: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::RangeError(errorMessage));
    case V8ValueErrorType::ReferenceError: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::ReferenceError(errorMessage));
    case V8ValueErrorType::SyntaxError: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::SyntaxError(errorMessage));
    case V8ValueErrorType::TypeError: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::TypeError(errorMessage));
    case V8ValueErrorType::WasmCompileError: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::WasmCompileError(errorMessage));
    case V8ValueErrorType::WasmLinkError: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::WasmLinkError(errorMessage));
    case V8ValueErrorType::WasmRuntimeError: return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8::Exception::WasmRuntimeError(errorMessage));
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}
