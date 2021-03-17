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
#include <functional>
#include <string.h>
#include <map>
#include <cstdlib>
#include "com_caoccao_javet_interop_V8Native.h"
#include "javet_callbacks.h"
#include "javet_converter.h"
#include "javet_enums.h"
#include "javet_exceptions.h"
#include "javet_globals.h"
#include "javet_inspector.h"
#include "javet_logging.h"
#include "javet_node.h"
#include "javet_types.h"
#include "javet_v8.h"
#include "javet_v8_runtime.h"

 /*
  * Development Guide:
  * 1. Omitting namespace is not recommended in this project.
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
#define IS_V8_MODULE(type) (type == Javet::Enums::V8ValueReferenceType::Module)
#define IS_V8_OBJECT(type) (type == Javet::Enums::V8ValueReferenceType::Object)
#define IS_V8_PROMISE(type) (type == Javet::Enums::V8ValueReferenceType::Promise)
#define IS_V8_SCRIPT(type) (type == Javet::Enums::V8ValueReferenceType::Script)
#define IS_V8_SET(type) (type == Javet::Enums::V8ValueReferenceType::Set)

#define RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	V8IsolateScope v8IsolateScope(v8Runtime->v8Isolate); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	V8ContextScope v8ContextScope(v8Context);

#define RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8DataHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(v8DataHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	V8IsolateScope v8IsolateScope(v8Runtime->v8Isolate); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	V8ContextScope v8ContextScope(v8Context); \
	auto v8LocalData = v8PersistentDataPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentModulePointer = TO_V8_PERSISTENT_MODULE_POINTER(v8ValueHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	V8IsolateScope v8IsolateScope(v8Runtime->v8Isolate); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	V8ContextScope v8ContextScope(v8Context); \
	auto v8LocalModule = v8PersistentModulePointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentScriptPointer = TO_V8_PERSISTENT_SCRIPT_POINTER(v8ValueHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	V8IsolateScope v8IsolateScope(v8Runtime->v8Isolate); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	V8ContextScope v8ContextScope(v8Context); \
	auto v8LocalScript = v8PersistentScriptPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentObjectPointer = TO_V8_PERSISTENT_OBJECT_POINTER(v8ValueHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	V8IsolateScope v8IsolateScope(v8Runtime->v8Isolate); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	V8ContextScope v8ContextScope(v8Context); \
	auto v8LocalObject = v8PersistentObjectPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentObjectPointer1 = TO_V8_PERSISTENT_OBJECT_POINTER(v8ValueHandle1); \
	auto v8PersistentObjectPointer2 = TO_V8_PERSISTENT_OBJECT_POINTER(v8ValueHandle2); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	V8IsolateScope v8IsolateScope(v8Runtime->v8Isolate); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	V8ContextScope v8ContextScope(v8Context); \
	auto v8LocalObject1 = v8PersistentObjectPointer1->Get(v8Context->GetIsolate()); \
	auto v8LocalObject2 = v8PersistentObjectPointer2->Get(v8Context->GetIsolate());

namespace Javet {
	namespace Main {
		static JavaVM* GlobalJavaVM;

		static jclass jclassV8ValueInteger;
		static jmethodID jmethodIDV8ValueIntegerToPrimitive;

		static jclass jclassV8ValueString;
		static jmethodID jmethodIDV8ValueStringToPrimitive;

		/*
		These Java classes and methods need to be initialized within this file
		because the memory address probed changes in another file,
		or runtime memory corruption will take place.
		*/
		void Initialize(JNIEnv* jniEnv, JavaVM* javaVM) {
			GlobalJavaVM = javaVM;

			jclassV8ValueInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueInteger"));
			jmethodIDV8ValueIntegerToPrimitive = jniEnv->GetMethodID(jclassV8ValueInteger, JAVA_METHOD_TO_PRIMITIVE, "()I");

			jclassV8ValueString = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueString"));
			jmethodIDV8ValueStringToPrimitive = jniEnv->GetMethodID(jclassV8ValueString, JAVA_METHOD_TO_PRIMITIVE, "()Ljava/lang/String;");
		}

		/*
		This callback function has to stay within the same file
		so that the memory address doesn't get messed up.
		*/
		void FunctionCallback(const v8::FunctionCallbackInfo<v8::Value>& args) {
			FETCH_JNI_ENV(Javet::Main::GlobalJavaVM);
			auto v8LocalContextHandle = args.Data().As<v8::BigInt>();
			auto umContext = TO_JAVA_OBJECT(v8LocalContextHandle->Int64Value());
			Javet::Callback::JavetCallbackContextReference javetCallbackContextReference(jniEnv, umContext);
			javetCallbackContextReference.Invoke(args);
		}

		void CloseWeakDataReference(const v8::WeakCallbackInfo<Javet::Callback::V8ValueReference>& data) {
			FETCH_JNI_ENV(Javet::Main::GlobalJavaVM);
			auto v8ValueReference = data.GetParameter();
			v8ValueReference->Close(jniEnv);
			delete v8ValueReference;
		}
	}
}

