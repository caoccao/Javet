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
 * This file is a polyfill to Node.js.
 * libnode is built with few symbols not properly linked.
 * Those missing symbols are declared in this file.
 */

#include "node_snapshot_builder.h"

namespace node {

    const SnapshotData* SnapshotBuilder::GetEmbeddedSnapshotData() {
        return nullptr;
    }

}

namespace Javet {

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
