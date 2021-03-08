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

#include "javet_exceptions.h"
#include "javet_inspector.h"
#include "javet_logging.h"
#include "javet_v8_runtime.h"

namespace Javet {
	void V8Runtime::reset() {
		if (v8Inspector) {
			std::shared_ptr<v8::Locker> internalV8Locker = v8Locker ? v8Locker : std::make_shared<v8::Locker>(v8Isolate);
			v8Inspector.reset();
		}
		v8Context.Reset();
		v8GlobalObject.Reset();
		v8Locker.reset();
		// Isolate must be the last one to be disposed.
		if (v8Isolate != nullptr) {
			v8Isolate->Dispose();
			v8Isolate = nullptr;
		}
	}

	V8Runtime::~V8Runtime() {
		reset();
	}
}
