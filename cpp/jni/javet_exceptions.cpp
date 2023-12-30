/*
 *   Copyright (c) 2021-2024. caoccao.com Sam Cao
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
        void Initialize(JNIEnv* jniEnv) noexcept {
            /*
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
            */

            jclassJavetCompilationException = FIND_CLASS(jniEnv, "com/caoccao/javet/exceptions/JavetCompilationException");
            jmethodIDJavetCompilationExceptionConstructor = jniEnv->GetMethodID(jclassJavetCompilationException, "<init>", "(Lcom/caoccao/javet/exceptions/JavetScriptingError;Ljava/lang/Throwable;)V");

            jclassJavetConverterException = FIND_CLASS(jniEnv, "com/caoccao/javet/exceptions/JavetConverterException");

            jclassJavetExecutionException = FIND_CLASS(jniEnv, "com/caoccao/javet/exceptions/JavetExecutionException");
            jmethodIDJavetExecutionExceptionConstructor = jniEnv->GetMethodID(jclassJavetExecutionException, "<init>", "(Lcom/caoccao/javet/exceptions/JavetScriptingError;Ljava/lang/Throwable;)V");

            jclassJavetOutOfMemoryException = FIND_CLASS(jniEnv, "com/caoccao/javet/exceptions/JavetOutOfMemoryException");
            jmethodIDJavetOutOfMemoryExceptionConstructor = jniEnv->GetMethodID(jclassJavetOutOfMemoryException, "<init>", "(Ljava/lang/String;Lcom/caoccao/javet/interop/monitoring/V8HeapStatistics;)V");

            jclassJavetTerminatedException = FIND_CLASS(jniEnv, "com/caoccao/javet/exceptions/JavetTerminatedException");
            jmethodIDJavetTerminatedExceptionConstructor = jniEnv->GetMethodID(jclassJavetTerminatedException, "<init>", "(Z)V");

            jclassThrowable = FIND_CLASS(jniEnv, "java/lang/Throwable");
            jmethodIDThrowableGetMessage = jniEnv->GetMethodID(jclassThrowable, "getMessage", "()Ljava/lang/String;");
        }

        bool HandlePendingException(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const char* message) noexcept {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            if (HAS_PENDING_EXCEPTION(v8InternalIsolate)) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                v8InternalIsolate->ReportPendingMessages();
                if (v8TryCatch.HasCaught()) {
                    ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                    return true;
                }
            }
#ifdef ENABLE_NODE
            else if (v8InternalIsolate->has_scheduled_exception()) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                v8InternalIsolate->PromoteScheduledException();
                if (v8InternalIsolate->has_pending_exception()) {
                    if (v8TryCatch.HasCaught()) {
                        ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                        return true;
                    }
                }
            }
