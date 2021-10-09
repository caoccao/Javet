/*
 *   Copyright (c) 2021 caoccao.com Sam Cao
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
#include "javet_logging.h"

 // Primitive
#define IS_JAVA_BOOLEAN(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueBoolean)
#define IS_JAVA_DOUBLE(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueDouble)
#define IS_JAVA_INTEGER(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueInteger)
#define IS_JAVA_LONG(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueLong)
#define IS_JAVA_NULL(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueNull)
#define IS_JAVA_STRING(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueString)
#define IS_JAVA_UNDEFINED(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueUndefined)
#define IS_JAVA_ZONED_DATE_TIME(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueZonedDateTime)

// Reference
#define IS_JAVA_ARGUMENTS(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueArguments)
#define IS_JAVA_ARRAY(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueArray)
#define IS_JAVA_ARRAY_BUFFER(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueArrayBuffer)
#define IS_JAVA_DATA_VIEW(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueDataView)
#define IS_JAVA_MODULE(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueModule)
#define IS_JAVA_FUNCTION(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueFunction)
#define IS_JAVA_ERROR(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueError)
#define IS_JAVA_GLOBAL_OBJECT(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueGlobalObject)
#define IS_JAVA_MAP(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueMap)
#define IS_JAVA_ITERATOR(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueIterator)
#define IS_JAVA_OBJECT(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueObject)
#define IS_JAVA_PROMISE(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValuePromise)
#define IS_JAVA_PROXY(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueProxy)
#define IS_JAVA_REFERENCE(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueReference)
#define IS_JAVA_REG_EXP(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueRegExp)
#define IS_JAVA_SET(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueSet)
#define IS_JAVA_SHARED_ARRAY_BUFFER(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueSharedArrayBuffer)
#define IS_JAVA_SYMBOL(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueSymbol)
#define IS_JAVA_SYMBOL_OBJECT(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueSymbolObject)
#define IS_JAVA_WEAK_MAP(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueWeakMap)
#define IS_JAVA_WEAK_SET(jniEnv, obj) jniEnv->IsInstanceOf(obj, jclassV8ValueWeakSet)

namespace Javet {
    namespace Converter {
        void Initialize(JNIEnv* jniEnv) {
            /*
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
            */

            // Runtime

            jclassV8Runtime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/V8Runtime"));
            jmethodIDV8RuntimeCreateV8ValueBoolean = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueBoolean", "(Z)Lcom/caoccao/javet/values/primitive/V8ValueBoolean;");
            jmethodIDV8RuntimeCreateV8ValueInteger = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueInteger", "(I)Lcom/caoccao/javet/values/primitive/V8ValueInteger;");
            jmethodIDV8RuntimeCreateV8ValueLong = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueLong", "(J)Lcom/caoccao/javet/values/primitive/V8ValueLong;");
            jmethodIDV8RuntimeCreateV8ValueNull = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueNull", "()Lcom/caoccao/javet/values/primitive/V8ValueNull;");
            jmethodIDV8RuntimeCreateV8ValueUndefined = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueUndefined", "()Lcom/caoccao/javet/values/primitive/V8ValueUndefined;");

            // Primitive

            jclassV8ValueBoolean = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueBoolean"));
            jmethodIDV8ValueBooleanConstructor = jniEnv->GetMethodID(jclassV8ValueBoolean, "<init>", "(Z)V");
            jmethodIDV8ValueBooleanToPrimitive = jniEnv->GetMethodID(jclassV8ValueBoolean, JAVA_METHOD_TO_PRIMITIVE, "()Z");

            jclassV8ValueDouble = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueDouble"));
            jmethodIDV8ValueDoubleConstructor = jniEnv->GetMethodID(jclassV8ValueDouble, "<init>", "(D)V");
            jmethodIDV8ValueDoubleToPrimitive = jniEnv->GetMethodID(jclassV8ValueDouble, JAVA_METHOD_TO_PRIMITIVE, "()D");

            jclassV8ValueInteger = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueInteger"));
            jmethodIDV8ValueIntegerConstructor = jniEnv->GetMethodID(jclassV8ValueInteger, "<init>", "(I)V");
            jmethodIDV8ValueIntegerToPrimitive = jniEnv->GetMethodID(jclassV8ValueInteger, JAVA_METHOD_TO_PRIMITIVE, "()I");

            jclassV8ValueLong = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueLong"));
            jmethodIDV8ValueLongConstructorFromLong = jniEnv->GetMethodID(jclassV8ValueLong, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueLongToPrimitive = jniEnv->GetMethodID(jclassV8ValueLong, JAVA_METHOD_TO_PRIMITIVE, "()J");

            jclassV8ValueNull = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueNull"));

            jclassV8ValueString = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueString"));
            jmethodIDV8ValueStringConstructor = jniEnv->GetMethodID(jclassV8ValueString, "<init>", "(Ljava/lang/String;)V");
            jmethodIDV8ValueStringToPrimitive = jniEnv->GetMethodID(jclassV8ValueString, JAVA_METHOD_TO_PRIMITIVE, "()Ljava/lang/String;");

            jclassV8ValueUndefined = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueUndefined"));

            jclassV8ValueUnknown = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueUnknown"));
            jmethodIDV8ValueUnknownConstructor = jniEnv->GetMethodID(jclassV8ValueUnknown, "<init>", "(Ljava/lang/String;)V");

            jclassV8ValueZonedDateTime = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/primitive/V8ValueZonedDateTime"));
            jmethodIDV8ValueZonedDateTimeConstructor = jniEnv->GetMethodID(jclassV8ValueZonedDateTime, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueZonedDateTimeToPrimitive = jniEnv->GetMethodID(jclassV8ValueZonedDateTime, JAVA_METHOD_TO_PRIMITIVE, "()J");

            // Reference

            jclassV8Module = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8Module"));
            jmethodIDV8ModuleConstructor = jniEnv->GetMethodID(jclassV8Module, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ModuleGetHandle = jniEnv->GetMethodID(jclassV8Module, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8Script = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8Script"));
            jmethodIDV8ScriptConstructor = jniEnv->GetMethodID(jclassV8Script, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ScriptGetHandle = jniEnv->GetMethodID(jclassV8Script, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueArguments = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueArguments"));
            jmethodIDV8ValueArgumentsConstructor = jniEnv->GetMethodID(jclassV8ValueArguments, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueArgumentsGetHandle = jniEnv->GetMethodID(jclassV8ValueArguments, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueArray = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueArray"));
            jmethodIDV8ValueArrayConstructor = jniEnv->GetMethodID(jclassV8ValueArray, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueArrayGetHandle = jniEnv->GetMethodID(jclassV8ValueArray, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueArrayBuffer = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueArrayBuffer"));
            jmethodIDV8ValueArrayBufferConstructor = jniEnv->GetMethodID(jclassV8ValueArrayBuffer, "<init>", "(JLjava/nio/ByteBuffer;)V");
            jmethodIDV8ValueArrayBufferGetHandle = jniEnv->GetMethodID(jclassV8ValueArrayBuffer, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueDataView = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueDataView"));
            jmethodIDV8ValueDataViewConstructor = jniEnv->GetMethodID(jclassV8ValueDataView, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueDataViewGetHandle = jniEnv->GetMethodID(jclassV8ValueDataView, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueFunction = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueFunction"));
            jmethodIDV8ValueFunctionConstructor = jniEnv->GetMethodID(jclassV8ValueFunction, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueFunctionGetHandle = jniEnv->GetMethodID(jclassV8ValueFunction, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueError = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueError"));
            jmethodIDV8ValueErrorConstructor = jniEnv->GetMethodID(jclassV8ValueError, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueErrorGetHandle = jniEnv->GetMethodID(jclassV8ValueError, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueGlobalObject = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueGlobalObject"));
            jmethodIDV8ValueGlobalObjectConstructor = jniEnv->GetMethodID(jclassV8ValueGlobalObject, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueGlobalObjectGetHandle = jniEnv->GetMethodID(jclassV8ValueGlobalObject, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueMap = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueMap"));
            jmethodIDV8ValueMapConstructor = jniEnv->GetMethodID(jclassV8ValueMap, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueMapGetHandle = jniEnv->GetMethodID(jclassV8ValueMap, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueIterator = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueIterator"));
            jmethodIDV8ValueIteratorConstructor = jniEnv->GetMethodID(jclassV8ValueIterator, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueIteratorGetHandle = jniEnv->GetMethodID(jclassV8ValueIterator, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueObject = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueObject"));
            jmethodIDV8ValueObjectConstructor = jniEnv->GetMethodID(jclassV8ValueObject, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueObjectGetHandle = jniEnv->GetMethodID(jclassV8ValueObject, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValuePromise = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValuePromise"));
            jmethodIDV8ValuePromiseConstructor = jniEnv->GetMethodID(jclassV8ValuePromise, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValuePromiseGetHandle = jniEnv->GetMethodID(jclassV8ValuePromise, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueProxy = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueProxy"));
            jmethodIDV8ValueProxyConstructor = jniEnv->GetMethodID(jclassV8ValueProxy, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueProxyGetHandle = jniEnv->GetMethodID(jclassV8ValueProxy, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueReference = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueReference"));

            jclassV8ValueRegExp = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueRegExp"));
            jmethodIDV8ValueRegExpConstructor = jniEnv->GetMethodID(jclassV8ValueRegExp, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueRegExpGetHandle = jniEnv->GetMethodID(jclassV8ValueRegExp, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueSet = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSet"));
            jmethodIDV8ValueSetConstructor = jniEnv->GetMethodID(jclassV8ValueSet, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueSetGetHandle = jniEnv->GetMethodID(jclassV8ValueSet, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueSharedArrayBuffer = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSharedArrayBuffer"));
            jmethodIDV8ValueSharedArrayBufferConstructor = jniEnv->GetMethodID(jclassV8ValueSharedArrayBuffer, "<init>", "(JLjava/nio/ByteBuffer;)V");
            jmethodIDV8ValueSharedArrayBufferGetHandle = jniEnv->GetMethodID(jclassV8ValueSharedArrayBuffer, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueSymbol = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSymbol"));
            jmethodIDV8ValueSymbolConstructor = jniEnv->GetMethodID(jclassV8ValueSymbol, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueSymbolGetHandle = jniEnv->GetMethodID(jclassV8ValueSymbol, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueSymbolObject = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueSymbolObject"));
            jmethodIDV8ValueSymbolObjectConstructor = jniEnv->GetMethodID(jclassV8ValueSymbolObject, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueSymbolObjectGetHandle = jniEnv->GetMethodID(jclassV8ValueSymbolObject, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueTypedArray = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueTypedArray"));
            jmethodIDV8ValueTypedArrayConstructor = jniEnv->GetMethodID(jclassV8ValueTypedArray, "<init>", "(JI)V");
            jmethodIDV8ValueTypedArrayGetHandle = jniEnv->GetMethodID(jclassV8ValueTypedArray, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueWeakMap = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueWeakMap"));
            jmethodIDV8ValueWeakMapConstructor = jniEnv->GetMethodID(jclassV8ValueWeakMap, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueWeakMapGetHandle = jniEnv->GetMethodID(jclassV8ValueWeakMap, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);

            jclassV8ValueWeakSet = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/values/reference/V8ValueWeakSet"));
            jmethodIDV8ValueWeakSetConstructor = jniEnv->GetMethodID(jclassV8ValueWeakSet, JAVA_CONSTRUCTOR_AND_SIGNATURE_FROM_HANDLE);
            jmethodIDV8ValueWeakSetGetHandle = jniEnv->GetMethodID(jclassV8ValueWeakSet, JAVA_METHOD_AND_SIGNATURE_GET_HANDLE);
        }

        jobject ToExternalV8ValueArray(
            JNIEnv* jniEnv, jobject externalV8Runtime,
            const V8LocalContext& v8Context, const v8::FunctionCallbackInfo<v8::Value>& args) {
            int argLength = args.Length();
            if (argLength > 0) {
                auto v8Array = v8::Array::New(v8Context->GetIsolate(), argLength);
                for (int i = 0; i < argLength; ++i) {
                    auto maybeResult = v8Array->Set(v8Context, i, args[i]);
                    maybeResult.Check();
                }
                return ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, v8Array);
            }
            return nullptr;
        }

        jobject ToExternalV8Module(JNIEnv* jniEnv, jobject externalV8Runtime, const V8LocalContext& v8Context, const V8LocalModule& v8Module) {
            return jniEnv->NewObject(jclassV8Module, jmethodIDV8ModuleConstructor, ToV8PersistentDataReference(v8Context, v8Module));
        }

        jobject ToExternalV8Script(JNIEnv* jniEnv, jobject externalV8Runtime, const V8LocalContext& v8Context, const V8LocalScript& v8Script) {
            return jniEnv->NewObject(jclassV8Script, jmethodIDV8ScriptConstructor, ToV8PersistentScriptReference(v8Context, v8Script));
        }

        jobject ToExternalV8Value(JNIEnv* jniEnv, jobject externalV8Runtime, const V8LocalContext& v8Context, const V8LocalValue v8Value) {
            if (v8Value->IsUndefined()) {
                return ToExternalV8ValueUndefined(jniEnv, externalV8Runtime);
            }
            if (v8Value->IsNull()) {
                return ToExternalV8ValueNull(jniEnv, externalV8Runtime);
            }
            // Reference types
            // Note: Reference types must be checked before primitive types are checked.
            if (v8Value->IsArray()) {
                return jniEnv->NewObject(jclassV8ValueArray, jmethodIDV8ValueArrayConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            using V8ValueReferenceType = Javet::Enums::V8ValueReferenceType::V8ValueReferenceType;
            if (v8Value->IsTypedArray()) {
                int type = V8ValueReferenceType::Invalid;
                if (v8Value->IsBigInt64Array()) {
                    type = V8ValueReferenceType::BigInt64Array;
                }
                else if (v8Value->IsBigUint64Array()) {
                    type = V8ValueReferenceType::BigUint64Array;
                }
                else if (v8Value->IsFloat32Array()) {
                    type = V8ValueReferenceType::Float32Array;
                }
                else if (v8Value->IsFloat64Array()) {
                    type = V8ValueReferenceType::Float64Array;
                }
                else if (v8Value->IsInt16Array()) {
                    type = V8ValueReferenceType::Int16Array;
                }
                else if (v8Value->IsInt32Array()) {
                    type = V8ValueReferenceType::Int32Array;
                }
                else if (v8Value->IsInt8Array()) {
                    type = V8ValueReferenceType::Int8Array;
                }
                else if (v8Value->IsUint16Array()) {
                    type = V8ValueReferenceType::Uint16Array;
                }
                else if (v8Value->IsUint32Array()) {
                    type = V8ValueReferenceType::Uint32Array;
                }
                else if (v8Value->IsUint8Array()) {
                    type = V8ValueReferenceType::Uint8Array;
                }
                else if (v8Value->IsUint8ClampedArray()) {
                    type = V8ValueReferenceType::Uint8ClampedArray;
                }
                if (type != V8ValueReferenceType::Invalid) {
                    return jniEnv->NewObject(jclassV8ValueTypedArray, jmethodIDV8ValueTypedArrayConstructor, ToV8PersistentValueReference(v8Context, v8Value), type);
                }
            }
            if (v8Value->IsDataView()) {
                return jniEnv->NewObject(jclassV8ValueDataView, jmethodIDV8ValueDataViewConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsArrayBuffer()) {
                auto v8ArrayBuffer = v8Value.As<v8::ArrayBuffer>();
                return jniEnv->NewObject(jclassV8ValueArrayBuffer, jmethodIDV8ValueArrayBufferConstructor, ToV8PersistentValueReference(v8Context, v8Value),
                    jniEnv->NewDirectByteBuffer(v8ArrayBuffer->GetBackingStore()->Data(), v8ArrayBuffer->ByteLength()));
            }
            if (v8Value->IsSharedArrayBuffer()) {
                auto v8SharedArrayBuffer = v8Value.As<v8::SharedArrayBuffer>();
                return jniEnv->NewObject(jclassV8ValueSharedArrayBuffer, jmethodIDV8ValueSharedArrayBufferConstructor, ToV8PersistentValueReference(v8Context, v8Value),
                    jniEnv->NewDirectByteBuffer(v8SharedArrayBuffer->GetBackingStore()->Data(), v8SharedArrayBuffer->ByteLength()));
            }
            if (v8Value->IsArrayBufferView()) {
                /*
                ArrayBufferView is a helper type representing any of typed array or DataView.
                This block shouldn't be entered.
                 */
            }
            if (v8Value->IsWeakMap()) {
                return jniEnv->NewObject(jclassV8ValueWeakMap, jmethodIDV8ValueWeakMapConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsWeakSet()) {
                return jniEnv->NewObject(jclassV8ValueWeakSet, jmethodIDV8ValueWeakSetConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsMap()) {
                return jniEnv->NewObject(jclassV8ValueMap, jmethodIDV8ValueMapConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsSet()) {
                return jniEnv->NewObject(jclassV8ValueSet, jmethodIDV8ValueSetConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsMapIterator() || v8Value->IsSetIterator() || v8Value->IsGeneratorObject()) {
                return jniEnv->NewObject(jclassV8ValueIterator, jmethodIDV8ValueIteratorConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsArgumentsObject()) {
                return jniEnv->NewObject(jclassV8ValueArguments, jmethodIDV8ValueArgumentsConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsPromise()) {
                return jniEnv->NewObject(jclassV8ValuePromise, jmethodIDV8ValuePromiseConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsRegExp()) {
                return jniEnv->NewObject(jclassV8ValueRegExp, jmethodIDV8ValueRegExpConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsAsyncFunction()) {
                // It defaults to V8ValueFunction.
            }
            if (v8Value->IsGeneratorFunction()) {
                // It defaults to V8ValueFunction.
            }
            if (v8Value->IsProxy()) {
                // Proxy is also a function. So, it needs to be tested before IsFunction().
                return jniEnv->NewObject(jclassV8ValueProxy, jmethodIDV8ValueProxyConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsFunction()) {
                return jniEnv->NewObject(jclassV8ValueFunction, jmethodIDV8ValueFunctionConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsNativeError()) {
                return jniEnv->NewObject(jclassV8ValueError, jmethodIDV8ValueErrorConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsSymbolObject()) {
                return jniEnv->NewObject(jclassV8ValueSymbolObject, jmethodIDV8ValueSymbolObjectConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            if (v8Value->IsSymbol()) {
                return jniEnv->NewObject(jclassV8ValueSymbol, jmethodIDV8ValueSymbolConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            // Primitive types
            if (v8Value->IsBoolean() || v8Value->IsBooleanObject()) {
                return jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeCreateV8ValueBoolean, v8Value->IsTrue());
            }
            if (v8Value->IsInt32()) {
                return jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeCreateV8ValueInteger, v8Value->Int32Value(v8Context).FromMaybe(0));
            }
            if (v8Value->IsBigInt() || v8Value->IsBigIntObject()) {
                return jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeCreateV8ValueLong, v8Value->ToBigInt(v8Context).ToLocalChecked()->Int64Value());
            }
            if (v8Value->IsDate()) {
                auto v8Date = v8Value->ToObject(v8Context).ToLocalChecked().As<v8::Date>();
                return jniEnv->NewObject(jclassV8ValueZonedDateTime, jmethodIDV8ValueZonedDateTimeConstructor, static_cast<std::int64_t>(v8Date->ValueOf()));
            }
            if (v8Value->IsNumber() || v8Value->IsNumberObject()) {
                return jniEnv->NewObject(jclassV8ValueDouble, jmethodIDV8ValueDoubleConstructor, v8Value->NumberValue(v8Context).FromMaybe(0));
            }
            if (v8Value->IsString() || v8Value->IsStringObject()) {
                return ToExternalV8ValuePrimitive(jniEnv, jclassV8ValueString, jmethodIDV8ValueStringConstructor, v8Context, v8Value);
            }
            if (v8Value->IsName()) {
                /*
                 * Name is handled by either String or Symbol.
                 * This block should not be entered.
                 */
            }
            if (v8Value->IsModule()) {
                return jniEnv->NewObject(jclassV8Module, jmethodIDV8ModuleConstructor, ToV8PersistentDataReference(v8Context, v8Value));
            }
            // Object needs to be the last one.
            if (v8Value->IsObject()) {
                return jniEnv->NewObject(jclassV8ValueObject, jmethodIDV8ValueObjectConstructor, ToV8PersistentValueReference(v8Context, v8Value));
            }
            // Something is wrong. It defaults to toString().
            return ToExternalV8ValuePrimitive(jniEnv, jclassV8ValueUnknown, jmethodIDV8ValueUnknownConstructor, v8Context, v8Value);
        }

        jobject ToExternalV8ValueGlobalObject(JNIEnv* jniEnv, V8PersistentObject& v8PersistentObject) {
            return jniEnv->NewObject(jclassV8ValueGlobalObject, jmethodIDV8ValueGlobalObjectConstructor, TO_JAVA_LONG(&v8PersistentObject));
        }

        jobject ToExternalV8ValueUndefined(JNIEnv* jniEnv, jobject externalV8Runtime) {
            return jniEnv->CallObjectMethod(externalV8Runtime, jmethodIDV8RuntimeCreateV8ValueUndefined);
        }

        std::unique_ptr<v8::ScriptOrigin> ToV8ScriptOringinPointer(JNIEnv* jniEnv, const V8LocalContext& v8Context,
            jstring& mResourceName, jint& mResourceLineOffset, jint& mResourceColumnOffset, jint& mScriptId, jboolean& mIsWASM, jboolean& mIsModule) {
            return std::make_unique<v8::ScriptOrigin>(
                ToV8String(jniEnv, v8Context, mResourceName),
                ToV8Integer(v8Context, mResourceLineOffset),
                ToV8Integer(v8Context, mResourceColumnOffset),
                V8LocalBoolean(),
                ToV8Integer(v8Context, mScriptId),
                V8LocalValue(),
                V8LocalBoolean(),
                ToV8Boolean(v8Context, mIsWASM),
                ToV8Boolean(v8Context, mIsModule),
                V8LocalPrimitiveArray());
        }

        V8LocalString ToV8String(JNIEnv* jniEnv, const V8LocalContext& v8Context, jstring& managedString) {
            if (managedString == nullptr) {
                return V8LocalString();
            }
            const uint16_t* unmanagedString = jniEnv->GetStringChars(managedString, nullptr);
            int length = jniEnv->GetStringLength(managedString);
            auto twoByteString = v8::String::NewFromTwoByte(
                v8Context->GetIsolate(), unmanagedString, v8::NewStringType::kNormal, length);
            if (twoByteString.IsEmpty()) {
                return V8LocalString();
            }
            auto localV8String = twoByteString.ToLocalChecked();
            jniEnv->ReleaseStringChars(managedString, unmanagedString);
            return localV8String;
        }

        V8LocalValue ToV8Value(JNIEnv* jniEnv, const V8LocalContext& v8Context, jobject& obj) {
            if (obj == nullptr || IS_JAVA_NULL(jniEnv, obj)) {
                return ToV8Null(v8Context);
            }
            else if (IS_JAVA_INTEGER(jniEnv, obj)) {
                jint integerObject = jniEnv->CallIntMethod(obj, jmethodIDV8ValueIntegerToPrimitive);
                return ToV8Integer(v8Context, integerObject);
            }
            else if (IS_JAVA_STRING(jniEnv, obj)) {
                jstring stringObject = (jstring)jniEnv->CallObjectMethod(obj, jmethodIDV8ValueStringToPrimitive);
                return ToV8String(jniEnv, v8Context, stringObject);
            }
            else if (IS_JAVA_BOOLEAN(jniEnv, obj)) {
                jboolean booleanObject = jniEnv->CallBooleanMethod(obj, jmethodIDV8ValueBooleanToPrimitive);
                return ToV8Boolean(v8Context, booleanObject);
            }
            else if (IS_JAVA_DOUBLE(jniEnv, obj)) {
                jdouble doubleObject = jniEnv->CallDoubleMethod(obj, jmethodIDV8ValueDoubleToPrimitive);
                return ToV8Double(v8Context, doubleObject);
            }
            else if (IS_JAVA_LONG(jniEnv, obj)) {
                jlong longObject = jniEnv->CallLongMethod(obj, jmethodIDV8ValueLongToPrimitive);
                return ToV8Long(v8Context, longObject);
            }
            else if (IS_JAVA_ZONED_DATE_TIME(jniEnv, obj)) {
                jlong longObject = (jlong)jniEnv->CallObjectMethod(obj, jmethodIDV8ValueZonedDateTimeToPrimitive);
                return ToV8Date(v8Context, longObject);
            }
            else if (IS_JAVA_REFERENCE(jniEnv, obj)) {
                if (IS_JAVA_ARRAY(jniEnv, obj)) {
                    return V8LocalArray::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_ARRAY_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueArrayGetHandle)));
                }
                else if (IS_JAVA_GLOBAL_OBJECT(jniEnv, obj)) {
                    // Global object is a tricky one. 
                    return V8LocalObject::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_OBJECT_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueGlobalObjectGetHandle)));
                }
                else if (IS_JAVA_MAP(jniEnv, obj)) {
                    return V8LocalMap::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_MAP_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueMapGetHandle)));
                }
                else if (IS_JAVA_PROMISE(jniEnv, obj)) {
                    return V8LocalPromise::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_PROMISE_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValuePromiseGetHandle)));
                }
                else if (IS_JAVA_PROXY(jniEnv, obj)) {
                    return V8LocalProxy::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_PROXY_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueProxyGetHandle)));
                }
                else if (IS_JAVA_REG_EXP(jniEnv, obj)) {
                    return V8LocalRegExp::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_REG_EXP_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueRegExpGetHandle)));
                }
                else if (IS_JAVA_SET(jniEnv, obj)) {
                    return V8LocalSet::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_SET_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueSetGetHandle)));
                }
                else if (IS_JAVA_SYMBOL(jniEnv, obj)) {
                    return V8LocalSymbol::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_SYMBOL_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueSymbolGetHandle)));
                }
                else if (IS_JAVA_SYMBOL_OBJECT(jniEnv, obj)) {
                    return V8LocalSymbolObject::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_SYMBOL_OBJECT_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueSymbolObjectGetHandle)));
                }
                else if (
                    IS_JAVA_ARGUMENTS(jniEnv, obj) ||
                    IS_JAVA_ERROR(jniEnv, obj) ||
                    IS_JAVA_ITERATOR(jniEnv, obj) ||
                    IS_JAVA_OBJECT(jniEnv, obj) ||
                    IS_JAVA_WEAK_MAP(jniEnv, obj) ||
                    IS_JAVA_WEAK_SET(jniEnv, obj)) {
                    return V8LocalObject::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_OBJECT_POINTER(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueObjectGetHandle)));
                }
            }
            return ToV8Undefined(v8Context);
        }

        std::unique_ptr<V8LocalValue[]> ToV8Values(JNIEnv* jniEnv, const V8LocalContext& v8Context, jobjectArray& mValues) {
            std::unique_ptr<V8LocalValue[]> umValuesPointer;
            uint32_t valueCount = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
            if (valueCount > 0) {
                umValuesPointer.reset(new V8LocalValue[valueCount]);
                for (uint32_t i = 0; i < valueCount; ++i) {
                    jobject obj = jniEnv->GetObjectArrayElement(mValues, i);
                    umValuesPointer.get()[i] = ToV8Value(jniEnv, v8Context, obj);
                }
            }
            return umValuesPointer;
        }
    }
}
