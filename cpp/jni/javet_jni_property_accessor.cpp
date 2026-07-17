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

#include "javet_converter.h"
#include "javet_enums.h"
#include "javet_exceptions.h"
#include "javet_jni_property_accessor.h"

namespace Javet {
    namespace PropertyAccessor {
        namespace {
            constexpr jboolean defaultPrimitiveFlags[] = { JNI_FALSE };

            struct ObjectAccessor final {
                using Target = V8LocalObject;

                [[nodiscard]] static V8MaybeLocalValue get(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const V8LocalValue& v8LocalValue,
                    jobject key) noexcept {
                    auto v8LocalObject = v8LocalValue.As<v8::Object>();
                    if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
                        const jint integerKey =
                            Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
                        return v8LocalObject->Get(v8Context, integerKey);
                    }
                    auto v8LocalValueKey = Javet::Converter::ToV8Value(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        v8Context,
                        key);
                    if (v8LocalValueKey.IsEmpty()) {
                        return V8MaybeLocalValue();
                    }
                    return v8LocalObject->Get(v8Context, v8LocalValueKey);
                }

                [[nodiscard]] static bool prepareForGet(
                    const V8LocalContext& v8Context,
                    V8LocalValue& v8LocalValue,
                    jint v8ValueType) noexcept {
                    if (IS_V8_SYMBOL(v8ValueType)) {
                        auto v8MaybeLocalValue = v8LocalValue->ToObject(v8Context);
                        if (v8MaybeLocalValue.IsEmpty()) {
                            return false;
                        }
                        v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
                    }
                    return v8LocalValue->IsObject();
                }

                [[nodiscard]] static V8MaybeBool set(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const Target& v8LocalObject,
                    jobject key,
                    const V8LocalValue& v8LocalValueValue) noexcept {
                    if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
                        const jint integerKey =
                            Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
                        return v8LocalObject->Set(v8Context, integerKey, v8LocalValueValue);
                    }
                    auto v8LocalValueKey = Javet::Converter::ToV8Value(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        v8Context,
                        key);
                    if (v8LocalValueKey.IsEmpty()) {
                        return v8::Just(false);
                    }
                    return v8LocalObject->Set(v8Context, v8LocalValueKey, v8LocalValueValue);
                }
            };

            struct MapAccessor final {
                using Target = V8LocalMap;

                [[nodiscard]] static V8MaybeLocalValue get(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const V8LocalValue& v8LocalValue,
                    jobject key) noexcept {
                    auto v8LocalValueKey = Javet::Converter::ToV8Value(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        v8Context,
                        key);
                    if (v8LocalValueKey.IsEmpty()) {
                        return V8MaybeLocalValue();
                    }
                    return v8LocalValue.As<v8::Map>()->Get(v8Context, v8LocalValueKey);
                }

                [[nodiscard]] static bool prepareForGet(
                    const V8LocalContext&,
                    V8LocalValue&,
                    jint v8ValueType) noexcept {
                    return IS_V8_MAP(v8ValueType);
                }

                [[nodiscard]] static V8MaybeBool set(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const Target& v8LocalMap,
                    jobject key,
                    const V8LocalValue& v8LocalValueValue) noexcept {
                    auto v8LocalValueKey = Javet::Converter::ToV8Value(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        v8Context,
                        key);
                    if (v8LocalValueKey.IsEmpty()) {
                        return v8::Just(false);
                    }
                    auto v8MaybeLocalMap = v8LocalMap->Set(
                        v8Context,
                        v8LocalValueKey,
                        v8LocalValueValue);
                    if (v8MaybeLocalMap.IsEmpty()) {
                        return v8::Nothing<bool>();
                    }
                    return v8::Just(true);
                }
            };

            template<typename Result>
            struct PrimitiveResultConverter {
                [[nodiscard]] static Result exception(
                    JNIEnv*,
                    V8Runtime*,
                    const V8LocalContext&) noexcept {
                    return static_cast<Result>(0);
                }

                [[nodiscard]] static Result fallback(
                    JNIEnv* jniEnv,
                    V8Runtime*,
                    const V8LocalContext&,
                    jbooleanArray primitiveFlags) noexcept {
                    jniEnv->SetBooleanArrayRegion(
                        primitiveFlags,
                        0,
                        1,
                        defaultPrimitiveFlags);
                    return static_cast<Result>(0);
                }
            };

