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

		class V8ValueReference {
		public:
			jobject objectReference;
			v8::Persistent<v8::Object>* v8PersistentObjectPointer;
			void Clear(JNIEnv* jniEnv);
			void Close(JNIEnv* jniEnv);
		};

		class V8Callback {
		public:
			jobject callbackContext;
			v8::Persistent<v8::External> v8PersistentExternalData;
			jlong handle;
			Javet::V8Runtime* internalV8Runtime;

			void Dispose(JNIEnv* jniEnv);
			jstring GetFunctionName(JNIEnv* jniEnv);
			jobject GetExternalV8Runtime(JNIEnv* jniEnv);
			void Invoke(JNIEnv* jniEnv, const v8::FunctionCallbackInfo<v8::Value>& args);
			jboolean IsReturnResult(JNIEnv* jniEnv);
			void NotifyToDispose(JNIEnv* jniEnv);
		};

		static jclass jclassIV8ValueReference;
		static jmethodID jmethodIDIV8ValueReferenceClose;

		static jclass jclassJavetResourceUtils;
		static jmethodID jmethodIDJavetResourceUtilsSafeClose;

		static jclass jclassThrowable;
		static jmethodID jmethodIDThrowableGetMessage;

		static jclass jclassV8Runtime;
		static jmethodID jmethodIDV8RuntimeReceiveCallback;
		static jmethodID jmethodIDV8RuntimeRemoveCallback;

		static jclass jclassV8CallbackContext;
		static jmethodID jmethodIDV8CallbackContextGetFunctionName;
		static jmethodID jmethodIDV8CallbackContextGetV8Runtime;
		static jmethodID jmethodIDV8CallbackContextIsReturnResult;

		void Initialize(JNIEnv* jniEnv);

		void GlobalPropertyAccessorCallback(
			v8::Local<v8::String> propertyName,
			const v8::PropertyCallbackInfo<v8::Value>& propertyCallbackInfo);
	}
}
