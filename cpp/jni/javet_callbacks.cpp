/*
 *   Copyright (c) 2021 caoccao.com Sam Cao
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

#include "javet_callbacks.h"
#include "javet_converter.h"
#include "javet_logging.h"
#include "javet_native.h"
#include "javet_v8_runtime.h"

namespace Javet {
    namespace Callback {
        const std::string PROMISE_REJECT_EVENTS[] = {
            "PromiseRejectWithNoHandler",
            "PromiseHandlerAddedAfterReject",
            "PromiseResolveAfterResolved",
            "PromiseRejectAfterResolved",
        };

        void Initialize(JNIEnv* jniEnv) {

            jclassJavetCallbackContext = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/callback/JavetCallbackContext"));
            jmethodIDJavetCallbackContextIsReturnResult = jniEnv->GetMethodID(jclassJavetCallbackContext, "isReturnResult", "()Z");
            jmethodIDJavetCallbackContextIsThisObjectRequired = jniEnv->GetMethodID(jclassJavetCallbackContext, "isThisObjectRequired", "()Z");
            jmethodIDJavetCallbackContextSetHandle = jniEnv->GetMethodID(jclassJavetCallbackContext, "setHandle", "(J)V");

            jclassIV8Module = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/IV8Module"));
            jmethodIDIV8ModuleGetHandle = jniEnv->GetMethodID(jclassIV8Module, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassIV8ValueReference = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/IV8ValueReference"));
            jmethodIDIV8ValueReferenceClose = jniEnv->GetMethodID(jclassIV8ValueReference, "close", "(Z)V");

            jclassJavetResourceUtils = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/utils/JavetResourceUtils"));
            jmethodIDJavetResourceUtilsSafeClose = jniEnv->GetStaticMethodID(jclassJavetResourceUtils, "safeClose", "(Ljava/lang/Object;)V");

            jclassV8FunctionCallback = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/callback/V8FunctionCallback"));
            jmethodIDV8FunctionCallbackReceiveCallback = jniEnv->GetStaticMethodID(jclassV8FunctionCallback, "receiveCallback",
                "(Lcom/caoccao/javet/interop/V8Runtime;Lcom/caoccao/javet/interop/callback/JavetCallbackContext;Lcom/caoccao/javet/values/V8Value;Lcom/caoccao/javet/values/reference/V8ValueArray;)Lcom/caoccao/javet/values/V8Value;");

            jclassV8Runtime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Runtime"));
            jmethodIDV8RuntimeGetV8Module = jniEnv->GetMethodID(jclassV8Runtime, "getV8Module", "(Ljava/lang/String;Lcom/caoccao/javet/values/reference/IV8Module;)Lcom/caoccao/javet/values/reference/IV8Module;");
            jmethodIDV8RuntimeReceiveGCEpilogueCallback = jniEnv->GetMethodID(jclassV8Runtime, "receiveGCEpilogueCallback", "(II)V");
            jmethodIDV8RuntimeReceiveGCPrologueCallback = jniEnv->GetMethodID(jclassV8Runtime, "receiveGCPrologueCallback", "(II)V");
            jmethodIDV8RuntimeReceivePromiseRejectCallback = jniEnv->GetMethodID(jclassV8Runtime, "receivePromiseRejectCallback", "(ILcom/caoccao/javet/values/reference/V8ValuePromise;Lcom/caoccao/javet/values/V8Value;)V");
            jmethodIDV8RuntimeRemoveCallbackContext = jniEnv->GetMethodID(jclassV8Runtime, "removeCallbackContext", "(J)V");
        }

        void JavetCloseWeakCallbackContextHandle(const v8::WeakCallbackInfo<JavetCallbackContextReference>& info) {
            FETCH_JNI_ENV(GlobalJavaVM);
            auto javetCallbackContextReferencePointer = info.GetParameter();
            auto v8Context = info.GetIsolate()->GetCurrentContext();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("JavetCloseWeakCallbackContextHandle: V8 context is empty.");
            }
            else {
                auto v8Runtime = V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("JavetCloseWeakCallbackContextHandle: V8 runtime is empty.");
                }
                else {
                    jobject externalV8Runtime = v8Runtime->externalV8Runtime;
                    javetCallbackContextReferencePointer->RemoveCallbackContext(externalV8Runtime);
                }
            }
            delete javetCallbackContextReferencePointer;
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteJavetCallbackContextReference);
        }

        void JavetCloseWeakDataReference(const v8::WeakCallbackInfo<V8ValueReference>& info) {
            auto v8ValueReference = info.GetParameter();
            v8ValueReference->Close();
            delete v8ValueReference;
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteWeakCallbackReference);
        }

        void JavetFunctionCallback(const v8::FunctionCallbackInfo<v8::Value>& info) {
            reinterpret_cast<Javet::Callback::JavetCallbackContextReference*>(
                info.Data().As<v8::BigInt>()->Int64Value())->CallFunction(info);
        }

        void JavetGCEpilogueCallback(v8::Isolate* v8Isolate, v8::GCType v8GCType, v8::GCCallbackFlags v8GCCallbackFlags) {
            auto v8Context = v8Isolate->GetCurrentContext();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("JavetGCEpilogueCallback: V8 context is empty.");
            }
            else {
                auto v8Runtime = V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("JavetGCEpilogueCallback: V8 runtime is empty.");
                }
                else {
                    FETCH_JNI_ENV(GlobalJavaVM);
                    auto externalV8Runtime = v8Runtime->externalV8Runtime;
                    jobject mIV8Module = jniEnv->CallObjectMethod(
                        externalV8Runtime, jmethodIDV8RuntimeReceiveGCEpilogueCallback, (jint)v8GCType, (jint)v8GCCallbackFlags);
                }
            }
        }

        void JavetGCPrologueCallback(v8::Isolate* v8Isolate, v8::GCType v8GCType, v8::GCCallbackFlags v8GCCallbackFlags) {
            auto v8Context = v8Isolate->GetCurrentContext();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("JavetGCPrologueCallback: V8 context is empty.");
            }
            else {
                auto v8Runtime = V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("JavetGCPrologueCallback: V8 runtime is empty.");
                }
                else {
                    FETCH_JNI_ENV(GlobalJavaVM);
                    auto externalV8Runtime = v8Runtime->externalV8Runtime;
                    jobject mIV8Module = jniEnv->CallObjectMethod(
                        externalV8Runtime, jmethodIDV8RuntimeReceiveGCPrologueCallback, (jint)v8GCType, (jint)v8GCCallbackFlags);
                }
            }
        }

        void JavetPropertyGetterCallback(V8LocalName propertyName, const v8::PropertyCallbackInfo<v8::Value>& info) {
            reinterpret_cast<Javet::Callback::JavetCallbackContextReference*>(
                info.Data().As<v8::Array>()->Get(info.GetIsolate()->GetCurrentContext(), 0).ToLocalChecked().As<v8::BigInt>()->Int64Value())
                ->CallPropertyGetter(propertyName, info);
        }

        void JavetPropertySetterCallback(V8LocalName propertyName, V8LocalValue propertyValue, const v8::PropertyCallbackInfo<void>& info) {
            reinterpret_cast<Javet::Callback::JavetCallbackContextReference*>(
                info.Data().As<v8::Array>()->Get(info.GetIsolate()->GetCurrentContext(), 1).ToLocalChecked().As<v8::BigInt>()->Int64Value())
                ->CallPropertySetter(propertyName, propertyValue, info);
        }

        V8MaybeLocalModule JavetModuleResolveCallback(
            V8LocalContext v8Context,
            V8LocalString specifier,
            V8LocalFixedArray importAssertions,
            V8LocalModule referrer) {
            V8MaybeLocalModule resolvedV8MaybeLocalModule = V8MaybeLocalModule();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("JavetModuleResolveCallback: V8 context is empty.");
            }
            else {
                auto v8Runtime = V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("JavetModuleResolveCallback: V8 runtime is empty.");
                }
                else {
                    FETCH_JNI_ENV(GlobalJavaVM);
                    auto externalV8Runtime = v8Runtime->externalV8Runtime;
                    jobject mReferrerV8Module = referrer.IsEmpty() ? nullptr : Javet::Converter::ToExternalV8Module(jniEnv, externalV8Runtime, v8Context, referrer);
                    jobject mIV8Module = jniEnv->CallObjectMethod(
                        externalV8Runtime, jmethodIDV8RuntimeGetV8Module,
                        Javet::Converter::ToJavaString(jniEnv, v8Context, specifier),
                        mReferrerV8Module);
                    auto moduleNamePointer = Javet::Converter::ToStdString(v8Context, specifier);
                    if (jniEnv->ExceptionCheck()) {
                        // JNI exception is not re-thrown in this callback function because it will pop up automatically.
                        LOG_ERROR("JavetModuleResolveCallback: module '" << moduleNamePointer.get() << "' with exception");
                        std::string errorMessage("Cannot resolve package '");
                        errorMessage.append(*moduleNamePointer);
                        errorMessage.append("'");
                        Javet::Exceptions::ThrowV8Exception(jniEnv, v8Context, errorMessage.c_str(), false);
                    }
                    else if (mIV8Module == nullptr) {
                        LOG_ERROR("JavetModuleResolveCallback: module '" << moduleNamePointer.get() << "' not found");
                        std::string errorMessage("Cannot find package '");
                        errorMessage.append(*moduleNamePointer);
                        errorMessage.append("'");
                        Javet::Exceptions::ThrowV8Exception(jniEnv, v8Context, errorMessage.c_str());
                    }
                    else {
                        auto mHandle = jniEnv->CallLongMethod(mIV8Module, jmethodIDIV8ModuleGetHandle);
                        auto v8PersistentModule = TO_V8_PERSISTENT_MODULE_POINTER(mHandle);
                        LOG_DEBUG("JavetModuleResolveCallback: module '" << moduleNamePointer.get() << "' found");
                        resolvedV8MaybeLocalModule = v8PersistentModule->Get(v8Context->GetIsolate());
                    }
                    if (mReferrerV8Module != nullptr) {
                        jniEnv->CallVoidMethod(mReferrerV8Module, jmethodIDIV8ValueReferenceClose, true);
                    }
                }
            }
            return resolvedV8MaybeLocalModule;
        }

        void JavetPromiseRejectCallback(v8::PromiseRejectMessage message) {
            auto promiseRejectEvent = message.GetEvent();
            auto v8LocalPromise = message.GetPromise();
            LOG_ERROR("Unhandled promise rejection with event " << PROMISE_REJECT_EVENTS[promiseRejectEvent] << ".");
            FETCH_JNI_ENV(GlobalJavaVM);
            auto v8Isolate = v8LocalPromise->GetIsolate();
            auto v8Context = v8Isolate->GetCurrentContext();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("JavetPromiseRejectCallback: V8 context is empty.");
            }
            else {
                auto v8Runtime = Javet::V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("JavetPromiseRejectCallback: V8 runtime is empty.");
                }
                else {
                    auto externalV8Runtime = v8Runtime->externalV8Runtime;
                    jobject value;
                    if (promiseRejectEvent == v8::PromiseRejectEvent::kPromiseHandlerAddedAfterReject) {
                        value = Javet::Converter::ToExternalV8ValueUndefined(jniEnv, externalV8Runtime);
                    }
                    else {
                        value = Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, message.GetValue());
                    }
                    jniEnv->CallVoidMethod(
                        externalV8Runtime,
                        jmethodIDV8RuntimeReceivePromiseRejectCallback,
                        promiseRejectEvent,
                        Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, v8LocalPromise),
                        value);
                }
            }
        }

        JavetCallbackContextReference::JavetCallbackContextReference(JNIEnv* jniEnv, jobject callbackContext) {
            this->callbackContext = jniEnv->NewGlobalRef(callbackContext);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            v8PersistentCallbackContextHandlePointer = nullptr;
            SetHandle();
        }

        void JavetCallbackContextReference::CallFunction(const v8::FunctionCallbackInfo<v8::Value>& args) {
            FETCH_JNI_ENV(GlobalJavaVM);
            v8::Isolate* v8Isolate = args.GetIsolate();
            V8IsolateScope v8IsolateScope(v8Isolate);
            V8HandleScope v8HandleScope(v8Isolate);
            auto v8Context = v8Isolate->GetCurrentContext();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("CallFunction: V8 context is empty.");
                args.GetReturnValue().SetUndefined();
            }
            else {
                auto v8Runtime = Javet::V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("CallFunction: V8 runtime is empty.");
                    args.GetReturnValue().SetUndefined();
                }
                else {
                    jobject externalV8Runtime = v8Runtime->externalV8Runtime;
                    V8ContextScope v8ContextScope(v8Context);
                    jboolean isReturnResult = IsReturnResult();
                    jboolean isThisObjectRequired = IsThisObjectRequired();
                    jobject externalArgs = Javet::Converter::ToExternalV8ValueArray(jniEnv, externalV8Runtime, v8Context, args);
                    jobject thisObject = isThisObjectRequired ? Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, args.This()) : nullptr;
                    jobject mResult = jniEnv->CallStaticObjectMethod(
                        jclassV8FunctionCallback,
                        jmethodIDV8FunctionCallbackReceiveCallback,
                        externalV8Runtime,
                        callbackContext,
                        thisObject,
                        externalArgs);
                    if (thisObject != nullptr) {
                        jniEnv->DeleteLocalRef(thisObject);
                    }
                    if (externalArgs != nullptr) {
                        jniEnv->DeleteLocalRef(externalArgs);
                    }
                    if (jniEnv->ExceptionCheck()) {
                        if (mResult != nullptr) {
                            jniEnv->CallStaticVoidMethod(jclassJavetResourceUtils, jmethodIDJavetResourceUtilsSafeClose, mResult);
                            jniEnv->DeleteLocalRef(mResult);
                        }
                        Javet::Exceptions::ThrowV8Exception(jniEnv, v8Context, "Uncaught JavaError in function callback");
                    }
                    else {
                        if (isReturnResult) {
                            if (mResult == nullptr) {
                                args.GetReturnValue().SetUndefined();
                            }
                            else {
                                args.GetReturnValue().Set(Javet::Converter::ToV8Value(jniEnv, v8Context, mResult));
                            }
                        }
                        if (mResult != nullptr) {
                            jniEnv->CallStaticVoidMethod(jclassJavetResourceUtils, jmethodIDJavetResourceUtilsSafeClose, mResult);
                            jniEnv->DeleteLocalRef(mResult);
                        }
                    }
                }
            }
        }

        void JavetCallbackContextReference::CallPropertyGetter(V8LocalName propertyName, const v8::PropertyCallbackInfo<v8::Value>& args) {
            FETCH_JNI_ENV(GlobalJavaVM);
            v8::Isolate* v8Isolate = args.GetIsolate();
            V8IsolateScope v8IsolateScope(v8Isolate);
            V8HandleScope v8HandleScope(v8Isolate);
            auto v8Context = v8Isolate->GetCurrentContext();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("CallPropertyGetter: V8 context is empty.");
                args.GetReturnValue().SetUndefined();
            }
            else {
                auto v8Runtime = Javet::V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("CallPropertyGetter: V8 runtime is empty.");
                    args.GetReturnValue().SetUndefined();
                }
                else {
                    jobject externalV8Runtime = v8Runtime->externalV8Runtime;
                    V8ContextScope v8ContextScope(v8Context);
                    jboolean isThisObjectRequired = IsThisObjectRequired();
                    jobject thisObject = isThisObjectRequired ? Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, args.This()) : nullptr;
                    jobject mResult = jniEnv->CallStaticObjectMethod(
                        jclassV8FunctionCallback,
                        jmethodIDV8FunctionCallbackReceiveCallback,
                        externalV8Runtime,
                        callbackContext,
                        thisObject,
                        nullptr);
                    if (thisObject != nullptr) {
                        jniEnv->DeleteLocalRef(thisObject);
                    }
                    if (jniEnv->ExceptionCheck()) {
                        if (mResult != nullptr) {
                            jniEnv->CallStaticVoidMethod(jclassJavetResourceUtils, jmethodIDJavetResourceUtilsSafeClose, mResult);
                            jniEnv->DeleteLocalRef(mResult);
                        }
                        Javet::Exceptions::ThrowV8Exception(jniEnv, v8Context, "Uncaught JavaError in property getter callback");
                    }
                    else {
                        if (mResult == nullptr) {
                            args.GetReturnValue().SetUndefined();
                        }
                        else {
                            args.GetReturnValue().Set(Javet::Converter::ToV8Value(jniEnv, v8Context, mResult));
                            jniEnv->CallStaticVoidMethod(jclassJavetResourceUtils, jmethodIDJavetResourceUtilsSafeClose, mResult);
                            jniEnv->DeleteLocalRef(mResult);
                        }
                    }
                }
            }
        }

        void JavetCallbackContextReference::CallPropertySetter(V8LocalName propertyName, V8LocalValue propertyValue, const v8::PropertyCallbackInfo<void>& args) {
            FETCH_JNI_ENV(GlobalJavaVM);
            v8::Isolate* v8Isolate = args.GetIsolate();
            V8IsolateScope v8IsolateScope(v8Isolate);
            V8HandleScope v8HandleScope(v8Isolate);
            auto v8Context = v8Isolate->GetCurrentContext();
            if (v8Context.IsEmpty()) {
                LOG_ERROR("CallPropertySetter: V8 context is empty.");
            }
            else {
                auto v8Runtime = Javet::V8Runtime::FromV8Context(v8Context);
                if (v8Runtime == nullptr) {
                    LOG_ERROR("CallPropertySetter: V8 runtime is empty.");
                }
                else {
                    jobject externalV8Runtime = v8Runtime->externalV8Runtime;
                    V8ContextScope v8ContextScope(v8Context);
                    auto v8Array = v8::Array::New(v8Context->GetIsolate(), 1);
                    auto maybeResult = v8Array->Set(v8Context, 0, propertyValue);
                    if (maybeResult.IsNothing()) {
                        Javet::Exceptions::HandlePendingException(jniEnv, externalV8Runtime, v8Context);
                    }
                    else {
                        jboolean isThisObjectRequired = IsThisObjectRequired();
                        jobject thisObject = isThisObjectRequired ? Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, args.This()) : nullptr;
                        jobject mPropertyValue = Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, v8Array);
                        jobject mResult = jniEnv->CallStaticObjectMethod(
                            jclassV8FunctionCallback,
                            jmethodIDV8FunctionCallbackReceiveCallback,
                            externalV8Runtime,
                            callbackContext,
                            thisObject,
                            mPropertyValue);
                        if (thisObject != nullptr) {
                            jniEnv->DeleteLocalRef(thisObject);
                        }
                        if (mResult != nullptr) {
                            jniEnv->CallStaticVoidMethod(jclassJavetResourceUtils, jmethodIDJavetResourceUtilsSafeClose, mResult);
                            jniEnv->DeleteLocalRef(mResult);
                        }
                        if (jniEnv->ExceptionCheck()) {
                            Javet::Exceptions::ThrowV8Exception(jniEnv, v8Context, "Uncaught JavaError in property setter callback");
                        }
                    }
                }
            }
        }

        jboolean JavetCallbackContextReference::IsReturnResult() {
            FETCH_JNI_ENV(GlobalJavaVM);
            return jniEnv->CallBooleanMethod(callbackContext, jmethodIDJavetCallbackContextIsReturnResult);
        }

        jboolean JavetCallbackContextReference::IsThisObjectRequired() {
            FETCH_JNI_ENV(GlobalJavaVM);
            return jniEnv->CallBooleanMethod(callbackContext, jmethodIDJavetCallbackContextIsThisObjectRequired);
        }

        void JavetCallbackContextReference::SetHandle() {
            FETCH_JNI_ENV(GlobalJavaVM);
            jniEnv->CallVoidMethod(callbackContext, jmethodIDJavetCallbackContextSetHandle, TO_JAVA_LONG(callbackContext));
        }

        void JavetCallbackContextReference::RemoveCallbackContext(const jobject& externalV8Runtime) {
            FETCH_JNI_ENV(GlobalJavaVM);
            jniEnv->CallVoidMethod(externalV8Runtime, jmethodIDV8RuntimeRemoveCallbackContext, TO_JAVA_LONG(callbackContext));
            jniEnv->DeleteGlobalRef(callbackContext);
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
        }

        JavetCallbackContextReference::~JavetCallbackContextReference() {
            if (v8PersistentCallbackContextHandlePointer != nullptr) {
                v8PersistentCallbackContextHandlePointer->Reset();
                delete v8PersistentCallbackContextHandlePointer;
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeletePersistentCallbackContextReference);
                v8PersistentCallbackContextHandlePointer = nullptr;
            }
        }

        V8ValueReference::V8ValueReference(JNIEnv* jniEnv, jobject objectReference) {
            this->objectReference = jniEnv->NewGlobalRef(objectReference);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            v8PersistentDataPointer = nullptr;
        }

        void V8ValueReference::Clear() {
            if (v8PersistentDataPointer != nullptr) {
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->DeleteGlobalRef(objectReference);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
            }
        }

        void V8ValueReference::Close() {
            if (v8PersistentDataPointer != nullptr) {
                v8PersistentDataPointer->Reset();
                // v8PersistentDataPointer is borrowed. So it cannot be deleted.
                v8PersistentDataPointer = nullptr;
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->CallVoidMethod(objectReference, jmethodIDIV8ValueReferenceClose, true);
                jniEnv->DeleteGlobalRef(objectReference);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
            }
        }
    }
}