            template<typename Result>
            struct ResultConverter;

            template<>
            struct ResultConverter<jobject> final {
                [[nodiscard]] static jobject convert(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const V8LocalValue& v8LocalValue,
                    jbooleanArray) noexcept {
                    return v8Runtime->SafeToExternalV8Value(
                        jniEnv,
                        v8Runtime->v8Isolate,
                        v8Context,
                        v8LocalValue);
                }

                [[nodiscard]] static jobject exception(
                    JNIEnv*,
                    V8Runtime*,
                    const V8LocalContext&) noexcept {
                    return nullptr;
                }

                [[nodiscard]] static jobject fallback(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext&,
                    jbooleanArray) noexcept {
                    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
                }
            };

            template<>
            struct ResultConverter<jboolean> final : PrimitiveResultConverter<jboolean> {
                [[nodiscard]] static jboolean convert(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const V8LocalValue& v8LocalValue,
                    jbooleanArray primitiveFlags) noexcept {
                    jboolean booleanValue = false;
                    if (Javet::Converter::ToJavaBoolean(v8LocalValue, booleanValue)) {
                        return booleanValue;
                    }
                    return fallback(jniEnv, v8Runtime, v8Context, primitiveFlags);
                }
            };

            template<>
            struct ResultConverter<jdouble> final : PrimitiveResultConverter<jdouble> {
                [[nodiscard]] static jdouble convert(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const V8LocalValue& v8LocalValue,
                    jbooleanArray primitiveFlags) noexcept {
                    if (v8LocalValue->IsNumber() || v8LocalValue->IsNumberObject()) {
                        return v8LocalValue->NumberValue(v8Context).FromMaybe(0);
                    }
                    return fallback(jniEnv, v8Runtime, v8Context, primitiveFlags);
                }
            };

            template<>
            struct ResultConverter<jint> final : PrimitiveResultConverter<jint> {
                [[nodiscard]] static jint convert(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const V8LocalValue& v8LocalValue,
                    jbooleanArray primitiveFlags) noexcept {
                    if (v8LocalValue->IsInt32()) {
                        return v8LocalValue->Int32Value(v8Context).FromMaybe(0);
                    }
                    return fallback(jniEnv, v8Runtime, v8Context, primitiveFlags);
                }
            };

            template<>
            struct ResultConverter<jlong> final : PrimitiveResultConverter<jlong> {
                [[nodiscard]] static jlong convert(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext& v8Context,
                    const V8LocalValue& v8LocalValue,
                    jbooleanArray primitiveFlags) noexcept {
                    if (v8LocalValue->IsBigInt() || v8LocalValue->IsBigIntObject()) {
                        return v8LocalValue->ToBigInt(v8Context).ToLocalChecked()->Int64Value();
                    }
                    return fallback(jniEnv, v8Runtime, v8Context, primitiveFlags);
                }
            };

            template<>
            struct ResultConverter<jstring> final {
                [[nodiscard]] static jstring convert(
                    JNIEnv* jniEnv,
                    V8Runtime* v8Runtime,
                    const V8LocalContext&,
                    const V8LocalValue& v8LocalValue,
                    jbooleanArray) noexcept {
                    if (v8LocalValue->IsString()) {
                        return Javet::Converter::ToJavaStringFromV8String(
                            jniEnv,
                            v8Runtime->v8Isolate,
                            v8LocalValue);
                    }
                    return nullptr;
                }

                [[nodiscard]] static jstring exception(
                    JNIEnv*,
                    V8Runtime*,
                    const V8LocalContext&) noexcept {
                    return nullptr;
                }

                [[nodiscard]] static jstring fallback(
                    JNIEnv*,
                    V8Runtime*,
                    const V8LocalContext&,
                    jbooleanArray) noexcept {
                    return nullptr;
                }
            };

