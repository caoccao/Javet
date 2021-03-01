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

#include <jni.h>
#include <libplatform/libplatform.h>
#include <iostream>
#include <v8.h>
#include <v8-inspector.h>
#include <functional>
#include <string.h>
#include <map>
#include <cstdlib>
#include "com_caoccao_javet_interop_V8Native.h"
#include "javet_callbacks.h"
#include "javet_constants.h"
#include "javet_converter.h"
#include "javet_enums.h"
#include "javet_exceptions.h"
#include "javet_globals.h"
#include "javet_v8_runtime.h"

 /*
  * Development Guide:
  * 1. Namespace is not recommended in this project.
  * 2. Methods are expected to be sorted alphabatically except JNI_OnLoad.
  */

#define IS_JAVA_INTEGER(jniEnv, obj) jniEnv->IsInstanceOf(obj, Javet::Main::jclassV8ValueInteger)
#define IS_JAVA_STRING(jniEnv, obj) jniEnv->IsInstanceOf(obj, Javet::Main::jclassV8ValueString)
#define TO_JAVA_INTEGER(jniEnv, obj) jniEnv->CallIntMethod(obj, Javet::Main::jmethodIDV8ValueIntegerToPrimitive)
#define TO_JAVA_STRING(jniEnv, obj) (jstring)jniEnv->CallObjectMethod(obj, Javet::Main::jmethodIDV8ValueStringToPrimitive)
#define IS_V8_ARRAY(type) (type == Javet::Enums::V8ValueReferenceType::Array)
#define IS_V8_ARRAY_BUFFER(type) (type == Javet::Enums::V8ValueReferenceType::ArrayBuffer)
#define IS_V8_ARGUMENTS(type) (type == Javet::Enums::V8ValueReferenceType::Arguments)
#define IS_V8_FUNCTION(type) (type == Javet::Enums::V8ValueReferenceType::Function)
#define IS_V8_MAP(type) (type == Javet::Enums::V8ValueReferenceType::Map)
#define IS_V8_OBJECT(type) (type == Javet::Enums::V8ValueReferenceType::Object)
#define IS_V8_SET(type) (type == Javet::Enums::V8ValueReferenceType::Set)

#define FETCH_JNI_ENV \
	JNIEnv* jniEnv; \
	Javet::GlobalJavaVM->GetEnv((void**)&jniEnv, JNI_VERSION_1_8); \
	Javet::GlobalJavaVM->AttachCurrentThread((void**)&jniEnv, nullptr); \

#define RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle) \
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle); \
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate); \
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8::Local<v8::Context>::New(v8Runtime->v8Isolate, v8Runtime->v8Context); \
	v8::Context::Scope v8ContextScope(v8Context); \

#define RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle); \
	auto v8PersistentObjectPointer = reinterpret_cast<v8::Persistent<v8::Object>*>(v8ValueHandle); \
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate); \
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8::Local<v8::Context>::New(v8Runtime->v8Isolate, v8Runtime->v8Context); \
	v8::Context::Scope v8ContextScope(v8Context); \
	auto v8LocalObject = v8PersistentObjectPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2) \
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle); \
	auto v8PersistentObjectPointer1 = reinterpret_cast<v8::Persistent<v8::Object>*>(v8ValueHandle1); \
	auto v8PersistentObjectPointer2 = reinterpret_cast<v8::Persistent<v8::Object>*>(v8ValueHandle2); \
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate); \
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8::Local<v8::Context>::New(v8Runtime->v8Isolate, v8Runtime->v8Context); \
	v8::Context::Scope v8ContextScope(v8Context); \
	auto v8LocalObject1 = v8PersistentObjectPointer1->Get(v8Context->GetIsolate()); \
	auto v8LocalObject2 = v8PersistentObjectPointer2->Get(v8Context->GetIsolate());

#define SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, v8Value) \
	try { \
		return Javet::Converter::ToExternalV8Value(jniEnv, v8Context, v8Value); \
	} \
	catch (const std::exception& e) { \
		Javet::Exceptions::ThrowJavetConverterException(jniEnv, e.what()); \
	}

namespace Javet {
	namespace Main {
		static jclass jclassV8ValueInteger;
		static jmethodID jmethodIDV8ValueIntegerToPrimitive;

