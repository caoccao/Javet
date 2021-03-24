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
#ifdef ENABLE_NODE
	static std::mutex mutexForNodeResetEnvrironment;
#endif

	void GlobalAccessorGetterCallback(
		V8LocalString propertyName,
		const v8::PropertyCallbackInfo<v8::Value>& args) {
		args.GetReturnValue().Set(args.GetIsolate()->GetCurrentContext()->Global());
	}

#ifdef ENABLE_NODE
	V8Runtime::V8Runtime(node::MultiIsolatePlatform* v8PlatformPointer, std::shared_ptr<node::ArrayBufferAllocator> nodeArrayBufferAllocator)
		: nodeEnvironment(nullptr, node::FreeEnvironment), nodeIsolateData(nullptr, node::FreeIsolateData), v8Locker(nullptr), uvLoop() {
		this->nodeArrayBufferAllocator = nodeArrayBufferAllocator;
#else
	V8Runtime::V8Runtime(V8Platform * v8PlatformPointer)
		: v8Locker(nullptr) {
#endif
		externalV8Runtime = nullptr;
		v8Isolate = nullptr;
		this->v8PlatformPointer = v8PlatformPointer;
	}

	void V8Runtime::Await() {
#ifdef ENABLE_NODE
		bool hasMoreTasks;
		do {
			uv_run(&uvLoop, UV_RUN_DEFAULT);
			// DrainTasks is thread-safe.
			v8PlatformPointer->DrainTasks(v8Isolate);
			hasMoreTasks = uv_loop_alive(&uvLoop);
		} while (hasMoreTasks == true);
#endif
	}

	void V8Runtime::CloseV8Context() {
		auto internalV8Locker = GetSharedV8Locker();
		auto v8IsolateScope = GetV8IsolateScope();
		V8HandleScope v8HandleScope(v8Isolate);
		auto v8LocalContext = GetV8LocalContext();
		Unregister(v8LocalContext);
		v8GlobalObject.Reset();
#ifdef ENABLE_NODE
		{
			auto v8ContextScope = GetV8ContextScope(v8LocalContext);
			v8::SealHandleScope v8SealHandleScope(v8Isolate);
			bool hasMoreTasks;
			do {
				uv_run(&uvLoop, UV_RUN_DEFAULT);
				// DrainTasks is thread-safe.
				v8PlatformPointer->DrainTasks(v8Isolate);
				hasMoreTasks = uv_loop_alive(&uvLoop);
				if (hasMoreTasks) {
					continue;
				}
				// node::EmitBeforeExit is thread-safe.
				node::EmitBeforeExit(nodeEnvironment.get());
				hasMoreTasks = uv_loop_alive(&uvLoop);
			} while (hasMoreTasks == true);
		}
		int errorCode = 0;
		// node::EmitExit is thread-safe.
		errorCode = node::EmitExit(nodeEnvironment.get());
		if (errorCode != 0) {
			LOG_ERROR("node::EmitExit() returns " << errorCode << ".");
		}
		else {
			// node::Stop is thread-safe.
			errorCode = node::Stop(nodeEnvironment.get());
			if (errorCode != 0) {
				LOG_ERROR("node::Stop() returns " << errorCode << ".");
			}
		}
		{
			// node::FreeEnvironment is not thread-safe.
			std::lock_guard<std::mutex> lock(mutexForNodeResetEnvrironment);
			nodeEnvironment.reset();
		}
#endif
		v8PersistentContext.Reset();
	}

	void V8Runtime::CloseV8Isolate() {
		if (v8Inspector) {
			auto internalV8Locker = GetSharedV8Locker();
			v8Inspector.reset();
		}
		v8GlobalObject.Reset();
		v8PersistentContext.Reset();
#ifdef ENABLE_NODE
		// node::FreeIsolateData is thread-safe.
		nodeIsolateData.reset();
#endif
		v8Locker.reset();
		// Isolate must be the last one to be disposed.
		if (v8Isolate != nullptr) {
#ifdef ENABLE_NODE
			bool isIsolateFinished = false;
			// AddIsolateFinishedCallback is thread-safe.
			v8PlatformPointer->AddIsolateFinishedCallback(v8Isolate, [](void* data) {
				*static_cast<bool*>(data) = true;
				}, &isIsolateFinished);
			// UnregisterIsolate is thread-safe.
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

	void V8Runtime::CreateV8Context(JNIEnv * jniEnv, jstring mGlobalName) {
		auto internalV8Locker = GetSharedV8Locker();
		auto v8IsolateScope = GetV8IsolateScope();
		V8HandleScope v8HandleScope(v8Isolate);
#ifdef ENABLE_NODE
		// node::NewContext is thread-safe.
		V8LocalContext v8LocalContext = node::NewContext(v8Isolate);
		auto v8ContextScope = GetV8ContextScope(v8LocalContext);
		std::vector<std::string> args{ "" };
		std::vector<std::string> execArgs{ "" };
		// node::CreateEnvironment is thread-safe.
		nodeEnvironment.reset(node::CreateEnvironment(nodeIsolateData.get(), v8LocalContext, args, execArgs));
		// node::LoadEnvironment is thread-safe.
		auto v8MaybeLocalValue = node::LoadEnvironment(
			nodeEnvironment.get(),
			"const publicRequire = require('module').createRequire(process.cwd() + '/');"
			"globalThis.require = publicRequire;"
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
		v8PersistentContext.Reset(v8Isolate, v8LocalContext);
		v8GlobalObject.Reset(
			v8Isolate, v8LocalContext->Global()->GetPrototype()->ToObject(v8LocalContext).ToLocalChecked());
}

	void V8Runtime::CreateV8Isolate() {
#ifdef ENABLE_NODE
		int errorCode = uv_loop_init(&uvLoop);
		if (errorCode != 0) {
			LOG_ERROR("Failed to init uv loop. Reason: " << uv_err_name(errorCode));
		}
		// node::NewIsolate is thread-safe.
		v8Isolate = node::NewIsolate(nodeArrayBufferAllocator, &uvLoop, v8PlatformPointer);
		{
			auto internalV8Locker = GetUniqueV8Locker();
			auto v8IsolateScope = GetV8IsolateScope();
			V8HandleScope v8HandleScope(v8Isolate);
			// node::CreateIsolateData is thread-safe.
			nodeIsolateData.reset(node::CreateIsolateData(v8Isolate, &uvLoop, v8PlatformPointer, nodeArrayBufferAllocator.get()));
		}
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

