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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8MaybeLocalPromiseResolver = v8::Promise::Resolver::New(v8Context);
    if (v8MaybeLocalPromiseResolver.IsEmpty()) {
        if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context, "Promise resolver allocation failed")) {
            return nullptr;
        }
    }
    else {
        auto v8LocalPromiseResolver = v8MaybeLocalPromiseResolver.ToLocalChecked();
        if (!v8LocalPromiseResolver.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalPromiseResolver);
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetState
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        return (jint)v8LocalValue.As<v8::Promise>()->State();
    }
    return -1;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseCatch
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueFunctionHandle) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8PersistentFunctionPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionHandle);
        auto v8LocalFunction = v8PersistentFunctionPointer->Get(v8Context->GetIsolate());
        auto v8MaybeLocalValueResult = v8LocalValue.As<v8::Promise>()->Catch(v8Context, v8LocalFunction);
        if (v8MaybeLocalValueResult.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetResult
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromise = v8LocalValue.As<v8::Promise>();
        if (v8LocalPromise->State() != v8::Promise::PromiseState::kPending) {
            auto v8ValueResult = v8LocalPromise->Result();
            if (v8ValueResult.IsEmpty()) {
                if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                    return nullptr;
                }
            }
            else {
                return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8ValueResult);
            }
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseHasHandler
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        return v8LocalValue.As<v8::Promise>()->HasHandler();
    }
    return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_promiseMarkAsHandled
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        v8LocalValue.As<v8::Promise>()->MarkAsHandled();
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseThen
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueFunctionFulfilledHandle, jlong v8ValueFunctionRejectedHandle) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8PersistentFunctionFulfilledPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionFulfilledHandle);
        auto v8LocalFunctionFulfilled = v8PersistentFunctionFulfilledPointer->Get(v8Context->GetIsolate());
        V8MaybeLocalPromise v8MaybeLocalValueResult;
        if (v8ValueFunctionRejectedHandle == 0L) {
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Promise>()->Then(v8Context, v8LocalFunctionFulfilled);
        }
        else {
            auto v8PersistentFunctionRejectedPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionRejectedHandle);
            auto v8LocalFunctionRejected = v8PersistentFunctionRejectedPointer->Get(v8Context->GetIsolate());
            v8MaybeLocalValueResult = v8LocalValue.As<v8::Promise>()->Then(v8Context, v8LocalFunctionFulfilled, v8LocalFunctionRejected);
        }
        if (v8MaybeLocalValueResult.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetPromise
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromiseResolver = v8LocalValue.As<v8::Promise::Resolver>();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalPromiseResolver->GetPromise());
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseReject
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE_WITH_UNIQUE_LOCKER(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromiseResolver = v8LocalValue.As<v8::Promise::Resolver>();
        auto v8MaybeBool = v8LocalPromiseResolver->Reject(v8Context, Javet::Converter::ToV8Value(jniEnv, v8Context, value));
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseResolve
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE_WITH_UNIQUE_LOCKER(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_PROMISE(v8ValueType)) {
        auto v8LocalPromiseResolver = v8LocalValue.As<v8::Promise::Resolver>();
        auto v8MaybeBool = v8LocalPromiseResolver->Resolve(v8Context, Javet::Converter::ToV8Value(jniEnv, v8Context, value));
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}
