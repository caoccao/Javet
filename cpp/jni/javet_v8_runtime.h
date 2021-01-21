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

#include <jni.h>
#include <v8.h>
#include <v8-inspector.h>

namespace Javet {

	class V8Runtime {
	public:
		v8::Isolate* v8Isolate;
		v8::Persistent<v8::Context> v8Context;
		v8::Persistent<v8::Object>* v8GlobalObject;
		v8::Locker* v8Locker;
		jobject managedObject;
		jthrowable managedException;
		v8_inspector::V8Inspector* v8Inspector;
	};

}

