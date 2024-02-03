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

#include "javet_jni.h"

namespace Javet {
    namespace V8ValueMap {
        const jboolean defaultPrimitiveFlags[] = { JNI_FALSE };
        template <typename T>
        T mapGet(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            V8LocalValue v8LocalValue,
            jint v8ValueType,
            jobject key,
            jbooleanArray primitiveFlags,
            T(convert)(JNIEnv* jniEnv, V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue, jbooleanArray primitiveFlags),
            T(fallback)(JNIEnv* jniEnv, V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray primitiveFlags),
            T(except)(JNIEnv* jniEnv, V8Runtime* v8Runtime, const V8LocalContext& v8Context)) {
            if (IS_V8_MAP(v8ValueType)) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
                if (v8LocalValueKey.IsEmpty()) {
                    if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                        return except(jniEnv, v8Runtime, v8Context);
                    }
                }
                else {
                    V8MaybeLocalValue v8MaybeLocalValue = v8LocalValue.As<v8::Map>()->Get(v8Context, v8LocalValueKey);
                    if (v8TryCatch.HasCaught()) {
                        Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                        return except(jniEnv, v8Runtime, v8Context);
                    }
                    if (v8MaybeLocalValue.IsEmpty()) {
                        if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                            return except(jniEnv, v8Runtime, v8Context);
                        }
                    }
                    else {
                        return convert(jniEnv, v8Runtime, v8Context, v8MaybeLocalValue.ToLocalChecked(), primitiveFlags);
                    }
                }
            }
            return fallback(jniEnv, v8Runtime, v8Context, primitiveFlags);
        }

        bool mapSet(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalMap& v8LocalMap,
            const jobject key,
            const V8LocalValue& v8LocalValueValue) {
            V8MaybeBool v8MaybeBool = v8::Just(false);
            V8TryCatch v8TryCatch(v8Context->GetIsolate());
            auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return false;
            }
            if (!v8LocalValueKey.IsEmpty()) {
                auto v8MaybeLocalMap = v8LocalMap->Set(v8Context, v8LocalValueKey, v8LocalValueValue);
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                    return false;
                }
                if (v8MaybeLocalMap.IsEmpty()) {
                    Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
                    return false;
                }
                return true;
            }
            return false;
        }
    }
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_mapCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalMap = v8::Map::New(v8Context->GetIsolate());
    if (!v8LocalMap.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalMap);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_mapAsArray
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalArray = v8LocalValue.As<v8::Map>()->AsArray();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalArray);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_mapClear
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        v8LocalValue.As<v8::Map>()->Clear();
    }
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapDelete
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
        V8MaybeBool v8MaybeBool = v8LocalValue.As<v8::Map>()->Delete(v8Context, v8ValueKey);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        else {
            return v8MaybeBool.FromMaybe(false);
        }
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_mapGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueMap::mapGet<jobject>(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        nullptr,
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue, jbooleanArray mPrimitiveFlags) -> jobject {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValue);
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jobject {
            return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
        },
            [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jobject { return nullptr; });
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueMap::mapGet<jboolean>(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        mPrimitiveFlags,
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue, jbooleanArray mPrimitiveFlags) -> jboolean {
            if (v8LocalValue->IsBoolean() || v8LocalValue->IsBooleanObject()) {
                return v8LocalValue->IsTrue();
            }
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return false;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jboolean {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return false;
        },
            [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jboolean { return false; });
}

JNIEXPORT jdouble JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueMap::mapGet<jdouble>(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        mPrimitiveFlags,
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue, jbooleanArray mPrimitiveFlags) -> jdouble {
            if (v8LocalValue->IsNumber() || v8LocalValue->IsNumberObject()) {
                return v8LocalValue->NumberValue(v8Context).FromMaybe(0);
            }
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jdouble {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return 0;
        },
            [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jdouble { return 0; });
}
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetInteger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueMap::mapGet<jint>(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        mPrimitiveFlags,
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue, jbooleanArray mPrimitiveFlags) -> jint {
            if (v8LocalValue->IsInt32()) {
                return v8LocalValue->Int32Value(v8Context).FromMaybe(0);
            }
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jint {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return 0;
        },
            [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jint { return 0; });
}

JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueMap::mapGet<jlong>(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        mPrimitiveFlags,
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue, jbooleanArray mPrimitiveFlags) -> jlong {
            if (v8LocalValue->IsBigInt() || v8LocalValue->IsBigIntObject()) {
                return v8LocalValue->ToBigInt(v8Context).ToLocalChecked()->Int64Value();
            }
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jlong {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueMap::defaultPrimitiveFlags);
            return 0;
        },
            [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jlong { return 0; });
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetSize
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        return (jint)v8LocalValue.As<v8::Map>()->Size();
    }
    return 0;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueMap::mapGet<jstring>(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        nullptr,
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, const V8LocalValue& v8LocalValue, jbooleanArray mPrimitiveFlags) -> jstring {
            if (v8LocalValue->IsString()) {
                return Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalValue);
            }
            return nullptr;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jstring { return nullptr; },
            [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jstring { return nullptr; });
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapHas
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
        if (v8TryCatch.HasCaught()) {
            Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
            return false;
        }
        if (!v8LocalValueKey.IsEmpty()) {
            V8MaybeBool v8MaybeBool = v8LocalValue.As<v8::Map>()->Has(v8Context, v8LocalValueKey);
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return false;
            }
            if (v8MaybeBool.IsNothing()) {
                Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
                return false;
            }
            return v8MaybeBool.FromMaybe(false);
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray keysAndValues) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto length = jniEnv->GetArrayLength(keysAndValues);
        if (length == 0 || length % 2 != 0) {
            return false;
        }
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        for (int i = 0; i < length; i += 2) {
            auto jobjectValue = jniEnv->GetObjectArrayElement(keysAndValues, i + 1);
            auto v8LocalValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, jobjectValue);
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return false;
            }
            auto jobjectKey = jniEnv->GetObjectArrayElement(keysAndValues, i);
            if (!Javet::V8ValueMap::mapSet(
                jniEnv,
                v8Runtime,
                v8Context,
                v8LocalMap,
                jobjectKey,
                v8LocalValueValue)) {
                return false;
            }
        }
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jboolean value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Boolean(v8Context, value);
        return Javet::V8ValueMap::mapSet(jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jdouble value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Double(v8Context, value);
        return Javet::V8ValueMap::mapSet(jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetInteger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Integer(v8Context, value);
        return Javet::V8ValueMap::mapSet(jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jlong value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Long(v8Context, value);
        return Javet::V8ValueMap::mapSet(jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetNull
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Null(v8Context);
        return Javet::V8ValueMap::mapSet(jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jstring value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = value == nullptr
            ? Javet::Converter::ToV8Null(v8Context).As<v8::Value>()
            : Javet::Converter::ToV8String(jniEnv, v8Context, value).As<v8::Value>();
        return Javet::V8ValueMap::mapSet(jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetUndefined
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Undefined(v8Context);
        return Javet::V8ValueMap::mapSet(jniEnv, v8Runtime, v8Context, v8LocalMap, key, v8LocalValueValue);
    }
    return false;
}
