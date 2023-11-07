/*
 *   Copyright (c) 2021-2023 caoccao.com Sam Cao
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
#include "javet_logging.h"

namespace Javet {
    namespace Converter {
        // JDK

        jclass jclassByteBuffer;
        jclass jclassString;

        // Runtime

        jclass jclassV8Runtime;
        jmethodID jmethodIDV8RuntimeCreateV8ValueBoolean;
        jmethodID jmethodIDV8RuntimeCreateV8ValueDouble;
        jmethodID jmethodIDV8RuntimeCreateV8ValueInteger;
        jmethodID jmethodIDV8RuntimeCreateV8ValueLong;
        jmethodID jmethodIDV8RuntimeCreateV8ValueNull;
        jmethodID jmethodIDV8RuntimeCreateV8ValueUndefined;
        jmethodID jmethodIDV8RuntimeCreateV8ValueZonedDateTime;

        // Primitive

        jclass jclassV8Value;

        jclass jclassV8ValueBigInteger;
        jmethodID jmethodIDV8ValueBigIntegerConstructor;
        jmethodID jmethodIDV8ValueBigIntegerGetLongArray;
        jmethodID jmethodIDV8ValueBigIntegerGetSignum;

        jclass jclassV8ValueBoolean;
        jmethodID jmethodIDV8ValueBooleanToPrimitive;

        jclass jclassV8ValueDouble;
        jmethodID jmethodIDV8ValueDoubleToPrimitive;

        jclass jclassV8ValueLong;
        jmethodID jmethodIDV8ValueLongToPrimitive;

        jclass jclassV8ValueNull;

        jclass jclassV8ValueInteger;
        jmethodID jmethodIDV8ValueIntegerToPrimitive;

        jclass jclassV8ValueString;
        jmethodID jmethodIDV8ValueStringConstructor;
        jmethodID jmethodIDV8ValueStringToPrimitive;

        jclass jclassV8ValueSymbol;

        jclass jclassV8ValueUndefined;

        jclass jclassV8ValueUnknown;
        jmethodID jmethodIDV8ValueUnknownConstructor;

        jclass jclassV8ValueZonedDateTime;
        jmethodID jmethodIDV8ValueZonedDateTimeToPrimitive;

        // Reference

        jclass jclassV8Context;
        jmethodID jmethodIDV8ContextConstructor;
        jmethodID jmethodIDV8ContextGetHandle;

        jclass jclassV8Module;
        jmethodID jmethodIDV8ModuleConstructor;
        jmethodID jmethodIDV8ModuleGetHandle;

        jclass jclassV8Script;
        jmethodID jmethodIDV8ScriptConstructor;
        jmethodID jmethodIDV8ScriptGetHandle;

        jclass jclassV8ValueArguments;
        jmethodID jmethodIDV8ValueArgumentsConstructor;
        jmethodID jmethodIDV8ValueArgumentsGetHandle;

        jclass jclassV8ValueArray;
        jmethodID jmethodIDV8ValueArrayConstructor;
        jmethodID jmethodIDV8ValueArrayGetHandle;

        jclass jclassV8ValueArrayBuffer;
        jmethodID jmethodIDV8ValueArrayBufferConstructor;
        jmethodID jmethodIDV8ValueArrayBufferGetHandle;

        jclass jclassV8ValueDataView;
        jmethodID jmethodIDV8ValueDataViewConstructor;
        jmethodID jmethodIDV8ValueDataViewGetHandle;

        jclass jclassV8ValueFunction;
        jmethodID jmethodIDV8ValueFunctionConstructor;
        jmethodID jmethodIDV8ValueFunctionGetHandle;

        jclass jclassV8ValueError;
        jmethodID jmethodIDV8ValueErrorConstructor;
        jmethodID jmethodIDV8ValueErrorGetHandle;

        jclass jclassV8ValueGlobalObject;
        jmethodID jmethodIDV8ValueGlobalObjectConstructor;
        jmethodID jmethodIDV8ValueGlobalObjectGetHandle;

        jclass jclassV8ValueIterator;
        jmethodID jmethodIDV8ValueIteratorConstructor;
        jmethodID jmethodIDV8ValueIteratorGetHandle;

        jclass jclassV8ValueMap;
        jmethodID jmethodIDV8ValueMapConstructor;
        jmethodID jmethodIDV8ValueMapGetHandle;

        jclass jclassV8ValueObject;
        jmethodID jmethodIDV8ValueObjectConstructor;
        jmethodID jmethodIDV8ValueObjectGetHandle;

        jclass jclassV8ValuePromise;
        jmethodID jmethodIDV8ValuePromiseConstructor;
        jmethodID jmethodIDV8ValuePromiseGetHandle;

        jclass jclassV8ValueProxy;
        jmethodID jmethodIDV8ValueProxyConstructor;
        jmethodID jmethodIDV8ValueProxyGetHandle;

        jclass jclassV8ValueReference;

        jclass jclassV8ValueRegExp;
        jmethodID jmethodIDV8ValueRegExpConstructor;
        jmethodID jmethodIDV8ValueRegExpGetHandle;

        jclass jclassV8ValueSet;
        jmethodID jmethodIDV8ValueSetConstructor;
        jmethodID jmethodIDV8ValueSetGetHandle;

        jclass jclassV8ValueSharedArrayBuffer;
        jmethodID jmethodIDV8ValueSharedArrayBufferConstructor;
        jmethodID jmethodIDV8ValueSharedArrayBufferGetHandle;

        jmethodID jmethodIDV8ValueSymbolConstructor;
        jmethodID jmethodIDV8ValueSymbolGetHandle;

        jclass jclassV8ValueSymbolObject;
        jmethodID jmethodIDV8ValueSymbolObjectConstructor;
        jmethodID jmethodIDV8ValueSymbolObjectGetHandle;

        jclass jclassV8ValueTypedArray;
        jmethodID jmethodIDV8ValueTypedArrayConstructor;
        jmethodID jmethodIDV8ValueTypedArrayGetHandle;

        jclass jclassV8ValueWeakMap;
        jmethodID jmethodIDV8ValueWeakMapConstructor;
        jmethodID jmethodIDV8ValueWeakMapGetHandle;

        jclass jclassV8ValueWeakSet;
        jmethodID jmethodIDV8ValueWeakSetConstructor;
        jmethodID jmethodIDV8ValueWeakSetGetHandle;

        // Misc

        jclass jclassJavetScriptingError;
        jmethodID jmethodIDJavetScriptingErrorConstructor;

        jclass jclassIV8ValueFunctionScriptSource;
        jmethodID jmethodIDIV8ValueFunctionScriptSourceConstructor;
        jmethodID jmethodIDIV8ValueFunctionScriptGetCode;
        jmethodID jmethodIDIV8ValueFunctionScriptGetEndPosition;
        jmethodID jmethodIDIV8ValueFunctionScriptGetStartPosition;

        constexpr auto JAVA_METHOD_TO_PRIMITIVE = "toPrimitive";

        // Primitive

        template<typename T1, typename T2>
        constexpr auto IsV8ValueBigInteger(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueBigInteger);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueBoolean(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueBoolean);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueDouble(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueDouble);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueLong(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueLong);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueNull(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueNull);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueUndefined(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueUndefined);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueZonedDateTime(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueZonedDateTime);
        }

        // Reference

        template<typename T1, typename T2>
        constexpr auto IsV8ValueArguments(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueArguments);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueArray(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueArray);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueArrayBuffer(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueArrayBuffer);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueContext(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8Context);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueDataView(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueDataView);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueFunction(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueFunction);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueError(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueError);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueGlobalObject(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueGlobalObject);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueMap(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueMap);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueIterator(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueIterator);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueObject(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueObject);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValuePromise(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValuePromise);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueProxy(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueProxy);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueReference(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueReference);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueRegExp(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueRegExp);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueSet(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueSet);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueSharedArrayBuffer(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueSharedArrayBuffer);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueSymbolObject(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueSymbolObject);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueWeakMap(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueWeakMap);
        }

        template<typename T1, typename T2>
        constexpr auto IsV8ValueWeakSet(T1 jniEnv, T2 obj) {
            return jniEnv->IsInstanceOf(obj, jclassV8ValueWeakSet);
        }

        void Initialize(JNIEnv* jniEnv) noexcept {
            /*
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
            */

            // Runtime

            jclassV8Runtime = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/V8Runtime");
            jmethodIDV8RuntimeCreateV8ValueBoolean = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueBoolean", "(Z)Lcom/caoccao/javet/values/primitive/V8ValueBoolean;");
            jmethodIDV8RuntimeCreateV8ValueDouble = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueDouble", "(D)Lcom/caoccao/javet/values/primitive/V8ValueDouble;");
            jmethodIDV8RuntimeCreateV8ValueInteger = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueInteger", "(I)Lcom/caoccao/javet/values/primitive/V8ValueInteger;");
            jmethodIDV8RuntimeCreateV8ValueLong = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueLong", "(J)Lcom/caoccao/javet/values/primitive/V8ValueLong;");
            jmethodIDV8RuntimeCreateV8ValueNull = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueNull", "()Lcom/caoccao/javet/values/primitive/V8ValueNull;");
            jmethodIDV8RuntimeCreateV8ValueUndefined = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueUndefined", "()Lcom/caoccao/javet/values/primitive/V8ValueUndefined;");
            jmethodIDV8RuntimeCreateV8ValueZonedDateTime = jniEnv->GetMethodID(jclassV8Runtime, "createV8ValueZonedDateTime", "(J)Lcom/caoccao/javet/values/primitive/V8ValueZonedDateTime;");

            // Primitive

            jclassV8Value = FIND_CLASS(jniEnv, "com/caoccao/javet/values/V8Value");

            jclassV8ValueBigInteger = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueBigInteger");
            jmethodIDV8ValueBigIntegerConstructor = jniEnv->GetMethodID(jclassV8ValueBigInteger, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;I[J)V");
            jmethodIDV8ValueBigIntegerGetLongArray = jniEnv->GetMethodID(jclassV8ValueBigInteger, "getLongArray", "()[J");
            jmethodIDV8ValueBigIntegerGetSignum = jniEnv->GetMethodID(jclassV8ValueBigInteger, "getSignum", "()I");

            jclassV8ValueBoolean = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueBoolean");
            jmethodIDV8ValueBooleanToPrimitive = jniEnv->GetMethodID(jclassV8ValueBoolean, JAVA_METHOD_TO_PRIMITIVE, "()Z");

            jclassV8ValueDouble = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueDouble");
            jmethodIDV8ValueDoubleToPrimitive = jniEnv->GetMethodID(jclassV8ValueDouble, JAVA_METHOD_TO_PRIMITIVE, "()D");

            jclassV8ValueInteger = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueInteger");
            jmethodIDV8ValueIntegerToPrimitive = jniEnv->GetMethodID(jclassV8ValueInteger, JAVA_METHOD_TO_PRIMITIVE, "()I");

            jclassV8ValueLong = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueLong");
            jmethodIDV8ValueLongToPrimitive = jniEnv->GetMethodID(jclassV8ValueLong, JAVA_METHOD_TO_PRIMITIVE, "()J");

            jclassV8ValueNull = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueNull");

            jclassV8ValueString = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueString");
            jmethodIDV8ValueStringConstructor = jniEnv->GetMethodID(jclassV8ValueString, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;Ljava/lang/String;)V");
            jmethodIDV8ValueStringToPrimitive = jniEnv->GetMethodID(jclassV8ValueString, JAVA_METHOD_TO_PRIMITIVE, "()Ljava/lang/String;");

            jclassV8ValueUndefined = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueUndefined");

            jclassV8ValueUnknown = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueUnknown");
            jmethodIDV8ValueUnknownConstructor = jniEnv->GetMethodID(jclassV8ValueUnknown, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;Ljava/lang/String;)V");

            jclassV8ValueZonedDateTime = FIND_CLASS(jniEnv, "com/caoccao/javet/values/primitive/V8ValueZonedDateTime");
            jmethodIDV8ValueZonedDateTimeToPrimitive = jniEnv->GetMethodID(jclassV8ValueZonedDateTime, JAVA_METHOD_TO_PRIMITIVE, "()J");

            // Reference

            jclassV8Context = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8Context");
            jmethodIDV8ContextConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8Context);
            jmethodIDV8ContextGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8Context);

            jclassV8Module = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8Module");
            jmethodIDV8ModuleConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8Module);
            jmethodIDV8ModuleGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8Module);

            jclassV8Script = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8Script");
            jmethodIDV8ScriptConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8Script);
            jmethodIDV8ScriptGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8Script);

            jclassV8ValueArguments = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueArguments");
            jmethodIDV8ValueArgumentsConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueArguments);
            jmethodIDV8ValueArgumentsGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueArguments);

            jclassV8ValueArray = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueArray");
            jmethodIDV8ValueArrayConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueArray);
            jmethodIDV8ValueArrayGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueArray);

            jclassV8ValueArrayBuffer = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueArrayBuffer");
            jmethodIDV8ValueArrayBufferConstructor = jniEnv->GetMethodID(jclassV8ValueArrayBuffer, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;JLjava/nio/ByteBuffer;)V");
            jmethodIDV8ValueArrayBufferGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueArrayBuffer);

            jclassV8ValueDataView = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueDataView");
            jmethodIDV8ValueDataViewConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueDataView);
            jmethodIDV8ValueDataViewGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueDataView);

            jclassV8ValueFunction = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueFunction");
            jmethodIDV8ValueFunctionConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueFunction);
            jmethodIDV8ValueFunctionGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueFunction);

            jclassV8ValueError = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueError");
            jmethodIDV8ValueErrorConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueError);
            jmethodIDV8ValueErrorGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueError);

            jclassV8ValueGlobalObject = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueGlobalObject");
            jmethodIDV8ValueGlobalObjectConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueGlobalObject);
            jmethodIDV8ValueGlobalObjectGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueGlobalObject);

            jclassV8ValueIterator = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueIterator");
            jmethodIDV8ValueIteratorConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueIterator);
            jmethodIDV8ValueIteratorGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueIterator);

            jclassV8ValueMap = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueMap");
            jmethodIDV8ValueMapConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueMap);
            jmethodIDV8ValueMapGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueMap);

            jclassV8ValueObject = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueObject");
            jmethodIDV8ValueObjectConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueObject);
            jmethodIDV8ValueObjectGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueObject);

            jclassV8ValuePromise = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValuePromise");
            jmethodIDV8ValuePromiseConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValuePromise);
            jmethodIDV8ValuePromiseGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValuePromise);

            jclassV8ValueProxy = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueProxy");
            jmethodIDV8ValueProxyConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueProxy);
            jmethodIDV8ValueProxyGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueProxy);

            jclassV8ValueReference = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueReference");

            jclassV8ValueRegExp = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueRegExp");
            jmethodIDV8ValueRegExpConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueRegExp);
            jmethodIDV8ValueRegExpGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueRegExp);

            jclassV8ValueSet = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueSet");
            jmethodIDV8ValueSetConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueSet);
            jmethodIDV8ValueSetGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueSet);

            jclassV8ValueSharedArrayBuffer = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueSharedArrayBuffer");
            jmethodIDV8ValueSharedArrayBufferConstructor = jniEnv->GetMethodID(jclassV8ValueSharedArrayBuffer, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;JLjava/nio/ByteBuffer;)V");
            jmethodIDV8ValueSharedArrayBufferGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueSharedArrayBuffer);

            jclassV8ValueSymbol = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueSymbol");
            jmethodIDV8ValueSymbolConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueSymbol);
            jmethodIDV8ValueSymbolGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueSymbol);

            jclassV8ValueSymbolObject = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueSymbolObject");
            jmethodIDV8ValueSymbolObjectConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueSymbolObject);
            jmethodIDV8ValueSymbolObjectGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueSymbolObject);

            jclassV8ValueTypedArray = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueTypedArray");
            jmethodIDV8ValueTypedArrayConstructor = jniEnv->GetMethodID(jclassV8ValueTypedArray, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;JI)V");
            jmethodIDV8ValueTypedArrayGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueTypedArray);

            jclassV8ValueWeakMap = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueWeakMap");
            jmethodIDV8ValueWeakMapConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueWeakMap);
            jmethodIDV8ValueWeakMapGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueWeakMap);

            jclassV8ValueWeakSet = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/V8ValueWeakSet");
            jmethodIDV8ValueWeakSetConstructor = GET_METHOD_CONSTRUCTOR(jniEnv, jclassV8ValueWeakSet);
            jmethodIDV8ValueWeakSetGetHandle = GET_METHOD_GET_HANDLE(jniEnv, jclassV8ValueWeakSet);

            // Misc
            jclassByteBuffer = FIND_CLASS(jniEnv, "java/nio/ByteBuffer");
            jclassString = FIND_CLASS(jniEnv, "java/lang/String");

            jclassJavetScriptingError = FIND_CLASS(jniEnv, "com/caoccao/javet/exceptions/JavetScriptingError");
            jmethodIDJavetScriptingErrorConstructor = jniEnv->GetMethodID(jclassJavetScriptingError, "<init>", "(Lcom/caoccao/javet/values/V8Value;Ljava/lang/String;Ljava/lang/String;IIIII)V");

            jclassIV8ValueFunctionScriptSource = FIND_CLASS(jniEnv, "com/caoccao/javet/values/reference/IV8ValueFunction$ScriptSource");
            jmethodIDIV8ValueFunctionScriptSourceConstructor = jniEnv->GetMethodID(jclassIV8ValueFunctionScriptSource, "<init>", "(Ljava/lang/String;II)V");
            jmethodIDIV8ValueFunctionScriptGetCode = jniEnv->GetMethodID(jclassIV8ValueFunctionScriptSource, "getCode", "()Ljava/lang/String;");
            jmethodIDIV8ValueFunctionScriptGetEndPosition = jniEnv->GetMethodID(jclassIV8ValueFunctionScriptSource, "getEndPosition", "()I");
            jmethodIDIV8ValueFunctionScriptGetStartPosition = jniEnv->GetMethodID(jclassIV8ValueFunctionScriptSource, "getStartPosition", "()I");
        }

        V8ScriptCompilerCachedData* ToCachedDataPointer(
            JNIEnv* jniEnv,
            const jbyteArray mCachedArray) noexcept {
            jsize length = jniEnv->GetArrayLength(mCachedArray);
            uint8_t* bytes = new uint8_t[length];
            jboolean isCopy;
            jbyte* bytePointer = jniEnv->GetByteArrayElements(mCachedArray, &isCopy);
            memcpy(bytes, bytePointer, length);
            jniEnv->ReleaseByteArrayElements(mCachedArray, bytePointer, JNI_ABORT);
            return new V8ScriptCompilerCachedData(bytes, length, V8ScriptCompilerCachedDataBufferPolicy::BufferOwned);
        }

        jbyteArray ToJavaByteArray(
            JNIEnv* jniEnv,
            const V8ScriptCompilerCachedData* cachedDataPointer) noexcept {
            jbyteArray byteArray = jniEnv->NewByteArray((jsize)cachedDataPointer->length);
            jboolean isCopy;
            jbyte* bytePointer = jniEnv->GetByteArrayElements(byteArray, &isCopy);
            memcpy(bytePointer, cachedDataPointer->data, cachedDataPointer->length);
            jniEnv->ReleaseByteArrayElements(byteArray, bytePointer, JNI_COMMIT);
            return byteArray;
        }

        jobject ToExternalV8Context(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalContext& v8ContextValue) noexcept {
            return jniEnv->NewObject(
                jclassV8Context,
                jmethodIDV8ContextConstructor,
                v8Runtime->externalV8Runtime,
                ToV8PersistentReference(v8Context, v8ContextValue));
        }

        jobject ToExternalV8Module(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalModule& v8Module) noexcept {
            return jniEnv->NewObject(
                jclassV8Module,
                jmethodIDV8ModuleConstructor,
                v8Runtime->externalV8Runtime,
                ToV8PersistentReference(v8Context, v8Module));
        }

        jobject ToExternalV8Script(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalScript& v8Script) noexcept {
            return jniEnv->NewObject(
                jclassV8Script,
                jmethodIDV8ScriptConstructor,
                v8Runtime->externalV8Runtime,
                ToV8PersistentReference(v8Context, v8Script));
        }

