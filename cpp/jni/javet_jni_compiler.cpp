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

#include <memory>
#include <optional>
#include <utility>
#include "javet_converter.h"
#include "javet_exceptions.h"
#include "javet_jni_compiler.h"

namespace Javet {
    namespace Compiler {
        namespace {
            class CompileRequest final {
            public:
                CompileRequest(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    jstring script,
                    jstring resourceName,
                    jint resourceLineOffset,
                    jint resourceColumnOffset,
                    jint scriptId,
                    jboolean isWASM,
                    jboolean isModule) noexcept
                    : jniEnv(jniEnv),
                    v8Runtime(v8Runtime),
                    v8Context(v8Context),
                    v8TryCatch(v8Runtime->v8Isolate) {
                    v8LocalSource = Javet::Converter::ToV8String(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        script);
                    scriptOriginPointer = Javet::Converter::ToV8ScriptOringinPointer(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        resourceName,
                        resourceLineOffset,
                        resourceColumnOffset,
                        scriptId,
                        isWASM,
                        isModule);
                }

                CompileRequest(const CompileRequest&) = delete;
                CompileRequest& operator=(const CompileRequest&) = delete;
                CompileRequest(CompileRequest&&) = delete;
                CompileRequest& operator=(CompileRequest&&) = delete;

                [[nodiscard]] v8::ScriptCompiler::CompileOptions getCompileOptions() const noexcept {
                    return hasCachedData
                        ? v8::ScriptCompiler::kConsumeCodeCache
                        : v8::ScriptCompiler::kNoCompileOptions;
                }

                [[nodiscard]] V8ScriptCompilerSource* getCompilerSource() noexcept {
                    return &compilerSource.value();
                }

                [[nodiscard]] V8TryCatch& getTryCatch() noexcept {
                    return v8TryCatch;
                }

                [[nodiscard]] bool isCacheRejected() const noexcept {
                    return hasCachedData && compilerSource->GetCachedData()->rejected;
                }

                [[nodiscard]] bool prepareCompilerSource(jbyteArray cachedData) noexcept {
                    hasCachedData = cachedData != nullptr;
                    if (hasCachedData) {
                        auto cachedDataPointer = Javet::Converter::ToCachedDataPointer(
                            jniEnv,
                            cachedData);
                        if (cachedDataPointer == nullptr) {
                            return false;
                        }
                        compilerSource.emplace(
                            v8LocalSource,
                            *scriptOriginPointer,
                            cachedDataPointer);
                    }
                    else {
                        compilerSource.emplace(v8LocalSource, *scriptOriginPointer);
                    }
                    return true;
                }

                [[nodiscard]] bool usesCachedData() const noexcept {
                    return hasCachedData;
                }

                JNIEnv* const jniEnv;
                V8Runtime* const v8Runtime;
                const V8LocalContext v8Context;

            private:
                V8TryCatch v8TryCatch;
                V8LocalString v8LocalSource;
                std::unique_ptr<v8::ScriptOrigin> scriptOriginPointer;
                std::optional<V8ScriptCompilerSource> compilerSource;
                bool hasCachedData = false;
            };

            template<typename T, typename Compile>
            [[nodiscard]] CompileResult<T> withCompilerSource(
                CompileRequest& compileRequest,
                jbyteArray cachedData,
                const char* cacheType,
                Compile&& compile) noexcept {
                CompileResult<T> compileResult;
                if (!compileRequest.prepareCompilerSource(cachedData)) {
                    compileResult.failed = true;
                    return compileResult;
                }
                compileResult.compiledValue = std::forward<Compile>(compile)(
                    compileRequest.getCompilerSource(),
                    compileRequest.getCompileOptions());
                if (compileRequest.usesCachedData()) {
                    compileResult.cacheRejected = compileRequest.isCacheRejected();
                    LOG_DEBUG(cacheType << " cache is "
                        << (compileResult.cacheRejected ? "rejected" : "accepted") << ".");
                }
                if (compileRequest.getTryCatch().HasCaught()) {
                    Javet::Exceptions::ThrowJavetCompilationException(
                        compileRequest.jniEnv,
                        compileRequest.v8Runtime,
                        compileRequest.v8Context,
                        compileRequest.getTryCatch());
                    compileResult.compiledValue = v8::MaybeLocal<T>();
                    compileResult.failed = true;
                }
                return compileResult;
            }

            template<typename CreateCachedData>
            [[nodiscard]] jbyteArray createCachedData(
                JNIEnv* jniEnv,
                V8Runtime* v8Runtime,
                const V8LocalContext& v8Context,
                CreateCachedData&& create) noexcept {
                V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
                std::unique_ptr<V8ScriptCompilerCachedData> cachedDataPointer(
                    std::forward<CreateCachedData>(create)());
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(
                        jniEnv,
                        v8Runtime,
                        v8Context,
                        v8TryCatch);
                    return nullptr;
                }
                if (cachedDataPointer) {
                    return Javet::Converter::ToJavaByteArray(jniEnv, cachedDataPointer.get());
                }
                return nullptr;
            }
        }