#endif
            if (message != nullptr) {
                ThrowJavetOutOfMemoryException(jniEnv, v8Context, message);
                return true;
            }
            return false;
        }

        jobject ThrowJavetCompilationException(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8TryCatch& v8TryCatch) noexcept {
            if (v8TryCatch.HasTerminated()) {
                LOG_ERROR("Compilation has been terminated.");
                v8Runtime->ClearExternalException(jniEnv);
                return ThrowJavetTerminatedException(jniEnv, v8TryCatch.CanContinue());
            }
            else {
                LOG_ERROR("Compilation exception.");
                bool pendingException = false;
                jthrowable innerException = nullptr;
                if (jniEnv->ExceptionCheck()) {
                    LOG_ERROR("Inner exception is found.");
                    innerException = jniEnv->ExceptionOccurred();
                    jniEnv->ExceptionClear();
                    v8Runtime->ClearExternalException(jniEnv);
                }
                else {
                    if (v8Runtime->HasExternalException()) {
                        innerException = v8Runtime->externalException;
                        pendingException = true;
                    }
                }
                jobject javetScriptingError = Javet::Converter::ToJavetScriptingError(jniEnv, v8Runtime, v8Context, v8TryCatch);
                jthrowable javetCompilationException = (jthrowable)jniEnv->NewObject(
                    jclassJavetCompilationException,
                    jmethodIDJavetCompilationExceptionConstructor,
                    javetScriptingError,
                    innerException);
                jniEnv->Throw(javetCompilationException);
                jniEnv->DeleteLocalRef(javetCompilationException);
                jniEnv->DeleteLocalRef(javetScriptingError);
                if (innerException != nullptr) {
                    if (pendingException) {
                        v8Runtime->ClearExternalException(jniEnv);
                    }
                    else {
                        jniEnv->DeleteLocalRef(innerException);
                    }
                }
            }
            return nullptr;
        }

        jobject ThrowJavetExecutionException(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8TryCatch& v8TryCatch) noexcept {
            if (v8TryCatch.HasTerminated()) {
                LOG_ERROR("Execution has been terminated.");
                v8Runtime->ClearExternalException(jniEnv);
                return ThrowJavetTerminatedException(jniEnv, v8TryCatch.CanContinue());
            }
            else {
                LOG_ERROR("Execution exception.");
                bool pendingException = false;
                jthrowable innerException = nullptr;
                if (jniEnv->ExceptionCheck()) {
                    LOG_ERROR("Inner exception is found.");
                    innerException = jniEnv->ExceptionOccurred();
                    jniEnv->ExceptionClear();
                    v8Runtime->ClearExternalException(jniEnv);
                }
                else {
                    if (v8Runtime->HasExternalException()) {
                        innerException = v8Runtime->externalException;
                        pendingException = true;
                    }
                }
                jobject javetScriptingError = Javet::Converter::ToJavetScriptingError(jniEnv, v8Runtime, v8Context, v8TryCatch);
                jthrowable javetExecutionException = (jthrowable)jniEnv->NewObject(
                    jclassJavetExecutionException,
                    jmethodIDJavetExecutionExceptionConstructor,
                    javetScriptingError,
                    innerException);
                jniEnv->Throw(javetExecutionException);
                jniEnv->DeleteLocalRef(javetExecutionException);
                jniEnv->DeleteLocalRef(javetScriptingError);
                if (innerException != nullptr) {
                    if (pendingException) {
                        v8Runtime->ClearExternalException(jniEnv);
                    }
                    else {
                        jniEnv->DeleteLocalRef(innerException);
                    }
                }
            }
            return nullptr;
        }

        jobject ThrowJavetOutOfMemoryException(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const char* message) noexcept {
            LOG_ERROR(*message);
            jstring jStringExceptionMessage = Javet::Converter::ToJavaString(jniEnv, message);
            jobject jObjectHeapStatistics = Javet::Monitor::GetHeapStatistics(jniEnv, v8Context->GetIsolate());
            jthrowable javetOutOfMemoryException = (jthrowable)jniEnv->NewObject(
                jclassJavetOutOfMemoryException,
                jmethodIDJavetOutOfMemoryExceptionConstructor,
                jStringExceptionMessage,
                jObjectHeapStatistics);
            jniEnv->DeleteLocalRef(jStringExceptionMessage);
            jniEnv->DeleteLocalRef(jObjectHeapStatistics);
            jniEnv->Throw(javetOutOfMemoryException);
            return nullptr;
        }

        jobject ThrowJavetTerminatedException(
            JNIEnv* jniEnv,
            const bool canContinue) noexcept {
            jthrowable javetTerminatedException = (jthrowable)jniEnv->NewObject(
                jclassJavetTerminatedException,
                jmethodIDJavetTerminatedExceptionConstructor,
                canContinue);
            jniEnv->Throw(javetTerminatedException);
            return nullptr;
        }

        void ThrowV8Exception(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const char* defaultMessage) noexcept {
            auto v8Isolate = v8Context->GetIsolate();
            auto v8Runtime = V8Runtime::FromV8Context(v8Context);
            jstring externalErrorMessage = nullptr;
            if (jniEnv->ExceptionCheck()) {
                jthrowable externalException = jniEnv->ExceptionOccurred();
                jniEnv->ExceptionClear();
                externalException = (jthrowable)jniEnv->NewGlobalRef(externalException);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
                v8Runtime->ClearExternalException(jniEnv);
                v8Runtime->externalException = externalException;
                externalErrorMessage = (jstring)jniEnv->CallObjectMethod(externalException, jmethodIDThrowableGetMessage);
            }
            V8LocalString v8ErrorMessage;
            if (externalErrorMessage == nullptr) {
                v8ErrorMessage = Javet::Converter::ToV8String(v8Context, defaultMessage);
            }
            else {
                v8ErrorMessage = Javet::Converter::ToV8String(jniEnv, v8Context, externalErrorMessage);
                jniEnv->DeleteLocalRef(externalErrorMessage);
            }
            v8Isolate->ThrowException(v8::Exception::Error(v8ErrorMessage));
        }
    }
}
