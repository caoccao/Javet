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

#include "javet_inspector.h"
#include "javet_types.h"
#include "javet_v8_runtime.h"

namespace Javet {
	void GlobalAccessorGetterCallback(
		V8LocalString propertyName,
		const v8::PropertyCallbackInfo<v8::Value>& args) {
		args.GetReturnValue().Set(args.GetIsolate()->GetCurrentContext()->Global());
	}

	void V8Runtime::CloseV8Isolate() {
		if (v8Inspector) {
			auto internalV8Locker = GetSharedV8Locker();
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

	void V8Runtime::ResetV8Context(JNIEnv* jniEnv, jstring mGlobalName) {
		V8IsolateScope v8IsolateScope(v8Isolate);
		V8HandleScope v8HandleScope(v8Isolate);
		auto v8IsolateHandle = v8::ObjectTemplate::New(v8Isolate);
		auto v8LocalContext = v8::Context::New(v8Isolate, nullptr, v8IsolateHandle);
		Register(v8LocalContext);
		if (mGlobalName != nullptr) {
			auto umGlobalName = Javet::Converter::ToV8String(jniEnv, v8LocalContext, mGlobalName);
			v8IsolateHandle->SetAccessor(umGlobalName, GlobalAccessorGetterCallback);
		}
		v8Context.Reset(v8Isolate, v8LocalContext);
		v8GlobalObject.Reset(
			v8Isolate, v8LocalContext->Global()->GetPrototype()->ToObject(v8LocalContext).ToLocalChecked());
	}

	V8Runtime::~V8Runtime() {
		CloseV8Isolate();
	}
}