jint JNI_OnLoad(JavaVM* javaVM, void* reserved) {
	LOG_INFO("JNI_Onload() begins.");
	JNIEnv* jniEnv;
	if (javaVM->GetEnv((void**)&jniEnv, JNI_VERSION_1_8) != JNI_OK) {
		return ERROR_JNI_ON_LOAD;
	}
	if (jniEnv == nullptr) {
		return ERROR_JNI_ON_LOAD;
	}
	LOG_INFO("V8::Initialize() begins.");
#ifdef ENABLE_NODE
	uv_setup_args(0, nullptr);
	std::vector<std::string> args{ "" };
	std::vector<std::string> exec_args;
	std::vector<std::string> errors;
	int exit_code = node::InitializeNodeWithArgs(&args, &exec_args, &errors);
	Javet::GlobalV8Platform = node::MultiIsolatePlatform::Create(16);
#else
	//v8::V8::InitializeICU();
	Javet::GlobalV8Platform = v8::platform::NewDefaultPlatform();
#endif
	v8::V8::InitializePlatform(Javet::GlobalV8Platform.get());
	v8::V8::Initialize();
	LOG_INFO("V8::Initialize() ends.");
	Javet::Callback::Initialize(jniEnv, javaVM);
	Javet::Converter::Initialize(jniEnv);
	Javet::Exceptions::Initialize(jniEnv);
	Javet::Inspector::Initialize(jniEnv, javaVM);
	Javet::Main::Initialize(jniEnv, javaVM);
	LOG_INFO("JNI_Onload() ends.");
	return JNI_VERSION_1_8;
}

