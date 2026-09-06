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

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_batchObjectGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType,
    jobjectArray v8ValueKeys, jintArray v8ValueKeyTypes, jobjectArray v8ValueValues, jint length) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        int keyLength = jniEnv->GetArrayLength(v8ValueKeys);
        int valueLength = jniEnv->GetArrayLength(v8ValueValues);
        length = length > keyLength ? keyLength : length;
        length = length > valueLength ? valueLength : length;
        if (length > 0) {
            std::unique_ptr<jint[]> keyTypes(new jint[length]);
            jniEnv->GetIntArrayRegion(v8ValueKeyTypes, 0, length, keyTypes.get());
            if (jniEnv->ExceptionCheck()) {
                return 0;
            }
            auto v8LocalObject = v8LocalValue.As<v8::Object>();
            V8TryCatch v8TryCatch(v8Isolate);
            for (int i = 0; i < length; ++i) {
                V8MaybeLocalValue v8MaybeLocalValueResult;
                auto key = jniEnv->GetObjectArrayElement(v8ValueKeys, i);
                if (keyTypes[i] == static_cast<jint>(Javet::Enums::V8ValueType::Integer)) {
                    jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
                    v8MaybeLocalValueResult = v8LocalObject->Get(v8Context, integerKey);
                }
                else {
                    auto v8LocalValueKey = Javet::Converter::ToV8Value(
                        jniEnv,
                        v8Isolate,
                        v8Context,
                        key,
                        keyTypes[i]);
                    v8MaybeLocalValueResult = v8LocalObject->Get(v8Context, v8LocalValueKey);
                }
                if (v8TryCatch.HasCaught()) {
                    DELETE_LOCAL_REF(jniEnv, key);
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                    return i;
                }
                V8LocalValue v8LocalValueValue;
                if (v8MaybeLocalValueResult.IsEmpty()) {
                    if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                        DELETE_LOCAL_REF(jniEnv, key);
                        return i;
                    }
                }
                else {
                    v8LocalValueValue = v8MaybeLocalValueResult.ToLocalChecked();
                }
                jobject v8Value = Javet::Converter::ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8LocalValueValue);
                if (!jniEnv->ExceptionCheck()) {
                    jniEnv->SetObjectArrayElement(
                        v8ValueValues,
                        i,
                        v8Value);
                }
                DELETE_LOCAL_REF(jniEnv, v8Value);
                DELETE_LOCAL_REF(jniEnv, key);
                if (jniEnv->ExceptionCheck()) {
                    return i;
                }
            }
        }
        return length;
    }
    return 0;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalObject = v8::Object::New(v8Isolate);
    if (!v8LocalObject.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8LocalObject);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectDelete
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8MaybeBool v8MaybeBool = v8::Just(false);
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        if (keyType == static_cast<jint>(Javet::Enums::V8ValueType::Integer)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            v8MaybeBool = v8LocalObject->Delete(v8Context, integerKey);
        }
        else {
            auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Isolate, v8Context, key, keyType);
            v8MaybeBool = v8LocalObject->Delete(v8Context, v8ValueKey);
        }
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        else {
            return v8MaybeBool.FromMaybe(false);
        }
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectDeletePrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Isolate, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Isolate, v8LocalStringKey);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->DeletePrivate(v8Context, v8LocalPrivateKey);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getObject(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getObjectBoolean(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
}

JNIEXPORT jdouble JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getObjectDouble(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetIdentityHash
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        return v8LocalValue.As<v8::Object>()->GetIdentityHash();
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetInteger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getObjectInteger(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
}

JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getObjectLong(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType,
        mPrimitiveFlags);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8TryCatch v8TryCatch(v8Isolate);
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Isolate, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Isolate, v8LocalStringKey);
        auto v8MaybeLocalValue = v8LocalValue.As<v8::Object>()->GetPrivate(v8Context, v8LocalPrivateKey);
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalValue.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getObject(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetOwnPropertyNames
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        auto v8MaybeLocalValue = v8LocalValue->ToObject(v8Context);
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
        }
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8MaybeLocalArray = v8LocalObject->GetOwnPropertyNames(v8Context);
        if (v8MaybeLocalArray.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalArray.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetPropertyNames
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        auto v8MaybeLocalValue = v8LocalValue->ToObject(v8Context);
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
        }
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8MaybeLocalArray = v8LocalObject->GetPropertyNames(v8Context);
        if (v8MaybeLocalArray.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalArray.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetPrototype
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
#ifdef ENABLE_NODE
        V8LocalValue v8LocalValueResult = v8LocalObject->GetPrototypeV2();
#else
        V8LocalValue v8LocalValueResult = v8LocalObject->GetPrototype();
#endif
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8LocalValueResult);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::PropertyAccessor::getObjectString(
        jniEnv,
        v8Runtime,
        v8Context,
        v8LocalValue,
        v8ValueType,
        key,
        keyType);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectHas
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value, jint valueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8TryCatch v8TryCatch(v8Isolate);
        V8MaybeBool v8MaybeBool = v8::Just(false);
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        if (valueType == static_cast<jint>(Javet::Enums::V8ValueType::Integer)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, value);
            v8MaybeBool = v8LocalObject->Has(v8Context, integerKey);
        }
        else {
            auto v8LocalValueKey = Javet::Converter::ToV8Value(
                jniEnv,
                v8Isolate,
                v8Context,
                value,
                valueType);
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return false;
            }
            if (!v8LocalValueKey.IsEmpty()) {
                v8MaybeBool = v8LocalObject->Has(v8Context, v8LocalValueKey);
            }
        }
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
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectHasOwnProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        auto v8MaybeLocalValue = v8LocalValue->ToObject(v8Context);
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return false;
            }
        }
        else {
            v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
        }
    }
    if (v8LocalValue->IsObject()) {
        V8MaybeBool v8MaybeBool = v8::Just(false);
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        if (keyType == static_cast<jint>(Javet::Enums::V8ValueType::Integer)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            v8MaybeBool = v8LocalObject->HasOwnProperty(v8Context, integerKey);
        }
        else if (
            keyType == static_cast<jint>(Javet::Enums::V8ValueType::String) ||
            keyType == static_cast<jint>(Javet::Enums::V8ValueType::Symbol)) {
            auto v8LocalValueKey = Javet::Converter::ToV8Value(
                jniEnv,
                v8Isolate,
                v8Context,
                key,
                keyType);
            if (v8LocalValueKey.IsEmpty()) {
                if (!jniEnv->ExceptionCheck()) {
                    Javet::Exceptions::ThrowJavetConverterException(jniEnv, "Failed to convert the property key.");
                }
                return false;
            }
            v8MaybeBool = v8LocalObject->HasOwnProperty(v8Context, v8LocalValueKey.As<v8::Name>());
        }
        else {
            Javet::Exceptions::ThrowJavetConverterException(
                jniEnv,
                "Property key must be an integer, string, or symbol.");
            return false;
        }
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectHasPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Isolate, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Isolate, v8LocalStringKey);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->HasPrivate(v8Context, v8LocalPrivateKey);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectInvoke
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mFunctionName, jboolean mResultRequired, jobjectArray mValues, jintArray valueTypes) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_SYMBOL(v8ValueType)) {
        auto v8MaybeLocalValue = v8LocalValue->ToObject(v8Context);
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
        }
    }
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8MaybeLocalValue = v8LocalObject->Get(v8Context, Javet::Converter::ToV8String(jniEnv, v8Isolate, mFunctionName));
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
            return nullptr;
        }
        else {
            auto v8Function = v8MaybeLocalValue.ToLocalChecked();
            if (v8Function->IsFunction()) {
                V8TryCatch v8TryCatch(v8Isolate);
                V8MaybeLocalValue v8MaybeLocalValueResult;
                uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
                if (valueCount > 0) {
                    auto v8Values = Javet::Converter::ToV8Values(
                        jniEnv,
                        v8Isolate,
                        v8Context,
                        mValues,
                        valueTypes);
                    v8MaybeLocalValueResult = v8Function.As<v8::Function>()->Call(
                        v8Context,
                        v8LocalObject,
                        valueCount,
                        v8Values.empty() ? nullptr : v8Values.data());
                }
                else {
                    v8MaybeLocalValueResult = v8Function.As<v8::Function>()->Call(v8Context, v8LocalObject, 0, nullptr);
                }
                if (v8TryCatch.HasCaught()) {
                    return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                }
                else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
                    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Isolate, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
                }
            }
            else {
                return nullptr;
            }
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectIsFrozen
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8InternalJSObject = Javet::Converter::ToV8InternalJSObject(v8LocalValue);
        auto elementKind = v8::internal::Cast<V8InternalJSObject>(v8InternalJSObject)->GetElementsKind();
        return v8::internal::IsFrozenElementsKind(elementKind);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectIsSealed
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8InternalJSObject = Javet::Converter::ToV8InternalJSObject(v8LocalValue);
        auto elementKind = v8::internal::Cast<V8InternalJSObject>(v8InternalJSObject)->GetElementsKind();
        return v8::internal::IsSealedElementsKind(elementKind);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray keysAndValues, jintArray valueTypes) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto length = jniEnv->GetArrayLength(keysAndValues);
        if (length == 0 || length % 2 != 0) {
            return false;
        }
        jint* types = jniEnv->GetIntArrayElements(valueTypes, nullptr);
        if (types == nullptr) {
            return false;
        }
        V8TryCatch v8TryCatch(v8Isolate);
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
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
            const bool success = Javet::PropertyAccessor::setObject(
                jniEnv,
                v8Runtime,
                v8Context,
                v8LocalObject,
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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetAccessor
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mPropertyName, jint propertyNameType, jobject mContextGetter, jobject mContextSetter) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8MaybeBool v8MaybeBool = v8::Just(false);
    Javet::Callback::JavetCallbackContextReference* getterCallbackContextReference = nullptr;
    Javet::Callback::JavetCallbackContextReference* setterCallbackContextReference = nullptr;
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        V8LocalName v8LocalName;
        if (propertyNameType == static_cast<jint>(Javet::Enums::V8ValueType::String)) {
            v8LocalName = Javet::Converter::ToV8Value(
                jniEnv,
                v8Isolate,
                v8Context,
                mPropertyName,
                propertyNameType).As<v8::String>();
        }
        else if (propertyNameType == static_cast<jint>(Javet::Enums::V8ValueType::Symbol)) {
            v8LocalName = Javet::Converter::ToV8Value(
                jniEnv,
                v8Isolate,
                v8Context,
                mPropertyName,
                propertyNameType).As<v8::Symbol>();
        }
        else {
            return false;
        }
        if (mContextGetter == nullptr) {
            v8MaybeBool = v8LocalObject.As<v8::Object>()->SetNativeDataProperty(v8Context, v8LocalName, nullptr);
        }
        else {
            auto v8LocalArrayContext = v8::Array::New(v8Isolate, 2);
            getterCallbackContextReference = new Javet::Callback::JavetCallbackContextReference(v8Runtime);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
            auto v8LocalContextGetterHandle = v8::BigInt::New(v8Isolate, TO_NATIVE_INT_64(getterCallbackContextReference));
            getterCallbackContextReference->v8PersistentCallbackContextHandlePointer =
                new V8PersistentBigInt(v8Isolate, v8LocalContextGetterHandle);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
            getterCallbackContextReference->v8PersistentCallbackContextHandlePointer->SetWeak(
                getterCallbackContextReference, Javet::Callback::JavetCloseWeakCallbackContextHandle, v8::WeakCallbackType::kParameter);
            auto maybeResult = v8LocalArrayContext->Set(v8Context, 0, v8LocalContextGetterHandle);
            v8::AccessorNameGetterCallback getter = Javet::Callback::JavetPropertyGetterCallback;
#ifdef ENABLE_NODE
            v8::AccessorNameSetterCallback setter = nullptr;
#else
            v8::AccessorNameSetterCallbackV2 setter = nullptr;
#endif
            if (mContextSetter != nullptr) {
                setterCallbackContextReference = new Javet::Callback::JavetCallbackContextReference(v8Runtime);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
                auto v8LocalContextSetterHandle = v8::BigInt::New(v8Isolate, TO_NATIVE_INT_64(setterCallbackContextReference));
                setterCallbackContextReference->v8PersistentCallbackContextHandlePointer =
                    new V8PersistentBigInt(v8Isolate, v8LocalContextSetterHandle);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
                setterCallbackContextReference->v8PersistentCallbackContextHandlePointer->SetWeak(
                    setterCallbackContextReference, Javet::Callback::JavetCloseWeakCallbackContextHandle, v8::WeakCallbackType::kParameter);
                maybeResult = v8LocalArrayContext->Set(v8Context, 1, v8LocalContextSetterHandle);
                setter = Javet::Callback::JavetPropertySetterCallback;
            }
            v8MaybeBool = v8LocalObject.As<v8::Object>()->SetNativeDataProperty(v8Context, v8LocalName, getter, setter, v8LocalArrayContext);
        }
    }
    const bool success = v8MaybeBool.FromMaybe(false);
    if (success) {
        if (getterCallbackContextReference != nullptr) {
            getterCallbackContextReference->SetHandle(jniEnv, mContextGetter);
        }
        if (setterCallbackContextReference != nullptr) {
            setterCallbackContextReference->SetHandle(jniEnv, mContextSetter);
        }
    }
    else {
        if (getterCallbackContextReference != nullptr) {
            delete getterCallbackContextReference;
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteJavetCallbackContextReference);
        }
        if (setterCallbackContextReference != nullptr) {
            delete setterCallbackContextReference;
            INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteJavetCallbackContextReference);
        }
    }
    if (v8MaybeBool.IsNothing()) {
        Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
    }
    return success;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jboolean value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Boolean(v8Isolate, value);
        return Javet::PropertyAccessor::setObject(
            jniEnv, v8Runtime, v8Context, v8LocalObject, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jdouble value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Double(v8Isolate, value);
        return Javet::PropertyAccessor::setObject(
            jniEnv, v8Runtime, v8Context, v8LocalObject, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetInteger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jint value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Integer(v8Isolate, value);
        return Javet::PropertyAccessor::setObject(
            jniEnv, v8Runtime, v8Context, v8LocalObject, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jlong value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Long(v8Isolate, value);
        return Javet::PropertyAccessor::setObject(
            jniEnv, v8Runtime, v8Context, v8LocalObject, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetNull
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Null(v8Isolate);
        return Javet::PropertyAccessor::setObject(
            jniEnv, v8Runtime, v8Context, v8LocalObject, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey, jobject mValue, jint valueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Isolate, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Isolate, v8LocalStringKey);
        auto v8LocalPrivateValue = Javet::Converter::ToV8Value(
            jniEnv,
            v8Isolate,
            v8Context,
            mValue,
            valueType);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->SetPrivate(v8Context, v8LocalPrivateKey, v8LocalPrivateValue);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jobject value, jint valueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Value(
            jniEnv,
            v8Isolate,
            v8Context,
            value,
            valueType);
        return Javet::PropertyAccessor::setObject(
            jniEnv,
            v8Runtime,
            v8Context,
            v8LocalObject,
            key,
            keyType,
            v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetPrototype
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueHandlePrototype) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8PersistentObjectPrototypePointer = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandlePrototype);
        auto v8LocalObjectPrototype = v8PersistentObjectPrototypePointer->Get(v8Isolate);
#ifdef ENABLE_NODE
        auto v8MaybeBool = v8LocalObject->SetPrototypeV2(v8Context, v8LocalObjectPrototype);
#else
        auto v8MaybeBool = v8LocalObject->SetPrototype(v8Context, v8LocalObjectPrototype);
#endif
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType, jstring value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = value == nullptr
            ? Javet::Converter::ToV8Null(v8Isolate).As<v8::Value>()
            : Javet::Converter::ToV8String(jniEnv, v8Isolate, value).As<v8::Value>();
        return Javet::PropertyAccessor::setObject(
            jniEnv, v8Runtime, v8Context, v8LocalObject, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetUndefined
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint keyType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Undefined(v8Isolate);
        return Javet::PropertyAccessor::setObject(
            jniEnv, v8Runtime, v8Context, v8LocalObject, key, keyType, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_objectToProtoString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8MaybeLocalString v8MaybeLocalString;
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        v8MaybeLocalString = v8LocalObject->ObjectProtoToString(v8Context);
        if (v8MaybeLocalString.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
    }
    V8LocalString v8LocalString = v8MaybeLocalString.IsEmpty() ? V8LocalString() : v8MaybeLocalString.ToLocalChecked();
    return Javet::Converter::ToJavaStringFromV8String(jniEnv, v8Isolate, v8LocalString);
}
