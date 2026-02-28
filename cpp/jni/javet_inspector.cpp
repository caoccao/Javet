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

#include "javet_converter.h"
#include "javet_inspector.h"
#include "javet_logging.h"
#include "javet_native.h"

#define CONTEXT_GROUP_ID 1
#define EMBEDDER_DATA_INDEX 1

namespace Javet {
    namespace Inspector {
        static inline std::unique_ptr<std::string> ConvertFromStringViewToStdStringPointer(
            v8::Isolate* v8Isolate,
            const v8_inspector::StringView& stringView) {
            int length = static_cast<int>(stringView.length());
            V8LocalString v8StringMessage;
            if (length > 0) {
                if (stringView.is8Bit()) {
                    v8StringMessage = v8::String::NewFromOneByte(v8Isolate, reinterpret_cast<const uint8_t*>(
                        stringView.characters8()), v8::NewStringType::kNormal, length).ToLocalChecked();
                }
                else {
                    v8StringMessage = v8::String::NewFromTwoByte(v8Isolate, reinterpret_cast<const uint16_t*>(
                        stringView.characters16()), v8::NewStringType::kNormal, length).ToLocalChecked();
                }
            }
            V8StringUtf8Value v8Utf8Value(v8Isolate, v8StringMessage);
            return std::make_unique<std::string>(*v8Utf8Value);
        }

        static inline std::unique_ptr<v8_inspector::StringView> ConvertFromStdStringToStringViewPointer(
            const std::string& stdString) {
            return std::make_unique<v8_inspector::StringView>(
                reinterpret_cast<const uint8_t*>(stdString.c_str()),
                stdString.length());
        }

        static inline std::unique_ptr<std::string> ConvertFromStringBufferToStdStringPointer(
            v8::Isolate* v8Isolate,
            v8_inspector::StringBuffer* stringBuffer) {
            auto stringViewMessage = stringBuffer->string();
            int length = static_cast<int>(stringViewMessage.length());
            V8LocalString v8StringMessage;
            if (length > 0) {
                if (stringViewMessage.is8Bit()) {
                    v8StringMessage = v8::String::NewFromOneByte(v8Isolate, reinterpret_cast<const uint8_t*>(
                        stringViewMessage.characters8()), v8::NewStringType::kNormal, length).ToLocalChecked();
                }
                else {
                    v8StringMessage = v8::String::NewFromTwoByte(v8Isolate, reinterpret_cast<const uint16_t*>(
                        stringViewMessage.characters16()), v8::NewStringType::kNormal, length).ToLocalChecked();
                }
            }
            V8StringUtf8Value v8Utf8Value(v8Isolate, v8StringMessage);
            return std::make_unique<std::string>(*v8Utf8Value);
        }

        void Initialize(JNIEnv* jniEnv) noexcept {
            jclassV8Inspector = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/V8Inspector");
            jmethodIDV8InspectorConsoleAPIMessage = jniEnv->GetMethodID(jclassV8Inspector, "consoleAPIMessage", "(IILjava/lang/String;Ljava/lang/String;II)V");
            jmethodIDV8InspectorFlushProtocolNotifications = jniEnv->GetMethodID(jclassV8Inspector, "flushProtocolNotifications", "()V");
            jmethodIDV8InspectorReceiveNotification = jniEnv->GetMethodID(jclassV8Inspector, "receiveNotification", "(Ljava/lang/String;)V");
            jmethodIDV8InspectorReceiveResponse = jniEnv->GetMethodID(jclassV8Inspector, "receiveResponse", "(Ljava/lang/String;)V");
            jmethodIDV8InspectorRunIfWaitingForDebugger = jniEnv->GetMethodID(jclassV8Inspector, "runIfWaitingForDebugger", "(I)V");
        }

        JavetInspector::JavetInspector(V8Runtime* v8Runtime, const std::string& name) noexcept {
            this->v8Runtime = v8Runtime;
            client.reset(new JavetInspectorClient(v8Runtime, name));
        }

        int JavetInspector::addSession(const jobject mV8Inspector, bool waitForDebugger) noexcept {
            return client->addSession(mV8Inspector, waitForDebugger);
        }

        void JavetInspector::removeSession(int sessionId) noexcept {
            client->removeSession(sessionId);
        }

        void JavetInspector::contextCreated() noexcept {
            auto v8Context = v8Runtime->GetV8LocalContext();
            client->contextCreated(v8Context);
        }

        void JavetInspector::contextDestroyed() noexcept {
            auto v8Context = v8Runtime->GetV8LocalContext();
            client->contextDestroyed(v8Context);
        }

