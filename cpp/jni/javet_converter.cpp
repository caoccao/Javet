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

#include "javet_converter.h"

 // Primitive
#define IS_JAVA_BOOLEAN(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueBoolean)
#define IS_JAVA_DOUBLE(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueDouble)
#define IS_JAVA_INTEGER(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueInteger)
#define IS_JAVA_LONG(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueLong)
#define IS_JAVA_NULL(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueNull)
#define IS_JAVA_STRING(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueString)
#define IS_JAVA_UNDEFINED(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueUndefined)
#define IS_JAVA_ZONED_DATE_TIME(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueZonedDateTime)

// Reference
#define IS_JAVA_ARGUMENTS(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueArguments)
#define IS_JAVA_ARRAY(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueArray)
#define IS_JAVA_FUNCTION(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueFunction)
#define IS_JAVA_ERROR(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueError)
#define IS_JAVA_GLOBAL_OBJECT(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueGlobalObject)
#define IS_JAVA_MAP(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueMap)
#define IS_JAVA_OBJECT(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueObject)
#define IS_JAVA_PROMISE(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValuePromise)
#define IS_JAVA_PROXY(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueProxy)
#define IS_JAVA_REFERENCE(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueReference)
#define IS_JAVA_REG_EXP(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueRegExp)
#define IS_JAVA_SET(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueSet)
#define IS_JAVA_SYMBOL(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueSymbol)

