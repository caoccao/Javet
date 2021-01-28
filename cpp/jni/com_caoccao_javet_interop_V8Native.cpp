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

#define HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle); \
	auto v8PersistentObjectPointer = reinterpret_cast<v8::Persistent<v8::Object>*>(v8ValueHandle); \
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate); \
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8::Local<v8::Context>::New(v8Runtime->v8Isolate, v8Runtime->v8Context); \
	v8::Context::Scope v8ContextScope(v8Context); \
	auto v8LocalObject = v8PersistentObjectPointer->Get(v8Runtime->v8Isolate);

 /*
  * Development Guide:
  * 1. Namespace is not recommended in this project.
  * 2. Methods are expected to be sorted alphabatically except JNI_OnLoad.
  */

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
	Javet::Converter::initializeJavetConverter(jniEnv);
	Javet::Exceptions::initializeJavetExceptions(jniEnv);
	return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	if (v8Runtime->v8Locker != nullptr) {
		Javet::Exceptions::throwJavetV8RuntimeLockConflictException(jniEnv, "Cannot close V8 runtime because the native lock is not released");
	}
	else {
		v8Runtime->caller = nullptr;
		v8Runtime->v8Context.Reset();
		if (v8Runtime->v8GlobalObject != nullptr) {
			v8Runtime->v8GlobalObject->Reset();
			v8Runtime->v8GlobalObject = nullptr;
		}
		// Isolate must be the last one to be disposed.
		if (v8Runtime->v8Isolate != nullptr) {
			v8Runtime->v8Isolate->Dispose();
			v8Runtime->v8Isolate = nullptr;
		}
	}
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_containsKey__JJII
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jint key) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8ValueType == Javet::Enums::V8ValueReferenceType::Array) {
		// It does nothing.
	}
	else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Map) {
		return v8LocalObject.As<v8::Map>()->Has(
			v8Context, Javet::Converter::toV8Integer(v8Context->GetIsolate(), key)).FromMaybe(false);
	}
	else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Set) {
		return v8LocalObject.As<v8::Set>()->Has(
			v8Context, Javet::Converter::toV8Integer(v8Context->GetIsolate(), key)).FromMaybe(false);
	}
	else {
		return v8LocalObject->Has(v8Context, key).FromMaybe(false);
	}
	return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_containsKey__JJILjava_lang_String_2
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring key) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8ValueType == Javet::Enums::V8ValueReferenceType::Array) {
		// It does nothing.
	}
	else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Map) {
		return v8LocalObject.As<v8::Map>()->Has(
			v8Context, Javet::Converter::toV8String(jniEnv, v8Context->GetIsolate(), key)).FromMaybe(false);
	}
	else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Set) {
		return v8LocalObject.As<v8::Set>()->Has(
			v8Context, Javet::Converter::toV8String(jniEnv, v8Context->GetIsolate(), key)).FromMaybe(false);
	}
	else {
		return v8LocalObject->Has(
			v8Context, Javet::Converter::toV8String(jniEnv, v8Context->GetIsolate(), key)).FromMaybe(false);
	}
	return false;
}

