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
#include <v8.h>
#include "javet_types.h"

namespace Javet {
	namespace Exceptions {
		static jclass jclassJavetCompilationException;
		static jmethodID jmethodIDJavetCompilationExceptionConstructor;
		static jclass jclassJavetConverterException;
		static jclass jclassJavetExecutionException;
		static jmethodID jmethodIDJavetExecutionExceptionConstructor;
		static jclass jclassJavetTerminatedException;
		static jmethodID jmethodIDJavetTerminatedExceptionConstructor;
		static jclass jclassJavetUnknownCompilationException;
		static jmethodID jmethodIDJavetUnknownCompilationExceptionConstructor;
		static jclass jclassJavetUnknownExecutionException;
		static jmethodID jmethodIDJavetUnknownExecutionExceptionConstructor;
		static jclass jclassJavetV8LockConflictException;

		void Initialize(JNIEnv* jniEnv);

		void ThrowJavetCompilationException(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch);
		void ThrowJavetConverterException(JNIEnv* jniEnv, const char* message);
		void ThrowJavetExecutionException(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch);
		void ThrowJavetV8LockConflictException(JNIEnv* jniEnv, const char* message);
	}
}
