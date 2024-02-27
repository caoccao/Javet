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
#include "javet_inspector.h"
#include "javet_logging.h"
#include "javet_native.h"

#define CONTEXT_GROUP_ID 1
#define EMBEDDER_DATA_INDEX 1

namespace Javet {
    namespace Inspector {
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
            jmethodIDV8InspectorFlushProtocolNotifications = jniEnv->GetMethodID(jclassV8Inspector, "flushProtocolNotifications", "()V");
            jmethodIDV8InspectorGetName = jniEnv->GetMethodID(jclassV8Inspector, "getName", "()Ljava/lang/String;");
            jmethodIDV8InspectorReceiveNotification = jniEnv->GetMethodID(jclassV8Inspector, "receiveNotification", "(Ljava/lang/String;)V");
            jmethodIDV8InspectorReceiveResponse = jniEnv->GetMethodID(jclassV8Inspector, "receiveResponse", "(Ljava/lang/String;)V");
            jmethodIDV8InspectorRunIfWaitingForDebugger = jniEnv->GetMethodID(jclassV8Inspector, "runIfWaitingForDebugger", "(I)V");
        }

        JavetInspector::JavetInspector(V8Runtime* v8Runtime, const jobject mV8Inspector) noexcept {
            FETCH_JNI_ENV(GlobalJavaVM);
            this->mV8Inspector = jniEnv->NewGlobalRef(mV8Inspector);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            this->v8Runtime = v8Runtime;
            jstring mName = (jstring)jniEnv->CallObjectMethod(this->mV8Inspector, jmethodIDV8InspectorGetName);
            char const* umName = jniEnv->GetStringUTFChars(mName, nullptr);
            std::string name(umName, jniEnv->GetStringUTFLength(mName));
            client.reset(new JavetInspectorClient(v8Runtime, name, this->mV8Inspector));
            jniEnv->ReleaseStringUTFChars(mName, umName);
        }

        void JavetInspector::send(const std::string& message) noexcept {
            LOG_DEBUG("Sending request: " << message);
            auto stringViewMessagePointer = ConvertFromStdStringToStringViewPointer(message);
            client->dispatchProtocolMessage(*stringViewMessagePointer.get());
        }

        JavetInspector::~JavetInspector() {
            if (mV8Inspector != nullptr) {
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->DeleteGlobalRef(mV8Inspector);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                mV8Inspector = nullptr;
            }
        }

        JavetInspectorClient::JavetInspectorClient(
            V8Runtime* v8Runtime,
            const std::string& name,
            const jobject mV8Inspector) noexcept
            : javetInspectorChannel(nullptr), v8Inspector(nullptr), v8InspectorSession(nullptr) {
            activateMessageLoop = false;
            runningMessageLoop = false;
            this->mV8Inspector = mV8Inspector;
            this->v8Runtime = v8Runtime;
            auto v8Context = v8Runtime->GetV8LocalContext();
            javetInspectorChannel.reset(new JavetInspectorChannel(v8Runtime, mV8Inspector));
            v8Inspector.reset(v8_inspector::V8Inspector::create(v8Runtime->v8Isolate, this).release());
            v8InspectorSession.reset(v8Inspector->connect(
                CONTEXT_GROUP_ID,
                javetInspectorChannel.get(),
                v8_inspector::StringView(),
                v8_inspector::V8Inspector::kFullyTrusted).release());
            v8Context->SetAlignedPointerInEmbedderData(EMBEDDER_DATA_INDEX, this);
            auto humanReadableNamePointer = ConvertFromStdStringToStringViewPointer(name);
            v8Inspector->contextCreated(v8_inspector::V8ContextInfo(v8Context, CONTEXT_GROUP_ID, *humanReadableNamePointer.get()));
        }

        void JavetInspectorClient::dispatchProtocolMessage(const v8_inspector::StringView& message) noexcept {
            v8InspectorSession->dispatchProtocolMessage(message);
        }

        void JavetInspectorClient::quitMessageLoopOnPause() {
            activateMessageLoop = false;
        }

        void JavetInspectorClient::runIfWaitingForDebugger(int contextGroupId) {
            FETCH_JNI_ENV(GlobalJavaVM);
            jniEnv->CallVoidMethod(mV8Inspector, jmethodIDV8InspectorRunIfWaitingForDebugger, contextGroupId);
        }

        void JavetInspectorClient::runMessageLoopOnPause(int contextGroupId) {
            if (!runningMessageLoop) {
                activateMessageLoop = true;
                runningMessageLoop = true;
                while (activateMessageLoop) {
                    while (v8::platform::PumpMessageLoop(v8Runtime->v8PlatformPointer, v8Runtime->v8Isolate)) {
                    }
                }
                runningMessageLoop = false;
                activateMessageLoop = false;
            }
        }

        V8LocalContext JavetInspectorClient::ensureDefaultContextInGroup(int contextGroupId) {
            return v8Runtime->GetV8LocalContext();
        }

        JavetInspectorClient::~JavetInspectorClient() {
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

