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

#include <jni.h>
#include "javet_node.h"
#include "javet_v8.h"

#ifdef __ANDROID__
#define SUPPORTED_JNI_VERSION JNI_VERSION_1_6
#else
#define SUPPORTED_JNI_VERSION JNI_VERSION_1_8
#endif

jclass FIND_CLASS(JNIEnv* jniEnv, const char* className) noexcept;

void DELETE_GLOBAL_CLASS_REFS(JNIEnv* jniEnv) noexcept;

namespace Javet {
    class V8Runtime;

    class JNIStringChars final {
    public:
        JNIStringChars(JNIEnv* jniEnv, jstring string) noexcept
            : chars(string == nullptr ? nullptr : jniEnv->GetStringChars(string, nullptr)),
            jniEnv(jniEnv), string(string) {
        }

        JNIStringChars(const JNIStringChars&) = delete;
        JNIStringChars& operator=(const JNIStringChars&) = delete;

        JNIStringChars(JNIStringChars&& other) noexcept
            : chars(other.chars), jniEnv(other.jniEnv), string(other.string) {
            other.chars = nullptr;
            other.string = nullptr;
        }

        explicit operator bool() const noexcept {
            return chars != nullptr;
        }

        const jchar* Get() const noexcept {
            return chars;
        }

        ~JNIStringChars() {
            if (chars != nullptr) {
                jniEnv->ReleaseStringChars(string, chars);
            }
        }

    private:
        const jchar* chars;
        JNIEnv* jniEnv;
        jstring string;
    };

    class JNIStringUTFChars final {
    public:
        JNIStringUTFChars(JNIEnv* jniEnv, jstring string) noexcept
            : chars(string == nullptr ? nullptr : jniEnv->GetStringUTFChars(string, nullptr)),
            jniEnv(jniEnv), string(string) {
        }

        JNIStringUTFChars(const JNIStringUTFChars&) = delete;
        JNIStringUTFChars& operator=(const JNIStringUTFChars&) = delete;

        JNIStringUTFChars(JNIStringUTFChars&& other) noexcept
            : chars(other.chars), jniEnv(other.jniEnv), string(other.string) {
            other.chars = nullptr;
            other.string = nullptr;
        }

        explicit operator bool() const noexcept {
            return chars != nullptr;
        }

        const char* Get() const noexcept {
            return chars;
        }

        ~JNIStringUTFChars() {
            if (chars != nullptr) {
                jniEnv->ReleaseStringUTFChars(string, chars);
            }
        }

    private:
        const char* chars;
        JNIEnv* jniEnv;
        jstring string;
    };

    class JNIInitializer final {
    public:
        explicit JNIInitializer(JNIEnv* jniEnv) noexcept
            : jniEnv(jniEnv), valid(jniEnv != nullptr && !jniEnv->ExceptionCheck()) {
        }

        void FindGlobalClass(jclass& javaClass, const char* className) noexcept {
            if (valid) {
                javaClass = FIND_CLASS(jniEnv, className);
                Validate(javaClass);
            }
            else {
                javaClass = nullptr;
            }
        }

        void FindLocalClass(jclass& javaClass, const char* className) noexcept {
            if (valid) {
                javaClass = jniEnv->FindClass(className);
                Validate(javaClass);
            }
            else {
                javaClass = nullptr;
            }
        }

        void GetFieldID(
            jfieldID& fieldID,
            jclass javaClass,
            const char* name,
            const char* signature) noexcept {
            if (valid && javaClass != nullptr) {
                fieldID = jniEnv->GetFieldID(javaClass, name, signature);
                Validate(fieldID);
            }
            else {
                fieldID = nullptr;
                valid = false;
            }
        }

        void GetMethodID(
            jmethodID& methodID,
            jclass javaClass,
            const char* name,
            const char* signature) noexcept {
            if (valid && javaClass != nullptr) {
                methodID = jniEnv->GetMethodID(javaClass, name, signature);
                Validate(methodID);
            }
            else {
                methodID = nullptr;
                valid = false;
            }
        }

        void GetStaticFieldID(
            jfieldID& fieldID,
            jclass javaClass,
            const char* name,
            const char* signature) noexcept {
            if (valid && javaClass != nullptr) {
                fieldID = jniEnv->GetStaticFieldID(javaClass, name, signature);
                Validate(fieldID);
            }
            else {
                fieldID = nullptr;
                valid = false;
            }
        }

        void GetStaticMethodID(
            jmethodID& methodID,
            jclass javaClass,
            const char* name,
            const char* signature) noexcept {
            if (valid && javaClass != nullptr) {
                methodID = jniEnv->GetStaticMethodID(javaClass, name, signature);
                Validate(methodID);
            }
            else {
                methodID = nullptr;
                valid = false;
            }
        }

        [[nodiscard]] bool IsValid() const noexcept {
            return valid && !jniEnv->ExceptionCheck();
        }

    private:
        template<typename T>
        void Validate(T reference) noexcept {
            valid = reference != nullptr && !jniEnv->ExceptionCheck();
        }

        JNIEnv* jniEnv;
        bool valid;
    };

