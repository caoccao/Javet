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
#include "javet_exceptions.h"
#include "javet_logging.h"
#include "javet_monitor.h"

namespace Javet {
    namespace Exceptions {
        void Initialize(JNIEnv* jniEnv) {
            /*
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
            */

            jclassJavetCompilationException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetCompilationException"));
            jmethodIDJavetCompilationExceptionConstructor = jniEnv->GetMethodID(jclassJavetCompilationException, "<init>", "(Lcom/caoccao/javet/exceptions/JavetScriptingError;)V");

            jclassJavetConverterException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetConverterException"));

            jclassJavetExecutionException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetExecutionException"));
            jmethodIDJavetExecutionExceptionConstructor = jniEnv->GetMethodID(jclassJavetExecutionException, "<init>", "(Lcom/caoccao/javet/exceptions/JavetScriptingError;)V");

            jclassJavetOutOfMemoryException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetOutOfMemoryException"));
            jmethodIDJavetOutOfMemoryExceptionConstructor = jniEnv->GetMethodID(jclassJavetOutOfMemoryException, "<init>", "(Ljava/lang/String;Lcom/caoccao/javet/interop/monitoring/V8HeapStatistics;)V");

            jclassJavetTerminatedException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetTerminatedException"));
            jmethodIDJavetTerminatedExceptionConstructor = jniEnv->GetMethodID(jclassJavetTerminatedException, "<init>", "(Z)V");

            jclassThrowable = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("java/lang/Throwable"));
            jmethodIDThrowableGetMessage = jniEnv->GetMethodID(jclassThrowable, "getMessage", "()Ljava/lang/String;");
        }

        bool HandlePendingException(JNIEnv* jniEnv, const V8LocalContext& v8Context) {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            if (v8InternalIsolate->has_pending_exception()) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                v8InternalIsolate->ReportPendingMessages();
                if (v8TryCatch.HasCaught()) {
                    ThrowJavetExecutionException(jniEnv, v8Context, v8TryCatch);
                    return true;
                }
            }
            return false;
        }

        void ThrowJavetCompilationException(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch) {
            if (v8TryCatch.HasTerminated()) {
                LOG_ERROR("Compilation has been terminated.");
                ThrowJavetTerminatedException(jniEnv, v8TryCatch.CanContinue());
            }
            else {
                LOG_ERROR("Compilation exception.");
                jobject javetScriptingError = Javet::Converter::ToJavetScriptingError(jniEnv, v8Context, v8TryCatch);
                jthrowable javetCompilationException = (jthrowable)jniEnv->NewObject(
                    jclassJavetCompilationException,
                    jmethodIDJavetCompilationExceptionConstructor,
                    javetScriptingError);
                jniEnv->Throw(javetCompilationException);
                jniEnv->DeleteLocalRef(javetCompilationException);
                jniEnv->DeleteLocalRef(javetScriptingError);
            }
        }

        void ThrowJavetConverterException(JNIEnv* jniEnv, const char* message) {
            LOG_ERROR(*message);
            jniEnv->ThrowNew(jclassJavetConverterException, message);
        }

        void ThrowJavetExecutionException(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch) {
            if (v8TryCatch.HasTerminated()) {
                LOG_ERROR("Execution has been terminated.");
                ThrowJavetTerminatedException(jniEnv, v8TryCatch.CanContinue());
            }
            else {
                LOG_ERROR("Execution exception.");
                jobject javetScriptingError = Javet::Converter::ToJavetScriptingError(jniEnv, v8Context, v8TryCatch);
                jthrowable javetExecutionException = (jthrowable)jniEnv->NewObject(
                    jclassJavetExecutionException,
                    jmethodIDJavetExecutionExceptionConstructor,
                    javetScriptingError);
                jniEnv->Throw(javetExecutionException);
                jniEnv->DeleteLocalRef(javetExecutionException);
            }
        }

        void ThrowJavetOutOfMemoryException(JNIEnv* jniEnv, v8::Isolate* v8Isolate, const char* message) {
            LOG_ERROR(*message);
            jstring jStringExceptionMessage = Javet::Converter::ToJavaString(jniEnv, message);
            jobject jObjectHeapStatistics = Javet::Monitor::GetHeapStatistics(jniEnv, v8Isolate);
            jthrowable javetOutOfMemoryException = (jthrowable)jniEnv->NewObject(
                jclassJavetOutOfMemoryException,
                jmethodIDJavetOutOfMemoryExceptionConstructor,
                jStringExceptionMessage,
                jObjectHeapStatistics);
            jniEnv->DeleteLocalRef(jStringExceptionMessage);
            jniEnv->DeleteLocalRef(jObjectHeapStatistics);
            jniEnv->Throw(javetOutOfMemoryException);
        }

        void ThrowJavetTerminatedException(JNIEnv* jniEnv, bool canContinue) {
            jthrowable javetTerminatedException = (jthrowable)jniEnv->NewObject(
                jclassJavetTerminatedException,
                jmethodIDJavetTerminatedExceptionConstructor,
                canContinue);
            jniEnv->Throw(javetTerminatedException);
        }

        void ThrowV8Exception(JNIEnv* jniEnv, const V8LocalContext& v8Context, const char* defaultMessage) {
            auto v8Isolate = v8Context->GetIsolate();
            jthrowable externalException = jniEnv->ExceptionOccurred();
            jstring externalErrorMessage = (jstring)jniEnv->CallObjectMethod(externalException, jmethodIDThrowableGetMessage);
            jniEnv->ExceptionClear();
            V8LocalString v8ErrorMessage;
            if (externalErrorMessage == nullptr) {
                v8ErrorMessage = v8::String::NewFromUtf8(v8Isolate, defaultMessage).ToLocalChecked();
            }
            else {
                v8ErrorMessage = Javet::Converter::ToV8String(jniEnv, v8Context, externalErrorMessage);
                jniEnv->DeleteLocalRef(externalErrorMessage);
            }
            v8Isolate->ThrowException(v8::Exception::Error(v8ErrorMessage));
            jniEnv->DeleteLocalRef(externalException);
        }
    }
}
