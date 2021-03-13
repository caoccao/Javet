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
#include <libplatform/libplatform.h>
#include "javet_types.h"

#define FETCH_JNI_ENV(javaVMPointer) \
	JNIEnv* jniEnv; \
	javaVMPointer->GetEnv((void**)&jniEnv, JNI_VERSION_1_8); \
	javaVMPointer->AttachCurrentThread((void**)&jniEnv, nullptr);

namespace Javet {
	static std::unique_ptr<V8Platform> GlobalV8Platform = nullptr;
}

