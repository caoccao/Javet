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

namespace Javet {
    namespace Exceptions {
        void Initialize(JNIEnv* jniEnv) {
            /*
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
            */

            jclassJavetCompilationException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetCompilationException"));
            jmethodIDJavetCompilationExceptionConstructor = jniEnv->GetMethodID(jclassJavetCompilationException, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIII)V");

            jclassJavetConverterException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetConverterException"));

            jclassJavetExecutionException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetExecutionException"));
            jmethodIDJavetExecutionExceptionConstructor = jniEnv->GetMethodID(jclassJavetExecutionException, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIII)V");

            jclassJavetOutOfMemoryException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetOutOfMemoryException"));

            jclassJavetTerminatedException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetTerminatedException"));
            jmethodIDJavetTerminatedExceptionConstructor = jniEnv->GetMethodID(jclassJavetTerminatedException, "<init>", "(Z)V");

            jclassThrowable = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("java/lang/Throwable"));
            jmethodIDThrowableGetMessage = jniEnv->GetMethodID(jclassThrowable, "getMessage", "()Ljava/lang/String;");
        }

        void ThrowJavetCompilationException(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch) {
            LOG_ERROR("Compilation exception.");
            auto isolate = v8Context->GetIsolate();
            jstring jStringExceptionMessage = Javet::Converter::ToJavaString(jniEnv, v8Context, v8TryCatch.Exception());
            jstring jStringScriptResourceName = nullptr, jStringSourceLine = nullptr;
            int lineNumber = 0, startColumn = 0, endColumn = 0, startPosition = 0, endPosition = 0;
            auto v8LocalMessage = v8TryCatch.Message();
            if (!v8LocalMessage.IsEmpty()) {
                jStringScriptResourceName = Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalMessage->GetScriptResourceName());
                jStringSourceLine = Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalMessage->GetSourceLine(v8Context).ToLocalChecked());
                lineNumber = v8LocalMessage->GetLineNumber(v8Context).FromMaybe(0);
                startColumn = v8LocalMessage->GetStartColumn();
                endColumn = v8LocalMessage->GetEndColumn();
                startPosition = v8LocalMessage->GetStartPosition();
                endPosition = v8LocalMessage->GetEndPosition();
            }
            jthrowable javetConverterException = (jthrowable)jniEnv->NewObject(
                jclassJavetCompilationException,
                jmethodIDJavetCompilationExceptionConstructor,
                jStringExceptionMessage, jStringScriptResourceName, jStringSourceLine,
                lineNumber, startColumn, endColumn, startPosition, endPosition);
            jniEnv->Throw(javetConverterException);
            if (jStringSourceLine != nullptr) {
                jniEnv->DeleteLocalRef(jStringSourceLine);
            }
            if (jStringScriptResourceName != nullptr) {
                jniEnv->DeleteLocalRef(jStringScriptResourceName);
            }
            jniEnv->DeleteLocalRef(jStringExceptionMessage);
        }

        void ThrowJavetConverterException(JNIEnv* jniEnv, const char* message) {
            LOG_ERROR(*message);
            jniEnv->ThrowNew(jclassJavetConverterException, message);
        }

        void ThrowJavetExecutionException(JNIEnv* jniEnv, const V8LocalContext& v8Context, const V8TryCatch& v8TryCatch) {
            auto isolate = v8Context->GetIsolate();
            if (v8TryCatch.HasTerminated()) {
                LOG_ERROR("Execution has been terminated.");
                ThrowJavetTerminatedException(jniEnv, v8TryCatch.CanContinue());
            }
            else {
                LOG_ERROR("Execution exception.");
                jstring jStringExceptionMessage = Javet::Converter::ToJavaString(jniEnv, v8Context, v8TryCatch.Exception());
                jstring jStringScriptResourceName = nullptr, jStringSourceLine = nullptr;
                int lineNumber = 0, startColumn = 0, endColumn = 0, startPosition = 0, endPosition = 0;
                auto v8LocalMessage = v8TryCatch.Message();
                if (!v8LocalMessage.IsEmpty()) {
                    jStringScriptResourceName = Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalMessage->GetScriptResourceName());
                    jStringSourceLine = Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalMessage->GetSourceLine(v8Context).ToLocalChecked());
                    lineNumber = v8LocalMessage->GetLineNumber(v8Context).FromMaybe(0);
                    startColumn = v8LocalMessage->GetStartColumn();
                    endColumn = v8LocalMessage->GetEndColumn();
                    startPosition = v8LocalMessage->GetStartPosition();
                    endPosition = v8LocalMessage->GetEndPosition();
                }
                jthrowable javetConverterException = (jthrowable)jniEnv->NewObject(
                    jclassJavetExecutionException,
                    jmethodIDJavetExecutionExceptionConstructor,
                    jStringExceptionMessage, jStringScriptResourceName, jStringSourceLine,
                    lineNumber, startColumn, endColumn, startPosition, endPosition);
                jniEnv->Throw(javetConverterException);
                if (jStringSourceLine != nullptr) {
                    jniEnv->DeleteLocalRef(jStringSourceLine);
                }
                if (jStringScriptResourceName != nullptr) {
                    jniEnv->DeleteLocalRef(jStringScriptResourceName);
                }
                jniEnv->DeleteLocalRef(jStringExceptionMessage);
            }
        }

        void ThrowJavetOutOfMemoryException(JNIEnv* jniEnv, const char* message) {
            LOG_ERROR(*message);
            jniEnv->ThrowNew(jclassJavetOutOfMemoryException, message);
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
