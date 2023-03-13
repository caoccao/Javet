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

#pragma once

#define ERROR_JNI_ON_LOAD -1

#define EMBEDDER_DATA_INDEX_V8_RUNTIME 5

#define INDEX_SCOPE_TYPE 0
#define INDEX_SCOPE_OBJECT 1
#define INDEX_SCOPE_HAS_CONTEXT 2
#define INDEX_SCOPE_START_POSITION 3
#define INDEX_SCOPE_END_POSITION 4
#define INDEX_SCOPE_SIZE 5

#define JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V"

#define JAVA_METHOD_AND_SIGNATURE_GET_HANDLE "getHandle", "()J"

#define JAVA_METHOD_TO_PRIMITIVE "toPrimitive"

