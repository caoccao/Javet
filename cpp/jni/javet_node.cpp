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
#endif
