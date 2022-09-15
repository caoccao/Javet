/*
 *   Copyright (c) 2021-2022 caoccao.com Sam Cao
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

#pragma warning(disable: 4005)
#pragma warning(disable: 4251)
#pragma warning(disable: 4267)
#pragma warning(disable: 4275)
#define NODE_WANT_INTERNALS 1
#include <node.h>
#include <uv.h>
#include <env-inl.h>
#define BASE_TRACE_EVENT_COMMON_TRACE_EVENT_COMMON_H_
#define V8_TRACING_TRACE_EVENT_H_
#pragma warning(default: 4251)
#pragma warning(default: 4267)
#pragma warning(default: 4275)

#ifdef _WIN32
#pragma comment(lib, "Crypt32.lib")
#pragma comment(lib, "Dbghelp.lib")
#pragma comment(lib, "Iphlpapi.lib")
#pragma comment(lib, "Psapi.lib")
#pragma comment(lib, "Userenv.lib")
#pragma comment(lib, "Winmm.lib")
#pragma comment(lib, "Ws2_32.lib")
#endif

#define DEFAULT_SCRIPT_NAME "javet.js"

#endif
