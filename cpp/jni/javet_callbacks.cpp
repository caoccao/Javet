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
		void Initialize(JNIEnv* jniEnv, JavaVM* javaVM) {
			GlobalJavaVM = javaVM;

			jclassJavetCallbackContext = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/utils/JavetCallbackContext"));
			jmethodIDJavetCallbackContextGetCallbackOwnerFunction = jniEnv->GetMethodID(
				jclassJavetCallbackContext,
				"getCallbackOwnerFunction",
				"()Lcom/caoccao/javet/values/reference/IV8ValueFunction;");
			jmethodIDJavetCallbackContextIsReturnResult = jniEnv->GetMethodID(jclassJavetCallbackContext, "isReturnResult", "()Z");
			jmethodIDJavetCallbackContextIsThisObjectRequired = jniEnv->GetMethodID(jclassJavetCallbackContext, "isThisObjectRequired", "()Z");
			jmethodIDJavetCallbackContextSetHandle = jniEnv->GetMethodID(jclassJavetCallbackContext, "setHandle", "(J)V");

			jclassIV8Module = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/IV8Module"));
			jmethodIDIV8ModuleGetHandle = jniEnv->GetMethodID(jclassIV8Module, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

			jclassIV8ValueFunction = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/IV8ValueFunction"));
			jmethodIDIV8ValueFunctionReceiveCallback = jniEnv->GetMethodID(jclassIV8ValueFunction, "receiveCallback",
				"(Lcom/caoccao/javet/values/V8Value;Lcom/caoccao/javet/values/reference/V8ValueArray;)Lcom/caoccao/javet/values/V8Value;");

			jclassIV8ValueReference = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/IV8ValueReference"));
			jmethodIDIV8ValueReferenceClose = jniEnv->GetMethodID(jclassIV8ValueReference, "close", "(Z)V");

			jclassJavetResourceUtils = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/utils/JavetResourceUtils"));
			jmethodIDJavetResourceUtilsSafeClose = jniEnv->GetStaticMethodID(jclassJavetResourceUtils, "safeClose", "(Ljava/lang/Object;)V");

			jclassThrowable = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("java/lang/Throwable"));
			jmethodIDThrowableGetMessage = jniEnv->GetMethodID(jclassThrowable, "getMessage", "()Ljava/lang/String;");

			jclassV8Runtime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Runtime"));
			jmethodIDV8RuntimeGetV8Module = jniEnv->GetMethodID(jclassV8Runtime, "getV8Module", "(Ljava/lang/String;)Lcom/caoccao/javet/values/reference/IV8Module;");
		}

		V8MaybeLocalModule ModuleResolveCallback(
			V8LocalContext v8Context,
			V8LocalString specifier,
#ifndef ENABLE_NODE
			V8LocalFixedArray importAssertions,
#endif
			V8LocalModule referrer) {
			auto v8Runtime = V8Runtime::FromV8Context(v8Context);
			FETCH_JNI_ENV(GlobalJavaVM);
			jobject mIV8Module = jniEnv->CallObjectMethod(
				v8Runtime->externalV8Runtime, jmethodIDV8RuntimeGetV8Module,
				Javet::Converter::ToJavaString(jniEnv, v8Context, specifier));
			if (mIV8Module == nullptr) {
				LOG_ERROR("ModuleResolveCallback: module " << *Javet::Converter::ToStdString(v8Context, specifier) << " not found");
				return V8MaybeLocalModule();
			}
			else {
				auto mHandle = jniEnv->CallLongMethod(mIV8Module, jmethodIDIV8ModuleGetHandle);
				auto v8PersistentModule = TO_V8_PERSISTENT_MODULE_POINTER(mHandle);
				LOG_DEBUG("ModuleResolveCallback: module " << *Javet::Converter::ToStdString(v8Context, specifier) << " found");
				return v8PersistentModule->Get(v8Context->GetIsolate());
			}
		}

		JavetCallbackContextReference::JavetCallbackContextReference(JNIEnv* jniEnv, jobject callbackContext) {
			this->jniEnv = jniEnv;
			this->callbackContext = callbackContext;
		}

		jobject JavetCallbackContextReference::GetCallbackOwnerFunction() {
			return jniEnv->CallObjectMethod(callbackContext, jmethodIDJavetCallbackContextGetCallbackOwnerFunction);
		}

		void JavetCallbackContextReference::Invoke(const v8::FunctionCallbackInfo<v8::Value>& args) {
			v8::Isolate* v8Isolate = args.GetIsolate();
			V8IsolateScope v8IsolateScope(v8Isolate);
			V8HandleScope v8HandleScope(v8Isolate);
			auto v8Context = v8Isolate->GetCurrentContext();
			jobject callbackOwnerFunction = GetCallbackOwnerFunction();
			jobject externalV8Runtime = Javet::V8Runtime::FromV8Context(v8Context)->externalV8Runtime;
			jboolean isReturnResult = IsReturnResult();
			jboolean isThisObjectRequired = IsThisObjectRequired();
			jobject externalArgs = Javet::Converter::ToExternalV8ValueArray(jniEnv, externalV8Runtime, v8Context, args);
			jobject thisObject = isThisObjectRequired ? Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, args.This()) : nullptr;
			jobject mResult = jniEnv->CallObjectMethod(callbackOwnerFunction, jmethodIDIV8ValueFunctionReceiveCallback, thisObject, externalArgs);
			if (jniEnv->ExceptionCheck()) {
				jthrowable externalException = jniEnv->ExceptionOccurred();
				jstring externalErrorMessage = (jstring)jniEnv->CallObjectMethod(externalException, jmethodIDThrowableGetMessage);
				jniEnv->ExceptionClear();
				V8LocalString v8ErrorMessage;
				if (externalErrorMessage == nullptr) {
					v8ErrorMessage = v8::String::NewFromUtf8(v8Isolate, "Uncaught JavaError: unknown").ToLocalChecked();
				}
				else {
					v8ErrorMessage = Javet::Converter::ToV8String(jniEnv, v8Context, externalErrorMessage);
					jniEnv->DeleteLocalRef(externalErrorMessage);
				}
				v8Isolate->ThrowException(v8::Exception::Error(v8ErrorMessage));
				jniEnv->DeleteLocalRef(externalException);
			}
			else if (isReturnResult) {
				if (mResult == nullptr) {
					args.GetReturnValue().SetUndefined();
				}
				else {
					args.GetReturnValue().Set(Javet::Converter::ToV8Value(jniEnv, v8Context, mResult));
				}
			}
			if (thisObject != nullptr) {
				jniEnv->DeleteLocalRef(thisObject);
			}
			if (externalArgs != nullptr) {
				jniEnv->DeleteLocalRef(externalArgs);
			}
			if (mResult != nullptr) {
				jniEnv->CallStaticVoidMethod(jclassJavetResourceUtils, jmethodIDJavetResourceUtilsSafeClose, mResult);
				jniEnv->DeleteLocalRef(mResult);
			}
		}

		jboolean JavetCallbackContextReference::IsReturnResult() {
			return jniEnv->CallBooleanMethod(callbackContext, jmethodIDJavetCallbackContextIsReturnResult);
		}

		jboolean JavetCallbackContextReference::IsThisObjectRequired() {
			return jniEnv->CallBooleanMethod(callbackContext, jmethodIDJavetCallbackContextIsThisObjectRequired);
		}

		void JavetCallbackContextReference::SetHandle() {
			jniEnv->CallVoidMethod(callbackContext, jmethodIDJavetCallbackContextSetHandle, TO_JAVA_LONG(callbackContext));
		}

		void V8ValueReference::Clear(JNIEnv* jniEnv) {
			if (v8PersistentDataPointer != nullptr) {
				jniEnv->DeleteGlobalRef(objectReference);
			}
		}

		void V8ValueReference::Close(JNIEnv* jniEnv) {
			if (v8PersistentDataPointer != nullptr) {
				v8PersistentDataPointer->Reset();
				v8PersistentDataPointer = nullptr;
				jniEnv->CallVoidMethod(TO_JAVA_OBJECT(objectReference), jmethodIDIV8ValueReferenceClose, true);
				jniEnv->DeleteGlobalRef(objectReference);
			}
		}
	}
}