namespace Javet {
	namespace Converter {
		void initializeJavetConverter(JNIEnv* jniEnv) {
			/*
			 @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
			 @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
			*/

			// Primitive

			jclassV8ValueBoolean = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueBoolean"));
			jmethodIDV8ValueBooleanConstructor = jniEnv->GetMethodID(jclassV8ValueBoolean, "<init>", "(Z)V");
			jmethodIDV8ValueBooleanToPrimitive = jniEnv->GetMethodID(jclassV8ValueBoolean, "toPrimitive", "()Z");

			jclassV8ValueDouble = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueDouble"));
			jmethodIDV8ValueDoubleConstructor = jniEnv->GetMethodID(jclassV8ValueDouble, "<init>", "(D)V");
			jmethodIDV8ValueDoubleToPrimitive = jniEnv->GetMethodID(jclassV8ValueDouble, "toPrimitive", "()D");

			jclassV8ValueInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueInteger"));
			jmethodIDV8ValueIntegerConstructor = jniEnv->GetMethodID(jclassV8ValueInteger, "<init>", "(I)V");
			jmethodIDV8ValueIntegerToPrimitive = jniEnv->GetMethodID(jclassV8ValueInteger, "toPrimitive", "()I");

			jclassV8ValueLong = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueLong"));
			jmethodIDV8ValueLongConstructorFromLong = jniEnv->GetMethodID(jclassV8ValueLong, "<init>", "(J)V");
			jmethodIDV8ValueLongConstructorFromString = jniEnv->GetMethodID(jclassV8ValueLong, "<init>", "(Ljava/lang/String;)V");
			jmethodIDV8ValueLongToPrimitive = jniEnv->GetMethodID(jclassV8ValueLong, "toPrimitive", "()J");

			jclassV8ValueNull = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueNull"));
			jmethodIDV8ValueNullConstructor = jniEnv->GetMethodID(jclassV8ValueNull, "<init>", "()V");

			jclassV8ValueString = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueString"));
			jmethodIDV8ValueStringConstructor = jniEnv->GetMethodID(jclassV8ValueString, "<init>", "(Ljava/lang/String;)V");
			jmethodIDV8ValueStringToPrimitive = jniEnv->GetMethodID(jclassV8ValueString, "toPrimitive", "()Ljava/lang/String;");

			jclassV8ValueUndefined = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueUndefined"));
			jmethodIDV8ValueUndefinedConstructor = jniEnv->GetMethodID(jclassV8ValueUndefined, "<init>", "()V");

			jclassV8ValueUnknown = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueUnknown"));
			jmethodIDV8ValueUnknownConstructor = jniEnv->GetMethodID(jclassV8ValueUnknown, "<init>", "(Ljava/lang/String;)V");

			jclassV8ValueZonedDateTime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueZonedDateTime"));
			jmethodIDV8ValueZonedDateTimeConstructor = jniEnv->GetMethodID(jclassV8ValueZonedDateTime, "<init>", "(J)V");
			jmethodIDV8ValueZonedDateTimeToPrimitive = jniEnv->GetMethodID(jclassV8ValueZonedDateTime, "toPrimitive", "()J");

			// Reference

			jclassV8ValueArguments = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueArguments"));
			jmethodIDV8ValueArgumentsConstructor = jniEnv->GetMethodID(jclassV8ValueArguments, "<init>", "(J)V");
			jmethodIDV8ValueArgumentsGetHandle = jniEnv->GetMethodID(jclassV8ValueArguments, "getHandle", "()J");

			jclassV8ValueArray = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueArray"));
			jmethodIDV8ValueArrayConstructor = jniEnv->GetMethodID(jclassV8ValueArray, "<init>", "(J)V");
			jmethodIDV8ValueArrayGetHandle = jniEnv->GetMethodID(jclassV8ValueArray, "getHandle", "()J");

			jclassV8ValueFunction = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueFunction"));
			jmethodIDV8ValueFunctionConstructor = jniEnv->GetMethodID(jclassV8ValueFunction, "<init>", "(J)V");
			jmethodIDV8ValueFunctionGetHandle = jniEnv->GetMethodID(jclassV8ValueFunction, "getHandle", "()J");

			jclassV8ValueError = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueError"));
			jmethodIDV8ValueErrorConstructor = jniEnv->GetMethodID(jclassV8ValueError, "<init>", "(J)V");
			jmethodIDV8ValueErrorGetHandle = jniEnv->GetMethodID(jclassV8ValueError, "getHandle", "()J");

			jclassV8ValueGlobalObject = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueGlobalObject"));
			jmethodIDV8ValueGlobalObjectConstructor = jniEnv->GetMethodID(jclassV8ValueGlobalObject, "<init>", "(J)V");
			jmethodIDV8ValueGlobalObjectGetHandle = jniEnv->GetMethodID(jclassV8ValueGlobalObject, "getHandle", "()J");

			jclassV8ValueMap = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueMap"));
			jmethodIDV8ValueMapConstructor = jniEnv->GetMethodID(jclassV8ValueMap, "<init>", "(J)V");
			jmethodIDV8ValueMapGetHandle = jniEnv->GetMethodID(jclassV8ValueMap, "getHandle", "()J");

			jclassV8ValueObject = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueObject"));
			jmethodIDV8ValueObjectConstructor = jniEnv->GetMethodID(jclassV8ValueObject, "<init>", "(J)V");
			jmethodIDV8ValueObjectGetHandle = jniEnv->GetMethodID(jclassV8ValueObject, "getHandle", "()J");

			jclassV8ValuePromise = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValuePromise"));
			jmethodIDV8ValuePromiseConstructor = jniEnv->GetMethodID(jclassV8ValuePromise, "<init>", "(J)V");
			jmethodIDV8ValuePromiseGetHandle = jniEnv->GetMethodID(jclassV8ValuePromise, "getHandle", "()J");

			jclassV8ValueProxy = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueProxy"));
			jmethodIDV8ValueProxyConstructor = jniEnv->GetMethodID(jclassV8ValueProxy, "<init>", "(J)V");
			jmethodIDV8ValueProxyGetHandle = jniEnv->GetMethodID(jclassV8ValueProxy, "getHandle", "()J");

			jclassV8ValueReference = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueReference"));

			jclassV8ValueRegExp = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueRegExp"));
			jmethodIDV8ValueRegExpConstructor = jniEnv->GetMethodID(jclassV8ValueRegExp, "<init>", "(J)V");
			jmethodIDV8ValueRegExpGetHandle = jniEnv->GetMethodID(jclassV8ValueRegExp, "getHandle", "()J");

			jclassV8ValueSet = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSet"));
			jmethodIDV8ValueSetConstructor = jniEnv->GetMethodID(jclassV8ValueSet, "<init>", "(J)V");
			jmethodIDV8ValueSetGetHandle = jniEnv->GetMethodID(jclassV8ValueSet, "getHandle", "()J");

			jclassV8ValueSymbol = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSymbol"));
			jmethodIDV8ValueSymbolConstructor = jniEnv->GetMethodID(jclassV8ValueSymbol, "<init>", "(J)V");
			jmethodIDV8ValueSymbolGetHandle = jniEnv->GetMethodID(jclassV8ValueSymbol, "getHandle", "()J");
		}

