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

#include <v8.h>
#include <v8-inspector.h>
#include <jni.h>

namespace Javet {
	namespace Inspector {
		class JavetInspector;
		class JavetInspectorClient;
		class JavetInspectorChannel;

		static JavaVM* GlobalJavaVM;

		static jclass jclassV8Inspector;
		static jmethodID jmethodIDV8InspectorGetName;
		static jmethodID jmethodIDV8InspectorReceiveNotification;
		static jmethodID jmethodIDV8InspectorReceiveResponse;

		void Initialize(JNIEnv* jniEnv, JavaVM* javaVM);

		class JavetInspector {
		public:
			JavetInspector(JNIEnv* jniEnv, v8::Local<v8::Context> v8Context, const jobject& mV8Inspector);
			void reset(JNIEnv* jniEnv);
			void send(const std::string& message);
			virtual ~JavetInspector();
		private:
			jobject mV8Inspector;
			std::unique_ptr<JavetInspectorClient> client;
		};

		class JavetInspectorClient final : public v8_inspector::V8InspectorClient {
		public:
			JavetInspectorClient(v8::Local<v8::Context> v8Context, const std::string& name, const jobject& mV8Inspector);
			void dispatchProtocolMessage(const v8_inspector::StringView& message);
			void quitMessageLoopOnPause() override;
			void runIfWaitingForDebugger(int contextGroupId) override;
			void runMessageLoopOnPause(int contextGroupId) override;
			virtual ~JavetInspectorClient();
		private:
			bool activateMessageLoop;
			bool runningMessageLoop;
			v8::Isolate* v8Isolate;
			v8::Local<v8::Context> v8Context;
			std::unique_ptr<JavetInspectorChannel> javetInspectorChannel;
			std::unique_ptr<v8_inspector::V8Inspector> v8Inspector;
			std::unique_ptr<v8_inspector::V8InspectorSession> v8InspectorSession;
			v8::Local<v8::Context> ensureDefaultContextInGroup(int contextGroupId) override;
		};

		class JavetInspectorChannel final : public v8_inspector::V8Inspector::Channel {
		public:
			JavetInspectorChannel(v8::Isolate* v8Isolate, const jobject& mV8Inspector);
			void flushProtocolNotifications() override;
			void sendNotification(std::unique_ptr<v8_inspector::StringBuffer> message) override;
			void sendResponse(int callId, std::unique_ptr<v8_inspector::StringBuffer> message) override;
			virtual ~JavetInspectorChannel();
		private:
			jobject mV8Inspector;
			v8::Isolate* v8Isolate;
		};
	}
}
