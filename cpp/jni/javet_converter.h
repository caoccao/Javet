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
#include "javet_constants.h"
#include "javet_monitor.h"
#include "javet_v8.h"

namespace Javet {
	namespace Converter {
		// Runtime

		static jclass jclassV8Runtime;
		static jmethodID jmethodIDV8RuntimeCreateV8ValueBoolean;
		static jmethodID jmethodIDV8RuntimeCreateV8ValueInteger;
		static jmethodID jmethodIDV8RuntimeCreateV8ValueLong;
		static jmethodID jmethodIDV8RuntimeCreateV8ValueNull;
		static jmethodID jmethodIDV8RuntimeCreateV8ValueUndefined;

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
		static jmethodID jmethodIDV8ValueLongToPrimitive;

		static jclass jclassV8ValueNull;

		static jclass jclassV8ValueString;
		static jmethodID jmethodIDV8ValueStringConstructor;
		static jmethodID jmethodIDV8ValueStringToPrimitive;

		static jclass jclassV8ValueUndefined;

		static jclass jclassV8ValueUnknown;
		static jmethodID jmethodIDV8ValueUnknownConstructor;

		static jclass jclassV8ValueZonedDateTime;
		static jmethodID jmethodIDV8ValueZonedDateTimeConstructor;
		static jmethodID jmethodIDV8ValueZonedDateTimeToPrimitive;

		// Reference

		static jclass jclassV8Module;
		static jmethodID jmethodIDV8ModuleConstructor;
		static jmethodID jmethodIDV8ModuleGetHandle;

		static jclass jclassV8Script;
		static jmethodID jmethodIDV8ScriptConstructor;
		static jmethodID jmethodIDV8ScriptGetHandle;

		static jclass jclassV8ValueArguments;
		static jmethodID jmethodIDV8ValueArgumentsConstructor;
		static jmethodID jmethodIDV8ValueArgumentsGetHandle;

		static jclass jclassV8ValueArray;
		static jmethodID jmethodIDV8ValueArrayConstructor;
		static jmethodID jmethodIDV8ValueArrayGetHandle;

		static jclass jclassV8ValueArrayBuffer;
		static jmethodID jmethodIDV8ValueArrayBufferConstructor;
		static jmethodID jmethodIDV8ValueArrayBufferGetHandle;

		static jclass jclassV8ValueDataView;
		static jmethodID jmethodIDV8ValueDataViewConstructor;
		static jmethodID jmethodIDV8ValueDataViewGetHandle;

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

		static jclass jclassV8ValueWeakMap;
		static jmethodID jmethodIDV8ValueWeakMapConstructor;
		static jmethodID jmethodIDV8ValueWeakMapGetHandle;

		static jclass jclassV8ValueWeakSet;
		static jmethodID jmethodIDV8ValueWeakSetConstructor;
		static jmethodID jmethodIDV8ValueWeakSetGetHandle;

		void Initialize(JNIEnv* jniEnv);

		static inline jstring ToJavaString(JNIEnv* jniEnv, const char* utfString) {
			return jniEnv->NewStringUTF(utfString);
		}

		static inline jstring ToJavaString(JNIEnv* jniEnv, const std::string& stdString) {
			return jniEnv->NewStringUTF(stdString.c_str());
		}

		static inline jstring ToJavaString(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8LocalString& v8LocalString) {
			V8StringValue v8StringValue(v8Context->GetIsolate(), v8LocalString);
			return jniEnv->NewString(*v8StringValue, v8StringValue.length());
		}

		static inline jstring ToJavaString(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue) {
			V8StringUtf8Value v8StringUtf8Value(v8Context->GetIsolate(), v8LocalValue);
			return jniEnv->NewStringUTF(*v8StringUtf8Value);
		}

		static inline std::unique_ptr<std::string> ToStdString(JNIEnv* jniEnv, jstring mString) {
			jboolean isCopy(false);
			const char* utfChars = jniEnv->GetStringUTFChars(mString, &isCopy);
			auto stdStringPointer = std::make_unique<std::string>(utfChars, jniEnv->GetStringUTFLength(mString));
			jniEnv->ReleaseStringUTFChars(mString, utfChars);
			return stdStringPointer;
		}

		static inline std::unique_ptr<std::string> ToStdString(const V8LocalContext& v8Context, const V8LocalString& v8LocalString) {
			V8StringUtf8Value v8StringUtf8Value(v8Context->GetIsolate(), v8LocalString);
			return std::make_unique<std::string>(*v8StringUtf8Value, v8StringUtf8Value.length());
		}

		jobject ToExternalV8Module(JNIEnv* jniEnv, jobject externalV8Runtime, const V8LocalContext& v8Context, const V8LocalModule& v8Module);

