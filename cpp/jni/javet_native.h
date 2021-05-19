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
#include "javet_node.h"
#include "javet_v8.h"

#define FETCH_JNI_ENV(javaVMPointer) \
	JNIEnv* jniEnv; \
	javaVMPointer->GetEnv((void**)&jniEnv, JNI_VERSION_1_8); \
	javaVMPointer->AttachCurrentThread((void**)&jniEnv, nullptr);

#define RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context);

#define RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8DataHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(v8DataHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
	auto v8LocalData = v8PersistentDataPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentModulePointer = TO_V8_PERSISTENT_MODULE_POINTER(v8ValueHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
	auto v8LocalModule = v8PersistentModulePointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentScriptPointer = TO_V8_PERSISTENT_SCRIPT_POINTER(v8ValueHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
	auto v8LocalScript = v8PersistentScriptPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentObjectPointer = TO_V8_PERSISTENT_OBJECT_POINTER(v8ValueHandle); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
	auto v8LocalObject = v8PersistentObjectPointer->Get(v8Context->GetIsolate());

#define RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2) \
	auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
	auto v8PersistentObjectPointer1 = TO_V8_PERSISTENT_OBJECT_POINTER(v8ValueHandle1); \
	auto v8PersistentObjectPointer2 = TO_V8_PERSISTENT_OBJECT_POINTER(v8ValueHandle2); \
	auto v8Locker = v8Runtime->GetSharedV8Locker(); \
	auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
	V8HandleScope v8HandleScope(v8Runtime->v8Isolate); \
	auto v8Context = v8Runtime->GetV8LocalContext(); \
	auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
	auto v8LocalObject1 = v8PersistentObjectPointer1->Get(v8Context->GetIsolate()); \
	auto v8LocalObject2 = v8PersistentObjectPointer2->Get(v8Context->GetIsolate());

extern JavaVM* GlobalJavaVM;

namespace Javet {

#ifdef ENABLE_NODE
	namespace NodeNative {
		void Dispose();
		void Initialize(JNIEnv* jniEnv);
	}
#endif

	namespace V8Native {
		void Dispose();
		void Initialize(JNIEnv* jniEnv);
	}
}
