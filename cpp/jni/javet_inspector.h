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

#pragma once

#include <jni.h>
#include "javet_v8_runtime.h"
#include "javet_v8.h"

namespace Javet {
    namespace Inspector {
        class JavetInspector;
        class JavetInspectorClient;
        class JavetInspectorChannel;

        static jclass jclassV8Inspector;
        static jmethodID jmethodIDV8InspectorFlushProtocolNotifications;
        static jmethodID jmethodIDV8InspectorGetName;
        static jmethodID jmethodIDV8InspectorReceiveNotification;
        static jmethodID jmethodIDV8InspectorReceiveResponse;
        static jmethodID jmethodIDV8InspectorRunIfWaitingForDebugger;

        void Initialize(JNIEnv* jniEnv) noexcept;

        class JavetInspector {
        public:
            JavetInspector(V8Runtime* v8Runtime, const jobject mV8Inspector) noexcept;
            void send(const std::string& message) noexcept;
            virtual ~JavetInspector();
        private:
            jobject mV8Inspector;
            V8Runtime* v8Runtime;
            std::unique_ptr<JavetInspectorClient> client;
        };

        class JavetInspectorClient final : public v8_inspector::V8InspectorClient {
        public:
            JavetInspectorClient(
                V8Runtime* v8Runtime,
                const std::string& name,
                const jobject mV8Inspector) noexcept;
            void dispatchProtocolMessage(const v8_inspector::StringView& message) noexcept;
            void quitMessageLoopOnPause() override;
            void runIfWaitingForDebugger(int contextGroupId) override;
            void runMessageLoopOnPause(int contextGroupId) override;
            virtual ~JavetInspectorClient();
        private:
            bool activateMessageLoop;
            jobject mV8Inspector;
            bool runningMessageLoop;
            V8Runtime* v8Runtime;
            std::unique_ptr<JavetInspectorChannel> javetInspectorChannel;
            std::unique_ptr<v8_inspector::V8Inspector> v8Inspector;
            std::unique_ptr<v8_inspector::V8InspectorSession> v8InspectorSession;
            V8LocalContext ensureDefaultContextInGroup(int contextGroupId) override;
        };

        class JavetInspectorChannel final : public v8_inspector::V8Inspector::Channel {
        public:
            JavetInspectorChannel(V8Runtime* v8Runtime, const jobject mV8Inspector) noexcept;
            void flushProtocolNotifications() override;
            void sendNotification(std::unique_ptr<v8_inspector::StringBuffer> message) override;
            void sendResponse(int callId, std::unique_ptr<v8_inspector::StringBuffer> message) override;
            virtual ~JavetInspectorChannel();
        private:
            jobject mV8Inspector;
            V8Runtime* v8Runtime;
        };
    }
}