		jobject ToExternalV8Script(JNIEnv* jniEnv, jobject externalV8Runtime, const V8LocalContext& v8Context, const V8LocalScript& v8Script);

		jobject ToExternalV8Value(JNIEnv* jniEnv, jobject externalV8Runtime, const V8LocalContext& v8Context, const V8LocalValue v8Value);

		jobject ToExternalV8ValueArray(JNIEnv* jniEnv, jobject externalV8Runtime, const V8LocalContext& v8Context, const v8::FunctionCallbackInfo<v8::Value>& args);

		static inline jobject ToExternalV8ValueNull(JNIEnv* jniEnv, jobject externalV8Runtime) {
			return jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeCreateV8ValueNull);
		}

		jobject ToExternalV8ValueGlobalObject(JNIEnv* jniEnv, V8PersistentObject& v8PersistentObject);

		static inline jobject ToExternalV8ValuePrimitive(
			JNIEnv* jniEnv, jclass jclassV8ValuePrimitive, jmethodID jmethodIDV8ValuePrimitiveConstructor,
			const V8LocalContext& v8Context, const V8LocalValue v8Value) {
			jstring mStringValue = ToJavaString(jniEnv, v8Context, v8Value->ToString(v8Context).ToLocalChecked());
			jobject mV8ValuePrimitive = jniEnv->NewObject(
				jclassV8ValuePrimitive, jmethodIDV8ValuePrimitiveConstructor, mStringValue);
			jniEnv->DeleteLocalRef(mStringValue);
			return mV8ValuePrimitive;
		}

		jobject ToExternalV8ValueUndefined(JNIEnv* jniEnv, jobject externalV8Runtime);

		static inline V8LocalBoolean ToV8Boolean(const V8LocalContext& v8Context, jboolean& managedBoolean) {
			return v8::Boolean::New(v8Context->GetIsolate(), managedBoolean);
		}

		static inline V8LocalValue ToV8Date(const V8LocalContext& v8Context, jlong& managedLong) {
			return v8::Date::New(v8Context, (double)managedLong).ToLocalChecked();
		}

		static inline V8LocalNumber ToV8Double(const V8LocalContext& v8Context, jdouble& managedDouble) {
			return v8::Number::New(v8Context->GetIsolate(), managedDouble);
		}

		static inline V8LocalInteger ToV8Integer(const V8LocalContext& v8Context, jint& managedInteger) {
			return v8::Integer::New(v8Context->GetIsolate(), managedInteger);
		}

		static inline V8LocalBigInt ToV8Long(const V8LocalContext& v8Context, jlong& managedLong) {
			return v8::BigInt::New(v8Context->GetIsolate(), managedLong);
		}

		static inline V8LocalPrimitive ToV8Null(const V8LocalContext& v8Context) {
			return v8::Null(v8Context->GetIsolate());
		}

		static inline V8LocalPrimitive ToV8Undefined(const V8LocalContext& v8Context) {
			return v8::Undefined(v8Context->GetIsolate());
		}

		static inline jlong ToV8PersistentDataReference(const V8LocalContext& v8Context, const V8LocalData v8Data) {
			V8PersistentData* v8PersistentDataPointer = new V8PersistentData(v8Context->GetIsolate(), v8Data);
			INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentReference);
			return TO_JAVA_LONG(v8PersistentDataPointer);
		}

		static inline jlong ToV8PersistentObjectReference(const V8LocalContext& v8Context, const V8LocalValue v8Value) {
			V8PersistentObject* v8PersistentObjectPointer = new V8PersistentObject(
				v8Context->GetIsolate(), v8Value->ToObject(v8Context).ToLocalChecked());
			INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentReference);
			return TO_JAVA_LONG(v8PersistentObjectPointer);
		}

		static inline jlong ToV8PersistentScriptReference(const V8LocalContext& v8Context, const V8LocalScript v8Script) {
			V8PersistentScript* v8PersistentScriptPointer = new V8PersistentScript(v8Context->GetIsolate(), v8Script);
			INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentReference);
			return TO_JAVA_LONG(v8PersistentScriptPointer);
		}

		std::unique_ptr<v8::ScriptOrigin> ToV8ScriptOringinPointer(JNIEnv* jniEnv, const V8LocalContext& v8Context,
			jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule);

		V8LocalString ToV8String(JNIEnv* jniEnv, const V8LocalContext& v8Context, jstring& managedString);

		V8LocalValue ToV8Value(JNIEnv* jniEnv, const V8LocalContext& v8Context, jobject& obj);

		std::unique_ptr<V8LocalValue[]> ToV8Values(JNIEnv* jniEnv, const V8LocalContext& v8Context, jobjectArray& mValues);
	}
}
