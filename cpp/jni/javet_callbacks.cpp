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

		jstring V8Callback::GetFunctionName(JNIEnv* jniEnv) {
			return (jstring)jniEnv->CallObjectMethod(callbackContext, jmethodIDV8CallbackContextGetFunctionName);
		}

		jobject V8Callback::GetExternalV8Runtime(JNIEnv* jniEnv) {
			return jniEnv->CallObjectMethod(callbackContext, jmethodIDV8CallbackContextGetV8Runtime);
		}

		void V8Callback::Invoke(JNIEnv* jniEnv) {
			jobject externalV8Runtime = GetExternalV8Runtime(jniEnv);
			jboolean isReturnResult = IsReturnResult(jniEnv);
			jobject mResult = jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeReceiveCallback, handle);
			if (isReturnResult) {
			}
			else {
			}
			Javet::Converter::ToExternalV8ValueUndefined(jniEnv);
		}

		jboolean V8Callback::IsReturnResult(JNIEnv* jniEnv) {
			return jniEnv->CallBooleanMethod(callbackContext, jmethodIDV8CallbackContextIsReturnResult);
		}

		void Initialize(JNIEnv* jniEnv) {
			jclassV8Runtime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Runtime"));
			jmethodIDV8RuntimeReceiveCallback = jniEnv->GetMethodID(jclassV8Runtime, "receiveCallback", "(J)Ljava/lang/Object;");

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

