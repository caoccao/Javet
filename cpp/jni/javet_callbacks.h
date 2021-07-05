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

#include <jni.h>
#include "javet_v8.h"

namespace Javet {
	namespace Callback {
		class JavetCallbackContextReference;
		class V8ValueReference;

		static jclass jclassJavetCallbackContext;
		static jmethodID jmethodIDJavetCallbackContextIsReturnResult;
		static jmethodID jmethodIDJavetCallbackContextIsThisObjectRequired;
		static jmethodID jmethodIDJavetCallbackContextSetHandle;

		static jclass jclassIV8Module;
		static jmethodID jmethodIDIV8ModuleGetHandle;

		static jclass jclassIV8ValueReference;
		static jmethodID jmethodIDIV8ValueReferenceClose;

		static jclass jclassJavetResourceUtils;
		static jmethodID jmethodIDJavetResourceUtilsSafeClose;

		static jclass jclassV8FunctionCallback;
		static jmethodID jmethodIDV8FunctionCallbackReceiveCallback;

		static jclass jclassV8Runtime;
		static jmethodID jmethodIDV8RuntimeGetV8Module;
		static jmethodID jmethodIDV8RuntimeReceivePromiseRejectCallback;
		static jmethodID jmethodIDV8RuntimeRemoveCallbackContext;

		void Initialize(JNIEnv* jniEnv);

		void JavetCloseWeakCallbackContextHandle(const v8::WeakCallbackInfo<JavetCallbackContextReference>& info);
		void JavetCloseWeakDataReference(const v8::WeakCallbackInfo<V8ValueReference>& info);
		void JavetFunctionCallback(const v8::FunctionCallbackInfo<v8::Value>& info);
		void JavetPropertyGetterCallback(V8LocalName propertyName, const v8::PropertyCallbackInfo<v8::Value>& info);
		void JavetPropertySetterCallback(V8LocalName propertyName, V8LocalValue propertyValue, const v8::PropertyCallbackInfo<void>& info);
		void JavetPromiseRejectCallback(v8::PromiseRejectMessage message);

		V8MaybeLocalModule JavetModuleResolveCallback(
			V8LocalContext v8Context,
			V8LocalString specifier,
#ifndef ENABLE_NODE
			V8LocalFixedArray importAssertions,
#endif
			V8LocalModule referrer);

		class JavetCallbackContextReference {
		public:
			jobject callbackContext;
			V8PersistentBigInt* v8PersistentCallbackContextHandlePointer;
			JavetCallbackContextReference(JNIEnv* jniEnv, jobject callbackContext);
			void CallFunction(const v8::FunctionCallbackInfo<v8::Value>& args);
			void CallPropertyGetter(V8LocalName propertyName, const v8::PropertyCallbackInfo<v8::Value>& args);
			void CallPropertySetter(V8LocalName propertyName, V8LocalValue propertyValue, const v8::PropertyCallbackInfo<void>& args);
			jboolean IsReturnResult();
			jboolean IsThisObjectRequired();
			void SetHandle();
			void RemoveCallbackContext(const jobject& externalV8Runtime);
			virtual ~JavetCallbackContextReference();
		};

		class V8ValueReference {
		public:
			jobject objectReference;
			V8PersistentData* v8PersistentDataPointer;
			V8ValueReference(JNIEnv* jniEnv, jobject objectReference);
			void Clear();
			void Close();
		};
	}
}
