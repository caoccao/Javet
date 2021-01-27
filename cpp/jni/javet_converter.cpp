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

namespace Javet {
	namespace Converter {
		void initializeJavetConverter(JNIEnv* jniEnv) {
			/*
			 @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
			 @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
			*/

			jclassV8ValueNull = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/V8ValueNull"));
			jmethodIDV8ValueNullConstructor = jniEnv->GetMethodID(jclassV8ValueNull, "<init>", "()V");

			jclassV8ValueUndefined = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/V8ValueUndefined"));
			jmethodIDV8ValueUndefinedConstructor = jniEnv->GetMethodID(jclassV8ValueUndefined, "<init>", "()V");

			// Primitive

			jclassV8ValueBoolean = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueBoolean"));
			jmethodIDV8ValueBooleanConstructor = jniEnv->GetMethodID(jclassV8ValueBoolean, "<init>", "(Z)V");

			jclassV8ValueDouble = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueDouble"));
			jmethodIDV8ValueDoubleConstructor = jniEnv->GetMethodID(jclassV8ValueDouble, "<init>", "(D)V");

			jclassV8ValueInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueInteger"));
			jmethodIDV8ValueIntegerConstructor = jniEnv->GetMethodID(jclassV8ValueInteger, "<init>", "(I)V");

			jclassV8ValueLong = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueLong"));
			jmethodIDV8ValueLongConstructorFromLong = jniEnv->GetMethodID(jclassV8ValueLong, "<init>", "(J)V");
			jmethodIDV8ValueLongConstructorFromString = jniEnv->GetMethodID(jclassV8ValueLong, "<init>", "(Ljava/lang/String;)V");

			jclassV8ValueString = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueString"));
			jmethodIDV8ValueStringConstructor = jniEnv->GetMethodID(jclassV8ValueString, "<init>", "(Ljava/lang/String;)V");

			jclassV8ValueUnknown = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueUnknown"));
			jmethodIDV8ValueUnknownConstructor = jniEnv->GetMethodID(jclassV8ValueUnknown, "<init>", "(Ljava/lang/String;)V");

			jclassV8ValueZonedDateTime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueZonedDateTime"));
			jmethodIDV8ValueZonedDateTimeConstructor = jniEnv->GetMethodID(jclassV8ValueZonedDateTime, "<init>", "(J)V");

			// Reference

			jclassV8ValueArguments = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueArguments"));
			jmethodIDV8ValueArgumentsConstructor = jniEnv->GetMethodID(jclassV8ValueArguments, "<init>", "(J)V");

			jclassV8ValueArray = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueArray"));
			jmethodIDV8ValueArrayConstructor = jniEnv->GetMethodID(jclassV8ValueArray, "<init>", "(J)V");

			jclassV8ValueError = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueError"));
			jmethodIDV8ValueErrorConstructor = jniEnv->GetMethodID(jclassV8ValueError, "<init>", "(J)V");

			jclassV8ValueMap = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueMap"));
			jmethodIDV8ValueMapConstructor = jniEnv->GetMethodID(jclassV8ValueMap, "<init>", "(J)V");

			jclassV8ValueObject = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueObject"));
			jmethodIDV8ValueObjectConstructor = jniEnv->GetMethodID(jclassV8ValueObject, "<init>", "(J)V");

			jclassV8ValuePromise = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValuePromise"));
			jmethodIDV8ValuePromiseConstructor = jniEnv->GetMethodID(jclassV8ValuePromise, "<init>", "(J)V");

			jclassV8ValueProxy = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueProxy"));
			jmethodIDV8ValueProxyConstructor = jniEnv->GetMethodID(jclassV8ValueProxy, "<init>", "(J)V");

			jclassV8ValueRegex = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueRegex"));
			jmethodIDV8ValueRegexConstructor = jniEnv->GetMethodID(jclassV8ValueRegex, "<init>", "(J)V");

			jclassV8ValueSymbol = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSymbol"));
			jmethodIDV8ValueSymbolConstructor = jniEnv->GetMethodID(jclassV8ValueSymbol, "<init>", "(J)V");
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
				// TODO
			}
			if (v8Value->IsSet()) {
				// TODO
			}
			if (v8Value->IsSetIterator()) {
				// TODO
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
				return jniEnv->NewObject(jclassV8ValueRegex, jmethodIDV8ValueRegexConstructor, toV8PersistentObjectReference(v8Context, v8Value));
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
			if (v8Value->IsFunction()) {
				// TODO
			}
			if (v8Value->IsProxy()) {
				return jniEnv->NewObject(jclassV8ValueProxy, jmethodIDV8ValueProxyConstructor, toV8PersistentObjectReference(v8Context, v8Value));
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
				return toJV8ValueReference(jniEnv, jclassV8ValueString, jmethodIDV8ValueStringConstructor, v8Context, v8Value);
			}
			if (v8Value->IsName()) {
				// Name is not supported.
			}
			// Object needs to be the last one.
			if (v8Value->IsObject()) {
				return jniEnv->NewObject(jclassV8ValueObject, jmethodIDV8ValueObjectConstructor, toV8PersistentObjectReference(v8Context, v8Value));
			}
			// Something is wrong. It defaults to toString().
			return toJV8ValueReference(jniEnv, jclassV8ValueUnknown, jmethodIDV8ValueUnknownConstructor, v8Context, v8Value);
		}

