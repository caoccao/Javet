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

#include "javet_node.h"

#ifdef ENABLE_NODE

/*
 * This file is a polyfill to Node.js.
 * libnode is built with few symbols not properly linked.
 * Those missing symbols are declared in this file.
 */

#define NODE_WANT_INTERNALS 1

#include "node_main_instance.h"
#include "node_native_module_env.h"

namespace node {

	v8::StartupData* NodeMainInstance::GetEmbeddedSnapshotBlob() {
		return nullptr;
	}

	const std::vector<size_t>* NodeMainInstance::GetIsolateDataIndexes() {
		return nullptr;
	}

	namespace native_module {

		const bool has_code_cache = false;

		void NativeModuleEnv::InitializeCodeCache() {}

	}
}
#endif