void JNI_OnUnload(JavaVM* javaVM, void* reserved) {
	LOG_INFO("JNI_OnUnload() begins.");
	v8::V8::Dispose();
	v8::V8::ShutdownPlatform();
	LOG_INFO("JNI_OnUnload() ends.");
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_add
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_SET(v8ValueType)) {
		auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
		auto unusedSet = v8LocalObject.As<v8::Set>()->Add(v8Context, v8ValueValue);
	}
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_allowCodeGenerationFromStrings
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jboolean allow) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	v8Context->AllowCodeGenerationFromStrings(allow);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_call
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mReceiver, jboolean mResultRequired, jobjectArray mValues) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsFunction()) {
		V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
		V8MaybeLocalValue maybeLocalValueResult;
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
		else if (mResultRequired) {
			return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_callAsConstructor
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray mValues) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsFunction()) {
		V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
		V8MaybeLocalValue maybeLocalValueResult;
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
			return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return nullptr;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_clearWeak
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (!v8PersistentDataPointer->IsEmpty() && v8PersistentDataPointer->IsWeak()) {
		auto v8ValueReference = v8PersistentDataPointer->ClearWeak<Javet::Callback::V8ValueReference>();
		v8ValueReference->Clear(jniEnv);
		delete v8ValueReference;
	}
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_cloneV8Value
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto clonedV8LocalObject = V8LocalObject::New(v8Context->GetIsolate(), v8LocalObject);
	return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, clonedV8LocalObject);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	if (v8Runtime->externalV8Runtime != nullptr) {
		jniEnv->DeleteGlobalRef(v8Runtime->externalV8Runtime);
	}
	delete v8Runtime;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_compile
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mScript, jboolean mResultRequired,
	jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
	auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
	auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
		jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
	if (mIsModule) {
		v8::ScriptCompiler::Source scriptSource(umScript, *scriptOriginPointer.get());
		auto maybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(v8Runtime->v8Isolate, &scriptSource);
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
		}
		else if (mResultRequired) {
			try {
				return Javet::Converter::ToExternalV8Data(jniEnv, v8Runtime->externalV8Runtime, v8Context, maybeLocalCompiledModule.ToLocalChecked());
			}
			catch (const std::exception& e) {
				LOG_ERROR(e.what());
				Javet::Exceptions::ThrowJavetConverterException(jniEnv, e.what());
			}
		}
	}
	else {
		auto maybeLocalCompiledScript = v8::Script::Compile(v8Context, umScript, scriptOriginPointer.get());
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
		}
		else if (mResultRequired) {
			try {
				return Javet::Converter::ToExternalV8Script(jniEnv, v8Runtime->externalV8Runtime, v8Context, maybeLocalCompiledScript.ToLocalChecked());
			}
			catch (const std::exception& e) {
				LOG_ERROR(e.what());
				Javet::Exceptions::ThrowJavetConverterException(jniEnv, e.what());
			}
		}
	}
	return nullptr;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Inspector
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jobject mV8Inspector) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	v8Runtime->v8Inspector.reset(
		new Javet::Inspector::JavetInspector(v8Runtime, mV8Inspector));
}

/*
Creating multiple isolates allows running JavaScript code in multiple threads, truly parallel.
*/
JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jstring mGlobalName) {
	auto v8Runtime = new Javet::V8Runtime();
	v8Runtime->CreateV8Isolate();
	auto v8Locker = v8Runtime->GetUniqueV8Locker();
	v8Runtime->ResetV8Context(jniEnv, mGlobalName);
	return TO_JAVA_LONG(v8Runtime);
}

