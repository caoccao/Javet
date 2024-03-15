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
#include "javet_constants.h"
#include "javet_monitor.h"
#include "javet_native.h"
#include "javet_v8.h"
#include "javet_v8_internal.h"
#include "javet_v8_runtime.h"

namespace Javet {
    namespace Converter {
        // extern
        extern jclass jclassV8ValueInteger;
        extern jmethodID jmethodIDV8ValueIntegerToPrimitive;

        extern jclass jclassV8ValueString;
        extern jmethodID jmethodIDV8ValueStringToPrimitive;

        extern jclass jclassV8ValueSymbol;

        extern jclass jclassByteBuffer;
        extern jclass jclassString;

        extern jclass jclassIV8ValueFunctionScriptSource;
        extern jmethodID jmethodIDIV8ValueFunctionScriptSourceConstructor;
        extern jmethodID jmethodIDIV8ValueFunctionScriptGetCode;
        extern jmethodID jmethodIDIV8ValueFunctionScriptGetEndPosition;
        extern jmethodID jmethodIDIV8ValueFunctionScriptGetStartPosition;

        template<typename T1, typename T2>
        constexpr auto IsJavaByteBuffer(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassByteBuffer);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueInteger(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueInteger);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueString(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueString);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueSymbol(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueSymbol);
        }

        template<typename T1, typename T2>
        constexpr auto ToJavaIntegerFromV8ValueInteger(T1 jniEnv, T2 obj) {
            return jniEnv->CallIntMethod(obj, jmethodIDV8ValueIntegerToPrimitive);
        }

        template<typename T1, typename T2>
        constexpr auto ToJavaStringFromV8ValueString(T1 jniEnv, T2 obj) {
            return (jstring)jniEnv->CallObjectMethod(obj, jmethodIDV8ValueStringToPrimitive);
        }

        void Initialize(JNIEnv* jniEnv) noexcept;

        V8ScriptCompilerCachedData* ToCachedDataPointer(
            JNIEnv* jniEnv,
            const jbyteArray mCachedArray) noexcept;

        jbyteArray ToJavaByteArray(
            JNIEnv* jniEnv,
            const V8ScriptCompilerCachedData* cachedDataPointer) noexcept;

        static inline jstring ToJavaString(
            JNIEnv* jniEnv,
            const char* utfString) noexcept {
            return jniEnv->NewStringUTF(utfString);
        }

        static inline jstring ToJavaString(
            JNIEnv* jniEnv,
            const std::string& stdString) noexcept {
            return jniEnv->NewStringUTF(stdString.c_str());
        }

        static inline jstring ToJavaString(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const V8LocalString& v8LocalString) noexcept {
            V8StringValue v8StringValue(v8Context->GetIsolate(), v8LocalString);
            return jniEnv->NewString(*v8StringValue, v8StringValue.length());
        }

        static inline jstring ToJavaString(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const V8LocalValue& v8LocalValue) noexcept {
            V8StringUtf8Value v8StringUtf8Value(v8Context->GetIsolate(), v8LocalValue);
            return jniEnv->NewStringUTF(*v8StringUtf8Value);
        }

        static inline std::unique_ptr<std::string> ToStdString(
            JNIEnv* jniEnv,
            const jstring& mString) noexcept {
            const char* utfChars = jniEnv->GetStringUTFChars(mString, nullptr);
            auto stdStringPointer = std::make_unique<std::string>(utfChars, jniEnv->GetStringUTFLength(mString));
            jniEnv->ReleaseStringUTFChars(mString, utfChars);
            return stdStringPointer;
        }

        static inline std::unique_ptr<std::string> ToStdString(
            const V8LocalContext& v8Context,
            const V8LocalString& v8LocalString) noexcept {
            V8StringUtf8Value v8StringUtf8Value(v8Context->GetIsolate(), v8LocalString);
            return std::make_unique<std::string>(*v8StringUtf8Value, v8StringUtf8Value.length());
        }

        jobject ToExternalV8Context(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalContext& v8ContextValue) noexcept;

        jobject ToExternalV8Module(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalModule& v8Module) noexcept;

        jobject ToExternalV8Script(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalScript& v8Script) noexcept;

        jobject ToExternalV8Value(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
#ifdef ENABLE_NODE
            const V8InternalObject& v8InternalObject) noexcept;
#else
            const v8::internal::Tagged<V8InternalObject>& v8InternalObject) noexcept;
