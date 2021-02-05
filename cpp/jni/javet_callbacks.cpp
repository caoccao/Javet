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
#include "javet_globals.h"

namespace Javet {
	namespace Callback {
		void V8Callback::Dispose(JNIEnv* jniEnv) {
			if (!v8PersistentExternalData.IsEmpty()) {
				v8PersistentExternalData.ClearWeak();
				v8PersistentExternalData.Reset();
			}
			if (callbackContext != nullptr) {
				jniEnv->DeleteGlobalRef(callbackContext);
			}
		}

		jstring V8Callback::GetFunctionName(JNIEnv* jniEnv) {
			return (jstring)jniEnv->CallObjectMethod(callbackContext, jmethodIDV8CallbackContextGetFunctionName);
		}

		jobject V8Callback::GetExternalV8Runtime(JNIEnv* jniEnv) {
			return jniEnv->CallObjectMethod(callbackContext, jmethodIDV8CallbackContextGetV8Runtime);
		}

		void V8Callback::Invoke(JNIEnv* jniEnv, const v8::FunctionCallbackInfo<v8::Value>& args) {
			v8::Isolate* v8Isolate = internalV8Runtime->v8Isolate;
			v8::Isolate::Scope v8IsolateScope(v8Isolate);
			v8::Local<v8::Context> v8Context = internalV8Runtime->v8Context.Get(v8Isolate);
			jobject externalV8Runtime = GetExternalV8Runtime(jniEnv);
			jboolean isReturnResult = IsReturnResult(jniEnv);
			jobject externalArgs = Javet::Converter::ToExternalV8ValueArray(jniEnv, v8Context, args);
			jobject mResult = jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeReceiveCallback, handle, externalArgs);
			if (jniEnv->ExceptionCheck()) {
				jthrowable externalException = jniEnv->ExceptionOccurred();
				jstring externalErrorMessage = (jstring)jniEnv->CallObjectMethod(externalException, jmethodIDThrowableGetMessage);
				jniEnv->ExceptionClear();
				v8::Local<v8::String> v8ErrorMessage;
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
					// Recycling the result has to be performed in this step.
					jniEnv->CallStaticVoidMethod(jclassJavetResourceUtils, jmethodIDJavetResourceUtilsSafeClose, mResult);
				}
			}
			if (externalArgs != nullptr) {
				jniEnv->DeleteLocalRef(externalArgs);
			}
			if (mResult != nullptr) {
				jniEnv->DeleteLocalRef(mResult);
			}
		}

		jboolean V8Callback::IsReturnResult(JNIEnv* jniEnv) {
			return jniEnv->CallBooleanMethod(callbackContext, jmethodIDV8CallbackContextIsReturnResult);
		}

		void V8Callback::NotifyToDispose(JNIEnv* jniEnv) {
			jniEnv->CallVoidMethod(
				GetExternalV8Runtime(jniEnv),
				jmethodIDV8RuntimeRemoveCallback,
				callbackContext);
		}

		void Initialize(JNIEnv* jniEnv) {
			jclassJavetResourceUtils = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/utils/JavetResourceUtils"));
			jmethodIDJavetResourceUtilsSafeClose = jniEnv->GetStaticMethodID(jclassJavetResourceUtils, "safeClose", "(Ljava/lang/Object;)V");

			jclassThrowable = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("java/lang/Throwable"));
			jmethodIDThrowableGetMessage = jniEnv->GetMethodID(jclassThrowable, "getMessage", "()Ljava/lang/String;");

			jclassV8Runtime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Runtime"));
			jmethodIDV8RuntimeReceiveCallback = jniEnv->GetMethodID(jclassV8Runtime, "receiveCallback", "(JLcom/caoccao/javet/values/reference/V8ValueArray;)Lcom/caoccao/javet/values/V8Value;");
			jmethodIDV8RuntimeRemoveCallback = jniEnv->GetMethodID(jclassV8Runtime, "removeCallback", "(Lcom/caoccao/javet/interop/V8CallbackContext;)V");

			jclassV8CallbackContext = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8CallbackContext"));
			jmethodIDV8CallbackContextGetFunctionName = jniEnv->GetMethodID(jclassV8CallbackContext, "getFunctionName", "()Ljava/lang/String;");
			jmethodIDV8CallbackContextGetV8Runtime = jniEnv->GetMethodID(jclassV8CallbackContext, "getV8Runtime", "()Lcom/caoccao/javet/interop/V8Runtime;");
			jmethodIDV8CallbackContextIsReturnResult = jniEnv->GetMethodID(jclassV8CallbackContext, "isReturnResult", "()Z");
		}

		void GlobalPropertyAccessorCallback(
			v8::Local<v8::String> propertyName,
			const v8::PropertyCallbackInfo<v8::Value>& propertyCallbackInfo) {
			propertyCallbackInfo.GetReturnValue().Set(
				propertyCallbackInfo.GetIsolate()->GetCurrentContext()->Global());
		}
	}
}

