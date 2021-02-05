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

#pragma once

#include <v8.h>
#include <jni.h>
#include "javet_v8_runtime.h"

namespace Javet {
	namespace Callback {

		class V8Callback {
		public:
			jobject callbackContext;
			jlong handle;
			Javet::V8Runtime* internalV8Runtime;

			jstring GetFunctionName(JNIEnv* jniEnv);
			jobject GetExternalV8Runtime(JNIEnv* jniEnv);
			void Invoke(JNIEnv* jniEnv, const v8::FunctionCallbackInfo<v8::Value>& v8FunctionCallbackInfo);
			jboolean IsReturnResult(JNIEnv* jniEnv);
		};

		static jclass jclassV8Runtime;
		static jmethodID jmethodIDV8RuntimeReceiveCallback;

		static jclass jclassV8CallbackContext;
		static jmethodID jmethodIDV8CallbackContextGetFunctionName;
		static jmethodID jmethodIDV8CallbackContextGetV8Runtime;
		static jmethodID jmethodIDV8CallbackContextIsReturnResult;

		static jclass jclassJavetResourceUtils;
		static jmethodID jmethodIDJavetResourceUtilsSafeClose;

		void Initialize(JNIEnv* jniEnv);

		void GlobalPropertyAccessorCallback(
			v8::Local<v8::String> propertyName,
			const v8::PropertyCallbackInfo<v8::Value>& propertyCallbackInfo);
	}
}
