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

#include "javet_callbacks.h"
#include "javet_constants.h"
#include "javet_converter.h"
#include "javet_enums.h"
#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_logging.h"
#include "javet_native.h"
#include "javet_v8_runtime.h"

JavaVM* GlobalJavaVM;

jint JNI_OnLoad(JavaVM* javaVM, void* reserved) {
    LOG_INFO("JNI_Onload() begins.");
    JNIEnv* jniEnv;
    if (javaVM->GetEnv((void**)&jniEnv, SUPPORTED_JNI_VERSION) != JNI_OK) {
        LOG_ERROR("Failed to call JavaVM.GetEnv().");
        return ERROR_JNI_ON_LOAD;
    }
    if (jniEnv == nullptr) {
        LOG_ERROR("Failed to get JNIEnv.");
        return ERROR_JNI_ON_LOAD;
    }
    GlobalJavaVM = javaVM;
    Javet::Initialize(jniEnv);
    Javet::V8Native::Initialize(jniEnv);
#ifdef ENABLE_NODE
    Javet::NodeNative::Initialize(jniEnv);
#endif
    Javet::Callback::Initialize(jniEnv);
    Javet::Converter::Initialize(jniEnv);
    Javet::Exceptions::Initialize(jniEnv);
    Javet::Inspector::Initialize(jniEnv);
    Javet::Monitor::Initialize(jniEnv);
    LOG_INFO("JNI_Onload() ends.");
    return SUPPORTED_JNI_VERSION;
}

void JNI_OnUnload(JavaVM* javaVM, void* reserved) {
    LOG_INFO("JNI_OnUnload() begins.");
    JNIEnv* jniEnv;
    if (javaVM->GetEnv((void**)&jniEnv, SUPPORTED_JNI_VERSION) != JNI_OK) {
        LOG_ERROR("Failed to call JavaVM.GetEnv().");
    }
    if (jniEnv == nullptr) {
        LOG_ERROR("Failed to get JNIEnv.");
    }
    else {
#ifdef ENABLE_NODE
        Javet::NodeNative::Dispose(jniEnv);
#endif
        Javet::V8Native::Dispose(jniEnv);
    }
    LOG_INFO("JNI_OnUnload() ends.");
}

namespace Javet {
#ifdef ENABLE_NODE
    namespace NodeNative {
        jclass jclassV8Host;
        jmethodID jmethodIDV8HostIsLibraryReloadable;

        std::shared_ptr<node::ArrayBufferAllocator> GlobalNodeArrayBufferAllocator;

        void Dispose(JNIEnv* jniEnv) noexcept {
            if (!jniEnv->CallStaticBooleanMethod(jclassV8Host, jmethodIDV8HostIsLibraryReloadable)) {
                GlobalNodeArrayBufferAllocator.reset();
            }
        }

        void Initialize(JNIEnv* jniEnv) noexcept {
            jclassV8Host = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/V8Host");
            jmethodIDV8HostIsLibraryReloadable = jniEnv->GetStaticMethodID(jclassV8Host, "isLibraryReloadable", "()Z");

            if (!GlobalNodeArrayBufferAllocator) {
                GlobalNodeArrayBufferAllocator = node::ArrayBufferAllocator::Create();
            }
        }
    }
#endif

    namespace V8Native {
#ifdef ENABLE_NODE
        std::unique_ptr<node::MultiIsolatePlatform> GlobalV8Platform;
#else
        std::unique_ptr<V8Platform> GlobalV8Platform;
        std::shared_ptr<V8ArrayBufferAllocator> GlobalV8ArrayBufferAllocator;
#endif

        jclass jclassV8Host;
        jmethodID jmethodIDV8HostIsLibraryReloadable;

        void Dispose(JNIEnv* jniEnv) noexcept {
#ifdef ENABLE_NODE
            LOG_INFO("Calling cppgc::ShutdownProcess().");
            cppgc::ShutdownProcess();
#endif
            if (!jniEnv->CallStaticBooleanMethod(jclassV8Host, jmethodIDV8HostIsLibraryReloadable)) {
                v8::V8::Dispose();
                v8::V8::DisposePlatform();
                GlobalV8Platform.reset();
#ifndef ENABLE_NODE
                GlobalV8ArrayBufferAllocator.reset();
#endif
            }
        }

        /*
        These Java classes and methods need to be initialized within this file
        because the memory address probed changes in another file,
        or runtime memory corruption will take place.
        */
        void Initialize(JNIEnv* jniEnv) noexcept {
            jclassV8Host = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/V8Host");
            jmethodIDV8HostIsLibraryReloadable = jniEnv->GetStaticMethodID(jclassV8Host, "isLibraryReloadable", "()Z");

            LOG_INFO("V8::Initialize() begins.");
#ifdef ENABLE_I18N
            LOG_INFO("Calling v8::V8::InitializeICU().");
            v8::V8::InitializeICU();
#endif
            if (Javet::V8Native::GlobalV8Platform) {
                LOG_INFO("V8::Initialize() is skipped.");
            }
            else {
#ifdef ENABLE_NODE
                uv_setup_args(0, nullptr);
                std::vector<std::string> args{ DEFAULT_SCRIPT_NAME };
                std::vector<std::string> execArgs;
                std::vector<std::string> errors;
                auto flags = static_cast<node::ProcessInitializationFlags::Flags>(
                    node::ProcessInitializationFlags::kNoFlags
                    | node::ProcessInitializationFlags::kNoStdioInitialization
                    | node::ProcessInitializationFlags::kNoDefaultSignalHandling
                    | node::ProcessInitializationFlags::kNoInitializeV8
                    | node::ProcessInitializationFlags::kNoInitializeNodeV8Platform
                    | node::ProcessInitializationFlags::kNoInitializeCppgc);
                int exitCode = node::InitializeNodeWithArgs(&args, &execArgs, &errors, flags);
                if (exitCode != 0) {
                    LOG_ERROR("Failed to call node::InitializeNodeWithArgs().");
                }
                Javet::V8Native::GlobalV8Platform = node::MultiIsolatePlatform::Create(4);
#else
                Javet::V8Native::GlobalV8Platform = v8::platform::NewDefaultPlatform();
#endif
                v8::V8::InitializePlatform(Javet::V8Native::GlobalV8Platform.get());
                v8::V8::Initialize();
            }
#ifdef ENABLE_NODE
            auto pageAllocator = Javet::V8Native::GlobalV8Platform->GetPageAllocator();
            LOG_INFO("Calling cppgc::InitializeProcess().");
            cppgc::InitializeProcess(pageAllocator);
#else
            if (!GlobalV8ArrayBufferAllocator) {
                GlobalV8ArrayBufferAllocator = std::shared_ptr<V8ArrayBufferAllocator>();
                GlobalV8ArrayBufferAllocator.reset(V8ArrayBufferAllocator::NewDefaultAllocator());
            }
#endif
            LOG_INFO("V8::Initialize() ends.");
        }
    }
}

