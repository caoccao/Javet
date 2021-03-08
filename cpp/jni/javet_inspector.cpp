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

#include "javet_globals.h"
#include "javet_inspector.h"
#include "javet_logging.h"

#define CONTEXT_GROUP_ID 1
#define EMBEDDER_DATA_INDEX 1

#define CONVERT_FROM_STD_STRING_TO_STRING_VIEW(stdString, stringView) v8_inspector::StringView stringView(reinterpret_cast<const uint8_t*>(stdString.c_str()), stdString.length())
#define CONVERT_FROM_STRING_VIEW_TO_STD_STRING(stringView, stdString) \
	auto stringViewMessage = stringView->string(); \
	int length = static_cast<int>(stringViewMessage.length()); \
	v8::Local<v8::String> v8StringMessage; \
	if (length > 0) { \
		if (stringViewMessage.is8Bit()) { \
			v8StringMessage = v8::String::NewFromOneByte(v8Isolate, reinterpret_cast<const uint8_t*>( \
				stringViewMessage.characters8()), v8::NewStringType::kNormal, length).ToLocalChecked(); \
		} \
		else { \
			v8StringMessage = v8::String::NewFromTwoByte(v8Isolate, reinterpret_cast<const uint16_t*>( \
				stringViewMessage.characters16()), v8::NewStringType::kNormal, length).ToLocalChecked(); \
		} \
	} \
	v8::String::Utf8Value v8Utf8Value(v8Isolate, v8StringMessage); \
	std::string stdString(*v8Utf8Value);

namespace Javet {
	namespace Inspector {
		void Initialize(JNIEnv* jniEnv, JavaVM* javaVM) {
			GlobalJavaVM = javaVM;

			jclassV8Inspector = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Inspector"));
			jmethodIDV8InspectorGetName = jniEnv->GetMethodID(jclassV8Inspector, "getName", "()Ljava/lang/String;");
			jmethodIDV8InspectorReceiveNotification = jniEnv->GetMethodID(jclassV8Inspector, "receiveNotification", "(Ljava/lang/String;)Z");
			jmethodIDV8InspectorReceiveResponse = jniEnv->GetMethodID(jclassV8Inspector, "receiveResponse", "(Ljava/lang/String;)Z");
		}

		JavetInspector::JavetInspector(Javet::V8Runtime* v8Runtime, const jobject& mV8Inspector) {
			FETCH_JNI_ENV(GlobalJavaVM);
			this->mV8Inspector = jniEnv->NewGlobalRef(mV8Inspector);
			this->v8Runtime = v8Runtime;
			jstring mName = (jstring)jniEnv->CallObjectMethod(this->mV8Inspector, jmethodIDV8InspectorGetName);
			char const* umName = jniEnv->GetStringUTFChars(mName, nullptr);
			std::string name(umName, jniEnv->GetStringUTFLength(mName));
			client.reset(new JavetInspectorClient(v8Runtime, name, this->mV8Inspector));
			jniEnv->ReleaseStringUTFChars(mName, umName);
		}

		void JavetInspector::send(const std::string& message) {
			DEBUG("Sending request: " << message);
			CONVERT_FROM_STD_STRING_TO_STRING_VIEW(message, stringViewMessage);
			client->dispatchProtocolMessage(stringViewMessage);
		}

		JavetInspector::~JavetInspector() {
			if (mV8Inspector != nullptr) {
				FETCH_JNI_ENV(GlobalJavaVM);
				jniEnv->DeleteGlobalRef(mV8Inspector);
				mV8Inspector = nullptr;
			}
		}

