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

#include <limits>
#include <new>

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

        jclass jclassV8ValueArray;
        jmethodID jmethodIDV8ValueArrayConstructor;

        jclass jclassV8ValueArrayBuffer;
        jmethodID jmethodIDV8ValueArrayBufferConstructor;

        jclass jclassV8ValueBooleanObject;
        jmethodID jmethodIDV8ValueBooleanObjectConstructor;

        jclass jclassV8ValueDataView;
        jmethodID jmethodIDV8ValueDataViewConstructor;

        jclass jclassV8ValueDoubleObject;
        jmethodID jmethodIDV8ValueDoubleObjectConstructor;

        jclass jclassV8ValueFunction;
        jmethodID jmethodIDV8ValueFunctionConstructor;

        jclass jclassV8ValueError;
        jmethodID jmethodIDV8ValueErrorConstructor;

        jclass jclassV8ValueGlobalObject;
        jmethodID jmethodIDV8ValueGlobalObjectConstructor;

        jclass jclassV8ValueIntegerObject;
        jmethodID jmethodIDV8ValueIntegerObjectConstructor;

        jclass jclassV8ValueIterator;
        jmethodID jmethodIDV8ValueIteratorConstructor;

        jclass jclassV8ValueLongObject;
        jmethodID jmethodIDV8ValueLongObjectConstructor;

        jclass jclassV8ValueMap;
        jmethodID jmethodIDV8ValueMapConstructor;

        jclass jclassV8ValueObject;
        jmethodID jmethodIDV8ValueObjectConstructor;

        jclass jclassV8ValuePromise;
        jmethodID jmethodIDV8ValuePromiseConstructor;

        jclass jclassV8ValueProxy;
        jmethodID jmethodIDV8ValueProxyConstructor;

        jclass jclassV8ValueReference;
        jmethodID jmethodIDV8ValueReferenceGetHandle;

        jclass jclassV8ValueRegExp;
        jmethodID jmethodIDV8ValueRegExpConstructor;

        jclass jclassV8ValueSet;
        jmethodID jmethodIDV8ValueSetConstructor;

        jclass jclassV8ValueSharedArrayBuffer;
        jmethodID jmethodIDV8ValueSharedArrayBufferConstructor;

        jclass jclassV8ValueStringObject;
        jmethodID jmethodIDV8ValueStringObjectConstructor;

        jmethodID jmethodIDV8ValueSymbolConstructor;

        jclass jclassV8ValueSymbolObject;
        jmethodID jmethodIDV8ValueSymbolObjectConstructor;

        jclass jclassV8ValueTypedArray;
        jmethodID jmethodIDV8ValueTypedArrayConstructor;

        jclass jclassV8ValueWeakMap;
        jmethodID jmethodIDV8ValueWeakMapConstructor;

        jclass jclassV8ValueWeakSet;
        jmethodID jmethodIDV8ValueWeakSetConstructor;

        // Misc

        jclass jclassJavetScriptingError;
        jmethodID jmethodIDJavetScriptingErrorConstructor;

        jclass jclassIV8ValueFunctionScriptSource;
        jmethodID jmethodIDIV8ValueFunctionScriptSourceConstructor;
        jmethodID jmethodIDIV8ValueFunctionScriptGetCode;
        jmethodID jmethodIDIV8ValueFunctionScriptGetEndPosition;
        jmethodID jmethodIDIV8ValueFunctionScriptGetStartPosition;

        constexpr auto JAVA_METHOD_TO_PRIMITIVE = "toPrimitive";

        static inline void AppendCodePointToUtf8(
            std::string& utf8String,
            uint32_t codePoint) {
            if (codePoint <= 0x7F) {
                utf8String.push_back(static_cast<char>(codePoint));
            }
            else if (codePoint <= 0x7FF) {
                utf8String.push_back(static_cast<char>(0xC0 | (codePoint >> 6)));
                utf8String.push_back(static_cast<char>(0x80 | (codePoint & 0x3F)));
            }
            else if (codePoint <= 0xFFFF) {
                utf8String.push_back(static_cast<char>(0xE0 | (codePoint >> 12)));
                utf8String.push_back(static_cast<char>(0x80 | ((codePoint >> 6) & 0x3F)));
                utf8String.push_back(static_cast<char>(0x80 | (codePoint & 0x3F)));
            }
            else {
                utf8String.push_back(static_cast<char>(0xF0 | (codePoint >> 18)));
                utf8String.push_back(static_cast<char>(0x80 | ((codePoint >> 12) & 0x3F)));
                utf8String.push_back(static_cast<char>(0x80 | ((codePoint >> 6) & 0x3F)));
                utf8String.push_back(static_cast<char>(0x80 | (codePoint & 0x3F)));
            }
        }

        static inline void AppendCodePointToUtf16(
            std::u16string& utf16String,
            uint32_t codePoint) {
            if (codePoint <= 0xFFFF) {
                utf16String.push_back(static_cast<char16_t>(codePoint));
            }
            else {
                codePoint -= 0x10000;
                utf16String.push_back(static_cast<char16_t>(0xD800 | (codePoint >> 10)));
                utf16String.push_back(static_cast<char16_t>(0xDC00 | (codePoint & 0x3FF)));
            }
        }

        jstring ToJavaStringFromUtf8(
            JNIEnv* jniEnv,
            const char* utf8String) noexcept {
            if (utf8String == nullptr) {
                return nullptr;
            }
            return ToJavaStringFromUtf8(
                jniEnv,
                utf8String,
                std::char_traits<char>::length(utf8String));
        }

        jstring ToJavaStringFromUtf8(
            JNIEnv* jniEnv,
            const char* utf8String,
            size_t length) noexcept {
            if (utf8String == nullptr) {
                return nullptr;
            }
            std::u16string utf16String;
            utf16String.reserve(length);
            size_t index = 0;
            while (index < length) {
                const auto firstByte = static_cast<uint8_t>(utf8String[index]);
                uint32_t codePoint = 0;
                uint32_t minimumCodePoint = 0;
                size_t sequenceLength = 0;
                if (firstByte <= 0x7F) {
                    codePoint = firstByte;
                    sequenceLength = 1;
                }
                else if ((firstByte & 0xE0) == 0xC0) {
                    codePoint = firstByte & 0x1F;
                    minimumCodePoint = 0x80;
                    sequenceLength = 2;
                }
                else if ((firstByte & 0xF0) == 0xE0) {
                    codePoint = firstByte & 0x0F;
                    minimumCodePoint = 0x800;
                    sequenceLength = 3;
                }
                else if ((firstByte & 0xF8) == 0xF0) {
                    codePoint = firstByte & 0x07;
                    minimumCodePoint = 0x10000;
                    sequenceLength = 4;
                }
                bool valid = sequenceLength > 0 && index + sequenceLength <= length;
                for (size_t offset = 1; valid && offset < sequenceLength; ++offset) {
                    const auto continuationByte = static_cast<uint8_t>(utf8String[index + offset]);
                    if ((continuationByte & 0xC0) != 0x80) {
                        valid = false;
                    }
                    else {
                        codePoint = (codePoint << 6) | (continuationByte & 0x3F);
                    }
                }
                valid = valid &&
                    codePoint >= minimumCodePoint &&
                    codePoint <= 0x10FFFF &&
                    (codePoint < 0xD800 || codePoint > 0xDFFF);
                if (!valid) {
                    codePoint = 0xFFFD;
                    sequenceLength = 1;
                }
                AppendCodePointToUtf16(utf16String, codePoint);
                index += sequenceLength;
            }
            return ToJavaStringFromUtf16(jniEnv, utf16String);
        }

        jstring ToJavaStringFromUtf8(
            JNIEnv* jniEnv,
            const std::string& utf8String) noexcept {
            return ToJavaStringFromUtf8(jniEnv, utf8String.data(), utf8String.length());
        }

        jstring ToJavaStringFromUtf16(
            JNIEnv* jniEnv,
            const std::u16string& utf16String) noexcept {
            static_assert(sizeof(char16_t) == sizeof(jchar));
            return jniEnv->NewString(
                reinterpret_cast<const jchar*>(utf16String.data()),
                static_cast<jsize>(utf16String.length()));
        }

        std::unique_ptr<std::string> ToUtf8String(
            JNIEnv* jniEnv,
            const jstring& mString) noexcept {
            auto utf16String = ToUtf16String(jniEnv, mString);
            return utf16String == nullptr ? nullptr : ToUtf8String(*utf16String);
        }

        std::unique_ptr<std::string> ToUtf8String(
            const std::u16string& utf16String) noexcept {
            auto utf8String = std::make_unique<std::string>();
            utf8String->reserve(utf16String.length() * 3);
            for (size_t index = 0; index < utf16String.length(); ++index) {
                uint32_t codePoint = utf16String[index];
                if (codePoint >= 0xD800 && codePoint <= 0xDBFF &&
                    index + 1 < utf16String.length()) {
                    const uint32_t lowSurrogate = utf16String[index + 1];
                    if (lowSurrogate >= 0xDC00 && lowSurrogate <= 0xDFFF) {
                        codePoint = 0x10000 +
                            ((codePoint - 0xD800) << 10) +
                            (lowSurrogate - 0xDC00);
                        ++index;
                    }
                    else {
                        codePoint = 0xFFFD;
                    }
                }
                else if (codePoint >= 0xD800 && codePoint <= 0xDFFF) {
                    codePoint = 0xFFFD;
                }
                AppendCodePointToUtf8(*utf8String, codePoint);
            }
            return utf8String;
        }

        std::unique_ptr<std::u16string> ToUtf16String(
            JNIEnv* jniEnv,
            const jstring& mString) noexcept {
            JNIStringChars utf16Chars(jniEnv, mString);
            if (!utf16Chars) {
                return nullptr;
            }
            const jsize length = jniEnv->GetStringLength(mString);
            auto utf16String = std::make_unique<std::u16string>();
            utf16String->resize(length);
            for (jsize index = 0; index < length; ++index) {
                (*utf16String)[index] = static_cast<char16_t>(utf16Chars.Get()[index]);
            }
            return utf16String;
        }

        bool Initialize(JNIEnv* jniEnv) noexcept {
            /*
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
             @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
            */

            JNIInitializer jniInitializer(jniEnv);

            // Runtime

            jniInitializer.FindGlobalClass(jclassV8Runtime, "com/caoccao/javet/interop/V8Runtime");
            jniInitializer.GetMethodID(jmethodIDV8RuntimeCreateV8ValueBoolean, jclassV8Runtime, "createV8ValueBoolean", "(Z)Lcom/caoccao/javet/values/primitive/V8ValueBoolean;");
            jniInitializer.GetMethodID(jmethodIDV8RuntimeCreateV8ValueDouble, jclassV8Runtime, "createV8ValueDouble", "(D)Lcom/caoccao/javet/values/primitive/V8ValueDouble;");
            jniInitializer.GetMethodID(jmethodIDV8RuntimeCreateV8ValueInteger, jclassV8Runtime, "createV8ValueInteger", "(I)Lcom/caoccao/javet/values/primitive/V8ValueInteger;");
            jniInitializer.GetMethodID(jmethodIDV8RuntimeCreateV8ValueLong, jclassV8Runtime, "createV8ValueLong", "(J)Lcom/caoccao/javet/values/primitive/V8ValueLong;");
            jniInitializer.GetMethodID(jmethodIDV8RuntimeCreateV8ValueNull, jclassV8Runtime, "createV8ValueNull", "()Lcom/caoccao/javet/values/primitive/V8ValueNull;");
            jniInitializer.GetMethodID(jmethodIDV8RuntimeCreateV8ValueUndefined, jclassV8Runtime, "createV8ValueUndefined", "()Lcom/caoccao/javet/values/primitive/V8ValueUndefined;");
            jniInitializer.GetMethodID(jmethodIDV8RuntimeCreateV8ValueZonedDateTime, jclassV8Runtime, "createV8ValueZonedDateTime", "(J)Lcom/caoccao/javet/values/primitive/V8ValueZonedDateTime;");

            // Primitive

            jniInitializer.FindGlobalClass(jclassV8Value, "com/caoccao/javet/values/V8Value");

            jniInitializer.FindGlobalClass(jclassV8ValueBigInteger, "com/caoccao/javet/values/primitive/V8ValueBigInteger");
            jniInitializer.GetMethodID(jmethodIDV8ValueBigIntegerConstructor, jclassV8ValueBigInteger, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;I[J)V");
            jniInitializer.GetMethodID(jmethodIDV8ValueBigIntegerGetLongArray, jclassV8ValueBigInteger, "getLongArray", "()[J");
            jniInitializer.GetMethodID(jmethodIDV8ValueBigIntegerGetSignum, jclassV8ValueBigInteger, "getSignum", "()I");

            jniInitializer.FindGlobalClass(jclassV8ValueBoolean, "com/caoccao/javet/values/primitive/V8ValueBoolean");
            jniInitializer.GetMethodID(jmethodIDV8ValueBooleanToPrimitive, jclassV8ValueBoolean, JAVA_METHOD_TO_PRIMITIVE, "()Z");

            jniInitializer.FindGlobalClass(jclassV8ValueDouble, "com/caoccao/javet/values/primitive/V8ValueDouble");
            jniInitializer.GetMethodID(jmethodIDV8ValueDoubleToPrimitive, jclassV8ValueDouble, JAVA_METHOD_TO_PRIMITIVE, "()D");

            jniInitializer.FindGlobalClass(jclassV8ValueInteger, "com/caoccao/javet/values/primitive/V8ValueInteger");
            jniInitializer.GetMethodID(jmethodIDV8ValueIntegerToPrimitive, jclassV8ValueInteger, JAVA_METHOD_TO_PRIMITIVE, "()I");

            jniInitializer.FindGlobalClass(jclassV8ValueLong, "com/caoccao/javet/values/primitive/V8ValueLong");
            jniInitializer.GetMethodID(jmethodIDV8ValueLongToPrimitive, jclassV8ValueLong, JAVA_METHOD_TO_PRIMITIVE, "()J");

            jniInitializer.FindGlobalClass(jclassV8ValueNull, "com/caoccao/javet/values/primitive/V8ValueNull");

            jniInitializer.FindGlobalClass(jclassV8ValueString, "com/caoccao/javet/values/primitive/V8ValueString");
            jniInitializer.GetMethodID(jmethodIDV8ValueStringConstructor, jclassV8ValueString, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;Ljava/lang/String;)V");
            jniInitializer.GetMethodID(jmethodIDV8ValueStringToPrimitive, jclassV8ValueString, JAVA_METHOD_TO_PRIMITIVE, "()Ljava/lang/String;");

            jniInitializer.FindGlobalClass(jclassV8ValueUndefined, "com/caoccao/javet/values/primitive/V8ValueUndefined");

            jniInitializer.FindGlobalClass(jclassV8ValueUnknown, "com/caoccao/javet/values/primitive/V8ValueUnknown");
            jniInitializer.GetMethodID(jmethodIDV8ValueUnknownConstructor, jclassV8ValueUnknown, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;Ljava/lang/String;)V");

            jniInitializer.FindGlobalClass(jclassV8ValueZonedDateTime, "com/caoccao/javet/values/primitive/V8ValueZonedDateTime");
            jniInitializer.GetMethodID(jmethodIDV8ValueZonedDateTimeToPrimitive, jclassV8ValueZonedDateTime, JAVA_METHOD_TO_PRIMITIVE, "()J");

            // Reference

            jniInitializer.FindGlobalClass(jclassV8Context, "com/caoccao/javet/values/reference/V8Context");
            jniInitializer.GetMethodID(jmethodIDV8ContextConstructor, jclassV8Context, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");
            jniInitializer.GetMethodID(jmethodIDV8ContextGetHandle, jclassV8Context, "getHandle", "()J");

            jniInitializer.FindGlobalClass(jclassV8Module, "com/caoccao/javet/values/reference/V8Module");
            jniInitializer.GetMethodID(jmethodIDV8ModuleConstructor, jclassV8Module, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");
            jniInitializer.GetMethodID(jmethodIDV8ModuleGetHandle, jclassV8Module, "getHandle", "()J");

            jniInitializer.FindGlobalClass(jclassV8Script, "com/caoccao/javet/values/reference/V8Script");
            jniInitializer.GetMethodID(jmethodIDV8ScriptConstructor, jclassV8Script, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");
            jniInitializer.GetMethodID(jmethodIDV8ScriptGetHandle, jclassV8Script, "getHandle", "()J");

            jniInitializer.FindGlobalClass(jclassV8ValueArguments, "com/caoccao/javet/values/reference/V8ValueArguments");
            jniInitializer.GetMethodID(jmethodIDV8ValueArgumentsConstructor, jclassV8ValueArguments, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueArray, "com/caoccao/javet/values/reference/V8ValueArray");
            jniInitializer.GetMethodID(jmethodIDV8ValueArrayConstructor, jclassV8ValueArray, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueArrayBuffer, "com/caoccao/javet/values/reference/V8ValueArrayBuffer");
            jniInitializer.GetMethodID(jmethodIDV8ValueArrayBufferConstructor, jclassV8ValueArrayBuffer, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;JLjava/nio/ByteBuffer;)V");

            jniInitializer.FindGlobalClass(jclassV8ValueBooleanObject, "com/caoccao/javet/values/reference/V8ValueBooleanObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueBooleanObjectConstructor, jclassV8ValueBooleanObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueDataView, "com/caoccao/javet/values/reference/V8ValueDataView");
            jniInitializer.GetMethodID(jmethodIDV8ValueDataViewConstructor, jclassV8ValueDataView, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueDoubleObject, "com/caoccao/javet/values/reference/V8ValueDoubleObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueDoubleObjectConstructor, jclassV8ValueDoubleObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueFunction, "com/caoccao/javet/values/reference/V8ValueFunction");
            jniInitializer.GetMethodID(jmethodIDV8ValueFunctionConstructor, jclassV8ValueFunction, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueError, "com/caoccao/javet/values/reference/V8ValueError");
            jniInitializer.GetMethodID(jmethodIDV8ValueErrorConstructor, jclassV8ValueError, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueGlobalObject, "com/caoccao/javet/values/reference/V8ValueGlobalObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueGlobalObjectConstructor, jclassV8ValueGlobalObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueIntegerObject, "com/caoccao/javet/values/reference/V8ValueIntegerObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueIntegerObjectConstructor, jclassV8ValueIntegerObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueIterator, "com/caoccao/javet/values/reference/V8ValueIterator");
            jniInitializer.GetMethodID(jmethodIDV8ValueIteratorConstructor, jclassV8ValueIterator, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueLongObject, "com/caoccao/javet/values/reference/V8ValueLongObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueLongObjectConstructor, jclassV8ValueLongObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueMap, "com/caoccao/javet/values/reference/V8ValueMap");
            jniInitializer.GetMethodID(jmethodIDV8ValueMapConstructor, jclassV8ValueMap, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueObject, "com/caoccao/javet/values/reference/V8ValueObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueObjectConstructor, jclassV8ValueObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValuePromise, "com/caoccao/javet/values/reference/V8ValuePromise");
            jniInitializer.GetMethodID(jmethodIDV8ValuePromiseConstructor, jclassV8ValuePromise, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueProxy, "com/caoccao/javet/values/reference/V8ValueProxy");
            jniInitializer.GetMethodID(jmethodIDV8ValueProxyConstructor, jclassV8ValueProxy, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueReference, "com/caoccao/javet/values/reference/V8ValueReference");
            jniInitializer.GetMethodID(jmethodIDV8ValueReferenceGetHandle, jclassV8ValueReference, "getHandle", "()J");

            jniInitializer.FindGlobalClass(jclassV8ValueRegExp, "com/caoccao/javet/values/reference/V8ValueRegExp");
            jniInitializer.GetMethodID(jmethodIDV8ValueRegExpConstructor, jclassV8ValueRegExp, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueSet, "com/caoccao/javet/values/reference/V8ValueSet");
            jniInitializer.GetMethodID(jmethodIDV8ValueSetConstructor, jclassV8ValueSet, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueSharedArrayBuffer, "com/caoccao/javet/values/reference/V8ValueSharedArrayBuffer");
            jniInitializer.GetMethodID(jmethodIDV8ValueSharedArrayBufferConstructor, jclassV8ValueSharedArrayBuffer, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;JLjava/nio/ByteBuffer;)V");

            jniInitializer.FindGlobalClass(jclassV8ValueStringObject, "com/caoccao/javet/values/reference/V8ValueStringObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueStringObjectConstructor, jclassV8ValueStringObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueSymbol, "com/caoccao/javet/values/reference/V8ValueSymbol");
            jniInitializer.GetMethodID(jmethodIDV8ValueSymbolConstructor, jclassV8ValueSymbol, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueSymbolObject, "com/caoccao/javet/values/reference/V8ValueSymbolObject");
            jniInitializer.GetMethodID(jmethodIDV8ValueSymbolObjectConstructor, jclassV8ValueSymbolObject, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueTypedArray, "com/caoccao/javet/values/reference/V8ValueTypedArray");
            jniInitializer.GetMethodID(jmethodIDV8ValueTypedArrayConstructor, jclassV8ValueTypedArray, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;JI)V");

            jniInitializer.FindGlobalClass(jclassV8ValueWeakMap, "com/caoccao/javet/values/reference/V8ValueWeakMap");
            jniInitializer.GetMethodID(jmethodIDV8ValueWeakMapConstructor, jclassV8ValueWeakMap, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            jniInitializer.FindGlobalClass(jclassV8ValueWeakSet, "com/caoccao/javet/values/reference/V8ValueWeakSet");
            jniInitializer.GetMethodID(jmethodIDV8ValueWeakSetConstructor, jclassV8ValueWeakSet, "<init>", "(Lcom/caoccao/javet/interop/V8Runtime;J)V");

            // Misc
            jniInitializer.FindGlobalClass(jclassByteBuffer, "java/nio/ByteBuffer");
            jniInitializer.FindGlobalClass(jclassString, "java/lang/String");

            jniInitializer.FindGlobalClass(jclassJavetScriptingError, "com/caoccao/javet/exceptions/JavetScriptingError");
            jniInitializer.GetMethodID(jmethodIDJavetScriptingErrorConstructor, jclassJavetScriptingError, "<init>", "(Lcom/caoccao/javet/values/V8Value;Ljava/lang/String;Ljava/lang/String;IIIII)V");

            jniInitializer.FindGlobalClass(jclassIV8ValueFunctionScriptSource, "com/caoccao/javet/values/reference/IV8ValueFunction$ScriptSource");
            jniInitializer.GetMethodID(jmethodIDIV8ValueFunctionScriptSourceConstructor, jclassIV8ValueFunctionScriptSource, "<init>", "(Ljava/lang/String;II)V");
            jniInitializer.GetMethodID(jmethodIDIV8ValueFunctionScriptGetCode, jclassIV8ValueFunctionScriptSource, "getCode", "()Ljava/lang/String;");
            jniInitializer.GetMethodID(jmethodIDIV8ValueFunctionScriptGetEndPosition, jclassIV8ValueFunctionScriptSource, "getEndPosition", "()I");
            jniInitializer.GetMethodID(jmethodIDIV8ValueFunctionScriptGetStartPosition, jclassIV8ValueFunctionScriptSource, "getStartPosition", "()I");
            return jniInitializer.IsValid();
        }

        V8ScriptCompilerCachedData* ToCachedDataPointer(
            JNIEnv* jniEnv,
            const jbyteArray mCachedArray) noexcept {
            if (mCachedArray == nullptr) {
                return nullptr;
            }
            jsize length = jniEnv->GetArrayLength(mCachedArray);
            std::unique_ptr<uint8_t[]> bytes(new (std::nothrow) uint8_t[length]);
            if (!bytes) {
                return nullptr;
            }
            if (length > 0) {
                jniEnv->GetByteArrayRegion(
                    mCachedArray,
                    0,
                    length,
                    reinterpret_cast<jbyte*>(bytes.get()));
                if (jniEnv->ExceptionCheck()) {
                    return nullptr;
                }
            }
            auto cachedDataPointer = new (std::nothrow) V8ScriptCompilerCachedData(
                bytes.get(),
                length,
                V8ScriptCompilerCachedDataBufferPolicy::BufferOwned);
            if (cachedDataPointer != nullptr) {
                bytes.release();
            }
            return cachedDataPointer;
        }

        jbyteArray ToJavaByteArray(
            JNIEnv* jniEnv,
            const V8ScriptCompilerCachedData* cachedDataPointer) noexcept {
            if (cachedDataPointer == nullptr ||
                cachedDataPointer->length > static_cast<size_t>(std::numeric_limits<jsize>::max())) {
                return nullptr;
            }
            jbyteArray byteArray = jniEnv->NewByteArray(static_cast<jsize>(cachedDataPointer->length));
            if (byteArray == nullptr) {
                return nullptr;
            }
            if (cachedDataPointer->length > 0) {
                jniEnv->SetByteArrayRegion(
                    byteArray,
                    0,
                    static_cast<jsize>(cachedDataPointer->length),
                    reinterpret_cast<const jbyte*>(cachedDataPointer->data));
                if (jniEnv->ExceptionCheck()) {
                    DELETE_LOCAL_REF(jniEnv, byteArray);
                    return nullptr;
                }
            }
            return byteArray;
        }

        jobject ToExternalV8Context(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8ContextValue) noexcept {
            return ToExternalV8Reference(
                jniEnv,
                jclassV8Context,
                jmethodIDV8ContextConstructor,
                v8Runtime,
                v8ContextValue);
        }

        jobject ToExternalV8Module(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalModule& v8Module) noexcept {
            return ToExternalV8Reference(
                jniEnv,
                jclassV8Module,
                jmethodIDV8ModuleConstructor,
                v8Runtime,
                v8Module);
        }

        jobject ToExternalV8Script(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalScript& v8Script) noexcept {
            return ToExternalV8Reference(
                jniEnv,
                jclassV8Script,
                jmethodIDV8ScriptConstructor,
                v8Runtime,
                v8Script);
        }

        jobject ToExternalV8Value(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const v8::internal::Tagged<V8InternalObject>& v8InternalObject) noexcept {
            auto v8InternalIsolate = reinterpret_cast<V8InternalIsolate*>(v8Runtime->v8Isolate);
            if (v8::internal::IsJSObject(v8InternalObject) || v8::internal::IsPrimitive(v8InternalObject)
                || v8::internal::IsJSArray(v8InternalObject) || v8::internal::IsJSTypedArray(v8InternalObject)) {
                auto v8LocalObject = v8::Utils::ToLocal(v8::internal::handle(v8InternalObject, v8InternalIsolate));
                return ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8LocalObject);
            }
            else if (v8::internal::IsContext(v8InternalObject)) {
                auto v8InternalContext = v8::internal::Cast<V8InternalNativeContext>(v8InternalObject);
                auto v8LocalContext = v8::Utils::ToLocal(v8::internal::handle(v8InternalContext, v8InternalIsolate));
                return ToExternalV8Context(jniEnv, v8Runtime, v8LocalContext);
            }
            else if (v8::internal::IsModule(v8InternalObject)) {
                auto v8LocalModule = v8::Utils::ToLocal(v8::internal::handle(v8::internal::Cast<V8InternalModule>(v8InternalObject), v8InternalIsolate));
                return ToExternalV8Module(jniEnv, v8Runtime, v8LocalModule);
            }
            else if (v8::internal::IsScript(v8InternalObject)) {
                LOG_DEBUG("Converter: Script is not supported.");
            }
            else if (v8::internal::IsCode(v8InternalObject)) {
                LOG_DEBUG("Converter: Code is not supported.");
            }
            return ToExternalV8ValueUndefined(jniEnv, v8Runtime);
        }

        jobject ToExternalV8Value(
            JNIEnv* jniEnv,
            const V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalValue& v8Value) noexcept {
            using V8ValueType = Javet::Enums::V8ValueType;
            if (v8Value->IsUndefined()) {
                return ToExternalV8ValueUndefined(jniEnv, v8Runtime);
            }
            if (v8Value->IsNull()) {
                return ToExternalV8ValueNull(jniEnv, v8Runtime);
            }
            // Reference types
            if (v8Value->IsObject()) {
                if (v8Value->IsArray()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueArray,
                        jmethodIDV8ValueArrayConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsTypedArray()) {
                    jint type = static_cast<jint>(V8ValueType::Invalid);
                    if (v8Value->IsBigInt64Array()) {
                        type = static_cast<jint>(V8ValueType::BigInt64Array);
                    }
                    else if (v8Value->IsBigUint64Array()) {
                        type = static_cast<jint>(V8ValueType::BigUint64Array);
                    }
                    else if (v8Value->IsFloat32Array()) {
                        type = static_cast<jint>(V8ValueType::Float32Array);
                    }
                    else if (v8Value->IsFloat64Array()) {
                        type = static_cast<jint>(V8ValueType::Float64Array);
                    }
                    else if (v8Value->IsInt16Array()) {
                        type = static_cast<jint>(V8ValueType::Int16Array);
                    }
                    else if (v8Value->IsInt32Array()) {
                        type = static_cast<jint>(V8ValueType::Int32Array);
                    }
                    else if (v8Value->IsInt8Array()) {
                        type = static_cast<jint>(V8ValueType::Int8Array);
                    }
                    else if (v8Value->IsUint16Array()) {
                        type = static_cast<jint>(V8ValueType::Uint16Array);
                    }
                    else if (v8Value->IsUint32Array()) {
                        type = static_cast<jint>(V8ValueType::Uint32Array);
                    }
                    else if (v8Value->IsUint8Array()) {
                        type = static_cast<jint>(V8ValueType::Uint8Array);
                    }
                    else if (v8Value->IsUint8ClampedArray()) {
                        type = static_cast<jint>(V8ValueType::Uint8ClampedArray);
                    }
                    else if (v8Value->IsFloat16Array()) {
                        type = static_cast<jint>(V8ValueType::Float16Array);
                    }
                    if (type != static_cast<jint>(V8ValueType::Invalid)) {
                        return ToExternalV8Reference(
                            jniEnv,
                            jclassV8ValueTypedArray,
                            jmethodIDV8ValueTypedArrayConstructor,
                            v8Runtime,
                            v8Value,
                            type);
                    }
                }
                if (v8Value->IsDataView()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueDataView,
                        jmethodIDV8ValueDataViewConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsArrayBuffer()) {
                    auto v8ArrayBuffer = v8Value.As<v8::ArrayBuffer>();
                    jobject directByteBuffer = jniEnv->NewDirectByteBuffer(
                        v8ArrayBuffer->GetBackingStore()->Data(),
                        v8ArrayBuffer->ByteLength());
                    if (directByteBuffer == nullptr) {
                        return nullptr;
                    }
                    if (jniEnv->ExceptionCheck()) {
                        DELETE_LOCAL_REF(jniEnv, directByteBuffer);
                        return nullptr;
                    }
                    jobject v8ValueArrayBuffer = ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueArrayBuffer,
                        jmethodIDV8ValueArrayBufferConstructor,
                        v8Runtime,
                        v8Value,
                        directByteBuffer);
                    DELETE_LOCAL_REF(jniEnv, directByteBuffer);
                    return v8ValueArrayBuffer;
                }
                if (v8Value->IsSharedArrayBuffer()) {
                    auto v8SharedArrayBuffer = v8Value.As<v8::SharedArrayBuffer>();
                    jobject directByteBuffer = jniEnv->NewDirectByteBuffer(
                        v8SharedArrayBuffer->GetBackingStore()->Data(),
                        v8SharedArrayBuffer->ByteLength());
                    if (directByteBuffer == nullptr) {
                        return nullptr;
                    }
                    if (jniEnv->ExceptionCheck()) {
                        DELETE_LOCAL_REF(jniEnv, directByteBuffer);
                        return nullptr;
                    }
                    jobject v8ValueSharedArrayBuffer = ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueSharedArrayBuffer,
                        jmethodIDV8ValueSharedArrayBufferConstructor,
                        v8Runtime,
                        v8Value,
                        directByteBuffer);
                    DELETE_LOCAL_REF(jniEnv, directByteBuffer);
                    return v8ValueSharedArrayBuffer;
                }
                if (v8Value->IsArrayBufferView()) {
                    /*
                     * ArrayBufferView is a helper type representing any of typed array or DataView.
                     * This block shouldn't be entered.
                     */
                }
                if (v8Value->IsWeakMap()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueWeakMap,
                        jmethodIDV8ValueWeakMapConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsWeakSet()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueWeakSet,
                        jmethodIDV8ValueWeakSetConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsMap()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueMap,
                        jmethodIDV8ValueMapConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsSet()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueSet,
                        jmethodIDV8ValueSetConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsMapIterator() || v8Value->IsSetIterator() || v8Value->IsGeneratorObject()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueIterator,
                        jmethodIDV8ValueIteratorConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsArgumentsObject()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueArguments,
                        jmethodIDV8ValueArgumentsConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsPromise()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValuePromise,
                        jmethodIDV8ValuePromiseConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsRegExp()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueRegExp,
                        jmethodIDV8ValueRegExpConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsAsyncFunction()) {
                    // It defaults to V8ValueFunction.
                }
                if (v8Value->IsGeneratorFunction()) {
                    // It defaults to V8ValueFunction.
                }
                if (v8Value->IsProxy()) {
                    // Proxy is also a function. So, it needs to be tested before IsFunction().
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueProxy,
                        jmethodIDV8ValueProxyConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsFunction()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueFunction,
                        jmethodIDV8ValueFunctionConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsNativeError()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueError,
                        jmethodIDV8ValueErrorConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsDate()) {
                    auto v8Date = v8Value->ToObject(v8Context).ToLocalChecked().As<v8::Date>();
                    return jniEnv->CallObjectMethod(
                        v8Runtime->externalV8Runtime,
                        jmethodIDV8RuntimeCreateV8ValueZonedDateTime,
                        static_cast<std::int64_t>(v8Date->ValueOf()));
                }
                if (v8Value->IsSymbolObject()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueSymbolObject,
                        jmethodIDV8ValueSymbolObjectConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsStringObject()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueStringObject,
                        jmethodIDV8ValueStringObjectConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsNumberObject()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueDoubleObject,
                        jmethodIDV8ValueDoubleObjectConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsBooleanObject()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueBooleanObject,
                        jmethodIDV8ValueBooleanObjectConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsBigIntObject()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8ValueLongObject,
                        jmethodIDV8ValueLongObjectConstructor,
                        v8Runtime,
                        v8Value);
                }
                if (v8Value->IsName()) {
                    /*
                     * Name is handled by either String or Symbol.
                     * This block should not be entered.
                     */
                }
                if (v8Value->IsModule()) {
                    return ToExternalV8Reference(
                        jniEnv,
                        jclassV8Module,
                        jmethodIDV8ModuleConstructor,
                        v8Runtime,
                        v8Value);
                }
                return ToExternalV8Reference(
                    jniEnv,
                    jclassV8ValueObject,
                    jmethodIDV8ValueObjectConstructor,
                    v8Runtime,
                    v8Value);
            }
            // Primitive types
            if (v8Value->IsBoolean()) {
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
            if (v8Value->IsNumber()) {
                return jniEnv->CallObjectMethod(
                    v8Runtime->externalV8Runtime,
                    jmethodIDV8RuntimeCreateV8ValueDouble,
                    v8Value->NumberValue(v8Context).FromMaybe(0));
            }
            if (v8Value->IsString()) {
                return ToExternalV8ValuePrimitive(
                    jniEnv,
                    jclassV8ValueString,
                    jmethodIDV8ValueStringConstructor,
                    v8Runtime,
                    v8Context,
                    v8Value);
            }
            if (v8Value->IsSymbol()) {
                return ToExternalV8Reference(
                    jniEnv,
                    jclassV8ValueSymbol,
                    jmethodIDV8ValueSymbolConstructor,
                    v8Runtime,
                    v8Value);
            }
            if (v8Value->IsBigInt()) {
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
                    if (mLongArray == nullptr) {
                        return nullptr;
                    }
                    auto words = std::unique_ptr<jlong[]>(new (std::nothrow) jlong[wordCount]);
                    if (!words) {
                        DELETE_LOCAL_REF(jniEnv, mLongArray);
                        return nullptr;
                    }
                    v8LocalBigInt->ToWordsArray(
                        &signBit,
                        &wordCount,
                        reinterpret_cast<uint64_t*>(words.get()));
                    jniEnv->SetLongArrayRegion(mLongArray, 0, wordCount, words.get());
                    if (jniEnv->ExceptionCheck()) {
                        DELETE_LOCAL_REF(jniEnv, mLongArray);
                        return nullptr;
                    }
                    jint signum = signBit == 0 ? 1 : -1;
                    jobject v8ValueBigInteger = jniEnv->NewObject(
                        jclassV8ValueBigInteger,
                        jmethodIDV8ValueBigIntegerConstructor,
                        v8Runtime->externalV8Runtime,
                        signum,
                        mLongArray);
                    DELETE_LOCAL_REF(jniEnv, mLongArray);
                    return v8ValueBigInteger;
                }
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
                v8ValueArray = jniEnv->NewObjectArray(argLength, jclassV8Value, nullptr);
                if (v8ValueArray == nullptr) {
                    return nullptr;
                }
                for (int i = 0; i < argLength; ++i) {
                    jobject v8Value = ToExternalV8Value(jniEnv, v8Runtime, v8Context, args[i]);
                    if (jniEnv->ExceptionCheck()) {
                        DELETE_LOCAL_REF(jniEnv, v8Value);
                        DELETE_LOCAL_REF(jniEnv, v8ValueArray);
                        return nullptr;
                    }
                    jniEnv->SetObjectArrayElement(v8ValueArray, i, v8Value);
                    DELETE_LOCAL_REF(jniEnv, v8Value);
                    if (jniEnv->ExceptionCheck()) {
                        DELETE_LOCAL_REF(jniEnv, v8ValueArray);
                        return nullptr;
                    }
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
            auto v8ValueArray = jniEnv->NewObjectArray(length, jclassV8Value, nullptr);
            if (v8ValueArray == nullptr) {
                return nullptr;
            }
            ToExternalV8ValueArray(
                jniEnv,
                v8Runtime,
                v8Context,
                v8LocalArray,
                length,
                v8ValueArray,
                0,
                length);
            if (jniEnv->ExceptionCheck()) {
                DELETE_LOCAL_REF(jniEnv, v8ValueArray);
                return nullptr;
            }
            return v8ValueArray;
        }

        int ToExternalV8ValueArray(
            JNIEnv* jniEnv,
            V8Runtime* v8Runtime,
            const V8LocalContext& v8Context,
            const V8LocalObject& v8LocalObject,
            const int arrayLength,
            jobjectArray v8Values,
            const int startIndex,
            const int endIndex) noexcept {
            if (v8Values == nullptr) {
                return 0;
            }
            int v8ValueLength = jniEnv->GetArrayLength(v8Values);
            int actualEndIndex = endIndex > arrayLength ? arrayLength : endIndex;
            int actualLength = actualEndIndex - startIndex;
            actualLength = actualLength > v8ValueLength ? v8ValueLength : actualLength;
            if (startIndex >= 0 && actualLength > 0) {
                for (int i = 0; i < actualLength; ++i) {
                    auto v8MaybeLocalValue = v8LocalObject->Get(v8Context, i + startIndex);
                    V8LocalValue v8LocalValue;
                    if (v8MaybeLocalValue.IsEmpty()) {
                        if (Javet::Exceptions::HandlePendingException(jniEnv, v8Runtime, v8Context)) {
                            return i;
                        }
                    }
                    else {
                        v8LocalValue = v8MaybeLocalValue.ToLocalChecked();
                    }
                    jobject v8Value = ToExternalV8Value(jniEnv, v8Runtime, v8Context, v8LocalValue);
                    if (!jniEnv->ExceptionCheck()) {
                        jniEnv->SetObjectArrayElement(v8Values, i, v8Value);
                    }
                    DELETE_LOCAL_REF(jniEnv, v8Value);
                    if (jniEnv->ExceptionCheck()) {
                        return i;
                    }
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
                jStringScriptResourceName = ToJavaStringFromV8String(
                    jniEnv, v8Runtime->v8Isolate, v8LocalMessage->GetScriptResourceName());
                jStringSourceLine = ToJavaStringFromV8String(
                    jniEnv, v8Runtime->v8Isolate, v8LocalMessage->GetSourceLine(v8Context).FromMaybe(V8LocalString()));
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
            V8Isolate* v8Isolate,
            const V8LocalContext& v8Context,
            const jint mSignum,
            const jlongArray mLongArray) noexcept {
            if (mSignum == 0) {
                return v8::BigInt::New(v8Isolate, 0);
            }
            else {
                if (mLongArray == nullptr) {
                    return v8::BigInt::New(v8Isolate, 0);
                }
                jsize wordCount = jniEnv->GetArrayLength(mLongArray);
                if (wordCount == 0) {
                    return v8::BigInt::New(v8Isolate, 0);
                }
                else {
                    auto words = std::unique_ptr<jlong[]>(new (std::nothrow) jlong[wordCount]);
                    if (!words) {
                        return v8::BigInt::New(v8Isolate, 0);
                    }
                    jniEnv->GetLongArrayRegion(mLongArray, 0, wordCount, words.get());
                    if (jniEnv->ExceptionCheck()) {
                        return v8::BigInt::New(v8Isolate, 0);
                    }
                    int signBit = mSignum > 0 ? 0 : 1;
                    V8LocalBigInt v8LocalBigInt = v8::BigInt::NewFromWords(
                        v8Context,
                        signBit,
                        wordCount,
                        reinterpret_cast<uint64_t*>(words.get())).ToLocalChecked();
                    return v8LocalBigInt;
                }
            }
        }

        V8LocalContext ToV8Context(
            JNIEnv* jniEnv,
            V8Isolate* v8Isolate,
            const jobject obj,
            const jint valueType) noexcept {
            if (IS_V8_CONTEXT(valueType)) {
                auto v8PersistentContext = TO_V8_PERSISTENT_CONTEXT_POINTER(jniEnv->CallLongMethod(obj, jmethodIDV8ContextGetHandle));
                return v8PersistentContext->Get(v8Isolate);
            }
            return V8LocalContext();
        }

        std::unique_ptr<v8::ScriptOrigin> ToV8ScriptOringinPointer(
            JNIEnv* jniEnv,
            V8Isolate* v8Isolate,
            const jstring mResourceName,
            const jint mResourceLineOffset,
            const jint mResourceColumnOffset,
            const jint mScriptId,
            const jboolean mIsWASM,
            const jboolean mIsModule) noexcept {
            return std::make_unique<v8::ScriptOrigin>(
                ToV8String(jniEnv, v8Isolate, mResourceName),
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
            V8Isolate* v8Isolate,
            const jstring mString) noexcept {
            if (mString == nullptr) {
                return V8LocalString();
            }
            JNIStringChars unmanagedString(jniEnv, mString);
            if (!unmanagedString) {
                return V8LocalString();
            }
            int length = jniEnv->GetStringLength(mString);
            auto twoByteString = v8::String::NewFromTwoByte(
                v8Isolate,
                reinterpret_cast<const uint16_t*>(unmanagedString.Get()),
                v8::NewStringType::kNormal,
                length);
            if (twoByteString.IsEmpty()) {
                return V8LocalString();
            }
            return twoByteString.ToLocalChecked();
        }

        V8LocalValue ToV8Value(
            JNIEnv* jniEnv,
            V8Isolate* v8Isolate,
            const V8LocalContext& v8Context,
            const jobject obj,
            const jint valueType) noexcept {
            using V8ValueType = Javet::Enums::V8ValueType;
            if (obj == nullptr) {
                return ToV8Null(v8Isolate);
            }
            const auto v8ValueType = static_cast<V8ValueType>(valueType);
            switch (v8ValueType) {
            case V8ValueType::Invalid:
                return ToV8Undefined(v8Isolate);
            case V8ValueType::Null:
                return ToV8Null(v8Isolate);
            case V8ValueType::Undefined:
                return ToV8Undefined(v8Isolate);
            case V8ValueType::Integer: {
                jint integerObject = ToJavaIntegerFromV8ValueInteger(jniEnv, obj);
                return ToV8Integer(v8Isolate, integerObject);
            }
            case V8ValueType::String: {
                jstring stringObject = ToJavaStringFromV8ValueString(jniEnv, obj);
                auto v8String = ToV8String(jniEnv, v8Isolate, stringObject);
                DELETE_LOCAL_REF(jniEnv, stringObject);
                return v8String;
            }
            case V8ValueType::Boolean: {
                jboolean booleanObject = jniEnv->CallBooleanMethod(obj, jmethodIDV8ValueBooleanToPrimitive);
                return ToV8Boolean(v8Isolate, booleanObject);
            }
            case V8ValueType::Double: {
                jdouble doubleObject = jniEnv->CallDoubleMethod(obj, jmethodIDV8ValueDoubleToPrimitive);
                return ToV8Double(v8Isolate, doubleObject);
            }
            case V8ValueType::Long: {
                jlong longObject = jniEnv->CallLongMethod(obj, jmethodIDV8ValueLongToPrimitive);
                return ToV8Long(v8Isolate, longObject);
            }
            case V8ValueType::ZonedDateTime: {
                jlong longObject = (jlong)jniEnv->CallLongMethod(obj, jmethodIDV8ValueZonedDateTimeToPrimitive);
                return ToV8Date(v8Context, longObject);
            }
            case V8ValueType::BigInteger: {
                jint signum = jniEnv->CallIntMethod(obj, jmethodIDV8ValueBigIntegerGetSignum);
                jlongArray longArray = (jlongArray)jniEnv->CallObjectMethod(obj, jmethodIDV8ValueBigIntegerGetLongArray);
                V8LocalBigInt v8BigInt = ToV8BigInt(
                    jniEnv, v8Isolate, v8Context, signum, longArray);
                DELETE_LOCAL_REF(jniEnv, longArray);
                return v8BigInt;
            }
            default:
                break;
            }
            const jlong v8ValueHandle = jniEnv->CallLongMethod(
                obj,
                jmethodIDV8ValueReferenceGetHandle);
            switch (v8ValueType) {
            case V8ValueType::Arguments:
            case V8ValueType::Array:
                return V8LocalArray::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_ARRAY(v8ValueHandle));
            case V8ValueType::Map:
                return V8LocalMap::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_MAP(v8ValueHandle));
            case V8ValueType::Promise:
                return V8LocalPromise::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_PROMISE(v8ValueHandle));
            case V8ValueType::Proxy:
                return V8LocalProxy::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_PROXY(v8ValueHandle));
            case V8ValueType::RegExp:
                return V8LocalRegExp::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_REG_EXP(v8ValueHandle));
            case V8ValueType::Set:
                return V8LocalSet::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_SET(v8ValueHandle));
            case V8ValueType::Symbol:
                return V8LocalSymbol::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_SYMBOL(v8ValueHandle));
            case V8ValueType::SymbolObject:
                return V8LocalSymbolObject::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_SYMBOL_OBJECT(v8ValueHandle));
            case V8ValueType::Object:
            case V8ValueType::Error:
            case V8ValueType::Function:
            case V8ValueType::Iterator:
            case V8ValueType::WeakMap:
            case V8ValueType::WeakSet:
            case V8ValueType::BigIntObject:
            case V8ValueType::BooleanObject:
            case V8ValueType::IntegerObject:
            case V8ValueType::DoubleObject:
            case V8ValueType::StringObject:
            case V8ValueType::DataView:
            case V8ValueType::ArrayBuffer:
            case V8ValueType::SharedArrayBuffer:
            case V8ValueType::Int8Array:
            case V8ValueType::Uint8Array:
            case V8ValueType::Uint8ClampedArray:
            case V8ValueType::Int16Array:
            case V8ValueType::Uint16Array:
            case V8ValueType::Int32Array:
            case V8ValueType::Uint32Array:
            case V8ValueType::Float16Array:
            case V8ValueType::Float32Array:
            case V8ValueType::Float64Array:
            case V8ValueType::BigInt64Array:
            case V8ValueType::BigUint64Array:
                return V8LocalObject::New(
                    v8Isolate,
                    TO_V8_PERSISTENT_OBJECT(v8ValueHandle));
            default:
                return ToV8Undefined(v8Isolate);
            }
        }

        std::unique_ptr<V8LocalObject[]> ToV8Objects(
            JNIEnv* jniEnv,
            V8Isolate* v8Isolate,
            const V8LocalContext& v8Context,
            const jobjectArray mObjects,
            const jintArray mObjectTypes) noexcept {
            std::unique_ptr<V8LocalObject[]> umObjectsPointer;
            uint32_t count = mObjects == nullptr ? 0 : jniEnv->GetArrayLength(mObjects);
            if (count > 0) {
                jint* objectTypes = jniEnv->GetIntArrayElements(mObjectTypes, nullptr);
                if (objectTypes == nullptr) {
                    return umObjectsPointer;
                }
                umObjectsPointer.reset(new V8LocalObject[count]);
                for (uint32_t i = 0; i < count; ++i) {
                    jobject element = jniEnv->GetObjectArrayElement(mObjects, i);
                    umObjectsPointer.get()[i] = ToV8Value(
                        jniEnv,
                        v8Isolate,
                        v8Context,
                        element,
                        objectTypes[i]).As<v8::Object>();
                    DELETE_LOCAL_REF(jniEnv, element);
                }
                jniEnv->ReleaseIntArrayElements(mObjectTypes, objectTypes, JNI_ABORT);
            }
            return umObjectsPointer;
        }

        std::unique_ptr<V8LocalString[]> ToV8Strings(
            JNIEnv* jniEnv,
            V8Isolate* v8Isolate,
            const jobjectArray mStrings) noexcept {
            std::unique_ptr<V8LocalString[]> umStringsPointer;
            uint32_t count = mStrings == nullptr ? 0 : jniEnv->GetArrayLength(mStrings);
            if (count > 0) {
                umStringsPointer.reset(new V8LocalString[count]);
                for (uint32_t i = 0; i < count; ++i) {
                    jstring element = (jstring)jniEnv->GetObjectArrayElement(mStrings, i);
                    umStringsPointer.get()[i] = ToV8String(jniEnv, v8Isolate, element);
                    DELETE_LOCAL_REF(jniEnv, element);
                }
            }
            return umStringsPointer;
        }

        std::unique_ptr<V8LocalValue[]> ToV8Values(
            JNIEnv* jniEnv,
            V8Isolate* v8Isolate,
            const V8LocalContext& v8Context,
            const jobjectArray mValues,
            const jintArray mValueTypes) noexcept {
            std::unique_ptr<V8LocalValue[]> umValuesPointer;
            uint32_t count = mValues == nullptr ? 0 : jniEnv->GetArrayLength(mValues);
            if (count > 0) {
                jint* valueTypes = jniEnv->GetIntArrayElements(mValueTypes, nullptr);
                if (valueTypes == nullptr) {
                    return umValuesPointer;
                }
                umValuesPointer.reset(new V8LocalValue[count]);
                for (uint32_t i = 0; i < count; ++i) {
                    jobject element = jniEnv->GetObjectArrayElement(mValues, i);
                    umValuesPointer.get()[i] = ToV8Value(
                        jniEnv,
                        v8Isolate,
                        v8Context,
                        element,
                        valueTypes[i]);
                    DELETE_LOCAL_REF(jniEnv, element);
                }
                jniEnv->ReleaseIntArrayElements(mValueTypes, valueTypes, JNI_ABORT);
            }
            return umValuesPointer;
        }
    }
}