    class ExternalExceptionScope {
    public:
        ExternalExceptionScope(JNIEnv* jniEnv, V8Runtime* v8Runtime) noexcept;
        virtual ~ExternalExceptionScope();

    private:
        JNIEnv* jniEnv;
        V8Runtime* v8Runtime;
    };

    class JNIEnvScope final {
    public:
        [[nodiscard]] static JNIEnvScope Acquire(JavaVM* javaVM) noexcept {
            return JNIEnvScope(javaVM);
        }

        JNIEnvScope(const JNIEnvScope&) = delete;
        JNIEnvScope& operator=(const JNIEnvScope&) = delete;

        explicit operator bool() const noexcept {
            return jniEnv != nullptr;
        }

        inline JNIEnv* Get() const noexcept {
            return jniEnv;
        }

        ~JNIEnvScope() {
            if (localFramePushed) {
                jniEnv->PopLocalFrame(nullptr);
            }
            if (attached) {
                javaVM->DetachCurrentThread();
            }
        }

    private:
        explicit JNIEnvScope(JavaVM* javaVM) noexcept
            : attached(false), javaVM(javaVM), jniEnv(nullptr), localFramePushed(false) {
            if (javaVM == nullptr) {
                return;
            }
            const jint getEnvResult = javaVM->GetEnv(
                reinterpret_cast<void**>(&jniEnv), SUPPORTED_JNI_VERSION);
            if (getEnvResult == JNI_EDETACHED) {
                if (javaVM->AttachCurrentThread(
                    reinterpret_cast<void**>(&jniEnv), nullptr) == JNI_OK) {
                    attached = true;
                }
                else {
                    jniEnv = nullptr;
                }
            }
            else if (getEnvResult != JNI_OK) {
                jniEnv = nullptr;
            }
            if (jniEnv != nullptr && jniEnv->PushLocalFrame(64) == JNI_OK) {
                localFramePushed = true;
            }
        }
        bool attached;
        JavaVM* javaVM;
        JNIEnv* jniEnv;
        bool localFramePushed;
    };
}

#define DELETE_LOCAL_REF(jniEnv, localRef) if (localRef != nullptr) { jniEnv->DeleteLocalRef(localRef); }

#define RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context);

#define RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE_WITH_UNIQUE_LOCKER(v8RuntimeHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8Locker = v8Runtime->GetUniqueV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context);

#define RUNTIME_AND_DATA_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8DataHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8PersistentDataPointer = TO_V8_PERSISTENT_DATA_POINTER(v8DataHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \

#define RUNTIME_AND_MODULE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8PersistentModulePointer = TO_V8_PERSISTENT_MODULE_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalModule = v8PersistentModulePointer->Get(v8Isolate);

#define RUNTIME_AND_SCRIPT_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8PersistentScriptPointer = TO_V8_PERSISTENT_SCRIPT_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalScript = v8PersistentScriptPointer->Get(v8Isolate);

#define RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8PersistentValuePointer = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalValue = v8PersistentValuePointer->Get(v8Isolate);

#define RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE_WITH_UNIQUE_LOCKER(v8RuntimeHandle, v8ValueHandle) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8PersistentValuePointer = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle); \
    auto v8Locker = v8Runtime->GetUniqueV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalValue = v8PersistentValuePointer->Get(v8Isolate);

#define RUNTIME_AND_2_VALUES_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle1, v8ValueHandle2) \
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle); \
    Javet::ExternalExceptionScope externalExceptionScope(jniEnv, v8Runtime); \
    auto v8PersistentValuePointer1 = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle1); \
    auto v8PersistentValuePointer2 = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandle2); \
    auto v8Locker = v8Runtime->GetSharedV8Locker(); \
    auto v8Isolate = v8Runtime->v8Isolate; \
    auto v8IsolateScope = v8Runtime->GetV8IsolateScope(); \
    V8HandleScope v8HandleScope(v8Isolate); \
    auto v8Context = v8Runtime->GetV8LocalContext(); \
    auto v8ContextScope = v8Runtime->GetV8ContextScope(v8Context); \
    auto v8LocalValue1 = v8PersistentValuePointer1->Get(v8Isolate); \
    auto v8LocalValue2 = v8PersistentValuePointer2->Get(v8Isolate);

extern JavaVM* GlobalJavaVM;

namespace Javet {

#ifdef ENABLE_NODE
    namespace NodeNative {
        extern std::shared_ptr<node::ArrayBufferAllocator> GlobalNodeArrayBufferAllocator;

        void Dispose(JNIEnv* jniEnv) noexcept;
        [[nodiscard]] bool Initialize(JNIEnv* jniEnv) noexcept;
    }
#endif

    namespace V8Native {
#ifdef ENABLE_NODE
        extern std::unique_ptr<node::MultiIsolatePlatform> GlobalV8Platform;
#else
        extern std::unique_ptr<V8Platform> GlobalV8Platform;
        extern std::shared_ptr<V8ArrayBufferAllocator> GlobalV8ArrayBufferAllocator;
#endif

        void Dispose(JNIEnv* jniEnv) noexcept;
        [[nodiscard]] bool Initialize(JNIEnv* jniEnv) noexcept;
    }
}
