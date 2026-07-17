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

#include "javet_v8_runtime.h"

namespace Javet {
    namespace Compiler {
        template<typename T>
        struct CompileResult final {
            v8::MaybeLocal<T> compiledValue;
            bool cacheRejected = false;
            bool failed = false;
        };

        using FunctionCompileResult = CompileResult<v8::Function>;
        using ModuleCompileResult = CompileResult<v8::Module>;
        using ScriptCompileResult = CompileResult<v8::Script>;

        [[nodiscard]] FunctionCompileResult compileFunction(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            jstring script,
            jbyteArray cachedData,
            jstring resourceName,
            jint resourceLineOffset,
            jint resourceColumnOffset,
            jint scriptId,
            jboolean isWASM,
            jobjectArray arguments,
            jobjectArray contextExtensions) noexcept;

        [[nodiscard]] ModuleCompileResult compileModule(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            jstring script,
            jbyteArray cachedData,
            jstring resourceName,
            jint resourceLineOffset,
            jint resourceColumnOffset,
            jint scriptId,
            jboolean isWASM,
            jboolean isModule) noexcept;

        [[nodiscard]] ScriptCompileResult compileScript(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            jstring script,
            jbyteArray cachedData,
            jstring resourceName,
            jint resourceLineOffset,
            jint resourceColumnOffset,
            jint scriptId,
            jboolean isWASM,
            jboolean isModule) noexcept;

        [[nodiscard]] jbyteArray getCachedData(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalFunction& v8LocalFunction) noexcept;

        [[nodiscard]] jbyteArray getCachedData(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalModule& v8LocalModule) noexcept;

        [[nodiscard]] jbyteArray getCachedData(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalScript& v8LocalScript) noexcept;

        [[nodiscard]] jobject toExternal(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const FunctionCompileResult& compileResult) noexcept;

        [[nodiscard]] jobject toExternal(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const ModuleCompileResult& compileResult) noexcept;

        [[nodiscard]] jobject toExternal(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const ScriptCompileResult& compileResult) noexcept;
    }
}