// Creating multiple isolates allows running JavaScript code in multiple threads, truly parallel.
JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Runtime
(JNIEnv* jniEnv, jclass caller, jstring mGlobalName) {
	auto v8Runtime = new Javet::V8Runtime();
	v8Runtime->mException = nullptr;
	jlong v8RuntimeHandle = reinterpret_cast<jlong>(v8Runtime);
	Java_com_caoccao_javet_interop_V8Native_resetV8Runtime(jniEnv, caller, v8RuntimeHandle, mGlobalName);
	return v8RuntimeHandle;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jstring mScript, jboolean mReturnResult,
	jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	v8::ScriptOrigin* scriptOriginPointer = Javet::Converter::toV8ScriptOringinPointer(
		jniEnv, v8Runtime->v8Isolate, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate);
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate);
	auto v8Context = v8::Local<v8::Context>::New(v8Runtime->v8Isolate, v8Runtime->v8Context);
	v8::Context::Scope v8ContextScope(v8Context);
	v8::TryCatch v8TryCatchCompilation(v8Runtime->v8Isolate);
	auto umScript = Javet::Converter::toV8String(jniEnv, v8Runtime->v8Isolate, mScript);
	auto maybeLocalCompiledScript = v8::Script::Compile(v8Context, umScript, scriptOriginPointer);
	if (scriptOriginPointer != nullptr) {
		delete scriptOriginPointer;
	}
	if (v8TryCatchCompilation.HasCaught()) {
		Javet::Exceptions::throwJavetCompilationException(jniEnv, v8Context, v8TryCatchCompilation);
	}
	else if (!maybeLocalCompiledScript.IsEmpty()) {
		v8::TryCatch v8TryCatchExecution(v8Runtime->v8Isolate);
		v8::Local<v8::Script> compliedScript = maybeLocalCompiledScript.ToLocalChecked();
		v8::MaybeLocal<v8::Value> maybeLocalValueResult = compliedScript->Run(v8Context);
		if (v8TryCatchExecution.HasCaught()) {
			Javet::Exceptions::throwJavetExecutionException(jniEnv, v8Context, v8TryCatchExecution);
		}
		else {
			if (mReturnResult) {
				try {
					auto localValueResult = maybeLocalValueResult.ToLocalChecked();
					return Javet::Converter::toJV8Value(jniEnv, v8Context, localValueResult);
				}
				catch (const std::exception& e) {
					Javet::Exceptions::throwJavetConverterException(jniEnv, e.what());
				}
			}
		}
	}
	return nullptr;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getLength
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8ValueType == Javet::Enums::V8ValueReferenceType::Array) {
		return v8LocalObject.As<v8::Array>()->Length();
	}
	return 0;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getSize
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8ValueType == Javet::Enums::V8ValueReferenceType::Map) {
		return (jint)v8LocalObject.As<v8::Map>()->Size();
	}
	if (v8ValueType == Javet::Enums::V8ValueReferenceType::Set) {
		return (jint)v8LocalObject.As<v8::Set>()->Size();
	}
	return 0;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getOwnPropertyNames
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto localValueResult = v8LocalObject->GetOwnPropertyNames(v8Context).ToLocalChecked();
	return Javet::Converter::toJV8Value(jniEnv, v8Context, localValueResult);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPropertyNames
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto localValueResult = v8LocalObject->GetPropertyNames(v8Context).ToLocalChecked();
	return Javet::Converter::toJV8Value(jniEnv, v8Context, localValueResult);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getValue__JJII
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jint index) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8ValueType == Javet::Enums::V8ValueReferenceType::Array) {
		if (index >= 0) {
			auto localValueResult = v8LocalObject.As<v8::Array>()->Get(v8Context, index).ToLocalChecked();
			return Javet::Converter::toJV8Value(jniEnv, v8Context, localValueResult);
		}
	}
	else {
		v8::Local<v8::Value> localValueResult;
		if (v8ValueType == Javet::Enums::V8ValueReferenceType::Map) {
			localValueResult = v8LocalObject.As<v8::Map>()->Get(
				v8Context, Javet::Converter::toV8Integer(v8Context->GetIsolate(), index)).ToLocalChecked();
		}
		else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Set) {
			localValueResult = v8LocalObject.As<v8::Set>()->Get(
				v8Context, Javet::Converter::toV8Integer(v8Context->GetIsolate(), index)).ToLocalChecked();
		}
		else {
			localValueResult = v8LocalObject->Get(v8Context, index).ToLocalChecked();
		}
		return Javet::Converter::toJV8Value(jniEnv, v8Context, localValueResult);
	}
	return Javet::Converter::toJV8ValueUndefined(jniEnv);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getValue__JJILjava_lang_String_2
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring key) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (key != nullptr) {
		auto v8StringKey = Javet::Converter::toV8String(jniEnv, v8Context->GetIsolate(), key);
		v8::Local<v8::Value> localValueResult;
		if (v8ValueType == Javet::Enums::V8ValueReferenceType::Map) {
			localValueResult = v8LocalObject.As<v8::Map>()->Get(v8Context, v8StringKey).ToLocalChecked();
		}
		else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Set) {
			localValueResult = v8LocalObject.As<v8::Set>()->Get(v8Context, v8StringKey).ToLocalChecked();
		}
		else {
			localValueResult = v8LocalObject->Get(v8Context, v8StringKey).ToLocalChecked();
		}
		return Javet::Converter::toJV8Value(jniEnv, v8Context, localValueResult);
	}
	return Javet::Converter::toJV8ValueUndefined(jniEnv);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
