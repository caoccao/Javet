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

		static jclass jclassV8ValueNull;
		static jmethodID jmethodIDV8ValueNullConstructor;

		static jclass jclassV8ValueString;
		static jmethodID jmethodIDV8ValueStringConstructor;
		static jmethodID jmethodIDV8ValueStringToPrimitive;

		static jclass jclassV8ValueUndefined;
		static jmethodID jmethodIDV8ValueUndefinedConstructor;

		static jclass jclassV8ValueUnknown;
		static jmethodID jmethodIDV8ValueUnknownConstructor;

		static jclass jclassV8ValueZonedDateTime;
		static jmethodID jmethodIDV8ValueZonedDateTimeConstructor;
		static jmethodID jmethodIDV8ValueZonedDateTimeToPrimitive;

		// Reference

		static jclass jclassV8ValueArguments;
		static jmethodID jmethodIDV8ValueArgumentsConstructor;
		static jmethodID jmethodIDV8ValueArgumentsGetHandle;

		static jclass jclassV8ValueArray;
		static jmethodID jmethodIDV8ValueArrayConstructor;
		static jmethodID jmethodIDV8ValueArrayGetHandle;

		static jclass jclassV8ValueArrayBuffer;
		static jmethodID jmethodIDV8ValueArrayBufferConstructor;
		static jmethodID jmethodIDV8ValueArrayBufferGetHandle;

		static jclass jclassV8ValueFunction;
		static jmethodID jmethodIDV8ValueFunctionConstructor;
		static jmethodID jmethodIDV8ValueFunctionGetHandle;

		static jclass jclassV8ValueError;
		static jmethodID jmethodIDV8ValueErrorConstructor;
		static jmethodID jmethodIDV8ValueErrorGetHandle;

		static jclass jclassV8ValueGlobalObject;
		static jmethodID jmethodIDV8ValueGlobalObjectConstructor;
		static jmethodID jmethodIDV8ValueGlobalObjectGetHandle;

		static jclass jclassV8ValueMap;
		static jmethodID jmethodIDV8ValueMapConstructor;
		static jmethodID jmethodIDV8ValueMapGetHandle;

		static jclass jclassV8ValueIterator;
		static jmethodID jmethodIDV8ValueIteratorConstructor;
		static jmethodID jmethodIDV8ValueIteratorGetHandle;

		static jclass jclassV8ValueObject;
		static jmethodID jmethodIDV8ValueObjectConstructor;
		static jmethodID jmethodIDV8ValueObjectGetHandle;

		static jclass jclassV8ValuePromise;
		static jmethodID jmethodIDV8ValuePromiseConstructor;
		static jmethodID jmethodIDV8ValuePromiseGetHandle;

		static jclass jclassV8ValueProxy;
		static jmethodID jmethodIDV8ValueProxyConstructor;
		static jmethodID jmethodIDV8ValueProxyGetHandle;

		static jclass jclassV8ValueReference;

		static jclass jclassV8ValueRegExp;
		static jmethodID jmethodIDV8ValueRegExpConstructor;
		static jmethodID jmethodIDV8ValueRegExpGetHandle;

		static jclass jclassV8ValueSet;
		static jmethodID jmethodIDV8ValueSetConstructor;
		static jmethodID jmethodIDV8ValueSetGetHandle;

		static jclass jclassV8ValueSymbol;
		static jmethodID jmethodIDV8ValueSymbolConstructor;
		static jmethodID jmethodIDV8ValueSymbolGetHandle;

		static jclass jclassV8ValueTypedArray;
		static jmethodID jmethodIDV8ValueTypedArrayConstructor;
		static jmethodID jmethodIDV8ValueTypedArrayGetHandle;

		void Initialize(JNIEnv* jniEnv);

		jobject ToExternalV8Value(JNIEnv* jniEnv, v8::Local<v8::Context>& v8Context, v8::Local<v8::Value> v8Value);

		jobject ToExternalV8ValueArray(JNIEnv* jniEnv, v8::Local<v8::Context>& v8Context, const v8::FunctionCallbackInfo<v8::Value>& args);

		inline jobject ToExternalV8ValueNull(JNIEnv* jniEnv);

		jobject ToExternalV8ValueGlobalObject(JNIEnv* jniEnv, v8::Persistent<v8::Object>& v8PersistentObject);

		inline jobject ToExternalV8ValuePrimitive(
			JNIEnv* jniEnv, jclass jclassV8ValuePrimitive, jmethodID jmethodIDV8ValuePrimitiveConstructor,
			v8::Local<v8::Context>& v8Context, v8::Local<v8::Value> v8Value);

		jobject ToExternalV8ValueUndefined(JNIEnv* jniEnv);

		inline v8::Local<v8::Boolean> ToV8Boolean(v8::Local<v8::Context>& v8Context, jboolean& managedBoolean);

		inline v8::Local<v8::Value> ToV8Date(v8::Local<v8::Context>& v8Context, jlong& managedLong);

		inline v8::Local<v8::Number> ToV8Double(v8::Local<v8::Context>& v8Context, jdouble& managedDouble);

		inline v8::Local<v8::Integer> ToV8Integer(v8::Local<v8::Context>& v8Context, jint& managedInteger);

		inline v8::Local<v8::BigInt> ToV8Long(v8::Local<v8::Context>& v8Context, jlong& managedLong);

		inline v8::Local<v8::Primitive> ToV8Null(v8::Local<v8::Context>& v8Context);

		inline v8::Local<v8::Primitive> ToV8Undefined(v8::Local<v8::Context>& v8Context);

		inline jlong ToV8PersistentObjectReference(v8::Local<v8::Context>& v8Context, v8::Local<v8::Value> v8Value);

		v8::ScriptOrigin* ToV8ScriptOringinPointer(JNIEnv* jniEnv, v8::Local<v8::Context>& v8Context,
			jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule);

		v8::Local<v8::String> ToV8String(JNIEnv* jniEnv, v8::Local<v8::Context>& v8Context, jstring& managedString);

		v8::Local<v8::Value> ToV8Value(JNIEnv* jniEnv, v8::Local<v8::Context>& v8Context, jobject& obj);

		std::unique_ptr<v8::Local<v8::Value>[]> ToV8Values(JNIEnv* jniEnv, v8::Local<v8::Context>& v8Context, jobjectArray& mValues);
	}
}
