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

	V8Runtime::V8Runtime(node::MultiIsolatePlatform* v8PlatformPointer)
		: nodeEnvironment(nullptr, node::FreeEnvironment) {
		this->v8PlatformPointer = v8PlatformPointer;
	}

	void V8Runtime::CloseV8Context() {
		auto v8Locker = GetSharedV8Locker();
		V8IsolateScope v8IsolateScope(v8Isolate);
		V8HandleScope v8HandleScope(v8Isolate);
		V8LocalContext v8LocalContext = GetV8LocalContext();
		Unregister(v8LocalContext);
#ifdef ENABLE_NODE
		{
			V8ContextScope v8ContextScope(v8LocalContext);
			v8::SealHandleScope v8SealHandleScope(v8Isolate);
			LOG_DEBUG("SealHandleScope");
			bool hasMoreTasks;
			do {
				LOG_DEBUG("uv_run");
				uv_run(&uvLoop, UV_RUN_DEFAULT);
				v8PlatformPointer->DrainTasks(v8Isolate);
				hasMoreTasks = uv_loop_alive(&uvLoop);
				if (hasMoreTasks) {
					continue;
				}
				LOG_DEBUG("node::EmitBeforeExit " << nodeEnvironment.get());
				node::EmitBeforeExit(nodeEnvironment.get());
				LOG_DEBUG("uv_loop_alive");
				hasMoreTasks = uv_loop_alive(&uvLoop);
			} while (hasMoreTasks == true);
		}
		LOG_DEBUG("node::EmitExit");
		int errorCode = node::EmitExit(nodeEnvironment.get());
		if (errorCode != 0) {
			LOG_ERROR("node::EmitExit() returns " << errorCode << ".");
		}
		else {
			LOG_DEBUG("node::Stop");
			errorCode = node::Stop(nodeEnvironment.get());
			if (errorCode != 0) {
				LOG_ERROR("node::Stop() returns " << errorCode << ".");
			}
		}
		nodeEnvironment.reset();
#endif
		v8Context.Reset();
		v8GlobalObject.Reset();
	}

	void V8Runtime::CloseV8Isolate() {
		if (v8Inspector) {
			auto internalV8Locker = GetSharedV8Locker();
			v8Inspector.reset();
		}
		v8Context.Reset();
		v8GlobalObject.Reset();
#ifdef ENABLE_NODE
		arrayBufferAllocator.reset();
#endif
		v8Locker.reset();
		// Isolate must be the last one to be disposed.
		if (v8Isolate != nullptr) {
#ifdef ENABLE_NODE
			bool isIsolateFinished = false;
			v8PlatformPointer->AddIsolateFinishedCallback(v8Isolate, [](void* data) {
				*static_cast<bool*>(data) = true;
				}, &isIsolateFinished);
			v8PlatformPointer->UnregisterIsolate(v8Isolate);
#endif
			v8Isolate->Dispose();
#ifdef ENABLE_NODE
			while (!isIsolateFinished) {
				uv_run(&uvLoop, UV_RUN_ONCE);
			}
			int errorCode = uv_loop_close(&uvLoop);
			if (errorCode != 0) {
				LOG_ERROR("Failed to close uv loop. Reason: " << uv_err_name(errorCode));
			}
#endif
			v8Isolate = nullptr;
		}
	}

	void V8Runtime::CreateV8Context(JNIEnv* jniEnv, jstring mGlobalName) {
		auto v8Locker = GetSharedV8Locker();
		V8IsolateScope v8IsolateScope(v8Isolate);
		V8HandleScope v8HandleScope(v8Isolate);
#ifdef ENABLE_NODE
		std::unique_ptr<node::IsolateData, decltype(&node::FreeIsolateData)> isolateData(
			node::CreateIsolateData(v8Isolate, &uvLoop, v8PlatformPointer, arrayBufferAllocator.get()),
			node::FreeIsolateData);
		auto v8LocalContext = node::NewContext(v8Isolate);
		V8ContextScope v8ContextScope(v8LocalContext);
		std::vector<std::string> args{ "" };
		std::vector<std::string> execArgs{ "" };
		nodeEnvironment.reset(node::CreateEnvironment(isolateData.get(), v8LocalContext, args, execArgs));
		V8MaybeLocalValue v8MaybeLocalValue = node::LoadEnvironment(
			nodeEnvironment.get(),
			"const publicRequire ="
			"  require('module').createRequire(process.cwd() + '/');"
			"globalThis.require = publicRequire;"
			"console.log('Hello from Node!');"
		);
#else
		auto v8ObjectTemplate = v8::ObjectTemplate::New(v8Isolate);
		auto v8LocalContext = v8::Context::New(v8Isolate, nullptr, v8ObjectTemplate);
		if (mGlobalName != nullptr) {
			auto umGlobalName = Javet::Converter::ToV8String(jniEnv, v8LocalContext, mGlobalName);
			v8ObjectTemplate->SetAccessor(umGlobalName, GlobalAccessorGetterCallback);
		}
#endif
		Register(v8LocalContext);
		v8Context.Reset(v8Isolate, v8LocalContext);
		v8GlobalObject.Reset(
			v8Isolate, v8LocalContext->Global()->GetPrototype()->ToObject(v8LocalContext).ToLocalChecked());
	}

	void V8Runtime::CreateV8Isolate() {
#ifdef ENABLE_NODE
		int errorCode = uv_loop_init(&uvLoop);
		if (errorCode != 0) {
			LOG_ERROR("Failed to init uv loop. Reason: " << uv_err_name(errorCode));
		}
		arrayBufferAllocator = node::ArrayBufferAllocator::Create();
		v8Isolate = node::NewIsolate(arrayBufferAllocator, &uvLoop, v8PlatformPointer);
#else
		v8::Isolate::CreateParams createParams;
		createParams.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();
		v8Isolate = v8::Isolate::New(createParams);
#endif
	}

	V8Runtime::~V8Runtime() {
		CloseV8Context();
		CloseV8Isolate();
	}
}

