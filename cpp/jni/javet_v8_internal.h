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

#pragma once

 /*
  * This header is for developing V8 internal features only.
  * It shall only be included in .cpp files and only be the last header,
  * otherwise compilation errors will take place.
  */

#pragma warning(disable: 4065)
#pragma warning(disable: 4018)
#pragma warning(disable: 4146)
#pragma warning(disable: 4244)
#pragma warning(disable: 4267)
#pragma warning(disable: 4291)
#pragma warning(disable: 4819)
#pragma warning(disable: 4996)

#include <src/objects/objects.h>
#include <src/objects/objects-inl.h>
#include <src/api/api-inl.h>
#include <src/objects/js-function-inl.h>
#include <src/objects/shared-function-info-inl.h>
#include <src/objects/string-set-inl.h>
#include <src/objects/source-text-module-inl.h>
#include <src/objects/synthetic-module-inl.h>
#include <src/strings/string-builder-inl.h>
#include <src/handles/handles-inl.h>
#include <src/flags/flags.h>
#include <src/debug/debug-scopes.h>
#include <src/inspector/v8-debugger.h>
#include <src/inspector/v8-inspector-impl.h>

#pragma warning(default: 4065)
#pragma warning(default: 4018)
#pragma warning(default: 4146)
#pragma warning(default: 4244)
#pragma warning(default: 4267)
#pragma warning(default: 4291)
#pragma warning(default: 4819)
#pragma warning(default: 4996)

using V8internalBlockingBehavior = v8::internal::BlockingBehavior;
using V8InternalBuiltin = v8::internal::Builtin;
using V8InternalDisallowCompilation = v8::internal::DisallowCompilation;
using V8InternalDisallowGarbageCollection = v8::internal::DisallowGarbageCollection;
using V8InternalFlagList = v8::internal::FlagList;
using V8InternalHeapObject = v8::internal::HeapObject;
using V8InternalIncrementalStringBuilder = v8::internal::IncrementalStringBuilder;
using V8InternalIsolate = v8::internal::Isolate;
using V8InternalJSFunction = v8::internal::JSFunction;
using V8InternalJSObject = v8::internal::JSObject;
using V8InternalModule = v8::internal::Module;
using V8InternalNativeContext = v8::internal::NativeContext;
using V8InternalObject = v8::internal::Object;
using V8InternalScopeInfo = v8::internal::ScopeInfo;
using V8InternalScopeIterator = v8::internal::ScopeIterator;
using V8InternalScopeType = v8::internal::ScopeType;
using V8InternalScript = v8::internal::Script;
using V8InternalScriptOrModule = v8::internal::ScriptOrModule;
using V8InternalSharedFunctionInfo = v8::internal::SharedFunctionInfo;
using V8InternalString = v8::internal::String;
using V8InternalStringSet = v8::internal::StringSet;
using V8InternalSourceTextModule = v8::internal::SourceTextModule;
using V8InternalSyntheticModule = v8::internal::SyntheticModule;
using V8InternalWriteBarrierMode = v8::internal::WriteBarrierMode;

template<typename T>
constexpr auto CONVERT_OFFSET_FOR_SCOPE_INFO(T offset) {
    return (offset - V8InternalHeapObject::kHeaderSize) / v8::internal::kTaggedSize;
}

template<typename T>
constexpr auto HAS_EXCEPTION(T v8InternalIsolate) {
    return v8InternalIsolate->has_exception();
}

template<typename T>
constexpr auto IS_USER_DEFINED_FUNCTION(T v8InternalShared) {
    return !v8InternalShared->native() && !v8InternalShared->IsApiFunction() && v8InternalShared->IsUserJavaScript();
}
