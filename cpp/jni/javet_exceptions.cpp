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
			jclassJavetTerminatedException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetTerminatedException"));
			jmethodIDJavetTerminatedExceptionConstructor = jniEnv->GetMethodID(jclassJavetTerminatedException, "<init>", "(Z)V");
			jclassJavetUnknownCompilationException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetUnknownCompilationException"));
			jmethodIDJavetUnknownCompilationExceptionConstructor = jniEnv->GetMethodID(jclassJavetUnknownCompilationException, "<init>", "(Ljava/lang/String;)V");
			jclassJavetUnknownExecutionException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetUnknownExecutionException"));
			jmethodIDJavetUnknownExecutionExceptionConstructor = jniEnv->GetMethodID(jclassJavetUnknownExecutionException, "<init>", "(Ljava/lang/String;)V");
			jclassJavetV8RuntimeLockConflictException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetV8RuntimeLockConflictException"));
		}

		void ThrowJavetCompilationException(JNIEnv* jniEnv, const v8::Local<v8::Context>& v8Context, const v8::TryCatch& v8TryCatch) {
			auto isolate = v8Context->GetIsolate();
			v8::String::Value exceptionMessage(isolate, v8TryCatch.Exception());
			jstring jStringExceptionMessage = jniEnv->NewString(*exceptionMessage, exceptionMessage.length());
			auto v8LocalMessage = v8TryCatch.Message();
			if (v8LocalMessage.IsEmpty()) {
				jthrowable javetUnknownCompilationException = (jthrowable)jniEnv->NewObject(
					jclassJavetUnknownCompilationException,
					jmethodIDJavetUnknownCompilationExceptionConstructor,
					jStringExceptionMessage);
				jniEnv->Throw(javetUnknownCompilationException);
			}
			else {
				v8::String::Utf8Value scriptResourceName(isolate, v8LocalMessage->GetScriptResourceName());
				jstring jStringScriptResourceName = jniEnv->NewStringUTF(*scriptResourceName);
				v8::String::Value sourceLine(isolate, v8LocalMessage->GetSourceLine(v8Context).ToLocalChecked());
				jstring jStringSourceLine = jniEnv->NewString(*sourceLine, sourceLine.length());
				jthrowable javetConverterException = (jthrowable)jniEnv->NewObject(
					jclassJavetCompilationException,
					jmethodIDJavetCompilationExceptionConstructor,
					jStringExceptionMessage,
					jStringScriptResourceName,
					jStringSourceLine,
					v8LocalMessage->GetLineNumber(v8Context).FromMaybe(0),
					v8LocalMessage->GetStartColumn(),
					v8LocalMessage->GetEndColumn(),
					v8LocalMessage->GetStartPosition(),
					v8LocalMessage->GetEndPosition());
				jniEnv->Throw(javetConverterException);
				jniEnv->DeleteLocalRef(jStringSourceLine);
				jniEnv->DeleteLocalRef(jStringScriptResourceName);
			}
			jniEnv->DeleteLocalRef(jStringExceptionMessage);
		}

		void ThrowJavetConverterException(JNIEnv* jniEnv, const char* message) {
			jniEnv->ThrowNew(jclassJavetConverterException, message);
		}

		void ThrowJavetExecutionException(JNIEnv* jniEnv, const v8::Local<v8::Context>& v8Context, const v8::TryCatch& v8TryCatch) {
			auto isolate = v8Context->GetIsolate();
			if (v8TryCatch.HasTerminated()) {
				jthrowable javetTerminatedException = (jthrowable)jniEnv->NewObject(
					jclassJavetTerminatedException,
					jmethodIDJavetTerminatedExceptionConstructor,
					v8TryCatch.CanContinue());
				jniEnv->Throw(javetTerminatedException);
			}
			else {
				v8::String::Value exceptionMessage(isolate, v8TryCatch.Exception());
				jstring jStringExceptionMessage = jniEnv->NewString(*exceptionMessage, exceptionMessage.length());
				auto v8LocalMessage = v8TryCatch.Message();
				if (v8LocalMessage.IsEmpty()) {
					jthrowable javetUnknownExecutionException = (jthrowable)jniEnv->NewObject(
						jclassJavetUnknownExecutionException,
						jmethodIDJavetUnknownExecutionExceptionConstructor,
						jStringExceptionMessage);
					jniEnv->Throw(javetUnknownExecutionException);
				}
				else {
					v8::String::Utf8Value scriptResourceName(isolate, v8LocalMessage->GetScriptResourceName());
					jstring jStringScriptResourceName = jniEnv->NewStringUTF(*scriptResourceName);
					v8::String::Value sourceLine(isolate, v8LocalMessage->GetSourceLine(v8Context).ToLocalChecked());
					jstring jStringSourceLine = jniEnv->NewString(*sourceLine, sourceLine.length());
					jthrowable javetConverterException = (jthrowable)jniEnv->NewObject(
						jclassJavetExecutionException,
						jmethodIDJavetExecutionExceptionConstructor,
						jStringExceptionMessage,
						jStringScriptResourceName,
						jStringSourceLine,
						v8LocalMessage->GetLineNumber(v8Context).FromMaybe(0),
						v8LocalMessage->GetStartColumn(),
						v8LocalMessage->GetEndColumn(),
						v8LocalMessage->GetStartPosition(),
						v8LocalMessage->GetEndPosition());
					jniEnv->Throw(javetConverterException);
					jniEnv->DeleteLocalRef(jStringSourceLine);
					jniEnv->DeleteLocalRef(jStringScriptResourceName);
				}
				jniEnv->DeleteLocalRef(jStringExceptionMessage);
			}
		}

		void ThrowJavetV8RuntimeLockConflictException(JNIEnv* jniEnv, const char* message) {
			jniEnv->ThrowNew(jclassJavetV8RuntimeLockConflictException, message);
		}
	}
}
