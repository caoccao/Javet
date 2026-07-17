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

#include "javet_jni.h"
#include "javet_jni_property_accessor.h"

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_mapCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalMap = v8::Map::New(v8Isolate);
    if (!v8LocalMap.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8LocalMap);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_mapAsArray
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalArray = v8LocalValue.As<v8::Map>()->AsArray();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8LocalArray);
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
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Isolate, v8Context, key, keyType);
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
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getMap(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getMapBoolean(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
}

JNIEXPORT jdouble JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getMapDouble(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
}
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetInteger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getMapInteger(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
}

JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_mapGetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getMapLong(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
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
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getMapString(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapHas
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value, jint valueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        V8TryCatch v8TryCatch(v8Isolate);
        auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Isolate, v8Context, value, valueType);
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
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray keysAndValues, jintArray valueTypes) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto length = jniEnv->GetArrayLength(keysAndValues);
        if (length == 0 || length % 2 != 0) {
            return false;
        }
        jint* types = jniEnv->GetIntArrayElements(valueTypes, nullptr);
        if (types == nullptr) {
            return false;
        }
        V8TryCatch v8TryCatch(v8Isolate);
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        for (int i = 0; i < length; i += 2) {
            auto jobjectValue = jniEnv->GetObjectArrayElement(keysAndValues, i + 1);
            auto v8LocalValueValue = Javet::Converter::ToV8Value(
                jniEnv,
                v8Isolate,
                v8Context,
                jobjectValue,
                types[i + 1]);
            if (v8TryCatch.HasCaught()) {
                DELETE_LOCAL_REF(jniEnv, jobjectValue);
                jniEnv->ReleaseIntArrayElements(valueTypes, types, JNI_ABORT);
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return false;
            }
            auto jobjectKey = jniEnv->GetObjectArrayElement(keysAndValues, i);
            const bool success = Javet::PropertyAccessor::setMap(
                jniEnv,
                v8Runtime,
                v8Context,
                v8LocalMap,
                jobjectKey,
                types[i],
                v8LocalValueValue);
            DELETE_LOCAL_REF(jniEnv, jobjectKey);
            DELETE_LOCAL_REF(jniEnv, jobjectValue);
            if (!success) {
                jniEnv->ReleaseIntArrayElements(valueTypes, types, JNI_ABORT);
                return false;
            }
        }
        jniEnv->ReleaseIntArrayElements(valueTypes, types, JNI_ABORT);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jboolean value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Boolean(v8Isolate, value);
        return Javet::PropertyAccessor::setMap(
            jniEnv, v8Runtime, v8Context, v8LocalMap, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jdouble value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Double(v8Isolate, value);
        return Javet::PropertyAccessor::setMap(
            jniEnv, v8Runtime, v8Context, v8LocalMap, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetInteger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jint value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Integer(v8Isolate, value);
        return Javet::PropertyAccessor::setMap(
            jniEnv, v8Runtime, v8Context, v8LocalMap, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jlong value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Long(v8Isolate, value);
        return Javet::PropertyAccessor::setMap(
            jniEnv, v8Runtime, v8Context, v8LocalMap, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetNull
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Null(v8Isolate);
        return Javet::PropertyAccessor::setMap(
            jniEnv, v8Runtime, v8Context, v8LocalMap, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jstring value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = value == nullptr
            ? Javet::Converter::ToV8Null(v8Isolate).As<v8::Value>()
            : Javet::Converter::ToV8String(jniEnv, v8Isolate, value).As<v8::Value>();
        return Javet::PropertyAccessor::setMap(
            jniEnv, v8Runtime, v8Context, v8LocalMap, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_mapSetUndefined
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_MAP(v8ValueType)) {
        auto v8LocalMap = v8LocalValue.As<v8::Map>();
        auto v8LocalValueValue = Javet::Converter::ToV8Undefined(v8Isolate);
        return Javet::PropertyAccessor::setMap(
            jniEnv, v8Runtime, v8Context, v8LocalMap, key, keyType, v8LocalValueValue);
    }
    return false;
}
