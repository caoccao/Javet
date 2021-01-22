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

			jclassInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("java/lang/Integer"));
			jmethodIDIntegerValueOf = jniEnv->GetStaticMethodID(jclassInteger, "valueOf", "(I)Ljava/lang/Integer;");

			jclassLong = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("java/lang/Long"));
			jmethodIDLongValueOf = jniEnv->GetStaticMethodID(jclassLong, "valueOf", "(J)Ljava/lang/Long;");

			jclassV8ValueInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/V8ValueInteger"));
			jmethodIDV8ValueIntegerConstructor = jniEnv->GetMethodID(jclassV8ValueInteger, "<init>", "(Ljava/lang/Integer;Z)V");

			jclassV8ValueLong = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/V8ValueLong"));
			//jmethodIDV8ValueLongConstructor = jniEnv->GetMethodID(jclassV8ValueLong, "<init>", "(Ljava/lang/Long;Z)V");
			jmethodIDV8ValueLongConstructor = jniEnv->GetMethodID(jclassV8ValueLong, "<init>", "(Ljava/lang/String;Z)V");

			jclassV8ValueNull = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/V8ValueNull"));
			jmethodIDV8ValueNullConstructor = jniEnv->GetMethodID(jclassV8ValueNull, "<init>", "()V");

			jclassV8ValueString = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/V8ValueString"));
			jmethodIDV8ValueStringConstructor = jniEnv->GetMethodID(jclassV8ValueString, "<init>", "(Ljava/lang/String;)V");

			jclassV8ValueUndefined = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/V8ValueUndefined"));
			jmethodIDV8ValueUndefinedConstructor = jniEnv->GetMethodID(jclassV8ValueUndefined, "<init>", "()V");
		}

		jobject toJObject(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, v8::Local<v8::Value> v8Value) {
			if (v8Value->IsUndefined()) {
				return jniEnv->NewObject(jclassV8ValueUndefined, jmethodIDV8ValueUndefinedConstructor);
			}
			if (v8Value->IsNull()) {
				return jniEnv->NewObject(jclassV8ValueNull, jmethodIDV8ValueNullConstructor);
			}
			if (v8Value->IsInt32()) {
				jobject integerValue = jniEnv->CallStaticObjectMethod(jclassInteger, jmethodIDIntegerValueOf, v8Value->Int32Value(v8Context).FromMaybe(0));
				return jniEnv->NewObject(jclassV8ValueInteger, jmethodIDV8ValueIntegerConstructor, integerValue, false);
			}
			if (v8Value->IsBigInt()) {
				// Note: There's something wrong with IntegerValue().
				//jobject longValue = jniEnv->CallStaticObjectMethod(jclassLong, jmethodIDLongValueOf, v8Value->IntegerValue(v8Context).FromMaybe(0L));
				//return jniEnv->NewObject(jclassV8ValueLong, jmethodIDV8ValueLongConstructor, longValue, false);
				v8::String::Value stringValue(v8Context->GetIsolate(), v8Value);
				return jniEnv->NewObject(jclassV8ValueLong, jmethodIDV8ValueLongConstructor, jniEnv->NewString(*stringValue, stringValue.length()), false);
			}
			if (v8Value->IsString()) {
				v8::String::Value stringValue(v8Context->GetIsolate(), v8Value->ToString(v8Context).ToLocalChecked());
				return jniEnv->NewObject(jclassV8ValueString, jmethodIDV8ValueStringConstructor, jniEnv->NewString(*stringValue, stringValue.length()));
			}
			return nullptr;
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
			v8::MaybeLocal<v8::String> twoByteString = v8::String::NewFromTwoByte(
				v8Isolate, unmanagedString, v8::NewStringType::kNormal, length);
			if (twoByteString.IsEmpty()) {
				return v8::Local<v8::String>();
			}
			v8::Local<v8::String> localV8String = twoByteString.ToLocalChecked();
			jniEnv->ReleaseStringChars(managedString, unmanagedString);
			return localV8String;
		}
	}
}
