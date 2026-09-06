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
        static inline v8_inspector::StringView ConvertFromAsciiStringToStringView(
            const std::string& asciiString) {
            return v8_inspector::StringView(
                reinterpret_cast<const uint8_t*>(asciiString.data()),
                asciiString.length());
        }

        static inline v8_inspector::StringView ConvertFromUtf16StringToStringView(
            const std::u16string& utf16String) {
            static_assert(sizeof(char16_t) == sizeof(uint16_t));
            return v8_inspector::StringView(
                reinterpret_cast<const uint16_t*>(utf16String.data()),
                utf16String.length());
        }

        static inline std::string ConvertFromStringBufferToUtf8String(
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
            return std::string(*v8Utf8Value);
        }

        static inline jstring ConvertFromStringViewToJavaString(
            JNIEnv* jniEnv,
            const v8_inspector::StringView& stringView) {
            std::u16string utf16String;
            utf16String.resize(stringView.length());
            if (stringView.is8Bit()) {
                for (size_t index = 0; index < stringView.length(); ++index) {
                    utf16String[index] = static_cast<char16_t>(stringView.characters8()[index]);
                }
            }
            else {
                for (size_t index = 0; index < stringView.length(); ++index) {
                    utf16String[index] = static_cast<char16_t>(stringView.characters16()[index]);
                }
            }
            return Javet::Converter::ToJavaStringFromUtf16(jniEnv, utf16String);
        }

        bool Initialize(JNIEnv* jniEnv) noexcept {
            JNIInitializer jniInitializer(jniEnv);
            jniInitializer.FindGlobalClass(jclassV8Inspector, "com/caoccao/javet/interop/V8Inspector");
            jniInitializer.GetMethodID(jmethodIDV8InspectorConsoleAPIMessage, jclassV8Inspector, "consoleAPIMessage", "(IILjava/lang/String;Ljava/lang/String;II)V");
            jniInitializer.GetMethodID(jmethodIDV8InspectorFlushProtocolNotifications, jclassV8Inspector, "flushProtocolNotifications", "()V");
            jniInitializer.GetMethodID(jmethodIDV8InspectorInstallAdditionalCommandLineAPI, jclassV8Inspector, "installAdditionalCommandLineAPI", "(Lcom/caoccao/javet/values/reference/IV8ValueObject;)V");
            jniInitializer.GetMethodID(jmethodIDV8InspectorReceiveNotification, jclassV8Inspector, "receiveNotification", "(Ljava/lang/String;)V");
            jniInitializer.GetMethodID(jmethodIDV8InspectorReceiveResponse, jclassV8Inspector, "receiveResponse", "(Ljava/lang/String;)V");
            jniInitializer.GetMethodID(jmethodIDV8InspectorRunIfWaitingForDebugger, jclassV8Inspector, "runIfWaitingForDebugger", "(I)V");
            return jniInitializer.IsValid();
        }

        JavetInspector::JavetInspector(V8Runtime* v8Runtime, const std::u16string& name) noexcept {
            this->v8Runtime = v8Runtime;
            client.reset(new JavetInspectorClient(v8Runtime, name));
        }

        int JavetInspector::addSession(JNIEnv* jniEnv, const jobject mV8Inspector, bool waitForDebugger) noexcept {
            return client->addSession(jniEnv, mV8Inspector, waitForDebugger);
        }

        void JavetInspector::breakProgram(int sessionId, const std::u16string& breakReason, const std::u16string& breakDetails) noexcept {
            client->breakProgram(sessionId, breakReason, breakDetails);
        }

        void JavetInspector::cancelPauseOnNextStatement(int sessionId) noexcept {
            client->cancelPauseOnNextStatement(sessionId);
        }

        jobject JavetInspector::evaluate(JNIEnv* jniEnv, int sessionId, const std::u16string& expression, bool includeCommandLineAPI) noexcept {
            return client->evaluate(jniEnv, sessionId, expression, includeCommandLineAPI);
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

        void JavetInspector::idleFinished() noexcept {
            client->idleFinished();
        }

        void JavetInspector::idleStarted() noexcept {
            client->idleStarted();
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

        void JavetInspector::postMessage(int sessionId, std::u16string message) noexcept {
            auto utf8Message = Javet::Converter::ToUtf8String(message);
            LOG_DEBUG("Queueing request for session " << sessionId << ": " << utf8Message);
            client->postMessage(sessionId, std::move(message));
        }

        void JavetInspector::removeSession(int sessionId) noexcept {
            client->removeSession(sessionId);
        }

        void JavetInspector::schedulePauseOnNextStatement(int sessionId, const std::u16string& breakReason, const std::u16string& breakDetails) noexcept {
            client->schedulePauseOnNextStatement(sessionId, breakReason, breakDetails);
        }

        void JavetInspector::setSkipAllPauses(int sessionId, bool skip) noexcept {
            client->setSkipAllPauses(sessionId, skip);
        }

        void JavetInspector::waitForDebugger() noexcept {
            client->waitForDebuggerLoop();
        }

        JavetInspector::~JavetInspector() {
            // Client destructor cleans up all sessions.
        }

        JavetInspectorClient::JavetInspectorClient(
            V8Runtime* v8Runtime,
            const std::u16string& name) noexcept
            : v8Inspector(nullptr) {
            activateMessageLoop = false;
            runningMessageLoop.store(false);
            waitingForDebugger.store(false);
            nextSessionId = 1;
            this->v8Runtime = v8Runtime;
            this->name = name;
            auto v8Context = v8Runtime->GetV8LocalContext();
            v8Inspector.reset(v8_inspector::V8Inspector::create(v8Runtime->v8Isolate, this).release());
            v8Context->SetAlignedPointerInEmbedderData(EMBEDDER_DATA_INDEX, this, v8::kEmbedderDataTypeTagDefault);
            auto humanReadableName = ConvertFromUtf16StringToStringView(name);
            v8_inspector::V8ContextInfo contextInfo(v8Context, CONTEXT_GROUP_ID, humanReadableName);
            contextInfo.origin = humanReadableName;
            static const std::string auxDataStr = "{\"isDefault\":true}";
            auto auxData = ConvertFromAsciiStringToStringView(auxDataStr);
            contextInfo.auxData = auxData;
            v8Inspector->contextCreated(contextInfo);
        }

        int JavetInspectorClient::addSession(JNIEnv* jniEnv, const jobject mV8Inspector, bool waitForDebugger) noexcept {
            int sessionId = nextSessionId++;
            auto sessionPointer = std::make_shared<JavetInspectorSession>(
                jniEnv, sessionId, v8Runtime, mV8Inspector, v8Inspector.get(),
                waitForDebugger, messageMutex);
            std::lock_guard<std::mutex> lock(messageMutex);
            sessionMap[sessionId] = std::move(sessionPointer);
            return sessionId;
        }

        void JavetInspectorClient::breakProgram(int sessionId, const std::u16string& breakReason, const std::u16string& breakDetails) noexcept {
            // Look up the session under the lock, then release it before
            // calling breakProgram().  V8's breakProgram() may enter
            // runMessageLoopOnPause() which also acquires messageMutex,
            // so holding it here would deadlock.
            JavetInspectorSession* sessionPtr = nullptr;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                auto it = sessionMap.find(sessionId);
                if (it != sessionMap.end()) {
                    sessionPtr = it->second.get();
                }
            }
            if (sessionPtr) {
                sessionPtr->breakProgram(breakReason, breakDetails);
            }
        }

        void JavetInspectorClient::cancelPauseOnNextStatement(int sessionId) noexcept {
            JavetInspectorSession* sessionPtr = nullptr;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                auto it = sessionMap.find(sessionId);
                if (it != sessionMap.end()) {
                    sessionPtr = it->second.get();
                }
            }
            if (sessionPtr) {
                sessionPtr->cancelPauseOnNextStatement();
            }
        }

        void JavetInspectorClient::contextCreated(const V8LocalContext& v8Context) noexcept {
            v8Context->SetAlignedPointerInEmbedderData(EMBEDDER_DATA_INDEX, this, v8::kEmbedderDataTypeTagDefault);
            auto humanReadableName = ConvertFromUtf16StringToStringView(name);
            v8_inspector::V8ContextInfo contextInfo(v8Context, CONTEXT_GROUP_ID, humanReadableName);
            contextInfo.origin = humanReadableName;
            static const std::string auxDataStr = "{\"isDefault\":true}";
            auto auxData = ConvertFromAsciiStringToStringView(auxDataStr);
            contextInfo.auxData = auxData;
            v8Inspector->contextCreated(contextInfo);
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
            // Notify all sessions' Java objects.
            std::vector<jobject> javaObjects;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                for (auto& [id, session] : sessionMap) {
                    javaObjects.push_back(session->getJavaObject());
                }
            }
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("JavetInspectorClient::consoleAPIMessage(): JNI environment is unavailable.");
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            jstring jMessage = ConvertFromStringViewToJavaString(jniEnv, message);
            jstring jUrl = ConvertFromStringViewToJavaString(jniEnv, url);
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

        jobject JavetInspectorClient::evaluate(JNIEnv* jniEnv, int sessionId, const std::u16string& expression, bool includeCommandLineAPI) noexcept {
            JavetInspectorSession* sessionPtr = nullptr;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                auto it = sessionMap.find(sessionId);
                if (it != sessionMap.end()) {
                    sessionPtr = it->second.get();
                }
            }
            if (sessionPtr) {
                return sessionPtr->evaluate(jniEnv, expression, includeCommandLineAPI);
            }
            return nullptr;
        }

        void JavetInspectorClient::idleFinished() noexcept {
            v8Inspector->idleFinished();
        }

        void JavetInspectorClient::idleStarted() noexcept {
            v8Inspector->idleStarted();
        }

        void JavetInspectorClient::installAdditionalCommandLineAPI(
                v8::Local<v8::Context> v8Context, v8::Local<v8::Object> commandLineAPI) {
            // Wrap the command-line API object via the converter so Java
            // listeners can install custom properties on it.
            // The Java side is responsible for closing the object.
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("JavetInspectorClient::installAdditionalCommandLineAPI(): JNI environment is unavailable.");
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            jobject jCommandLineAPI = Javet::Converter::ToExternalV8Value(
                jniEnv, v8Runtime, v8Context, commandLineAPI);
            if (jCommandLineAPI != nullptr) {
                std::vector<jobject> javaObjects;
                {
                    std::lock_guard<std::mutex> lock(messageMutex);
                    for (auto& [id, session] : sessionMap) {
                        javaObjects.push_back(session->getJavaObject());
                    }
                }
                for (jobject jobj : javaObjects) {
                    jniEnv->CallVoidMethod(jobj, jmethodIDV8InspectorInstallAdditionalCommandLineAPI, jCommandLineAPI);
                }
            }
            DELETE_LOCAL_REF(jniEnv, jCommandLineAPI);
        }

        bool JavetInspectorClient::isRunningMessageLoop() const noexcept {
            return runningMessageLoop.load();
        }

        bool JavetInspectorClient::isWaitingForDebugger() const noexcept {
            return waitingForDebugger.load();
        }

        bool JavetInspectorClient::hasQueuedMessages() const noexcept {
            // Caller must hold messageMutex.
            for (const auto& [id, session] : sessionMap) {
                if (session->hasQueuedMessages()) {
                    return true;
                }
            }
            return false;
        }

        void JavetInspectorClient::postMessage(int sessionId, std::u16string message) noexcept {
            std::shared_ptr<JavetInspectorSession> sessionPointer;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                auto it = sessionMap.find(sessionId);
                if (it != sessionMap.end()) {
                    sessionPointer = it->second;
                }
            }
            if (sessionPointer) {
                sessionPointer->postMessage(std::move(message));
                messageCondition.notify_one();
            }
        }

        void JavetInspectorClient::quitMessageLoopOnPause() {
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                activateMessageLoop = false;
            }
            messageCondition.notify_one();
        }

        void JavetInspectorClient::removeSession(int sessionId) noexcept {
            std::lock_guard<std::mutex> lock(messageMutex);
            sessionMap.erase(sessionId);
        }

        std::unique_ptr<v8_inspector::StringBuffer> JavetInspectorClient::resourceNameToUrl(
                const v8_inspector::StringView& resourceName) {
            // Pass the resource name through as a URL so DevTools shows clickable source links.
            return v8_inspector::StringBuffer::create(resourceName);
        }

        void JavetInspectorClient::runIfWaitingForDebugger(int contextGroupId) {
            // Notify all sessions' Java objects.
            std::vector<jobject> javaObjects;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                waitingForDebugger.store(false);
                for (auto& [id, session] : sessionMap) {
                    javaObjects.push_back(session->getJavaObject());
                }
            }
            messageCondition.notify_one();
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("JavetInspectorClient::runIfWaitingForDebugger(): JNI environment is unavailable.");
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            for (jobject jobj : javaObjects) {
                jniEnv->CallVoidMethod(jobj, jmethodIDV8InspectorRunIfWaitingForDebugger, contextGroupId);
            }
        }

        void JavetInspectorClient::runMessageLoopOnPause(int contextGroupId) {
            if (!runningMessageLoop) {
                {
                    std::lock_guard<std::mutex> lock(messageMutex);
                    activateMessageLoop = true;
                }
                runningMessageLoop.store(true);
                while (true) {
                    // Drain any queued protocol messages from the DevTools frontend.
                    drainQueue();
                    // Pump V8 platform tasks.
                    while (v8::platform::PumpMessageLoop(v8Runtime->v8PlatformPointer, v8Runtime->v8Isolate)) {
                    }
                    std::unique_lock<std::mutex> lock(messageMutex);
                    if (!activateMessageLoop) {
                        break;
                    }
                    messageCondition.wait(lock, [this]() {
                        return !activateMessageLoop || hasQueuedMessages();
                    });
                    if (!activateMessageLoop) {
                        break;
                    }
                }
                runningMessageLoop.store(false);
            }
        }

        void JavetInspectorClient::schedulePauseOnNextStatement(int sessionId, const std::u16string& breakReason, const std::u16string& breakDetails) noexcept {
            JavetInspectorSession* sessionPtr = nullptr;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                auto it = sessionMap.find(sessionId);
                if (it != sessionMap.end()) {
                    sessionPtr = it->second.get();
                }
            }
            if (sessionPtr) {
                sessionPtr->schedulePauseOnNextStatement(breakReason, breakDetails);
            }
        }

        void JavetInspectorClient::setSkipAllPauses(int sessionId, bool skip) noexcept {
            JavetInspectorSession* sessionPtr = nullptr;
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                auto it = sessionMap.find(sessionId);
                if (it != sessionMap.end()) {
                    sessionPtr = it->second.get();
                }
            }
            if (sessionPtr) {
                sessionPtr->setSkipAllPauses(skip);
            }
        }

        void JavetInspectorClient::waitForDebuggerLoop() noexcept {
            {
                std::lock_guard<std::mutex> lock(messageMutex);
                waitingForDebugger.store(true);
            }
            while (waitingForDebugger.load()) {
                // Drain any queued protocol messages from the DevTools frontend.
                drainQueue();
                // Pump V8 platform tasks.
                while (v8::platform::PumpMessageLoop(v8Runtime->v8PlatformPointer, v8Runtime->v8Isolate)) {
                }
                std::unique_lock<std::mutex> lock(messageMutex);
                messageCondition.wait(lock, [this]() {
                    return !waitingForDebugger.load() || hasQueuedMessages();
                });
            }
        }

        JavetInspectorSession::JavetInspectorSession(
            JNIEnv* jniEnv,
            int sessionId,
            V8Runtime* v8Runtime,
            const jobject mV8Inspector,
            v8_inspector::V8Inspector* v8Inspector,
            bool waitForDebugger,
            std::mutex& sharedMutex) noexcept
            : sharedMutex(sharedMutex) {
            this->sessionId = sessionId;
            this->v8Runtime = v8Runtime;
            this->mV8Inspector = jniEnv->NewGlobalRef(mV8Inspector);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            channel.reset(new JavetInspectorChannel(v8Runtime, this->mV8Inspector));
            auto pauseState = waitForDebugger
                ? v8_inspector::V8Inspector::kWaitingForDebugger
                : v8_inspector::V8Inspector::kNotWaitingForDebugger;
            v8InspectorSession = v8Inspector->connectShared(
                CONTEXT_GROUP_ID,
                channel.get(),
                v8_inspector::StringView(),
                v8_inspector::V8Inspector::kFullyTrusted,
                pauseState);
        }

        int JavetInspectorSession::getSessionId() const noexcept {
            return sessionId;
        }

        jobject JavetInspectorSession::getJavaObject() const noexcept {
            return mV8Inspector;
        }

        void JavetInspectorSession::drainQueue() noexcept {
            while (true) {
                std::queue<std::u16string> localMessageQueue;
                {
                    std::lock_guard<std::mutex> lock(sharedMutex);
                    if (messageQueue.empty()) {
                        return;
                    }
                    messageQueue.swap(localMessageQueue);
                }
                while (!localMessageQueue.empty()) {
                    auto stringView = ConvertFromUtf16StringToStringView(localMessageQueue.front());
                    v8InspectorSession->dispatchProtocolMessage(stringView);
                    localMessageQueue.pop();
                }
            }
        }

        void JavetInspectorSession::postMessage(std::u16string message) noexcept {
            std::lock_guard<std::mutex> lock(sharedMutex);
            messageQueue.push(std::move(message));
        }

        bool JavetInspectorSession::hasQueuedMessages() const noexcept {
            // Caller must hold sharedMutex.
            return !messageQueue.empty();
        }

        void JavetInspectorSession::stop() noexcept {
            if (v8InspectorSession) {
                v8InspectorSession->stop();
            }
        }

        void JavetInspectorSession::breakProgram(const std::u16string& breakReason, const std::u16string& breakDetails) noexcept {
            if (v8InspectorSession) {
                auto reasonStringView = ConvertFromUtf16StringToStringView(breakReason);
                auto detailsStringView = ConvertFromUtf16StringToStringView(breakDetails);
                v8InspectorSession->breakProgram(reasonStringView, detailsStringView);
            }
        }

        void JavetInspectorSession::cancelPauseOnNextStatement() noexcept {
            if (v8InspectorSession) {
                v8InspectorSession->cancelPauseOnNextStatement();
            }
        }

        jobject JavetInspectorSession::evaluate(JNIEnv* jniEnv, const std::u16string& expression, bool includeCommandLineAPI) noexcept {
            if (v8InspectorSession) {
                auto v8Context = v8Runtime->GetV8LocalContext();
                auto expressionStringView = ConvertFromUtf16StringToStringView(expression);
                auto result = v8InspectorSession->evaluate(v8Context, expressionStringView, includeCommandLineAPI);
                if (result.type == v8_inspector::V8InspectorSession::EvaluateResult::ResultType::kSuccess
                    || result.type == v8_inspector::V8InspectorSession::EvaluateResult::ResultType::kException) {
                    if (!result.value.IsEmpty()) {
                        return Javet::Converter::ToExternalV8Value(jniEnv, v8Runtime, v8Context, result.value);
                    }
                }
            }
            return nullptr;
        }

        void JavetInspectorSession::schedulePauseOnNextStatement(const std::u16string& breakReason, const std::u16string& breakDetails) noexcept {
            if (v8InspectorSession) {
                auto reasonStringView = ConvertFromUtf16StringToStringView(breakReason);
                auto detailsStringView = ConvertFromUtf16StringToStringView(breakDetails);
                v8InspectorSession->schedulePauseOnNextStatement(reasonStringView, detailsStringView);
            }
        }

        void JavetInspectorSession::setSkipAllPauses(bool skip) noexcept {
            if (v8InspectorSession) {
                v8InspectorSession->setSkipAllPauses(skip);
            }
        }

        JavetInspectorSession::~JavetInspectorSession() {
            // Gracefully stop the session to disable debugger pausing
            // and prevent callbacks during teardown.
            stop();
            // Disconnect the session (may reference the channel).
            v8InspectorSession.reset();
            channel.reset();
            if (mV8Inspector != nullptr) {
                auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
                if (!jniEnvScope) {
                    LOG_ERROR("JavetInspectorSession::~JavetInspectorSession(): JNI environment is unavailable.");
                    return;
                }
                JNIEnv* jniEnv = jniEnvScope.Get();
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
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("JavetInspectorChannel::flushProtocolNotifications(): JNI environment is unavailable.");
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            jniEnv->CallVoidMethod(mV8Inspector, jmethodIDV8InspectorFlushProtocolNotifications);
        }

        void JavetInspectorChannel::sendNotification(std::unique_ptr<v8_inspector::StringBuffer> message) {
            // The lock is not required.
            V8HandleScope v8HandleScope(v8Runtime->v8Isolate);
            auto stdStringMessage = ConvertFromStringBufferToUtf8String(v8Runtime->v8Isolate, message.get());
            LOG_DEBUG("Sending notification: " << stdStringMessage);
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("JavetInspectorChannel::sendNotification(): JNI environment is unavailable.");
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            jstring jMessage = ConvertFromStringViewToJavaString(jniEnv, message->string());
            jniEnv->CallVoidMethod(mV8Inspector, jmethodIDV8InspectorReceiveNotification, jMessage);
            jniEnv->DeleteLocalRef(jMessage);
        }

        void JavetInspectorChannel::sendResponse(int callId, std::unique_ptr<v8_inspector::StringBuffer> message) {
            // The lock is not required.
            V8HandleScope v8HandleScope(v8Runtime->v8Isolate);
            auto stdStringMessage = ConvertFromStringBufferToUtf8String(v8Runtime->v8Isolate, message.get());
            LOG_DEBUG("Sending response: " << stdStringMessage);
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("JavetInspectorChannel::sendResponse(): JNI environment is unavailable.");
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            jstring jMessage = ConvertFromStringViewToJavaString(jniEnv, message->string());
            jniEnv->CallVoidMethod(mV8Inspector, jmethodIDV8InspectorReceiveResponse, jMessage);
            jniEnv->DeleteLocalRef(jMessage);
        }

        JavetInspectorChannel::~JavetInspectorChannel() {
        }
    }
}
