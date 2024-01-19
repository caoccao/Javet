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

#include <jni.h>
#include "javet_v8.h"

namespace Javet {
    namespace Callback {
        class JavetCallbackContextReference;
        class V8ValueReference;

        void Initialize(JNIEnv* jniEnv) noexcept;

        void JavetCloseWeakCallbackContextHandle(const v8::WeakCallbackInfo<JavetCallbackContextReference>& info) noexcept;
        void JavetCloseWeakDataReference(const v8::WeakCallbackInfo<V8ValueReference>& info) noexcept;
        void JavetFunctionCallback(const v8::FunctionCallbackInfo<v8::Value>& info) noexcept;
        void JavetGCEpilogueCallback(
            v8::Isolate* v8Isolate,
            v8::GCType v8GCType,
            v8::GCCallbackFlags v8GCCallbackFlags) noexcept;
        void JavetGCPrologueCallback(
            v8::Isolate* v8Isolate,
            v8::GCType v8GCType,
            v8::GCCallbackFlags v8GCCallbackFlags) noexcept;
        V8MaybeLocalModule JavetModuleResolveCallback(
            V8LocalContext v8Context,
            V8LocalString specifier,
            V8LocalFixedArray importAssertions,
            V8LocalModule referrer) noexcept;
        void JavetPropertyGetterCallback(
            V8LocalName propertyName,
            const v8::PropertyCallbackInfo<v8::Value>& info) noexcept;
        void JavetPropertySetterCallback(
            V8LocalName propertyName,
            V8LocalValue propertyValue,
            const v8::PropertyCallbackInfo<void>& info) noexcept;
#ifndef ENABLE_NODE
        void OOMErrorCallback(const char* location, const v8::OOMDetails& oomDetails) noexcept;
#endif
        void JavetPromiseRejectCallback(v8::PromiseRejectMessage message) noexcept;
        V8MaybeLocalValue JavetSyntheticModuleEvaluationStepsCallback(
            V8LocalContext v8Context,
            V8LocalModule v8LocalModule);

        class JavetCallbackContextReference {
        public:
            V8PersistentBigInt* v8PersistentCallbackContextHandlePointer;
            JavetCallbackContextReference(JNIEnv* jniEnv, const jobject callbackContext) noexcept;
            void CallFunction(const v8::FunctionCallbackInfo<v8::Value>& args) noexcept;
            void CallPropertyGetter(
                const V8LocalName& propertyName,
                const v8::PropertyCallbackInfo<v8::Value>& args) noexcept;
            void CallPropertySetter(
                const V8LocalName& propertyName,
                const V8LocalValue& propertyValue,
                const v8::PropertyCallbackInfo<void>& args) noexcept;
            void RemoveCallbackContext(const jobject externalV8Runtime) noexcept;
            virtual ~JavetCallbackContextReference();
        };

        class V8ValueReference {
        public:
            jobject objectReference;
            V8PersistentData* v8PersistentDataPointer;
            V8ValueReference(JNIEnv* jniEnv, const jobject objectReference) noexcept;
            void Clear() noexcept;
            void Close() noexcept;
        };
    }
}
