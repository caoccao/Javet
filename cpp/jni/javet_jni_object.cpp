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
    namespace V8ValueObject {
        const jboolean defaultPrimitiveFlags[] = { JNI_FALSE };

        template <typename T>
        T objectGet(
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
            if (IS_V8_SYMBOL(v8ValueType)) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                auto v8MaybeLocalValue = v8LocalValue->ToObject(v8Context);
                if (v8MaybeLocalValue.IsEmpty()) {
                    if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                        return except(jniEnv, v8Runtime, v8Context);
                    }
                }
                else {
                    v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
                }
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                    return except(jniEnv, v8Runtime, v8Context);
                }
            }
            if (v8LocalValue->IsObject()) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
                if (v8LocalValueKey.IsEmpty()) {
                    if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                        return except(jniEnv, v8Runtime, v8Context);
                    }
                }
                else {
                    auto v8LocalObject = v8LocalValue.As<v8::Object>();
                    V8MaybeLocalValue v8MaybeLocalValue;
                    if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
                        jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
                        v8MaybeLocalValue = v8LocalObject->Get(v8Context, integerKey);
                    }
                    else {
                        v8MaybeLocalValue = v8LocalObject->Get(v8Context, v8LocalValueKey);
                    }
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

        bool objectSet(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalObject& v8LocalObject,
            const jobject key,
            const V8LocalValue& v8LocalValueValue) {
            V8MaybeBool v8MaybeBool = v8::Just(false);
            V8TryCatch v8TryCatch(v8Context->GetIsolate());
            if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
                jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
                v8MaybeBool = v8LocalObject->Set(v8Context, integerKey, v8LocalValueValue);
            }
            else {
                auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                    return false;
                }
                if (!v8ValueKey.IsEmpty()) {
                    v8MaybeBool = v8LocalObject->Set(v8Context, v8ValueKey, v8LocalValueValue);
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
    }
}

JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_batchObjectGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType,
    jobjectArray v8ValueKeys, jobjectArray v8ValueValues, jint length) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        int keyLength = jniEnv->GetArrayLength(v8ValueKeys);
        length = length > keyLength ? keyLength : length;
        if (length > 0) {
            auto v8LocalObject = v8LocalValue.As<v8::Object>();
            V8TryCatch v8TryCatch(v8Context->GetIsolate());
            for (int i = 0; i < length; ++i) {
                V8MaybeLocalValue v8MaybeLocalValueResult;
                auto key = jniEnv->GetObjectArrayElement(v8ValueKeys, i);
                if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
                    jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
                    v8MaybeLocalValueResult = v8LocalObject->Get(v8Context, integerKey);
                }
                else {
                    auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
                    v8MaybeLocalValueResult = v8LocalObject->Get(v8Context, v8LocalValueKey);
                }
                if (v8TryCatch.HasCaught()) {
                    Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                    return i;
                }
                V8LocalValue v8LocalValueValue;
                if (v8MaybeLocalValueResult.IsEmpty()) {
                    if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                        return i;
                    }
                }
                else {
                    v8LocalValueValue = v8MaybeLocalValueResult.ToLocalChecked();
                }
                jniEnv->SetObjectArrayElement(
                    v8ValueValues,
                    i,
                    Javet::Converter::ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8LocalValueValue));
            }
        }
        return length;
    }
    return 0;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectCreate
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle) {
    RUNTIME_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle);
    auto v8LocalObject = v8::Object::New(v8Context->GetIsolate());
    if (!v8LocalObject.IsEmpty()) {
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalObject);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectDelete
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8MaybeBool v8MaybeBool = v8::Just(false);
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            v8MaybeBool = v8LocalObject->Delete(v8Context, integerKey);
        }
        else {
            auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
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
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->DeletePrivate(v8Context, v8LocalPrivateKey);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueObject::objectGet<jobject>(
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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueObject::objectGet<jboolean>(
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
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return false;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jboolean {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return false;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jboolean { return false; });
}

JNIEXPORT jdouble JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueObject::objectGet<jdouble>(
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
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jdouble {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jdouble { return 0; });
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
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueObject::objectGet<jint>(
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
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jint {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jint { return 0; });
}

JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jbooleanArray mPrimitiveFlags) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueObject::objectGet<jlong>(
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
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context, jbooleanArray mPrimitiveFlags) -> jlong {
            jniEnv->SetBooleanArrayRegion(mPrimitiveFlags, 0, 1, Javet::V8ValueObject::defaultPrimitiveFlags);
            return 0;
        },
        [](JNIEnv* jniEnv, Javet::V8Runtime* v8Runtime, const V8LocalContext& v8Context) -> jlong { return 0; });
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
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
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValue.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
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
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        V8MaybeLocalValue v8MaybeLocalValueValue;
        if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            v8MaybeLocalValueValue = v8LocalObject->Get(v8Context, integerKey);
        }
        else {
            auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
            if (v8ValueKey.IsEmpty()) {
                if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                    return nullptr;
                }
            }
            else {
                v8MaybeLocalValueValue = v8LocalObject->Get(v8Context, v8ValueKey);
            }
        }
        if (v8TryCatch.HasCaught()) {
            return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
        }
        if (v8MaybeLocalValueValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
        }
        else {
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueValue.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
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
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalArray.ToLocalChecked());
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
            return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalArray.ToLocalChecked());
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetPrototype
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        V8LocalValue v8LocalValueResult = v8LocalObject->GetPrototype();
        return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8LocalValueResult);
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_objectGetString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    return Javet::V8ValueObject::objectGet<jstring>(
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

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectHas
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        V8MaybeBool v8MaybeBool = v8::Just(false);
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        if (Javet::Converter::IsV8ValueInteger(jniEnv, value)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, value);
            v8MaybeBool = v8LocalObject->Has(v8Context, integerKey);
        }
        else {
            auto v8LocalValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
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
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
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
        if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            v8MaybeBool = v8LocalObject->HasOwnProperty(v8Context, integerKey);
        }
        else if (Javet::Converter::IsV8ValueString(jniEnv, key)) {
            jstring stringKey = Javet::Converter::ToJavaStringFromV8ValueString(jniEnv, key);
            auto v8ValueKey = Javet::Converter::ToV8String(jniEnv, v8Context, stringKey);
            v8MaybeBool = v8LocalObject->HasOwnProperty(v8Context, v8ValueKey);
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
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->HasPrivate(v8Context, v8LocalPrivateKey);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_objectInvoke
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mFunctionName, jboolean mResultRequired, jobjectArray mValues) {
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
        auto v8MaybeLocalValue = v8LocalObject->Get(v8Context, Javet::Converter::ToV8String(jniEnv, v8Context, mFunctionName));
        if (v8MaybeLocalValue.IsEmpty()) {
            if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                return nullptr;
            }
            return nullptr;
        }
        else {
            auto v8Function = v8MaybeLocalValue.ToLocalChecked();
            if (v8Function->IsFunction()) {
                V8TryCatch v8TryCatch(v8Context->GetIsolate());
                V8MaybeLocalValue v8MaybeLocalValueResult;
                uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
                if (valueCount > 0) {
                    auto umValuesPointer = Javet::Converter::ToV8Values(jniEnv, v8Context, mValues);
                    v8MaybeLocalValueResult = v8Function.As<v8::Function>()->Call(v8Context, v8LocalObject, valueCount, umValuesPointer.get());
                }
                else {
                    v8MaybeLocalValueResult = v8Function.As<v8::Function>()->Call(v8Context, v8LocalObject, 0, nullptr);
                }
                if (v8TryCatch.HasCaught()) {
                    return Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                }
                else if (mResultRequired && !v8MaybeLocalValueResult.IsEmpty()) {
                    return v8Runtime->SafeToExternalV8Value(jniEnv, v8Context, v8MaybeLocalValueResult.ToLocalChecked());
                }
            }
            else {
                return nullptr;
            }
        }
    }
    return Javet::Converter::ToExternalV8ValueUndefined(jniEnv, v8Runtime);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSet
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobjectArray keysAndValues) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto length = jniEnv->GetArrayLength(keysAndValues);
        if (length == 0 || length % 2 != 0) {
            return false;
        }
        V8TryCatch v8TryCatch(v8Context->GetIsolate());
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        for (int i = 0; i < length; i += 2) {
            auto jobjectValue = jniEnv->GetObjectArrayElement(keysAndValues, i + 1);
            auto v8LocalValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, jobjectValue);
            if (v8TryCatch.HasCaught()) {
                Javet::Exceptions::ThrowJavetExecutionException(jniEnv, v8Runtime, v8Context, v8TryCatch);
                return false;
            }
            auto jobjectKey = jniEnv->GetObjectArrayElement(keysAndValues, i);
            if (!Javet::V8ValueObject::objectSet(
                jniEnv,
                v8Runtime,
                v8Context,
                v8LocalObject,
                jobjectKey,
                v8LocalValueValue)) {
                return false;
            }
        }
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetAccessor
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject mPropertyName, jobject mContextGetter, jobject mContextSetter) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    V8MaybeBool v8MaybeBool = v8::Just(false);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        V8LocalName v8LocalName;
        if (Javet::Converter::IsV8ValueString(jniEnv, mPropertyName)) {
            v8LocalName = Javet::Converter::ToV8Value(jniEnv, v8Context, mPropertyName).As<v8::String>();
        }
        else if (Javet::Converter::IsV8ValueSymbol(jniEnv, mPropertyName)) {
            v8LocalName = Javet::Converter::ToV8Value(jniEnv, v8Context, mPropertyName).As<v8::Symbol>();
        }
        else {
            return false;
        }
        if (mContextGetter == nullptr) {
            v8MaybeBool = v8LocalObject.As<v8::Object>()->SetAccessor(v8Context, v8LocalName, nullptr);
        }
        else {
            auto v8LocalArrayContext = v8::Array::New(v8Context->GetIsolate(), 2);
            auto javetCallbackContextReferencePointer = new Javet::Callback::JavetCallbackContextReference(jniEnv, mContextGetter);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
            auto v8LocalContextGetterHandle = v8::BigInt::New(v8Context->GetIsolate(), TO_NATIVE_INT_64(javetCallbackContextReferencePointer));
            javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer =
                new V8PersistentBigInt(v8Context->GetIsolate(), v8LocalContextGetterHandle);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
            javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer->SetWeak(
                javetCallbackContextReferencePointer, Javet::Callback::JavetCloseWeakCallbackContextHandle, v8::WeakCallbackType::kParameter);
            auto maybeResult = v8LocalArrayContext->Set(v8Context, 0, v8LocalContextGetterHandle);
            v8::AccessorNameGetterCallback getter = Javet::Callback::JavetPropertyGetterCallback;
            v8::AccessorNameSetterCallback setter = nullptr;
            if (mContextSetter != nullptr) {
                javetCallbackContextReferencePointer = new Javet::Callback::JavetCallbackContextReference(jniEnv, mContextSetter);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewJavetCallbackContextReference);
                auto v8LocalContextSetterHandle = v8::BigInt::New(v8Context->GetIsolate(), TO_NATIVE_INT_64(javetCallbackContextReferencePointer));
                javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer =
                    new V8PersistentBigInt(v8Context->GetIsolate(), v8LocalContextSetterHandle);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentCallbackContextReference);
                javetCallbackContextReferencePointer->v8PersistentCallbackContextHandlePointer->SetWeak(
                    javetCallbackContextReferencePointer, Javet::Callback::JavetCloseWeakCallbackContextHandle, v8::WeakCallbackType::kParameter);
                maybeResult = v8LocalArrayContext->Set(v8Context, 1, v8LocalContextSetterHandle);
                setter = Javet::Callback::JavetPropertySetterCallback;
            }
            v8MaybeBool = v8LocalObject.As<v8::Object>()->SetAccessor(v8Context, v8LocalName, getter, setter, v8LocalArrayContext);
        }
    }
    if (v8MaybeBool.IsNothing()) {
        Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
    }
    return v8MaybeBool.FromMaybe(false);
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetBoolean
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jboolean value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Boolean(v8Context, value);
        return Javet::V8ValueObject::objectSet(jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetDouble
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jdouble value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Double(v8Context, value);
        return Javet::V8ValueObject::objectSet(jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetInteger
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jint value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Integer(v8Context, value);
        return Javet::V8ValueObject::objectSet(jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetLong
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jlong value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (IS_V8_OBJECT(v8ValueType)) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Long(v8Context, value);
        return Javet::V8ValueObject::objectSet(jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetNull
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Null(v8Context);
        return Javet::V8ValueObject::objectSet(jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetPrivateProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jstring mKey, jobject mValue) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalStringKey = Javet::Converter::ToV8String(jniEnv, v8Context, mKey);
        auto v8LocalPrivateKey = v8::Private::ForApi(v8Context->GetIsolate(), v8LocalStringKey);
        auto v8LocalPrivateValue = Javet::Converter::ToV8Value(jniEnv, v8Context, mValue);
        auto v8MaybeBool = v8LocalValue.As<v8::Object>()->SetPrivate(v8Context, v8LocalPrivateKey, v8LocalPrivateValue);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetProperty
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jobject value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        V8MaybeBool v8MaybeBool = v8::Just(false);
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8ValueValue = Javet::Converter::ToV8Value(jniEnv, v8Context, value);
        if (Javet::Converter::IsV8ValueInteger(jniEnv, key)) {
            jint integerKey = Javet::Converter::ToJavaIntegerFromV8ValueInteger(jniEnv, key);
            v8MaybeBool = v8LocalObject->Set(v8Context, integerKey, v8ValueValue);
        }
        else {
            auto v8ValueKey = Javet::Converter::ToV8Value(jniEnv, v8Context, key);
            if (!v8ValueKey.IsEmpty()) {
                v8MaybeBool = v8LocalObject->Set(v8Context, v8ValueKey, v8ValueValue);
            }
        }
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetPrototype
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jlong v8ValueHandlePrototype) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8PersistentObjectPrototypePointer = TO_V8_PERSISTENT_VALUE_POINTER(v8ValueHandlePrototype);
        auto v8LocalObjectPrototype = v8PersistentObjectPrototypePointer->Get(v8Context->GetIsolate());
        auto v8MaybeBool = v8LocalObject->SetPrototype(v8Context, v8LocalObjectPrototype);
        if (v8MaybeBool.IsNothing()) {
            Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context);
        }
        return v8MaybeBool.FromMaybe(false);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetString
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key, jstring value) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = value == nullptr
            ? Javet::Converter::ToV8Null(v8Context).As<v8::Value>()
            : Javet::Converter::ToV8String(jniEnv, v8Context, value).As<v8::Value>();
        return Javet::V8ValueObject::objectSet(jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_objectSetUndefined
(JNIEnv* jniEnv, jobject caller, jlong v8RuntimeHandle, jlong v8ValueHandle, jint v8ValueType, jobject key) {
    RUNTIME_AND_VALUE_HANDLES_TO_OBJECTS_WITH_SCOPE(v8RuntimeHandle, v8ValueHandle);
    if (v8LocalValue->IsObject()) {
        auto v8LocalObject = v8LocalValue.As<v8::Object>();
        auto v8LocalValueValue = Javet::Converter::ToV8Undefined(v8Context);
        return Javet::V8ValueObject::objectSet(jniEnv, v8Runtime, v8Context, v8LocalObject, key, v8LocalValueValue);
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
    return Javet::Converter::ToJavaString(jniEnv, v8Context, v8LocalString);
}
