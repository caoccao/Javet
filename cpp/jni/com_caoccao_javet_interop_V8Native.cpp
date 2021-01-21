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
#include "javet_globals.h"
#include "javet_v8_runtime.h"

// Namespace is not recommended in this project.

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

	return JNI_VERSION_1_8;
}

JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createRuntime
(JNIEnv* jniEnv, jclass managedObject, jstring managedGlobalName) {
	Javet::V8Runtime* v8Runtime = new Javet::V8Runtime();
	v8::Isolate::CreateParams create_params;
	create_params.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();
	v8Runtime->v8Isolate = v8::Isolate::New(create_params);
	v8Runtime->v8Locker = new v8::Locker(v8Runtime->v8Isolate);
	v8::Isolate::Scope isolate_scope(v8Runtime->v8Isolate);
	v8Runtime->managedObject = jniEnv->NewGlobalRef(managedObject);
	v8Runtime->managedException = nullptr;
	v8::HandleScope handleScope(v8Runtime->v8Isolate);
	v8::Handle<v8::ObjectTemplate> v8IsolateHandle = v8::ObjectTemplate::New(v8Runtime->v8Isolate);
	if (managedGlobalName != nullptr) {
		v8::Local<v8::String> unmanagedGlobalName = Javet::Converter::toV8String(jniEnv, v8Runtime->v8Isolate, managedGlobalName);
		v8IsolateHandle->SetAccessor(unmanagedGlobalName, Javet::Callback::PropertyAccessorCallback);
	}
	v8::Handle<v8::Context> v8Context = v8::Context::New(v8Runtime->v8Isolate, nullptr, v8IsolateHandle);
	v8Runtime->v8Context.Reset(v8Runtime->v8Isolate, v8Context);
	v8Runtime->v8GlobalObject = new v8::Persistent<v8::Object>;
	v8Runtime->v8GlobalObject->Reset(v8Runtime->v8Isolate, v8Context->Global()->GetPrototype()->ToObject(v8Context).ToLocalChecked());
	return reinterpret_cast<jlong>(v8Runtime);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
(JNIEnv* jniEnv, jclass) {
	const char* utfString = v8::V8::GetVersion();
	return jniEnv->NewStringUTF(utfString);
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

