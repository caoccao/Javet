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

#define JAVET_CONVERTER_BIGINT_STANDARD

namespace Javet {
	namespace Converter {
		static jclass jclassV8ValueNull;
		static jmethodID jmethodIDV8ValueNullConstructor;

		static jclass jclassV8ValueUndefined;
		static jmethodID jmethodIDV8ValueUndefinedConstructor;

		// Primitive

		static jclass jclassV8ValueBoolean;
		static jmethodID jmethodIDV8ValueBooleanConstructor;

		static jclass jclassV8ValueDouble;
		static jmethodID jmethodIDV8ValueDoubleConstructor;

		static jclass jclassV8ValueInteger;
		static jmethodID jmethodIDV8ValueIntegerConstructor;

		static jclass jclassV8ValueLong;
		static jmethodID jmethodIDV8ValueLongConstructorFromLong;
		static jmethodID jmethodIDV8ValueLongConstructorFromString;

		static jclass jclassV8ValueString;
		static jmethodID jmethodIDV8ValueStringConstructor;

		static jclass jclassV8ValueUnknown;
		static jmethodID jmethodIDV8ValueUnknownConstructor;

		static jclass jclassV8ValueZonedDateTime;
		static jmethodID jmethodIDV8ValueZonedDateTimeConstructor;

		// Reference

		static jclass jclassV8ValueArguments;
		static jmethodID jmethodIDV8ValueArgumentsConstructor;

		static jclass jclassV8ValueArray;
		static jmethodID jmethodIDV8ValueArrayConstructor;

		static jclass jclassV8ValueError;
		static jmethodID jmethodIDV8ValueErrorConstructor;

		static jclass jclassV8ValueMap;
		static jmethodID jmethodIDV8ValueMapConstructor;

		static jclass jclassV8ValueObject;
		static jmethodID jmethodIDV8ValueObjectConstructor;

		static jclass jclassV8ValuePromise;
		static jmethodID jmethodIDV8ValuePromiseConstructor;

		static jclass jclassV8ValueProxy;
		static jmethodID jmethodIDV8ValueProxyConstructor;

		static jclass jclassV8ValueRegex;
		static jmethodID jmethodIDV8ValueRegexConstructor;

		static jclass jclassV8ValueSet;
		static jmethodID jmethodIDV8ValueSetConstructor;

		static jclass jclassV8ValueSymbol;
		static jmethodID jmethodIDV8ValueSymbolConstructor;

		void initializeJavetConverter(JNIEnv* jniEnv);

		jobject toJV8Value(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value);

		inline jobject toJV8ValueNull(JNIEnv* jniEnv);

		inline jobject toJV8ValueReference(
			JNIEnv* jniEnv, jclass jclassV8ValueReference, jmethodID jmethodIDV8ValueReferenceConstructor,
			v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value);

		jobject toJV8ValueUndefined(JNIEnv* jniEnv);

		inline v8::Local<v8::Boolean> toV8Boolean(v8::Isolate* v8Isolate, jboolean& managedBoolean);

		v8::Local<v8::Integer> toV8Integer(v8::Isolate* v8Isolate, jint& managedInteger);

		inline jlong toV8PersistentObjectReference(v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value);

		v8::ScriptOrigin* toV8ScriptOringinPointer(JNIEnv* jniEnv, v8::Isolate* v8Isolate,
			jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule);

		v8::Local<v8::String> toV8String(JNIEnv* jniEnv, v8::Isolate* v8Isolate, jstring& managedString);
	}
}
