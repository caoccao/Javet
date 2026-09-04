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

#ifdef ENABLE_NODE

#pragma warning(disable: 4244)
#pragma warning(disable: 4251)
#pragma warning(disable: 4267)
#pragma warning(disable: 4275)
#pragma warning(disable: 4819)
#pragma warning(disable: 4996)
#define NODE_WANT_INTERNALS 1
#define HAVE_AMARO 1
#define HAVE_INSPECTOR 1
#define HAVE_OPENSSL 1
#define HAVE_SQLITE 1
#include <node.h>
#include <uv.h>
#include <env-inl.h>
#include <crypto/crypto_util.h>
#include <node_snapshot_builder.h>
// ModuleWrap::ResolveModuleCallback and ModuleWrap::ResolveSourceCallback are
// private. javet_node.cpp takes their addresses through an explicit template
// instantiation, which the standard exempts from access checking, so this
// include stays untouched. Do not wrap it in `#define private public`: MSVC
// encodes the access specifier in the mangled name (`SA` for a public static
// member, `CA` for a private one), so that hack emits references that Node.js's
// own objects never define and lld-link fails with `undefined symbol`.
#include <module_wrap.h>
 // Hack Begins (The hack is for resolving the conflicts between Node.js and V8)
#define BASE_TRACE_EVENT_COMMON_TRACE_EVENT_COMMON_H_
#define V8_TRACING_TRACE_EVENT_H_
// Node.js and V8 each ship their own copy of Chromium's trace_event_common.h
// and both refuse to be included twice (`#error "Another copy of this file has
// already been included."`). The defines above keep V8's copy out because
// Node.js's copy is already in. V8 is built with v8_use_perfetto=0, so its
// headers (src/heap/heap.h, src/heap/gc-tracer.h) still refer to the
// perfetto stubs that V8's src/tracing/trace-event-no-perfetto.h declares.
// Node.js has no equivalent, so mirror that stub here - the types are empty
// placeholders, which keeps the layout identical to the compiled V8.
#ifndef V8_TRACING_TRACE_EVENT_NO_PERFETTO_H_
#define V8_TRACING_TRACE_EVENT_NO_PERFETTO_H_
namespace perfetto {
    class EventContext;

    class StaticString {
    public:
        template <typename T>
        StaticString(T) {}
    };

    class DynamicString {
    public:
        template <typename T>
        explicit DynamicString(T) {}
    };

    struct Track {
        Track() = default;
        explicit Track(uint64_t id) {}
    };

    struct ThreadTrack : public Track {
        static ThreadTrack Current() { return ThreadTrack(); }
    };

    struct NamedTrack : public Track {
        NamedTrack() = default;

        template <class T>
        explicit NamedTrack(T name, uint64_t id = 0, Track parent = Track{ 0 }) {}

        template <class T>
        static NamedTrack FromPointer(T name, const void* ptr, Track parent = Track{ 0 }) {
            return NamedTrack();
        }

        template <class T>
        static NamedTrack ThreadScoped(T name, uint64_t id = 0, Track parent = Track{ 0 }) {
            return NamedTrack();
        }

        template <class T>
        static NamedTrack Global(T name, uint64_t id = 0) {
            return NamedTrack();
        }

        NamedTrack disable_sibling_merge() { return *this; }
    };

    struct CounterTrack : public Track {
        template <class T>
        explicit CounterTrack(T name, Track parent = Track{ 0 }) {}
        template <class T>
        explicit CounterTrack(T name, uint64_t id = 0, Track parent = Track{ 0 }) {}
    };

    struct Flow {
        static inline Flow ProcessScoped(uint64_t flow_id) { return Flow(); }
        static inline Flow FromPointer(void* ptr) { return Flow(); }
        static inline Flow Global(uint64_t flow_id) { return Flow(); }
    };
}
#endif
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
#pragma warning(default: 4996)

#ifdef _WIN32
#pragma comment(lib, "Crypt32.lib")
#pragma comment(lib, "Dbghelp.lib")
#pragma comment(lib, "Iphlpapi.lib")
#pragma comment(lib, "ole32.lib")
#pragma comment(lib, "shell32.lib")
#pragma comment(lib, "Psapi.lib")
#pragma comment(lib, "User32.lib")
#pragma comment(lib, "Userenv.lib")
#pragma comment(lib, "Winmm.lib")
#pragma comment(lib, "Ws2_32.lib")
#endif

constexpr auto DEFAULT_SCRIPT_NAME = "javet.js";
constexpr auto INIT_SCRIPT_WITH_SNAPSHOT = "globalThis.require = require;";
constexpr auto INIT_SCRIPT_WITHOUT_SNAPSHOT = "globalThis.require = require('module').createRequire(process.cwd() + '/');";

namespace Javet {
    // Hand out Node.js's own index-based module resolvers so that
    // v8::Module::InstantiateModule() can delegate module resolution to
    // Node.js. @see javet_node.cpp
    v8::Module::ResolveModuleByIndexCallback GetNodeResolveModuleCallback();
    v8::Module::ResolveSourceByIndexCallback GetNodeResolveSourceCallback();

    // Creates a V8 isolate initialized from snapshot data, bypassing
    // node::NewIsolate's static variable that forces all isolates
    // to use the first-ever snapshot blob (shared-readonly-heap).
    v8::Isolate* NewIsolateForSnapshotRestore(
        node::MultiIsolatePlatform* platform,
        uv_loop_t* event_loop,
        const node::EmbedderSnapshotData* snapshotData,
        std::shared_ptr<node::ArrayBufferAllocator> allocator);
}

#endif