(JNIEnv* jniEnv, jclass caller) {
	const char* utfString = v8::V8::GetVersion();
	return jniEnv->NewStringUTF(utfString);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lockV8Runtime
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	if (v8Runtime->v8Locker != nullptr) {
		Javet::Exceptions::throwJavetV8RuntimeLockConflictException(jniEnv, "Cannot acquire V8 native lock because it has not been released yet");
	}
	else {
		v8Runtime->v8Locker = new v8::Locker(v8Runtime->v8Isolate);
	}
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
(JNIEnv* jniEnv, jclass caller, jlong referenceHandle) {
	auto v8PersistentObjectPointer = reinterpret_cast<v8::Persistent<v8::Object>*>(referenceHandle);
	delete v8PersistentObjectPointer;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Runtime
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jstring mGlobalName) {
	Java_com_caoccao_javet_interop_V8Native_closeV8Runtime(jniEnv, caller, v8RuntimeHandle);
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	// TODO Is caller needed to be stored?
	v8Runtime->caller = jniEnv->NewGlobalRef(caller);
	v8::Isolate::CreateParams create_params;
	create_params.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();
	v8Runtime->v8Isolate = v8::Isolate::New(create_params);
	v8Runtime->v8GlobalObject = new v8::Persistent<v8::Object>;
	v8::Locker v8Locker(v8Runtime->v8Isolate);
	v8::Isolate::Scope v8IsolateScope(v8Runtime->v8Isolate);
	// Create a stack-allocated handle scope.
	v8::HandleScope v8HandleScope(v8Runtime->v8Isolate);
	auto v8IsolateHandle = v8::ObjectTemplate::New(v8Runtime->v8Isolate);
	// Redirects global calls to a given global name. E.g. parseInt() -> window.parseInt().
	if (mGlobalName != nullptr) {
		auto umGlobalName = Javet::Converter::toV8String(jniEnv, v8Runtime->v8Isolate, mGlobalName);
		v8IsolateHandle->SetAccessor(umGlobalName, Javet::Callback::GlobalPropertyAccessorCallback);
	}
	auto v8Context = v8::Context::New(v8Runtime->v8Isolate, nullptr, v8IsolateHandle);
	v8Runtime->v8Context.Reset(v8Runtime->v8Isolate, v8Context);
	v8Runtime->v8GlobalObject->Reset(
		v8Runtime->v8Isolate,
		v8Context->Global()->GetPrototype()->ToObject(v8Context).ToLocalChecked());
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

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toString
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	v8::Local<v8::String> v8String;
	if (v8ValueType == Javet::Enums::V8ValueReferenceType::Array) {
		v8String = v8LocalObject.As<v8::Array>()->ToString(v8Context).ToLocalChecked();
	}
	else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Map) {
		v8String = v8LocalObject.As<v8::Map>()->ToString(v8Context).ToLocalChecked();
	}
	else if (v8ValueType == Javet::Enums::V8ValueReferenceType::Set) {
		v8String = v8LocalObject.As<v8::Set>()->ToString(v8Context).ToLocalChecked();
	}
	else {
		v8String = v8LocalObject->ToString(v8Context).ToLocalChecked();
	}
	v8::String::Value stringValue(v8Context->GetIsolate(), v8String);
	return jniEnv->NewString(*stringValue, stringValue.length());
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
(JNIEnv* jniEnv, jclass caller, jlong v8RuntimeHandle) {
	auto v8Runtime = reinterpret_cast<Javet::V8Runtime*>(v8RuntimeHandle);
	if (v8Runtime->v8Locker == nullptr) {
		Javet::Exceptions::throwJavetV8RuntimeLockConflictException(jniEnv, "Cannot release V8 native lock because it has not been acquired yet");
	}
	else {
		delete v8Runtime->v8Locker;
		v8Runtime->v8Locker = nullptr;
	}
}