            template<typename Result, typename Accessor>
            [[nodiscard]] Result getConverted(
                JNIEnv* jniEnv,
                V8Runtime* v8Runtime,
                const V8LocalContext& v8Context,
                V8LocalValue v8LocalValue,
                jint v8ValueType,
                jobject key,
                jbooleanArray primitiveFlags) noexcept {
                V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
                if (Accessor::prepareForGet(v8Context, v8LocalValue, v8ValueType)) {
                    auto v8MaybeLocalValue = Accessor::get(
                        jniEnv,
                        v8Runtime,
                        v8Context,
                        v8LocalValue,
                        key);
                    if (v8TryCatch.HasCaught()) {
                        Javet::Exceptions::ThrowJavetExecutionException(
                            jniEnv,
                            v8Runtime,
                            v8Context,
                            v8TryCatch);
                        return ResultConverter<Result>::exception(jniEnv, v8Runtime, v8Context);
                    }
                    if (v8MaybeLocalValue.IsEmpty()) {
                        if (Javet::Exceptions::HandlePendingException(
                            jniEnv,
                            v8Runtime,
                            v8Context)) {
                            return ResultConverter<Result>::exception(jniEnv, v8Runtime, v8Context);
                        }
                    }
                    else {
                        return ResultConverter<Result>::convert(
                            jniEnv,
                            v8Runtime,
                            v8Context,
                            v8MaybeLocalValue.ToLocalChecked(),
                            primitiveFlags);
                    }
                }
                else {
                    if (v8TryCatch.HasCaught()) {
                        Javet::Exceptions::ThrowJavetExecutionException(
                            jniEnv,
                            v8Runtime,
                            v8Context,
                            v8TryCatch);
                        return ResultConverter<Result>::exception(jniEnv, v8Runtime, v8Context);
                    }
                    if (Javet::Exceptions::HandlePendingException(
                        jniEnv,
                        v8Runtime,
                        v8Context)) {
                        return ResultConverter<Result>::exception(jniEnv, v8Runtime, v8Context);
                    }
                }
                return ResultConverter<Result>::fallback(
                    jniEnv,
                    v8Runtime,
                    v8Context,
                    primitiveFlags);
            }

            template<typename Accessor>
            [[nodiscard]] bool setConverted(
                JNIEnv* jniEnv,
                V8Runtime* v8Runtime,
                const V8LocalContext& v8Context,
                const typename Accessor::Target& target,
                jobject key,
                const V8LocalValue& v8LocalValueValue) noexcept {
                V8TryCatch v8TryCatch(v8Runtime->v8Isolate);
                auto v8MaybeBool = Accessor::set(
                    jniEnv,
                    v8Runtime,
                    v8Context,
                    target,
                    key,
                    v8LocalValueValue);
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(
                        jniEnv,
                        v8Runtime,
                        v8Context,
                        v8TryCatch);
                    return false;
                }
                if (v8MaybeBool.IsNothing()) {
                    Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
                    return false;
                }
                return v8MaybeBool.FromMaybe(false);
            }
        }

        jobject getMap(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept {
            return getConverted<jobject, MapAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, nullptr);
        }

        jboolean getMapBoolean(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jboolean, MapAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jdouble getMapDouble(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jdouble, MapAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jint getMapInteger(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jint, MapAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jlong getMapLong(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jlong, MapAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jstring getMapString(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept {
            return getConverted<jstring, MapAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, nullptr);
        }

        jobject getObject(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept {
            return getConverted<jobject, ObjectAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, nullptr);
        }

        jboolean getObjectBoolean(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jboolean, ObjectAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jdouble getObjectDouble(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jdouble, ObjectAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jint getObjectInteger(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jint, ObjectAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jlong getObjectLong(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags) noexcept {
            return getConverted<jlong, ObjectAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, primitiveFlags);
        }

        jstring getObjectString(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key) noexcept {
            return getConverted<jstring, ObjectAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalValue, v8ValueType, key, nullptr);
        }

        bool setMap(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalMap& v8LocalMap,
            jobject key,
            const V8LocalValue& v8LocalValueValue) noexcept {
            return setConverted<MapAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
        }

        bool setObject(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalObject& v8LocalObject,
            jobject key,
            const V8LocalValue& v8LocalValueValue) noexcept {
            return setConverted<ObjectAccessor>(
                jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
        }
    }
}
