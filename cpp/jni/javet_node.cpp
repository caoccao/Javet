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

#include "javet_node.h"

#ifdef ENABLE_NODE

/*
 * This file hosts the Node.js helpers that the public embedder API
 * does not expose.
 *
 * Node.js v24.19.0 split the former libnode target into node_base plus
 * libnode and moved node_snapshot.cc (or src/node_snapshot_stub.cc when
 * node_use_node_snapshot is false) from the node executable target to the
 * libnode target. node::SnapshotBuilder::GetEmbeddedSnapshotData() is
 * therefore defined by libnode on every platform. Do not polyfill it here
 * again or the link fails with a duplicate symbol because libnode is linked
 * with whole archive.
 */

#include "node_snapshot_builder.h"

namespace Javet {

    /*
     * Node.js declares ModuleWrap's index-based resolvers private, and Javet
     * needs their addresses to let v8::Module::InstantiateModule() delegate
     * module resolution to Node.js. The usual access checking rules do not
     * apply to the names in an explicit instantiation, so the thief below takes
     * both addresses without patching Node.js.
     *
     * `#define private public` around <module_wrap.h> compiles just as well,
     * but MSVC encodes the access specifier in the mangled name (`SA` for a
     * public static member, `CA` for a private one), so the references it emits
     * are never defined by Node.js's own objects and lld-link fails with
     * `undefined symbol`. The Itanium ABI leaves access out of the mangling,
     * which is why that hack only ever broke Windows.
     */
    namespace ModuleWrapAccess {
        template <typename Tag, typename Tag::Type kCallback>
        struct Thief {
            friend typename Tag::Type Steal(Tag) { return kCallback; }
        };

        struct ResolveModuleTag {
            using Type = v8::Module::ResolveModuleByIndexCallback;
            friend Type Steal(ResolveModuleTag);
        };

        struct ResolveSourceTag {
            using Type = v8::Module::ResolveSourceByIndexCallback;
            friend Type Steal(ResolveSourceTag);
        };

        template struct Thief<ResolveModuleTag, &node::loader::ModuleWrap::ResolveModuleCallback>;
        template struct Thief<ResolveSourceTag, &node::loader::ModuleWrap::ResolveSourceCallback>;
    }

    v8::Module::ResolveModuleByIndexCallback GetNodeResolveModuleCallback() {
        return Steal(ModuleWrapAccess::ResolveModuleTag{});
    }

    v8::Module::ResolveSourceByIndexCallback GetNodeResolveSourceCallback() {
        return Steal(ModuleWrapAccess::ResolveSourceTag{});
    }

    v8::Isolate* NewIsolateForSnapshotRestore(
        node::MultiIsolatePlatform* platform,
        uv_loop_t* event_loop,
        const node::EmbedderSnapshotData* snapshotData,
        std::shared_ptr<node::ArrayBufferAllocator> allocator) {

        const node::SnapshotData* snapData =
            node::SnapshotData::FromEmbedderWrapper(snapshotData);
        if (snapData == nullptr) {
            return nullptr;
        }

        v8::Isolate::CreateParams params;
        params.array_buffer_allocator_shared = allocator;

        // InitializeIsolateParams sets snapshot_blob and external_references.
        node::SnapshotBuilder::InitializeIsolateParams(snapData, &params);

        // Allocate the isolate in the same group as other Node.js isolates.
        v8::Isolate* isolate = v8::Isolate::Allocate(node::GetOrCreateIsolateGroup());
        if (isolate == nullptr) {
            return nullptr;
        }

        // Register with platform before initialization (for V8 memory reducer).
        platform->RegisterIsolate(isolate, event_loop);

        // CppHeap is required for Node.js environments.
        params.cpp_heap = v8::CppHeap::Create(
            platform, v8::CppHeapCreateParams{{}}).release();

        node::SetIsolateCreateParamsForNode(&params);
        v8::Isolate::Initialize(isolate, params);

        {
            v8::Isolate::Scope isolate_scope(isolate);
            // Use SetIsolateMiscHandlers (not SetIsolateUpForNode) for snapshot
            // restoration: error handlers are set during context deserialization
            // in node::CreateEnvironment via SetIsolateErrorHandlers.
            node::SetIsolateMiscHandlers(isolate, {});
        }

        return isolate;
    }

}
#endif