#ifdef ENABLE_NODE
        jobject ToExternalV8Value(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8InternalObject& v8InternalObject) noexcept {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            if (v8InternalObject.IsJSObject() || v8InternalObject.IsPrimitive()
                || v8InternalObject.IsJSArray() || v8InternalObject.IsJSTypedArray()) {
                auto v8LocalObject = v8::Utils::ToLocal(v8::internal::handle(v8InternalObject, v8InternalIsolate));
                return ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8LocalObject);
            }
            else if (v8InternalObject.IsContext()) {
                auto v8InternalContext = V8InternalContext::cast(v8InternalObject);
                auto v8LocalContext = v8::Utils::ToLocal(v8::internal::handle(v8InternalContext, v8InternalIsolate));
                return ToExternalV8Context(jniEnv, v8Runtime, v8Context, v8LocalContext);
            }
            else if (v8InternalObject.IsModule()) {
                auto v8LocalModule = v8::Utils::ToLocal(v8::internal::handle(V8InternalModule::cast(v8InternalObject), v8InternalIsolate));
                return ToExternalV8Module(jniEnv, v8Runtime, v8Context, v8LocalModule);
            }
            else if (v8InternalObject.IsScript()) {
                LOG_DEBUG("Converter: Script is not supported.");
            }
            else if (v8InternalObject.IsCode()) {
                LOG_DEBUG("Converter: Code is not supported.");
            }
            return ToExternalV8ValueUndefined(jniEnv, v8Runtime);
        }
