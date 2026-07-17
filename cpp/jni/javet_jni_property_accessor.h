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
    namespace PropertyAccessor {
        [[nodiscard]] jobject getMap(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept;

        [[nodiscard]] jboolean getMapBoolean(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jdouble getMapDouble(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jint getMapInteger(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jlong getMapLong(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jstring getMapString(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept;

        [[nodiscard]] jobject getObject(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept;

        [[nodiscard]] jboolean getObjectBoolean(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jdouble getObjectDouble(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jint getObjectInteger(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jlong getObjectLong(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept;

        [[nodiscard]] jstring getObjectString(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept;

        [[nodiscard]] bool setMap(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalMap& v8LocalMap,
            jobject key,
            const V8LocalValue& v8LocalValueValue) noexcept;

        [[nodiscard]] bool setObject(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalObject& v8LocalObject,
            jobject key,
            const V8LocalValue& v8LocalValueValue) noexcept;
    }
}