        void JavetInspector::drainQueue() noexcept {
            client->drainQueue();
            // Pump microtasks so that promise-based responses
            // (e.g., Runtime.evaluate with replMode/awaitPromise) are delivered immediately.
            v8Runtime->v8Isolate->PerformMicrotaskCheckpoint();
        }

        bool JavetInspector::isMessageLoopActive() const noexcept {
            return client->isRunningMessageLoop() || client->isWaitingForDebugger();
        }

        bool JavetInspector::isWaitingForDebugger() const noexcept {
            return client->isWaitingForDebugger();
        }

        bool JavetInspector::isPaused() const noexcept {
            return client->isRunningMessageLoop();
        }

        void JavetInspector::postMessage(int sessionId, const std::string& message) noexcept {
            LOG_DEBUG("Queueing request for session " << sessionId << ": " << message);
            client->postMessage(sessionId, message);
        }

        void JavetInspector::waitForDebugger() noexcept {
            client->waitForDebuggerLoop();
        }

        JavetInspector::~JavetInspector() {
            // Client destructor cleans up all sessions.
        }

        JavetInspectorClient::JavetInspectorClient(
            V8Runtime* v8Runtime,
            const std::string& name) noexcept
            : v8Inspector(nullptr) {
            activateMessageLoop = false;
            runningMessageLoop.store(false);
            waitingForDebugger.store(false);
            nextSessionId = 1;
            this->v8Runtime = v8Runtime;
            this->name = name;
            auto v8Context = v8Runtime->GetV8LocalContext();
            v8Inspector.reset(v8_inspector::V8Inspector::create(v8Runtime->v8Isolate, this).release());
            v8Context->SetAlignedPointerInEmbedderData(EMBEDDER_DATA_INDEX, this);
            auto humanReadableNamePointer = ConvertFromStdStringToStringViewPointer(name);
            v8Inspector->contextCreated(v8_inspector::V8ContextInfo(v8Context, CONTEXT_GROUP_ID, *humanReadableNamePointer.get()));
        }

        int JavetInspectorClient::addSession(const jobject mV8Inspector, bool waitForDebugger) noexcept {
            int sessionId = nextSessionId++;
            auto session = std::make_unique<JavetInspectorSession>(
                sessionId, v8Runtime, mV8Inspector, v8Inspector.get(),
                waitForDebugger, messageMutex);
            std::lock_guard<std::mutex> lock(messageMutex);
            sessionMap[sessionId] = std::move(session);
            return sessionId;
        }

        void JavetInspectorClient::removeSession(int sessionId) noexcept {
            std::lock_guard<std::mutex> lock(messageMutex);
            sessionMap.erase(sessionId);
        }

        void JavetInspectorClient::contextCreated(const V8LocalContext& v8Context) noexcept {
            v8Context->SetAlignedPointerInEmbedderData(EMBEDDER_DATA_INDEX, this);
            auto humanReadableNamePointer = ConvertFromStdStringToStringViewPointer(name);
            v8Inspector->contextCreated(v8_inspector::V8ContextInfo(v8Context, CONTEXT_GROUP_ID, *humanReadableNamePointer.get()));
        }

        void JavetInspectorClient::contextDestroyed(const V8LocalContext& v8Context) noexcept {
            v8Inspector->contextDestroyed(v8Context);
        }

        void JavetInspectorClient::consoleAPIMessage(
                int contextGroupId,
                v8::Isolate::MessageErrorLevel level,
                const v8_inspector::StringView& message,
                const v8_inspector::StringView& url,
                unsigned lineNumber,
                unsigned columnNumber,
                v8_inspector::V8StackTrace*) {
            V8HandleScope v8HandleScope(v8Runtime->v8Isolate);
            auto stdMessage = ConvertFromStringViewToStdStringPointer(v8Runtime->v8Isolate, message);
            auto stdUrl = ConvertFromStringViewToStdStringPointer(v8Runtime->v8Isolate, url);
            // Notify all sessions' Java objects.
            std::vector<jobject> javaObjects;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                for (auto& [id, session] : sessionMap) {
                    javaObjects.push_back(session->getJavaObject());
                }
            }
            FETCH_JNI_ENV(GlobalJavaVM);
            jstring jMessage = Javet::Converter::ToJavaString(jniEnv, stdMessage->c_str());
            jstring jUrl = Javet::Converter::ToJavaString(jniEnv, stdUrl->c_str());
            for (jobject jobj : javaObjects) {
                jniEnv->CallVoidMethod(jobj, jmethodIDV8InspectorConsoleAPIMessage,
                    contextGroupId, static_cast<jint>(level), jMessage, jUrl,
                    static_cast<jint>(lineNumber), static_cast<jint>(columnNumber));
            }
            jniEnv->DeleteLocalRef(jMessage);
            jniEnv->DeleteLocalRef(jUrl);
        }

