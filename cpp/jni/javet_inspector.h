/*
 *   Copyright (c) 2021-2026. caoccao.com Sam Cao
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

#include <atomic>
#include <condition_variable>
#include <jni.h>
#include <map>
#include <mutex>
#include <queue>
#include "javet_v8_runtime.h"
#include "javet_v8.h"

namespace Javet {
    namespace Inspector {
        class JavetInspector;
        class JavetInspectorClient;
        class JavetInspectorChannel;
        class JavetInspectorSession;

        static jclass jclassV8Inspector;
        static jmethodID jmethodIDV8InspectorConsoleAPIMessage;
        static jmethodID jmethodIDV8InspectorFlushProtocolNotifications;
        static jmethodID jmethodIDV8InspectorInstallAdditionalCommandLineAPI;
        static jmethodID jmethodIDV8InspectorReceiveNotification;
        static jmethodID jmethodIDV8InspectorReceiveResponse;
        static jmethodID jmethodIDV8InspectorRunIfWaitingForDebugger;

        void Initialize(JNIEnv* jniEnv) noexcept;

        class JavetInspector {
        public:
            JavetInspector(V8Runtime* v8Runtime, const std::string& name) noexcept;
            int addSession(const jobject mV8Inspector, bool waitForDebugger) noexcept;
            void breakProgram(int sessionId, const std::string& breakReason, const std::string& breakDetails) noexcept;
            void cancelPauseOnNextStatement(int sessionId) noexcept;
            void contextCreated() noexcept;
            void contextDestroyed() noexcept;
            void drainQueue() noexcept;
            jobject evaluate(JNIEnv* jniEnv, int sessionId, const std::string& expression, bool includeCommandLineAPI) noexcept;
            void idleFinished() noexcept;
            void idleStarted() noexcept;
            bool isMessageLoopActive() const noexcept;
            bool isPaused() const noexcept;
            bool isWaitingForDebugger() const noexcept;
            void postMessage(int sessionId, const std::string& message) noexcept;
            void removeSession(int sessionId) noexcept;
            void schedulePauseOnNextStatement(int sessionId, const std::string& breakReason, const std::string& breakDetails) noexcept;
            void setSkipAllPauses(int sessionId, bool skip) noexcept;
            void waitForDebugger() noexcept;
            virtual ~JavetInspector();
        private:
            V8Runtime* v8Runtime;
            std::unique_ptr<JavetInspectorClient> client;
        };

        class JavetInspectorClient final : public v8_inspector::V8InspectorClient {
        public:
            JavetInspectorClient(
                V8Runtime* v8Runtime,
                const std::string& name) noexcept;
            int addSession(const jobject mV8Inspector, bool waitForDebugger) noexcept;
            void breakProgram(int sessionId, const std::string& breakReason, const std::string& breakDetails) noexcept;
            void cancelPauseOnNextStatement(int sessionId) noexcept;
            void consoleAPIMessage(
                int contextGroupId,
                v8::Isolate::MessageErrorLevel level,
                const v8_inspector::StringView& message,
                const v8_inspector::StringView& url,
                unsigned lineNumber,
                unsigned columnNumber,
                v8_inspector::V8StackTrace*) override;
            void contextCreated(const V8LocalContext& v8Context) noexcept;
            void contextDestroyed(const V8LocalContext& v8Context) noexcept;
            void drainQueue() noexcept;
            jobject evaluate(JNIEnv* jniEnv, int sessionId, const std::string& expression, bool includeCommandLineAPI) noexcept;
            void idleFinished() noexcept;
            void idleStarted() noexcept;
            void installAdditionalCommandLineAPI(v8::Local<v8::Context>, v8::Local<v8::Object>) override;
            bool isRunningMessageLoop() const noexcept;
            bool isWaitingForDebugger() const noexcept;
            void postMessage(int sessionId, const std::string& message) noexcept;
            void quitMessageLoopOnPause() override;
            void removeSession(int sessionId) noexcept;
            std::unique_ptr<v8_inspector::StringBuffer> resourceNameToUrl(
                const v8_inspector::StringView& resourceName) override;
            void runIfWaitingForDebugger(int contextGroupId) override;
            void runMessageLoopOnPause(int contextGroupId) override;
            void schedulePauseOnNextStatement(int sessionId, const std::string& breakReason, const std::string& breakDetails) noexcept;
            void setSkipAllPauses(int sessionId, bool skip) noexcept;
            void waitForDebuggerLoop() noexcept;
            virtual ~JavetInspectorClient() = default;
        private:
            V8Runtime* v8Runtime;
            bool activateMessageLoop;
            std::atomic<bool> runningMessageLoop;
            std::atomic<bool> waitingForDebugger;
            std::string name;
            std::condition_variable messageCondition;
            std::mutex messageMutex;
            std::map<int, std::unique_ptr<JavetInspectorSession>> sessionMap;
            int nextSessionId;
            std::unique_ptr<v8_inspector::V8Inspector> v8Inspector;
            V8LocalContext ensureDefaultContextInGroup(int contextGroupId) override;
        };

        class JavetInspectorSession {
        public:
            JavetInspectorSession(
                int sessionId,
                V8Runtime* v8Runtime,
                const jobject mV8Inspector,
                v8_inspector::V8Inspector* v8Inspector,
                bool waitForDebugger,
                std::mutex& sharedMutex) noexcept;
            int getSessionId() const noexcept;
            jobject getJavaObject() const noexcept;
            void breakProgram(const std::string& breakReason, const std::string& breakDetails) noexcept;
            void cancelPauseOnNextStatement() noexcept;
            void drainQueue() noexcept;
            jobject evaluate(JNIEnv* jniEnv, const std::string& expression, bool includeCommandLineAPI) noexcept;
            bool hasQueuedMessages() const noexcept;
            void postMessage(const std::string& message) noexcept;
            void schedulePauseOnNextStatement(const std::string& breakReason, const std::string& breakDetails) noexcept;
            void setSkipAllPauses(bool skip) noexcept;
            void stop() noexcept;
            ~JavetInspectorSession();
        private:
            int sessionId;
            V8Runtime* v8Runtime;
            jobject mV8Inspector;
            std::unique_ptr<JavetInspectorChannel> channel;
#ifdef ENABLE_NODE
            std::unique_ptr<v8_inspector::V8InspectorSession> v8InspectorSession;
#else
            std::shared_ptr<v8_inspector::V8InspectorSession> v8InspectorSession;
#endif
            std::queue<std::string> messageQueue;
            std::mutex& sharedMutex;
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