/*
It only supports Object, Array, Function, Map, Set for now.
Error, Promise, RegExp, Proxy, Symbol, etc. are not supported.
*/
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Value
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jint v8ValueType, jobject mContext) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	V8LocalValue v8ValueValue;
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
		auto v8LocalContextHandle = v8::BigInt::New(v8Context->GetIsolate(), TO_NATIVE_INT_64(umContext));
		v8ValueValue = v8::Function::New(v8Context, Javet::Main::FunctionCallback, v8LocalContextHandle).ToLocalChecked();
	}
	else if (IS_V8_MAP(v8ValueType)) {
		v8ValueValue = v8::Map::New(v8Context->GetIsolate());
	}
	else if (IS_V8_SET(v8ValueType)) {
		v8ValueValue = v8::Set::New(v8Context->GetIsolate());
	}
	if (!v8ValueValue.IsEmpty()) {
		return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8ValueValue);
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

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute__JLjava_lang_String_2ZLjava_lang_String_2IIIZZ
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mScript, jboolean mResultRequired,
	jstring mResourceName, jint mResourceLineOffset, jint mResourceColumnOffset, jint mScriptId, jboolean mIsWASM, jboolean mIsModule) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
	auto umScript = Javet::Converter::ToV8String(jniEnv, v8Context, mScript);
	auto scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
		jniEnv, v8Context, mResourceName, mResourceLineOffset, mResourceColumnOffset, mScriptId, mIsWASM, mIsModule);
	if (mIsModule) {
		v8::ScriptCompiler::Source scriptSource(umScript, *scriptOriginPointer.get());
		auto maybeLocalCompiledModule = v8::ScriptCompiler::CompileModule(v8Runtime->v8Isolate, &scriptSource);
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
		}
		else if (!maybeLocalCompiledModule.IsEmpty()) {
			auto compliedModule = maybeLocalCompiledModule.ToLocalChecked();
			auto maybeResult = compliedModule->InstantiateModule(v8Context, Javet::Callback::ModuleResolveCallback);
			if (maybeResult.FromMaybe(false)) {
				auto maybeLocalValueResult = compliedModule->Evaluate(v8Context);
				if (v8TryCatch.HasCaught()) {
					Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
				}
				else if (mResultRequired) {
					return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
				}
			}
		}
	}
	else {
		auto maybeLocalCompiledScript = v8::Script::Compile(v8Context, umScript, scriptOriginPointer.get());
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetCompilationException(jniEnv, v8Context, v8TryCatch);
		}
		else if (!maybeLocalCompiledScript.IsEmpty()) {
			auto compliedScript = maybeLocalCompiledScript.ToLocalChecked();
			auto maybeLocalValueResult = compliedScript->Run(v8Context);
			if (v8TryCatch.HasCaught()) {
				Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
			}
			else if (mResultRequired) {
				return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
			}
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute__JLjava_lang_String_2ZI
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mScript, jboolean mResultRequired,
	jint mNodeScriptMode) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
#ifdef ENABLE_NODE
	auto stdStringScript = Javet::Converter::ToStdString(jniEnv, mScript);
	auto uvLoop = uv_default_loop();
	auto nodeIsolateData = node::CreateIsolateData(v8Context->GetIsolate(), uvLoop);
	std::vector<std::string> args;
	std::vector<std::string> execArgs;
	auto nodeEnvironment = node::CreateEnvironment(nodeIsolateData, v8Context, args, execArgs);
	auto maybeLocalValue = node::LoadEnvironment(nodeEnvironment, stdStringScript.get()->c_str());
	if (!maybeLocalValue.IsEmpty()) {
		v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValue.ToLocalChecked());
	}
