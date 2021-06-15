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

#include "javet_callbacks.h"
#include "javet_constants.h"
#include "javet_converter.h"
#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_logging.h"
#include "javet_native.h"

JavaVM* GlobalJavaVM;

jint JNI_OnLoad(JavaVM* javaVM, void* reserved) {
	LOG_INFO("JNI_Onload() begins.");
	JNIEnv* jniEnv;
	if (javaVM->GetEnv((void**)&jniEnv, JNI_VERSION_1_8) != JNI_OK) {
		LOG_ERROR("Failed to call JavaVM.GetEnv().");
		return ERROR_JNI_ON_LOAD;
	}
	if (jniEnv == nullptr) {
		LOG_ERROR("Failed to get JNIEnv.");
		return ERROR_JNI_ON_LOAD;
	}
	GlobalJavaVM = javaVM;
	Javet::V8Native::Initialize(jniEnv);
#ifdef ENABLE_NODE
	Javet::NodeNative::Initialize(jniEnv);
#endif
	Javet::Callback::Initialize(jniEnv);
	Javet::Converter::Initialize(jniEnv);
	Javet::Exceptions::Initialize(jniEnv);
	Javet::Inspector::Initialize(jniEnv);
	LOG_INFO("JNI_Onload() ends.");
	return JNI_VERSION_1_8;
}

void JNI_OnUnload(JavaVM* javaVM, void* reserved) {
	LOG_INFO("JNI_OnUnload() begins.");
	JNIEnv* jniEnv;
	if (javaVM->GetEnv((void**)&jniEnv, JNI_VERSION_1_8) != JNI_OK) {
		LOG_ERROR("Failed to call JavaVM.GetEnv().");
	}
	if (jniEnv == nullptr) {
		LOG_ERROR("Failed to get JNIEnv.");
	}
	else {
#ifdef ENABLE_NODE
		Javet::NodeNative::Dispose(jniEnv);
#endif
		Javet::V8Native::Dispose(jniEnv);
	}
	LOG_INFO("JNI_OnUnload() ends.");
}

