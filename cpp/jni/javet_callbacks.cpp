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

		void V8Callback::Invoke(JNIEnv* jniEnv, const v8::FunctionCallbackInfo<v8::Value>& v8FunctionCallbackInfo) {
			v8::Isolate* v8Isolate = internalV8Runtime->v8Isolate;
			v8::Isolate::Scope v8IsolateScope(v8Isolate);
			v8::Local<v8::Context> v8Context = internalV8Runtime->v8Context.Get(v8Isolate);
			jobject externalV8Runtime = GetExternalV8Runtime(jniEnv);
			jboolean isReturnResult = IsReturnResult(jniEnv);
			jobject externalArgs = Javet::Converter::ToExternalV8ValueArray(jniEnv, v8Context, v8FunctionCallbackInfo);
			jobject mResult = jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeReceiveCallback, handle, externalArgs);
			if (jniEnv->ExceptionCheck()) {
				// TODO
			}
			else if (isReturnResult) {
				if (mResult == nullptr) {
					v8FunctionCallbackInfo.GetReturnValue().SetUndefined();
				}
				else {
					v8FunctionCallbackInfo.GetReturnValue().Set(Javet::Converter::ToV8Value(jniEnv, v8Context, mResult));
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
			jclassV8Runtime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Runtime"));
			jmethodIDV8RuntimeReceiveCallback = jniEnv->GetMethodID(jclassV8Runtime, "receiveCallback", "(JLcom/caoccao/javet/values/reference/V8ValueArray;)Lcom/caoccao/javet/values/V8Value;");
			jmethodIDV8RuntimeRemoveCallback = jniEnv->GetMethodID(jclassV8Runtime, "removeCallback", "(Lcom/caoccao/javet/interop/V8CallbackContext;)V");

			jclassV8CallbackContext = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8CallbackContext"));
			jmethodIDV8CallbackContextGetFunctionName = jniEnv->GetMethodID(jclassV8CallbackContext, "getFunctionName", "()Ljava/lang/String;");
			jmethodIDV8CallbackContextGetV8Runtime = jniEnv->GetMethodID(jclassV8CallbackContext, "getV8Runtime", "()Lcom/caoccao/javet/interop/V8Runtime;");
			jmethodIDV8CallbackContextIsReturnResult = jniEnv->GetMethodID(jclassV8CallbackContext, "isReturnResult", "()Z");

			jclassJavetResourceUtils = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/utils/JavetResourceUtils"));
			jmethodIDJavetResourceUtilsSafeClose = jniEnv->GetStaticMethodID(jclassJavetResourceUtils, "safeClose", "(Ljava/lang/Object;)V");
		}

		void GlobalPropertyAccessorCallback(
			v8::Local<v8::String> propertyName,
			const v8::PropertyCallbackInfo<v8::Value>& propertyCallbackInfo) {
			propertyCallbackInfo.GetReturnValue().Set(
				propertyCallbackInfo.GetIsolate()->GetCurrentContext()->Global());
		}
	}
}

