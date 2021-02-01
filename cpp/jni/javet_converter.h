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
		static jmethodID jmethodIDV8ValueBooleanToPrimitive;

		static jclass jclassV8ValueDouble;
		static jmethodID jmethodIDV8ValueDoubleConstructor;
		static jmethodID jmethodIDV8ValueDoubleToPrimitive;

		static jclass jclassV8ValueInteger;
		static jmethodID jmethodIDV8ValueIntegerConstructor;
		static jmethodID jmethodIDV8ValueIntegerToPrimitive;

		static jclass jclassV8ValueLong;
		static jmethodID jmethodIDV8ValueLongConstructorFromLong;
		static jmethodID jmethodIDV8ValueLongConstructorFromString;
		static jmethodID jmethodIDV8ValueLongToPrimitive;

		static jclass jclassV8ValueString;
		static jmethodID jmethodIDV8ValueStringConstructor;
		static jmethodID jmethodIDV8ValueStringToPrimitive;

		static jclass jclassV8ValueUnknown;
		static jmethodID jmethodIDV8ValueUnknownConstructor;

		static jclass jclassV8ValueZonedDateTime;
		static jmethodID jmethodIDV8ValueZonedDateTimeConstructor;
		static jmethodID jmethodIDV8ValueZonedDateTimeToPrimitive;

		// Reference

		static jclass jclassV8ValueArguments;
		static jmethodID jmethodIDV8ValueArgumentsConstructor;

		static jclass jclassV8ValueArray;
		static jmethodID jmethodIDV8ValueArrayConstructor;

		static jclass jclassV8ValueError;
		static jmethodID jmethodIDV8ValueErrorConstructor;

		static jclass jclassV8ValueGlobalObject;
		static jmethodID jmethodIDV8ValueGlobalObjectConstructor;

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

		jobject toJV8ValueGlobalObject(JNIEnv* jniEnv, v8::Persistent<v8::Object>* v8PersistentObjectPointer);

		inline jobject toJV8ValueReference(
			JNIEnv* jniEnv, jclass jclassV8ValueReference, jmethodID jmethodIDV8ValueReferenceConstructor,
			v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value);

		jobject toJV8ValueUndefined(JNIEnv* jniEnv);

		inline v8::Local<v8::Boolean> toV8Boolean(v8::Local<v8::Context> v8Context, jboolean& managedBoolean);

		inline v8::Local<v8::Value> toV8Date(v8::Local<v8::Context> v8Context, jlong& managedLong);

		inline v8::Local<v8::Number> toV8Double(v8::Local<v8::Context> v8Context, jdouble& managedDouble);

		inline v8::Local<v8::Integer> toV8Integer(v8::Local<v8::Context> v8Context, jint& managedInteger);

		inline v8::Local<v8::BigInt> toV8Long(v8::Local<v8::Context> v8Context, jlong& managedLong);

		inline jlong toV8PersistentObjectReference(v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value);

		v8::ScriptOrigin* toV8ScriptOringinPointer(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context,
			jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule);

		v8::Local<v8::String> toV8String(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, jstring& managedString);

		v8::Local<v8::Value> toV8Value(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, jobject obj);
	}
}