		JavetInspectorClient::JavetInspectorClient(Javet::V8Runtime* v8Runtime, const std::string& name, const jobject& mV8Inspector) {
			activateMessageLoop = false;
			runningMessageLoop = false;
			this->v8Runtime = v8Runtime;
			auto v8Context = v8::Local<v8::Context>::New(v8Runtime->v8Isolate, v8Runtime->v8Context);
			javetInspectorChannel.reset(new JavetInspectorChannel(v8Runtime, mV8Inspector));
			v8Inspector = v8_inspector::V8Inspector::create(v8Runtime->v8Isolate, this);
			v8InspectorSession = v8Inspector->connect(CONTEXT_GROUP_ID, javetInspectorChannel.get(), v8_inspector::StringView());
			v8Context->SetAlignedPointerInEmbedderData(EMBEDDER_DATA_INDEX, this);
			CONVERT_FROM_STD_STRING_TO_STRING_VIEW(name, humanReadableName);
			v8Inspector->contextCreated(v8_inspector::V8ContextInfo(v8Context, CONTEXT_GROUP_ID, humanReadableName));
		}

		void JavetInspectorClient::dispatchProtocolMessage(const v8_inspector::StringView& message) {
			v8InspectorSession->dispatchProtocolMessage(message);
		}

		void JavetInspectorClient::quitMessageLoopOnPause() {
			activateMessageLoop = false;
		}

		void JavetInspectorClient::runIfWaitingForDebugger(int contextGroupId) {
			// TODO
		}

		void JavetInspectorClient::runMessageLoopOnPause(int contextGroupId) {
			if (!runningMessageLoop) {
				activateMessageLoop = true;
				runningMessageLoop = true;
				while (activateMessageLoop) {
					while (v8::platform::PumpMessageLoop(Javet::GlobalV8Platform.get(), v8Runtime->v8Isolate)) {
					}
				}
				runningMessageLoop = false;
				activateMessageLoop = false;
			}
		}

		v8::Local<v8::Context> JavetInspectorClient::ensureDefaultContextInGroup(int contextGroupId) {
			return v8::Local<v8::Context>::New(v8Runtime->v8Isolate, v8Runtime->v8Context);
		}

		JavetInspectorClient::~JavetInspectorClient() {
		}

		JavetInspectorChannel::JavetInspectorChannel(Javet::V8Runtime* v8Runtime, const jobject& mV8Inspector) {
			this->mV8Inspector = mV8Inspector;
			this->v8Runtime = v8Runtime;
		}

		void JavetInspectorChannel::flushProtocolNotifications() {
			// Do nothing.
		}

		void JavetInspectorChannel::sendNotification(std::unique_ptr<v8_inspector::StringBuffer> message) {
			auto v8Isolate = v8Runtime->v8Isolate;
			v8::Locker v8Locker(v8Isolate);
			v8::HandleScope v8HandleScope(v8Isolate);
			CONVERT_FROM_STRING_VIEW_TO_STD_STRING(message, stdStringMessage);
			DEBUG("Sending notification: " << stdStringMessage);
			FETCH_JNI_ENV(GlobalJavaVM);
			jstring jMessage = jniEnv->NewStringUTF(stdStringMessage.c_str());
			jniEnv->CallBooleanMethod(mV8Inspector, jmethodIDV8InspectorReceiveNotification, jMessage);
			jniEnv->DeleteLocalRef(jMessage);
		}

		void JavetInspectorChannel::sendResponse(int callId, std::unique_ptr<v8_inspector::StringBuffer> message) {
			auto v8Isolate = v8Runtime->v8Isolate;
			v8::Locker v8Locker(v8Isolate);
			v8::HandleScope v8HandleScope(v8Isolate);
			CONVERT_FROM_STRING_VIEW_TO_STD_STRING(message, stdStringMessage);
			DEBUG("Sending response: " << stdStringMessage);
			FETCH_JNI_ENV(GlobalJavaVM);
			jstring jMessage = jniEnv->NewStringUTF(stdStringMessage.c_str());
			jboolean booleanObject = jniEnv->CallBooleanMethod(mV8Inspector, jmethodIDV8InspectorReceiveResponse, jMessage);
			jniEnv->DeleteLocalRef(jMessage);
		}

		JavetInspectorChannel::~JavetInspectorChannel() {
		}
	}
}