		inline jobject toJV8ValueNull(JNIEnv* jniEnv) {
			return jniEnv->NewObject(jclassV8ValueNull, jmethodIDV8ValueNullConstructor);
		}

		inline jobject toJV8ValueReference(
			JNIEnv* jniEnv, jclass jclassV8ValueReference, jmethodID jmethodIDV8ValueReferenceConstructor,
			v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value) {
			v8::String::Value stringValue(v8Context->GetIsolate(), v8Value->ToString(v8Context).ToLocalChecked());
			jstring mStringValue = jniEnv->NewString(*stringValue, stringValue.length());
			jobject mV8ValueReference = jniEnv->NewObject(jclassV8ValueReference, jmethodIDV8ValueReferenceConstructor, mStringValue);
			jniEnv->DeleteLocalRef(mStringValue);
			return mV8ValueReference;
		}

		jobject toJV8ValueUndefined(JNIEnv* jniEnv) {
			return jniEnv->NewObject(jclassV8ValueUndefined, jmethodIDV8ValueUndefinedConstructor);
		}

		inline v8::Local<v8::Boolean> toV8Boolean(v8::Isolate* v8Isolate, jboolean& managedBoolean) {
			return v8::Boolean::New(v8Isolate, managedBoolean);
		}

		v8::Local<v8::Integer> toV8Integer(v8::Isolate* v8Isolate, jint& managedInteger) {
			return v8::Integer::New(v8Isolate, managedInteger);
		}

		inline jlong toV8PersistentObjectReference(v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value) {
			v8::Persistent<v8::Object>* v8PersistentObjectPointer = new v8::Persistent<v8::Object>(v8Context->GetIsolate(), v8Value->ToObject(v8Context).ToLocalChecked());
			return reinterpret_cast<jlong>(v8PersistentObjectPointer);
		}

		v8::ScriptOrigin* toV8ScriptOringinPointer(JNIEnv* jniEnv, v8::Isolate* v8Isolate,
			jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule) {
			if (mResourceName == nullptr) {
				return nullptr;
			}
			return new v8::ScriptOrigin(
				toV8String(jniEnv, v8Isolate, mResourceName),
				toV8Integer(v8Isolate, mResourceLineOffset),
				toV8Integer(v8Isolate, mResourceColumnOffset),
				v8::Local<v8::Boolean>(),
				toV8Integer(v8Isolate, mScriptId),
				v8::Local<v8::Value>(),
				v8::Local<v8::Boolean>(),
				toV8Boolean(v8Isolate, mIsWASM),
				toV8Boolean(v8Isolate, mIsModule),
				v8::Local<v8::PrimitiveArray>());
		}

		v8::Local<v8::String> toV8String(JNIEnv* jniEnv, v8::Isolate* v8Isolate, jstring& managedString) {
			const uint16_t* unmanagedString = jniEnv->GetStringChars(managedString, nullptr);
			int length = jniEnv->GetStringLength(managedString);
			auto twoByteString = v8::String::NewFromTwoByte(
				v8Isolate, unmanagedString, v8::NewStringType::kNormal, length);
			if (twoByteString.IsEmpty()) {
				return v8::Local<v8::String>();
			}
			auto localV8String = twoByteString.ToLocalChecked();
			jniEnv->ReleaseStringChars(managedString, unmanagedString);
			return localV8String;
		}
	}
}
