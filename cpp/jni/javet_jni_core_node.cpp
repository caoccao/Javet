/*
 *   Copyright (c) 2021-2025. caoccao.com Sam Cao
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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_NodeNative_await
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jint mAwaitMode) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    auto umAwaitMode = static_cast<Javet::Enums::V8AwaitMode::V8AwaitMode>(mAwaitMode);
    return (jboolean)v8Runtime->Await(umAwaitMode);
}

#ifdef ENABLE_NODE
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_NodeNative_isStopping
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    return v8Runtime->IsStopping();
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_NodeNative_setStopping
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jboolean stopping) {
    auto v8Runtime = Javet::V8Runtime::FromHandle(v8RuntimeHandle);
    v8Runtime->SetStopping(stopping);
}
#endif