#else
        jobject ToExternalV8Value(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const v8::internal::Tagged<V8InternalObject>& v8InternalObject) noexcept {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Context->GetIsolate());
            if (v8::internal::IsJSObject(v8InternalObject) || v8::internal::IsPrimitive(v8InternalObject)
                || v8::internal::IsJSArray(v8InternalObject) || v8::internal::IsJSTypedArray(v8InternalObject)) {
                auto v8LocalObject = v8::Utils::ToLocal(v8::internal::handle(v8InternalObject, v8InternalIsolate));
                return ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8LocalObject);
            }
            else if (v8::internal::IsContext(v8InternalObject)) {
                auto v8InternalContext = V8InternalContext::cast(v8InternalObject);
                auto v8LocalContext = v8::Utils::ToLocal(v8::internal::handle(v8InternalContext, v8InternalIsolate));
                return ToExternalV8Context(jniEnv, v8Runtime, v8Context, v8LocalContext);
            }
            else if (v8::internal::IsModule(v8InternalObject)) {
                auto v8LocalModule = v8::Utils::ToLocal(v8::internal::handle(V8InternalModule::cast(v8InternalObject), v8InternalIsolate));
                return ToExternalV8Module(jniEnv, v8Runtime, v8Context, v8LocalModule);
            }
            else if (v8::internal::IsScript(v8InternalObject)) {
                LOG_DEBUG("Converter: Script is not supported.");
            }
            else if (v8::internal::IsCode(v8InternalObject)) {
                LOG_DEBUG("Converter: Code is not supported.");
            }
            return ToExternalV8ValueUndefined(jniEnv, v8Runtime);
        }