		jobject toJV8Value(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value) {
			if (v8Value->IsUndefined()) {
				return toJV8ValueUndefined(jniEnv);
			}
			if (v8Value->IsNull()) {
				return toJV8ValueNull(jniEnv);
			}
			// Reference types
			// Note: Reference types must be checked before primitive types are checked.
			if (v8Value->IsArray()) {
				return jniEnv->NewObject(jclassV8ValueArray, jmethodIDV8ValueArrayConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsTypedArray()) {
				// TODO
				if (v8Value->IsBigInt64Array()) {
				}
				if (v8Value->IsBigUint64Array()) {
				}
				if (v8Value->IsFloat32Array()) {
				}
				if (v8Value->IsFloat64Array()) {
				}
				if (v8Value->IsInt16Array()) {
				}
				if (v8Value->IsInt32Array()) {
				}
				if (v8Value->IsInt8Array()) {
				}
				if (v8Value->IsUint16Array()) {
				}
				if (v8Value->IsUint32Array()) {
				}
				if (v8Value->IsUint8Array()) {
				}
				if (v8Value->IsUint8ClampedArray()) {
				}
			}
			if (v8Value->IsArrayBuffer()) {
				// TODO
			}
			if (v8Value->IsArrayBufferView()) {
				// TODO
			}
			if (v8Value->IsMap()) {
				return jniEnv->NewObject(jclassV8ValueMap, jmethodIDV8ValueMapConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsMapIterator()) {
				// It defaults to V8ValueObject.
			}
			if (v8Value->IsSet()) {
				return jniEnv->NewObject(jclassV8ValueSet, jmethodIDV8ValueSetConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsSetIterator()) {
				// It defaults to V8ValueObject.
			}
			if (v8Value->IsWeakMap()) {
				// TODO
			}
			if (v8Value->IsWeakSet()) {
				// TODO
			}
			if (v8Value->IsArgumentsObject()) {
				return jniEnv->NewObject(jclassV8ValueArguments, jmethodIDV8ValueArgumentsConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsPromise()) {
				return jniEnv->NewObject(jclassV8ValuePromise, jmethodIDV8ValuePromiseConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsRegExp()) {
				return jniEnv->NewObject(jclassV8ValueRegExp, jmethodIDV8ValueRegExpConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsGeneratorObject()) {
				// TODO
			}
			if (v8Value->IsAsyncFunction()) {
				// TODO
			}
			if (v8Value->IsGeneratorFunction()) {
				// TODO
			}
			if (v8Value->IsProxy()) {
				// Proxy is also a function. So, it needs to be tested before IsFunction().
				return jniEnv->NewObject(jclassV8ValueProxy, jmethodIDV8ValueProxyConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsFunction()) {
				return jniEnv->NewObject(jclassV8ValueFunction, jmethodIDV8ValueFunctionConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsNativeError()) {
				return jniEnv->NewObject(jclassV8ValueError, jmethodIDV8ValueErrorConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			if (v8Value->IsSymbol() || v8Value->IsSymbolObject()) {
				return jniEnv->NewObject(jclassV8ValueSymbol, jmethodIDV8ValueSymbolConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			// Primitive types
			if (v8Value->IsBoolean() || v8Value->IsBooleanObject()) {
				return jniEnv->NewObject(jclassV8ValueBoolean, jmethodIDV8ValueBooleanConstructor, v8Value->IsTrue());
			}
			if (v8Value->IsInt32()) {
				return jniEnv->NewObject(jclassV8ValueInteger, jmethodIDV8ValueIntegerConstructor, v8Value->Int32Value(v8Context).FromMaybe(0));
			}
			if (v8Value->IsBigInt() || v8Value->IsBigIntObject()) {
#ifdef JAVET_CONVERTER_BIGINT_STANDARD
				// This is the standard way of getting int64.
				return jniEnv->NewObject(jclassV8ValueLong, jmethodIDV8ValueLongConstructorFromLong,
					v8Value->ToBigInt(v8Context).ToLocalChecked()->Int64Value());
#else
				// There is another way of getting int64. This branch is disabled by default.
				v8::String::Value stringValue(v8Context->GetIsolate(), v8Value);
				jstring mStringValue = jniEnv->NewString(*stringValue, stringValue.length());
				jobject mV8Value = jniEnv->NewObject(jclassV8ValueLong, jmethodIDV8ValueLongConstructorFromString, mStringValue);
				jniEnv->DeleteLocalRef(mStringValue);
				return mV8Value;
#endif // JAVET_CONVERTER_BIGINT_STANDARD
			}
			if (v8Value->IsDate()) {
				auto v8Date = v8Value->ToObject(v8Context).ToLocalChecked().As<v8::Date>();
				return jniEnv->NewObject(jclassV8ValueZonedDateTime, jmethodIDV8ValueZonedDateTimeConstructor, static_cast<std::int64_t>(v8Date->ValueOf()));
			}
			if (v8Value->IsNumber() || v8Value->IsNumberObject()) {
				return jniEnv->NewObject(jclassV8ValueDouble, jmethodIDV8ValueDoubleConstructor, v8Value->NumberValue(v8Context).FromMaybe(0));
			}
			if (v8Value->IsString() || v8Value->IsStringObject()) {
				return toJV8ValuePrimitive(jniEnv, jclassV8ValueString, jmethodIDV8ValueStringConstructor, v8Context, v8Value);
			}
			if (v8Value->IsName()) {
				// Name is not supported.
			}
			// Object needs to be the last one.
			if (v8Value->IsObject()) {
				return jniEnv->NewObject(jclassV8ValueObject, jmethodIDV8ValueObjectConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			// Something is wrong. It defaults to toString().
			return toJV8ValuePrimitive(jniEnv, jclassV8ValueUnknown, jmethodIDV8ValueUnknownConstructor, v8Context, v8Value);
		}

		inline jobject toJV8ValueNull(JNIEnv* jniEnv) {
			return jniEnv->NewObject(jclassV8ValueNull, jmethodIDV8ValueNullConstructor);
		}

		jobject toJV8ValueGlobalObject(JNIEnv* jniEnv, v8::Persistent<v8::Object>* v8PersistentObjectPointer) {
			return jniEnv->NewObject(jclassV8ValueGlobalObject, jmethodIDV8ValueGlobalObjectConstructor, v8PersistentObjectPointer);
		}

		inline jobject toJV8ValuePrimitive(
			JNIEnv* jniEnv, jclass jclassV8ValuePrimitive, jmethodID jmethodIDV8ValuePrimitiveConstructor,
			v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value) {
			v8::String::Value stringValue(v8Context->GetIsolate(), v8Value->ToString(v8Context).ToLocalChecked());
			jstring mStringValue = jniEnv->NewString(*stringValue, stringValue.length());
			jobject mV8ValuePrimitive = jniEnv->NewObject(jclassV8ValuePrimitive, jmethodIDV8ValuePrimitiveConstructor, mStringValue);
			jniEnv->DeleteLocalRef(mStringValue);
			return mV8ValuePrimitive;
		}

		jobject toJV8ValueUndefined(JNIEnv* jniEnv) {
			return jniEnv->NewObject(jclassV8ValueUndefined, jmethodIDV8ValueUndefinedConstructor);
		}

		inline v8::Local<v8::Boolean> toV8Boolean(v8::Local<v8::Context> v8Context, jboolean& managedBoolean) {
			return v8::Boolean::New(v8Context->GetIsolate(), managedBoolean);
		}

		inline v8::Local<v8::Value> toV8Date(v8::Local<v8::Context> v8Context, jlong& managedLong) {
			return v8::Date::New(v8Context, (double)managedLong).ToLocalChecked();
		}

		inline v8::Local<v8::Number> toV8Double(v8::Local<v8::Context> v8Context, jdouble& managedDouble) {
			return v8::Number::New(v8Context->GetIsolate(), managedDouble);
		}

		inline v8::Local<v8::Integer> toV8Integer(v8::Local<v8::Context> v8Context, jint& managedInteger) {
			return v8::Integer::New(v8Context->GetIsolate(), managedInteger);
		}

		inline v8::Local<v8::BigInt> toV8Long(v8::Local<v8::Context> v8Context, jlong& managedLong) {
			return v8::BigInt::New(v8Context->GetIsolate(), managedLong);
		}

		inline v8::Local<v8::Primitive> toV8Null(v8::Local<v8::Context> v8Context) {
			return v8::Null(v8Context->GetIsolate());
		}

		inline v8::Local<v8::Primitive> toV8Undefined(v8::Local<v8::Context> v8Context) {
			return v8::Undefined(v8Context->GetIsolate());
		}

		inline jlong toV8PersistentObjectReference(v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value) {
			v8::Persistent<v8::Object>* v8PersistentObjectPointer = new v8::Persistent<v8::Object>(
				v8Context->GetIsolate(),
				v8Value->ToObject(v8Context).ToLocalChecked());
			return reinterpret_cast<jlong>(v8PersistentObjectPointer);
		}

		v8::ScriptOrigin* toV8ScriptOringinPointer(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context,
			jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule) {
			if (mResourceName == nullptr) {
				return nullptr;
			}
			return new v8::ScriptOrigin(
				toV8String(jniEnv, v8Context, mResourceName),
				toV8Integer(v8Context, mResourceLineOffset),
				toV8Integer(v8Context, mResourceColumnOffset),
				v8::Local<v8::Boolean>(),
				toV8Integer(v8Context, mScriptId),
				v8::Local<v8::Value>(),
				v8::Local<v8::Boolean>(),
				toV8Boolean(v8Context, mIsWASM),
				toV8Boolean(v8Context, mIsModule),
				v8::Local<v8::PrimitiveArray>());
		}

		v8::Local<v8::String> toV8String(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, jstring& managedString) {
			const uint16_t* unmanagedString = jniEnv->GetStringChars(managedString, nullptr);
			int length = jniEnv->GetStringLength(managedString);
			auto twoByteString = v8::String::NewFromTwoByte(
				v8Context->GetIsolate(), unmanagedString, v8::NewStringType::kNormal, length);
			if (twoByteString.IsEmpty()) {
				return v8::Local<v8::String>();
			}
			auto localV8String = twoByteString.ToLocalChecked();
			jniEnv->ReleaseStringChars(managedString, unmanagedString);
			return localV8String;
		}

		v8::Local<v8::Value> toV8Value(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, jobject obj) {
			if (obj == nullptr || IS_JAVA_NULL(jniEnv, obj)) {
				return toV8Null(v8Context);
			}
			else if (IS_JAVA_INTEGER(jniEnv, obj)) {
				jint integerObject = jniEnv->CallIntMethod(obj, jmethodIDV8ValueIntegerToPrimitive);
				return toV8Integer(v8Context, integerObject);
			}
			else if (IS_JAVA_STRING(jniEnv, obj)) {
				jstring stringObject = (jstring)jniEnv->CallObjectMethod(obj, jmethodIDV8ValueStringToPrimitive);
				return toV8String(jniEnv, v8Context, stringObject);
			}
			else if (IS_JAVA_BOOLEAN(jniEnv, obj)) {
				jboolean booleanObject = jniEnv->CallBooleanMethod(obj, jmethodIDV8ValueBooleanToPrimitive);
				return toV8Boolean(v8Context, booleanObject);
			}
			else if (IS_JAVA_DOUBLE(jniEnv, obj)) {
				jdouble doubleObject = jniEnv->CallDoubleMethod(obj, jmethodIDV8ValueDoubleToPrimitive);
				return toV8Double(v8Context, doubleObject);
			}
			else if (IS_JAVA_LONG(jniEnv, obj)) {
				jlong longObject = jniEnv->CallLongMethod(obj, jmethodIDV8ValueLongToPrimitive);
				return toV8Long(v8Context, longObject);
			}
			else if (IS_JAVA_ZONED_DATE_TIME(jniEnv, obj)) {
				jlong longObject = (jlong)jniEnv->CallObjectMethod(obj, jmethodIDV8ValueZonedDateTimeToPrimitive);
				return toV8Date(v8Context, longObject);
			}
			else if (IS_JAVA_REFERENCE(jniEnv, obj)) {
				if (IS_JAVA_ARGUMENTS(jniEnv, obj)) {
					return v8::Local<v8::Object>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Object>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueArgumentsGetHandle)));
				}
				else if (IS_JAVA_ARRAY(jniEnv, obj)) {
					return v8::Local<v8::Array>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Array>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueArrayGetHandle)));
				}
				else if (IS_JAVA_ERROR(jniEnv, obj)) {
					return v8::Local<v8::Object>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Object>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueErrorGetHandle)));
				}
				else if (IS_JAVA_GLOBAL_OBJECT(jniEnv, obj)) {
					// Global object is a tricky one. 
					return v8::Local<v8::Object>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Object>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueGlobalObjectGetHandle)));
				}
				else if (IS_JAVA_MAP(jniEnv, obj)) {
					return v8::Local<v8::Map>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Map>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueMapGetHandle)));
				}
				else if (IS_JAVA_OBJECT(jniEnv, obj)) {
					return v8::Local<v8::Object>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Object>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueObjectGetHandle)));
				}
				else if (IS_JAVA_PROMISE(jniEnv, obj)) {
					return v8::Local<v8::Promise>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Promise>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValuePromiseGetHandle)));
				}
				else if (IS_JAVA_PROXY(jniEnv, obj)) {
					return v8::Local<v8::Proxy>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Proxy>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueProxyGetHandle)));
				}
				else if (IS_JAVA_REG_EXP(jniEnv, obj)) {
					return v8::Local<v8::RegExp>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::RegExp>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueRegExpGetHandle)));
				}
				else if (IS_JAVA_SET(jniEnv, obj)) {
					return v8::Local<v8::Set>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Set>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueSetGetHandle)));
				}
				else if (IS_JAVA_SYMBOL(jniEnv, obj)) {
					return v8::Local<v8::Symbol>::New(v8Context->GetIsolate(), *reinterpret_cast<v8::Persistent<v8::Symbol>*>(
						jniEnv->CallLongMethod(obj, jmethodIDV8ValueSymbolGetHandle)));
				}
			}
			return toV8Undefined(v8Context);
		}

		std::unique_ptr<v8::Local<v8::Value>[]> toV8Values(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, jobjectArray& mValues) {
			std::unique_ptr<v8::Local<v8::Value>[]> umValuesPointer;
			uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
			if (valueCount > 0) {
				umValuesPointer.reset(new v8::Local<v8::Value>[valueCount]);
				for (int i = 0; i < valueCount; ++i) {
					umValuesPointer.get()[i] = toV8Value(jniEnv, v8Context, jniEnv->GetObjectArrayElement(mValues, i));
				}
			}
			return umValuesPointer;
		}
	}
}
