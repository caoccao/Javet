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

#define ERROR_JNI_ON_LOAD -1

constexpr auto EMBEDDER_DATA_INDEX_V8_RUNTIME = 5;

constexpr auto INDEX_SCOPE_TYPE = 0;
constexpr auto INDEX_SCOPE_OBJECT = 1;
constexpr auto INDEX_SCOPE_HAS_CONTEXT = 2;
constexpr auto INDEX_SCOPE_START_POSITION = 3;
constexpr auto INDEX_SCOPE_END_POSITION = 4;
#define INDEX_SCOPE_SIZE 5

template<typename T1, typename T2>
constexpr auto FIND_CLASS(T1 jniEnv, T2 className) {
    return (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass(className));
}

template<typename T1, typename T2>
constexpr auto GET_METHOD_CONSTRUCTOR(T1 jniEnv, T2 javaClass) {
    return jniEnv->GetMethodID(javaClass, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");
}

template<typename T1, typename T2>
constexpr auto GET_METHOD_GET_HANDLE(T1 jniEnv, T2 javaClass) {
    return jniEnv->GetMethodID(javaClass, "getHandle", "()J");
}