		static jclass jclassV8ValueString;
		static jmethodID jmethodIDV8ValueStringToPrimitive;

		/*
		These Java classes and methods need to be initialized within this file
		because the memory address probed changes in another file,
		or runtime memory corruption will take place.
		*/
		void Initialize(JNIEnv* jniEnv) {
			jclassV8ValueInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueInteger"));
			jmethodIDV8ValueIntegerToPrimitive = jniEnv->GetMethodID(jclassV8ValueInteger, "toPrimitive", "()I");

			jclassV8ValueString = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueString"));
			jmethodIDV8ValueStringToPrimitive = jniEnv->GetMethodID(jclassV8ValueString, "toPrimitive", "()Ljava/lang/String;");
		}

		/*
		This callback function has to stay within the same file
		so that the memory address doesn't get messed up.
		*/
		void FunctionCallback(const v8::FunctionCallbackInfo<v8::Value>& args) {
			FETCH_JNI_ENV;
			auto v8LocalContextHandle = args.Data().As<v8::BigInt>();
			auto umContext = reinterpret_cast<jobject>(v8LocalContextHandle->Int64Value());
			Javet::Callback::JavetCallbackContextReference javetCallbackContextReference(jniEnv, umContext);
			javetCallbackContextReference.Invoke(args);
		}

		void GlobalAccessorGetterCallback(
			v8::Local<v8::String> propertyName,
			const v8::PropertyCallbackInfo<v8::Value>& args) {
			args.GetReturnValue().Set(args.GetIsolate()->GetCurrentContext()->Global());
		}

		void CloseWeakObjectReference(const v8::WeakCallbackInfo<Javet::Callback::V8ValueReference>& data) {
			FETCH_JNI_ENV;
			auto v8ValueReference = data.GetParameter();
			v8ValueReference->Close(jniEnv);
			delete v8ValueReference;
		}
	}
}

JNIEXPORT jint JNICALL JNI_OnLoad
(JavaVM* javeVM, void*) {
	JNIEnv* jniEnv;
	if (javeVM->GetEnv((void**)&jniEnv, JNI_VERSION_1_8) != JNI_OK) {
		return ERROR_JNI_ON_LOAD;
	}
	if (jniEnv == nullptr) {
		return ERROR_JNI_ON_LOAD;
	}
	Javet::GlobalJavaVM = javeVM;
	v8::V8::InitializeICU();
	Javet::GlobalV8Platform = v8::platform::NewDefaultPlatform();
	v8::V8::InitializePlatform(Javet::GlobalV8Platform.get());
	v8::V8::Initialize();
	Javet::Callback::Initialize(jniEnv);
	Javet::Converter::Initialize(jniEnv);
	Javet::Exceptions::Initialize(jniEnv);
	Javet::Main::Initialize(jniEnv);
	return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_add
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_SET(v8ValueType)) {
		auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
		auto unusedSet = v8LocalObject.As<v8::Set>()->Add(v8Context, v8ValueValue);
	}
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_call
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mReceiver, jboolean mReturnResult, jobjectArray mValues) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsFunction()) {
		v8::TryCatch v8TryCatch(v8Runtime->v8Isolate);
		v8::MaybeLocal<v8::Value> maybeLocalValueResult;
		auto umReceiver = Javet::Converter::ToV8Value(jniEnv, v8Context, mReceiver);
		uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
		if (valueCount > 0) {
			auto umValuesPointer = Javet::Converter::ToV8Values(jniEnv, v8Context, mValues);
			maybeLocalValueResult = v8LocalObject.As<v8::Function>()->Call(v8Context, umReceiver, valueCount, umValuesPointer.get());
		}
		else {
			maybeLocalValueResult = v8LocalObject.As<v8::Function>()->Call(v8Context, umReceiver, 0, nullptr);
		}
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
		}
		else if (mReturnResult) {
			SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_callAsConstructor
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray mValues) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsFunction()) {
		v8::TryCatch v8TryCatch(v8Runtime->v8Isolate);
		v8::MaybeLocal<v8::Value> maybeLocalValueResult;
		uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
		if (valueCount > 0) {
			auto umValuesPointer = Javet::Converter::ToV8Values(jniEnv, v8Context, mValues);
			maybeLocalValueResult = v8LocalObject.As<v8::Function>()->CallAsConstructor(v8Context, valueCount, umValuesPointer.get());
		}
		else {
			maybeLocalValueResult = v8LocalObject.As<v8::Function>()->CallAsConstructor(v8Context, 0, nullptr);
		}
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
		}
		else {
			SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return nullptr;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_clearWeak
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject() && !v8PersistentObjectPointer->IsEmpty() && v8PersistentObjectPointer->IsWeak()) {
		auto v8ValueReference = v8PersistentObjectPointer->ClearWeak<Javet::Callback::V8ValueReference>();
		v8ValueReference->Clear(jniEnv);
		delete v8ValueReference;
	}
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_cloneV8Value
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto clonedV8LocalObject = v8::Local<v8::Object>::New(v8Context->GetIsolate(), v8LocalObject);
	return Javet::Converter::ToExternalV8Value(jniEnv, v8Context, clonedV8LocalObject);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	if (v8Runtime->v8Locker != nullptr) {
		Javet::Exceptions::ThrowJavetV8RuntimeLockConflictException(jniEnv, "Cannot close V8 runtime because the native lock is not released");
	}
	else {
		v8Runtime->v8Context.Reset();
		v8Runtime->v8GlobalObject.Reset();
		// Isolate must be the last one to be disposed.
		if (v8Runtime->v8Isolate != nullptr) {
			v8Runtime->v8Isolate->Dispose();
			v8Runtime->v8Isolate = nullptr;
		}
	}
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_compileOnly
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mScript,
	jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	v8::TryCatch v8TryCatch(v8Runtime->v8Isolate);
	auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
	v8::ScriptOrigin* scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
		jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
	auto maybeLocalCompiledScript = v8::Script::Compile(v8Context, umScript, scriptOriginPointer);
	if (scriptOriginPointer != nullptr) {
		delete scriptOriginPointer;
	}
	if (v8TryCatch.HasCaught()) {
		Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
	}
}

