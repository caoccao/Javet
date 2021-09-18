/*
 *   Copyright (c) 2021 caoccao.com Sam Cao
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License"),
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

#include "javet_enums.h"

namespace Javet {
    namespace Enums {
        namespace V8ValueInternalType {
            void Initialize() {
                InternalTypeChecks[0] = &(v8::Value::IsUndefined);
                InternalTypeChecks[1] = &(v8::Value::IsNull);
                InternalTypeChecks[2] = &(v8::Value::IsNullOrUndefined);
                InternalTypeChecks[3] = &(v8::Value::IsTrue);
                InternalTypeChecks[4] = &(v8::Value::IsFalse);
                InternalTypeChecks[5] = &(v8::Value::IsName);
                InternalTypeChecks[6] = &(v8::Value::IsString);
                InternalTypeChecks[7] = &(v8::Value::IsSymbol);
                InternalTypeChecks[8] = &(v8::Value::IsFunction);
                InternalTypeChecks[9] = &(v8::Value::IsArray);
                InternalTypeChecks[10] = &(v8::Value::IsObject);
                InternalTypeChecks[11] = &(v8::Value::IsBigInt);
                InternalTypeChecks[12] = &(v8::Value::IsBoolean);
                InternalTypeChecks[13] = &(v8::Value::IsNumber);
                InternalTypeChecks[14] = &(v8::Value::IsExternal);
                InternalTypeChecks[15] = &(v8::Value::IsInt32);
                InternalTypeChecks[16] = &(v8::Value::IsDate);
                InternalTypeChecks[17] = &(v8::Value::IsArgumentsObject);
                InternalTypeChecks[18] = &(v8::Value::IsBigIntObject);
                InternalTypeChecks[19] = &(v8::Value::IsBooleanObject);
                InternalTypeChecks[20] = &(v8::Value::IsNumberObject);
                InternalTypeChecks[21] = &(v8::Value::IsStringObject);
                InternalTypeChecks[22] = &(v8::Value::IsSymbolObject);
                InternalTypeChecks[23] = &(v8::Value::IsNativeError);
                InternalTypeChecks[24] = &(v8::Value::IsRegExp);
                InternalTypeChecks[25] = &(v8::Value::IsAsyncFunction);
                InternalTypeChecks[26] = &(v8::Value::IsGeneratorFunction);
                InternalTypeChecks[27] = &(v8::Value::IsGeneratorObject);
                InternalTypeChecks[28] = &(v8::Value::IsPromise);
                InternalTypeChecks[29] = &(v8::Value::IsMap);
                InternalTypeChecks[30] = &(v8::Value::IsSet);
                InternalTypeChecks[31] = &(v8::Value::IsMapIterator);
                InternalTypeChecks[32] = &(v8::Value::IsSetIterator);
                InternalTypeChecks[33] = &(v8::Value::IsWeakMap);
                InternalTypeChecks[34] = &(v8::Value::IsWeakSet);
                InternalTypeChecks[35] = &(v8::Value::IsArrayBuffer);
                InternalTypeChecks[36] = &(v8::Value::IsArrayBufferView);
                InternalTypeChecks[37] = &(v8::Value::IsTypedArray);
                InternalTypeChecks[38] = &(v8::Value::IsUint8Array);
                InternalTypeChecks[39] = &(v8::Value::IsUint8ClampedArray);
                InternalTypeChecks[40] = &(v8::Value::IsInt8Array);
                InternalTypeChecks[41] = &(v8::Value::IsUint16Array);
                InternalTypeChecks[42] = &(v8::Value::IsInt16Array);
                InternalTypeChecks[43] = &(v8::Value::IsUint32Array);
                InternalTypeChecks[44] = &(v8::Value::IsInt32Array);
                InternalTypeChecks[45] = &(v8::Value::IsFloat32Array);
                InternalTypeChecks[46] = &(v8::Value::IsFloat64Array);
                InternalTypeChecks[47] = &(v8::Value::IsBigInt64Array);
                InternalTypeChecks[48] = &(v8::Value::IsBigUint64Array);
                InternalTypeChecks[49] = &(v8::Value::IsDataView);
                InternalTypeChecks[50] = &(v8::Value::IsSharedArrayBuffer);
                InternalTypeChecks[51] = &(v8::Value::IsProxy);
                InternalTypeChecks[52] = &(v8::Value::IsWasmModuleObject);
                InternalTypeChecks[53] = &(v8::Value::IsModuleNamespaceObject);
            }
        }
    }
}