#endif

        jobject ToExternalV8Value(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalValue& v8Value) noexcept {
            using V8ValueReferenceType = Javet::Enums::V8ValueReferenceType::V8ValueReferenceType;
            if (v8Value->IsUndefined()) {
                return ToExternalV8ValueUndefined(jniEnv, v8Runtime);
            }
            if (v8Value->IsNull()) {
                return ToExternalV8ValueNull(jniEnv, v8Runtime);
            }
            // Reference types
            // Note: Reference types must be checked before primitive types are checked.
            if (v8Value->IsArray()) {
                return jniEnv->NewObject(
                    jclassV8ValueArray,
                    jmethodIDV8ValueArrayConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
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
                    return jniEnv->NewObject(
                        jclassV8ValueTypedArray,
                        jmethodIDV8ValueTypedArrayConstructor,
                        v8Runtime->externalV8Runtime,
                        ToV8PersistentReference(v8Context, v8Value),
                        type);
                }
            }
            if (v8Value->IsDataView()) {
                return jniEnv->NewObject(
                    jclassV8ValueDataView,
                    jmethodIDV8ValueDataViewConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsArrayBuffer()) {
                auto v8ArrayBuffer = v8Value.As<v8::ArrayBuffer>();
                return jniEnv->NewObject(
                    jclassV8ValueArrayBuffer,
                    jmethodIDV8ValueArrayBufferConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value),
                    jniEnv->NewDirectByteBuffer(v8ArrayBuffer->GetBackingStore()->Data(), v8ArrayBuffer->ByteLength()));
            }
            if (v8Value->IsSharedArrayBuffer()) {
                auto v8SharedArrayBuffer = v8Value.As<v8::SharedArrayBuffer>();
                return jniEnv->NewObject(
                    jclassV8ValueSharedArrayBuffer,
                    jmethodIDV8ValueSharedArrayBufferConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value),
                    jniEnv->NewDirectByteBuffer(v8SharedArrayBuffer->GetBackingStore()->Data(), v8SharedArrayBuffer->ByteLength()));
            }
            if (v8Value->IsArrayBufferView()) {
                /*
                 * ArrayBufferView is a helper type representing any of typed array or DataView.
                 * This block shouldn't be entered.
                 */
            }
            if (v8Value->IsWeakMap()) {
                return jniEnv->NewObject(
                    jclassV8ValueWeakMap,
                    jmethodIDV8ValueWeakMapConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsWeakSet()) {
                return jniEnv->NewObject(
                    jclassV8ValueWeakSet,
                    jmethodIDV8ValueWeakSetConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsMap()) {
                return jniEnv->NewObject(
                    jclassV8ValueMap,
                    jmethodIDV8ValueMapConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsSet()) {
                return jniEnv->NewObject(
                    jclassV8ValueSet,
                    jmethodIDV8ValueSetConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsMapIterator() || v8Value->IsSetIterator() || v8Value->IsGeneratorObject()) {
                return jniEnv->NewObject(
                    jclassV8ValueIterator,
                    jmethodIDV8ValueIteratorConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsArgumentsObject()) {
                return jniEnv->NewObject(
                    jclassV8ValueArguments,
                    jmethodIDV8ValueArgumentsConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsPromise()) {
                return jniEnv->NewObject(
                    jclassV8ValuePromise,
                    jmethodIDV8ValuePromiseConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsRegExp()) {
                return jniEnv->NewObject(
                    jclassV8ValueRegExp,
                    jmethodIDV8ValueRegExpConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsAsyncFunction()) {
                // It defaults to V8ValueFunction.
            }
            if (v8Value->IsGeneratorFunction()) {
                // It defaults to V8ValueFunction.
            }
            if (v8Value->IsProxy()) {
                // Proxy is also a function. So, it needs to be tested before IsFunction().
                return jniEnv->NewObject(
                    jclassV8ValueProxy,
                    jmethodIDV8ValueProxyConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsFunction()) {
                return jniEnv->NewObject(
                    jclassV8ValueFunction,
                    jmethodIDV8ValueFunctionConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsNativeError()) {
                return jniEnv->NewObject(
                    jclassV8ValueError,
                    jmethodIDV8ValueErrorConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsSymbolObject()) {
                return jniEnv->NewObject(
                    jclassV8ValueSymbolObject,
                    jmethodIDV8ValueSymbolObjectConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            if (v8Value->IsSymbol()) {
                return jniEnv->NewObject(
                    jclassV8ValueSymbol,
                    jmethodIDV8ValueSymbolConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            // Primitive types
            if (v8Value->IsBoolean() || v8Value->IsBooleanObject()) {
                return jniEnv->CallObjectMethod(
                    v8Runtime->externalV8Runtime,
                    jmethodIDV8RuntimeCreateV8ValueBoolean,
                    v8Value->IsTrue());
            }
            if (v8Value->IsInt32()) {
                return jniEnv->CallObjectMethod(
                    v8Runtime->externalV8Runtime,
                    jmethodIDV8RuntimeCreateV8ValueInteger,
                    v8Value->Int32Value(v8Context).FromMaybe(0));
            }
            if (v8Value->IsBigInt() || v8Value->IsBigIntObject()) {
                V8LocalBigInt v8LocalBigInt = v8Value->ToBigInt(v8Context).ToLocalChecked();
                int wordCount = v8LocalBigInt->WordCount();
                if (wordCount <= 1) {
                    return jniEnv->CallObjectMethod(
                        v8Runtime->externalV8Runtime,
                        jmethodIDV8RuntimeCreateV8ValueLong,
                        v8LocalBigInt->Int64Value());
                }
                else {
                    int signBit;
                    jlongArray mLongArray = jniEnv->NewLongArray(wordCount);
                    jboolean isCopy;
                    jlong* mLongArrayPointer = jniEnv->GetLongArrayElements(mLongArray, &isCopy);
                    v8LocalBigInt->ToWordsArray(&signBit, &wordCount, reinterpret_cast<uint64_t*>(mLongArrayPointer));
                    jniEnv->ReleaseLongArrayElements(mLongArray, mLongArrayPointer, 0);
                    jint signum = signBit == 0 ? 1 : -1;
                    return jniEnv->NewObject(
                        jclassV8ValueBigInteger,
                        jmethodIDV8ValueBigIntegerConstructor,
                        v8Runtime->externalV8Runtime,
                        signum,
                        mLongArray);
                }
            }
            if (v8Value->IsDate()) {
                auto v8Date = v8Value->ToObject(v8Context).ToLocalChecked().As<v8::Date>();
                return jniEnv->CallObjectMethod(
                    v8Runtime->externalV8Runtime,
                    jmethodIDV8RuntimeCreateV8ValueZonedDateTime,
                    static_cast<std::int64_t>(v8Date->ValueOf()));
            }
            if (v8Value->IsNumber() || v8Value->IsNumberObject()) {
                return jniEnv->CallObjectMethod(
                    v8Runtime->externalV8Runtime,
                    jmethodIDV8RuntimeCreateV8ValueDouble,
                    v8Value->NumberValue(v8Context).FromMaybe(0));
            }
            if (v8Value->IsString() || v8Value->IsStringObject()) {
                return ToExternalV8ValuePrimitive(
                    jniEnv,
                    jclassV8ValueString,
                    jmethodIDV8ValueStringConstructor,
                    v8Runtime,
                    v8Context,
                    v8Value);
            }
            if (v8Value->IsName()) {
                /*
                 * Name is handled by either String or Symbol.
                 * This block should not be entered.
                 */
            }
            if (v8Value->IsModule()) {
                return jniEnv->NewObject(
                    jclassV8Module,
                    jmethodIDV8ModuleConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            // Object needs to be the last one.
            if (v8Value->IsObject()) {
                return jniEnv->NewObject(
                    jclassV8ValueObject,
                    jmethodIDV8ValueObjectConstructor,
                    v8Runtime->externalV8Runtime,
                    ToV8PersistentReference(v8Context, v8Value));
            }
            // Something is wrong. It defaults to toString().
            return ToExternalV8ValuePrimitive(
                jniEnv,
                jclassV8ValueUnknown,
                jmethodIDV8ValueUnknownConstructor,
                v8Runtime,
                v8Context,
                v8Value);
        }

        jobjectArray ToExternalV8ValueArray(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const v8::FunctionCallbackInfo<v8::Value>& args) noexcept {
            jobjectArray v8ValueArray = nullptr;
            int argLength = args.Length();
            if (argLength > 0) {
                // TODO: Memory leak might take place.
                v8ValueArray = jniEnv->NewObjectArray(argLength, jclassV8Value, nullptr);
                for (int i = 0; i < argLength; ++i) {
                    jniEnv->SetObjectArrayElement(v8ValueArray, i, ToExternalV8Value(jniEnv, v8Runtime, v8Context, args[i]));
                }
            }
            return v8ValueArray;
        }

        jobjectArray ToExternalV8ValueArray(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalArray& v8LocalArray) noexcept {
            int length = v8LocalArray->Length();
            // TODO: Memory leak might take place.
            auto v8ValueArray = jniEnv->NewObjectArray(length, jclassV8Value, nullptr);
            ToExternalV8ValueArray(
                jniEnv,
                v8Runtime,
                v8Context,
                v8LocalArray,
                v8ValueArray,
                0,
                length);
            return v8ValueArray;
        }

        int ToExternalV8ValueArray(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalArray& v8LocalArray,
            jobjectArray v8Values,
            const int startIndex,
            const int endIndex) noexcept {
            int arrayLength = v8LocalArray->Length();
            int actualEndIndex = endIndex > arrayLength ? arrayLength : endIndex;
            int actualLength = actualEndIndex - startIndex;
            if (startIndex >= 0 && actualLength > 0) {
                for (int i = 0; i < actualLength; ++i) {
                    auto v8MaybeLocalValue = v8LocalArray->Get(v8Context, i + startIndex);
                    V8LocalValue v8LocalValue;
                    if (v8MaybeLocalValue.IsEmpty()) {
                        if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                            return i;
                        }
                    }
                    else {
                        v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
                    }
                    jniEnv->SetObjectArrayElement(
                        v8Values, i, ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8LocalValue));
                }
                return actualLength;
            }
            return 0;
        }

        jobject ToExternalV8ValueGlobalObject(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime) noexcept {
            return jniEnv->NewObject(
                jclassV8ValueGlobalObject,
                jmethodIDV8ValueGlobalObjectConstructor,
                v8Runtime->externalV8Runtime,
                TO_JAVA_LONG(&(v8Runtime->v8GlobalObject)));
        }

        jobject ToExternalV8ValueNull(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime) noexcept {
            return jniEnv->CallObjectMethod(
                v8Runtime->externalV8Runtime,
                jmethodIDV8RuntimeCreateV8ValueNull);
        }

        jobject ToExternalV8ValueUndefined(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime) noexcept {
            return jniEnv->CallObjectMethod(
                v8Runtime->externalV8Runtime,
                jmethodIDV8RuntimeCreateV8ValueUndefined);
        }

        jobject ToJavetScriptingError(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8TryCatch& v8TryCatch) noexcept {
            jobject jObjectException = ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8TryCatch.Exception());
            jstring jStringScriptResourceName = nullptr, jStringSourceLine = nullptr;
            int lineNumber = 0, startColumn = 0, endColumn = 0, startPosition = 0, endPosition = 0;
            auto v8LocalMessage = v8TryCatch.Message();
            if (!v8LocalMessage.IsEmpty()) {
                jStringScriptResourceName = ToJavaString(jniEnv, v8Context, v8LocalMessage->GetScriptResourceName());
                jStringSourceLine = ToJavaString(jniEnv, v8Context, v8LocalMessage->GetSourceLine(v8Context).FromMaybe(V8LocalString()));
                lineNumber = v8LocalMessage->GetLineNumber(v8Context).FromMaybe(0);
                startColumn = v8LocalMessage->GetStartColumn();
                endColumn = v8LocalMessage->GetEndColumn();
                startPosition = v8LocalMessage->GetStartPosition();
                endPosition = v8LocalMessage->GetEndPosition();
            }
            jobject javetScriptingError = jniEnv->NewObject(
                jclassJavetScriptingError,
                jmethodIDJavetScriptingErrorConstructor,
                jObjectException, jStringScriptResourceName, jStringSourceLine,
                lineNumber, startColumn, endColumn, startPosition, endPosition);
            DELETE_LOCAL_REF(jniEnv, jStringSourceLine);
            DELETE_LOCAL_REF(jniEnv, jStringScriptResourceName);
            DELETE_LOCAL_REF(jniEnv, jObjectException);
            return javetScriptingError;
        }

        V8LocalBigInt ToV8BigInt(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jint mSignum,
            const jlongArray mLongArray) noexcept {
            if (mSignum == 0) {
                return v8::BigInt::New(v8Context->GetIsolate(), 0);
            }
            else {
                jsize wordCount = jniEnv->GetArrayLength(mLongArray);
                if (wordCount == 0) {
                    return v8::BigInt::New(v8Context->GetIsolate(), 0);
                }
                else {
                    jboolean isCopy;
                    jlong* mLongArrayPointer = jniEnv->GetLongArrayElements(mLongArray, &isCopy);
                    int signBit = mSignum > 0 ? 0 : 1;
                    V8LocalBigInt v8LocalBigInt = v8::BigInt::NewFromWords(
                        v8Context, signBit, wordCount, reinterpret_cast<uint64_t*>(mLongArrayPointer)).ToLocalChecked();
                    jniEnv->ReleaseLongArrayElements(mLongArray, mLongArrayPointer, 0);
                    return v8LocalBigInt;
                }
            }
        }

        V8LocalContext ToV8Context(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobject obj) noexcept {
            if (IsV8ValueContext(jniEnv, obj)) {
                auto v8PersistentContext = TO_V8_PERSISTENT_CONTEXT_POINTER(jniEnv->CallLongMethod(obj, jmethodIDV8ContextGetHandle));
                return v8PersistentContext->Get(v8Context->GetIsolate());
            }
            return V8LocalContext();
        }

        std::unique_ptr<v8::ScriptOrigin> ToV8ScriptOringinPointer(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jstring mResourceName,
            const jint mResourceLineOffset,
            const jint mResourceColumnOffset,
            const jint mScriptId,
            const jboolean mIsWASM,
            const jboolean mIsModule) noexcept {
            return std::make_unique<v8::ScriptOrigin>(
                v8Context->GetIsolate(),
                ToV8String(jniEnv, v8Context, mResourceName),
                (int)mResourceLineOffset,
                (int)mResourceColumnOffset,
                false,
                (int)mScriptId,
                V8LocalValue(),
                false,
                (bool)mIsWASM,
                (bool)mIsModule,
                V8LocalPrimitiveArray());
        }

        V8LocalString ToV8String(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jstring mString) noexcept {
            if (mString == nullptr) {
                return V8LocalString();
            }
            const uint16_t* unmanagedString = jniEnv->GetStringChars(mString, nullptr);
            int length = jniEnv->GetStringLength(mString);
            auto twoByteString = v8::String::NewFromTwoByte(
                v8Context->GetIsolate(), unmanagedString, v8::NewStringType::kNormal, length);
            jniEnv->ReleaseStringChars(mString, unmanagedString);
            if (twoByteString.IsEmpty()) {
                return V8LocalString();
            }
            return twoByteString.ToLocalChecked();
        }

        V8LocalValue ToV8Value(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobject obj) noexcept {
            if (obj == nullptr || IsV8ValueNull(jniEnv, obj)) {
                return ToV8Null(v8Context);
            }
            else if (IsV8ValueInteger(jniEnv, obj)) {
                jint integerObject = ToJavaIntegerFromV8ValueInteger(jniEnv, obj);
                return ToV8Integer(v8Context, integerObject);
            }
            else if (IsV8ValueString(jniEnv, obj)) {
                jstring stringObject = ToJavaStringFromV8ValueString(jniEnv, obj);
                auto v8String = ToV8String(jniEnv, v8Context, stringObject);
                DELETE_LOCAL_REF(jniEnv, stringObject);
                return v8String;
            }
            else if (IsV8ValueBoolean(jniEnv, obj)) {
                jboolean booleanObject = jniEnv->CallBooleanMethod(obj, jmethodIDV8ValueBooleanToPrimitive);
                return ToV8Boolean(v8Context, booleanObject);
            }
            else if (IsV8ValueDouble(jniEnv, obj)) {
                jdouble doubleObject = jniEnv->CallDoubleMethod(obj, jmethodIDV8ValueDoubleToPrimitive);
                return ToV8Double(v8Context, doubleObject);
            }
            else if (IsV8ValueLong(jniEnv, obj)) {
                jlong longObject = jniEnv->CallLongMethod(obj, jmethodIDV8ValueLongToPrimitive);
                return ToV8Long(v8Context, longObject);
            }
            else if (IsV8ValueZonedDateTime(jniEnv, obj)) {
                jlong longObject = (jlong)jniEnv->CallLongMethod(obj, jmethodIDV8ValueZonedDateTimeToPrimitive);
                return ToV8Date(v8Context, longObject);
            }
            else if (IsV8ValueBigInteger(jniEnv, obj)) {
                jint signum = jniEnv->CallIntMethod(obj, jmethodIDV8ValueBigIntegerGetSignum);
                jlongArray longArray = (jlongArray)jniEnv->CallObjectMethod(obj, jmethodIDV8ValueBigIntegerGetLongArray);
                return ToV8BigInt(jniEnv, v8Context, signum, longArray);
            }
            else if (IsV8ValueReference(jniEnv, obj)) {
                if (IsV8ValueArray(jniEnv, obj)) {
                    return V8LocalArray::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_ARRAY(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueArrayGetHandle)));
                }
                else if (IsV8ValueGlobalObject(jniEnv, obj)) {
                    // Global object is a tricky one. 
                    return V8LocalObject::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_OBJECT(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueGlobalObjectGetHandle)));
                }
                else if (IsV8ValueMap(jniEnv, obj)) {
                    return V8LocalMap::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_MAP(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueMapGetHandle)));
                }
                else if (IsV8ValuePromise(jniEnv, obj)) {
                    return V8LocalPromise::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_PROMISE(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValuePromiseGetHandle)));
                }
                else if (IsV8ValueProxy(jniEnv, obj)) {
                    return V8LocalProxy::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_PROXY(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueProxyGetHandle)));
                }
                else if (IsV8ValueRegExp(jniEnv, obj)) {
                    return V8LocalRegExp::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_REG_EXP(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueRegExpGetHandle)));
                }
                else if (IsV8ValueSet(jniEnv, obj)) {
                    return V8LocalSet::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_SET(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueSetGetHandle)));
                }
                else if (IsV8ValueSymbol(jniEnv, obj)) {
                    return V8LocalSymbol::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_SYMBOL(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueSymbolGetHandle)));
                }
                else if (IsV8ValueSymbolObject(jniEnv, obj)) {
                    return V8LocalSymbolObject::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_SYMBOL_OBJECT(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueSymbolObjectGetHandle)));
                }
                else if (
                    IsV8ValueArguments(jniEnv, obj) ||
                    IsV8ValueError(jniEnv, obj) ||
                    IsV8ValueIterator(jniEnv, obj) ||
                    IsV8ValueObject(jniEnv, obj) ||
                    IsV8ValueWeakMap(jniEnv, obj) ||
                    IsV8ValueWeakSet(jniEnv, obj)) {
                    return V8LocalObject::New(v8Context->GetIsolate(), TO_V8_PERSISTENT_OBJECT(
                        jniEnv->CallLongMethod(obj, jmethodIDV8ValueObjectGetHandle)));
                }
            }
            return ToV8Undefined(v8Context);
        }

        std::unique_ptr<V8LocalObject[]> ToV8Objects(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobjectArray mObjects) noexcept {
            std::unique_ptr<V8LocalObject[]> umObjectsPointer;
            uint32_t count = mObjects == nullptr ? 0 : jniEnv->GetArrayLength(mObjects);
            if (count > 0) {
                umObjectsPointer.reset(new V8LocalObject[count]);
                for (uint32_t i = 0; i < count; ++i) {
                    jobject element = jniEnv->GetObjectArrayElement(mObjects, i);
                    umObjectsPointer.get()[i] = ToV8Value(jniEnv, v8Context, element).As<v8::Object>();
                    DELETE_LOCAL_REF(jniEnv, element);
                }
            }
            return umObjectsPointer;
        }

        std::unique_ptr<V8LocalString[]> ToV8Strings(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobjectArray mStrings) noexcept {
            std::unique_ptr<V8LocalString[]> umStringsPointer;
            uint32_t count = mStrings == nullptr ? 0 : jniEnv->GetArrayLength(mStrings);
            if (count > 0) {
                umStringsPointer.reset(new V8LocalString[count]);
                for (uint32_t i = 0; i < count; ++i) {
                    jstring element = (jstring)jniEnv->GetObjectArrayElement(mStrings, i);
                    umStringsPointer.get()[i] = ToV8String(jniEnv, v8Context, element);
                    DELETE_LOCAL_REF(jniEnv, element);
                }
            }
            return umStringsPointer;
        }

        std::unique_ptr<V8LocalValue[]> ToV8Values(
            JNIEnv* jniEnv,
            const V8LocalContext& v8Context,
            const jobjectArray mValues) noexcept {
            std::unique_ptr<V8LocalValue[]> umValuesPointer;
            uint32_t count = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
            if (count > 0) {
                umValuesPointer.reset(new V8LocalValue[count]);
                for (uint32_t i = 0; i < count; ++i) {
                    jobject element = jniEnv->GetObjectArrayElement(mValues, i);
                    umValuesPointer.get()[i] = ToV8Value(jniEnv, v8Context, element);
                    DELETE_LOCAL_REF(jniEnv, element);
                }
            }
            return umValuesPointer;
        }
    }
}
