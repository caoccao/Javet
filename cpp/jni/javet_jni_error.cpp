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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPendingException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    return HAS_PENDING_EXCEPTION(v8InternalIsolate);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPendingMessage
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    return v8InternalIsolate->has_pending_message();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasScheduledException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
#ifdef ENABLE_NODE
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    return v8InternalIsolate->has_scheduled_exception();
#else
    return false;
#endif
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promoteScheduledException
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
#ifdef ENABLE_NODE
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
    if (v8InternalIsolate->has_scheduled_exception()) {
        v8InternalIsolate->PromoteScheduledException();
        return true;
    }
#endif
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_reportPendingMessages
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    return Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_throwError__JILjava_lang_String_2
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint mErrorTypeId, jstring mMessage) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto errorMessage = Javet::Converter::ToV8String(jniEnv, v8Context, mMessage);
    V8LocalValue v8LocalValueError;
    using namespace Javet::Enums::V8ValueErrorType;
    switch (mErrorTypeId) {
    case V8ValueErrorType::Error: v8LocalValueError = v8::Exception::Error(errorMessage); break;
    case V8ValueErrorType::RangeError: v8LocalValueError = v8::Exception::RangeError(errorMessage); break;
    case V8ValueErrorType::ReferenceError: v8LocalValueError = v8::Exception::ReferenceError(errorMessage); break;
    case V8ValueErrorType::SyntaxError: v8LocalValueError = v8::Exception::SyntaxError(errorMessage); break;
    case V8ValueErrorType::TypeError: v8LocalValueError = v8::Exception::TypeError(errorMessage); break;
    case V8ValueErrorType::WasmCompileError: v8LocalValueError = v8::Exception::WasmCompileError(errorMessage); break;
    case V8ValueErrorType::WasmLinkError: v8LocalValueError = v8::Exception::WasmLinkError(errorMessage); break;
    case V8ValueErrorType::WasmRuntimeError: v8LocalValueError = v8::Exception::WasmRuntimeError(errorMessage); break;
    }
    if (!v8LocalValueError.IsEmpty()) {
        v8Context->GetIsolate()->ThrowException(v8LocalValueError);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_throwError__JLjava_lang_Object_2
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mV8Value) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalValue = Javet::Converter::ToV8Value(jniEnv, v8Context, mV8Value);
    if (!v8LocalValue.IsEmpty()) {
        v8Context->GetIsolate()->ThrowException(v8LocalValue);
        return true;
    }
    return false;
}
