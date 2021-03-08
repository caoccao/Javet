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

namespace Javet {
	namespace Callback {

		class JavetCallbackContextReference {
		public:
			jobject callbackContext;
			JNIEnv* jniEnv;
			JavetCallbackContextReference(JNIEnv* jniEnv, jobject callbackContext);
			jobject GetCallbackOwnerFunction();
			void Invoke(const v8::FunctionCallbackInfo<v8::Value>& args);
			jboolean IsReturnResult();
			jboolean IsThisObjectRequired();
			void SetHandle();
		};

		class V8ValueReference {
		public:
			v8::Isolate* v8Isolate;
			jobject objectReference;
			v8::Persistent<v8::Object>* v8PersistentObjectPointer;
			void Clear(JNIEnv* jniEnv);
			void Close(JNIEnv* jniEnv);
		};

		static jclass jclassJavetCallbackContext;
		static jmethodID jmethodIDJavetCallbackContextGetCallbackOwnerFunction;
		static jmethodID jmethodIDJavetCallbackContextIsReturnResult;
		static jmethodID jmethodIDJavetCallbackContextIsThisObjectRequired;
		static jmethodID jmethodIDJavetCallbackContextSetHandle;

		static jclass jclassIV8ValueFunction;
		static jmethodID jmethodIDIV8ValueFunctionReceiveCallback;

		static jclass jclassIV8ValueReference;
		static jmethodID jmethodIDIV8ValueReferenceClose;

		static jclass jclassJavetResourceUtils;
		static jmethodID jmethodIDJavetResourceUtilsSafeClose;

		static jclass jclassThrowable;
		static jmethodID jmethodIDThrowableGetMessage;

		void Initialize(JNIEnv* jniEnv);
	}
}