#endif
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_get
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
	V8LocalValue v8ValueValue;
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
		return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8ValueValue);
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
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
	return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalObject->GetOwnPropertyNames(v8Context).ToLocalChecked());
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPropertyNames
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalObject->GetPropertyNames(v8Context).ToLocalChecked());
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getProperty
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject()) {
		V8LocalValue v8ValueValue;
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
			return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8ValueValue);
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
(JNIEnv* jniEnv, jclass callerClass) {
	return Javet::Converter::ToJavaString(jniEnv, v8::V8::GetVersion());
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
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mFunctionName, jboolean mResultRequired, jobjectArray mValues) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalObject->IsObject()) {
		auto v8Function = v8LocalObject->Get(v8Context, Javet::Converter::ToV8String(jniEnv, v8Context, mFunctionName)).ToLocalChecked();
		if (v8Function->IsFunction()) {
			V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
			V8MaybeLocalValue maybeLocalValueResult;
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
			else if (mResultRequired) {
				return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
			}
		}
	}
	return nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isDead
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	return v8Runtime->v8Isolate->IsDead();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isInUse
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	return v8Runtime->v8Isolate->IsInUse();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isWeak
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (!v8PersistentDataPointer->IsEmpty()) {
		return (jboolean)v8PersistentDataPointer->IsWeak();
	}
	return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lockV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	if (v8Runtime->IsLocked()) {
		Javet::Exceptions::ThrowJavetV8LockConflictException(jniEnv, "Cannot acquire V8 native lock because it has not been released yet");
	}
	else {
		v8Runtime->Lock();
	}
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleEvaluate
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mResultRequired) {
	RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalModule->GetStatus() == v8::Module::Status::kInstantiated) {
		V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
		auto maybeLocalValueResult = v8LocalModule->Evaluate(v8Context);
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
		}
		else if (mResultRequired) {
			return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetException
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalModule->GetStatus() == v8::Module::Status::kErrored) {
		return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalModule->GetException());
	}
	return nullptr;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetNamespace
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalModule->GetModuleNamespace());
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetScriptId
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	return (jint)v8LocalModule->ScriptId();
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetStatus
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	return (jint)v8LocalModule->GetStatus();
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_moduleInstantiate
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (v8LocalModule->GetStatus() == v8::Module::Status::kUninstantiated) {
		return v8LocalModule->InstantiateModule(v8Context, Javet::Callback::ModuleResolveCallback).FromMaybe(false);
	}
	return false;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetState
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_PROMISE(v8ValueType)) {
		return (jint)v8LocalObject.As<v8::Promise>()->State();
	}
	return -1;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseCatch
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueFunctionHandle) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_PROMISE(v8ValueType)) {
		auto v8PersistentFunctionPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionHandle);
		auto v8LocalFunction = v8PersistentFunctionPointer->Get(v8Context->GetIsolate());
		auto maybeLocalValueResult = v8LocalObject.As<v8::Promise>()->Catch(v8Context, v8LocalFunction);
		if (!maybeLocalValueResult.IsEmpty()) {
			return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetResult
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_PROMISE(v8ValueType)) {
		auto v8LocalPromise = v8LocalObject.As<v8::Promise>();
		if (v8LocalPromise->State() != v8::Promise::PromiseState::kPending) {
			auto v8ValueResult = v8LocalPromise->Result();
			if (!v8ValueResult.IsEmpty()) {
				return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8ValueResult);
			}
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseHasHandler
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_PROMISE(v8ValueType)) {
		return v8LocalObject.As<v8::Promise>()->HasHandler();
	}
	return false;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_promiseMarkAsHandled
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_PROMISE(v8ValueType)) {
		v8LocalObject.As<v8::Promise>()->MarkAsHandled();
	}
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseThen
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueFunctionFulfilledHandle, jlong v8ValueFunctionRejectedHandle) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (IS_V8_PROMISE(v8ValueType)) {
		auto v8PersistentFunctionFulfilledPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionFulfilledHandle);
		auto v8LocalFunctionFulfilled = v8PersistentFunctionFulfilledPointer->Get(v8Context->GetIsolate());
		V8MaybeLocalPromise maybeLocalValueResult;
		if (v8ValueFunctionRejectedHandle == 0L) {
			maybeLocalValueResult = v8LocalObject.As<v8::Promise>()->Then(v8Context, v8LocalFunctionFulfilled);
		}
		else {
			auto v8PersistentFunctionRejectedPointer = TO_V8_PERSISTENT_FUNCTION_POINTER(v8ValueFunctionRejectedHandle);
			auto v8LocalFunctionRejected = v8PersistentFunctionRejectedPointer->Get(v8Context->GetIsolate());
			maybeLocalValueResult = v8LocalObject.As<v8::Promise>()->Then(v8Context, v8LocalFunctionFulfilled, v8LocalFunctionRejected);
		}
		if (!maybeLocalValueResult.IsEmpty()) {
			return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jobject mV8Runtime) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	if (v8Runtime->externalV8Runtime != nullptr) {
		jniEnv->DeleteGlobalRef(v8Runtime->externalV8Runtime);
	}
	v8Runtime->externalV8Runtime = jniEnv->NewGlobalRef(mV8Runtime);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeJNIGlobalRef
