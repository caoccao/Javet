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

#ifdef ENABLE_NODE

#pragma warning(disable: 4244)
#pragma warning(disable: 4251)
#pragma warning(disable: 4267)
#pragma warning(disable: 4275)
#pragma warning(disable: 4819)
#define NODE_WANT_INTERNALS 1
#include <node.h>
#include <uv.h>
#include <env-inl.h>
#include <node_snapshot_builder.h>
// Hack Begins (The hack is for resolving the conflicts between Node.js and V8)
#define BASE_TRACE_EVENT_COMMON_TRACE_EVENT_COMMON_H_
#define V8_TRACING_TRACE_EVENT_H_
#undef CHECK
#undef CHECK_EQ
#undef CHECK_GE
#undef CHECK_GT
#undef CHECK_IMPLIES
#undef CHECK_LE
#undef CHECK_LT
#undef CHECK_NE
#undef DCHECK
#undef DCHECK_EQ
#undef DCHECK_GE
#undef DCHECK_GT
#undef DCHECK_IMPLIES
#undef DCHECK_LE
#undef DCHECK_LT
#undef DCHECK_NE
#undef DCHECK_NOT_NULL
#undef DCHECK_NULL
#undef UNREACHABLE
// Hack Ends
#pragma warning(default: 4244)
#pragma warning(default: 4251)
#pragma warning(default: 4267)
#pragma warning(default: 4275)
#pragma warning(default: 4819)

#ifdef _WIN32
#pragma comment(lib, "Crypt32.lib")
#pragma comment(lib, "Dbghelp.lib")
#pragma comment(lib, "Iphlpapi.lib")
#pragma comment(lib, "Psapi.lib")
#pragma comment(lib, "Userenv.lib")
#pragma comment(lib, "Winmm.lib")
#pragma comment(lib, "Ws2_32.lib")
#endif

constexpr auto DEFAULT_SCRIPT_NAME = "javet.js";

#endif