        FunctionCompileResult compileFunction(
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
            jobjectArray contextExtensions,
            jintArray contextExtensionTypes) noexcept {
            CompileRequest compileRequest(
                jniEnv,
                v8Runtime,
                v8Context,
                script,
                resourceName,
                resourceLineOffset,
                resourceColumnOffset,
                scriptId,
                isWASM,
                false);
            size_t argumentCount = 0;
            size_t contextExtensionCount = 0;
            std::unique_ptr<V8LocalString[]> argumentsPointer;
            std::unique_ptr<V8LocalObject[]> contextExtensionsPointer;
            if (arguments != nullptr) {
                argumentCount = jniEnv->GetArrayLength(arguments);
                if (argumentCount > 0) {
                    argumentsPointer = Javet::Converter::ToV8Strings(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        arguments);
                }
            }
            if (contextExtensions != nullptr) {
                contextExtensionCount = jniEnv->GetArrayLength(contextExtensions);
                if (contextExtensionCount > 0) {
                    contextExtensionsPointer = Javet::Converter::ToV8Objects(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        v8Context,
                        contextExtensions,
                        contextExtensionTypes);
                }
            }
            return withCompilerSource<v8::Function>(
                compileRequest,
                cachedData,
                "Function",
                [&](V8ScriptCompilerSource* compilerSource,
                    v8::ScriptCompiler::CompileOptions compileOptions) {
                    return v8::ScriptCompiler::CompileFunction(
                        v8Context,
                        compilerSource,
                        argumentCount,
                        argumentsPointer.get(),
                        contextExtensionCount,
                        contextExtensionsPointer.get(),
                        compileOptions);
                });
        }

        ModuleCompileResult compileModule(
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
            jboolean isModule) noexcept {
            CompileRequest compileRequest(
                jniEnv,
                v8Runtime,
                v8Context,
                script,
                resourceName,
                resourceLineOffset,
                resourceColumnOffset,
                scriptId,
                isWASM,
                isModule);
            return withCompilerSource<v8::Module>(
                compileRequest,
                cachedData,
                "Module",
                [&](V8ScriptCompilerSource* compilerSource,
                    v8::ScriptCompiler::CompileOptions compileOptions) {
                    return v8::ScriptCompiler::CompileModule(
                        v8Runtime->v8Isolate,
                        compilerSource,
                        compileOptions);
                });
        }

        ScriptCompileResult compileScript(
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
            jboolean isModule) noexcept {
            CompileRequest compileRequest(
                jniEnv,
                v8Runtime,
                v8Context,
                script,
                resourceName,
                resourceLineOffset,
                resourceColumnOffset,
                scriptId,
                isWASM,
                isModule);
            return withCompilerSource<v8::Script>(
                compileRequest,
                cachedData,
                "Script",
                [&](V8ScriptCompilerSource* compilerSource,
                    v8::ScriptCompiler::CompileOptions compileOptions) {
                    if (compileOptions == v8::ScriptCompiler::kConsumeCodeCache) {
                        auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(
                            v8Runtime->v8Isolate);
                        V8InternalDisallowCompilation v8InternalDisallowCompilation(
                            v8InternalIsolate);
                        return v8::ScriptCompiler::Compile(
                            v8Context,
                            compilerSource,
                            compileOptions);
                    }
                    return v8::ScriptCompiler::Compile(
                        v8Context,
                        compilerSource,
                        compileOptions);
                });
        }

        jbyteArray getCachedData(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalFunction& v8LocalFunction) noexcept {
            return createCachedData(
                jniEnv,
                v8Runtime,
                v8Context,
                [&]() {
                    return v8::ScriptCompiler::CreateCodeCacheForFunction(v8LocalFunction);
                });
        }

        jbyteArray getCachedData(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalModule& v8LocalModule) noexcept {
            return createCachedData(
                jniEnv,
                v8Runtime,
                v8Context,
                [&]() {
                    return v8::ScriptCompiler::CreateCodeCache(
                        v8LocalModule->GetUnboundModuleScript());
                });
        }

        jbyteArray getCachedData(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalScript& v8LocalScript) noexcept {
            return createCachedData(
                jniEnv,
                v8Runtime,
                v8Context,
                [&]() {
                    return v8::ScriptCompiler::CreateCodeCache(
                        v8LocalScript->GetUnboundScript());
                });
        }

        jobject toExternal(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const FunctionCompileResult& compileResult) noexcept {
            auto compiledValue = compileResult.compiledValue;
            if (!compileResult.failed && !compiledValue.IsEmpty()) {
                return v8Runtime->SafeToExternalV8Value(
                    jniEnv,
                    v8Runtime->v8Isolate,
                    v8Context,
                    compiledValue.ToLocalChecked());
            }
            return nullptr;
        }

        jobject toExternal(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext&,
            const ModuleCompileResult& compileResult) noexcept {
            auto compiledValue = compileResult.compiledValue;
            if (!compileResult.failed && !compiledValue.IsEmpty()) {
                return Javet::Converter::ToExternalV8Module(
                    jniEnv,
                    v8Runtime,
                    compiledValue.ToLocalChecked());
            }
            return nullptr;
        }

        jobject toExternal(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext&,
            const ScriptCompileResult& compileResult) noexcept {
            auto compiledValue = compileResult.compiledValue;
            if (!compileResult.failed && !compiledValue.IsEmpty()) {
                return Javet::Converter::ToExternalV8Script(
                    jniEnv,
                    v8Runtime,
                    compiledValue.ToLocalChecked());
            }
            return nullptr;
        }
    }
}