        void JavetInspectorClient::drainQueue() noexcept {
            // Collect session pointers under the lock. During the pause loop
            // the V8 lock is held, so no session can be removed concurrently.
            std::vector<JavetInspectorSession*> sessionPtrs;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                for (auto& [id, session] : sessionMap) {
                    sessionPtrs.push_back(session.get());
                }
            }
            for (auto* session : sessionPtrs) {
                session->drainQueue();
            }
        }

        V8LocalContext JavetInspectorClient::ensureDefaultContextInGroup(int contextGroupId) {
            return v8Runtime->GetV8LocalContext();
        }

        bool JavetInspectorClient::isRunningMessageLoop() const noexcept {
            return runningMessageLoop.load();
        }

        bool JavetInspectorClient::isWaitingForDebugger() const noexcept {
            return waitingForDebugger.load();
        }

        void JavetInspectorClient::postMessage(int sessionId, const std::string& message) noexcept {
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                auto it = sessionMap.find(sessionId);
                if (it != sessionMap.end()) {
                    it->second->postMessage(message);
                }
            }
            messageCondition.notify_one();
        }

        void JavetInspectorClient::quitMessageLoopOnPause() {
            activateMessageLoop = false;
            messageCondition.notify_one();
        }

        void JavetInspectorClient::runIfWaitingForDebugger(int contextGroupId) {
            waitingForDebugger.store(false);
            messageCondition.notify_one();
            // Notify all sessions' Java objects.
            std::vector<jobject> javaObjects;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                for (auto& [id, session] : sessionMap) {
                    javaObjects.push_back(session->getJavaObject());
                }
            }
            FETCH_JNI_ENV(GlobalJavaVM);
            for (jobject jobj : javaObjects) {
                jniEnv->CallVoidMethod(jobj, jmethodIDV8InspectorRunIfWaitingForDebugger, contextGroupId);
            }
        }

        void JavetInspectorClient::runMessageLoopOnPause(int contextGroupId) {
            if (!runningMessageLoop) {
                activateMessageLoop = true;
                runningMessageLoop.store(true);
                while (activateMessageLoop) {
                    // Drain any queued protocol messages from the DevTools frontend.
                    drainQueue();
                    // Pump V8 platform tasks.
                    while (v8::platform::PumpMessageLoop(v8Runtime->v8PlatformPointer, v8Runtime->v8Isolate)) {
                    }
                    // Wait for new messages instead of busy-spinning.
                    {
                        std::unique_lock<std::mutex> lock(messageMutex);
                        bool anyQueued = false;
                        for (auto& [id, session] : sessionMap) {
                            if (session->hasQueuedMessages()) {
                                anyQueued = true;
                                break;
                            }
                        }
                        if (activateMessageLoop && !anyQueued) {
                            messageCondition.wait_for(lock, std::chrono::milliseconds(10));
                        }
                    }
                }
                runningMessageLoop.store(false);
            }
        }

        void JavetInspectorClient::waitForDebuggerLoop() noexcept {
            waitingForDebugger.store(true);
            while (waitingForDebugger.load()) {
                // Drain any queued protocol messages from the DevTools frontend.
                drainQueue();
                // Pump V8 platform tasks.
                while (v8::platform::PumpMessageLoop(v8Runtime->v8PlatformPointer, v8Runtime->v8Isolate)) {
                }
                // Wait for new messages instead of busy-spinning.
                {
                    std::unique_lock<std::mutex> lock(messageMutex);
                    bool anyQueued = false;
                    for (auto& [id, session] : sessionMap) {
                        if (session->hasQueuedMessages()) {
                            anyQueued = true;
                            break;
                        }
                    }
                    if (waitingForDebugger.load() && !anyQueued) {
                        messageCondition.wait_for(lock, std::chrono::milliseconds(10));
                    }
                }
            }
        }

        JavetInspectorSession::JavetInspectorSession(
            int sessionId,
            V8Runtime* v8Runtime,
            const jobject mV8Inspector,
            v8_inspector::V8Inspector* v8Inspector,
            bool waitForDebugger,
            std::mutex& sharedMutex) noexcept
            : sharedMutex(sharedMutex) {
            this->sessionId = sessionId;
            this->v8Runtime = v8Runtime;
            FETCH_JNI_ENV(GlobalJavaVM);
            this->mV8Inspector = jniEnv->NewGlobalRef(mV8Inspector);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            channel.reset(new JavetInspectorChannel(v8Runtime, this->mV8Inspector));
            auto pauseState = waitForDebugger
                ? v8_inspector::V8Inspector::kWaitingForDebugger
                : v8_inspector::V8Inspector::kNotWaitingForDebugger;
#ifdef ENABLE_NODE
            v8InspectorSession.reset(v8Inspector->connect(
                CONTEXT_GROUP_ID,
                channel.get(),
                v8_inspector::StringView(),
                v8_inspector::V8Inspector::kFullyTrusted,
                pauseState).release());
#else
            v8InspectorSession = v8Inspector->connectShared(
                CONTEXT_GROUP_ID,
                channel.get(),
                v8_inspector::StringView(),
                v8_inspector::V8Inspector::kFullyTrusted,
                pauseState);
#endif
        }

        int JavetInspectorSession::getSessionId() const noexcept {
            return sessionId;
        }

        jobject JavetInspectorSession::getJavaObject() const noexcept {
            return mV8Inspector;
        }

        void JavetInspectorSession::drainQueue() noexcept {
            std::unique_lock<std::mutex> lock(sharedMutex);
            while (!messageQueue.empty()) {
                std::string message = std::move(messageQueue.front());
                messageQueue.pop();
                lock.unlock();
                auto sv = ConvertFromStdStringToStringViewPointer(message);
                v8InspectorSession->dispatchProtocolMessage(*sv);
                lock.lock();
            }
        }

        void JavetInspectorSession::postMessage(const std::string& message) noexcept {
            // Caller already holds sharedMutex via JavetInspectorClient::postMessage.
            messageQueue.push(message);
        }

        bool JavetInspectorSession::hasQueuedMessages() const noexcept {
            // Caller must hold sharedMutex.
            return !messageQueue.empty();
        }

        JavetInspectorSession::~JavetInspectorSession() {
            // Disconnect the session first (may reference the channel).
            v8InspectorSession.reset();
            channel.reset();
            if (mV8Inspector != nullptr) {
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->DeleteGlobalRef(mV8Inspector);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                mV8Inspector = nullptr;
            }
        }

        JavetInspectorChannel::JavetInspectorChannel(V8Runtime* v8Runtime, const jobject mV8Inspector) noexcept {
            this->mV8Inspector = mV8Inspector;
            this->v8Runtime = v8Runtime;
        }

        void JavetInspectorChannel::flushProtocolNotifications() {
            FETCH_JNI_ENV(GlobalJavaVM);
            jniEnv->CallVoidMethod(mV8Inspector, jmethodIDV8InspectorFlushProtocolNotifications);
        }

        void JavetInspectorChannel::sendNotification(std::unique_ptr<v8_inspector::StringBuffer> message) {
            // The lock is not required.
            V8HandleScope v8HandleScope(v8Runtime->v8Isolate);
            auto stdStringMessagePointer = ConvertFromStringBufferToStdStringPointer(v8Runtime->v8Isolate, message.get());
            LOG_DEBUG("Sending notification: " << *stdStringMessagePointer.get());
            FETCH_JNI_ENV(GlobalJavaVM);
            jstring jMessage = Javet::Converter::ToJavaString(jniEnv, stdStringMessagePointer->c_str());
            jniEnv->CallVoidMethod(mV8Inspector, jmethodIDV8InspectorReceiveNotification, jMessage);
            jniEnv->DeleteLocalRef(jMessage);
        }

        void JavetInspectorChannel::sendResponse(int callId, std::unique_ptr<v8_inspector::StringBuffer> message) {
            // The lock is not required.
            V8HandleScope v8HandleScope(v8Runtime->v8Isolate);
            auto stdStringMessagePointer = ConvertFromStringBufferToStdStringPointer(v8Runtime->v8Isolate, message.get());
            LOG_DEBUG("Sending response: " << *stdStringMessagePointer.get());
            FETCH_JNI_ENV(GlobalJavaVM);
            jstring jMessage = Javet::Converter::ToJavaString(jniEnv, stdStringMessagePointer->c_str());
            jniEnv->CallVoidMethod(mV8Inspector, jmethodIDV8InspectorReceiveResponse, jMessage);
            jniEnv->DeleteLocalRef(jMessage);
        }

        JavetInspectorChannel::~JavetInspectorChannel() {
        }
    }
}