#endif

        jobject ToExternalV8Value(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalValue& v8Value) noexcept;

        jobjectArray ToExternalV8ValueArray(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const v8::FunctionCallbackInfo<v8::Value>& args) noexcept;

        jobjectArray ToExternalV8ValueArray(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalArray& v8LocalArray) noexcept;

        int ToExternalV8ValueArray(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalArray& v8LocalArray,
            jobjectArray v8Values,
            const int startIndex,
            const int endIndex) noexcept;

        jobject ToExternalV8ValueGlobalObject(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime) noexcept;

        jobject ToExternalV8ValueNull(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime) noexcept;

        static inline jobject ToExternalV8ValuePrimitive(
            JNIEnv* jniEnv,
            const jclass jclassV8ValuePrimitive,
            const jmethodID jmethodIDV8ValuePrimitiveConstructor,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalValue& v8Value) noexcept {
            jstring mStringValue = ToJavaString(jniEnv, v8Context, v8Value->ToString(v8Context).ToLocalChecked());
            jobject mV8ValuePrimitive = jniEnv->NewObject(
                jclassV8ValuePrimitive,
                jmethodIDV8ValuePrimitiveConstructor,
                v8Runtime->externalV8Runtime,
                mStringValue);
            jniEnv->DeleteLocalRef(mStringValue);
            return mV8ValuePrimitive;
        }

        jobject ToExternalV8ValueUndefined(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime) noexcept;

        jobject ToJavetScriptingError(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8TryCatch& v8TryCatch) noexcept;

        V8LocalBigInt ToV8BigInt(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jint mSignum,
            const jlongArray mLongArray) noexcept;

        static inline V8LocalBoolean ToV8Boolean(
            const V8LocalContext& v8Context,
            const bool boolValue) noexcept {
            return v8::Boolean::New(v8Context->GetIsolate(), boolValue);
        }

        V8LocalContext ToV8Context(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobject obj) noexcept;

        static inline V8LocalValue ToV8Date(
            const V8LocalContext& v8Context,
            const jlong longValue) noexcept {
            return v8::Date::New(v8Context, (double)longValue).ToLocalChecked();
        }

        static inline V8LocalNumber ToV8Double(
            const V8LocalContext& v8Context,
            const double doubleValue) noexcept {
            return v8::Number::New(v8Context->GetIsolate(), doubleValue);
        }

        static inline V8LocalInteger ToV8Integer(
            const V8LocalContext& v8Context,
            const int intValue) noexcept {
            return v8::Integer::New(v8Context->GetIsolate(), intValue);
        }

        static inline V8LocalBigInt ToV8Long(
            const V8LocalContext& v8Context,
            const jlong longValue) noexcept {
            return v8::BigInt::New(v8Context->GetIsolate(), longValue);
        }

        static inline V8LocalPrimitive ToV8Null(
            const V8LocalContext& v8Context) noexcept {
            return v8::Null(v8Context->GetIsolate());
        }

        static inline V8LocalPrimitive ToV8Undefined(
            const V8LocalContext& v8Context) noexcept {
            return v8::Undefined(v8Context->GetIsolate());
        }

        template<class T>
        static inline jlong ToV8PersistentReference(
            const V8LocalContext& v8Context,
            const v8::Local<T>& v8Data) noexcept {
            v8::Persistent<T>* v8PersistentDataPointer = new v8::Persistent<T>(v8Context->GetIsolate(), v8Data);
            INCREASE_COUNTER(Javet::Monitor::CounterType::NewPersistentReference);
            return TO_JAVA_LONG(v8PersistentDataPointer);
        }

        std::unique_ptr<v8::ScriptOrigin> ToV8ScriptOringinPointer(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jstring mResourceName,
            const jint mResourceLineOffset,
            const jint mResourceColumnOffset,
            const jint mScriptId,
            const jboolean mIsWASM,
            const jboolean mIsModule) noexcept;

        static inline V8LocalString ToV8String(
            const V8LocalContext& v8Context,
            const char* str) noexcept {
            return v8::String::NewFromUtf8(v8Context->GetIsolate(), str).ToLocalChecked();
        }

        V8LocalString ToV8String(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jstring mString) noexcept;

        static inline V8LocalStringObject ToV8StringObject(
            const V8LocalContext& v8Context,
            const V8LocalString v8LocalString) noexcept {
            return v8::StringObject::New(v8Context->GetIsolate(), v8LocalString).As<v8::StringObject>();
        }

        V8LocalValue ToV8Value(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobject obj) noexcept;

        std::unique_ptr<V8LocalObject[]> ToV8Objects(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobjectArray mObjects) noexcept;

        std::unique_ptr<V8LocalString[]> ToV8Strings(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobjectArray mStrings) noexcept;

        std::unique_ptr<V8LocalValue[]> ToV8Values(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobjectArray mValues) noexcept;

        static inline V8InternalContext ToV8InternalContext(
            const V8LocalContext& v8LocalContext) noexcept {
#ifdef ENABLE_NODE
            return V8InternalContext::cast(*v8::Utils::OpenHandle(*v8LocalContext));
#else
            return *V8InternalContext::cast(*v8::Utils::OpenHandle(*v8LocalContext));
#endif
        }

        static inline V8InternalJSFunction ToV8InternalJSFunction(
            const V8LocalValue& v8LocalValue) noexcept {
#ifdef ENABLE_NODE
            return V8InternalJSFunction::cast(*v8::Utils::OpenHandle(*v8LocalValue));
#else
            return *V8InternalJSFunction::cast(*v8::Utils::OpenHandle(*v8LocalValue));
#endif
        }

        static inline V8InternalModule ToV8InternalModule(
            const V8LocalModule& v8LocalModule) noexcept {
#ifdef ENABLE_NODE
            return V8InternalModule::cast(*v8::Utils::OpenHandle(*v8LocalModule));
#else
            return *V8InternalModule::cast(*v8::Utils::OpenHandle(*v8LocalModule));
#endif
        }

        static inline V8InternalScript ToV8InternalScript(
            const V8LocalScript& v8LocalScript) noexcept {
#ifdef ENABLE_NODE
            return V8InternalScript::cast(*v8::Utils::OpenHandle(*v8LocalScript));
#else
            return *V8InternalScript::cast(*v8::Utils::OpenHandle(*v8LocalScript));
#endif
        }
    }
}