/*
Creating multiple isolates allows running JavaScript code in multiple threads, truly parallel.
*/
JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jstring mGlobalName) {
	auto v8Runtime = new Javet::V8Runtime();
	v8Runtime->mException = nullptr;
	jlong v8RuntimeHandle = reinterpret_cast<jlong>(v8Runtime);
	Java_com_caoccao_javet_interop_V8Native_resetV8Isolate(jniEnv, callerClass, v8RuntimeHandle, mGlobalName);
	return v8RuntimeHandle;
}

/*
It only supports Object, Array, Function, Map, Set for now.
Error, Promise, RegExp, Proxy, Symbol, etc. are not supported.
*/
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Value
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jint v8ValueType, jobject mContext) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	v8::Local<v8::Value> v8ValueValue;
	if (IS_V8_OBJECT(v8ValueType)) {
		v8ValueValue = v8::Object::New(v8Context->GetIsolate());
	}
	else if (IS_V8_ARRAY(v8ValueType)) {
		v8ValueValue = v8::Array::New(v8Context->GetIsolate());
	}
	else if (IS_V8_ARRAY_BUFFER(v8ValueType)) {
		if (IS_JAVA_INTEGER(jniEnv, mContext)) {
			v8ValueValue = v8::ArrayBuffer::New(v8Context->GetIsolate(), TO_JAVA_INTEGER(jniEnv, mContext));
		}
	}
	else if (IS_V8_FUNCTION(v8ValueType)) {
		jobject umContext = jniEnv->NewGlobalRef(mContext);
		Javet::Callback::JavetCallbackContextReference javetCallbackContextReference(jniEnv, umContext);
		javetCallbackContextReference.SetHandle();
		auto v8LocalContextHandle = v8::BigInt::New(v8Context->GetIsolate(), reinterpret_cast<int64_t>(umContext));
		v8ValueValue = v8::Function::New(v8Context, Javet::Main::FunctionCallback, v8LocalContextHandle).ToLocalChecked();
	}
	else if (IS_V8_MAP(v8ValueType)) {
		v8ValueValue = v8::Map::New(v8Context->GetIsolate());
	}
	else if (IS_V8_SET(v8ValueType)) {
		v8ValueValue = v8::Set::New(v8Context->GetIsolate());
	}
	if (!v8ValueValue.IsEmpty()) {
		SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, v8ValueValue);
	}
	return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_delete
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
	if (IS_V8_ARRAY(v8ValueType)) {
		if (IS_JAVA_INTEGER(jniEnv, key)) {
			jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
			return v8LocalObject.As<v8::Array>()->Delete(v8Context, integerKey).FromMaybe(false);
		}
		else {
			return v8LocalObject.As<v8::Array>()->Delete(v8Context, v8ValueKey).FromMaybe(false);
		}
	}
	else if (IS_V8_MAP(v8ValueType)) {
		return v8LocalObject.As<v8::Map>()->Delete(v8Context, v8ValueKey).FromMaybe(false);
	}
	else if (IS_V8_SET(v8ValueType)) {
		return v8LocalObject.As<v8::Set>()->Delete(v8Context, v8ValueKey).FromMaybe(false);
	}
	else if (v8LocalObject->IsObject()) {
		if (IS_JAVA_INTEGER(jniEnv, key)) {
			jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
			return v8LocalObject->Delete(v8Context, integerKey).FromMaybe(false);
		}
		else {
			return v8LocalObject->Delete(v8Context, v8ValueKey).FromMaybe(false);
		}
	}
	return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_equals
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
	RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
	return v8LocalObject1->Equals(v8Context, v8LocalObject2).FromMaybe(false);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mScript, jboolean mReturnResult,
	jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	v8::TryCatch v8TryCatch(v8Runtime->v8Isolate);
	auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
	v8::ScriptOrigin* scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
		jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
	auto maybeLocalCompiledScript = v8::Script::Compile(v8Context, umScript, scriptOriginPointer);
	if (scriptOriginPointer != nullptr) {
		delete scriptOriginPointer;
	}
	if (v8TryCatch.HasCaught()) {
		Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
	}
	else if (!maybeLocalCompiledScript.IsEmpty()) {
		v8::Local<v8::Script> compliedScript = maybeLocalCompiledScript.ToLocalChecked();
		v8::MaybeLocal<v8::Value> maybeLocalValueResult = compliedScript->Run(v8Context);
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
		}
		else if (mReturnResult) {
			SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_get
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
	v8::Local<v8::Value> v8ValueValue;
	if (IS_V8_ARGUMENTS(v8ValueType) || IS_V8_ARRAY(v8ValueType) || v8LocalObject->IsTypedArray()) {
		if (IS_JAVA_INTEGER(jniEnv, key)) {
			jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
			if (integerKey >= 0) {
				v8ValueValue = v8LocalObject.As<v8::Array>()->Get(v8Context, integerKey).ToLocalChecked();
			}
		}
		else if (!v8ValueKey.IsEmpty()) {
			v8ValueValue = v8LocalObject.As<v8::Array>()->Get(v8Context, v8ValueKey).ToLocalChecked();
		}
	}
	else if (!v8ValueKey.IsEmpty()) {
		if (IS_V8_MAP(v8ValueType)) {
			v8ValueValue = v8LocalObject.As<v8::Map>()->Get(v8Context, v8ValueKey).ToLocalChecked();
		}
		else if (IS_V8_SET(v8ValueType)) {
			v8ValueValue = v8LocalObject.As<v8::Set>()->Get(v8Context, v8ValueKey).ToLocalChecked();
		}
		else if (v8LocalObject->IsObject()) {
			if (IS_JAVA_INTEGER(jniEnv, key)) {
				jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
				v8ValueValue = v8LocalObject->Get(v8Context, integerKey).ToLocalChecked();
			}
			else {
				v8ValueValue = v8LocalObject->Get(v8Context, v8ValueKey).ToLocalChecked();
			}
		}
	}
	if (!v8ValueValue.IsEmpty()) {
		SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, v8ValueValue);
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getGlobalObject
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	return Javet::Converter::ToExternalV8ValueGlobalObject(jniEnv, v8Runtime->v8GlobalObject);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getIdentityHash
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	return v8LocalObject->GetIdentityHash();
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getLength
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_ARRAY(v8ValueType)) {
		return (jint)v8LocalObject.As<v8::Array>()->Length();
	}
	if (v8LocalObject->IsTypedArray()) {
		return (jint)v8LocalObject.As<v8::TypedArray>()->Length();
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getSize
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_MAP(v8ValueType)) {
		return (jint)v8LocalObject.As<v8::Map>()->Size();
	}
	if (IS_V8_SET(v8ValueType)) {
		return (jint)v8LocalObject.As<v8::Set>()->Size();
	}
	return 0;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getOwnPropertyNames
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, v8LocalObject->GetOwnPropertyNames(v8Context).ToLocalChecked());
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPropertyNames
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, v8LocalObject->GetPropertyNames(v8Context).ToLocalChecked());
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getProperty
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject()) {
		v8::Local<v8::Value> v8ValueValue;
		if (IS_JAVA_INTEGER(jniEnv, key)) {
			jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
			v8ValueValue = v8LocalObject->Get(v8Context, integerKey).ToLocalChecked();
		}
		else {
			auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
			if (!v8ValueKey.IsEmpty()) {
				v8ValueValue = v8LocalObject->Get(v8Context, v8ValueKey).ToLocalChecked();
			}
		}
		if (!v8ValueValue.IsEmpty()) {
			SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, v8ValueValue);
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
(JNIEnv* jniEnv, jclass callerClass) {
	const char* utfString = v8::V8::GetVersion();
	return jniEnv->NewStringUTF(utfString);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_has
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
	if (!v8ValueKey.IsEmpty()) {
		if (IS_V8_MAP(v8ValueType)) {
			return v8LocalObject.As<v8::Map>()->Has(v8Context, v8ValueKey).FromMaybe(false);
		}
		else if (IS_V8_SET(v8ValueType)) {
			return v8LocalObject.As<v8::Set>()->Has(v8Context, v8ValueKey).FromMaybe(false);
		}
		else if (v8LocalObject->IsObject()) {
			if (IS_JAVA_INTEGER(jniEnv, value)) {
				jint integerKey = TO_JAVA_INTEGER(jniEnv, value);
				return v8LocalObject->Has(v8Context, integerKey).FromMaybe(false);
			}
			else {
				return v8LocalObject->Has(v8Context, v8ValueKey).FromMaybe(false);
			}
		}
	}
	return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasOwnProperty
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject()) {
		if (IS_JAVA_INTEGER(jniEnv, key)) {
			jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
			return v8LocalObject->HasOwnProperty(v8Context, integerKey).FromMaybe(false);
		}
		else if (IS_JAVA_STRING(jniEnv, key)) {
			jstring stringKey = TO_JAVA_STRING(jniEnv, key);
			auto v8ValueKey = Javet::Converter::ToV8String(jniEnv, v8Context, stringKey);
			return v8LocalObject->HasOwnProperty(v8Context, v8ValueKey).FromMaybe(false);
		}
	}
	return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_invoke
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mFunctionName, jboolean mReturnResult, jobjectArray mValues) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject()) {
		auto v8Function = v8LocalObject->Get(v8Context, Javet::Converter::ToV8String(jniEnv, v8Context, mFunctionName)).ToLocalChecked();
		if (v8Function->IsFunction()) {
			v8::TryCatch v8TryCatch(v8Runtime->v8Isolate);
			v8::MaybeLocal<v8::Value> maybeLocalValueResult;
			uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
			if (valueCount > 0) {
				auto umValuesPointer = Javet::Converter::ToV8Values(jniEnv, v8Context, mValues);
				maybeLocalValueResult = v8Function.As<v8::Function>()->Call(v8Context, v8LocalObject, valueCount, umValuesPointer.get());
			}
			else {
				maybeLocalValueResult = v8Function.As<v8::Function>()->Call(v8Context, v8LocalObject, 0, nullptr);
			}
			if (v8TryCatch.HasCaught()) {
				Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
			}
			else if (mReturnResult) {
				SAFE_CONVERT_AND_RETURN_JAVE_V8_VALUE(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
			}
		}
	}
	return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isDead
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	return v8Runtime->v8Isolate->IsDead();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isInUse
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	return v8Runtime->v8Isolate->IsInUse();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isWeak
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject() && !v8PersistentObjectPointer->IsEmpty()) {
		return (jboolean)v8PersistentObjectPointer->IsWeak();
	}
	return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lockV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	if (v8Runtime->v8Locker != nullptr) {
		Javet::Exceptions::ThrowJavetV8RuntimeLockConflictException(jniEnv, "Cannot acquire V8 native lock because it has not been released yet");
	}
	else {
		v8Runtime->v8Locker = new v8::Locker(v8Runtime->v8Isolate);
	}
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeJNIGlobalRef
(JNIEnv* jniEnv, jclass callerClass, jlong handle) {
	jniEnv->DeleteGlobalRef((jobject)handle);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
(JNIEnv* jniEnv, jclass callerClass, jlong referenceHandle) {
	auto v8PersistentObjectPointer = reinterpret_cast<v8::Persistent<v8::Object>*>(referenceHandle);
	v8PersistentObjectPointer->Reset();
	delete v8PersistentObjectPointer;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_requestGarbageCollectionForTesting
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jboolean fullGC) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	v8Runtime->v8Isolate->RequestGarbageCollectionForTesting(((bool)fullGC)
		? v8::Isolate::GarbageCollectionType::kFullGarbageCollection
		: v8::Isolate::GarbageCollectionType::kMinorGarbageCollection);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Context
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mGlobalName) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	v8::Locker v8Locker(v8Runtime->v8Isolate);
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate);
	// Create a stack-allocated handle scope.
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate);
	auto v8IsolateHandle = v8::ObjectTemplate::New(v8Runtime->v8Isolate);
	auto v8Context = v8::Context::New(v8Runtime->v8Isolate, nullptr, v8IsolateHandle);
	// Redirects global calls to a given global name. E.g. parseInt() -> window.parseInt().
	if (mGlobalName != nullptr) {
		auto umGlobalName = Javet::Converter::ToV8String(jniEnv, v8Context, mGlobalName);
		v8IsolateHandle->SetAccessor(umGlobalName, Javet::Main::GlobalAccessorGetterCallback);
	}
	v8Runtime->v8Context.Reset(v8Runtime->v8Isolate, v8Context);
	v8Runtime->v8GlobalObject.Reset(
		v8Runtime->v8Isolate,
		v8Context->Global()->GetPrototype()->ToObject(v8Context).ToLocalChecked());
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Isolate
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mGlobalName) {
	Java_com_caoccao_javet_interop_V8Native_closeV8Runtime(jniEnv, callerClass, v8RuntimeHandle);
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	v8::Isolate::CreateParams create_params;
	create_params.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();
	v8Runtime->v8Isolate = v8::Isolate::New(create_params);
	v8::Locker v8Locker(v8Runtime->v8Isolate);
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate);
	// Create a stack-allocated handle scope.
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate);
	auto v8IsolateHandle = v8::ObjectTemplate::New(v8Runtime->v8Isolate);
	auto v8Context = v8::Context::New(v8Runtime->v8Isolate, nullptr, v8IsolateHandle);
	// Redirects global calls to a given global name. E.g. parseInt() -> window.parseInt().
	if (mGlobalName != nullptr) {
		auto umGlobalName = Javet::Converter::ToV8String(jniEnv, v8Context, mGlobalName);
		v8IsolateHandle->SetAccessor(umGlobalName, Javet::Main::GlobalAccessorGetterCallback);
	}
	v8Runtime->v8Context.Reset(v8Runtime->v8Isolate, v8Context);
	v8Runtime->v8GlobalObject.Reset(
		v8Runtime->v8Isolate,
		v8Context->Global()->GetPrototype()->ToObject(v8Context).ToLocalChecked());
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_set
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jobject value) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
	auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
	if (IS_V8_ARRAY(v8ValueType)) {
		if (IS_JAVA_INTEGER(jniEnv, key)) {
			jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
			return v8LocalObject.As<v8::Array>()->Set(v8Context, integerKey, v8ValueValue).FromMaybe(false);
		}
		else if (!v8ValueKey.IsEmpty()) {
			return v8LocalObject.As<v8::Array>()->Set(v8Context, v8ValueKey, v8ValueValue).FromMaybe(false);
		}
	}
	else if (!v8ValueKey.IsEmpty()) {
		if (IS_V8_MAP(v8ValueType)) {
			auto unusedSet = v8LocalObject.As<v8::Map>()->Set(v8Context, v8ValueKey, v8ValueValue);
			return true;
		}
		else if (v8LocalObject->IsObject()) {
			if (IS_JAVA_INTEGER(jniEnv, key)) {
				jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
				return v8LocalObject->Set(v8Context, integerKey, v8ValueValue).FromMaybe(false);
			}
			else {
				return v8LocalObject->Set(v8Context, v8ValueKey, v8ValueValue).FromMaybe(false);
			}
		}
	}
	return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setFlags
