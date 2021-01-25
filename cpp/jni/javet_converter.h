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
#include <v8.h>

namespace Javet {
	namespace Converter {
		static jclass jclassInteger;
		static jmethodID jmethodIDIntegerValueOf;

		static jclass jclassLong;
		static jmethodID jmethodIDLongValueOf;

		static jclass jclassV8ValueNull;
		static jmethodID jmethodIDV8ValueNullConstructor;

		static jclass jclassV8ValueUndefined;
		static jmethodID jmethodIDV8ValueUndefinedConstructor;

		// Primitive

		static jclass jclassV8ValueInteger;
		static jmethodID jmethodIDV8ValueIntegerConstructor;

		static jclass jclassV8ValueLong;
		static jmethodID jmethodIDV8ValueLongConstructorFromLong;
		static jmethodID jmethodIDV8ValueLongConstructorFromString;

		static jclass jclassV8ValueString;
		static jmethodID jmethodIDV8ValueStringConstructor;

		static jclass jclassV8ValueUnknown;
		static jmethodID jmethodIDV8ValueUnknownConstructor;

		// Reference

		static jclass jclassV8ValueArray;
		static jmethodID jmethodIDV8ValueArrayConstructor;

		void initializeJavetConverter(JNIEnv* jniEnv);

		jobject toJObject(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value);

		inline v8::Local<v8::Boolean> toV8Boolean(v8::Isolate* v8Isolate, jboolean& managedBoolean) {
			return v8::Boolean::New(v8Isolate, managedBoolean);
		}

		inline v8::Local<v8::Integer> toV8Integer(v8::Isolate* v8Isolate, jint& managedInteger) {
			return v8::Integer::New(v8Isolate, managedInteger);
		}

		v8::ScriptOrigin* toV8ScriptOringinPointer(JNIEnv* jniEnv, v8::Isolate* v8Isolate,
			jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule);

		v8::Local<v8::String> toV8String(JNIEnv* jniEnv, v8::Isolate* v8Isolate, jstring& managedString);
	}
}