(JNIEnv* jniEnv, jclass callerClass, jlong handle) {
	jniEnv->DeleteGlobalRef((jobject)handle);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
(JNIEnv* jniEnv, jclass callerClass, jlong referenceHandle, jint referenceType) {
	auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(referenceHandle);
	v8PersistentDataPointer->Reset();
	delete v8PersistentDataPointer;
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_requestGarbageCollectionForTesting
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jboolean fullGC) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	v8Runtime->v8Isolate->RequestGarbageCollectionForTesting(fullGC
		? v8::Isolate::GarbageCollectionType::kFullGarbageCollection
		: v8::Isolate::GarbageCollectionType::kMinorGarbageCollection);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Context
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mGlobalName) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	auto v8Locker = v8Runtime->GetSharedV8Locker();
	v8Runtime->ResetV8Context(jniEnv, mGlobalName);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Isolate
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mGlobalName) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	v8Runtime->CloseV8Isolate();
	v8Runtime->CreateV8Isolate();
	auto v8Locker = v8Runtime->GetUniqueV8Locker();
	v8Runtime->ResetV8Context(jniEnv, mGlobalName);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptRun
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jboolean mResultRequired) {
	RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (!v8LocalScript.IsEmpty()) {
		V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
		auto maybeLocalValueResult = v8LocalScript->Run(v8Context);
		if (v8TryCatch.HasCaught()) {
			Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
		}
		else if (mResultRequired) {
			return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, maybeLocalValueResult.ToLocalChecked());
		}
	}
	return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime->externalV8Runtime);
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
	RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	if (!v8PersistentDataPointer->IsEmpty() && !v8PersistentDataPointer->IsWeak()) {
		auto v8ValueReference = new Javet::Callback::V8ValueReference;
		v8ValueReference->v8Isolate = v8Context->GetIsolate();
		v8ValueReference->objectReference = jniEnv->NewGlobalRef(objectReference);
		v8ValueReference->v8PersistentDataPointer = v8PersistentDataPointer;
		v8PersistentDataPointer->SetWeak(v8ValueReference, Javet::Main::CloseWeakDataReference, v8::WeakCallbackType::kParameter);
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
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	v8Runtime->v8Isolate->TerminateExecution();
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toProtoString
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
	V8LocalString v8LocalString;
	if (v8LocalObject->IsObject()) {
		v8LocalString = v8LocalObject->ObjectProtoToString(v8Context).ToLocalChecked();
	}
	return Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalString);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toString
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
	if (IS_V8_MODULE(v8ValueType) || IS_V8_SCRIPT(v8ValueType)) {
		return nullptr;
	}
	else {
		RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
		V8LocalString v8LocalString;
		if (IS_V8_ARRAY(v8ValueType)) {
			v8LocalString = v8LocalObject.As<v8::Array>()->ToString(v8Context).ToLocalChecked();
		}
		else if (IS_V8_MAP(v8ValueType)) {
			v8LocalString = v8LocalObject.As<v8::Map>()->ToString(v8Context).ToLocalChecked();
		}
		else if (IS_V8_SET(v8ValueType)) {
			v8LocalString = v8LocalObject.As<v8::Set>()->ToString(v8Context).ToLocalChecked();
		}
		else if (v8LocalObject->IsObject()) {
			v8LocalString = v8LocalObject->ToString(v8Context).ToLocalChecked();
		}
		return Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalString);
	}
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle) {
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
	if (!v8Runtime->IsLocked()) {
		Javet::Exceptions::ThrowJavetV8LockConflictException(jniEnv, "Cannot release V8 native lock because it has not been acquired yet");
	}
	else {
		v8Runtime->Unlock();
	}
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorSend
(JNIEnv* jniEnv, jclass callerClass, jlong v8RuntimeHandle, jstring mMessage) {
	RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
	char const* umMessage = jniEnv->GetStringUTFChars(mMessage, nullptr);
	std::string message(umMessage, jniEnv->GetStringUTFLength(mMessage));
	v8Runtime->v8Inspector->send(message);
	jniEnv->ReleaseStringUTFChars(mMessage, umMessage);
}

