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

#include "javet_jni.h"

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_arrayBufferCreate__JI
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint length) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    if (length >= 0) {
        auto v8LocalArrayBuffer = v8::ArrayBuffer::New(v8Context->GetIsolate(), length);
        if (!v8LocalArrayBuffer.IsEmpty()) {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalArrayBuffer);
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_arrayBufferCreate__JLjava_nio_ByteBuffer_2
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jobject mByteBuffer) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    std::unique_ptr<v8::BackingStore> v8BackingStorePointer = v8::ArrayBuffer::NewBackingStore(
        jniEnv->GetDirectBufferAddress(mByteBuffer),
        static_cast<size_t>(jniEnv->GetDirectBufferCapacity(mByteBuffer)),
        [](void*, size_t, void*) {},
        nullptr);
    auto v8LocalArrayBuffer = v8::ArrayBuffer::New(v8Context->GetIsolate(), std::move(v8BackingStorePointer));
    if (!v8LocalArrayBuffer.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalArrayBuffer);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}
