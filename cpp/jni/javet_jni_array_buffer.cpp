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

#include <cstdint>
#include <cstring>
#include <limits>
#include <memory>
#include <new>

#include "javet_jni.h"

namespace {
    struct DirectByteBufferReference {
        jobject byteBuffer;
        JavaVM* javaVM;
    };

    void DeleteDirectByteBufferReference(void*, size_t, void* deleterData) {
        auto directByteBufferReference =
            std::unique_ptr<DirectByteBufferReference>(
                static_cast<DirectByteBufferReference*>(deleterData));
        if (directByteBufferReference == nullptr ||
            directByteBufferReference->byteBuffer == nullptr ||
            directByteBufferReference->javaVM == nullptr) {
            return;
        }
        auto jniEnvScope = Javet::JNIEnvScope::Acquire(directByteBufferReference->javaVM);
        if (jniEnvScope) {
            JNIEnv* jniEnv = jniEnvScope.Get();
            jniEnv->DeleteGlobalRef(directByteBufferReference->byteBuffer);
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
        }
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_arrayBufferCreate__JI
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint length) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (length >= 0) {
        auto v8LocalArrayBuffer = v8::ArrayBuffer::New(v8Isolate, length);
        if (!v8LocalArrayBuffer.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8LocalArrayBuffer);
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_arrayBufferCreate__JLjava_nio_ByteBuffer_2
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mByteBuffer) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    void* sourceData = jniEnv->GetDirectBufferAddress(mByteBuffer);
    const jlong sourceCapacity = jniEnv->GetDirectBufferCapacity(mByteBuffer);
    if (sourceData == nullptr || sourceCapacity < 0) {
        return Javet::Exceptions::ThrowJavetConverterException(
            jniEnv,
            "Byte buffer must be direct.");
    }
    if (static_cast<std::uintmax_t>(sourceCapacity) >
        static_cast<std::uintmax_t>(std::numeric_limits<size_t>::max())) {
        return Javet::Exceptions::ThrowJavetConverterException(
            jniEnv,
            "Byte buffer capacity is too large.");
    }
    const auto sourceLength = static_cast<size_t>(sourceCapacity);
#ifdef V8_ENABLE_SANDBOX
    // V8's sandbox requires backing stores to live inside the sandbox address
    // space. The JVM direct buffer is on the regular process heap and cannot
    // be wrapped — allocate inside the sandbox via the isolate's allocator
    // and copy. This loses zero-copy sharing with the Java buffer; callers
    // running with sandbox enabled must treat the result as a snapshot.
    auto v8LocalArrayBuffer = v8::ArrayBuffer::New(v8Isolate, sourceLength);
    if (!v8LocalArrayBuffer.IsEmpty() && sourceData != nullptr && sourceLength > 0) {
        std::memcpy(v8LocalArrayBuffer->GetBackingStore()->Data(), sourceData, sourceLength);
    }
#else
    JavaVM* javaVM = nullptr;
    if (jniEnv->GetJavaVM(&javaVM) != JNI_OK || javaVM == nullptr) {
        return Javet::Exceptions::ThrowJavetConverterException(
            jniEnv,
            "Failed to access the Java VM.");
    }
    auto directByteBufferReference = std::unique_ptr<DirectByteBufferReference>(
        new (std::nothrow) DirectByteBufferReference{nullptr, javaVM});
    if (directByteBufferReference == nullptr) {
        return Javet::Exceptions::ThrowJavetConverterException(
            jniEnv,
            "Failed to retain the direct byte buffer.");
    }
    jobject globalByteBuffer = jniEnv->NewGlobalRef(mByteBuffer);
    if (globalByteBuffer == nullptr) {
        if (!jniEnv->ExceptionCheck()) {
            return Javet::Exceptions::ThrowJavetConverterException(
                jniEnv,
                "Failed to retain the direct byte buffer.");
        }
        return nullptr;
    }
    INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
    directByteBufferReference->byteBuffer = globalByteBuffer;
    std::unique_ptr<v8::BackingStore> v8BackingStorePointer = v8::ArrayBuffer::NewBackingStore(
        sourceData,
        sourceLength,
        DeleteDirectByteBufferReference,
        directByteBufferReference.get());
    if (v8BackingStorePointer == nullptr) {
        jniEnv->DeleteGlobalRef(globalByteBuffer);
        INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
        return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
    }
    directByteBufferReference.release();
    auto v8LocalArrayBuffer = v8::ArrayBuffer::New(v8Isolate, std::move(v8BackingStorePointer));
#endif
    if (!v8LocalArrayBuffer.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8LocalArrayBuffer);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}
