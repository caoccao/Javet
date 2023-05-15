/*
 *   Copyright (c) 2021-2023 caoccao.com Sam Cao
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

#include "com_caoccao_javet_interop_V8Native.h"
#include "javet_callbacks.h"
#include "javet_converter.h"
#include "javet_enums.h"
#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_monitor.h"
#include "javet_logging.h"
#include "javet_native.h"
#include "javet_node.h"
#include "javet_v8.h"
#include "javet_v8_runtime.h"

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_batchArrayGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType,
    jobjectArray v8Values, jint startIndex, jint endIndex) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_ARRAY(v8ValueType) || IS_V8_ARGUMENTS(v8ValueType)) {
        return Javet::Converter::ToExternalV8ValueArray(
            jniEnv,
            v8Runtime,
            v8Context,
            v8LocalValue.As<v8::Array>(),
            v8Values,
            startIndex,
            endIndex);
    }
    return 0;
}