(JNIEnv* jniEnv, jclass, jstring flags) {
	if (flags) {
		char const* str = jniEnv->GetStringUTFChars(flags, nullptr);
		v8::V8::SetFlagsFromString(str, jniEnv->GetStringUTFLength(flags));
		jniEnv->ReleaseStringUTFChars(flags, str);
		v8::V8::Initialize();
	}
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setProperty
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jobject value) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject()) {
		auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
		if (IS_JAVA_INTEGER(jniEnv, key)) {
			jint integerKey = TO_JAVA_INTEGER(jniEnv, key);
			return v8LocalObject->Set(v8Context, integerKey, v8ValueValue).FromMaybe(false);
		}
		else {
			auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
			if (!v8ValueKey.IsEmpty()) {
				return v8LocalObject->Set(v8Context, v8ValueKey, v8ValueValue).FromMaybe(false);
			}
		}
	}
	return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setWeak
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject objectReference) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject() && !v8PersistentObjectPointer->IsEmpty() && !v8PersistentObjectPointer->IsWeak()) {
		auto v8ValueReference = new Javet::Callback::V8ValueReference;
		v8ValueReference->v8Isolate = v8Context->GetIsolate();
		v8ValueReference->objectReference = jniEnv->NewGlobalRef(objectReference);
		v8ValueReference->v8PersistentObjectPointer = v8PersistentObjectPointer;
		v8PersistentObjectPointer->SetWeak(v8ValueReference, Javet::Main::CloseWeakObjectReference, v8::WeakCallbackType::kParameter);
	}
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_sameValue
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
	RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
	return v8LocalObject1->SameValue(v8LocalObject2);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_strictEquals
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle1, jlong v8ValueHandle2) {
	RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2);
	return v8LocalObject1->StrictEquals(v8LocalObject2);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_terminateExecution
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	v8Runtime->v8Isolate->TerminateExecution();
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toProtoString
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	v8::Local<v8::String> v8String;
	if (v8LocalObject->IsObject()) {
		v8String = v8LocalObject->ObjectProtoToString(v8Context).ToLocalChecked();
	}
	v8::String::Value stringValue(v8Context->GetIsolate(), v8String);
	return jniEnv->NewString(*stringValue, stringValue.length());
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toString
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	v8::Local<v8::String> v8String;
	if (IS_V8_ARRAY(v8ValueType)) {
		v8String = v8LocalObject.As<v8::Array>()->ToString(v8Context).ToLocalChecked();
	}
	else if (IS_V8_MAP(v8ValueType)) {
		v8String = v8LocalObject.As<v8::Map>()->ToString(v8Context).ToLocalChecked();
	}
	else if (IS_V8_SET(v8ValueType)) {
		v8String = v8LocalObject.As<v8::Set>()->ToString(v8Context).ToLocalChecked();
	}
	else if (v8LocalObject->IsObject()) {
		v8String = v8LocalObject->ToString(v8Context).ToLocalChecked();
	}
	v8::String::Value stringValue(v8Context->GetIsolate(), v8String);
	return jniEnv->NewString(*stringValue, stringValue.length());
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	if (v8Runtime->v8Locker == nullptr) {
		Javet::Exceptions::ThrowJavetV8RuntimeLockConflictException(jniEnv, "Cannot release V8 native lock because it has not been acquired yet");
	}
	else {
		delete v8Runtime->v8Locker;
		v8Runtime->v8Locker = nullptr;
	}
}

