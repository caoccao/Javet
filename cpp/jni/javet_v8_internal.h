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

#pragma once

/*
 * This header is for developing V8 internal features only.
 * It shall only be included in .cpp files and only be the last header,
 * otherwise compilation errors will take place.
 */

#pragma warning(disable: 4244)
#pragma warning(disable: 4267)
#pragma warning(disable: 4291)
#pragma warning(disable: 4819)
#pragma warning(disable: 4996)

#define V8_ENABLE_WEBASSEMBLY 1
#include <src/objects/objects-inl.h>
#include <src/api/api-inl.h>
#include <src/objects/js-function-inl.h>
#include <src/objects/shared-function-info-inl.h>
#include <src/handles/handles-inl.h>
#include <src/inspector/v8-debugger.h>
#include <src/inspector/v8-inspector-impl.h>
#pragma warning(default: 4244)
#pragma warning(default: 4267)
#pragma warning(default: 4291)
#pragma warning(default: 4819)
#pragma warning(default: 4996)

#define IS_API_FUNCTION(v8InternalShared) (!v8InternalShared.native() && v8InternalShared.IsApiFunction())
#define IS_USER_DEFINED_FUNCTION(v8InternalShared) (!v8InternalShared.native() && !v8InternalShared.IsApiFunction() && v8InternalShared.IsUserJavaScript())

using V8InternalAllowNullsFlag = v8::internal::AllowNullsFlag;
using V8InternalBuiltin = v8::internal::Builtin;
using V8InternalIncrementalStringBuilder = v8::internal::IncrementalStringBuilder;
using V8InternalIsolate = v8::internal::Isolate;
using V8InternalJSFunction = v8::internal::JSFunction;
using V8InternalRobustnessFlag = v8::internal::RobustnessFlag;
using V8InternalScopeType = v8::internal::ScopeType;
using V8InternalScript = v8::internal::Script;
using V8InternalSharedFunctionInfo = v8::internal::SharedFunctionInfo;
using V8InternalString = v8::internal::String;
using V8InternalWriteBarrierMode = v8::internal::WriteBarrierMode;

