/*
 *   Copyright (c) 2021-2022 caoccao.com Sam Cao
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
#include "javet_logging.h"
#include "javet_v8.h"
#include "javet_v8_internal.h"
#include "javet_v8_runtime.h"

namespace Javet {
    namespace Exceptions {
        static jclass jclassJavetCompilationException;
        static jmethodID jmethodIDJavetCompilationExceptionConstructor;

        static jclass jclassJavetConverterException;

        static jclass jclassJavetExecutionException;
        static jmethodID jmethodIDJavetExecutionExceptionConstructor;

        static jclass jclassJavetOutOfMemoryException;
        static jmethodID jmethodIDJavetOutOfMemoryExceptionConstructor;

        static jclass jclassJavetTerminatedException;
        static jmethodID jmethodIDJavetTerminatedExceptionConstructor;

        static jclass jclassThrowable;
        static jmethodID jmethodIDThrowableGetMessage;

        void Initialize(JNIEnv* jniEnv);

        static inline void ClearJNIException(JNIEnv* jniEnv) {
            if (jniEnv->ExceptionCheck()) {
                jniEnv->ExceptionClear();
                LOG_DEBUG("Cleared JNI exception.");
            }
        }

        bool HandlePendingException(JNIEnv* jniEnv, V8Runtime* v8Runtime, const V8LocalContext& v8Context, const char* message = nullptr);
        jobject ThrowJavetCompilationException(JNIEnv* jniEnv, V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch);
        jobject ThrowJavetConverterException(JNIEnv* jniEnv, const char* message);
        jobject ThrowJavetExecutionException(JNIEnv* jniEnv, V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch);
        jobject ThrowJavetOutOfMemoryException(JNIEnv* jniEnv, v8::Isolate* v8Isolate, const char* message);
        jobject ThrowJavetTerminatedException(JNIEnv* jniEnv, bool canContinue);
        void ThrowV8Exception(JNIEnv* jniEnv, const V8LocalContext& v8Context, const char* defaultMessage);
    }
}
